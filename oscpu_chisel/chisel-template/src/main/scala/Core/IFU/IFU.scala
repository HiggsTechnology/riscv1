package Core.IFU

import Core.MemReg.RAMHelper
import chisel3._
import utils.{BRU_OUTIO, Config, Pc_Instr}

class IFUIO extends Bundle {
  val in  = Flipped(new BRU_OUTIO)  //branch
  val out = Flipped(new Pc_Instr)
}

class IFU extends Module with Config {
  val io = IO(new IFUIO)
  val pc = RegInit(pc_start.U(XLEN.W))
  pc        := Mux(io.in.valid, io.in.newPC, pc + 4.U)
  io.out.pc := pc

  val ram = Module(new RAMHelper)
  ram.io.clk := clock
  ram.io.en  := !reset.asBool()

  val idx = (pc - pc_start.U) >> 3

  ram.io.rIdx := idx
  val rdata = ram.io.rdata
  ram.io.wIdx := DontCare
  ram.io.wen  := false.B
  ram.io.wdata := DontCare
  ram.io.wmask := DontCare

  io.out.instr := Mux(pc(2),rdata(63,32),rdata(31,0))
  printf("Print during simulation: io.out.pc is %x\n", io.out.pc)
  printf("Print during simulation: io.out.instr is %x\n", io.out.instr)
}