package Core

import chisel3._
import chisel3.internal.firrtl.Width
import chisel3.util.log2Up

import scala.math.{BigInt, pow}

trait Config {
  def PC_START_sim = 0x80000000L
  def PC_START_soc = 0x30000000L

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
  def IBufSize  = 8

  def nALU: Int = 2

  def nBRU: Int = 1

  def nCSR: Int = 1

  def nLSU: Int = 1

 // def nMDU : Int = 0//1 MU and 1 DU

  def ExuNum :Int = nALU + nBRU + nCSR + nLSU

  def RSNum :Int = ExuNum - 1// nLSU = 1 conect to 1 LSQ regarded as RS,BRU and CSR connect to 1 RS,thus tot is 6

  def rsSize: Int =4

  def PhyRegIdxWidth : Int = 7

  def NRPhyRegs : Int = 48

  def OrderQueueSize : Int = 16

  def DispatchQueueSize : Int = 4

  def robSize : Int = 12

  def lsqSize : Int = 7

  def RasSize = 16
  def VAddrBits = 64
  def GPHT_Size = 1024
  def ghrBits = log2Up(GPHT_Size)
  def BtbSize = 32
  def BtbWays = 4
  def btbRows = BtbSize/BtbWays

  object MMIOConfig {
    /**
     * address mapping<br/>
     * clint始终在CPU内部<br/>
     * uart仿真时在SimTop发出，接上SoC后与memory统一处理<br/>
     */
    val addrMap : Map[String, ((Long, Long), Boolean)] = Map(
      // name     ->  ((addr from  , to         ), ordered)
      "clint"     ->  ((0x02000000L, 0x0200ffffL), true   ), // "clint"
      "uart16550" ->  ((0x10000000L, 0x10000fffL), true   ), // "uart16550"
      "spi"       ->  ((0x10001000L, 0x10001fffL), true   ), // "spi"
      "spi-xip"   ->  ((0x30000000L, 0x3fffffffL), true   ), // "spi-xip"
      "chiplink"  ->  ((0x40000000L, 0x7fffffffL), true   ), // "chiplink"
      "mem"       ->  ((0x80000000L, 0xffffffffL), false  ), // "dcache/mem"
      "outside_sim"   ->  ((0x10002000L, 0x7fffffffL), false  ),  // "全部外设，地址和上述部分重叠，其实大于0x10000000L都是核外的地址空间" 0x10002000L
      "outside_soc"   ->  ((0x10000000L, 0x7fffffffL), false  )
    )
    val simAddrMap = List(
      addrMap("mem"),
      addrMap("clint"),
      addrMap("uart16550"),
      addrMap("outside_sim")
    )
    val realAddrMap = List(
      addrMap("mem"),
      addrMap("clint"),
      addrMap("outside_soc")
    )
  }

  object TrapConfig {
    def InterruptVecWidth = 12
    def ExceptionVecWidth = 16
  }
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