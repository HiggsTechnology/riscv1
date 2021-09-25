package Core.Difftest

import chisel3._
import chisel3.util.experimental.BoringUtils.{addSink, addSource}
import difftest.{DiffArchEventIO, DifftestArchEvent}

class DifftestCommitArch extends Module {
  val io = IO(new Bundle {  })

  val diffArchEventIO : DiffArchEventIO = WireInit(0.U).asTypeOf(new DiffArchEventIO)
  addSink(diffArchEventIO.intrNO, "difftest_intrNO")
  addSink(diffArchEventIO.cause, "difftest_cause")
  addSink(diffArchEventIO.exceptionPC, "difftest_exceptionPC")
  addSink(diffArchEventIO.exceptionInst, "difftest_exceptionInst")
  val difftestArchEvent = Module(new DifftestArchEvent)
  difftestArchEvent.io.clock := clock
  difftestArchEvent.io.coreid := 0.U
  difftestArchEvent.io.intrNO := diffArchEventIO.intrNO
  difftestArchEvent.io.cause := diffArchEventIO.cause
  difftestArchEvent.io.exceptionPC := diffArchEventIO.exceptionPC
  difftestArchEvent.io.exceptionInst := diffArchEventIO.exceptionInst
}
