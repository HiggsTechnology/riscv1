package Core.Pipeline

import Core.MemReg.RegWriteIO
import chisel3._
import chisel3.util._

class EX_WB extends Module {
  class EX_WB_IO extends Bundle {
    val in : ValidIO[RegWriteIO] = Flipped(Valid(new RegWriteIO))
    val out : ValidIO[RegWriteIO] = Valid(new RegWriteIO)
  }
  val io : EX_WB_IO = IO(new EX_WB_IO)
  io.out := RegNext(io.in)
}
