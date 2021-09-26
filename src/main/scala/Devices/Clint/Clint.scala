package Devices.Clint

import Bus.{MMIOSimpleBus, MMIOSimpleReqBundle}
import chisel3._
import chisel3.util._
import utils.BasicIOType.{OutBool, OutUInt}
import utils.SimpleSyncBus

class ClintOutPort extends Bundle {
  val mtip : Bool = OutBool()
  val mtime : UInt = OutUInt(64)
  val mtimecmp : UInt = OutUInt(64)
}

class ClintIO extends Bundle {
  val in = Flipped(new SimpleSyncBus)
  val out = new ClintOutPort
}

class Clint extends Module {
  val io : ClintIO = IO(new ClintIO)

  val addr_wire = WireInit(io.in.addr)
  val addr_reg  = RegEnable(io.in.addr, io.in.valid)
  val addr      = Mux(io.in.valid, addr_wire, addr_reg)
  val wdata     = io.in.wdata
  val wstrb     = io.in.wstrb
  val size      = io.in.size

  val rdata = WireInit(0.U)

  private val mtime     = RegInit(0.U(64.W))
  private val mtimecmp  = RegInit(1000.U(64.W))
  object Addr {
    val mtime     = 0x0200bff8L.U
    val mtimecmp  = 0x02004000L.U
  }

  val clk = 1000
  val tick = Counter(true.B, clk)._2
  when (tick) { mtime := mtime + 1.U }

  object State {
    val idle :: trans :: Nil = Enum(2)
  }
  val state = RegInit(State.idle)

  switch(state) {
    is(State.idle)  { when(io.in.valid) {state := State.trans} }
    is(State.trans) { when(io.in.ready) {state := State.idle} }
  }

  val ready = WireInit(0.U)

  switch(state) {
    is(State.idle)  {
      ready := false.B
    }
    is(State.trans) {
      ready := true.B
    }
  }

  when(io.in.valid && io.in.is_write) {
    switch(io.in.addr) {
      // 不支持给mtime赋值
//      is(Addr.mtime)    { mtime     := wdata }
      is(Addr.mtimecmp) { mtimecmp  := wdata }
    }
    rdata := 0.U
  }.otherwise {
    rdata := MuxLookup(addr, (BigInt(Long.MaxValue) * 2 + 1).U, Array(
      Addr.mtime -> mtime,
      Addr.mtimecmp -> mtimecmp,
    ))
  }

  io.in.rdata := rdata
  io.in.ready := ready

  io.out.mtip := mtime >= mtimecmp
  io.out.mtime := mtime
  io.out.mtimecmp := mtimecmp
}
