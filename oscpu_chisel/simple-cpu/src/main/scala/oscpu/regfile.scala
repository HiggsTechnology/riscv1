package oscpu

import chisel3._
import chisel3.util._

class Regfile extends Module {
  val io = IO(new Bundle {
    val rd_addr = Input(UInt(5.W))   
    val rd_data = Input(SInt(64.W))  
    val rd_ena = Input(Bool())  
    val rs1_ena = Input(Bool())  
    val rs2_ena = Input(Bool())  
    val rs1_addr = Input(UInt(5.W))  
    val rs2_addr = Input(UInt(5.W))  
    val rs1_data = Output(SInt(64.W))  
    val rs2_data = Output(SInt(64.W))  
    val reg_out = Output(Vec(32, SInt(64.W))) // use for debug
  })
  
  val regs = RegInit(VecInit(Seq.fill(32)(0.S(64.W))))
  
  when ( io.rd_ena && (io.rd_addr =/= "b00000".U) ) {
    regs(io.rd_addr) := io.rd_data
  }
  
  io.rs1_data := Mux(io.rs1_ena, regs(io.rs1_addr), 0.S)
  io.rs2_data := Mux(io.rs2_ena, regs(io.rs2_addr), 0.S)
  
  io.reg_out := regs
}

