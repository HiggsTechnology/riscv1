package Core.ExuBlock.Mem

import chisel3._
import chisel3.util._
import Core.Config.lsqSize
import Core.CtrlBlock.ROB.ROBPtr
import Core.{Config, FuInPut, FuOutPut, MicroOp, MisPredictIO}
import chisel3.{Bool, Bundle, Flipped, Input, Output, UInt, Vec}
import utils.{CircularQueuePtr, HasCircularQueuePtrHelper}

class LSQPtr extends CircularQueuePtr[LSQPtr](lsqSize) with HasCircularQueuePtrHelper{
  override def cloneType = (new LSQPtr).asInstanceOf[this.type]
}

class LSQIO extends Bundle with Config {
  //in
  val in = Vec(2,Flipped(ValidIO(new MicroOp)))

  val SrcIn = Vec(2,Vec(2,Input(UInt(XLEN.W))))

  val ExuResult = Vec(4, Flipped(ValidIO(new FuOutPut)))

  val lsu_in = Vec(2, ValidIO(new FuInPut))
  val lsu_out = Vec(2, Flipped(ValidIO(new FuOutPut)))

  val can_allocate = Output(Bool())

  val predict_robPtr = Input(new ROBPtr)
  val flush = Input(Bool())
  val mispred_robPtr = Input(new ROBPtr)

  val cache_ready = Vec(2,Input(Bool()))
  val lsu_spec_issued = Vec(2,Output(Bool()))
}

class LSQ extends Module with Config with HasCircularQueuePtrHelper{
  val io = IO(new LSQIO)//todo:小于isbranch的robIdx才能发射，ROB传来一个信号

  val decode  = Mem(lsqSize, new MicroOp)
  val valid   = RegInit(VecInit(Seq.fill(lsqSize)(false.B)))
  val addrState = RegInit(VecInit(Seq.fill(lsqSize)(false.B)))
  val dataState = RegInit(VecInit(Seq.fill(lsqSize)(false.B)))
  val addr = Reg(Vec(lsqSize, UInt(XLEN.W)))
  val data = Reg(Vec(lsqSize, UInt(XLEN.W)))
  val is_store = RegInit(VecInit(Seq.fill(lsqSize)(false.B)))

  val issued = RegInit(VecInit(Seq.fill(lsqSize)(false.B)))
  val resp = RegInit(VecInit(Seq.fill(lsqSize)(false.B)))

  //val lsqPtr = RegInit(VecInit((0 until lsqSize).map(_.U.asTypeOf(new LSQPtr))))
  val enq_vec = RegInit(VecInit((0 until 2).map(_.U.asTypeOf(new LSQPtr))))
  val deq_vec = RegInit(VecInit((0 until 2).map(_.U.asTypeOf(new LSQPtr))))



  val validEntries = distanceBetween(enq_vec(0), deq_vec(0))

  val numEnq   = PopCount(io.in.map(_.valid))
  val allowEnq = RegInit(true.B)
  allowEnq  := validEntries + numEnq + 2.U <= lsqSize.U
  io.can_allocate := allowEnq

  //侦听执行单元结果
  for (i <- 0 until lsqSize){
    //LSU外其它执行单元
    for(j <- 0 until 4){Vec
      val monitorValid = valid(i) && io.ExuResult(j).valid
      val exurfWen     = io.ExuResult(j).bits.uop.ctrl.rfWen
      val psrc1Rdy     = io.ExuResult(j).bits.uop.pdest === decode(i).psrc(0)
      val psrc2Rdy     = io.ExuResult(j).bits.uop.pdest === decode(i).psrc(1)
      when(monitorValid && exurfWen && psrc1Rdy && !addrState(i)){
        addr(i) := io.ExuResult(j).bits.res + decode(i).data.imm
        addrState(i) := true.B
      }
      when(monitorValid && exurfWen && psrc2Rdy && (!dataState(i) && is_store(i))){
        data(i) := io.ExuResult(j).bits.res
        dataState(i) := true.B
      }
    }
    //侦听LSU
    for(j <- 0 until 2){
      val lsuValid = valid(i) && io.lsu_out(j).valid
      val lsurfWen = io.lsu_out(j).bits.uop.ctrl.rfWen
      val psrc1Rdy = io.lsu_out(j).bits.uop.pdest === decode(i).psrc(0)
      val psrc2Rdy = io.lsu_out(j).bits.uop.pdest === decode(i).psrc(1)
      when(lsuValid && lsurfWen && psrc1Rdy && !addrState(i)){
        addr(i) := io.lsu_out(j).bits.res + decode(i).data.imm
        addrState(i) := true.B
      }
      when(lsuValid && lsurfWen && psrc2Rdy && (!dataState(i) && is_store(i))){
        data(i) := io.lsu_out(j).bits.res
        dataState(i) := true.B
      }
    }

  }

