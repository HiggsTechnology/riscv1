package Core.ExuBlock.FU

import Core.{CfCtrl, Config, LSU2RW, LSU_OUTIO}
import Core.CtrlBlock.IDU.FuncType
import Core.ExuBlock.MemReg.RAMHelper
import chisel3._
import utils.{LookupTree, SignExt, ZeroExt}

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
  val lsu2rw = new LSU2RW
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

  val rdataSel = RegInit(0.U)
  io.lsu2rw.valid := io.valid
  io.lsu2rw.is_write := isStore

  val data_out = RegInit(0.U)
  val strb_out = RegInit(0.U)
  val r_hs = io.valid && io.lsu2rw.rready
  val w_hs = io.valid && io.lsu2rw.wready
  val size = io.in.ctrl.funcOpType(1,0)
  when(r_hs){
    rdataSel  := io.lsu2rw.rdata //read data in
  }
  when(w_hs){
    strb_out  := genWmask(addr, size)
    data_out  := genWdata(storedata, size)
  }
  io.lsu2rw.addr := Mux( r_hs||w_hs , addr , 0.U )
  io.lsu2rw.strb := strb_out
  io.lsu2rw.wdata := data_out


  io.out.rdata := LookupTree(io.in.ctrl.funcOpType, List(
    LSUOpType.lb   -> SignExt(rdataSel(7, 0) , XLEN),
    LSUOpType.lh   -> SignExt(rdataSel(15, 0), XLEN),
    LSUOpType.lw   -> SignExt(rdataSel(31, 0), XLEN),
    LSUOpType.ld   -> SignExt(rdataSel(63, 0), XLEN),
    LSUOpType.lbu  -> ZeroExt(rdataSel(7, 0) , XLEN),
    LSUOpType.lhu  -> ZeroExt(rdataSel(15, 0), XLEN),
    LSUOpType.lwu  -> ZeroExt(rdataSel(31, 0), XLEN)
  ))


}
