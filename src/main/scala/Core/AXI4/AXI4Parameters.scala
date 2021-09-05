package Core.AXI4

import Core.Config.Config
import chisel3._
import chisel3.internal.firrtl.Width
import chisel3.util.{Enum, log2Up}

trait AXIParameter extends Config {
  val PAddrBits = 32
  val DataBits : Int = 256   // 256bits
//  val DataScale : Int = 4     // Vec[1]
}

object AXI4Parameters extends AXIParameter {
  // These are all fixed by the AXI4 standard:
  val lenBits = 8
  val sizeBits = 3
  val burstBits = 2
  val cacheBits = 4
  val protBits = 3
  val qosBits = 4
  val respBits = 2
  val regionBits = 4

  // These are not fixed:
  val idBits = 3
  val LSUidBits = 4
  val addrBits : Int = 64
  val dataBits : Int = DataBits
  val dataBytes: Int = dataBits / 8
//  val dataScale: Int = DataScale
  val strbBits : Int = dataBits / 8
  val addrAlignedBits: Int = log2Up(dataBytes)
  val userBits = 1

  def CACHE_RALLOCATE : UInt = 8.U(cacheBits.W)

  def CACHE_WALLOCATE : UInt = 4.U(cacheBits.W)

  def CACHE_MODIFIABLE : UInt = 2.U(cacheBits.W)

  def CACHE_BUFFERABLE : UInt = 1.U(cacheBits.W)

  def PROT_PRIVILEDGED : UInt = 1.U(protBits.W)

  def PROT_INSECURE : UInt = 2.U(protBits.W)

  def PROT_INSTRUCTION : UInt = 4.U(protBits.W)

  def BURST_FIXED : UInt = 0.U(burstBits.W)

  def BURST_INCR : UInt = 1.U(burstBits.W)

  def BURST_WRAP : UInt = 2.U(burstBits.W)

  def RESP_OKAY : UInt = 0.U(respBits.W)

  def RESP_EXOKAY : UInt = 1.U(respBits.W)

  def RESP_SLVERR : UInt = 2.U(respBits.W)

  def RESP_DECERR : UInt = 3.U(respBits.W)

  object AXI_SIZE {
    def size = 8

    val bytes1 :: bytes2 :: bytes4 :: bytes8 :: bytes16 :: bytes32 :: bytes64 :: bytes128 :: Nil = Enum(size)

    def width : Width = log2Up(size).W
  }

  /** Access permissions <br>
   * PRIVILEGED 与 UNPRIVILEGED：两种特权级，特权和非特权，可以与CPU的特权级做映射 <br>
   * NON_SECURE 与 SECURE，ARM Security Extensions 使用 <br>
   * INSTRUCTION 与 DATA，对指令和数据做了区分，表明这是数据，如果指令和数据混合，手册建议置为数据 <br>
   * */
  object AXI_PROT {
    private def default : UInt = "b000".U(width)

    def PRIVILEGED : UInt = "b001".U(width)

    def UNPRIVILEGED : UInt = default

    def NON_SECURE : UInt = "b010".U(width)

    def SECURE : UInt = default

    def INSTRUCTION : UInt = "b100".U(width)

    def DATA : UInt = default

    def isPrivileged(data : UInt) : Bool = data === PRIVILEGED

    def isNonSecure(data : UInt) : Bool = data === NON_SECURE

    def isInstruction(data : UInt) : Bool = data === INSTRUCTION

    def width : Width = 3.W
  }
}
