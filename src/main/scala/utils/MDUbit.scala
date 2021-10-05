package utils

import Core.Config
import chisel3._
import chisel3.util._

class MDUbit(val src: UInt) extends Bundle with Config  {
  def abs(a: UInt):  UInt = {
    val s = a(XLEN - 1)
    val temp = Wire(UInt((XLEN+1).W))
    temp := Mux(s, -a, a)
    temp(XLEN-1, 0)
  }                                  //取得绝对值
  def single(x: UInt):UInt = ZeroExt(abs(x),XLEN)
  def half(x: UInt) = ZeroExt(abs(x),32)
}

