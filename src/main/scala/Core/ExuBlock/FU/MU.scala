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

class MUIO extends Bundle {
  val in  = Flipped(ValidIO(new FuInPut))
  val out = ValidIO(new FuOutPut)
  val flush = Input(Bool())
}

class MulIO(val len: Int) extends Bundle {
  val in = Flipped(ValidIO(Vec(2, Output(UInt(len.W)))))
  val flush  = Input(Bool())
  val sign = Input(Bool())
  val out = ValidIO(Output(UInt((len * 2).W)))
}

class WTMultiplier extends Module {
  val io = IO(new MulIO(64))

  val (a, b) = (io.in.bits(0), io.in.bits(1))



  //level 0
  val branch0 = Wire(Vec(64, UInt(64.W)))
  for(i <- 0 until 64){
    branch0(i) := Mux(a(i),b,0.U)
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
  io.out.bits :=  res
  io.out.valid := io.in.valid && !io.flush
}

class MU extends Module with Config {
  val io   = IO(new MUIO)
  val mul  = Module(new WTMultiplier)

}
