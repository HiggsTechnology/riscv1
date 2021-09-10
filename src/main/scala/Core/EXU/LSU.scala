package Core.EXU

import Core.Config.Config
import Core.IDU.FuncType
import Core.MemReg.RAMHelper
import chisel3._
import chisel3.util.Valid
import utils.{CfCtrl, LSU2RW, LSU_OUTIO, LookupTree, SignExt, ZeroExt}

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

class LSUIO(use_axi: Boolean = false) extends Bundle with Config {
  val in      : Valid[CfCtrl]     = Flipped(Valid(new CfCtrl))
  val out     : Valid[LSU_OUTIO]  = Valid(new LSU_OUTIO)
  val lsu2rw  : LSU2RW            = if (use_axi) new LSU2RW else null
}

class LSU(use_axi: Boolean) extends Module with Config {
  def genWmask(sizeEncode: UInt): UInt = {
    LookupTree(sizeEncode, List(
      "b00".U -> BigInt(0xff).U(XLEN.W), //0001 << addr(2:0)
      "b01".U -> BigInt(0xffff).U(XLEN.W), //0011
      "b10".U -> BigInt(0xffffffffL).U(XLEN.W), //1111
      "b11".U -> (BigInt(Long.MaxValue) * 2 + 1).U(XLEN.W) //11111111
    )).asUInt()
  }
  // copy from axi branch written by BigZyb
  def genStrb(sizeEncode: UInt): UInt = {
    LookupTree(sizeEncode, List(
      "b00".U -> 0x1.U, //0001 << addr(2:0) 1111 1111
      "b01".U -> 0x3.U, //0011              1111 1111 1111 1111
      "b10".U -> 0xf.U, //1111              1111 1111 1111 1111 1111 1111 1111 1111
      "b11".U -> 0xff.U //11111111
    )) .asUInt()
  }

  def genWdata(data: UInt, sizeEncode: UInt): UInt = {
    LookupTree(sizeEncode, List(
      "b00".U -> ZeroExt(data(7, 0) , XLEN),
      "b01".U -> ZeroExt(data(15, 0), XLEN),
      "b10".U -> ZeroExt(data(31, 0), XLEN),
      "b11".U -> ZeroExt(data(63, 0), XLEN)
    ))
  }

  val io : LSUIO = IO(new LSUIO(use_axi))

  private val addr_wire = Mux(io.in.valid, io.in.bits.data.src1 + io.in.bits.data.imm, 0.U)
  private val storedata = io.in.bits.data.src2
  private val isStore = LSUOpType.isStore(io.in.bits.ctrl.funcOpType)
  private val rdataSel = WireInit(0.U)
  if (use_axi) {
    // 所有在读写完成之后使用信号都要放进寄存器里，比如addr
    val addr_reg = RegInit(0.U)
    val addr = Mux(io.in.valid, addr_wire, addr_reg)
    when (io.in.valid) {
      addr_reg := addr_wire
    }
    val rdata  = io.lsu2rw.rdata //read data in
    rdataSel  := (rdata >> (addr(2, 0) * 8.U)).asUInt()

    val data_out = WireInit(0.U)
    val strb_out = WireInit(0.U)
    val size = io.in.bits.ctrl.funcOpType(1,0)

    strb_out  := genStrb(size)
    data_out  := genWdata(storedata, size)

    io.lsu2rw.valid := io.in.valid
    io.lsu2rw.is_write := isStore
    io.lsu2rw.addr  := addr
    io.lsu2rw.wstrb := strb_out << addr(2, 0)
    io.lsu2rw.wdata := data_out << (addr(2,0) << 3.U)
    io.lsu2rw.size  := size
//    when (io.in.valid && isStore)
    when (io.in.valid && !isStore) {
      printf("lsu: addr: %x, data.src1: %x, data.imm: %x\n", addr, io.in.bits.data.src1, io.in.bits.data.imm);
    }
    printf("lsu: addr: %x, data.src1: %x, data.imm: %x\n", addr, io.in.bits.data.src1, io.in.bits.data.imm);

  }
  else {
    // 通过参数方式解决二者矛盾
    val ram = Module(new RAMHelper)
    ram.io.clk := clock
    ram.io.en := io.in.valid
    //Load
    val idx = (addr_wire - PC_START.U) >> 3
    ram.io.rIdx := idx
    val rdata = ram.io.rdata
    rdataSel := (rdata >> (addr_wire(2, 0) * 8.U)).asUInt()
    ram.io.wIdx := idx
    ram.io.wen  := (io.in.bits.ctrl.funcType === FuncType.lsu) & isStore

    val size = io.in.bits.ctrl.funcOpType(1,0)
    val wdata_align = genWdata(storedata, size) << (addr_wire(2, 0) * 8.U)
    val mask_align = genWmask(size) << (addr_wire(2, 0) * 8.U)
    ram.io.wdata := wdata_align
    ram.io.wmask := mask_align
  }

  io.out.bits.rdata := LookupTree(io.in.bits.ctrl.funcOpType, List(
    LSUOpType.lb   -> SignExt(rdataSel(7, 0) , XLEN),
    LSUOpType.lh   -> SignExt(rdataSel(15, 0), XLEN),
    LSUOpType.lw   -> SignExt(rdataSel(31, 0), XLEN),
    LSUOpType.ld   -> SignExt(rdataSel(63, 0), XLEN),
    LSUOpType.lbu  -> ZeroExt(rdataSel(7, 0) , XLEN),
    LSUOpType.lhu  -> ZeroExt(rdataSel(15, 0), XLEN),
    LSUOpType.lwu  -> ZeroExt(rdataSel(31, 0), XLEN)
  ))

  // IO 连线
  if(use_axi) {
    io.out.valid := io.lsu2rw.ready
  } else {
    io.out.valid := io.in.valid
  }
  when (io.in.valid) {
    printf("lsu enable\n");
  }


}
