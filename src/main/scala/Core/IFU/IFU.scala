package Core.IFU

import Core.ExuBlock.MemReg.RAMHelper
import Core.{BRU_OUTIO, Config, IFU2RW, Pc_Instr}
import chisel3._
import chisel3.util._
import utils.IFU2RW

class IFUIO extends Bundle {
  val in  = Flipped(new BRU_OUTIO)  //branch
  val out =  Valid(new Pc_Instr)
  val ifu2rw = new IFU2RW
}

class IFU extends Module with Config {
  val io = IO(new IFUIO)
  val pc = RegInit(PC_START.U(XLEN.W))

  // valid暂时恒为true，不停地取指令
  // todo: 将valid与流水线的stall关联
  io.ifu2rw.valid   := true.B
  io.ifu2rw.pc      := pc

  pc                := Mux(io.in.valid, io.in.new_pc, pc + 4.U)
  val rdata         = io.ifu2rw.rdata
  val inst          = Mux(pc(2), rdata(63,32), rdata(31,0))
  io.out.bits.pc    := pc
  io.out.bits.instr := inst
  // 告知下一个模块，数据可用
  io.out.valid      := io.ifu2rw.ready


}