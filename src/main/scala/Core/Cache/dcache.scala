package Core.Cache

import Bus.{SimpleBusParameter, SimpleBus, SimpleReqBundle, SimpleRespBundle}
import Core.AXI4.AXI4Parameters.{AXI_PROT, AXI_SIZE, dataBits}
import Core.AXI4.{AXI4IO, AXI4Parameters, AXIParameter}
import Core.Config
import chisel3._
import chisel3.util._
import utils.ParallelOperation



sealed trait CacheConfig extends AXIParameter{
  def TotalSize = 32 //Kb
  def Ways = 4
  def LineSize = 64 // byte
  def Sets = TotalSize * 1024 / LineSize / Ways
  def OffsetBits = log2Up(LineSize) //对应的是字节标号
  def IndexBits = log2Up(Sets)
  def TagBits = 64 - OffsetBits - IndexBits
  def CacheDataBits = LineSize*8
  def retTimes = CacheDataBits/dataBits
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
  val bus = Vec(2, Flipped(new SimpleBus))
//  val req   = Vec(2,Flipped(DecoupledIO(new SimpleReqBundle)))
//  val resp  = Vec(2,ValidIO(new SimpleRespBundle))
  val to_rw   = new AXI4IO   //
}

class DCache(cacheNum: Int = 0) extends Module with Config with CacheConfig with AXIParameter {
  val io = IO(new CacheIO)

  val s_idle :: s_lookUp :: s_miss :: s_replace :: s_refill :: s_refill_done :: Nil = Enum(6)
  val state: UInt = RegInit(s_idle)

  val valid = Seq.fill(Ways)(RegInit(VecInit(Seq.fill(Sets)(false.B))))
  val dirty = Seq.fill(Ways)(RegInit(VecInit(Seq.fill(Sets)(false.B))))
  val tagArray = Seq.fill(Ways)(Mem(Sets, UInt(TagBits.W)))
  val dataArray = Seq.fill(Ways)(Mem(Sets, Vec(LineSize, UInt(8.W))))
  val cacheUseTab = Seq.fill(Ways)(RegInit(VecInit(Seq.fill(Sets)(0.U(32.W)))))
  val readReg = Seq.fill(2)(Reg(Vec(LineSize, UInt(8.W))))
  val writeMem = Seq.fill(2)(Reg(UInt((LineSize * 8).W)))

  //stage1 拆信号 判断hitvec
  val addr = io.bus.map(_.req.bits.addr.asTypeOf(addrBundle))
  val storeEn = io.bus.map(_.req.valid && (state === s_idle || state === s_lookUp))
  val reqValid = Reg(Vec(2, Bool()))
  when(storeEn(0) || storeEn(1)) {
    reqValid(0) := io.bus(0).req.valid
    reqValid(1) := io.bus(1).req.valid
  }
  val addrReg = Seq.fill(2)(Reg(addrBundle))
  val writeReg = Seq.fill(2)(Reg(new CacheReq))
  for (j <- 0 until 2) {
    when(storeEn(j)) {
      addrReg(j) := addr(j)
      writeReg(j) := io.bus(j).req.bits
    }
  }

  val tagHitVec = Seq.fill(2)(Wire(Vec(Ways, Bool())))
  for (j <- 0 until 2) {
    for (i <- 0 until Ways) {
      tagHitVec(j)(i) := io.bus(j).req.valid && valid(i)(addr(j).index) && tagArray(i)(addr(j).index) === addr(j).tag && ((state === s_idle) || (state === s_lookUp))
    }
  }
  val hit = Wire(Vec(2, Bool()))
  for (j <- 0 until 2) {
    hit(j) := tagHitVec(j).asUInt.orR
  }

