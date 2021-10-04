package Core.ExuBlock.FU

import Core.{Config, FuInPut, FuOutPut}
import chisel3._
import chisel3.util._
import chisel3.util.experimental.BoringUtils
import utils._

object MDUOpType {
  def mul    = "b0000".U
  def mulh   = "b0001".U
  def mulhsu = "b0010".U
  def mulhu  = "b0011".U
  def div    = "b0100".U
  def divu   = "b0101".U
  def rem    = "b0110".U
  def remu   = "b0111".U

  def mulw   = "b1000".U
  def divw   = "b1100".U
  def divuw  = "b1101".U
  def remw   = "b1110".U
  def remuw  = "b1111".U

  def isDiv(op: UInt) = op(2)
  def isDivSign(op: UInt) = isDiv(op) && !op(0)
  def isW(op: UInt) = op(3)
}


class MDUIO extends Bundle {
  val in  = Flipped(ValidIO(new FuInPut))
  val out = ValidIO(new FuOutPut)
  val DivIdle = Output(Bool())
}

class DivIO(val len: Int) extends Bundle {
  val in = Flipped(DecoupledIO(Vec(2, Output(UInt(len.W)))))
  val flush  = Input(Bool())
  //val sign = Input(Bool())
  val out = ValidIO(Output(UInt((len * 2).W)))
  val DivIdle = Output(Bool())
}

class Radix8Divider(len: Int = 64) extends Module {
  val io = IO(new DivIO(len))


  val s_idle :: s_shift :: s_compute ::  Nil = Enum(3)
  val state = RegInit(s_idle)
  io.in.ready := (state === s_idle)
  val newReq = (state === s_idle) && io.in.fire()
  val (a, b) = (io.in.bits(0), io.in.bits(1))
  val shiftReg = Reg(UInt((len * 2).W))
  val (hi,lo) = (shiftReg(len * 2-1, len),shiftReg(len - 1, 0))

  val bReg = RegEnable(a, newReq)
  val aReg = RegEnable(b, newReq)

  val cnt = Reg(UInt((len+1).W))

  when(newReq){
    val canSkipShift = (len.U | Log2(b)) - Log2(a)
    cnt := Mux(canSkipShift >= len.U, len.U, canSkipShift)
    state := s_shift
  }.elsewhen (state === s_shift) {
    shiftReg := aReg << cnt
    state := s_compute
  }.elsewhen (state === s_compute) {
    when(cnt === 64.U){
      val sr1 = Wire(UInt(len.W))
      val x1enough = hi.asUInt >= bReg.asUInt
      sr1 := Mux(x1enough, hi - bReg, hi)
      shiftReg := Cat(sr1,lo(len-2,0),x1enough)
      state := s_idle
    }.elsewhen(cnt === 63.U){
      val sr2 = Wire(UInt(len.W))
      val sr1 = Wire(UInt(len.W))
      val x2enough = hi.asUInt >= bReg.asUInt
      sr2 := Cat(Mux(x2enough, hi - bReg, hi)(len - 2, 0), lo(len-1))
      val x1enough = sr2.asUInt >= bReg.asUInt
      sr1 := Mux(x1enough, sr2 - bReg, sr2)
      shiftReg := Cat(sr1,lo(len-3,0),x2enough,x1enough)
      state := s_idle
    }.elsewhen(cnt===62.U){
      val sr4 = Wire(UInt(len.W))
      val sr2 = Wire(UInt(len.W))
      val sr1 = Wire(UInt(len.W))
      val x4enough = hi.asUInt >= bReg.asUInt
      sr4 := Cat(Mux(x4enough, hi - bReg, hi)(len - 2, 0), lo(len-1))
      val x2enough = sr4.asUInt >= bReg.asUInt
      sr2 := Cat(Mux(x2enough, sr4 - bReg, sr4)(len - 2, 0), lo(len-2))
      val x1enough = sr2.asUInt >= bReg.asUInt
      sr1 := Mux(x1enough, sr2 - bReg, sr2)
      shiftReg := Cat(sr1, lo(len-4,0), x4enough, x2enough, x1enough)
      state := s_idle
    }.otherwise{
      val sr4 = Wire(UInt(len.W))
      val sr2 = Wire(UInt(len.W))
      val sr1 = Wire(UInt(len.W))
      val x4enough = hi.asUInt >= bReg.asUInt
      sr4 := Cat(Mux(x4enough, hi - bReg, hi)(len - 2, 0), lo(len-1))
      val x2enough = sr4.asUInt >= bReg.asUInt
      sr2 := Cat(Mux(x2enough, sr4 - bReg, sr4)(len - 2, 0), lo(len-2))
      val x1enough = sr2.asUInt >= bReg.asUInt
      sr1 := Cat(Mux(x1enough, sr2 - bReg, sr2)(len - 2, 0), lo(len-3))
      shiftReg := Cat(sr1, lo(len-4,0), x4enough, x2enough, x1enough)
    }
  }
  io.out.valid := RegNext(state) === s_compute && state === s_idle
  io.out.bits := shiftReg

}

class DU extends Module with Config {
  val io   = IO(new MDUIO)
  val mul  = Module(new WTMultiplier)
  val div  = Module(new Radix8Divider(XLEN))
  val src1 = Wire(UInt(XLEN.W))
  val src2 = Wire(UInt(XLEN.W))
  val funcOpType = io.in.bits.uop.ctrl.funcOpType
  val isDiv = MDUOpType.isDiv(funcOpType)
  val isDivSign = MDUOpType.isDivSign(funcOpType)
  val isW = MDUOpType.isW(funcOpType)

  src1 := io.in.bits.src(0)
  src2 := io.in.bits.src(1)

  io.out.bits.uop := io.in.bits.uop
  io.out.valid := Mux(isDiv,div.io.out.valid,mul.io.out.valid)

  mul.io.in.bits(0) := src1
  mul.io.in.bits(1) := src2
  mul.io.in.valid   := !isDiv && io.in.valid

  io.DivIdle := div.io.DivIdle


  val res = LookupTree(funcOpType, List(
    MDUOpType.mul   ->   (src1 * src2),//todo:add def to
    MDUOpType.mulh  ->    (src1 * src2),
    MDUOpType.mulhsu   ->   (src1 * src2),
    MDUOpType.mulhu   ->   (src1 * src2),
    MDUOpType.div   ->   (src1 * src2),
    MDUOpType.divu   ->   (src1 * src2),
    MDUOpType.rem   ->   (src1 * src2),
    MDUOpType.remu   ->   (src1 * src2),

    MDUOpType.mulw   ->   (src1 * src2),
    MDUOpType.divw   ->   (src1 * src2),
    MDUOpType.divuw   ->   (src1 * src2),
    MDUOpType.remw   ->   (src1 * src2),
    MDUOpType.remuw   ->   (src1 * src2)
  ))

}

