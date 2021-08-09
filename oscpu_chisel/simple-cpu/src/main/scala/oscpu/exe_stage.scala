package oscpu

import chisel3._
import chisel3.util._

class Execute extends Module {
  val io = IO(new Bundle {
    //val inst_type = Input(UInt(5.W)) 
    val inst_opcode = Input(UInt(8.W))  
    val op1 = Input(SInt(64.W))
    val op2 = Input(SInt(64.W))
    val rd_data = Output(SInt(64.W))    
  })
  
  val result = WireDefault(0.S)
  
  switch( io.inst_opcode ) {
    is(Types.addi) { result := io.op1 + io.op2 }
  }
  
  io.rd_data := Mux(Module.reset.asBool, 0.S, result)
}

