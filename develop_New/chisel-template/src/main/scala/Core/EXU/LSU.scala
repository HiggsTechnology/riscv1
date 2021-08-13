package Core.EXU

import Core.IDU.FuncType
import Core.MemReg.RAMHelper
import chisel3._
import chisel3.util._
import utils.{CfCtrl, Config, LSU_OUTIO, LookupTree, SignExt, ZeroExt}

object LSUOpType { 
  def lb   = "b0000000".U
  def lh   = "b0000001".U
  def lw   = "b0000010".U
  def ld   = "b0000011".U
  def lbu  = "b0000100".U
  def lhu  = "b0000101".U
  def lwu  = "b0000110".U
  def sb   = "b0001000".U
  def sh   = "b0001001".U
  def sw   = "b0001010".U
  def sd   = "b0001011".U

  def lr   = "b0100000".U
  def sc   = "b0100001".U

  def isStore(func: UInt): Bool = func(3)
  def isLoad(func: UInt): Bool = !isStore(func)
  def isLR(func: UInt): Bool = func === lr
  def isSC(func: UInt): Bool = func === sc
  def needMemRead(func: UInt): Bool  = isLoad(func) || isLR(func)
  def needMemWrite(func: UInt): Bool = isStore(func) || isSC(func)
}

class LSUIO extends Bundle with Config {
  val valid = Input(Bool())
  val in    = Flipped(new CfCtrl)
  val out   = new LSU_OUTIO
}

class LSU extends Module with Config {

  def genWmask(addr: UInt, sizeEncode: UInt): UInt = {
    (LookupTree(sizeEncode, List(
      "b00".U -> BigInt(0xff).U(XLEN.W), //0001 << addr(2:0)
      "b01".U -> BigInt(0xffff).U(XLEN.W), //0011
      "b10".U -> BigInt(0xffffffffL).U(XLEN.W), //1111
      "b11".U -> (BigInt(Long.MaxValue) * 2 + 1).U(XLEN.W) //11111111
    )) ).asUInt()
  }

  def genWdata(data: UInt, sizeEncode: UInt): UInt = {
    LookupTree(sizeEncode, List(
      "b00".U -> ZeroExt(data(7, 0) , XLEN),
      "b01".U -> ZeroExt(data(15, 0), XLEN),
      "b10".U -> ZeroExt(data(31, 0), XLEN),
      "b11".U -> ZeroExt(data(63, 0), XLEN)
    ))
  }


  val io = IO(new LSUIO)
  val addr = Mux(io.valid, io.in.data.src1 + io.in.data.imm, 0.U)
  val storedata = io.in.data.src2
  val isStore = LSUOpType.isStore(io.in.ctrl.funcOpType)
  val ram = Module(new RAMHelper)
  ram.io.clk := clock
  ram.io.en := io.valid
  
  //Load
  val idx = (addr - pc_start.U) >> 3
  ram.io.rIdx := idx
  val rdata = ram.io.rdata

  // val rdataSel = LookupTree(addr(2, 0), List(
  //   "b000".U -> rdata(63, 0),
  //   "b001".U -> rdata(63, 8),
  //   "b010".U -> rdata(63, 16),
  //   "b011".U -> rdata(63, 24),
  //   "b100".U -> rdata(63, 32),
  //   "b101".U -> rdata(63, 40),
  //   "b110".U -> rdata(63, 48),
  //   "b111".U -> rdata(63, 56)
  // ))
  val rdataSel = rdata >> (addr(2, 0) * 8.U)
  io.out.rdata := LookupTree(io.in.ctrl.funcOpType, List(
    LSUOpType.lb   -> SignExt(rdataSel(7, 0) , XLEN),
    LSUOpType.lh   -> SignExt(rdataSel(15, 0), XLEN),
    LSUOpType.lw   -> SignExt(rdataSel(31, 0), XLEN),
    LSUOpType.ld   -> SignExt(rdataSel(63, 0), XLEN),
    LSUOpType.lbu  -> ZeroExt(rdataSel(7, 0) , XLEN),
    LSUOpType.lhu  -> ZeroExt(rdataSel(15, 0), XLEN),
    LSUOpType.lwu  -> ZeroExt(rdataSel(31, 0), XLEN)
  ))

  //Store
  ram.io.wIdx := idx
  ram.io.wen  := (io.in.ctrl.funcType === FuncType.lsu) & isStore

  val size = io.in.ctrl.funcOpType(1,0)
  val wdata_align = genWdata(storedata, size) << (addr(2, 0) * 8.U)
  val mask_align = genWmask(addr, size) << (addr(2, 0) * 8.U)
  ram.io.wdata := wdata_align
  ram.io.wmask := mask_align 
  
  // //Align
  // val addrAligned = LookupTree(io.in.funcOpType(1,0), List(
  //   "b00".U   -> true.B,            //b
  //   "b01".U   -> (addr(0) === 0.U),   //h
  //   "b10".U   -> (addr(1,0) === 0.U), //w
  //   "b11".U   -> (addr(2,0) === 0.U)  //d
  // ))
  // io.out.loadAddrMisaligned := valid && !isStore && !addrAligned
  // io.out.storeAddrMisaligned := valid && isStore && !addrAligned

}
