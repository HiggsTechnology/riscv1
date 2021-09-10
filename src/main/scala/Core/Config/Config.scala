package Core.Config

import chisel3.internal.firrtl.Width
import chisel3.util.log2Up
import chisel3._
import scala.math.{BigInt, pow}

trait Config {
  def PC_START = 0x80000000L

  def XLEN : Int = 64
  def INST_WIDTH: Width = 32.W
  def ADDR_WIDTH : Width = XLEN.W
  def DATA_WIDTH : Width = XLEN.W

  def REG_NUM : Int = 32

  def REG_ADDR_WIDTH : Width = log2Up(REG_NUM).W

  // CSR
  // Machine
  def MXL = 2
  def MXLEN : Int = pow(2, MXL + 4).toInt
  def SXLEN : Int = MXLEN
  def UXLEN : Int = SXLEN
  class ISAExt(string : String) {
    val value : Int = toInt(string)
    def support(ch : Char) : Boolean = {
      (value & ch.toInt) != 0
    }
    private def toInt(string : String) : Int = {
      var value = 0
      for (ch <- string) {
        require(ch.isLetter)
        value |= (ch.toUpper - 'A')
      }
      value
    }

    def toInt : Int = value
  }

  def ISAEXT = new ISAExt("I")

  def CSR_ADDR_LEN = 12

  def CSR_ADDR_W : Width = CSR_ADDR_LEN.W

  def VendorID = 0

  def ArchitectureID = 0

  def ImplementationID = 0

  def HardwareThreadID = 0

  def MISA : BigInt = {
    BigInt(MXL) << (MXLEN - 2) | BigInt(ISAEXT.toInt)
  }

  def nALU : Int = 2

  def nBRU : Int = 1

  def nCSR : Int = 1

  def nLSU : Int = 1

  def ExuNum :Int = 5

  def rsSize :Int = 2

  def PhyRegIdxWidth : Int = 7

  def OrderQueueSize : Int = 32

  def DispatchQueueSize : Int = 8
}

object SrcState {
  def busy    = "b0".U
  def rdy     = "b1".U
  // def specRdy = "b10".U // speculative ready, for future use
  def apply() = UInt(1.W)
}

/** 可以import *.Config._ 在文件的全局导入 */
object Config extends Config