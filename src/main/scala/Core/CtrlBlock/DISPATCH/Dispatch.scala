package Core.CtrlBlock.DISPATCH

import Core.CtrlBlock.IDU.FuncType
import Core.{Config, MicroOp}
import chisel3._
import chisel3.util._
import utils._


class DispatchIO extends Bundle with Config {
  val in  = Vec(2, ValidIO(new MicroOp))

  val can_allocate = Input(Bool())
  val out = Vec(2, Flipped(ValidIO(new MicroOp)))
  val rs_num_out = Vec(2, Output(UInt(log2Up(ExuNum).W)))
}

class ALUPtr extends CircularQueuePtr[ALUPtr](2) with HasCircularQueuePtrHelper{
  override def cloneType = (new IbufPtr).asInstanceOf[this.type]
}

// 1csr 1jump 2alu 1lsu
class Dispatch extends Module with Config {
  val io = IO(new DispatchIO)

  val rs_num = Wire(Vec(2, Output(UInt(log2Up(ExuNum).W))))

  for(i <- 0 until 2){
    rs_num(i) := LookupTree(io.in(i).bits.ctrl.funcType, List(
      FuncType.csr -> 0.W
      FuncType.bru -> 1.W
      FuncType.alu -> 2.W
      FuncType.lsu -> 4.W
    ))
  }

  val alu_in = VecInit(io.in.map(_.vaild && _.bits.ctrl.funcType === FuncType.alu))

  val alu_ptr = RegInit(VecInit((0 until 2).map(_.U.asTypeOf(new ALUPtr))))
  when(io.can_allocate){
    alu_ptr := VecInit(alu_ptr.map(_ + PopCount(alu_in)))
  }

  for(i <- 0 until 2){
    when(io.in(i).bits.ctrl.funcType === FuncType.alu){
      val sel_rs = if (i == 0) 0.U else PopCount(alu_in.take(i))
      io.rs_num_out(i) := rs_num(i) + alu_ptr(sel_rs).value
    }.otherwise{
      io.rs_num_out(i) := rs_num(i)
    }
  }

  io.out := io.in

}