package Core.IFU

import Core.ExuBlock.MemReg.RAMHelper
import Core.{BRU_OUTIO, Config, IFU2RW, Pc_Instr}
import chisel3._
import chisel3.util._

class IFUIO extends Bundle {
  val in  = Flipped(ValidIO(new BRU_OUTIO))  //branch
  val out = Vec(2, DecoupledIO(new Pc_Instr))
  val ifu2rw = new IFU2RW
}

class IFU extends Module with Config {
  val io = IO(new IFUIO)
  val pc = RegInit(PC_START.U(XLEN.W))

  object IFUState {
    val continue :: stall :: Nil = Enum(2)
  }

  val ifuState = RegInit(IFUState.continue)

  //RAMHelper
  val ram1 = Module(new RAMHelper)
  ram1.io.clk := clock
  ram1.io.en  := true.B
  val idx1 = (pc - PC_START.U) >> 3
  ram1.io.rIdx := idx1
  val rdata1 = ram1.io.rdata
  ram1.io.wIdx := DontCare
  ram1.io.wen  := false.B
  ram1.io.wdata := DontCare
  ram1.io.wmask := DontCare

  io.out(0).bits.pc  := pc
  io.out(0).bits.instr := Mux(pc(2),rdata1(63,32),rdata1(31,0))

  val ram2 = Module(new RAMHelper)
  ram2.io.clk := clock
  ram2.io.en  := true.B
  val idx2 = (pc + 4.U - PC_START.U) >> 3
  ram2.io.rIdx := idx2
  val rdata2 = ram2.io.rdata
  ram2.io.wIdx := DontCare
  ram2.io.wen  := false.B
  ram2.io.wdata := DontCare
  ram2.io.wmask := DontCare

  io.out(1).bits.pc  := pc + 4.U
  io.out(1).bits.instr := Mux((pc+4.U)(2),rdata2(63,32),rdata2(31,0))

  //状态机
  def isJump(x: UInt) :Bool = {
    val opcode = x(6,0)
    opcode === b1101111.U || opcode === b1100111.U || opcode === b1100011.U
  }
  //取指令时
  when(ifuState === IFUState.continue){
    when(isJump(io.out(0).bits.instr)){
      io.out(0).vaild := true.B
      io.out(1).vaild := false.B
      ifuState := IFUState.stall
    }.elsewhen(isJump(io.out(1).bits.instr)){
      io.out(0).vaild := true.B
      io.out(1).vaild := true.B
      ifuState := IFUState.stall
    }.otherwise{
      io.out(0).vaild := true.B
      io.out(1).vaild := true.B
    }
    pc := pc + PopCount(io.out.map(_.fire)) * 4.U//指向下一条指令
  //等待分支结果时
  }.elsewhen(ifuState === IFUState.stall){
    io.out(0).vaild := false.B
    io.out(1).vaild := false.B
    when(io.in.valid){
      pc := Mux(io.in.bits.taken, io.in.bits.new_pc, pc)
      ifuState := IFUState.continue
    }
  }

}