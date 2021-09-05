package Core.WBU

import Core.MemReg.RegWriteIO
import chisel3._
import chisel3.util._

class WBUIO extends Bundle {
    val in  = Flipped(Valid(new RegWriteIO))
    val out = Valid(new RegWriteIO)
}

class WBU extends Module {
    val io = IO(new WBUIO)
    
    // printf("**************************************************\n")
    // printf("Print during simulation: io.in.wdata is %x\n", io.in.data)
    io.out <> io.in 
}