package Core.ExuBlock.Mem

import chisel3._
import chisel3.util._
import Core.Config.lsqSize
import Core.{Config, FuInPut, FuOutPut, MicroOp}
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
}

class LSQ extends Module with Config with HasCircularQueuePtrHelper{
  val io = IO(new LSQIO)

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

  for (i <- 0 until lsqSize){
    for(j <- 0 until 4){
      when(valid(i) && io.ExuResult(j).valid && io.ExuResult(j).bits.uop.ctrl.rfWen && (io.ExuResult(j).bits.uop.pdest === decode(i).psrc(0)) && (addrState(i)===false.B)){
        addr(i) := io.ExuResult(j).bits.res + decode(i).data.imm
        addrState(i) := true.B
      }
      when(valid(i) && io.ExuResult(j).valid && io.ExuResult(j).bits.uop.ctrl.rfWen && (io.ExuResult(j).bits.uop.pdest === decode(i).psrc(1)) && (dataState(i)===false.B && is_store(i))){
        data(i) := io.ExuResult(j).bits.res
        dataState(i) := true.B
      }
    }
    for(j <- 0 until 2){
      when(valid(i) && io.lsu_out(j).valid && io.lsu_out(j).bits.uop.ctrl.rfWen && (io.lsu_out(j).bits.uop.pdest === decode(i).psrc(0)) && (addrState(i)===false.B)){
        addr(i) := io.lsu_out(j).bits.res + decode(i).data.imm
        addrState(i) := true.B
      }
      when(valid(i) && io.lsu_out(j).valid && io.lsu_out(j).bits.uop.ctrl.rfWen && (io.lsu_out(j).bits.uop.pdest === decode(i).psrc(1)) && (dataState(i)===false.B && is_store(i))){
        data(i) := io.lsu_out(j).bits.res
        dataState(i) := true.B
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

      for (j <- 0 until 4) {
        when(io.ExuResult(j).valid && io.ExuResult(j).bits.uop.ctrl.rfWen && (io.ExuResult(j).bits.uop.pdest === io.in(i).bits.psrc(0)) && io.in(i).bits.srcState(0) === false.B) {
          addr(enq_vec(i).value) := io.ExuResult(j).bits.res + io.in(i).bits.data.imm
          addrState(enq_vec(i).value) := true.B
        }
        when(io.ExuResult(j).valid && io.ExuResult(j).bits.uop.ctrl.rfWen && (io.ExuResult(j).bits.uop.pdest === io.in(i).bits.psrc(1)) && io.in(i).bits.srcState(1) === false.B) {
          data(enq_vec(i).value) := io.ExuResult(j).bits.res
          dataState(enq_vec(i).value) := io.in(i).bits.ctrl.funcOpType(3)
        }
      }

      for (j <- 0 until 2) {
        when(io.lsu_out(j).valid && io.lsu_out(j).bits.uop.ctrl.rfWen && (io.lsu_out(j).bits.uop.pdest === io.in(i).bits.psrc(0)) && io.in(i).bits.srcState(0) === false.B) {
          addr(enq_vec(i).value) := io.lsu_out(j).bits.res + io.in(i).bits.data.imm
          addrState(enq_vec(i).value) := true.B
        }
        when(io.lsu_out(j).valid && io.lsu_out(j).bits.uop.ctrl.rfWen && (io.lsu_out(j).bits.uop.pdest === io.in(i).bits.psrc(1)) && io.in(i).bits.srcState(1) === false.B) {
          data(enq_vec(i).value) := io.lsu_out(j).bits.res
          dataState(enq_vec(i).value) := io.in(i).bits.ctrl.funcOpType(3)
        }
      }
    }
  }

  val vaild_enq = VecInit(io.in.map(_.valid && allowEnq))
  enq_vec := VecInit(enq_vec.map(_ + PopCount(vaild_enq)))

  //发射
  io.lsu_in(0).valid := !issued(deq_vec(0).value) && valid(deq_vec(0).value) && addrState(deq_vec(0).value) && (dataState(deq_vec(0).value) || !is_store(deq_vec(0).value))
  io.lsu_in(1).valid := !issued(deq_vec(1).value) && valid(deq_vec(1).value) && addrState(deq_vec(1).value) && (dataState(deq_vec(1).value) || !is_store(deq_vec(1).value)) && io.lsu_in(0).valid && addr(deq_vec(1).value) =/= addr(deq_vec(0).value)
  for(i <- 0 until 2){
    io.lsu_in(i).bits.uop := decode(deq_vec(i).value)
    io.lsu_in(i).bits.src(0) := addr(deq_vec(i).value)
    io.lsu_in(i).bits.src(1) := data(deq_vec(i).value)
    when(io.lsu_in(i).valid){issued(deq_vec(i).value) := true.B}
  }

  //等待写回
  val needresp = Wire(Vec(2,Bool()))
  for(i <- 0 until 2){
    needresp(i) := io.lsu_in(i).valid || issued(deq_vec(i).value)
  }

  for(i <- 0 until 2){
    when(io.lsu_out(i).valid){resp(deq_vec(i).value) := true.B}
  }

  when(needresp(0)===true.B && needresp(1)===false.B){
    when(io.lsu_out(0).valid){
      valid(deq_vec(0).value) := false.B
      deq_vec := VecInit(deq_vec.map(_ + 1.U))
    }
  }.elsewhen(needresp(0)===true.B && needresp(1)===true.B){
    when((io.lsu_out(0).valid || resp(deq_vec(0).value)) && (io.lsu_out(1).valid || resp(deq_vec(1).value))){
      valid(deq_vec(0).value) := false.B
      valid(deq_vec(1).value) := false.B
      deq_vec := VecInit(deq_vec.map(_ + 2.U))
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


}
