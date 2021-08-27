package Core.EXU
import chisel3._
import chisel3.util._
import Core.AXI4.{AXI4, AXI4LSU, AXI4Parameters}
import Core.Config.Config
import utils._

object LSUOpType {
  def lb   = "b0000000".U
  def lh   = "b0000001".U
  def lw   = "b0000010".U
  def ld   = "b0000011".U
  def lbu  = "b0000100".U
  def lhu  = "b0000101".U
  def lwu  = "b0000110".U
  def sb   = "b0001000".U
  def sh   = "b0001001".U
  def sw   = "b0001010".U
  def sd   = "b0001011".U

  def lr   = "b0100000".U
  def sc   = "b0100001".U

  def isStore(func: UInt): Bool = func(3)
  def isLoad(func: UInt): Bool = !isStore(func)
  def isLR(func: UInt): Bool = func === lr
  def isSC(func: UInt): Bool = func === sc
  def needMemRead(func: UInt): Bool  = isLoad(func) || isLR(func)
  def needMemWrite(func: UInt): Bool = isStore(func) || isSC(func)
}

class LSUIO extends Bundle with Config {
  val valid = Input(Bool())
  val in    = Flipped(new CfCtrl)
  val out   = new LSU_OUTIO
  val lsuaxi = new AXI4LSU
}

class LSU extends Module with Config {

  def genWmask(addr: UInt, sizeEncode: UInt): UInt = {
    LookupTree(sizeEncode, List(
      "b00".U -> 0x1.U, //0001 << addr(2:0)
      "b01".U -> 0x3.U, //0011
      "b10".U -> 0xf.U, //1111
      "b11".U -> 0xff.U //11111111
    )) << addr(2, 0)
  }

  def genWdata(data: UInt, sizeEncode: UInt): UInt = {
    LookupTree(sizeEncode, List(
      "b00".U -> ZeroExt(data(7, 0), XLEN),
      "b01".U -> ZeroExt(data(15, 0), XLEN),
      "b10".U -> ZeroExt(data(31, 0), XLEN),
      "b11".U -> ZeroExt(data(63, 0), XLEN)
    ))
  }

  val io = IO(new LSUIO)
  val addr = io.in.data.src1 + io.in.data.imm
  val storedata = io.in.data.src2
  val isStore : Bool = LSUOpType.isStore(io.in.ctrl.funcOpType)
  val isLoad = LSUOpType.isLoad(io.in.ctrl.funcOpType)
//------------------------------------------------------------
  val axi : AXI4LSU = io.lsuaxi
  val (ar, aw, w, r, b) = (axi.ar.bits, axi.aw.bits, axi.w.bits, axi.r.bits, axi.b.bits)
  val axi_reset = 0.U
  val axi_ar = 1.U
  val axi_r = 2.U
  val axi_aw = 3.U
  val axi_w = 4.U
  val axi_b = 5.U
  val inflight_type = RegInit(axi_reset)
  private def setState(axi_type: UInt) = {
    inflight_type := axi_type;
  }
  private def resetState() = {
    inflight_type := axi_reset
  }
  private def isState(state: UInt) : Bool = {
    inflight_type === state
  }
//  private def isInflight() : Bool = {
//    !isState(axi_reset)
//  }
  val ar_hs = axi.ar.valid && axi.ar.ready
  val r_hs = axi.r.valid && axi.r.ready
  val b_hs = axi.b.valid && axi.r.ready
  val aw_hs = axi.aw.valid && axi.aw.ready
  val w_hs = axi.w.valid && axi.w.ready
//-------------------------set state---------------------------------
  when(isState((axi_reset)) && isLoad){ //isLoad可以判断的时候， 代表IDU解码完成 也就是axi.ar.valid 准备发送地址
    setState(axi_ar)
    axi.ar.valid := true.B
    axi.aw.valid := false.B
    axi.w.valid  := false.B
  }.elsewhen(isState((axi_reset)) && isStore){ //isStore 同理
    setState(axi_aw)
    axi.ar.valid := false.B
    axi.aw.valid := true.B
    axi.w.valid  := true.B
  }.otherwise{
    axi.ar.valid := false.B
    axi.aw.valid := false.B
    axi.w.valid  := false.B
  }
//-------------------------load--------------------------------
  val rdata = RegInit(0.U(ADDR_WIDTH))
  def LineBeats = 8