  val hitReg = Seq.fill(2)(Reg(Bool()))
  for (j <- 0 until 2) {
    when(storeEn(0) || storeEn(1)) {
      hitReg(j) := hit(j)
    }
  }
  val needRefill = Seq.fill(2)(Reg(Bool()))
  when(storeEn(0) || storeEn(1)) {
    needRefill(0) := io.bus(0).req.valid && !hit(0)
    needRefill(1) := io.bus(1).req.valid && !hit(1) && (addr(0).tag =/= addr(1).tag || addr(0).index =/= addr(1).index)
  }

  //store the meta&data to the readReg or writeReg
  for (j <- 0 until 2) {
    for (i <- 0 until Ways) {
      when(tagHitVec(j)(i)) { //whatever hit write or hit read will clear the cacheUseTable(hit)
        readReg(j) := dataArray(i)(addr(j).index)
        cacheUseTab(i)(addr(j).index) := 0.U
      }.elsewhen(hit(j) && !tagHitVec(j)(i)) {
        cacheUseTab(i)(addr(j).index) := cacheUseTab(i)(addr(j).index) + 1.U
      }
    }
  }

  //forward
  val hazd = Wire(Vec(2,Vec(2,Bool())))
  for(m <- 0 until 2){
    for(n <- 0 until 2){
      hazd(m)(n) :=  RegNext(io.bus(m).req.bits.isWrite && storeEn(m) && hit(m)) && (!io.bus(n).req.bits.isWrite && storeEn(n) && hit(n)) && ((addr(n).tag === addrReg(m).tag) && (addr(n).index === addrReg(m).index))
    }
  }
  for(m <- 0 until 2){
    for(n <- 0 until 2){
      for(j <- 0 until XLEN/8){
        when(writeReg(m).wmask(j) && hazd(m)(n)){
          readReg(n)(addrReg(n).Offset+j.U) := writeReg(m).data(j*8+7,j*8)
        }
      }
    }
  }
  //for refill done state hit match
  val tagHitVec_done = Seq.fill(2)(Wire(Vec(Ways, Bool())))
  for (j <- 0 until 2) {
    for (i <- 0 until Ways) {
      tagHitVec_done(j)(i) := tagArray(i)(addrReg(j).index) === addrReg(j).tag && (state === s_refill_done)
    }
  }
  //hitwrite  is dirty
  for (j <- 0 until 2) {
    when(reqValid(j) && writeReg(j).isWrite && (state === s_lookUp || state ===s_refill_done)) {
      for (i <- 0 until Ways) {
        val writeWay = RegNext(tagHitVec(j)(i)) || tagHitVec_done(j)(i)
        when(writeWay) {
          val wdata = dataArray(i)(addrReg(j).index)
          for (k <- 0 until XLEN / 8) {
            when(writeReg(j).wmask(k)) {
              wdata(addrReg(j).Offset + k.U) := writeReg(j).data(k * 8 + 7, k * 8)
            }
          }
          dataArray(i)(addrReg(j).index) := wdata
          dirty(i)(addrReg(j).index) := true.B
        }
      }
    }
  }

  io.to_rw := DontCare

  //s_miss
  class select extends Bundle {
    val cnt = UInt(32.W)
    val addr = UInt(log2Up(Ways).W)
  }

  def compare(a: select, b: select): select = {
    Mux(a.cnt > b.cnt, a, b)
  }

  val changevec = Seq.fill(2)(Wire(Vec(Ways, new select)))
  val dirtyVec = Seq.fill(2)(Wire(Vec(Ways, Bool())))
  val validVec = Seq.fill(2)(Wire(Vec(Ways, Bool())))
  for (j <- 0 until 2) {
    for (i <- 0 until Ways) {
      changevec(j)(i).cnt := cacheUseTab(i)(addrReg(j).index)
      changevec(j)(i).addr := i.U
      dirtyVec(j)(i) := dirty(i)(addrReg(j).index)
      validVec(j)(i) := valid(i)(addrReg(j).index)
    }
  }

