package utils

import Core.Config.Config
import Core.IDU.{FuncOpType, FuncType, SrcType1, SrcType2}
import Core.MemReg.RegWriteIO
import chisel3._
import chisel3.util._
import utils.BasicIOType.OutUInt

class Pc_Instr extends Bundle with Config {
  val pc    = Output(UInt(XLEN.W))
  val instr = Output(UInt(INST_WIDTH))
}

class CtrlSignalIO extends Bundle with Config {
  val src1Type     = Output(SrcType1.uwidth)
  val src2Type     = Output(SrcType2.uwidth)
  val funcType     = Output(FuncType.uwidth)
  val funcOpType   = Output(FuncOpType.uwidth)
  val rfSrc1       = Output(UInt(5.W))    //src regfile address
  val rfSrc2       = Output(UInt(5.W))
  val rfrd         = Output(UInt(5.W))    //rd regfile address
  val rfWen        = Output(Bool())       //regfile write enable
}

class DataSrcIO extends Bundle with Config {
  val src1 = Output(UInt(XLEN.W))
  val src2 = Output(UInt(XLEN.W))
  val imm  = Output(UInt(XLEN.W))
  val uimm_ext = Output(UInt(XLEN.W))
}

class CfCtrl extends Bundle with Config {
  val cf   = new Pc_Instr
  val ctrl = new CtrlSignalIO
  val data = new DataSrcIO
}

class ALU_OUTIO extends Bundle with Config {
  val aluRes = Output(UInt(XLEN.W))
}

class BRU_OUTIO extends Bundle with Config {
  val new_pc = Output(UInt(XLEN.W))
  val ena = Output(Bool())
}

class LSU_OUTIO extends Bundle with Config {
  val rdata = Output(UInt(XLEN.W))
}

class EXU_OUTIO extends Bundle with Config {
  val new_pc       = Output(UInt(XLEN.W))
  val bru_valid    = Output(Bool())
  val reg_write_io = new RegWriteIO
}

// 简单的同步总线，valid表明Output数据有效，ready表明Input数据有效
class SimpleSyncBus extends  Bundle with Config{
  /** r:0, w:1 */
  val valid     = Output(Bool())
  val ready     = Input(Bool())
  val is_write  = Output(Bool())
  val addr      = Output(UInt(XLEN.W))
  val rdata     = Input(UInt(XLEN.W))
  val wdata     = Output(UInt(XLEN.W))
  val wstrb     = Output(UInt(8.W))
  val size     = Output(UInt(3.W))
}

class IFU2RW extends  Bundle with Config{
  val valid     = Output(Bool())
  val ready     = Input(Bool())
  val pc        = Output(UInt(XLEN.W))
  val rdata     = Input(UInt((XLEN * 4).W))
}

// 指令提交计数器，指示当前提交指令的数量
class InstInc extends Bundle {
  val instIncWidth : Int = 3
  val value : UInt = OutUInt(instIncWidth)
}