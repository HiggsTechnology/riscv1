package core
import chisel3._
import chisel3.util._

class WBUIO extends Bundle {
    val in  = new RegWriteIO
    val out = Flipped(new RegWriteIO)
}

class WBU extends Module {
    val io = IO(new WBUIO)
    io.out <> io.in 
}