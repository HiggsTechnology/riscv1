package Core.TOP

import Bus.AXI4.AXI4IO
import Core.MemReg.RegWriteIO
import Devices.Clint.Clint
import Devices.SimpleUart
import chisel3._
import difftest.UARTIO
import utils.BasicIOType.OutBool
import utils.Pc_Instr

class TopIO(use_axi: Boolean = true) extends Bundle {
  val out   = new Pc_Instr
  val inst_valid : Bool = Output(Bool())
  val difftest_skip : Bool = OutBool()
  val diff_reg = new RegWriteIO
  val axi4 : AXI4IO = if(use_axi) new AXI4IO else null
  val uart = new UARTIO
  //axi4 bundle out
}

class Top(
  ifu_use_axi: Boolean = false,
  lsu_use_axi: Boolean = false,
  need_difftest: Boolean = false,
) extends Module {
  private val use_axi = ifu_use_axi || lsu_use_axi

  val io = IO(new TopIO(use_axi))

  private val rvcore = Module(new RVCore(
    ifu_use_axi = ifu_use_axi,
    lsu_use_axi = lsu_use_axi,
    need_difftest = need_difftest,
  ))
  private val clint = Module(new Clint)
  private val uart = Module(new SimpleUart)

  io.out            <> rvcore.io.out
  io.inst_valid     <> rvcore.io.inst_valid
  io.diff_reg       <> rvcore.io.diff_reg
  io.axi4           <> rvcore.io.axi4
  clint.io          <> rvcore.io.clint
  uart.io.in        <> rvcore.io.to_uart
  uart.io.uart      <> io.uart

  io.difftest_skip  := rvcore.io.difftest_skip
}
