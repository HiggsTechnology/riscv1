package Core.CtrlBlock.DISPATCH

import Core.Config.{DispatchQueueSize, ExuNum}
import Core.{Config, MicroOp}
import chisel3._
import chisel3.util._
import utils._

class DispatchQueuePtr extends CircularQueuePtr[DispatchQueuePtr](DispatchQueueSize) with HasCircularQueuePtrHelper {
  override def cloneType = (new DispatchQueuePtr).asInstanceOf[this.type]
}
class DispatchQueueIN extends Bundle {
  val microop_in      = Vec(2, Flipped(ValidIO(new MicroOp)))
  val rs_num_in       = Vec(2, Input(UInt(log2Up(ExuNum).W)))
  val rs_can_allocate = Vec(ExuNum, Input(Bool()))
}
class DispatchQueueOUT extends Bundle {
  val can_allocate = Output(Bool())
  val microop_out  = Vec(2, ValidIO(new MicroOp))
  val rs_num_out   = Vec(2, Output(UInt(log2Up(ExuNum).W)))
}
class DispatchQueueIO extends Bundle {
  val in  = new DispatchQueueIN
  val out = new DispatchQueueOUT
}
//dispatchQueue 为dispatch发出信号的缓冲模块，接收由Dispach发出的指令，最多可存储 DispatchQueueSize=8条，发送至ExuBlock
class DispatchQueue extends Module with Config with HasCircularQueuePtrHelper {
  val io = IO(new DispatchQueueIO)
  //定义输入数据接收MEM，接收输入MicroOp中的valid、data、rs number
  val vaild     = Mem(DispatchQueueSize, Bool())
  val data      = Mem(DispatchQueueSize, new MicroOp)
  val rs_num    = Mem(DispatchQueueSize, UInt(log2Up(ExuNum).W))
  //定义头指针enq_vec代表入队逻辑，尾指针deq_vec代表出队逻辑
  val enq_vec   = RegInit(VecInit((0 until 2).map(_.U.asTypeOf(new DispatchQueuePtr))))
  val deq_vec   = RegInit(VecInit((0 until 2).map(_.U.asTypeOf(new DispatchQueuePtr))))
  //定义函数判断队为空： 头=尾 指针；
  val isEmpty   = enq_vec(0) === deq_vec(0)
  //由MicroOp valid控制入队操作enq有效
  val vaild_enq = VecInit(io.in.microop_in.map(_.valid))

  //enqueue 入队操作
  //存储输入数据
  for (i <- 0 until 2) {
    vaild(enq_vec(i).value)  := io.in.microop_in(i).valid
    rs_num(enq_vec(i).value) := io.in.rs_num_in(i)
    data(enq_vec(i).value)   := io.in.microop_in(i).bits
  }
  //头指针前移，前移由MicroOp(i).valid决定
  enq_vec := VecInit(enq_vec.map(_ + PopCount(vaild_enq)))
  //调用指针中的distanceBetween函数，判断头指针与尾指针的距离，距离为1代表队列满，故输出can allocate大与1代表可分配入队
  val validEntries = distanceBetween(enq_vec(0), deq_vec(0))
  io.out.can_allocate := (DispatchQueueSize.U - validEntries) > 1.U

  //dequeue 出队操作
  //出队是否有效
  io.out.microop_out(0).valid := io.in.rs_can_allocate(rs_num(deq_vec(0).value)) && vaild(deq_vec(0).value)
  io.out.microop_out(1).valid := (io.in.rs_can_allocate(rs_num(deq_vec(1).value)) && vaild(deq_vec(1).value)) && (rs_num(deq_vec(0).value) =/= rs_num(deq_vec(1).value))
  for (i <- 0 until 2) {
    io.out.microop_out(i).bits  := data(deq_vec(i).value)
    io.out.rs_num_out(i)        := rs_num(deq_vec(i).value)
  }
  deq_vec := VecInit(deq_vec.map(_ + io.out.microop_out(0).valid + io.out.microop_out(1).valid))

}