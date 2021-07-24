package oscpu

import chisel3._
import chisel3.Driver

class RiscvCpu extends Module {
  val io = IO(new Bundle {
    val inst = Input(UInt(32.W))         // instrucion
    val inst_addr = Output(UInt(64.W))   // pc
    val inst_ena = Output(Bool())     // ready to fetch the instuction
    val reg_out = Output(Vec(32, SInt(64.W))) // use for debug
  })
  
  val fetch = Module(new Fetch())
  val decode = Module(new Decode())
  val execute = Module(new Execute())
  val regfile = Module(new Regfile())

  io.inst_addr := fetch.io.inst_addr
  io.inst_ena := fetch.io.inst_ena
  
  decode.io.inst := io.inst
  
  decode.io.rs1_data := regfile.io.rs1_data
  decode.io.rs2_data := regfile.io.rs2_data
  regfile.io.rd_addr := decode.io.rd_addr
  regfile.io.rd_ena := decode.io.rd_ena
  regfile.io.rs1_ena := decode.io.rs1_ena
  regfile.io.rs1_addr := decode.io.rs1_addr
  regfile.io.rs2_ena := decode.io.rs2_ena
  regfile.io.rs2_addr := decode.io.rs2_addr
  
  execute.io.inst_opcode := decode.io.inst_opcode
  execute.io.op1 := decode.io.op1
  execute.io.op2 := decode.io.op2
  
  regfile.io.rd_data := execute.io.rd_data
  
  io.reg_out := regfile.io.reg_out
  
}

/**
 * An object extending App to generate the Verilog code.
 */
object RiscvCpu extends App {
  (new chisel3.stage.ChiselStage).emitVerilog(new RiscvCpu())
}
