package Core.AXI4

import chisel3._
import chisel3.util._
import utils.{InBool, InUInt, OutBool, OutUInt}

class ysyxAXI4IO(
              val dataBits: Int = AXI4Parameters.soc_DataBits,
              val idBits: Int = AXI4Parameters.idBits
            )
  extends Bundle {
  val arready   = InBool()
  val arvalid   = OutBool()
  val araddr    = OutUInt(AXI4Parameters.addrBits)
  val arid      = OutUInt(idBits)
  val arlen     = OutUInt(AXI4Parameters.lenBits)
  val arsize    = OutUInt(AXI4Parameters.sizeBits)
  val arburst   = OutUInt(AXI4Parameters.burstBits)

  val rready   = OutBool()
  val rvalid   = InBool()
  val rresp  : UInt = InUInt(AXI4Parameters.respBits)
  val rdata  : UInt = InUInt(dataBits)
  val rlast  : Bool = InBool()
  val rid    : UInt = InUInt(idBits)

  val awready   = InBool()
  val awvalid   = OutBool()
  val awaddr    = OutUInt(AXI4Parameters.addrBits)
  val awid      = OutUInt(idBits)
  val awlen     = OutUInt(AXI4Parameters.lenBits)
  val awsize    = OutUInt(AXI4Parameters.sizeBits)
  val awburst   = OutUInt(AXI4Parameters.burstBits)

  val wready        = InBool()
  val wvalid        = OutBool()
  val wdata  : UInt = OutUInt(dataBits)
  val wlast  : Bool = OutBool()
  val wstrb  : UInt = OutUInt(dataBits/8)

  val bready       = OutBool()
  val bvalid       = InBool()
  val bresp : UInt = InUInt(AXI4Parameters.respBits)
  val bid   : UInt = InUInt(AXI4Parameters.idBits)

}