  axi.ar.bits.id    := 2.U
  axi.ar.bits.len   := 0.U
  axi.ar.bits.size  := 1.U(3.W)
  axi.ar.bits.burst := AXI4Parameters.BURST_WRAP
  axi.ar.bits.lock  := false.B
  axi.ar.bits.cache := 0.U
  axi.ar.bits.qos   := 0.U
  axi.ar.bits.user  := 0.U
  axi.ar.valid      := false.B
  axi.ar.bits.prot  := 0.U
  axi.ar.bits.region := 0.U


  val ar_addr = RegInit(0.U(ADDR_WIDTH))
  axi.ar.bits.addr := ar_addr
  when(ar_hs){
    ar_addr  := addr
    setState(axi_r)
  }
  axi.r.ready := isState(axi_r)
  when(r_hs){
    rdata := axi.r.bits.data
    setState(axi_reset)
  }



  //-------------------------store--------------------------------
  val size = io.in.ctrl.funcOpType(1,0)
  axi.aw.bits.id    := 3.U
  axi.aw.bits.len   := 0.U
  axi.aw.bits.size  := 1.U(3.W)
  axi.aw.bits.burst := AXI4Parameters.BURST_WRAP
  axi.aw.bits.lock  := false.B
  axi.aw.bits.cache := 0.U
  axi.aw.bits.qos   := 0.U
  axi.aw.bits.user  := 0.U
  axi.aw.bits.prot  := 0.U
  axi.aw.bits.region := 0.U
  val aw_addr = RegInit(0.U(ADDR_WIDTH))
  axi.aw.bits.addr := aw_addr
  when(aw_hs){
    aw_addr  := addr
    setState(axi_w)
  }
  val w_strb = RegInit(0.U(8.W))
  val w_data = RegInit(0.U(ADDR_WIDTH))
  axi.w.bits.strb  := w_strb
  axi.w.bits.last  := true.B
  axi.w.bits.data := w_data
  when(w_hs) {
    w_data := genWdata(storedata, size)
    w_strb := genWmask(addr, size)
    setState(axi_b)
  }
  axi.b.ready := isState(axi_b)
  when(b_hs){
    setState(axi_reset)
    axi.aw.valid := false.B
    axi.w.valid  := false.B
  }
  //---------------------------------------------------------

  val rdataSel = LookupTree(addr(2, 0), List(
    "b000".U -> rdata(63, 0),
    "b001".U -> rdata(63, 8),
    "b010".U -> rdata(63, 16),
    "b011".U -> rdata(63, 24),
    "b100".U -> rdata(63, 32),
    "b101".U -> rdata(63, 40),
    "b110".U -> rdata(63, 48),
    "b111".U -> rdata(63, 56)
  ))

  io.out.rdata := LookupTree(io.in.ctrl.funcOpType, List(
    LSUOpType.lb   -> SignExt(rdataSel(7, 0) , XLEN),
    LSUOpType.lh   -> SignExt(rdataSel(15, 0), XLEN),
    LSUOpType.lw   -> SignExt(rdataSel(31, 0), XLEN),
    LSUOpType.ld   -> SignExt(rdataSel(63, 0), XLEN),
    LSUOpType.lbu  -> ZeroExt(rdataSel(7, 0) , XLEN),
    LSUOpType.lhu  -> ZeroExt(rdataSel(15, 0), XLEN),
    LSUOpType.lwu  -> ZeroExt(rdataSel(31, 0), XLEN)
  ))


}
