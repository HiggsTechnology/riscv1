package Core.Cache

import Bus.{SimpleBus, SimpleBusParameter, SimpleReqBundle, SimpleRespBundle}
import Core.AXI4.AXI4Parameters.{AXI_PROT, AXI_SIZE}
import Core.AXI4.{AXI4IO, AXI4Parameters, AXIParameter}
import Core.{Config, cohResp}
import chisel3._
import chisel3.util._
import utils.ParallelOperation



sealed trait CacheConfig extends AXIParameter{
  def TotalSize = 4 //Kb
  def Ways = 1
  def LineSize = 64 // byte
  def Sets = TotalSize * 1024 / LineSize / Ways
  def OffsetBits = log2Up(LineSize) //对应的是字节标号
  def IndexBits = log2Up(Sets)
  def TagBits = 64 - OffsetBits - IndexBits
  def CacheDataBits = LineSize*8
  def AxiDataBits = 64
  def CacheCatNum  = 4
  def CacheCatWidth = log2Up(CacheCatNum)
  def RetTimes = CacheDataBits/AxiDataBits
  def addrBundle = new Bundle {
    val tag        = UInt(TagBits.W)
    val index      = UInt(IndexBits.W)
    val Offset = UInt(OffsetBits.W)
  }
}

class CacheReq extends Bundle with Config with CacheConfig {
  val addr = UInt(XLEN.W)
  val isWrite = Bool() //op
  val data = UInt(XLEN.W)
  val wmask = UInt((XLEN/8).W)
  def toSimpleReqBundle : SimpleReqBundle = {
    val res = new SimpleReqBundle
    res.isWrite := isWrite
    res.addr := addr
    res.data := data
    res.wmask := wmask
    res.size := SimpleBusParameter.SIZE.bytes8
    res
  }
}

class CacheResp extends Bundle with Config with CacheConfig {
  val data = Output(UInt(XLEN.W))
  def toSimpleRespBundle : SimpleRespBundle = {
    val res = new SimpleRespBundle
    res.data := data
    res
  }
}

class CacheIO extends Bundle with Config {
  val bus = Flipped(new SimpleBus)
  val to_rw   = new AXI4IO   //
  val cohreq = Flipped(ValidIO(UInt(XLEN.W)))
  val cohresp = ValidIO(new cohResp)
}

class DCache(cacheNum: Int = 0) extends Module with Config with CacheConfig with AXIParameter {
  val io = IO(new CacheIO)

  val s_idle :: s_lookUp :: s_miss :: s_replace :: s_refill :: s_refill_done :: Nil = Enum(6)
  val state: UInt = RegInit(s_idle)

//  val valid       = Seq.fill(Ways)(RegInit(VecInit(Seq.fill(Sets)(false.B))))
//  val dirty       = Seq.fill(Ways)(RegInit(VecInit(Seq.fill(Sets)(false.B))))
//  val tagArray    = Seq.fill(Ways)(Mem(Sets, UInt(TagBits.W)))
//  val dataArray   = Seq.fill(Ways)(Mem(Sets, Vec(LineSize, UInt(8.W))))
//  val readReg     = Reg(Vec(LineSize, UInt(8.W)))
  val valid         = RegInit(VecInit(Seq.fill(Sets)(false.B)))
  val dirty         = RegInit(VecInit(Seq.fill(Sets)(false.B)))
  val tagArray      = Mem(Sets, UInt(TagBits.W))
  //val dataArray     = Seq.fill(CacheCatNum)(Mem(Sets, Vec(LineSize/CacheCatNum, UInt(8.W))))//(new 100)


  val SRam_read  = Seq.fill(CacheCatNum)(WireInit(VecInit(Seq.fill(LineSize/CacheCatNum)(0.U(8.W)))))
  val SRam_write = Seq.fill(CacheCatNum)(WireInit(VecInit(Seq.fill(LineSize/CacheCatNum)(0.U(8.W)))))

  val readReg       = Wire(Vec(CacheCatNum,Vec(LineSize/CacheCatNum,UInt(8.W))))// 128bit * 4
  val writeMem      = Reg(UInt((LineSize * 8).W))

