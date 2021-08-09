/*
 * This code is a minimal hardware described in Chisel.
 * 
 * And Gate: the FPGA version of Hello World
 */

import chisel3._
import chisel3.Driver

/**
 * The And Gate component.
 */

class Hello extends Module {
  val io = IO(new Bundle {
    val sw = Input(UInt(2.W))
    val led = Output(UInt(1.W))
  })
  
  io.led := io.sw(0) & io.sw(1)
}

/**
 * An object extending App to generate the Verilog code.
 */
object Hello extends App {
  (new chisel3.stage.ChiselStage).emitVerilog(new Hello())
}
