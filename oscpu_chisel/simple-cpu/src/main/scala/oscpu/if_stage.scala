package oscpu

import chisel3._
import chisel3.util._

class Fetch extends Module {
  val io = IO(new Bundle {
    val inst_addr = Output(UInt(64.W))   // pc
    val inst_ena = Output(Bool())     // ready to fetch the instuction
  })
  
  val pcReg = RegInit(0.U(64.W))
  pcReg := pcReg + 4.U
  
  io.inst_addr := pcReg
  io.inst_ena := Mux(Module.reset.asBool, false.B, true.B)
}

