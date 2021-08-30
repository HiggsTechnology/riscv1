package Core.IDU

import Core.Config.Config
import Core.EXU.CsrOpType
import chisel3._
import chisel3.util._
import utils.RVIInstr._
import utils.{CfCtrl, LookupTree, Pc_Instr, RVIInstr, SignExt, ZeroExt}


class IDUIO extends Bundle {
  val in  = new Pc_Instr
  val out = new CfCtrl
}

class IDU extends Module with Config{
  val io : IDUIO = IO(new IDUIO)
  private val instr = io.in.instr
  val (src1Addr, src2Addr, rdAddr) = (instr(19, 15), instr(24, 20), instr(11, 7))
  val instrType :: funcType :: funcOpType :: src1Type :: src2Type :: Nil
    = ListLookup(instr, RVIInstr.defaultTable, RVIInstr.table)
  val uimm : UInt = instr(19, 15)

  io.out.cf.pc         := io.in.pc
  io.out.cf.instr      := io.in.instr
  io.out.ctrl.rfSrc1     := Mux(src1Type === SrcType1.reg, src1Addr, 0.U)  //保证取到的地址均为有效寄存器地址，若无效则置0
  io.out.ctrl.rfSrc2     := Mux(src2Type === SrcType2.reg, src2Addr, 0.U)
  io.out.ctrl.rfrd       := Mux(isrfWen(instrType), rdAddr, 0.U)
  io.out.ctrl.funcType   := funcType
  io.out.ctrl.funcOpType := funcOpType
  io.out.ctrl.rfWen      := isrfWen(instrType)

  io.out.ctrl.src1Type := src1Type
  io.out.ctrl.src2Type := src2Type

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

  io.out.data.imm  := imm
  io.out.data.uimm_ext := uimm_ext
  io.out.data.src1 := DontCare
  io.out.data.src2 := DontCare
}