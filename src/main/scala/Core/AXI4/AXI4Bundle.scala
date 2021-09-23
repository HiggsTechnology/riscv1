package Core.AXI4
import Core.Config.Config
import Core.Config.Config.REG_NUM
import chisel3._
import chisel3.internal.firrtl.Width
import chisel3.util._
import utils.BasicIOType.{InBool, InUInt, OutBool, OutUInt}




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
//  val data    : Vec[UInt] = Output(Vec(AXI4Parameters.dataScale, UInt(AXI4Parameters.dataBits.W)))
  val data  : UInt = OutUInt(dataBits)
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
  val data  : UInt = InUInt(dataBits)
  val last  : Bool = InBool()
  val id    : UInt = InUInt(idBits)
  val user  : UInt = InUInt(AXI4Parameters.userBits)
  override def toPrintable: Printable = p"resp = $resp, id = $id, data = ${Hexadecimal(data)}, last = $last"
}

class AXI4IO(
              val dataBits: Int = AXI4Parameters.dataBits,
              val idBits: Int = AXI4Parameters.idBits
            )
  extends Bundle {
  val aw : DecoupledIO[AXI4BundleA] = Decoupled(new AXI4BundleA(idBits))
  val w : DecoupledIO[AXI4BundleW] = Decoupled(new AXI4BundleW(dataBits))
  val b : DecoupledIO[AXI4BundleB] = Flipped(Decoupled(new AXI4BundleB(idBits)))
  val ar : DecoupledIO[AXI4BundleA] = Decoupled(new AXI4BundleA(idBits))
  val r : DecoupledIO[AXI4BundleR] = Flipped(Decoupled(new AXI4BundleR(dataBits, idBits)))
}


