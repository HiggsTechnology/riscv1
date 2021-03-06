package Core.EXU

import Core.Config.Config
import chisel3._
import chisel3.util._
import utils._

object ALUOpType {
  def add  = "b1000000".U
  def sll  = "b0000001".U
  def slt  = "b0000010".U
  def sltu = "b0000011".U
  def xor  = "b0000100".U
  def srl  = "b0000101".U
  def or   = "b0000110".U
  def and  = "b0000111".U
  def sub  = "b0001000".U
  def sra  = "b0001101".U
  def lui  = "b0001111".U

  def addw = "b1100000".U
  def subw = "b0101000".U
  def sllw = "b0100001".U
  def srlw = "b0100101".U
  def sraw = "b0101101".U

  def isWordOp(func: UInt) = func(5)  //if 32bit 
}

class ALUIO extends Bundle {
  val in  = Flipped(Valid(new CfCtrl))
  val out = Valid(new ALU_OUTIO)
}

class ALU extends Module with Config {
  val io = IO(new ALUIO)
  val src1 = Wire(UInt(XLEN.W))
  val src2 = Wire(UInt(XLEN.W))
  src1 := io.in.bits.data.src1
  src2 := io.in.bits.data.src2
  val shamt = Mux(ALUOpType.isWordOp(io.in.bits.ctrl.funcOpType), src2(4, 0), src2(5, 0))
  val res = LookupTree(io.in.bits.ctrl.funcOpType, List(
    ALUOpType.add   ->   (src1 + src2),
    ALUOpType.sll   ->   (src1 << shamt),
    ALUOpType.slt   ->   (Cat(0.U((XLEN - 1).W), src1.asSInt() < src2.asSInt())),
    ALUOpType.sltu  ->   (src1 < src2),
    ALUOpType.xor   ->   (src1 ^ src2),
    ALUOpType.srl   ->   (src1 >> shamt),
    ALUOpType.or    ->   (src1 | src2),
    ALUOpType.and   ->   (src1 & src2),
    ALUOpType.sub   ->   (src1 - src2),
    ALUOpType.sra   ->   ((src1.asSInt >> shamt).asUInt),
    ALUOpType.addw  ->   (src1 + src2),
    ALUOpType.subw  ->   (src1 - src2),
    ALUOpType.sllw  ->   (src1 << shamt),
    ALUOpType.srlw  ->   (src1(31,0) >> shamt),
    ALUOpType.sraw  ->   ((src1(31,0).asSInt() >> shamt).asUInt()),
    ALUOpType.lui  ->    (src2)
  ))
  // 非流水线状态，立即完成
  io.out.valid := io.in.valid
  io.out.bits.aluRes := Mux(ALUOpType.isWordOp(io.in.bits.ctrl.funcOpType), SignExt(res(31,0), 64), res)
  when (io.in.valid) {
    printf("alu enable\n")
  }
}

