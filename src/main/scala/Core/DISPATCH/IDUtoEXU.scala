package Core.DISPATCH

import Core.Config.Config
import Core.IDU.{SrcType1, SrcType2}
import Core.MemReg.RegfileFunc
import chisel3._
import utils._

class IDUtoEXUIO extends Bundle with Config {
    val in  = Flipped(new CfCtrl)
    val src1 = Input(UInt(XLEN.W))
    val src2 = Input(UInt(XLEN.W))
    val out = new CfCtrl
}

class IDUtoEXU extends Module with Config {
    val io = IO(new IDUtoEXUIO)
    val regfile = new RegfileFunc
    val src1Data = io.src1
    val src2Data = io.src2

    // printf("Print during simulation: io.in.ctrl.rfSrc1 is %d\n", io.in.ctrl.rfSrc1)
    // printf("Print during simulation: io.in.ctrl.rfSrc2 is %d\n", io.in.ctrl.rfSrc2)

    io.out.data.src1 := LookupTree(io.in.ctrl.src1Type, List(
        SrcType1.reg  -> src1Data,
        SrcType1.pc   -> io.in.cf.pc,
        SrcType1.uimm -> io.in.data.uimm_ext
    ))
    io.out.data.src2 := LookupTree(io.in.ctrl.src2Type, List(
        SrcType2.reg  -> src2Data,
        SrcType2.imm  -> io.in.data.imm
    ))
    // printf("Print during simulation: io.out.data.src1 is %x\n", io.out.data.src1)
    // printf("Print during simulation: io.out.data.src2 is %x\n", io.out.data.src2)
    // 独立的uimm通路
    io.out.data.uimm_ext := DontCare
    io.out.data.imm := io.in.data.imm
    io.in.cf    <>  io.out.cf
    io.in.ctrl  <>  io.out.ctrl
}