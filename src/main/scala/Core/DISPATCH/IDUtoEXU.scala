package Core.DISPATCH

import Core.Config.Config
import Core.IDU.{SrcType1, SrcType2}
import Core.MemReg.RegfileFunc
import chisel3._
import chisel3.util.Valid
import utils._

class IDUtoEXUIO extends Bundle with Config {
    val in  = Flipped(Valid(new CfCtrl))
    val src1 = Input(UInt(XLEN.W))
    val src2 = Input(UInt(XLEN.W))
    val out = Valid(new CfCtrl)
}

class IDUtoEXU extends Module with Config {
    val io = IO(new IDUtoEXUIO)
    val regfile = new RegfileFunc
    val src1Data = io.src1
    val src2Data = io.src2

    io.out.valid := io.in.valid
    io.out.bits.data.src1 := LookupTree(io.in.bits.ctrl.src1Type, List(
        SrcType1.reg  -> src1Data,
        SrcType1.pc   -> io.in.bits.cf.pc,
        SrcType1.uimm -> io.in.bits.data.uimm_ext
    ))
    io.out.bits.data.src2 := LookupTree(io.in.bits.ctrl.src2Type, List(
        SrcType2.reg  -> src2Data,
        SrcType2.imm  -> io.in.bits.data.imm
    ))

    // 独立的uimm通路
    io.out.bits.data.uimm_ext := DontCare
    io.out.bits.data.imm := io.in.bits.data.imm
    io.in.bits.cf    <>  io.out.bits.cf
    io.in.bits.ctrl  <>  io.out.bits.ctrl
}