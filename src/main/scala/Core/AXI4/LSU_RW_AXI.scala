package Core.AXI4
import Core.AXI4.AXI4Parameters.{AXI_PROT, AXI_SIZE}
import Core.Config.Config
import chisel3._
import utils._
import chisel3.util._

class LSURWIO extends Bundle with Config {
  val lsuin = Flipped(new LSU2RW)
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


  //----------------------------读初始化-----------------------------------

  val ar_valid  : Bool = WireInit(false.B)
  val ar_addr   : UInt = WireInit(0.U)
  val ar_prot   : UInt = WireInit(0.U)
  val ar_id     : UInt = WireInit(0.U)
  val ar_user   : UInt = WireInit(0.U)
  val ar_len    : UInt = WireInit(0.U)
  val ar_size   : UInt = WireInit(0.U)
  val ar_burst  : UInt = WireInit(AXI4Parameters.BURST_INCR)
  val ar_lock   : UInt = WireInit(0.U)
  val ar_cache  : UInt = WireInit("b0010".U)
  val ar_qos    : UInt = WireInit(0.U)
  val ar_region : UInt = WireInit(0.U)
  val r_ready   : Bool = WireInit(false.B)
  val lsu_addr  : UInt = RegInit(0.U)
  val lsu_rdata : UInt = WireInit(0.U)
  val lsu_wdata : UInt = io.lsuin.wdata
  val lsu_wstrb : UInt = io.lsuin.wstrb
  val lsu_r_ready : Bool = WireInit(false.B)
  val lsu_w_ready : Bool = WireInit(false.B)

  //----------------------------读数据处理-----------------------------------

//  val ar_addr_new = lsu_addr
  val ar_addr_new = Cat(
    lsu_addr(XLEN-1, AXI4Parameters.addrAlignedBits),
    0.U(AXI4Parameters.addrAlignedBits.W)
  )
  val ar_len_new  = 0.U
  val ar_prot_new = AXI_PROT.UNPRIVILEGED | AXI_PROT.SECURE | AXI_PROT.DATA
  val ar_size_new = AXI4Parameters.AXI_SIZE.bytes32

  //-------------------------状态机-------------------------------
  when(reset.asBool()) {
    rState := RState.idle
  }.otherwise {
    switch(rState) {
      is(RState.idle)     {when(lsu_r_valid)  {rState := RState.ar_valid; lsu_addr := io.lsuin.addr}}
      is(RState.ar_valid) {when(ar_ready)     {rState := RState.ar_trans}}
      is(RState.ar_trans) {when(r_valid)      {rState := RState.r_trans}}
      is(RState.r_trans)  {when(r_done)       {rState := RState.r_done}}
      // todo: 检查是否可以合并idle和r_end状态
      is(RState.r_done)   { rState := RState.idle }
    }
  }

  when(reset.asBool()) {
    wState := WState.idle
  }.otherwise {
    switch(wState) {
      // 读写请求的上升沿保存地址，后续没有寄存器支持可能会改变
      is(WState.idle)       {when(lsu_w_valid)        {wState := WState.valid;  lsu_addr := io.lsuin.addr}}
      is(WState.valid)      {when(aw_ready & w_ready) {wState := WState.trans}}
      is(WState.trans)      {when(w_done)             {wState := WState.wait_resp}}
      is(WState.wait_resp)  {when(b_valid)            {wState := WState.done}}
      is(WState.done)       {wState := WState.idle}
    }
  }

  //----------------------------读过程响应-----------------------------------

