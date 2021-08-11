package utils

import Core.IDU.{FuncOpType, FuncType, SrcType}
import Core.MemReg.RegWriteIO
import chisel3._
import chisel3.util._

class Pc_Instr extends Bundle with Config {
  val pc    = Input(UInt(XLEN.W))
  val instr = Input(UInt(INST_WIDTH.W))
}

class CtrlSignalIO extends Bundle with Config {
  val src1Type     = Output(SrcType())    
  val src2Type     = Output(SrcType())
  val funcType     = Output(FuncType())
  val funcOpType   = Output(FuncOpType())
  val rfSrc1       = Output(UInt(5.W))    //src regfile address
  val rfSrc2       = Output(UInt(5.W))
  val rfrd         = Output(UInt(5.W))    //rd regfile address
  val rfWen        = Output(Bool())       //regfile write enable
}

class DataSrcIO extends Bundle with Config {
  val src1 = Output(UInt(XLEN.W))
  val src2 = Output(UInt(XLEN.W))
  val imm  = Output(UInt(XLEN.W))
}

class CfCtrl extends Bundle with Config {
  val cf   = Flipped(new Pc_Instr)
  val ctrl = new CtrlSignalIO
  val data = new DataSrcIO
}

class ALU_OUTIO extends Bundle with Config {
  val aluRes = Output(UInt(XLEN.W))
}

class BRU_OUTIO extends Bundle with Config {
  val newPC = Output(UInt(XLEN.W))
  val valid = Output(Bool())
}

class LSU_OUTIO extends Bundle with Config {
  val rdata = Output(UInt(XLEN.W))
}

class EXU_OUTIO extends Bundle with Config {
  val new_pc       = Output(UInt(XLEN.W))
  val bru_valid    = Output(Bool())
  val reg_write_io = new RegWriteIO
}