package Device

import Bus.SimpleBus
import chisel3._
import chisel3.util._
import chisel3.util.experimental.BoringUtils
import utils.{OutBool, OutUInt}

class ClintOutPort extends Bundle {
  val mtip : Bool = OutBool()
  val mtime : UInt = OutUInt(64)
  val mtimecmp : UInt = OutUInt(64)
}

class ClintIO extends Bundle {
  val bus = Flipped(new SimpleBus)
}

class Clint extends Module {
  val io : ClintIO = IO(new ClintIO)

  private val addr_wire = WireInit(io.bus.req.bits.addr)
  private val addr_reg  = RegEnable(io.bus.req.bits.addr, io.bus.req.valid)
  private val addr      = Mux(io.bus.req.valid, addr_wire, addr_reg)
  private val wdata     = io.bus.req.bits.data
  private val wstrb     = io.bus.req.bits.wmask
  private val size      = io.bus.req.bits.size

  private val rdata = WireInit(0.U)

  private val mtime     = RegInit(0.U(64.W))
  private val mtimecmp  = RegInit(20.U(64.W))
  object Addr {
    val mtime     = 0x0200bff8L.U
    val mtimecmp  = 0x02004000L.U
  }

  private val clk = 1000
  private val tick = Counter(true.B, clk)._2
  when (tick) { mtime := mtime + 1.U }

  object State {
    val idle :: trans :: Nil = Enum(2)
  }
  private val state = RegInit(State.idle)

  switch(state) {
    is(State.idle)  { when(io.bus.req.fire()) {state := State.trans} }
    is(State.trans) { when(io.bus.resp.fire()) {state := State.idle} }
  }

  private val ready = WireInit(0.U)

  switch(state) {
    is(State.idle)  {
      ready := false.B
    }
    is(State.trans) {
      ready := true.B
    }
  }

  when(io.bus.req.valid && io.bus.req.bits.isWrite) {
    switch(addr) {
      // 不支持给mtime赋值
      //      is(Addr.mtime)    { mtime     := wdata }
      is(Addr.mtimecmp) { mtimecmp  := wdata }
    }
    rdata := 0.U
  }.otherwise {
    rdata := MuxLookup(addr, (BigInt(Long.MaxValue) * 2 + 1).U, Array(
      Addr.mtime -> mtime,
      Addr.mtimecmp -> mtimecmp
    ))
  }

  io.bus.req.ready := true.B
  io.bus.resp.bits.data := rdata
  io.bus.resp.valid := RegNext(io.bus.req.valid)

  val mtip = mtime >= mtimecmp
  BoringUtils.addSource(mtip, "mtip")
  BoringUtils.addSource(mtime, "mtime")
}
