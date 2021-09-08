package Core.EXU

import Core.Config.Config
import Core.IDU.FuncType
import chisel3._
import chisel3.util._
import utils.{BRU_OUTIO, CfCtrl, LookupTree}

object BRUOpType {
  def jal  = "b1011000".U
  def jalr = "b1011010".U
  def beq  = "b0010000".U
  def bne  = "b0010001".U
  def blt  = "b0010100".U
  def bge  = "b0010101".U
  def bltu = "b0010110".U
  def bgeu = "b0010111".U
  def isJalr(func: UInt): Bool = func(6)
}

class BRUIO extends Bundle {
  val in  = Flipped(Valid(new CfCtrl))
  val out = Valid(new BRU_OUTIO)
}

class BRU extends Module with Config {
  val io = IO(new BRUIO)
  val src1 = Wire(UInt(XLEN.W))
  val src2 = Wire(UInt(XLEN.W))
  src1 := io.in.bits.data.src1
  src2 := io.in.bits.data.src2

  io.out.bits.ena := (io.in.bits.ctrl.funcType === FuncType.bru) && LookupTree(io.in.bits.ctrl.funcOpType, List(
    BRUOpType.jal   ->  (true.B),
    BRUOpType.jalr  ->  (true.B),
    BRUOpType.beq   ->  (src1 === src2),
    BRUOpType.bne   ->  (src1 =/= src2),
    BRUOpType.blt   ->  (src1.asSInt() < src2.asSInt()),
    BRUOpType.bge   ->  (src1.asSInt() >= src2.asSInt()),
    BRUOpType.bltu  ->  (src1 < src2),
    BRUOpType.bgeu  ->  (src1 >= src2)
  ))
  // 非流水线实现，立即完成
  io.out.valid := io.in.valid
  io.out.bits.new_pc := Mux(io.in.bits.ctrl.funcOpType === BRUOpType.jalr,
    Cat(io.in.bits.data.src1(XLEN - 1,1), 0.U(1.W)) + io.in.bits.data.imm, io.in.bits.cf.pc + io.in.bits.data.imm)
  when (io.in.valid) {
    printf("bru enable\n");
  }
}