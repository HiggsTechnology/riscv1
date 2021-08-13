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
  val table : Array[(BitPat, List[UInt])]
}

// todo: 将两个数据通路的选择信号分开
// todo: 使用width代替apply表示宽度
object SrcType {
  def typeSize = 3
  // src1
  def reg = 0.U
  def pc  = 1.U
  def uimm= 2.U
  // src2
  def imm = 1.U
  def apply() = UInt(log2Up(typeSize).W)
  def width = UInt(log2Up(typeSize).W)
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
  def width = 7.W
}

