package Core.IDU

import chisel3._
import chisel3.util._
import utils.{CfCtrl, Config, LookupTree, Pc_Instr, RVIInstr, SignExt}


class IDUIO extends Bundle {
  val in  = new Pc_Instr
  val out = new CfCtrl
}

class IDU extends Module with Config with InstrType{
  val io = IO(new IDUIO)
  val instr = io.in.instr
  val (src1Addr, src2Addr, rdAddr) = (instr(19, 15), instr(24, 20), instr(11, 7))
  val instrType :: funcType :: funcOpType :: Nil = ListLookup(instr, RVIInstr.defaultTable, RVIInstr.table)
  
  io.out.cf.pc         := io.in.pc
  io.out.cf.instr      := io.in.instr
  io.out.ctrl.rfSrc1     := Mux(io.out.ctrl.src1Type === SrcType.pc, 0.U, src1Addr)  //保证取到的地址均为有效寄存器地址，若无效则置0
  io.out.ctrl.rfSrc2     := Mux(io.out.ctrl.src2Type === SrcType.reg, src2Addr, 0.U) 
  io.out.ctrl.rfrd       := Mux(isrfWen(instrType), rdAddr, 0.U)
  io.out.ctrl.funcType   := funcType
  io.out.ctrl.funcOpType := funcOpType
  io.out.ctrl.rfWen      := isrfWen(instrType)
  
  val SrcTypeTable = List(
    InstrI -> (SrcType.reg, SrcType.imm),
    InstrR -> (SrcType.reg, SrcType.reg),
    InstrS -> (SrcType.reg, SrcType.reg),
    InstrB -> (SrcType.reg, SrcType.reg),
    InstrU -> (SrcType.pc , SrcType.imm),
    InstrJ -> (SrcType.pc , SrcType.imm),
    InstrN -> (SrcType.pc , SrcType.imm)
  )
  io.out.ctrl.src1Type := LookupTree(instrType, SrcTypeTable.map(p => (p._1, p._2._1)))
  io.out.ctrl.src2Type := LookupTree(instrType, SrcTypeTable.map(p => (p._1, p._2._2)))

  val imm = LookupTree(instrType, List(
    InstrI  -> SignExt(instr(31, 20), XLEN),
    InstrS  -> SignExt(Cat(instr(31, 25), instr(11, 7)), XLEN),
    InstrB  -> SignExt(Cat(instr(31), instr(7), instr(30, 25), instr(11, 8), 0.U(1.W)), XLEN),
    InstrU  -> SignExt(Cat(instr(31, 12), 0.U(12.W)), XLEN),//fixed
    InstrJ  -> SignExt(Cat(instr(31), instr(19, 12), instr(20), instr(30, 21), 0.U(1.W)), XLEN)
  ))
  
  // val imm = List(
  //   (InstrI, SignExt(instr(31, 20), XLEN)),
  //   (InstrS, SignExt(Cat(instr(31, 25), instr(11, 7)), XLEN)),
  //   (InstrB, SignExt(Cat(instr(31), instr(7), instr(30, 25), instr(11, 8), 0.U(1.W)), XLEN)),
  //   (InstrU, SignExt(Cat(instr(31, 12), 0.U(12.W)), XLEN)),//fixed
  //   (InstrJ, SignExt(Cat(instr(31), instr(19, 12), instr(20), instr(30, 21), 0.U(1.W)), XLEN))
  // )
  // val res : UInt = Wire(UInt(XLEN.W))
  // res := MuxLookup(instrType, 0.U, imm)

  io.out.data.imm  := imm
  io.out.data.src1 := DontCare
  io.out.data.src2 := DontCare
}