  val addr = io.bus.req.bits.addr.asTypeOf(addrBundle)
  val storeEn = io.bus.req.valid && (state === s_idle || state === s_lookUp)
  val reqValid = Reg(Bool())
  when(storeEn) {
    reqValid := io.bus.req.valid
  }
  val addrReg  = Reg(addrBundle)
  val writeReg = Reg(new CacheReq)

  when(storeEn) {
    addrReg   := addr
    writeReg  := io.bus.req.bits
  }
  val hit = Wire(Bool())
  hit := io.bus.req.valid && valid(addr.index) && tagArray.read(addr.index) === addr.tag && ((state === s_idle) || (state === s_lookUp))

  val needRefill = Reg(Bool())
  when(storeEn) {
    needRefill := io.bus.req.valid && !hit
  }


  //for refill done state hit match
  val tagHitVec_done = Wire(Bool())

  when(state === s_refill_done){
    tagHitVec_done := tagArray.read(addrReg.index) === addrReg.tag
  }.otherwise{
    tagHitVec_done := false.B
  }
  //hitwrite  is dirty
  val readIdx = Mux(state===s_refill_done,addrReg.index,addr.index)
  //for (i <- 0 until CacheCatNum) {
  for (i <- 0 until CacheCatNum) {
    readReg(i) := SRam_read(i) //dataArray(i).read(RegNext(readIdx))
  }

  for (i <- 0 until CacheCatNum) {
    when(reqValid && writeReg.isWrite && (state === s_lookUp) && addrReg.Offset(OffsetBits-1,OffsetBits-2) === i.U) {
      for (k <- 0 until XLEN / 8) {
        when(writeReg.wmask(k)) {
          readReg(i)(addrReg.Offset(3, 0) + k.U) := writeReg.data(k * 8 + 7, k * 8)
        }
      }
      SRam_write(i) := readReg(i)
      //dataArray(i)(addrReg.index) := readReg(i)
      dirty(addrReg.index) := true.B
    }
  }



  io.to_rw := DontCare
  //s_miss

  val needWriteBack = dirty(addrReg.index) && valid(addrReg.index)
  val wb_tag        = tagArray.read(addrReg.index)


  val writeDataReg = RegInit(VecInit(Seq.fill(RetTimes)(0.U(AxiDataBits.W))))
  val writeMemCnt  = Reg(UInt(log2Up(RetTimes + 1).W))
  for (i <- 0 until CacheCatNum) {
    when(RegNext(state === s_miss)) {
      writeDataReg(2*i)   := SRam_read(i).asUInt()//dataArray(i)(addrReg.index).asUInt
      writeDataReg(2*i+1) := SRam_read(i).asUInt() >> 64.U//dataArray(i)(addrReg.index).asUInt >> 64.U
    }
  }
  when(state === s_miss) {
    writeMemCnt := 0.U
  }
  when(needRefill) {
    io.to_rw.aw.valid := (state === s_miss) && needWriteBack
    io.to_rw.aw.bits.addr := Cat(wb_tag, addrReg.index, 0.U(OffsetBits.W))
  }

  //write to mem
  io.to_rw.w.valid  := RegNext((state === s_replace || state === s_refill )) && needRefill
  when(writeMemCnt < RetTimes.U) {
    io.to_rw.w.bits.data := writeDataReg(writeMemCnt)
    io.to_rw.w.bits.strb := 0xffffffffL.U
    when(io.to_rw.w.fire){
      writeMemCnt := writeMemCnt + 1.U
    }
    when(writeMemCnt === (RetTimes-1).U){
      io.to_rw.w.bits.last := true.B
    }.otherwise{
      io.to_rw.w.bits.last := false.B
    }
  }

  when( writeMemCnt === (RetTimes-1).U && io.to_rw.w.fire){
    dirty(addrReg.index) := false.B
  }

  io.to_rw.b.ready := (state === s_replace || state === s_refill) && (writeMemCnt === RetTimes.U)

  //replace
  val readMemCnt = Reg(UInt(log2Up(RetTimes+1).W))
  when(state === s_replace){
    readMemCnt := 0.U
  }

  io.to_rw.ar.valid := (state === s_replace)
  when(needRefill) {
    io.to_rw.ar.bits.addr := Cat(addrReg.tag, addrReg.index, 0.U(OffsetBits.W))
  }

