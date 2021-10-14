package Core.ExuBlock.FU

import Bus.{SimpleBus, SimpleReqBundle, SimpleRespBundle}
import Core.Cache.{CacheReq, CacheResp}
import Core.{Config, FuInPut, FuOutPut}
import chisel3._
import chisel3.util._
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
  val in  = Flipped(ValidIO(new FuInPut))
  val out = ValidIO(new FuOutPut)
  val toMem = new SimpleBus
//  val cachereq  = DecoupledIO(new SimpleReqBundle)
//  val cacheresp = Flipped(ValidIO(new SimpleRespBundle))
  val flush = Input(Bool())
  val spec_issued = Input(Bool())
  val skip = Output(Bool())
  val trapvalid = Input(Bool())
}

class LSU extends Module with Config {

  def genWmask(sizeEncode: UInt): UInt = {
    LookupTree(sizeEncode, List(
      "b00".U -> 0x1.U, //0001 << addr(2:0) 1111 1111
      "b01".U -> 0x3.U, //0011              1111 1111 1111 1111
      "b10".U -> 0xf.U, //1111              1111 1111 1111 1111 1111 1111 1111 1111
      "b11".U -> 0xff.U //11111111
    )).asUInt()
  }

  def genWdata(data: UInt, sizeEncode: UInt): UInt = {
    LookupTree(sizeEncode, List(
      "b00".U -> ZeroExt(data(7, 0) , XLEN),
      "b01".U -> ZeroExt(data(15, 0), XLEN),
      "b10".U -> ZeroExt(data(31, 0), XLEN),
      "b11".U -> ZeroExt(data(63, 0), XLEN)
    ))
  }

  val io  = IO(new LSUIO)
  val uop     = RegInit(io.in.bits.uop)
  val addrReg = RegInit(io.in.bits.src(0))
  when(io.toMem.req.fire()){
    uop     := io.in.bits.uop
    addrReg := io.in.bits.src(0)
  }

  val addr = Mux(io.in.valid, io.in.bits.src(0), 0.U)
  val storedata = io.in.bits.src(1)
  val isStore = LSUOpType.isStore(io.in.bits.uop.ctrl.funcOpType)

  val size = io.in.bits.uop.ctrl.funcOpType(1,0)
  val wdata_align = genWdata(storedata, size) //<< (addr(2, 0) * 8.U)
  val mask_align = genWmask(size) //<< (addr(2, 0))

  io.toMem.req.valid := io.in.valid
  io.toMem.req.bits.addr := addr
  io.toMem.req.bits.isWrite := isStore
  io.toMem.req.bits.wmask   := mask_align
  io.toMem.req.bits.data    := wdata_align
  io.toMem.req.bits.size    := size       // 0: 1byte, 1: 2bytes, 2: 4bytes, 3: 8bytes

  val rdataSel = io.toMem.resp.bits.data
  io.out.bits.res := LookupTree(uop.ctrl.funcOpType, List(
    LSUOpType.lb   -> SignExt(rdataSel(7, 0) , XLEN),
    LSUOpType.lh   -> SignExt(rdataSel(15, 0), XLEN),
    LSUOpType.lw   -> SignExt(rdataSel(31, 0), XLEN),
    LSUOpType.ld   -> SignExt(rdataSel(63, 0), XLEN),
    LSUOpType.lbu  -> ZeroExt(rdataSel(7, 0) , XLEN),
    LSUOpType.lhu  -> ZeroExt(rdataSel(15, 0), XLEN),
    LSUOpType.lwu  -> ZeroExt(rdataSel(31, 0), XLEN)
  ))

  val inst_flushed = RegInit(false.B)
  when(io.flush && io.spec_issued && !io.toMem.resp.valid){
    inst_flushed := true.B
  }.elsewhen(io.toMem.resp.valid){
    inst_flushed := false.B
  }
  io.toMem.resp.ready := true.B


  io.out.valid := io.toMem.resp.valid && !inst_flushed
  io.out.bits.uop := uop
  io.skip := addrReg < 0x80000000L.U
//   when(io.toMem.req.fire()){
//     printf("LSU valid, pc %x, inst %x, addr %x, isStore %d, storedata %x\n", io.in.bits.uop.cf.pc, io.in.bits.uop.cf.instr, addr, isStore, storedata)
//   }
//   when(io.out.valid){
//     printf("cache out, pc %x, inst %x, res %x\n", uop.cf.pc, uop.cf.instr, io.out.bits.res)
//   }

}