  val flushed = WireInit(VecInit(Seq.fill(lsqSize)(false.B)))
  //flush
  when(io.flush){
    for(i <- 0 until lsqSize) {
      when(isBefore(io.mispred_robPtr,decode(i).ROBIdx)){
        valid(i) := false.B//todo:置到最后一个
        flushed(i) := valid(i)//当拍的值赋给flushed
      }
    }
  }

  //enq
  for(i <- 0 until 2){
    when(io.in(i).valid && allowEnq) {
      valid(enq_vec(i).value) := true.B
      decode(enq_vec(i).value) := io.in(i).bits
      is_store(enq_vec(i).value) := io.in(i).bits.ctrl.funcOpType(3)
      addrState(enq_vec(i).value) := io.in(i).bits.srcState(0)
      dataState(enq_vec(i).value) := io.in(i).bits.srcState(1) === 1.U && io.in(i).bits.ctrl.funcOpType(3)
      addr(enq_vec(i).value) := io.SrcIn(i)(0) + io.in(i).bits.data.imm
      data(enq_vec(i).value) := io.SrcIn(i)(1)
      issued(enq_vec(i).value) := false.B
      resp(enq_vec(i).value) := false.B

      //入列指令侦听当拍执行单元
      for (j <- 0 until 4) {
        //侦听LSU外其它执行单元
        val exurfWen    = io.ExuResult(j).bits.uop.ctrl.rfWen
        val psrc1Rdy = io.ExuResult(j).bits.uop.pdest === io.in(i).bits.psrc(0)
        val psrc2Rdy = io.ExuResult(j).bits.uop.pdest === io.in(i).bits.psrc(1)
        when(io.ExuResult(j).valid && exurfWen && psrc1Rdy && !io.in(i).bits.srcState(0)) {
          addr(enq_vec(i).value) := io.ExuResult(j).bits.res + io.in(i).bits.data.imm
          addrState(enq_vec(i).value) := true.B
        }
        when(io.ExuResult(j).valid && exurfWen && psrc2Rdy && !io.in(i).bits.srcState(1)) {
          data(enq_vec(i).value) := io.ExuResult(j).bits.res
          dataState(enq_vec(i).value) := io.in(i).bits.ctrl.funcOpType(3)
        }
      }

      for (j <- 0 until 2) {
        //侦听LSU
        val lsurfWen = io.lsu_out(j).bits.uop.ctrl.rfWen
        val psrc1Rdy = io.lsu_out(j).bits.uop.pdest === io.in(i).bits.psrc(0)
        val psrc2Rdy = io.lsu_out(j).bits.uop.pdest === io.in(i).bits.psrc(1)
        when(io.lsu_out(j).valid && lsurfWen && psrc1Rdy && !io.in(i).bits.srcState(0)) {
          addr(enq_vec(i).value) := io.lsu_out(j).bits.res + io.in(i).bits.data.imm
          addrState(enq_vec(i).value) := true.B
        }
        when(io.lsu_out(j).valid && lsurfWen && psrc2Rdy && !io.in(i).bits.srcState(1)) {
          data(enq_vec(i).value) := io.lsu_out(j).bits.res
          dataState(enq_vec(i).value) := io.in(i).bits.ctrl.funcOpType(3)
        }
      }
    }
  }

  val vaild_enq = VecInit(io.in.map(_.valid && allowEnq))
  enq_vec := Mux(io.flush, VecInit(enq_vec.map(_ - PopCount(flushed))), VecInit(enq_vec.map(_ + PopCount(vaild_enq))))

  //发射
  val deq0_dataRdy = (dataState(deq_vec(0).value) && isAfter(io.predict_robPtr,decode(deq_vec(0).value).ROBIdx)) || !is_store(deq_vec(0).value)
  val check_deq0    = io.lsu_in(0).valid && addr(deq_vec(1).value) =/= addr(deq_vec(0).value)
  val deq1_dataRdy = ((dataState(deq_vec(1).value) && isAfter(io.predict_robPtr,decode(deq_vec(1).value).ROBIdx)) || !is_store(deq_vec(1).value)) && check_deq0
  io.lsu_in(0).valid := !io.flush && !issued(deq_vec(0).value) && valid(deq_vec(0).value) && addrState(deq_vec(0).value) && deq0_dataRdy
  io.lsu_in(1).valid := !io.flush && !issued(deq_vec(1).value) && valid(deq_vec(1).value) && addrState(deq_vec(1).value) && deq1_dataRdy && io.cache_ready(0)
  for(i <- 0 until 2){
    io.lsu_in(i).bits.uop := decode(deq_vec(i).value)
    io.lsu_in(i).bits.src(0) := addr(deq_vec(i).value)
    io.lsu_in(i).bits.src(1) := data(deq_vec(i).value)
    when(io.lsu_in(i).valid && io.cache_ready(i)){issued(deq_vec(i).value) := true.B}
  }
  for(i <- 0 until 2){
    io.lsu_spec_issued(i) := valid(deq_vec(i).value) && issued(deq_vec(i).value) && isAfter(decode(deq_vec(i).value).ROBIdx,io.predict_robPtr)
  }
  //等待写回
  val needresp = Wire(Vec(2,Bool()))
  for(i <- 0 until 2){
    needresp(i) := (io.lsu_in(i).valid || issued(deq_vec(i).value)) && valid(deq_vec(i).value)
  }