  //refill
  io.to_rw.r.ready := (state === s_refill) && (readMemCnt < RetTimes.U)
  val readDataReg = RegInit(VecInit(Seq.fill(RetTimes)(0.U(AxiDataBits.W))))
  when(readMemCnt < RetTimes.U && state === s_refill){
    when(needRefill) {
      readDataReg(readMemCnt) := io.to_rw.r.bits.data
    }
    when(io.to_rw.r.valid){
      readMemCnt := readMemCnt + 1.U
    }
  }
  val mem_wb = Seq.fill(CacheCatNum)(Wire(Vec(LineSize/CacheCatNum, UInt(8.W))))
  for(i <- 0 until  CacheCatNum){
    mem_wb(i) := readDataReg.asUInt()(128*i+127,128*i).asTypeOf(mem_wb(i))
  }
  for( i <- 0 until CacheCatNum){
    when(state === s_refill && readMemCnt === RetTimes.U){
      when(reqValid && writeReg.isWrite && addrReg.Offset(5,4) === i.U ) {
        for (k <- 0 until XLEN / 8) {
          when(writeReg.wmask(k)) {
            mem_wb(i)(addrReg.Offset(3, 0) + k.U) := writeReg.data(k * 8 + 7, k * 8)
          }
        }
        dirty(addrReg.index) := true.B
      }
      tagArray.write(addrReg.index,addrReg.tag)
      valid(addrReg.index)     := true.B
      SRam_write(i) := mem_wb(i)
      //dataArray(i)(addrReg.index) := mem_wb(i)
    }
  }

  //s_refill_done


  io.bus.req.ready  := (state ===s_idle) || (state ===s_lookUp)
  io.bus.resp.bits.data := readReg.asUInt >> addrReg.Offset * 8.U
  io.bus.resp.valid := ((state ===s_lookUp) || RegNext(state === s_refill_done)) && reqValid

  val cohaddr = io.cohreq.bits.asTypeOf(addrBundle)
  io.cohresp.bits.needforward := io.cohreq.valid && cohaddr.tag === tagArray(cohaddr.index) && valid(cohaddr.index) && dirty(cohaddr.index)
  when(io.cohreq.valid && (!io.cohresp.bits.needforward)){
    io.cohresp.valid := true.B
    io.cohresp.bits.forward := DontCare
  }.elsewhen(io.cohreq.valid && state===s_idle && !io.bus.req.valid){
    io.cohresp.valid := RegNext(io.cohreq.valid && state===s_idle && !io.bus.req.valid)
    for(i <- 0 until 4) {
      io.cohresp.bits.forward(i) := SRam_read(i).asUInt()//RegNext(dataArray(i)(cohaddr.index).asUInt())
    }
  }.otherwise{
    io.cohresp.valid := false.B
    io.cohresp.bits.forward := DontCare
  }

  val hit_read = state===s_refill_done || io.bus.req.valid
  val refill_read = state === s_miss
  val fw_read = io.cohreq.valid && state===s_idle && !io.bus.req.valid
  val hit_write = reqValid && writeReg.isWrite && (state === s_lookUp)
  val refill_write = state === s_refill && readMemCnt === RetTimes.U
  val SRamArray     = Seq.fill(CacheCatNum)(Module(new SRam))
  // linesize = 512 bit
  // sram width = 128 bit
  // idx = addr(11,6)
  // en  = addr(5,4) === i.U
  for(i<- 0 until CacheCatNum){
    SRam_read(i) := SRamArray(i).io.rData.asTypeOf(SRam_read(i))
    SRamArray(i).io.idx := Mux(fw_read, cohaddr.index, Mux(refill_read || refill_write || hit_write, addrReg.index, readIdx))
    SRamArray(i).io.wMask := VecInit(Seq.fill(128)(true.B)).asUInt() //wmask had been done
    SRamArray(i).io.wData := SRam_write(i).asUInt()
    SRamArray(i).io.en := (hit_read && Mux(state===s_refill_done,addrReg.Offset(5,4) === i.U,addr.Offset(5,4) === i.U)) || (hit_write && addrReg.Offset(5,4) === i.U) || refill_read || refill_write || fw_read
    SRamArray(i).io.wen := (hit_write && addrReg.Offset(5,4) === i.U) || refill_write
  }
  //-------------------------------------状态机------------------------------------------------
  switch(state) {
    is(s_idle) {
      when(!hit && io.bus.req.valid ){
        state := s_miss
      }.elsewhen(io.bus.req.valid) {
        state := s_lookUp
      }
    }
    is(s_lookUp) {
      when(!hit && io.bus.req.valid){
        state := s_miss
      }.elsewhen(!io.bus.req.valid){
        state := s_idle
      }//若hit即完成本次读取回归idle，否则需要icache从mem读取并且refill icache
    }
    is(s_miss) {
      when(!needWriteBack || io.to_rw.aw.ready){
        state := s_replace
      }
    }
    is(s_replace) {
      when(io.to_rw.ar.ready){
        state := s_refill
      }
    }
    is(s_refill) {
      when((readMemCnt === RetTimes.U) && (!needWriteBack) && needRefill){
        state := s_refill_done
        needRefill := false.B
      }
    }
    is(s_refill_done){
      state := s_idle
    }
  }