  when (!reset.asBool()){
    switch(rState) {
      is(RState.idle) {
        ar_valid  := false.B
        ar_addr   := ar_addr_new
        ar_len    := ar_len_new
        ar_prot   := ar_prot_new
        ar_size   := ar_size_new

        r_ready   := false.B
        lsu_r_ready := false.B
        lsu_rdata := 0.U
      }
      is(RState.ar_valid) {
        ar_valid  := true.B
        ar_addr   := ar_addr_new
        ar_len    := ar_len_new
        ar_prot   := ar_prot_new
        ar_size   := ar_size_new

        r_ready   := true.B
        lsu_r_ready := false.B
        lsu_rdata := 0.U
      }
      is(RState.ar_trans) {
        // todo: support burst mode
        ar_valid  := false.B
        ar_addr   := ar_addr_new
        ar_len    := ar_len_new
        ar_prot   := ar_prot_new
        ar_size   := ar_size_new

        r_ready   := true.B
        lsu_r_ready := false.B
        lsu_rdata := 0.U
      }
      is(RState.r_trans) {
        // todo: support burst mode
        ar_valid  := false.B
        ar_addr   := ar_addr_new
        ar_len    := ar_len_new
        ar_prot   := ar_prot_new
        ar_size   := ar_size_new
        r_ready   := true.B
        lsu_r_ready := false.B
        lsu_rdata := MuxLookup(lsu_addr(AXI4Parameters.addrAlignedBits-1, 3), 0.U, Array(
          0.U -> axi4.r.bits.data( 63,   0),
          1.U -> axi4.r.bits.data(127,  64),
          2.U -> axi4.r.bits.data(191, 128),
          3.U -> axi4.r.bits.data(255, 192),
        ))
      }
      is(RState.r_done) {
        ar_valid  := false.B
        ar_addr   := ar_addr_new
        ar_len    := ar_len_new
        ar_prot   := ar_prot_new
        ar_size   := ar_size_new
        r_ready   := true.B
        lsu_r_ready := true.B
        lsu_rdata := MuxLookup(lsu_addr(AXI4Parameters.addrAlignedBits-1, 3), 0.U, Array(
          0.U -> axi4.r.bits.data( 63,   0),
          1.U -> axi4.r.bits.data(127,  64),
          2.U -> axi4.r.bits.data(191, 128),
          3.U -> axi4.r.bits.data(255, 192),
        ))
      }
    }
  }

  //----------------------------写初始化-----------------------------------
  val aw_valid  : Bool = WireInit(false.B)
  val aw_addr   : UInt = WireInit(0.U)
  val aw_prot   : UInt = WireInit(0.U)
  val aw_id     : UInt = WireInit(0.U)
  val aw_user   : UInt = WireInit(0.U)
  val aw_len    : UInt = WireInit(0.U)
  val aw_size   : UInt = WireInit(0.U)
  val aw_burst  : UInt = WireInit(AXI4Parameters.BURST_INCR)
  val aw_lock   : UInt = WireInit(0.U)
  val aw_cache  : UInt = WireInit("b0010".U)
  val aw_qos    : UInt = WireInit(0.U)
  val aw_region : UInt = WireInit(0.U)
  val w_valid   : Bool = WireInit(false.B)
  val w_data    : UInt = WireInit(0.U)
  val w_strb    : UInt = WireInit(0.U)
  val w_last    : UInt = WireInit(0.U)
  val w_user    : UInt = WireInit(0.U)
  val b_ready   : Bool = WireInit(false.B)

  //----------------------------写数据处理-----------------------------------
//  val aw_addr_new = lsu_addr
  val aw_addr_new = Cat(
    lsu_addr(XLEN-1, 3),
    0.U(3.W)
  )
//  val aw_addr_new = Cat(
//    io.lsuin.addr(XLEN-1, AXI4Parameters.addrAlignedBits),
//    0.U(AXI4Parameters.addrAlignedBits.W)
//  )
  val aw_len_new  = 0.U
  val aw_prot_new = AXI_PROT.UNPRIVILEGED | AXI_PROT.SECURE | AXI_PROT.DATA
//  val aw_size_new = io.lsuin.size   //
  val aw_size_new = AXI4Parameters.AXI_SIZE.bytes8   //
  val w_data_new  = lsu_wdata(63,0)
//  val w_data_new  = MuxLookup(lsu_addr(AXI4Parameters.addrAlignedBits-1,3), 0.U, Array(
//    0.U -> Cat(0.U(192.W), lsu_wdata(63,0)            ),
//    1.U -> Cat(0.U(128.W), lsu_wdata(63,0), 0.U( 64.W)),
//    2.U -> Cat(0.U( 64.W), lsu_wdata(63,0), 0.U(128.W)),
//    3.U -> Cat(            lsu_wdata(63,0), 0.U(192.W)),
//  ))
  val w_strb_new  = lsu_wstrb(7,0)
//  val w_strb_new  = MuxLookup(lsu_addr(AXI4Parameters.addrAlignedBits-1,3), 0.U, Array(
//    0.U -> Cat(0.U(24.W), lsu_wstrb(7,0)            ),
//    1.U -> Cat(0.U(16.W), lsu_wstrb(7,0), 0.U( 8.W) ),
//    2.U -> Cat(0.U( 8.W), lsu_wstrb(7,0), 0.U(16.W) ),
//    3.U -> Cat(           lsu_wstrb(7,0), 0.U(24.W) ),
//  ))
  val w_last_new  = true.B            // Todo: Support burst

