package Core.AXI4
import Core.AXI4.AXI4Parameters.{AXI_PROT, AXI_SIZE}
import Core.{Config, LSU2MemIO}
import chisel3._
import utils._
import chisel3.util._



class LSURWIO extends Bundle with Config {
  val lsuin = Flipped(new LSU2MemIO)
  val lsu2crossbar = new AXI4IO
}

class LSURW extends Module with Config {
  val io : LSURWIO = IO(new LSURWIO)
  val axi4 : AXI4IO = io.lsu2crossbar
  val (ar, aw, w, r, b) = (axi4.ar.bits, axi4.aw.bits, axi4.w.bits, axi4.r.bits, axi4.b.bits)
  object RState {
    val idle :: ar_valid :: ar_trans :: r_trans :: r_done :: Nil = Enum(5)
  }
  object WState {
    val idle :: valid :: trans :: wait_resp :: done :: Nil = Enum(5)//
  }

  val rState : UInt = RegInit(RState.idle)
  val wState : UInt = RegInit(WState.idle)

  val lsu_valid : Bool = io.lsuin.valid
  val lsu_r_valid : Bool = lsu_valid & !io.lsuin.is_write
  val lsu_w_valid : Bool = lsu_valid & io.lsuin.is_write

  //----------------------------状态机转移信号----------------------------
  val ar_ready : Bool = axi4.ar.ready
  val aw_ready : Bool = axi4.aw.ready
  val w_ready : Bool = axi4.w.ready
  val b_valid : Bool = axi4.b.valid
  val r_valid : Bool = axi4.r.valid
  val ar_hs : Bool = axi4.ar.valid & axi4.ar.ready
  val r_hs : Bool = axi4.r.valid  & axi4.r.ready
  val aw_hs : Bool = axi4.aw.valid & axi4.aw.ready
  val w_hs : Bool = axi4.w.valid  & axi4.w.ready
  val b_hs : Bool = axi4.b.valid  & axi4.b.ready


  val r_done : Bool = axi4.r.bits.last
  val w_done : Bool = axi4.w.bits.last

  //-------------------------状态机-------------------------------
  when(reset.asBool()) {
    rState := RState.idle
  }.otherwise {
    when(lsu_r_valid) {
      switch(rState) {
        is(RState.idle)     {when(lsu_r_valid)  {rState := RState.ar_valid}}
        is(RState.ar_valid) {when(ar_ready)     {rState := RState.ar_trans}}
        is(RState.ar_trans) {when(r_valid)      {rState := RState.r_trans}}
        is(RState.r_trans)  {when(r_done)       {rState := RState.r_done}}
        // todo: 检查是否可以合并idle和r_end状态
        is(RState.r_done)   { rState := RState.idle }
      }
    }
  }

  when(reset.asBool()) {
    wState := WState.idle
  }.otherwise {
    when(lsu_w_valid) {
      switch(wState) {
        is(WState.idle)       {when(lsu_w_valid)        {wState := WState.valid}}
        is(WState.valid)      {when(aw_ready & w_ready) {wState := WState.trans}}
        is(WState.trans)      {when(w_done)            {wState := WState.wait_resp}}
        is(WState.wait_resp)  {when(b_valid)             {wState := WState.done}}
        is(WState.done)       {wState := WState.idle}
      }
    }
  }

  //----------------------------初始化-----------------------------------
  val aw_valid : UInt = RegInit(0.U)
  val aw_addr : UInt = RegInit(0.U)
  val aw_prot : UInt = RegInit(0.U)
  val aw_id : UInt = RegInit(0.U)
  val aw_user : UInt = RegInit(0.U)
  val aw_len : UInt = RegInit(0.U)
  val aw_size : UInt = RegInit(0.U)
  val aw_burst : UInt = RegInit(0.U)
  val aw_lock : UInt = RegInit(0.U)
  val aw_cache : UInt = RegInit(0.U)
  val aw_qos : UInt = RegInit(0.U)
  val aw_region : UInt = RegInit(0.U)

  val w_valid : UInt = RegInit(0.U)
  val w_data : UInt = RegInit(0.U)
  val w_strb : UInt = RegInit(0.U)
  val w_last : UInt = RegInit(0.U)
  val w_user : UInt = RegInit(0.U)

  val b_ready : UInt = RegInit(0.U)

  val ar_valid : UInt = RegInit(0.U)
  val ar_addr : UInt = RegInit(0.U)
  val ar_prot : UInt = RegInit(0.U)
  val ar_id : UInt = RegInit(0.U)
  val ar_user : UInt = RegInit(0.U)
  val ar_len : UInt = RegInit(0.U)
  val ar_size : UInt = RegInit(0.U)
  val ar_burst : UInt = RegInit(0.U)
  val ar_lock : UInt = RegInit(0.U)
  val ar_cache : UInt = RegInit(0.U)
  val ar_qos : UInt = RegInit(0.U)
  val ar_region : UInt = RegInit(0.U)