  //AXI连线
  //write
  io.to_rw.aw.bits.prot     := AXI_PROT.UNPRIVILEGED | AXI_PROT.SECURE | AXI_PROT.DATA
  io.to_rw.aw.bits.id       := 0.U
  io.to_rw.aw.bits.user     := 0.U
  io.to_rw.aw.bits.len      := 7.U
  io.to_rw.aw.bits.size     := AXI_SIZE.bytes8
  io.to_rw.aw.bits.burst    := AXI4Parameters.BURST_INCR
  io.to_rw.aw.bits.lock     := 0.U
  io.to_rw.aw.bits.cache    := 0.U
  io.to_rw.aw.bits.qos      := 0.U
  io.to_rw.aw.bits.region   := 0.U

  io.to_rw.w.bits.user      := 0.U

  //read
  io.to_rw.ar.bits.prot   := AXI_PROT.UNPRIVILEGED | AXI_PROT.SECURE | AXI_PROT.DATA
  io.to_rw.ar.bits.id     := 0.U
  io.to_rw.ar.bits.user   := 0.U
  io.to_rw.ar.bits.len    := 7.U
  io.to_rw.ar.bits.size   := AXI_SIZE.bytes8
  io.to_rw.ar.bits.burst  := AXI4Parameters.BURST_INCR
  io.to_rw.ar.bits.lock   := 0.U
  io.to_rw.ar.bits.cache  := 0.U
  io.to_rw.ar.bits.qos    := 0.U
  io.to_rw.ar.bits.region := 0.U

//   when(state === s_miss || state === s_replace || state === s_refill ){
//     printf("cache num %d, state %d, needwb %d, needrefill %d, readMenCnt %d, writeMemCnt %d, ar_valid %d, ar_ready %d, r_valid %d, r_ready %d, aw_valid %d, aw_ready %d, w_valid %d, w_ready %d\n", cacheNum.U, state, needWriteBack,needRefill,readMemCnt, writeMemCnt, io.to_rw.ar.valid,io.to_rw.ar.ready,io.to_rw.r.valid,io.to_rw.r.ready,io.to_rw.aw.valid,io.to_rw.aw.ready,io.to_rw.w.valid,io.to_rw.w.ready)
//   }
//   when(io.bus.req.valid ){
//     printf("LSU1 valid %d, addr %x, isStore %d, data %x, wmask %x\n",io.bus.req.valid, io.bus.req.bits.addr,io.bus.req.bits.isWrite,io.bus.req.bits.data,io.bus.req.bits.wmask)
//     //printf("LSU2 valid %d, addr %x, isStore %d, data %x, wmask %x\n",io.req(1).valid,io.req(1).bits.addr,io.req(1).bits.isWrite,io.req(1).bits.data,io.req(1).bits.wmask)
//   }
//   when(io.bus.resp.ready && (state =/= s_idle) ){
//     printf("cache out valid , out inst is %x\n",io.bus.resp.bits.data)
//     printf("readReg1 %x, offset %x\n",readReg.asUInt,addrReg.Offset)
// //    printf("readReg1 %x, offset %x\n",readReg(1).asUInt,addrReg(1).Offset)
//   }
//   when(io.to_rw.ar.fire){
//     printf("ar addr is %x\n", io.to_rw.ar.bits.addr)
//   }
//   when(io.to_rw.aw.fire){
//     printf("aw addr is %x\n", io.to_rw.aw.bits.addr)
//   }

}


