package Sim

import Bus.AXI4.AXI4IO
import Core.TOP.{RVCore, Top}
import chisel3._
import difftest._


class SimTopIO extends Bundle {
  val logCtrl = new LogCtrlIO
  val perfInfo = new PerfInfoIO
  val uart = new UARTIO
  val memAXI_0 = new AXI4IO
}

class SimTop extends MultiIOModule {
  val ifu_use_axi: Boolean = true
  val lsu_use_axi: Boolean = true
  val need_difftest: Boolean = true

  val io : SimTopIO = IO(new SimTopIO())

  val top = Module(new Top(
    ifu_use_axi = ifu_use_axi,
    lsu_use_axi = lsu_use_axi,
    need_difftest = need_difftest
  ))

  if(ifu_use_axi || lsu_use_axi) {
    io.memAXI_0 <> top.io.axi4
  } else {
    io.memAXI_0 <> DontCare
  }

  io.uart <> top.io.uart

  val instrCommit = Module(new DifftestInstrCommit)
  instrCommit.io.clock := clock
  instrCommit.io.coreid := 0.U
  instrCommit.io.index := 0.U
  instrCommit.io.skip := RegNext(RegNext(top.io.difftest_skip))
  instrCommit.io.isRVC := false.B
  instrCommit.io.scFailed := false.B

  if(ifu_use_axi) {
    instrCommit.io.valid := RegNext(RegNext(top.io.inst_valid))
  } else {
    instrCommit.io.valid := RegNext(RegNext(top.io.inst_valid))
  }

  instrCommit.io.pc    := RegNext(RegNext(top.io.out.pc))

  instrCommit.io.instr := RegNext(RegNext(top.io.out.instr))

  instrCommit.io.wen   := RegNext(RegNext(top.io.diff_reg.ena))
  instrCommit.io.wdata := RegNext(RegNext(top.io.diff_reg.data))
  instrCommit.io.wdest := RegNext(RegNext(top.io.diff_reg.addr))


}


