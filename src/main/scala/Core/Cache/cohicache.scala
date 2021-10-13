package Core.Cache

import Bus.SimpleBus
import Core.AXI4.AXI4Parameters.{AXI_PROT, AXI_SIZE, dataBits}
import Core.AXI4.{AXI4IO, AXI4Parameters, AXIParameter}
import Core.{Config, cohResp}
import chisel3._
import chisel3.util._

trait ICacheConfig extends AXIParameter{
  def TotalSize = 4 //Kb
  def Ways = 1
  def LineSize = 64 // byte
  def Sets = TotalSize * 1024 / LineSize / Ways //4096 / 64
  def OffsetBits = log2Up(LineSize) //对应的是字节标号
  def IndexBits = log2Up(Sets)
  def TagBits = 64 - OffsetBits - IndexBits
  def CacheDataBits = LineSize*8
  def axiDataBits = 64
  def retTimes = CacheDataBits/axiDataBits
  def cacheUseTabCnt = 32
  def CacheCatNum  = 4
  def addrBundle = new Bundle {
    val tag        = UInt(TagBits.W)
    val index      = UInt(IndexBits.W)
    val Offset = UInt(OffsetBits.W)
  }
}

class ICacheIO extends Bundle with Config {
  val bus = Vec(2,Flipped(new SimpleBus))
  val to_rw   = new AXI4IO   //
  val cohreq = ValidIO(UInt(XLEN.W))
  val cohresp = Flipped(ValidIO(new cohResp))
}

class ICache(cacheNum: Int = 0) extends Module with Config with ICacheConfig with AXIParameter {
  val io = IO(new ICacheIO)
  io.to_rw := DontCare

  val s_idle :: s_lookUp :: s_miss :: s_replace :: s_refill :: s_refill_done :: Nil = Enum(6)
  val state: UInt   = RegInit(s_idle)
  val valid         = RegInit(VecInit(Seq.fill(Sets)(false.B)))
  val tagArray      = Mem(Sets, UInt(TagBits.W))
  //val dataArray     = Seq.fill(cacheCatNum)(Mem(Sets, Vec(LineSize/cacheCatNum, UInt(8.W))))
  val readReg       = Wire(Vec(CacheCatNum, UInt((LineSize*8/CacheCatNum).W) ))// 128bit * 4

  val SRam_read = Seq.fill(CacheCatNum)(WireInit(VecInit(Seq.fill(LineSize/CacheCatNum)(0.U(8.W)))))
  val SRam_write  = Seq.fill(CacheCatNum)(WireInit(VecInit(Seq.fill(LineSize/CacheCatNum)(0.U(8.W)))))


  //stage1 拆信号 判断hitvec
  val addr        = io.bus.map(_.req.bits.addr.asTypeOf(addrBundle))
  val storeEn     = io.bus.map(_.req.valid && (state === s_idle || state === s_lookUp))
  val reqValid    = Reg(Vec(2, Bool()))
  when(storeEn(0) || storeEn(1)) {
    reqValid(0) := io.bus(0).req.valid
    reqValid(1) := io.bus(1).req.valid
  }
  val addrReg     = Seq.fill(FETCH_WIDTH)(Reg(addrBundle))
  for (j <- 0 until 2) {
    when(storeEn(j)) {
      addrReg(j) := addr(j)
    }
  }
  val hit = Wire(Vec(FETCH_WIDTH, Bool()))
  val axireadMemCnt = Reg(UInt(log2Up(retTimes+1).W))