  val r_ready : UInt = RegInit(0.U)

  val lsu_rdata : UInt = RegInit(0.U)
  val lsu_r_ready : UInt = RegInit(0.U)
  val lsu_w_ready : UInt = RegInit(0.U)

  axi4.aw.valid         := aw_valid
  axi4.aw.bits.addr     := aw_addr
  axi4.aw.bits.prot     := aw_prot
  axi4.aw.bits.id       := aw_id
  axi4.aw.bits.user     := aw_user
  axi4.aw.bits.len      := aw_len
  axi4.aw.bits.size     := aw_size
  axi4.aw.bits.burst    := aw_burst
  axi4.aw.bits.lock     := aw_lock
  axi4.aw.bits.cache    := aw_cache
  axi4.aw.bits.qos      := aw_qos
  axi4.aw.bits.region   := aw_region
  axi4.w.valid          := w_valid
  axi4.w.bits.data      := w_data
  axi4.w.bits.strb      := w_strb
  axi4.w.bits.last      := w_last
  axi4.w.bits.user      := w_user
  axi4.b.ready          := b_ready
  axi4.ar.valid         := ar_valid
  axi4.ar.bits.addr     := ar_addr
  axi4.ar.bits.prot     := ar_prot
  axi4.ar.bits.id       := ar_id
  axi4.ar.bits.user     := ar_user
  axi4.ar.bits.len      := ar_len
  axi4.ar.bits.size     := ar_size
  axi4.ar.bits.burst    := ar_burst
  axi4.ar.bits.lock     := ar_lock
  axi4.ar.bits.cache    := ar_cache
  axi4.ar.bits.qos      := ar_qos
  axi4.ar.bits.region   := ar_region
  axi4.r.ready          := ar_ready

  when (!reset.asBool()){
    switch(rState) {
      is(RState.idle) {
        ar_valid  := 0.U
        ar_addr   := 0.U
        ar_prot   := 0.U
        ar_id     := 0.U
        ar_user   := 0.U
        ar_len    := 0.U
        ar_size   := 0.U
        ar_burst  := 0.U
        ar_lock   := 0.U
        ar_cache  := 0.U
        ar_qos    := 0.U
        ar_region := 0.U
        r_ready   := 0.U
        lsu_r_ready := 0.U
        lsu_rdata := 0.U
      }
      is(RState.ar_valid) {
        ar_valid  := true.B
      }
      is(RState.ar_trans) {
        // todo: support burst mode
        ar_addr   := io.lsuin.addr
        ar_prot   := AXI_PROT.PRIVILEGED | AXI_PROT.SECURE | AXI_PROT.DATA
        ar_id     := 0.U
        ar_user   := 0.U
        ar_len    := 0.U
        ar_size   := AXI_SIZE.bytes8
        ar_burst  := 0.U
        ar_lock   := 0.U
        ar_cache  := 0.U
        ar_qos    := 0.U
        ar_region := 0.U
      }
      is(RState.r_trans) {
        // todo: support burst mode
        lsu_rdata := axi4.r.bits.data
        r_ready   := true.B
      }
      is(RState.r_done) {
        lsu_r_ready := true.B
      }
    }
  }

  io.lsuin.rdata := lsu_rdata
  io.lsuin.rready := lsu_r_ready

  when (!reset.asBool()) {
    switch(wState) {
      is(WState.idle)  {
        aw_valid    := 0.U
        aw_addr     := 0.U
        aw_prot     := 0.U
        aw_id       := 0.U
        aw_user     := 0.U
        aw_len      := 0.U
        aw_size     := 0.U
        aw_burst    := 0.U
        aw_lock     := 0.U
        aw_cache    := 0.U
        aw_qos      := 0.U
        aw_region   := 0.U
        w_valid     := 0.U
        w_data      := 0.U
        w_strb      := 0.U
        w_last      := 0.U
        w_user      := 0.U
        b_ready     := 0.U
      }
      is(WState.valid) {
        aw_valid    := true.B
        w_valid     := true.B
      }
      is(WState.trans) {
        aw_addr     := io.lsuin.addr
        aw_prot     := AXI_PROT.PRIVILEGED | AXI_PROT.SECURE | AXI_PROT.DATA
        aw_id       := 0.U
        aw_user     := 0.U
        aw_len      := 0.U      // todo: support burst
        aw_size     := AXI_SIZE.bytes8
        aw_burst    := 0.U      // todo: support burst
        aw_lock     := 0.U
        aw_cache    := 0.U
        aw_qos      := 0.U
        aw_region   := 0.U
        w_data      := io.lsuin.wdata
        w_strb      := io.lsuin.strb
        w_last      := true.B   // todo: support burst
        w_user      := 0.U
      }
      is(WState.wait_resp) {
        // nothing
      }
      is(WState.done) {
        b_ready     := true.B
        lsu_w_ready := true.B
      }
    }
  }
  io.lsuin.wready := lsu_w_ready
}
