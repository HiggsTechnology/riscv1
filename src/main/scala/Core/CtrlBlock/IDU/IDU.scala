package Core.CtrlBlock.IDU

import Core.Define.Exceptions
import Core.ExuBlock.FU.CsrOpType
import Core.{CfCtrl, Config, CtrlFlow}
import chisel3._
import chisel3.util._
import RVIInstr._
import chisel3.util.experimental.BoringUtils
import utils.{LookupTree, SignExt, ZeroExt}


class IDUIO extends Bundle {
  val in  = Flipped(Decoupled(new CtrlFlow))
  val out = Decoupled(new CfCtrl)
}

class IDU extends Module with Config{
  val io : IDUIO = IO(new IDUIO)

  private val interruptValid = WireInit(false.B)
  private val instr = io.in.bits.instr
  val (src1Addr, src2Addr, rdAddr) = (instr(19, 15), instr(24, 20), instr(11, 7))
  val decodeList = ListLookup(instr, RVIInstr.defaultInst, RVIInstr.table)
  val instrType :: funcType :: funcOpType :: src1Type :: src2Type :: Nil =
    decodeList.zip(RVIInstr.defaultInst).map{ case(decode, default) => Mux(interruptValid, default, decode)}
  // 中断来临，插入一条默认指令，指令类型是N，是非法指令
  val uimm : UInt = instr(19, 15)

  io.out.valid                := io.in.valid || interruptValid      // 插入中断指令不依赖于IFU的输出
  io.out.bits.cf              := io.in.bits
  io.out.bits.ctrl.rfSrc(0)   := Mux(src1Type === SrcType1.reg, src1Addr, 0.U)  //保证取到的地址均为有效寄存器地址，若无效则置0
  io.out.bits.ctrl.rfSrc(1)   := Mux(src2Type === SrcType2.reg, src2Addr, 0.U)
  io.out.bits.ctrl.rfrd       := Mux(isrfWen(instrType), rdAddr, 0.U)
  io.out.bits.ctrl.funcType   := funcType
  io.out.bits.ctrl.funcOpType := funcOpType
  io.out.bits.ctrl.rfWen      := isrfWen(instrType) && (rdAddr =/= 0.U)

  io.out.bits.ctrl.src1Type := src1Type
  io.out.bits.ctrl.src2Type := src2Type

  private val uimm_ext = Mux((funcType === FuncType.csr) & CsrOpType.isCsri(funcOpType),
    ZeroExt(uimm, XLEN), 0.U
  )

  private val imm = LookupTree(instrType, List(
    InstrI  -> SignExt(instr(31, 20), XLEN),
    InstrS  -> SignExt(Cat(instr(31, 25), instr(11, 7)), XLEN),
    InstrB  -> SignExt(Cat(instr(31), instr(7), instr(30, 25), instr(11, 8), 0.U(1.W)), XLEN),
    InstrU  -> SignExt(Cat(instr(31, 12), 0.U(12.W)), XLEN),//fixed
    InstrJ  -> SignExt(Cat(instr(31), instr(19, 12), instr(20), instr(30, 21), 0.U(1.W)), XLEN)
  ))

  val interruptVec = WireInit(0.U(TrapConfig.InterruptVecWidth.W))
  // 中断向量从CSR模块引出

  io.out.bits.ctrl.interruptVec.foreach(_ := false.B)
  io.out.bits.ctrl.exceptionVec.foreach(_ := false.B)
  // 非法指令异常
  io.out.bits.ctrl.exceptionVec(Exceptions.IllegalInst) := (instrType === InstrN) && !interruptValid && io.in.valid

  io.out.bits.data.imm  := imm
  io.out.bits.data.uimm_ext := uimm_ext

  // 中断来临，让译码阻塞
  io.in.ready := io.out.ready && !interruptValid
}