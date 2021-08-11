package Core.EXU

import Core.IDU.FuncType
import chisel3._
import chisel3.util._
import utils.{BRU_OUTIO, CfCtrl, Config, LookupTree}

object BRUOpType {
  def jal  = "b1011000".U
  def jalr = "b1011010".U
  def beq  = "b0010000".U
  def bne  = "b0010001".U
  def blt  = "b0010100".U
  def bge  = "b0010101".U
  def bltu = "b0010110".U
  def bgeu = "b0010111".U
  def isJal_r(func: UInt): Bool = func(6)
}

class BRUIO extends Bundle {
  val in  = Flipped(new CfCtrl)
  val out = new BRU_OUTIO
}

class BRU extends Module with Config {
  val io = IO(new BRUIO)
  val src1 = Wire(UInt(XLEN.W))
  val src2 = Wire(UInt(XLEN.W))
  src1 := io.in.data.src1
  src2 := io.in.data.src2

  io.out.valid := (io.in.ctrl.funcType === FuncType.bru) && LookupTree(io.in.ctrl.funcOpType, List(
    BRUOpType.jal   ->  (true.B),
    BRUOpType.jalr  ->  (true.B),
    BRUOpType.beq   ->  (src1 === src2),
    BRUOpType.bne   ->  (src1 =/= src2),
    BRUOpType.blt   ->  (src1.asSInt() < src2.asSInt()),
    BRUOpType.bge   ->  (src1.asSInt() >= src2.asSInt()),
    BRUOpType.bltu  ->  (src1 < src2),
    BRUOpType.bgeu  ->  (src1 >= src2)
  ))
  
  io.out.newPC := Mux((io.in.ctrl.funcOpType === BRUOpType.jalr),
    io.in.data.src1 + io.in.data.imm, io.in.cf.pc + io.in.data.imm)

}