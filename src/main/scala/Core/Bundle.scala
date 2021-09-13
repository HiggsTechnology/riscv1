package Core

import Core.CtrlBlock.IDU.{FuncOpType, FuncType, SrcType1, SrcType2}
import Core.ExuBlock.MemReg.RegWriteIO
import Core.ExuBlock.OrderQueue.OrderQueuePtr
import chisel3._

class Pc_Instr extends Bundle with Config {
  val pc    = Output(UInt(XLEN.W))
  val instr = Output(UInt(INST_WIDTH))
}

class CtrlSignalIO extends Bundle with Config {
  val src1Type     = Output(SrcType1.uwidth)
  val src2Type     = Output(SrcType2.uwidth)
  val funcType     = Output(FuncType.uwidth)
  val funcOpType   = Output(FuncOpType.uwidth)
  val rfSrc        = Vec(2,Output(UInt(5.W))) //src regfile address//logic
  val rfrd         = Output(UInt(5.W))    //rd regfile address
  val rfWen        = Output(Bool())       //regfile write enable
}

class DataSrcIO extends Bundle with Config {
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
  val taken = Output(Bool())
}

class LSU_OUTIO extends Bundle with Config {
  val rdata = Output(UInt(XLEN.W))
}

class EXU_OUTIO extends Bundle with Config {
  val new_pc       = Output(UInt(XLEN.W))
  val bru_valid    = Output(Bool())
  val reg_write_io = new RegWriteIO
}

class LSU2RW extends  Bundle with Config{
  /** r:0, w:1 */
  val valid     = Output(Bool())
  val rready    = Input(Bool())
  val wready    = Input(Bool())
  val is_write  = Output(Bool())
  val addr      = Output(UInt(XLEN.W))
  val rdata     = Input(UInt(XLEN.W))
  val wdata     = Output(UInt(XLEN.W))
  val strb      = Output(UInt(8.W))
}

class IFU2RW extends  Bundle with Config{
  val valid     = Output(Bool())
  val ready     = Input(Bool())
  val pc        = Output(UInt(XLEN.W))
  val rdata     = Input(UInt(XLEN.W))
}

// Micro OP
class MicroOp extends CfCtrl {
  val srcState = Vec(2, Output(SrcState()))
  val psrc = Vec(2, Output(UInt(PhyRegIdxWidth.W)))
  val pdest = Output(UInt(PhyRegIdxWidth.W))
  val old_pdest = Output(UInt(PhyRegIdxWidth.W))//一路返回到freelist
  val OQIdx = Output(new OrderQueuePtr)
}

class FuInPut extends Bundle with Config{
  val src = Vec(2, Output(UInt(XLEN.W)))
  val uop = new MicroOp
  val isSecond = Output(Bool())
}

class FuOutPut extends Bundle with Config{
  val res = Output(UInt(XLEN.W))
  val uop = new MicroOp
  val isSecond = Output(Bool())
}

class CommitIO extends Bundle with Config {
  val pdest = Output(UInt(PhyRegIdxWidth.W))
  val old_pdest = Output(UInt(PhyRegIdxWidth.W))
  val ldest = Output(UInt(5.W))//logic
  val rfWen = Output(Bool())
}