  for (i <- 0 until 2) {
    hit(i) := io.bus(i).req.valid && valid(addr(i).index) && tagArray.read(addr(i).index) === addr(i).tag //&& ((state === s_idle) || (state === s_lookUp))
  }
  //hit Reg has one clk lag
  val hitReg = Seq.fill(FETCH_WIDTH)(Reg(Bool()))
  for (j <- 0 until 2) {
    when(storeEn(0) || storeEn(1)) {
      hitReg(j) := hit(j)
    }
  }
  val needRefill = Seq.fill(2)(Reg(Bool()))
  when(storeEn(0) && storeEn(1)) {
    needRefill(0) := io.bus(0).req.valid && !hit(0)
    needRefill(1) := io.bus(1).req.valid && !hit(1) && (addr(0).tag =/= addr(1).tag || addr(0).index =/= addr(1).index)
  }
  val refill_idx = Mux(needRefill(0),addrReg(0).index,addrReg(1).index)
  //s_miss
  val validVec = Seq.fill(FETCH_WIDTH)(Wire(Bool()))
  for (i <- 0 until 2) {
    validVec(i) := valid(addrReg(i).index)
  }
  io.cohreq.valid := state === s_miss
  io.cohreq.bits := Mux(needRefill(0),addrReg(0).asUInt(),addrReg(1).asUInt())

  for( i <- 0 until  CacheCatNum){
    //val refill_idx = Mux(needRefill(0),addrReg(0).index,addrReg(1).index)
    when(state === s_miss && io.cohresp.valid && io.cohresp.bits.needforward){
      tagArray.write(refill_idx,Mux(needRefill(0),addrReg(0).tag,addrReg(1).tag))
      valid(refill_idx)     := true.B
      SRam_write(i) := io.cohresp.bits.forward(i).asTypeOf(Vec(LineSize/CacheCatNum, UInt(8.W)))
      //dataArray(i).write(refill_idx,io.cohresp.bits.forward(i).asTypeOf(Vec(LineSize/cacheCatNum, UInt(8.W))))
    }
  }
  //replace

  when(state === s_replace){
    axireadMemCnt := 0.U
  }

  io.to_rw.ar.valid := (state === s_replace)
  when(needRefill(0) && needRefill(1)) {
    io.to_rw.ar.bits.addr := Cat(addrReg(0).tag, addrReg(0).index, 0.U(OffsetBits.W))
  }.elsewhen(needRefill(0) || needRefill(1)){
    for (j <- 0 until 2) {
      when(needRefill(j)){
        io.to_rw.ar.bits.addr := Cat(addrReg(j).tag, addrReg(j).index, 0.U(OffsetBits.W))
      }
    }
  }.otherwise{
    io.to_rw.ar.bits.addr := DontCare
  }

  //refill
  io.to_rw.r.ready := (state === s_refill) && (axireadMemCnt < retTimes.U)
  val readDataReg = RegInit(VecInit(Seq.fill(retTimes)(0.U(axiDataBits.W))))

  when(axireadMemCnt < retTimes.U && state === s_refill){
    when(needRefill(0)) {
      readDataReg(axireadMemCnt) := io.to_rw.r.bits.data(63,0)//Mux(forwarden(0),forwardinst(0),io.to_rw.r.bits.data)
    }.elsewhen(needRefill(1)){
      readDataReg(axireadMemCnt) := io.to_rw.r.bits.data(63,0)//Mux(forwarden(1),forwardinst(1),io.to_rw.r.bits.data)
    }
    when(io.to_rw.r.valid){
      axireadMemCnt := axireadMemCnt + 1.U
    }
  }

  val mem_wb = Seq.fill(CacheCatNum)(Wire(Vec(LineSize/CacheCatNum, UInt(8.W))))
  for(i <- 0 until  CacheCatNum){
    mem_wb(i) := readDataReg.asUInt()(128*i+127,128*i).asTypeOf(mem_wb(i))
  }

  for( i <- 0 until  CacheCatNum){
    //val refill_idx = Mux(needRefill(0),addrReg(0).index,addrReg(1).index)
    when(state === s_refill && axireadMemCnt === retTimes.U){
      tagArray.write(refill_idx,Mux(needRefill(0),addrReg(0).tag,addrReg(1).tag))
      valid(refill_idx)     := true.B
      SRam_write(i) := mem_wb(i)
      //dataArray(i).write(refill_idx,mem_wb(i))
    }.otherwise{
      mem_wb(i) := DontCare
    }
  }
  //for refill done state hit match
  val tagHitVec_done = Seq.fill(2)(Wire(Bool()))
  for (i <- 0 until 2) {
    tagHitVec_done(i) := tagArray(i)(addrReg(i).index) === addrReg(i).tag && (state === s_refill_done)
  }

