package Device

import Bus.SimpleBus
import chisel3._
import difftest.UARTIO

class SimUartIO extends Bundle {
  val bus : SimpleBus = Flipped(new SimpleBus)
  val uart = new UARTIO
}

class SimUart extends Module {
  val io : SimUartIO = IO(new SimUartIO)

  io.uart.out.valid := io.bus.req.valid && io.bus.req.bits.isWrite
  io.uart.out.ch    := io.bus.req.bits.data(7,0)
  io.uart.in.valid  := io.bus.req.valid && !io.bus.req.bits.isWrite
  io.bus.req.ready   := true.B

  io.bus.resp.valid  := RegNext(io.bus.req.valid)
  io.bus.resp.bits.data := io.uart.in.ch
}
