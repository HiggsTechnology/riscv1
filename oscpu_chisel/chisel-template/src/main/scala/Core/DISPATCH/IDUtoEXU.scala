package Core.DISPATCH

import Core.IDU.SrcType
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

    printf("Print during simulation: io.in.ctrl.rfSrc1 is %d\n", io.in.ctrl.rfSrc1)
    printf("Print during simulation: io.in.ctrl.rfSrc2 is %d\n", io.in.ctrl.rfSrc2)

    io.out.data.src1 := LookupTree(io.in.ctrl.src1Type, List(
        SrcType.reg  -> src1Data,
        SrcType.pc   -> io.in.cf.pc
    ))
    io.out.data.src2 := LookupTree(io.in.ctrl.src2Type, List(
        SrcType.reg  -> src2Data,
        SrcType.imm  -> io.in.data.imm
    ))
    printf("Print during simulation: io.out.data.src1 is %x\n", io.out.data.src1)
    printf("Print during simulation: io.out.data.src2 is %x\n", io.out.data.src2)

    io.out.data.imm := io.in.data.imm
    io.in.cf    <>  io.out.cf
    io.in.ctrl  <>  io.out.ctrl
}