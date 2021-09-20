package Core.IFU

import Core.Config
import chisel3._
import chisel3.util._
import utils._

object BRtype {
  def B = "b00".U  // branch
  def R = "b01".U  // jalr
  def N = "b10".U  // err instr
  def J = "b11".U  // jal

  def apply() = UInt(2.W)
}

class PreDecode extends Module with Config{
  val io = IO(new Bundle{
    val instr = Input(UInt(INST_WIDTH))
    val is_br = Output(Bool())
    val offset = Output(UInt(XLEN.W))
    val br_type = Output(BRtype())
    val br_taken= Output(Bool())
  })

  val instr = io.instr
  // val imm = Wire(UInt(XLEN.W))
  when(instr(6,4)==="b110".U){
    val brtype = instr(3,2)
    io.br_type := brtype
    io.offset:= 4.U
    io.is_br    := false.B
    io.br_taken := false.B
    switch(brtype){
      is(BRtype.J){
        io.offset   := SignExt(Cat(instr(31), instr(19,12), instr(20), instr(30,21), 0.U(1.W)), XLEN)
        io.is_br    := true.B
        io.br_taken := true.B
      }
      is(BRtype.R){
        io.is_br    := true.B
        io.offset   := 4.U
        io.br_taken := true.B
      }
      is(BRtype.B){
        io.is_br   := true.B
        when(instr(31)){
          io.offset   := SignExt(Cat(instr(31), instr(7), instr(30,25), instr(11,8), 0.U(1.W)), XLEN)
          io.br_taken := true.B
        }.otherwise{
          io.offset   := 4.U
          io.br_taken := false.B
        }
      }
      is(BRtype.N){
        io.offset:= 4.U
        io.is_br    := false.B
        io.br_taken := false.B
      }
    }
  }.otherwise{
    io.is_br    := false.B
    io.offset   := 4.U
    io.br_taken := false.B
    io.br_type := DontCare
  }
  // io.offset := DontCare


}