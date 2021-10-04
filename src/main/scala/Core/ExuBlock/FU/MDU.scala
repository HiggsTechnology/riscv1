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

class MulDivIO(val len: Int) extends Bundle {
  val in = Flipped(DecoupledIO(Vec(2, Output(UInt(len.W)))))
  val flush  = Input(Bool())
  val sign = Input(Bool())
  val out = DecoupledIO(Output(UInt((len * 2).W)))
}

class WTMultiplier extends Module {
  val io = IO(new Bundle{
    val src1 = Input(UInt(64.W))
    val src2 = Input(UInt(64.W))
    val res = Output(UInt(128.W))
  })
  //level 0
  val branch0 = Wire(Vec(64, UInt(64.W)))
  for(i <- 0 until 64){
    branch0(i) := Mux(io.src2(i),io.src1,0.U)
  }
  //level 1
  val branch1 = Wire(Vec(32, UInt(66.W)))
  for(i <- 0 until 32){
    branch1(i) := Cat(0.U(1.W),branch0(2*i+1),0.U(1.W)) + Cat(0.U(2.W),branch0(2*i))
  }
  //level2
  val branch2 = Wire(Vec(16, UInt(68.W)))
  for(i <- 0 until 16){
    branch2(i) := Cat(branch1(2*i+1),0.U(2.W)) + Cat(0.U(2.W),branch1(2*i))
  }
  //level3
  val branch3 = Wire(Vec(8, UInt(72.W)))
  for(i <- 0 until 8){
    branch3(i) := Cat(branch2(2*i+1),0.U(4.W)) + Cat(0.U(4.W),branch2(2*i))
  }
  //level4
  val branch4 = Wire(Vec(4, UInt(80.W)))
  for(i <- 0 until 4){
    branch4(i) := Cat(branch3(2*i+1),0.U(8.W)) + Cat(0.U(8.W),branch3(2*i))
  }
  //level5
  val branch5 = Wire(Vec(2, UInt(96.W)))
  for(i <- 0 until 2){
    branch5(i) := Cat(branch4(2*i+1),0.U(16.W)) + Cat(0.U(16.W),branch4(2*i))
  }
  //level6
  io.res := Cat(branch5(1),0.U(32.W)) + Cat(0.U(32.W),branch5(0))

}


class Radix8Divider(len: Int = 64) extends Module {
  val io = IO(new MulDivIO(len))

  def abs(a: UInt, sign: Bool): (Bool, UInt) = {val s = a(len - 1) && sign
    (s, Mux(s, -a, a))}
  val s_idle :: s_log8 :: s_shift :: s_compute :: s_finish :: Nil = Enum(5)
  val state = RegInit(s_idle)
  val newReq = (state === s_idle) && io.in.fire()
  val (a, b) = (io.in.bits(0), io.in.bits(1))
  val divBy0 = b === 0.U(len.W)
  val shiftReg = Reg(UInt((1 + len * 2).W))
  val (hi,lo) = (shiftReg(len * 2, len),shiftReg(len - 1, 0))//get high 64 bit number and low 64 bit number
  val (aSign, aVal) = abs(a, io.sign)
  val (bSign, bVal) = abs(b, io.sign)
  val aSignReg = RegEnable(aSign, newReq)
  val qSignReg = RegEnable((aSign ^ bSign) && !divBy0, newReq)
  val bReg = RegEnable(bVal, newReq)
  val aValx2Reg = RegEnable(Cat(aVal, "b0".U), newReq)
  val cnt = Counter(len)
  when (newReq) {state := s_log8} .elsewhen (state === s_log8) {
    val canSkipShift = (len.U | Log2(bReg)) - Log2(aValx2Reg)
    cnt.value := Mux(divBy0, 0.U, Mux(canSkipShift >= (len-1).U, (len-1).U, canSkipShift))
    state := s_shift
  } .elsewhen (state === s_shift) {
    shiftReg := aValx2Reg << cnt.value
    state := s_compute
  } .elsewhen (state === s_compute) {
    val x4bReg   = (bReg << 2.U).asUInt
    val x2bReg   = (bReg << 1.U).asUInt
    val x4enough = hi.asUInt >= x4bReg.asUInt
    val sr4 = Cat(Mux(x4enough, hi - x4bReg, hi)(len - 1, 0), lo, x4enough)
    val x2enough = sr4(len * 2, len).asUInt >= x2bReg.asUInt
    val sr2 = Cat(Mux(x2enough, sr4(len * 2, len) - x4bReg, sr4(len * 2, len))(len - 1, 0), sr4(len - 1, 0), x2enough)
    val x1enough = sr2(len * 2, len).asUInt >= bReg.asUInt
    val sr1 = Cat(Mux(x1enough, sr2(len * 2, len) - x4bReg, sr2(len * 2, len))(len - 1, 0), sr2(len - 1, 0), x1enough)
    shiftReg := sr4 ^ sr2 ^ sr1
    cnt.inc()
    cnt.inc()
    cnt.inc()
    when (cnt.value === (len-1).U) { state := s_finish }} .elsewhen (state === s_finish) {state := s_idle}

