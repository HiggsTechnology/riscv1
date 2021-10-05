package Core

import Core.CtrlBlock.IDU.{FuncOpType, FuncType, SrcType1, SrcType2}
import Core.CtrlBlock.ROB.ROBPtr
import Core.ExuBlock.MemReg.RegWriteIO
import chisel3._
import utils.{OutBool, OutUInt}

/**
 * 真正的只有 PC 和 Instruction
 */
class PcInst extends Bundle with Config {
  val pc    : UInt = OutUInt(XLEN)
  val inst  : UInt = OutUInt(INST_WIDTH)
}

class CtrlFlow extends Bundle with Config {
  val pc    = Output(UInt(XLEN.W))
  val instr = Output(UInt(INST_WIDTH))
  val is_br = Output(Bool())
  val br_taken = Output(Bool())//todo:bru里根据此判断是否mispredict
  val gshare_idx = Output(UInt(ghrBits.W))
  val gshare_pred = Output(Bool())
  val pht_pred = Output(Bool())
  val btbtarget = Output(UInt(XLEN.W))
  val rastarget = Output(UInt(XLEN.W))
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
  val imm      = Output(UInt(XLEN.W))
  val uimm_ext = Output(UInt(XLEN.W))
}

class CfCtrl extends Bundle with Config {
  val cf   = new CtrlFlow
  val ctrl = new CtrlSignalIO
  val data = new DataSrcIO
  val interruptVec = Vec(TrapConfig.InterruptVecWidth, OutBool())
  val exceptionVec = Vec(TrapConfig.ExceptionVecWidth, OutBool())
}

class ALU_OUTIO extends Bundle with Config {
  val aluRes = Output(UInt(XLEN.W))
}

class RedirectIO extends Bundle with Config {
  val new_pc = Output(UInt(XLEN.W))
  val mispred = Output(Bool())
  val ROBIdx = Output(new ROBPtr)
}

class BPU_Update extends Bundle with Config {
  val pc = Output(UInt(XLEN.W))
  val new_pc = Output(UInt(XLEN.W))
  val taken  = Output(Bool())
  val is_jalr = Output(Bool())
  val is_ret = Output(Bool())
  val is_call = Output(Bool())
  val is_B = Output(Bool())
  val gshare_idx = Output(UInt(ghrBits.W))
  val gshare_mispred = Output(Bool())
  val pht_mispred = Output(Bool())//
  val btb_update = Output(Bool())
  val ras_flush = Output(Bool())
}

class LSU_OUTIO extends Bundle with Config {
  val rdata = Output(UInt(XLEN.W))
}

class EXU_OUTIO extends Bundle with Config {
  val new_pc       = Output(UInt(XLEN.W))
  val bru_valid    = Output(Bool())
  val reg_write_io = new RegWriteIO
}

class LSU2MemIO extends  Bundle with Config{
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

class IFU2MemIO extends  Bundle with Config{
  val valid     = Output(Bool())
  val ready     = Input(Bool())
  val addr        = Output(UInt(XLEN.W))
  val rdata     = Input(UInt((XLEN * 4).W))
}

// Micro OP
class MicroOp extends CfCtrl {
  val srcState  = Vec(2, Output(SrcState()))
  val psrc      = Vec(2, Output(UInt(PhyRegIdxWidth.W)))
  val pdest     = Output(UInt(PhyRegIdxWidth.W))
  val old_pdest = Output(UInt(PhyRegIdxWidth.W))//一路返回到freelist
  val ROBIdx     = Output(new ROBPtr)
}

class FuInPut extends Bundle with Config{
  val src      = Vec(2, Output(UInt(XLEN.W)))
  val uop      = new MicroOp
}

class FuOutPut extends Bundle with Config{
  val res      = Output(UInt(XLEN.W))
  val uop      = new MicroOp
}

class ExuCommit extends Bundle with Config {
  val ROBIdx    = Output(new ROBPtr)
  val pdest     = Output(UInt(PhyRegIdxWidth.W))
  val res       = Output(UInt(XLEN.W))
  val skip      = OutBool()
}

class CommitIO extends Bundle with Config {
  val pdest     = Output(UInt(PhyRegIdxWidth.W))
  val old_pdest = Output(UInt(PhyRegIdxWidth.W))
  val ldest     = Output(UInt(5.W))//logic
  val rfWen     = Output(Bool())
}

class MisPredictIO extends Bundle with Config {
  val misROBIdx = Output(new ROBPtr)
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

class CsrCommitIO extends Bundle {
  val priviledgeMode = UInt(2.W)
  val mstatus = UInt(64.W)
  val sstatus = UInt(64.W)
  val mepc = UInt(64.W)
  val sepc = UInt(64.W)
  val mtval = UInt(64.W)
  val stval = UInt(64.W)
  val mtvec = UInt(64.W)
  val stvec = UInt(64.W)
  val mcause = UInt(64.W)
  val scause = UInt(64.W)
  val satp = UInt(64.W)
  val mip = UInt(64.W)
  val mie = UInt(64.W)
  val mscratch = UInt(64.W)
  val sscratch = UInt(64.W)
  val mideleg = UInt(64.W)
  val medeleg = UInt(64.W)
}