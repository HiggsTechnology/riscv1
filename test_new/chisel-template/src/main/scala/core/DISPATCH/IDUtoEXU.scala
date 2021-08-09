package core
import chisel3._
import chisel3.util._

class IDUtoEXUIO extends Bundle {
    val in  = Flipped(new CfCtrl)
    val out = new CfCtrl
}

class IDUtoEXU extends Module with Config {
    val io = IO(new IDUtoEXUIO)
    val regfile = new RegfileFunc
    val src1Data = regfile.read(io.in.ctrl.rfSrc1)
    val src2Data = regfile.read(io.in.ctrl.rfSrc2)
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