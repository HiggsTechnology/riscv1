package Core.AXI4
import Core.AXI4.AXI4Parameters.{AXI_PROT, AXI_SIZE}
import Core.Config.Config
import chisel3._
import chisel3.util._
import utils._

object requireType {
  val r :: w :: Nil = Enum(2)
}

class IFURWIO extends Bundle with Config {
  val ifuin : IFU2RW = Flipped(new IFU2RW) //ifu data in
  val to_crossbar : AXI4IO = new AXI4IO   //RW 2 crossbar AXI4
}

class IFURW extends Module with Config{
  val io : IFURWIO = IO(new IFURWIO)
  val axi4 : AXI4IO = io.to_crossbar  // axi4 signal
  object RState {
    val idle :: ar_valid :: ar_trans :: r_trans :: r_done :: Nil = Enum(5)//-->  0 :: 1 :: 2  three states
  }

  private val rState : UInt = RegInit(RState.idle)

  //----------------------------状态机转移信号----------------------------
  private val ifu_valid : Bool = io.ifuin.valid
  private val ar_ready : Bool = axi4.ar.ready
  private val ar_hs : Bool = axi4.ar.valid & axi4.ar.ready
  private val r_hs  : Bool = axi4.r.valid  & axi4.r.ready
  private val r_valid : Bool = axi4.r.valid
  private val r_done : Bool = axi4.r.bits.last

  //----------------------------初始化-----------------------------------
  val ar_valid  : UInt = WireInit(0.U)
  private val ar_id     : UInt = WireInit(0.U)
  private val ar_len    : UInt = WireInit(0.U)
  private val ar_size   : UInt = WireInit(0.U)
  private val ar_burst  : UInt = WireInit(AXI4Parameters.BURST_INCR)
  private val ar_lock   : UInt = WireInit(0.U)
  private val ar_cache  : UInt = WireInit("b0010".U)
  private val ar_qos    : UInt = WireInit(0.U)
  private val ar_user   : UInt = WireInit(0.U)
  private val ar_prot   : UInt = WireInit(0.U)
  private val ar_region : UInt = WireInit(0.U)
  private val ar_addr   : UInt = WireInit(0.U)
  private val r_ready   : UInt = WireInit(0.U)
  private val ifu_ready : UInt = WireInit(0.U)
  private val ifu_rdata : UInt = WireInit(0.U)

  private val ar_addr_new   = Cat(io.ifuin.pc(XLEN-1, AXI4Parameters.addrAlignedBits), 0.U(AXI4Parameters.addrAlignedBits.W))
  private val ar_len_new    = 0.U
  private val ar_prot_new   = AXI_PROT.UNPRIVILEGED | AXI_PROT.SECURE | AXI_PROT.INSTRUCTION
  private val ar_size_new   = AXI_SIZE.bytes32

  //-------------------------状态机-------------------------------
  when(reset.asBool()) {
    rState := RState.idle
  }.otherwise{
    switch(rState) {
      is(RState.idle)     {when(ifu_valid)    {rState := RState.ar_valid}}
      is(RState.ar_valid) {when(ar_ready)     {rState := RState.ar_trans}}
      is(RState.ar_trans) {when(r_valid)      {rState := RState.r_trans}}
      is(RState.r_trans)  {when(r_done)       {rState := RState.r_done}}
      // todo: 检查是否可以合并idle和r_end状态
      is(RState.r_done)   { rState := RState.idle }
    }
  }

  //----------------------------状态机赋值-----------------------------

  switch(rState) {
    is(RState.idle) {
      ar_valid  := false.B
      ar_addr   := ar_addr_new
      ar_len    := ar_len_new
      ar_prot   := ar_prot_new
      ar_size   := ar_size_new

      r_ready   := false.B
      ifu_ready := false.B
      ifu_rdata  := 0.U
    }//fuck zero
    is(RState.ar_valid) {
      ar_valid  := true.B
      ar_addr   := ar_addr_new
      ar_len    := ar_len_new
      ar_prot   := ar_prot_new
      ar_size   := ar_size_new

      r_ready   := true.B
      ifu_ready := false.B
      ifu_rdata := 0.U
      //TODO 考虑burst后ar.bits :=
    }
    is(RState.ar_trans) {
      ar_valid  := false.B
      ar_addr   := ar_addr_new
      ar_len    := ar_len_new
      ar_prot   := ar_prot_new
      ar_size   := ar_size_new

      r_ready   := true.B
      ifu_ready := false.B
      ifu_rdata := 0.U
    }
    is(RState.r_trans) {
      ar_valid  := false.B
      ar_addr   := ar_addr_new
      ar_len    := ar_len_new
      ar_prot   := ar_prot_new
      ar_size   := ar_size_new

      r_ready   := true.B
      ifu_ready := false.B
      ifu_rdata := 0.U
      //TODO 考虑burst后r.bits :=
    }
    is(RState.r_done) {
      ar_valid  := false.B
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

  when (rState =/= RState.idle){
    printf("----------------------ifu axi----------------------\n")
    printf("ar_valid: %d, ar_ready: %d, r_valid: %d, r_ready: %d\n", axi4.ar.valid, axi4.ar.ready, axi4.r.valid, axi4.r.ready)
    printf("read_addr: %x, raddr: %x%x, rdata: %x%x%x%x\n", io.ifuin.pc(31,0), axi4.ar.bits.addr(63,32), axi4.ar.bits.addr(31,0), axi4.r.bits.data(255, 192), axi4.r.bits.data(191, 128), axi4.r.bits.data(127,64), axi4.r.bits.data(63,0))
    printf("rState: %d\n", rState)
  }


}
