package Core

import Core.AXI4.AXI4IO
import chisel3._
import utils.OutBool

class riscv_cpu_io extends Bundle {
  val mem = new AXI4IO()
  val meip = OutBool()
}

class riscv_cpu extends Module {
  val io : riscv_cpu_io = IO(new riscv_cpu_io)

  val core = Module(new CoreTop)
}
