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

class MulIO(val len: Int) extends Bundle {
  val in = Flipped(DecoupledIO(Vec(2, Output(UInt(len.W)))))
  val flush  = Input(Bool())
  val sign = Input(Bool())
  val out = ValidIO(Output(UInt((len * 2).W)))
}

class DivIO(val len: Int) extends Bundle {
  val in = Flipped(DecoupledIO(Vec(2, Output(UInt(len.W)))))
  val flush  = Input(Bool())
  val sign = Input(Bool())
  val out = ValidIO(Output(UInt((len * 2).W)))
  val DivIdle = Output(Bool())
}

class WTMultiplier extends Module {
  val io = IO(new MulIO(64))

  def abs(a: UInt, sign: Bool): (Bool, UInt) = {val s = a(64 - 1) && sign
    (s, Mux(s, -a, a))}
  val (a, b) = (io.in.bits(0), io.in.bits(1))
  val (aSign, aVal) = abs(a, io.sign)
  val (bSign, bVal) = abs(b, io.sign)


  //level 0
  val branch0 = Wire(Vec(64, UInt(64.W)))
  for(i <- 0 until 64){
    branch0(i) := Mux(aVal(i),bVal,0.U)
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
  val pip = RegEnable(branch3, io.in.valid)
  val qSign = RegEnable((aSign ^ bSign), io.in.valid)
  //level4
  val branch4 = Wire(Vec(4, UInt(80.W)))
  for(i <- 0 until 4){
    branch4(i) := Cat(pip(2*i+1),0.U(8.W)) + Cat(0.U(8.W),pip(2*i))
  }
  //level5
  val branch5 = Wire(Vec(2, UInt(96.W)))
  for(i <- 0 until 2){
    branch5(i) := Cat(branch4(2*i+1),0.U(16.W)) + Cat(0.U(16.W),branch4(2*i))
  }
  //level6
  val res = Cat(branch5(1),0.U(32.W)) + Cat(0.U(32.W),branch5(0))
  io.out.bits :=  Mux(qSign, -res, res)
  io.out.valid := io.in.valid && !io.flush
  io.in.ready := true.B
}


class Radix8Divider(len: Int = 64) extends Module {
  val io = IO(new DivIO(len))

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
    cnt.value := Mux(divBy0, 0.U, Mux(canSkipShift >= (len-3).U, (len-3).U, canSkipShift))
    state := s_shift
  } .elsewhen (state === s_shift) {
    shiftReg := aValx2Reg << cnt.value
    state := s_compute
  } .elsewhen (state === s_compute) {
    val x4bReg   = (bReg << 2.U).asUInt
    val x2bReg   = (bReg << 1.U).asUInt
    val x4enough = hi.asUInt >= x4bReg.asUInt
    val sr4 = Cat(Mux(x4enough, hi - x4bReg, hi)(len - 1, 0), lo, x4enough)
    cnt.inc()
    when (cnt.value === (len-3).U) { state := s_finish }.elsewhen(cnt.value < (len-3).U)

    val x2enough = sr4(len * 2, len).asUInt >= x2bReg.asUInt
    val sr2 = Cat(Mux(x2enough, sr4(len * 2, len) - x4bReg, sr4(len * 2, len))(len - 1, 0), sr4(len - 1, 0), x2enough)
    cnt.inc()

    val x1enough = sr2(len * 2, len).asUInt >= bReg.asUInt
    val sr1 = Cat(Mux(x1enough, sr2(len * 2, len) - x4bReg, sr2(len * 2, len))(len - 1, 0), sr2(len - 1, 0), x1enough)
    cnt.inc()

    shiftReg := sr4 ^ sr2 ^ sr1

    when (cnt.value >= (len-3).U) { state := s_finish }} .elsewhen (state === s_finish) {state := s_idle}

  val kill = state=/=s_idle && io.flush
  when(kill){
    state := s_idle
  }

  val r = hi(len, 1)
  val resQ = Mux(qSignReg, -lo, lo)
  val resR = Mux(aSignReg, -r, r)
  io.out.bits := Cat(resR, resQ)
  io.out.valid :=  io.in.valid && (state === s_finish)
  io.in.ready := (state === s_idle)
}


class MDUIO extends Bundle {
  val in  = Flipped(ValidIO(new FuInPut))
  val out = ValidIO(new FuOutPut)
  val DivIdle = Output(Bool())
}

class MDU extends Module with Config {
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