  val selectWay = Wire(Vec(2, UInt(log2Up(Ways).W)))
  val needWriteBack = Wire(Vec(2, Bool()))
  val wb_tag = Wire(Vec(2, UInt(XLEN.W)))
  wb_tag := DontCare
  for (j <- 0 until 2) {
    selectWay(j) := ParallelOperation(changevec(j), compare).addr
    needWriteBack(j) := dirtyVec(j)(selectWay(j)) && validVec(j)(selectWay(j))
    for(i <- 0 until Ways){
      when(i.U === selectWay(j)){
        wb_tag(j) := tagArray(i)(addrReg(j).index)
      }
    }
  }

  val writeDataReg = RegInit(VecInit(Seq.fill(retTimes)(0.U(dataBits.W))))
  val writeMemCnt = Reg(UInt(log2Up(retTimes + 1).W))

  for (i <- 0 until Ways) {
    val way = Mux(needRefill(0),selectWay(0),selectWay(1))
    val refill_idx = Mux(needRefill(0),addrReg(0).index,addrReg(1).index)
    when(i.U === way && (state === s_miss)) {
      for (k <- 0 until retTimes) {
        writeDataReg(k) := dataArray(i)(refill_idx).asUInt >> (k.U * dataBits.U)
      }
    }
  }

  when(state === s_miss) {
    writeMemCnt := 0.U
  }
  when(needRefill(0) && needRefill(1)) {
    io.to_rw.aw.valid := (state === s_miss) && needWriteBack(0)
    io.to_rw.aw.bits.addr := Cat(wb_tag(0), addrReg(0).index, 0.U(OffsetBits.W))
  }.elsewhen(needRefill(0) || needRefill(1)){
    for (j <- 0 until 2) {
      when(needRefill(j)){
        io.to_rw.aw.valid := (state === s_miss) && needWriteBack(j)
        io.to_rw.aw.bits.addr := Cat(wb_tag(j), addrReg(j).index, 0.U(OffsetBits.W))
      }
    }
  }

  //write to mem
  io.to_rw.w.valid  := (state === s_replace || state === s_refill ) && Mux(needRefill(0),needWriteBack(0),needWriteBack(1))
  when(writeMemCnt < retTimes.U) {
    io.to_rw.w.bits.data := writeDataReg(writeMemCnt)
    io.to_rw.w.bits.strb := 0xffffffffL.U
    when(io.to_rw.w.fire){
      writeMemCnt := writeMemCnt + 1.U
    }
    when(writeMemCnt === (retTimes-1).U){
      io.to_rw.w.bits.last := true.B
    }.otherwise{
      io.to_rw.w.bits.last := false.B
    }
  }

  for(i <- 0 until Ways){
    val way = Mux(needRefill(0),selectWay(0),selectWay(1))
    val refill_idx = Mux(needRefill(0),addrReg(0).index,addrReg(1).index)
    when(i.U===way && writeMemCnt === (retTimes-1).U && io.to_rw.w.fire){
      dirty(i)(refill_idx) := false.B
    }
  }
  io.to_rw.b.ready := (state === s_replace || state === s_refill) && (writeMemCnt === retTimes.U)

  //replace
  val readMemCnt = Reg(UInt(log2Up(retTimes+1).W))
  when(state === s_replace){
    readMemCnt := 0.U
  }

  io.to_rw.ar.valid := (state === s_replace)
  when(needRefill(0) && needRefill(1)) {
    io.to_rw.ar.bits.addr := Cat(addrReg(0).tag, addrReg(0).index, 0.U(OffsetBits.W)) // TODO print 验证 asUInt
  }.elsewhen(needRefill(0) || needRefill(1)){
    for (j <- 0 until 2) {
      when(needRefill(j)){
        io.to_rw.ar.bits.addr := Cat(addrReg(j).tag, addrReg(j).index, 0.U(OffsetBits.W))
      }
    }
  }

