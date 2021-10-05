package Core

import chisel3._
import chisel3.internal.firrtl.Width
import chisel3.util.log2Up

import scala.math.{BigInt, pow}

trait Config {
  def PC_START = 0x80000000L

  def XLEN : Int = 64
  def INST_WIDTH: Width = 32.W
  def ADDR_WIDTH : Width = XLEN.W
  def DATA_WIDTH : Width = XLEN.W
  def FETCH_WIDTH : Int = 2
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
  def IBufSize  = 16

  def nALU : Int = 2

  def nBRU : Int = 1

  def nCSR : Int = 1

  def nLSU : Int = 2

  def nMDU : Int = 2//1 MU and 1 DU

  def ExuNum :Int = nALU + nBRU + nCSR + nLSU + nMDU

  def RSNum :Int = ExuNum - 2//LSQ regarded as RS, 2 LSU connect to 1 LSQ, BRU and CSR connect to 1 RS

  def rsSize :Int = 8

  def PhyRegIdxWidth : Int = 7

  def NRPhyRegs : Int = 128

  def OrderQueueSize : Int = 16

  def DispatchQueueSize : Int = 8

  def robSize : Int = 16

  def lsqSize : Int = 16

  def RasSize = 32
  def VAddrBits = 64
  def GPHT_Size = 1024
  def ghrBits = log2Up(GPHT_Size)
  def BtbSize = 256
  def BtbWays = 4
  def btbRows = BtbSize/BtbWays

}

object RSType {
  def typeSize  = 6
  def jumprs: UInt = 0.U//csr,bru -> jumprs
  def alurs: UInt  = 1.U//alu1rs, alu2rs
  def alurs2: UInt = 2.U
  def lsurs: UInt  = 3.U
  def murs: UInt   = 4.U
  def durs: UInt   = 5.U
  def width     = log2Up(typeSize).W
  def uwidth    = UInt(width)
}

object SrcState {
  def busy    = "b0".U
  def rdy     = "b1".U
  // def specRdy = "b10".U // speculative ready, for future use
  def apply() = UInt(1.W)
}

/** 可以import *.Config._ 在文件的全局导入 */
object Config extends Config