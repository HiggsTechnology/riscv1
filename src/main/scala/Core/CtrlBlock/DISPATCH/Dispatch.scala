package Core.CtrlBlock.DISPATCH

import Core.CtrlBlock.IDU.FuncType
import Core.ExuBlock.FU.MDUOpType
import Core.{Config, MicroOp, RSType}
import chisel3._
import chisel3.util._
import utils._

class DispatchIN extends Bundle with Config {
  val can_allocate = Input(Bool())
  val microop_in   = Vec(2, Flipped(ValidIO(new MicroOp)))
}
class DispatchOUT extends Bundle with Config {
  val microop_out  = Vec(2, ValidIO(new MicroOp))
  val rs_num_out   = Vec(2, Output(UInt(log2Up(RSNum).W)))
}
class DispatchIO extends Bundle with Config {
  val in  = new DispatchIN
  val out = new DispatchOUT
}

class ALUPtr extends CircularQueuePtr[ALUPtr](2) with HasCircularQueuePtrHelper{
}
// 1csr 1jump 2alu 1lsu
class Dispatch extends Module with Config {
  val io = IO(new DispatchIO)
  val rs_num  = Wire(Vec(2, Output(UInt(log2Up(RSNum).W))))
  val is_alu: Vec[Bool] = VecInit(io.in.microop_in.map(item => {item.valid && item.bits.ctrl.funcType === FuncType.alu}))
  val alu_ptr = RegInit(VecInit((0 until 2).map(_.U.asTypeOf(new ALUPtr))))
  for(i <- 0 until 2){
    rs_num(i) := LookupTree(io.in.microop_in(i).bits.ctrl.funcType, List(
      FuncType.csr -> RSType.jumprs,
      FuncType.bru -> RSType.jumprs,
      FuncType.alu -> RSType.alurs,
      FuncType.lsu -> RSType.lsurs,
      FuncType.mdu -> RSType.murs
    ))
  }
  when(io.in.can_allocate){
    alu_ptr := VecInit(alu_ptr.map(_ + PopCount(is_alu)))
  }
  for(i <- 0 until 2){
    val funcType = io.in.microop_in(i).bits.ctrl.funcType
    when(funcType === FuncType.alu){
      val sel_rs = if (i == 0) 0.U else PopCount(is_alu.take(i))
      io.out.rs_num_out(i) := rs_num(i) + alu_ptr(sel_rs).value
    }.elsewhen(funcType === FuncType.mdu){
      val mdu_num = Mux(!MDUOpType.isDiv(funcType), 0.U, 1.U)//murs->4.U,durs->5.U
      io.out.rs_num_out(i) := rs_num(i) + mdu_num
    }.otherwise{
      io.out.rs_num_out(i) := rs_num(i)
    }
  }
  io.out.microop_out := io.in.microop_in
}