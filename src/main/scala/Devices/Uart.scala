package Devices

import chisel3.{Bundle, Flipped, Module, RegNext}
import difftest.UARTIO
import utils.SimpleSyncBus

class SimpleUartIO extends Bundle {
  val in : SimpleSyncBus = Flipped(new SimpleSyncBus)
  val uart = new UARTIO
}

class SimpleUart extends Module {
  val io : SimpleUartIO = IO(new SimpleUartIO)

  io.uart.out.valid := io.in.valid && io.in.is_write
  io.uart.out.ch    := io.in.wdata(7,0)
  io.uart.in.valid  := io.in.valid && !io.in.is_write
  io.in.rdata := io.uart.in.ch
  io.in.ready := RegNext(io.in.valid)
}
