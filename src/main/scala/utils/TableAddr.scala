package utils

import Core.Config
import chisel3._
import chisel3.util._

class TableAddr(val idxBits: Int) extends Bundle with Config {
  val padLen:Int = 2

  def tagBits = VAddrBits - padLen - idxBits

  //val res = UInt((AddrBits - VAddrBits).W)
  val tag = UInt(tagBits.W)
  val idx = UInt(idxBits.W)
  val pad = UInt(padLen.W)

  def fromUInt(x: UInt) = x.asTypeOf(UInt(VAddrBits.W)).asTypeOf(this)
  def getTag(x: UInt) = fromUInt(x).tag
  def getIdx(x: UInt) = fromUInt(x).idx
}