  //refill
  io.to_rw.r.ready := (state === s_refill) && (readMemCnt < retTimes.U)
  val readDataReg = RegInit(VecInit(Seq.fill(retTimes)(0.U(dataBits.W))))

  when(readMemCnt < retTimes.U && state === s_refill){
    when(needRefill(0)) {
      readDataReg(readMemCnt) := io.to_rw.r.bits.data
    }.elsewhen(needRefill(1)){
      readDataReg(readMemCnt) := io.to_rw.r.bits.data
    }
    when(io.to_rw.r.valid){
      readMemCnt := readMemCnt + 1.U
    }
  }

  val mem_wb = Wire(Vec(LineSize, UInt(8.W)))
  for( i <- 0 until  Ways){
    val way = Mux(needRefill(0),selectWay(0),selectWay(1))
    val refill_idx = Mux(needRefill(0),addrReg(0).index,addrReg(1).index)
    when(i.U===way && state === s_refill && readMemCnt === retTimes.U){
      mem_wb := readDataReg.asTypeOf(mem_wb)
      tagArray(i)(refill_idx)  := Mux(needRefill(0),addrReg(0).tag,addrReg(1).tag)
      valid(i)(refill_idx)     := true.B

      //      when(reqValid(0) && writeReg(0).isWrite && needRefill(0)) {
      //        for (k <- 0 until XLEN / 8) {
      //          when(writeReg(0).wmask(k)) {
      //            mem_wb(addrReg(0).Offset + k.U) := writeReg(0).data(k * 8 + 7, k * 8)
      //          }
      //        }
      //        dirty(i)(addrReg(0).index) := true.B
      //      }.elsewhen(reqValid(0) && !writeReg(0).isWrite && needRefill(0)) {
      //        dirty(i)(addrReg(0).index) := false.B
      //      }.elsewhen(reqValid(1) && writeReg(1).isWrite && needRefill(1)){
      //        for (k <- 0 until XLEN / 8) {
      //          when(writeReg(1).wmask(k)) {
      //            mem_wb(addrReg(1).Offset + k.U) := writeReg(1).data(k * 8 + 7, k * 8)
      //          }
      //        }
      //      }.elsewhen(reqValid(1) && !writeReg(1).isWrite && needRefill(1)) {
      //        dirty(i)(addrReg(1).index) := false.B
      //      }
      //dirty(i)(refill_idx) := false.B
      dataArray(i)(refill_idx) := mem_wb
    }.otherwise{
      mem_wb := DontCare
    }

    when(i.U===way && state === s_refill && readMemCnt === retTimes.U){
      cacheUseTab(i)(refill_idx) := 0.U
    }.elsewhen(i.U=/=way && state === s_refill && readMemCnt === retTimes.U) {
      cacheUseTab(i)(refill_idx) := cacheUseTab(i)(refill_idx) + 1.U
    }

  }

  //s_refill_done


  for (j <- 0 until 2) {
    for (i <- 0 until Ways) {
      when(tagHitVec_done(j)(i)) { //whatever hit write or hit read will clear the cacheUseTable(hit)
        readReg(j) := dataArray(i)(addrReg(j).index)
      }
    }
  }

  //TODO hazrad will happend when the read HIT is after write HIT, write need at lest one clk to write regs, when the read will read the data in the RegNext(CLK)
  for (j <- 0 until 2) {
    io.bus(j).req.ready  := (state ===s_idle) || (state ===s_lookUp)
    io.bus(j).resp.bits.data := readReg(j).asUInt >> addrReg(j).Offset * 8.U
    io.bus(j).resp.valid := ((state ===s_lookUp) || RegNext(state === s_refill_done)) && reqValid(j)
  }





