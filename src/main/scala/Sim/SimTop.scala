package Sim

import chisel3._
import chisel3.util._
import difftest._
import Core.AXI4.AXI4IO
import Core.TOP.Top


class SimTopIO extends Bundle {
  val logCtrl = new LogCtrlIO
  val perfInfo = new PerfInfoIO
  val uart = new UARTIO
  val memAXI_0 = new AXI4IO
}

class SimTop extends MultiIOModule {
  val ifu_use_axi: Boolean = false
  val lsu_use_axi: Boolean = false

  val io : SimTopIO = IO(new SimTopIO())
  io.uart.in.valid  := false.B
  io.uart.out.valid := false.B
  io.uart.out.ch  := 0.U
  val rvcore = Module(new Top(ifu_use_axi, lsu_use_axi))

  if(ifu_use_axi || lsu_use_axi) {
    io.memAXI_0 <> rvcore.io.axi4
  } else {
    io.memAXI_0 <> DontCare
  }

  val instrCommit = Module(new DifftestInstrCommit)
  instrCommit.io.clock := clock
  instrCommit.io.coreid := 0.U
  instrCommit.io.index := 0.U
  instrCommit.io.skip := false.B
  instrCommit.io.isRVC := false.B
  instrCommit.io.scFailed := false.B

  if(ifu_use_axi) {
    instrCommit.io.valid := RegNext(RegNext(rvcore.io.valid))
  } else {
    instrCommit.io.valid := RegNext(RegNext(rvcore.io.valid))
  }

  instrCommit.io.pc    := RegNext(RegNext(rvcore.io.out.pc))

  instrCommit.io.instr := RegNext(RegNext(rvcore.io.out.instr))

  instrCommit.io.wen   := RegNext(RegNext(rvcore.io.diffreg.ena))
  instrCommit.io.wdata := RegNext(RegNext(rvcore.io.diffreg.data))
  instrCommit.io.wdest := RegNext(RegNext(rvcore.io.diffreg.addr))

  val trap = Module(new DifftestTrapEvent)
  trap.io.clock    := clock
  trap.io.coreid   := 0.U
  trap.io.valid    := RegNext(RegNext(rvcore.io.out.instr)) === BigInt("0000006b", 16).U
  trap.io.code     := 0.U // GoodTrap
  trap.io.pc       := RegNext(RegNext(rvcore.io.out.pc))
  trap.io.cycleCnt := 0.U
  trap.io.instrCnt := 0.U


}


