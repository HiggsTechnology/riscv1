package Sim

import chisel3._
import chisel3.util._
import difftest._
import Chisel.unless
import Core.TOP.Top


class SimTopIO extends Bundle {
  val logCtrl = new LogCtrlIO
  val perfInfo = new PerfInfoIO
  val uart = new UARTIO
}

class SimTop extends Module {
  val io : SimTopIO = IO(new SimTopIO())
  io.uart.in.valid  := false.B
  io.uart.out.valid := false.B
  io.uart.out.ch  := 0.U
  val rvcore = Module(new Top)


  val instrCommit = Module(new DifftestInstrCommit)
  instrCommit.io.clock := clock
  instrCommit.io.coreid := 0.U
  instrCommit.io.index := 0.U
  instrCommit.io.skip := false.B
  instrCommit.io.isRVC := false.B
  instrCommit.io.scFailed := false.B

  instrCommit.io.valid := RegNext(RegNext(rvcore.io.valid))
  instrCommit.io.pc    := RegNext(RegNext(rvcore.io.out.pc))

  instrCommit.io.instr := RegNext(RegNext(rvcore.io.out.instr))

  instrCommit.io.wen   := RegNext(RegNext(rvcore.io.diffreg.ena))
  instrCommit.io.wdata := RegNext(RegNext(rvcore.io.diffreg.data))
  instrCommit.io.wdest := RegNext(RegNext(rvcore.io.diffreg.addr))
  

  val csrCommit = Module(new DifftestCSRState)
  csrCommit.io.clock          := clock
  csrCommit.io.priviledgeMode := 0.U
  csrCommit.io.mstatus        := 0.U
  csrCommit.io.sstatus        := 0.U
  csrCommit.io.mepc           := 0.U
  csrCommit.io.sepc           := 0.U
  csrCommit.io.mtval          := 0.U
  csrCommit.io.stval          := 0.U
  csrCommit.io.mtvec          := 0.U
  csrCommit.io.stvec          := 0.U
  csrCommit.io.mcause         := 0.U
  csrCommit.io.scause         := 0.U
  csrCommit.io.satp           := 0.U
  csrCommit.io.mip            := 0.U
  csrCommit.io.mie            := 0.U
  csrCommit.io.mscratch       := 0.U
  csrCommit.io.sscratch       := 0.U
  csrCommit.io.mideleg        := 0.U
  csrCommit.io.medeleg        := 0.U
}


