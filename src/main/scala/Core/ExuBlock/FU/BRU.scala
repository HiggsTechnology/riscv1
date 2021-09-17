package Core.ExuBlock.FU

import Core.{BRU_OUTIO, Config, FuInPut, FuOutPut}
import Core.CtrlBlock.IDU.FuncType
import chisel3._
import chisel3.util._
import utils.LookupTree

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
  val in  = Flipped(ValidIO(new FuInPut))
  val out = ValidIO(new FuOutPut)
  val jmp : ValidIO[BRU_OUTIO] = ValidIO(new BRU_OUTIO)
}

class BRU extends Module with Config {
  val io = IO(new BRUIO)
  val src1 = Wire(UInt(XLEN.W))
  val src2 = Wire(UInt(XLEN.W))
  src1 := io.in.bits.src(0)
  src2 := io.in.bits.src(1)

  io.jmp.bits.taken := (io.in.bits.uop.ctrl.funcType === FuncType.bru) && LookupTree(io.in.bits.uop.ctrl.funcOpType, List(
    BRUOpType.jal   ->  (true.B),
    BRUOpType.jalr  ->  (true.B),
    BRUOpType.beq   ->  (src1 === src2),
    BRUOpType.bne   ->  (src1 =/= src2),
    BRUOpType.blt   ->  (src1.asSInt() < src2.asSInt()),
    BRUOpType.bge   ->  (src1.asSInt() >= src2.asSInt()),
    BRUOpType.bltu  ->  (src1 < src2),
    BRUOpType.bgeu  ->  (src1 >= src2)
  ))

  io.out.bits.res := io.in.bits.uop.cf.pc + 4.U
  io.jmp.bits.new_pc := Mux((io.in.bits.uop.ctrl.funcOpType === BRUOpType.jalr),
    Cat(io.in.bits.src(0)(XLEN - 1,1), 0.U(1.W)) + io.in.bits.uop.data.imm, io.in.bits.uop.cf.pc + io.in.bits.uop.data.imm)
  io.out.bits.uop := io.in.bits.uop
  io.out.valid := io.in.valid
  io.jmp.valid := io.in.valid
}