  val kill = state=/=s_idle && io.flush
  when(kill){
    state := s_idle
  }

  val r = hi(len, 1)
  val resQ = Mux(qSignReg, -lo, lo)
  val resR = Mux(aSignReg, -r, r)
  io.out.bits := Cat(resR, resQ)
  io.out.valid :=  io.in.valid // FIXME: should deal with ready = 0
  io.in.ready := (state === s_idle)
}


class MDUIO extends Bundle {
  val in  = Flipped(DecoupledIO(new FuInPut))
  val out = DecoupledIO(new FuOutPut)
}

class MDU extends Module with Config {
  val io = IO(new MDUIO)
  val multiplier = Module(new WTMultiplier)
  val divider    = Module(new Radix8Divider(XLEN))
  val src1 = Wire(UInt(XLEN.W))
  val src2 = Wire(UInt(XLEN.W))
  val funcOpType = io.in.bits.uop.ctrl.funcOpType
  src1 := io.in.bits.src(0)
  src2 := io.in.bits.src(1)

  io.out.bits.uop := io.in.bits.uop
  io.out.valid :=

  val isDiv = MDUOpType.isDiv(funcOpType)
  val isDivSign = MDUOpType.isDivSign(funcOpType)
  val isW = MDUOpType.isW(funcOpType)
  multiplier.io.src1 := src1
  multiplier.io.src2 := src2
  //multiplier.io.in.valid   := !isDiv && io.in.valid

  val res = LookupTree(funcOpType, List(
    MDUOpType.mul   ->   (src1,src2),//todo:add def to
    MDUOpType.mulh  ->    (src2),
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

class MDU extends Module with Config {
  val io = IO(new MDUIO)

  val (valid, src1, src2, func) = (io.in.valid, io.in.bits.src(0), io.in.bits.src(1), io.in.bits.uop.ctrl.funcOpType)
  def access(valid: Bool, src1: UInt, src2: UInt, func: UInt): UInt = {
    this.valid := valid
    this.src1 := src1
    this.src2 := src2
    this.func := func
    io.out.bits
  }

  val isDiv = MDUOpType.isDiv(func)
  val isDivSign = MDUOpType.isDivSign(func)
  val isW = MDUOpType.isW(func)

  val mul = Module(new Multiplier(XLEN + 1))
  val div = Module(new Divider(XLEN))
  List(mul.io, div.io).map { case x =>
    x.sign := isDivSign
    x.out.ready := io.out.ready
  }

  val signext = SignExt(_: UInt, XLEN+1)
  val zeroext = ZeroExt(_: UInt, XLEN+1)
  val mulInputFuncTable = List(
    MDUOpType.mul    -> (zeroext, zeroext),
    MDUOpType.mulh   -> (signext, signext),
    MDUOpType.mulhsu -> (signext, zeroext),
    MDUOpType.mulhu  -> (zeroext, zeroext)
  )
  mul.io.in.bits(0) := LookupTree(func(1,0), mulInputFuncTable.map(p => (p._1(1,0), p._2._1(src1))))
  mul.io.in.bits(1) := LookupTree(func(1,0), mulInputFuncTable.map(p => (p._1(1,0), p._2._2(src2))))

  val divInputFunc = (x: UInt) => Mux(isW, Mux(isDivSign, SignExt(x(31,0), XLEN), ZeroExt(x(31,0), XLEN)), x)
  div.io.in.bits(0) := divInputFunc(src1)
  div.io.in.bits(1) := divInputFunc(src2)

  mul.io.in.valid := io.in.valid && !isDiv
  div.io.in.valid := io.in.valid && isDiv

  val mulRes = Mux(func(1,0) === MDUOpType.mul(1,0), mul.io.out.bits(XLEN-1,0), mul.io.out.bits(2*XLEN-1,XLEN))
  val divRes = Mux(func(1) /* rem */, div.io.out.bits(2*XLEN-1,XLEN), div.io.out.bits(XLEN-1,0))
  val res = Mux(isDiv, divRes, mulRes)
  io.out.bits := Mux(isW, SignExt(res(31,0),XLEN), res)

  val isDivReg = Mux(io.in.fire(), isDiv, RegNext(isDiv))
  io.in.ready := Mux(isDiv, div.io.in.ready, mul.io.in.ready)
  io.out.valid := Mux(isDivReg, div.io.out.valid, mul.io.out.valid)

  Debug(){printf("[FU-MDU] irv-orv %d %d - %d %d\n", io.in.ready, io.in.valid, io.out.ready, io.out.valid)}

  BoringUtils.addSource(mul.io.out.fire(), "perfCntCondMmulInstr")
}