  val readIdx = Wire(Vec(2,UInt(IndexBits.W)))
  for(i <- 0 until 2){
    readIdx(i) := Mux(state===s_refill_done,addrReg(i).index,addr(i).index)
  }
  for(i <- 0 until CacheCatNum){
    if(i > 1){
      readReg(i) := SRam_read(i).asUInt()//RegNext(dataArray(i).read(readIdx(0)).asUInt())
    }
    if(i < 2){
      readReg(i) := SRam_read(i).asUInt()//RegNext(dataArray(i).read(readIdx(1)).asUInt())
    }
  }

  for (j <- 0 until 2) {
    io.bus(j).req.ready  := (state ===s_idle) || (state ===s_lookUp)
    io.bus(j).resp.bits.data  := readReg.asUInt() >> addrReg(j).Offset * 8.U
    io.bus(j).resp.valid := ((state ===s_lookUp) || RegNext(state === s_refill_done)) && reqValid(j)
  }

  val hit_read = state===s_refill_done || io.bus.req(0).valid
  val fw_write = state === s_miss && io.cohresp.valid && io.cohresp.bits.needforward
  val refill_write = state === s_refill && axireadMemCnt === retTimes.U
  val SRamArray     = Seq.fill(CacheCatNum)(Module(new SRam))



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
      }
    }
    is(s_miss) {
      when(io.cohresp.valid && io.cohresp.bits.needforward && needRefill(0) && needRefill(1)){
        state := s_miss
        needRefill(0) := false.B
      }.elsewhen(io.cohresp.valid && io.cohresp.bits.needforward && ((needRefill(0) && !needRefill(1)) || (!needRefill(0) && needRefill(1)))){
        state := s_refill_done
        needRefill(0) := false.B
        needRefill(1) := false.B
      }.elsewhen(io.cohresp.valid && !io.cohresp.bits.needforward && (needRefill(0) || needRefill(1))){
        state := s_replace
        val refill_idx = Mux(needRefill(0),addrReg(0).index,addrReg(1).index)
        valid(refill_idx)     := false.B
      }
    }
    is(s_replace) {
      when(io.to_rw.ar.ready){
        state := s_refill
      }
    }
    is(s_refill) {
      when((axireadMemCnt === retTimes.U) && needRefill(0) && needRefill(1)){
        state := s_miss
        needRefill(0) := false.B
      }.elsewhen((axireadMemCnt === retTimes.U) && ((needRefill(0) && !needRefill(1)) || (!needRefill(0) && needRefill(1))) ){
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

//  when(state === s_lookUp){
//    printf(" in valid is %d %d \n",io.bus(0).req.valid,io.bus(1).req.valid)
//  }
//
//  when(io.bus(0).req.fire()){
//    printf("hit message :  hit %d:   %d  \n",0.U, hit(0))
//    printf("addridx %x, req.valid %d , valid %d, tagArray is %x , tag is %x \n",addr(0).index,io.bus(0).req.valid, valid(addr(0).index), tagArray(addr(0).index) , addr(0).tag)
//  }
//  when(io.bus(1).req.fire()){
//    printf("hit message :  hit %d:   %d  \n",1.U, hit(1))
//    printf("addridx %x, req.valid %d , valid %d, tagArray is %x , tag is %x \n",addr(1).index,io.bus(1).req.valid, valid(addr(1).index), tagArray(addr(1).index) , addr(1).tag)
//  }
//
// when(state === s_miss || state === s_replace || state === s_refill  || state === s_refill_done){
//   printf("state %d, needrefill %d %d, readMenCnt %d, ar_valid %d, ar_ready %d, r_valid %d, r_ready %d,\n", state,needRefill(0),needRefill(1),axireadMemCnt, io.to_rw.ar.valid,io.to_rw.ar.ready,io.to_rw.r.valid,io.to_rw.r.ready)
// }
// when(io.bus(0).req.valid ||io.bus(1).req.valid ){
//   printf("PC1 valid %d, addr %x, \n",io.bus(0).req.valid,io.bus(0).req.bits.addr)
//   printf("PC2 valid %d, addr %x, \n",io.bus(1).req.valid,io.bus(1).req.bits.addr)
//
// }

// when(io.bus(0).resp.fire || io.bus(1).resp.fire){
//   printf("cache out valid \n")
//   printf("readReg %x \n",readReg.asUInt)
//   printf("data array 1 , idx is %d\n",addr(0).index)
//   for(i<- 0 until cacheCatNum){
//     printf(" %x ",dataArray(i).read(addr(0).index).asUInt())
//   }
//   printf("\ndata array 2 , idx is %d\n",addr(1).index)
//   for(i<- 0 until cacheCatNum){
//     printf(" %x ",dataArray(i).read(addr(1).index).asUInt())
//   }
//   printf("\n")
//   printf("offset0 %x, out inst0 is %x, PC0 is %x ,offset1 %x, out inst1 is %x  PC1  is  %x \n",addrReg(0).Offset,io.bus(0).resp.bits.data,addrReg(0).asUInt,addrReg(1).Offset,io.bus(1).resp.bits.data,addrReg(1).asUInt)
// }
// when(io.to_rw.ar.fire){
//   printf("ar addr is %x\n", io.to_rw.ar.bits.addr)
// }
  //   if(cacheNum==1){
  //   when(state === s_miss || state === s_replace || state === s_refill ){
  //     printf("cache num %d, state %d, needwb %d %d, needrefill %d %d, readMenCnt %d, writeMemCnt %d, ar_valid %d, ar_ready %d, r_valid %d, r_ready %d, aw_valid %d, aw_ready %d, w_valid %d, w_ready %d\n", cacheNum.U, state, needWriteBack(0), needWriteBack(1),needRefill(0),needRefill(1),readMemCnt, writeMemCnt, io.to_rw.ar.valid,io.to_rw.ar.ready,io.to_rw.r.valid,io.to_rw.r.ready,io.to_rw.aw.valid,io.to_rw.aw.ready,io.to_rw.w.valid,io.to_rw.w.ready)
  //   }

  //   when(io.req(0).valid ||io.req(1).valid ){
  //     printf("LSU1 valid %d, addr %x, isStore %d, data %x, wmask %x\n",io.req(0).valid,io.req(0).bits.addr,io.req(0).bits.isWrite,io.req(0).bits.data,io.req(0).bits.wmask)
  //     printf("LSU2 valid %d, addr %x, isStore %d, data %x, wmask %x\n",io.req(1).valid,io.req(1).bits.addr,io.req(1).bits.isWrite,io.req(1).bits.data,io.req(1).bits.wmask)
  //   }
  //   when(io.resp(0).datadone || io.resp(1).datadone){
  //     printf("cache out valid %d %d, %x %x\n",io.resp(0).datadone,io.resp(1).datadone,io.resp(0).data,io.resp(1).data)
  //     printf("readReg1 %x, offset %x\n",readReg(0).asUInt,addrReg(0).Offset)
  //     printf("readReg1 %x, offset %x\n",readReg(1).asUInt,addrReg(1).Offset)
  //   }
  //   when(io.to_rw.ar.fire){
  //     printf("ar addr is %x\n", io.to_rw.ar.bits.addr)
  //   }
  //   when(io.to_rw.aw.fire){
  //     printf("aw addr is %x\n", io.to_rw.aw.bits.addr)
  //   }
  //   }
}

