package Core

import Core.AXI4.{AXI4IO, ysyxAXI4IO}
import chisel3._
import utils.InBool

class riscv_cpu_io extends Bundle {
  val master = new ysyxAXI4IO()
  val slave  = Flipped(new ysyxAXI4IO())
}

class riscv_cpu extends Module {
  val io : riscv_cpu_io = IO(new riscv_cpu_io)
  val core = Module(new CoreTop)
  core.io.axi4 := DontCare

  core.io.axi4.ar.ready     := io.master.arready
  io.master.arvalid         := core.io.axi4.ar.valid
  io.master.araddr          := core.io.axi4.ar.bits.addr
  io.master.arid            := core.io.axi4.ar.bits.id
  io.master.arlen           := core.io.axi4.ar.bits.len
  io.master.arsize          := core.io.axi4.ar.bits.size
  io.master.arburst         := core.io.axi4.ar.bits.burst

  core.io.axi4.r.valid      := io.master.rvalid
  io.master.rready          := core.io.axi4.r.ready
  core.io.axi4.r.bits.resp  := io.master.rresp
  core.io.axi4.r.bits.data  := io.master.rdata
  core.io.axi4.r.bits.last  := io.master.rlast
  core.io.axi4.r.bits.id    := io.master.rid


  core.io.axi4.aw.ready     := io.master.awready
  io.master.awvalid         := core.io.axi4.aw.valid
  io.master.awaddr          := core.io.axi4.aw.bits.addr
  io.master.awid            := core.io.axi4.aw.bits.id
  io.master.awlen           := core.io.axi4.aw.bits.len
  io.master.awsize          := core.io.axi4.aw.bits.size
  io.master.awburst         := core.io.axi4.aw.bits.burst

  core.io.axi4.w.ready      := io.master.wready
  io.master.wvalid          := core.io.axi4.w.valid
  io.master.wdata           := core.io.axi4.w.bits.data
  io.master.wlast           := core.io.axi4.w.bits.last
  io.master.wstrb           := core.io.axi4.w.bits.strb

  io.master.bready          := core.io.axi4.b.ready
  core.io.axi4.b.valid      := io.master.bvalid
  core.io.axi4.b.bits.resp  := io.master.bresp
  core.io.axi4.b.bits.id    := io.master.bid

  io.slave := DontCare
  io.slave.arready := false.B
  io.slave.rvalid  := false.B
  io.slave.awready := false.B
  io.slave.wready  := false.B
  io.slave.bvalid  := false.B
}
