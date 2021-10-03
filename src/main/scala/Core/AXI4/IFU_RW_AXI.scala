package Core.AXI4
import Core.{Config, IFU2MemIO}
import chisel3._
import utils._
import chisel3.util._

object requireType {
  val r :: w :: Nil = Enum(2)
}

class IFURWIO extends Bundle with Config {
  val ifuin = Flipped(new IFU2MemIO) //ifu data in
  val ifu2crossbar = new AXI4IO //RW 2 crossbar AXI4
}
class IFURW extends Module with Config{
  val io : IFURWIO = IO(new IFURWIO)
  val axi4 : AXI4IO = io.ifu2crossbar  // axi4 signal
  val (ar,r)  = (axi4.ar.bits, axi4.r.bits)
  object RState {
    val idle :: ar_end :: r_begin :: r_end :: Nil = Enum(4)//-->  0 :: 1 :: 2  three states
    // addr in IFU is PC , and data in IFU is instruction
  }

  val rState : UInt = RegInit(RState.idle)
  //  val wState = RegInit(WState.idle)

  //----------------------------状态机转移信号----------------------------
  val ar_hs : Bool = axi4.ar.valid & axi4.ar.ready
  val r_hs : Bool = axi4.r.valid  & axi4.r.ready
  val r_valid : Bool = axi4.r.valid
  val r_done : Bool = axi4.r.bits.last

  //----------------------------初始化-----------------------------------
  val ar_id : UInt = RegInit(0.U)
  val ar_len : UInt = RegInit(0.U)
  val ar_size : UInt = RegInit(0.U)
  val ar_burst : UInt = RegInit(0.U)
  val ar_lock : UInt = RegInit(0.U)
  val ar_cache : UInt = RegInit(0.U)
  val ar_qos : UInt = RegInit(0.U)
  val ar_user : UInt = RegInit(0.U)
  val ar_prot : UInt = RegInit(0.U)
  val ar_region : UInt = RegInit(0.U)
  val ar_addr : UInt = RegInit(0.U)
  val r_ready : UInt = RegInit(0.U)
  val ifu_ready : UInt = RegInit(0.U)
  val ifu_rdata : UInt = RegInit(0.U)


  //-------------------------状态机-------------------------------
  when(reset.asBool()) {
    rState := RState.idle
  }.otherwise{
    when (io.ifuin.valid) {
      switch(rState) {
        is(RState.idle)     {when(ar_hs)  {rState := RState.ar_end}}//idle or reset
        is(RState.ar_end)   {when(r_valid) {rState := RState.r_begin}}
        is(RState.r_begin)  {when(r_done) {rState := RState.r_end}}
        // when (Rstate == addr)  && (r.ready_out) && ( r.valid_in ){change state}
        // todo: 检查是否可以合并idle和r_end状态
        is(RState.r_end)    { rState := RState.idle} // 等一个周期就恢复idle
      }
    }
  }

  //----------------------------状态机赋值-----------------------------
  switch(rState) {
    is(RState.idle) {//
      ar_id     := 0.U
      ar_len    := 0.U
      ar_size   := 0.U
      ar_burst  := 0.U
      ar_lock   := 0.U
      ar_cache  := 0.U
      ar_qos    := 0.U
      ar_user   := 0.U
      ar_prot   := 0.U
      ar_region := 0.U
      ar_addr   := 0.U
      r_ready   := 0.U
      ifu_ready := false.B
      ifu_rdata  := 0.U
    }//fuck zero
    is(RState.ar_end) {
      import AXI4Parameters.{AXI_PROT, AXI_SIZE}
      ar_addr   := io.ifuin.addr
      ar_len    := 0.U
      ar_prot   := AXI_PROT.PRIVILEGED | AXI_PROT.SECURE | AXI_PROT.INSTRUCTION
      ar_size   := AXI_SIZE.bytes8
      //TODO 考虑burst后ar.bits :=
    }
    is(RState.r_begin) {
      r_ready   := true.B
      ifu_rdata := axi4.r.bits.data
      //TODO 考虑burst后r.bits :=
    }
    is(RState.r_end) {
      ifu_ready := true.B
    }
  }
  // ----------------------AXI连接------------------------
  axi4.ar.valid       := io.ifuin.valid // ifu pc always valid
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
}