  //-------------------------------------状态机------------------------------------------------
  switch(state) {
    is(s_idle) {
      when((!hit(0) && io.bus(0).req.valid) || (!hit(1) && io.bus(1).req.valid)){
        state := s_miss
      }.elsewhen(io.bus(0).req.valid || io.bus(1).req.valid) {
        state := s_lookUp
      }
    }
    is(s_lookUp) {
      when((!hit(0) && io.bus(0).req.valid) || (!hit(1) && io.bus(1).req.valid)){
        state := s_miss
      }.elsewhen(!io.bus(0).req.valid && !io.bus(1).req.valid){
        state := s_idle
      }//若hit即完成本次读取回归idle，否则需要icache从mem读取并且refill icache
    }
    is(s_miss) {
      when(Mux(needRefill(0),!needWriteBack(0),!needWriteBack(1)) || io.to_rw.aw.ready){
        state := s_replace
      }
    }
    is(s_replace) {
      when(io.to_rw.ar.ready){
        state := s_refill
      }
    }
    is(s_refill) {
      when((readMemCnt === retTimes.U) && (!needWriteBack(0)) && needRefill(0) && needRefill(1)){
        state := s_miss
        needRefill(0) := false.B
      }.elsewhen((readMemCnt === retTimes.U) && Mux(needRefill(0),!needWriteBack(0),!needWriteBack(1))){
        state := s_refill_done
        needRefill(0) := false.B
        needRefill(1) := false.B
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
  io.to_rw.aw.bits.len      := 1.U
  io.to_rw.aw.bits.size     := AXI_SIZE.bytes32
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
  io.to_rw.ar.bits.len    := 1.U
  io.to_rw.ar.bits.size   := AXI_SIZE.bytes32
  io.to_rw.ar.bits.burst  := AXI4Parameters.BURST_INCR
  io.to_rw.ar.bits.lock   := 0.U
  io.to_rw.ar.bits.cache  := 0.U
  io.to_rw.ar.bits.qos    := 0.U
  io.to_rw.ar.bits.region := 0.U


    // if(cacheNum==0){
    // when(state === s_miss || state === s_replace || state === s_refill ){
    //   printf("cache num %d, state %d, needwb %d %d, needrefill %d %d, readMenCnt %d, writeMemCnt %d, ar_valid %d, ar_ready %d, r_valid %d, r_ready %d, aw_valid %d, aw_ready %d, w_valid %d, w_ready %d\n", cacheNum.U, state, needWriteBack(0), needWriteBack(1),needRefill(0),needRefill(1),readMemCnt, writeMemCnt, io.to_rw.ar.valid,io.to_rw.ar.ready,io.to_rw.r.valid,io.to_rw.r.ready,io.to_rw.aw.valid,io.to_rw.aw.ready,io.to_rw.w.valid,io.to_rw.w.ready)
    // }

    // when(io.req(0).valid ||io.req(1).valid ){
    //   printf("LSU1 valid %d, addr %x, isStore %d, data %x, wmask %x\n",io.req(0).valid,io.req(0).bits.addr,io.req(0).bits.isWrite,io.req(0).bits.data,io.req(0).bits.wmask)
    //   printf("LSU2 valid %d, addr %x, isStore %d, data %x, wmask %x\n",io.req(1).valid,io.req(1).bits.addr,io.req(1).bits.isWrite,io.req(1).bits.data,io.req(1).bits.wmask)
    // }
    // when(io.resp(0).datadone || io.resp(1).datadone){
    //   printf("cache out valid %d %d, %x %x\n",io.resp(0).datadone,io.resp(1).datadone,io.resp(0).data,io.resp(1).data)
    //   printf("readReg1 %x, offset %x\n",readReg(0).asUInt,addrReg(0).Offset)
    //   printf("readReg1 %x, offset %x\n",readReg(1).asUInt,addrReg(1).Offset)
    // }
    // when(io.to_rw.ar.fire){
    //   printf("ar addr is %x\n", io.to_rw.ar.bits.addr)
    // }
    // when(io.to_rw.aw.fire){
    //   printf("aw addr is %x\n", io.to_rw.aw.bits.addr)
    // }
    // }
}


