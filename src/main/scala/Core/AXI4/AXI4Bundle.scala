package Core.AXI4
import Core.Config
import chisel3._
import chisel3.internal.firrtl.Width
import chisel3.util._

trait AXIParameter extends Config {
  val PAddrBits = 32
  val DataBits : Int = XLEN //64
}


object AXI4Parameters extends AXIParameter {
  // These are all fixed by the AXI4 standard:
  val lenBits   = 8
  val sizeBits  = 3
  val burstBits = 2
  val cacheBits = 4
  val protBits  = 3
  val qosBits   = 4
  val respBits  = 2
  val regionBits = 4

  // These are not fixed:
  val idBits    = 3
  val LSUidBits    = 4
  val addrBits : Int = PAddrBits
  val dataBits : Int = DataBits
  val strbBits : Int = dataBits / 8
  val userBits  = 1

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
    val bytes1::bytes2::bytes4::bytes8::bytes16::bytes32::bytes64::bytes128::Nil = Enum(size)
    def width: Width = log2Up(size).W
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

    def isPrivileged(data: UInt) : Bool = data === PRIVILEGED
    def isNonSecure(data: UInt) : Bool = data === NON_SECURE
    def isInstruction(data: UInt) : Bool = data === INSTRUCTION
    def width : Width = 3.W
  }
}

object OutUInt {
  def apply(w: Int) : UInt = Output(UInt(w.W))
  def apply(w: Width) : UInt = Output(UInt(w))
}

object InUInt {
  def apply(w: Int) : UInt = Input(UInt(w.W))
  def apply(w: Width) : UInt = Input(UInt(w))
}

object OutBool {
  def apply() : Bool = Output(Bool())
}

object InBool {
  def apply() : Bool = Input(Bool())
}



// AXI4-full

class AXI4BundleA(val idBits: Int) extends Bundle {
  val addr    : UInt = OutUInt(AXI4Parameters.addrBits)
  val prot    : UInt = OutUInt(AXI4Parameters.protBits)
  val id      : UInt = OutUInt(idBits)
  val user    : UInt = OutUInt(AXI4Parameters.userBits)
  val len     : UInt = OutUInt(AXI4Parameters.lenBits)           // number of beats - 1
  val size    : UInt = OutUInt(AXI4Parameters.sizeBits)          // bytes in beat = 2^size
  val burst   : UInt = OutUInt(AXI4Parameters.burstBits)
  val lock    : Bool = OutBool()
  val cache   : UInt = OutUInt(AXI4Parameters.cacheBits)
  val qos     : UInt = OutUInt(AXI4Parameters.qosBits)           // 0=no QoS, bigger = higher priority
  val region  : UInt = OutUInt(AXI4Parameters.regionBits)        // optional
  override def toPrintable: Printable = p"addr = 0x${Hexadecimal(addr)}, id = $id, len = $len, size = $size"
}

// id ... removed in AXI4
class AXI4BundleW(val dataBits: Int) extends Bundle {
  val data  : UInt = OutUInt(AXI4Parameters.dataBits)
  val last  : Bool = OutBool()
  val user  : UInt = OutUInt(AXI4Parameters.userBits)
  val strb  : UInt = OutUInt(AXI4Parameters.strbBits)
  override def toPrintable: Printable = p"data = ${Hexadecimal(data)}, wmask = 0x$strb, last = $last"
}
class AXI4BundleB(val idBits: Int) extends Bundle {
  val resp : UInt = InUInt(AXI4Parameters.respBits)
  val id : UInt = InUInt(AXI4Parameters.idBits)
  val user : UInt = InUInt(AXI4Parameters.userBits)
  override def toPrintable: Printable = p"resp = $resp, id = $id"
}
class AXI4BundleR(val dataBits: Int, val idBits: Int) extends Bundle {
  val resp  : UInt = InUInt(AXI4Parameters.respBits)
  val data  : UInt = InUInt(AXI4Parameters.dataBits)
  val last  : Bool = InBool()
  val id    : UInt = InUInt(AXI4Parameters.idBits)
  val user  : UInt = InUInt(AXI4Parameters.userBits)
  override def toPrintable: Printable = p"resp = $resp, id = $id, data = ${Hexadecimal(data)}, last = $last"
}

class AXI4IO(val dataBits: Int = AXI4Parameters.dataBits, val idBits: Int = AXI4Parameters.idBits)
  extends Bundle {
  val aw : DecoupledIO[AXI4BundleA] = Decoupled(new AXI4BundleA(idBits))
  val w : DecoupledIO[AXI4BundleW] = Decoupled(new AXI4BundleW(dataBits))
  val b : DecoupledIO[AXI4BundleB] = Flipped(Decoupled(new AXI4BundleB(idBits)))
  val ar : DecoupledIO[AXI4BundleA] = Decoupled(new AXI4BundleA(idBits))
  val r : DecoupledIO[AXI4BundleR] = Flipped(Decoupled(new AXI4BundleR(dataBits, idBits)))
}


