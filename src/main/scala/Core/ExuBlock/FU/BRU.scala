package Core.ExuBlock.FU

import Core.{BRU_OUTIO, Config, FuInPut, FuOutPut, MisPredictIO}
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
  val jump_pc = Mux((io.in.bits.uop.ctrl.funcOpType === BRUOpType.jalr),
    Cat(io.in.bits.src(0)(XLEN - 1,1), 0.U(1.W)) + io.in.bits.uop.data.imm, io.in.bits.uop.cf.pc + io.in.bits.uop.data.imm)
  io.jmp.bits.new_pc := Mux(io.jmp.bits.taken, jump_pc, io.in.bits.uop.cf.pc + 4.U)
  io.out.bits.uop := io.in.bits.uop
  io.out.valid := io.in.valid
  io.jmp.valid := io.in.valid

  io.jmp.bits.pc := io.in.bits.uop.cf.pc
  io.jmp.bits.ROBIdx := io.in.bits.uop.ROBIdx
  io.jmp.bits.mispred := io.jmp.bits.taken ^ io.in.bits.uop.cf.br_taken //todo:bru里判断，直接传给IBF、RS、LSQ
  io.jmp.bits.is_jalr := io.in.bits.uop.ctrl.funcOpType === BRUOpType.jalr
  io.jmp.bits.is_ret := io.in.bits.uop.ctrl.funcOpType === BRUOpType.jalr && io.in.bits.uop.cf.instr(11,7) === 0.U && (io.in.bits.uop.cf.instr(19,15) === 1.U || io.in.bits.uop.cf.instr(19,15) === 5.U)
  io.jmp.bits.is_call := (io.in.bits.uop.ctrl.funcOpType === BRUOpType.jal || io.in.bits.uop.ctrl.funcOpType === BRUOpType.jalr) && (io.in.bits.uop.cf.instr(11,7) === 1.U || io.in.bits.uop.cf.instr(11,7) === 5.U)
  io.jmp.bits.is_B := io.in.bits.uop.ctrl.funcOpType =/= BRUOpType.jalr && io.in.bits.uop.ctrl.funcOpType =/= BRUOpType.jal
  io.jmp.bits.gshare_idx := io.in.bits.uop.cf.gshare_idx

  // when(io.in.valid){
  //   printf("BRU valid %d, pc %x, inst %x, new_pc %x, taken %d, mispred %d\n",io.in.valid, io.in.bits.uop.cf.pc, io.in.bits.uop.cf.instr, io.jmp.bits.new_pc, io.jmp.bits.taken, io.jmp.bits.mispred)
  //   printf("BRU pc %x, iscall %d, is ret %d\n",io.jmp.bits.pc,io.jmp.bits.is_call,io.jmp.bits.is_ret)
  // }
}