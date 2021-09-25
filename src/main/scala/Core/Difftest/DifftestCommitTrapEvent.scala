package Core.Difftest

import chisel3._
import chisel3.util.experimental.BoringUtils.{addSink, addSource}
import difftest.{DiffTrapEventIO, DifftestTrapEvent}

class DifftestCommitTrapEvent extends Module{
  val io = IO(new Bundle() {})

  val trapEventIO = WireInit(0.U).asTypeOf(new DiffTrapEventIO)
  addSink(trapEventIO.valid, "difftest_trapEvent_valid")
  addSink(trapEventIO.code, "difftest_trapEvent_code")
  addSink(trapEventIO.pc, "difftest_trapEvent_pc")
  addSink(trapEventIO.cycleCnt, "difftest_trapEvent_cycleCnt")
  addSink(trapEventIO.instrCnt, "difftest_trapEvent_instrCnt")

  val trap : DifftestTrapEvent = Module(new DifftestTrapEvent)
  trap.io.clock    := clock
  trap.io.coreid   := 0.U
  trap.io.valid    := trapEventIO.valid
  trap.io.code     := trapEventIO.code
  trap.io.pc       := trapEventIO.pc
  trap.io.cycleCnt := trapEventIO.cycleCnt
  trap.io.instrCnt := trapEventIO.instrCnt
}