  //----------------------------写过程响应-----------------------------------
  when (!reset.asBool()) {
    switch(wState) {
      is(WState.idle)  {
        aw_valid    := false.B
        aw_addr     := 0.U
        aw_prot     := 0.U
        aw_len      := 0.U
        aw_size     := 0.U
        w_valid     := false.B
        w_data      := 0.U
        w_strb      := 0.U
        w_last      := 0.U
        b_ready     := false.B
        lsu_w_ready := false.B
      }
      is(WState.valid) {
        aw_valid    := true.B
        aw_addr     := aw_addr_new
        aw_prot     := aw_prot_new
        aw_len      := aw_len_new
        aw_size     := aw_size_new
        w_valid     := true.B
        w_data      := w_data_new
        w_strb      := w_strb_new
        w_last      := w_last_new
        b_ready     := false.B
        lsu_w_ready := false.B
      }
      is(WState.trans) {
        aw_valid    := false.B
        aw_addr     := aw_addr_new
        aw_prot     := aw_prot_new
        aw_len      := aw_len_new
        aw_size     := aw_size_new
        w_valid     := false.B
        w_data      := w_data_new
        w_strb      := w_strb_new
        w_last      := w_last_new
        b_ready     := false.B
        lsu_w_ready := false.B
      }
      is(WState.wait_resp) {
        aw_valid    := false.B
        aw_addr     := aw_addr_new
        aw_prot     := aw_prot_new
        aw_len      := aw_len_new
        aw_size     := aw_size_new
        w_valid     := false.B
        w_data      := 0.U
        w_strb      := 0.U
        w_last      := 0.U
        b_ready     := true.B
        lsu_w_ready := false.B
      }
      is(WState.done) {
        aw_valid    := false.B
        aw_addr     := aw_addr_new
        aw_prot     := aw_prot_new
        aw_len      := aw_len_new
        aw_size     := aw_size_new
        w_valid     := false.B
        w_data      := 0.U
        w_strb      := 0.U
        w_last      := 0.U
        b_ready     := true.B
        lsu_w_ready := true.B
      }
    }
  }

  io.lsuin.rdata := lsu_rdata
  io.lsuin.ready := lsu_r_ready || lsu_w_ready

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
  axi4.r.ready          := r_ready

  when(rState =/= RState.idle) {
    printf("----------------------------------------------------\n")
    printf("lsu_r_valid:%d, lsu_r_ready:%d, ar_valid: %d, ar_ready: %d, r_valid: %d, r_ready: %d\n",
      lsu_r_valid, lsu_r_ready, axi4.ar.valid, axi4.ar.ready, axi4.r.valid, axi4.r.ready)
    printf("read_addr: %x%x, raddr: %x%x, rdata: %x%x%x%x, read_data: %x%x\n",
      lsu_addr(63,32), lsu_addr(31, 0), axi4.ar.bits.addr(63,32), axi4.ar.bits.addr(31,0),
      axi4.r.bits.data(255, 192), axi4.r.bits.data(191, 128), axi4.r.bits.data(127,64), axi4.r.bits.data(63,0),
      io.lsuin.rdata(63,32), io.lsuin.rdata(31,0)
    )
    printf("rState: %d\n", rState)
  }.elsewhen(wState =/= WState.idle){
    printf("----------------------------------------------------\n")
    printf("lsu_w_valid:%d, aw_valid: %d, aw_ready: %d, w_valid: %d, w_ready: %d, b_valid: %d, b_ready: %d\n",lsu_w_valid, axi4.aw.valid, axi4.aw.ready, axi4.w.valid, axi4.w.ready, axi4.b.valid, axi4.b.ready)
    printf("waddr: %x%x, wstrb:%x, wdata: %x%x%x%x\n", axi4.aw.bits.addr(63,32), axi4.aw.bits.addr(31,0), axi4.w.bits.strb, axi4.w.bits.data(255, 192), axi4.w.bits.data(191, 128), axi4.w.bits.data(127,64), axi4.w.bits.data(63,0))
    printf("wState: %d\n", wState)
  }

  when (b_valid) {
    printf("axi4.b.bits.resp: %x\n", axi4.b.bits.resp)
  }
}
