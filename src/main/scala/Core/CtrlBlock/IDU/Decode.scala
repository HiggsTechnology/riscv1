package Core.CtrlBlock.IDU

import chisel3._
import chisel3.util._
import Core.utils._

trait InstrType {
  def InstrN  = "b0000".U
  def InstrI  = "b0100".U
  def InstrR  = "b0101".U
  def InstrS  = "b0010".U
  def InstrB  = "b0001".U
  def InstrU  = "b0110".U
  def InstrJ  = "b0111".U

  def isrfWen(instrType : UInt): Bool = instrType(2)
  val table : Array[(BitPat, List[UInt])]
}

object SrcType1 {
  def typeSize  = 3
  def reg       = 0.U
  def pc        = 1.U
  def uimm      = 2.U
  def width     = log2Up(typeSize).W
  def uwidth    = UInt(width)
}

object SrcType2 {
  def typeSize  = 2
  def reg       = 0.U
  def imm       = 1.U
  def width     = log2Up(typeSize).W
  def uwidth    = UInt(width)

}


object FuncType {
  def typeSize = 6
  def alu = 0.U
  def lsu = 1.U
  def mdu = 2.U
  def csr = 3.U
  def mou = 4.U
  def bru = 5.U
  def width = log2Up(typeSize).W
  def uwidth = UInt(width)
}

object FuncOpType {
  def width = 7.W
  def uwidth = UInt(width)
}

