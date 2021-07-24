package oscpu

import chisel3._
import chisel3.util._

class Decode extends Module {
  val io = IO(new Bundle {
    val inst = Input(UInt(32.W))  
    val rs1_data = Input(SInt(64.W))  
    val rs2_data = Input(SInt(64.W))  
    val rs1_ena = Output(Bool())  
    val rs2_ena = Output(Bool())  
    val rs1_addr = Output(UInt(5.W))  
    val rs2_addr = Output(UInt(5.W))  
    val inst_opcode = Output(UInt(8.W)) 
    val rd_addr = Output(UInt(5.W))   
    val rd_ena = Output(Bool())  
    val op1 = Output(SInt(64.W))  
    val op2 = Output(SInt(64.W)) 
  })
  
  val opcode = io.inst(6, 0)
  val rd = io.inst(11, 7)
  val func3 = io.inst(14, 12)
  val rs1 = io.inst(19, 15)
  val imm = io.inst(31, 20)
  
  //val inst_type = WireDefault(0.U)
  io.inst_opcode := 0.U
  io.rs1_ena := false.B
  io.rs1_addr := 0.U
  io.rs2_ena := false.B
  io.rs2_addr := 0.U
  io.rd_ena := false.B
  io.rd_addr := 0.U
  io.op1 := 0.S
  io.op2 := 0.S
  
  switch( opcode ) {
    is("b0010011".U) { 
      when (func3 === "b000".U) {
        //inst_type := "b10000".U
        io.inst_opcode := Types.addi
        io.rs1_ena := true.B
        io.rs1_addr := rs1
        io.rd_ena := true.B
        io.rd_addr := rd
        io.op1 := io.rs1_data
        io.op2 := imm.asSInt
      }
    }
  }
  
  when (Module.reset.asBool) {
    //inst_type := 0.U
    io.inst_opcode := 0.U
    io.rs1_ena := false.B
    io.rs1_addr := 0.U
    io.rs2_ena := false.B
    io.rs2_addr := 0.U
    io.rd_ena := false.B
    io.rd_addr := 0.U
    io.op1 := 0.S
    io.op2 := 0.S
  }

}

