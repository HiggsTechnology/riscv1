package Core.CtrlBlock.DISPATCH

import Core.Config.{DispatchQueueSize, ExuNum}
import Core.{Config, MicroOp}
import chisel3._
import chisel3.util._
import utils._



class DispatchQueuePtr extends CircularQueuePtr[DispatchQueuePtr](DispatchQueueSize) with HasCircularQueuePtrHelper{
  override def cloneType = (new IbufPtr).asInstanceOf[this.type]
}

class DispatchQueueIO extends Bundle {
  val in  = Vec(2, ValidIO(new MicroOp))
  val rs_num_in = Vec(2, Input(UInt(log2Up(ExuNum).W)))
  val can_allocate = Output(Bool())

  val out = Vec(2, Flipped(ValidIO(new MicroOp)))
  val rs_num_out = Vec(2, Output(UInt(log2Up(ExuNum).W)))
  val rs_can_allocate = Vec(ExuNum, Input(Bool()))
}

class DispatchQueue extends Module with Config with HasCircularQueuePtrHelper {
  val io = IO(new DispatchQueueIO)

  val vaild = Mem(DispatchQueueSize, Bool())
  val data = Mem(DispatchQueueSize, new MicroOp)
  val rs_num = Mem(DispatchQueueSize, UInt(log2Up(ExuNum).W))

  val enq_vec = RegInit(VecInit((0 until 2).map(_.U.asTypeOf(new DispatchQueuePtr))))
  val deq_vec = RegInit(VecInit((0 until 2).map(_.U.asTypeOf(new DispatchQueuePtr))))

  val isEmpty = enq_vec(0) === deq_vec(0)

  //enqueue
  for (i <- 0 until 2) {
    vaild(enq_vec(i).value) := io.in(i).valid
    rs_num(enq_vec(i).value) := io.rs_num_in(i)
    data(enq_vec(i).value) := io.in(i).bits
  }

  val vaild_enq = VecInit(io.in.map(_.vaild))
  enq_vec := VecInit(enq_vec.map(_ + PopCount(vaild_enq)))

  val validEntries = distanceBetween(enq_vec(0), deq_vec(0))
  can_allocate := (DispatchQueueSize.W - validEntries) > 1

  //dequeue
  io.out(0).valid := io.rs_can_allocate(rs_num(deq_vec(0))) && vaild(deq_vec(0))
  io.out(1).valid := io.rs_can_allocate(rs_num(deq_vec(1))) && vaild(deq_vec(1)) && rs_num(deq_vec(0)) != rs_num(deq_vec(1))
  io.out(0).bits := data(deq_vec(0))
  io.out(1).bits := data(deq_vec(1))
  io.rs_num_out(0) := rs_num(deq_vec(0))
  io.rs_num_out(1) := rs_num(deq_vec(1))

  deq_vec := VecInit(deq_vec.map(_ + io.out(0).valid + io.out(1).valid))

}