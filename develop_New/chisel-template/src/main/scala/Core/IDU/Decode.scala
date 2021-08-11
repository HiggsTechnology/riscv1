package Core.IDU

import chisel3._
import chisel3.util._
import utils._

trait InstrType {
  def InstrN  = "b0000".U
  def InstrI  = "b0100".U
  def InstrR  = "b0101".U
  def InstrS  = "b0010".U
  def InstrB  = "b0001".U
  def InstrU  = "b0110".U
  def InstrJ  = "b0111".U

  def isrfWen(instrType : UInt): Bool = instrType(2)
}

object SrcType {
  def reg = "b0".U
  def pc  = "b1".U
  def imm = "b1".U
  // def isReg(srcType: UInt) = srcType===reg
  // def isPc(srcType: UInt) = srcType===pc
  // def isImm(srcType: UInt) = srcType===imm
  // def isPcImm(srcType: UInt) = srcType(0)
  // def isReg(srcType: UInt) = !srcType(0)
  def apply() = UInt(1.W)
}

object FuncType {
  def alu = "b000".U
  def lsu = "b001".U
  def mdu = "b010".U
  def csr = "b011".U
  def mou = "b100".U
  def bru = "b101".U
  def apply() = UInt(3.W)
}

object FuncOpType {
  def apply() = UInt(7.W)
}