  for(i <- 0 until 2){
    when(io.lsu_out(i).valid){resp(deq_vec(i).value) := true.B}
  }

  val deq0_bfflush = isBefore(decode(deq_vec(0).value).ROBIdx,io.predict_robPtr)
  val deq1_bfflush = isBefore(decode(deq_vec(1).value).ROBIdx,io.predict_robPtr)
  val check_flush = (!io.flush || deq0_bfflush)
  when(needresp(0)===true.B && (needresp(1)===false.B || valid(deq_vec(1).value) === false.B)){
    when((io.lsu_out(0).valid || resp(deq_vec(0).value)) && check_flush){
      valid(deq_vec(0).value) := false.B
      deq_vec := VecInit(deq_vec.map(_ + 1.U))
    }
  }.elsewhen(needresp(0)===true.B && needresp(1)===true.B){
    when((io.lsu_out(0).valid || resp(deq_vec(0).value)) && (io.lsu_out(1).valid || resp(deq_vec(1).value))){
      when(!io.flush ||(deq0_bfflush && deq1_bfflush)) {
        valid(deq_vec(0).value) := false.B
        valid(deq_vec(1).value) := false.B
        deq_vec := VecInit(deq_vec.map(_ + 2.U))
      }.elsewhen(deq0_bfflush && !deq1_bfflush){
        valid(deq_vec(0).value) := false.B
        deq_vec := VecInit(deq_vec.map(_ + 1.U))
      }
    }
  }


  //  //根据数据相关性，计算can_issue
  //  for(i <- 0 until lsqSize){
  //    val entries_before = distanceBetween(lsqPtr(i), deq_vec(0))
  //    when(entries_before < validEntries){
  //      val compare = WireInit(Vec(lsqSize,Bool()))
  //      for(j <- 0 until lsqSize){
  //        when(j.U < entries_before){
  //          compare(j) := !is_store((deq_vec(0)+j.U).value) || (addrState((deq_vec(0)+j.U).value) && addr((deq_vec(0)+j.U).value) =/= addr(lsqPtr(i).value)) || resp((deq_vec(0)+j.U).value)
  //        }.otherwise {
  //          compare(j) := true.B
  //        }
  //      }
  //      can_issue(i) := compare.asUInt.andR && addrState(lsqPtr(i).value) && (dataState(lsqPtr(i).value) || !is_store(lsqPtr(i).value)) && valid(lsqPtr(i).value)
  //    }
  //  }

  //LSU


  // printf("LSQ enqvalid %d %d, enq_vec %d %d\n", io.in(0).valid && allowEnq, io.in(1).valid && allowEnq, enq_vec(0).value, enq_vec(1).value)
  // printf("LSQ deqvalid %d %d, deq_vec %d %d\n", needresp(0)===true.B && (io.lsu_out(0).valid || resp(deq_vec(0).value)), needresp(0)===true.B && needresp(1)===true.B && (io.lsu_out(0).valid || resp(deq_vec(0).value)) && (io.lsu_out(1).valid || resp(deq_vec(1).value)), deq_vec(0).value, deq_vec(1).value)
  // printf("LSQ to LSU valid %d %d\n",io.lsu_in(0).valid,io.lsu_in(1).valid)
  // //printf("deq0 %d %d %d\n",needresp(0)===true.B && (needresp(1)===false.B || valid(deq_vec(1).value) === false.B), (io.lsu_out(0).valid && resp(deq_vec(0).value)),(!io.flush || isBefore(decode(deq_vec(0).value).ROBIdx,io.predict_robPtr)))
  // for(i <- 0 until lsqSize){
  //   printf("LSQ %d: valid %d, pc %x, inst %x, issued %d, resp %d, addr %x\n",i.U, valid(i),decode(i).cf.pc,decode(i).cf.instr, issued(i), resp(i), addr(i))
  // }


}
