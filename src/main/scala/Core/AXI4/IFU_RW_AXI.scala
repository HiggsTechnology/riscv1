package Core.AXI4
import Core.AXI4.AXI4Parameters.{AXI_PROT, AXI_SIZE, addrBits}
import Core.Config.Config
import chisel3._
import utils._
import chisel3.util._

object requireType {
  val r :: w :: Nil = Enum(2)
}

class IFURWIO extends Bundle with Config {
  val ifuin = Flipped(new IFU2RW) //ifu data in
  val ifu2crossbar = new AXI4IO   //RW 2 crossbar AXI4
}
class IFURW extends Module with Config{
  val io : IFURWIO = IO(new IFURWIO)
  val axi4 : AXI4IO = io.ifu2crossbar  // axi4 signal
//  val (ar,r)  = (axi4.ar.bits, axi4.r.bits)
  object RState {
    val idle :: addr :: read :: r_end :: Nil = Enum(4)//-->  0 :: 1 :: 2  three states
    // addr in IFU is PC , and data in IFU is instruction
  }

  val rState : UInt = RegInit(RState.idle)

  //----------------------------状态机转移信号----------------------------
  val ar_hs : Bool = axi4.ar.valid & axi4.ar.ready
  val r_hs  : Bool = axi4.r.valid  & axi4.r.ready
  val r_valid : Bool = axi4.r.valid

  val r_done : Bool = r_hs & axi4.r.bits.last

  //----------------------------初始化-----------------------------------
  val ar_valid  : UInt = WireInit(0.U)
  val ar_id     : UInt = WireInit(0.U)
  val ar_len    : UInt = WireInit(0.U)
  val ar_size   : UInt = WireInit(0.U)
  val ar_burst  : UInt = WireInit(AXI4Parameters.BURST_INCR)
  val ar_lock   : UInt = WireInit(0.U)
  val ar_cache  : UInt = WireInit("b0010".U)
  val ar_qos    : UInt = WireInit(0.U)
  val ar_user   : UInt = WireInit(0.U)
  val ar_prot   : UInt = WireInit(0.U)
  val ar_region : UInt = WireInit(0.U)
  val ar_addr   : UInt = WireInit(0.U)
  val r_ready   : UInt = WireInit(0.U)
  val ifu_ready : UInt = WireInit(0.U)
  val ifu_rdata : UInt = WireInit(0.U)

  val ar_addr_new   = Cat(io.ifuin.pc(XLEN-1, AXI4Parameters.addrAlignedBits), 0.U((AXI4Parameters.addrAlignedBits).W))
  val ar_len_new    = 0.U
  val ar_prot_new   = AXI_PROT.UNPRIVILEGED | AXI_PROT.SECURE | AXI_PROT.INSTRUCTION
  val ar_size_new   = AXI_SIZE.bytes32

  //-------------------------状态机-------------------------------
  when(reset.asBool()) {
    rState := RState.idle
  }.otherwise{
    when (io.ifuin.valid) {
      switch(rState) {
        is(RState.idle)  {               rState := RState.addr}
        is(RState.addr)  {when(ar_hs)   {rState := RState.read}}
        is(RState.read)  {when(r_done)  {rState := RState.r_end}}
        // todo: 检查是否可以合并idle和r_end状态
        is(RState.r_end) {rState := RState.idle} // 等一个周期就恢复idle
      }
    }
  }

  //----------------------------状态机赋值-----------------------------

  switch(rState) {
    is(RState.idle) {
      ar_valid  := io.ifuin.valid
      ar_addr   := ar_addr_new
      ar_len    := ar_len_new
      ar_prot   := ar_prot_new
      ar_size   := ar_size_new

      r_ready   := false.B
      ifu_ready := false.B
      ifu_rdata  := 0.U
    }//fuck zero
    is(RState.addr) {
      ar_valid  := io.ifuin.valid
      ar_addr   := ar_addr_new
      ar_len    := ar_len_new
      ar_prot   := ar_prot_new
      ar_size   := ar_size_new

      r_ready   := true.B
      ifu_ready := false.B
      ifu_rdata := 0.U
      //TODO 考虑burst后ar.bits :=
    }
    is(RState.read) {
      ar_valid  := 0.U
      ar_addr   := ar_addr_new
      ar_len    := ar_len_new
      ar_prot   := ar_prot_new
      ar_size   := ar_size_new

      r_ready   := true.B
      ifu_ready := false.B
      ifu_rdata := 0.U
      //TODO 考虑burst后r.bits :=
    }
    is(RState.r_end) {
      ar_valid  := 0.U
      ar_addr   := ar_addr_new
      ar_len    := ar_len_new
      ar_prot   := ar_prot_new
      ar_size   := ar_size_new

      r_ready   := true.B
      ifu_ready := true.B
      ifu_rdata := axi4.r.bits.data
    }
  }
  // ----------------------AXI连接------------------------
  axi4.ar.valid       := ar_valid
  axi4.ar.bits.id     := ar_id
  axi4.ar.bits.len    := ar_len
  axi4.ar.bits.size   := ar_size
  axi4.ar.bits.burst  := ar_burst
  axi4.ar.bits.lock   := ar_lock
  axi4.ar.bits.cache  := ar_cache
  axi4.ar.bits.qos    := ar_qos
  axi4.ar.bits.user   := ar_user
  axi4.ar.bits.prot   := ar_prot
  axi4.ar.bits.region := ar_region
  axi4.ar.bits.addr   := ar_addr
  axi4.r.ready        := r_ready
  axi4.aw             := DontCare
  axi4.w              := DontCare
  axi4.b              := DontCare
  // ----------------------ifu连接------------------------
  io.ifuin.ready      := ifu_ready
  io.ifuin.rdata      := ifu_rdata

  printf("----------------------------------------------------\n")
  printf("ar_valid: %d, ar_ready: %d, r_valid: %d, r_ready: %d\n", axi4.ar.valid, axi4.ar.ready, axi4.r.valid, axi4.r.ready)
  printf("raddr: %x%x, rdata: %x%x%x%x\n", axi4.ar.bits.addr(63,32), axi4.ar.bits.addr(31,0), axi4.r.bits.data(255, 192), axi4.r.bits.data(191, 128), axi4.r.bits.data(127,64), axi4.r.bits.data(63,0))
  printf("rState: %d\n", rState)

}
