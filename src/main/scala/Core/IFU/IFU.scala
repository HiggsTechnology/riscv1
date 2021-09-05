package Core.IFU

import Core.Config.Config
import Core.MemReg.RAMHelper
import chisel3._
import chisel3.util._
import utils.{BRU_OUTIO, IFU2RW, Pc_Instr}

class IFUIO extends Bundle {
  val bru  = Flipped(new BRU_OUTIO)  //branch
  val out =  Valid(new Pc_Instr)
  val stall = Input(Bool())
  val ifu2rw = new IFU2RW
}

class IFU extends Module with Config {
  val io = IO(new IFUIO)
  val pc = RegInit(PC_START.U(XLEN.W))
  val inst = WireInit(0.U)
  when(io.ifu2rw.ready) {
    pc := Mux(io.bru.valid, io.bru.new_pc, pc + 4.U)
  }
//  io.ifu2rw.valid   := !io.stall
  io.ifu2rw.valid   := true.B
  io.ifu2rw.pc      := pc
  printf("pc: %x, inst: %x\n", pc, inst);
  val rdata         = io.ifu2rw.rdata
  when(io.ifu2rw.ready) {
    inst := MuxLookup(pc(4,2), 0.U, Array(
      0.U -> rdata(32*1-1, 32*0),
      1.U -> rdata(32*2-1, 32*1),
      2.U -> rdata(32*3-1, 32*2),
      3.U -> rdata(32*4-1, 32*3),
      4.U -> rdata(32*5-1, 32*4),
      5.U -> rdata(32*6-1, 32*5),
      6.U -> rdata(32*7-1, 32*6),
      7.U -> rdata(32*8-1, 32*7),
    ))
  }

  io.out.bits.pc    := pc
  io.out.bits.instr := inst
  // 告知下一个模块，数据可用
  io.out.valid      := io.ifu2rw.ready

}