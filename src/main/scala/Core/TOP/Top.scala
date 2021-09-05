
// mill -i __.test.runMain core.TopMain -td ./build
package Core.TOP

import Core.DISPATCH.IDUtoEXU
import Core.EXU.EXU
import Core.IDU.IDU
import Core.IFU.IFU
import Core.WBU.WBU
import Core.MemReg.{RegWriteIO, Regfile}
import chisel3._
import utils.Pc_Instr
import Core.AXI4.{AXI4IO, IFURW}

class TOPIO extends Bundle {
    val out   = new Pc_Instr
    val valid = Output(Bool())
    val diffreg = new RegWriteIO
    val axi4 = new AXI4IO
    //axi4 bundle out
}

class Top extends Module {
    val io  = IO(new TOPIO)
    val ifu = Module(new IFU)
    val idu = Module(new IDU)
    val dis = Module(new IDUtoEXU)
    val exu = Module(new EXU)
    val wbu = Module(new WBU)
    val reg = Module(new Regfile)
    val ifuaxi = Module(new IFURW)

    io.axi4                 <>  ifuaxi.io.ifu2crossbar
    ifuaxi.io.ifuin         <>  ifu.io.ifu2rw
    ifu.io.out              <>  idu.io.in
    idu.io.out              <>  dis.io.in
    dis.io.out              <>  exu.io.in
    exu.io.reg_write_back   <>  wbu.io.in
    exu.io.branch           <>  ifu.io.bru
    wbu.io.out.bits         <>  reg.io.rd
    reg.io.src1.addr        := idu.io.out.bits.ctrl.rfSrc1
    reg.io.src2.addr        := idu.io.out.bits.ctrl.rfSrc2
    dis.io.src1             := reg.io.src1.data
    dis.io.src2             := reg.io.src2.data

    io.diffreg              <>  wbu.io.out.bits

    io.out.pc    := ifu.io.out.bits.pc
    io.out.instr := ifu.io.out.bits.instr
    // Todo: 改为通过流水线模块控制
    // 当写回和pc修改都完成后，可以继续取指
    val first_inst = RegInit(true.B)
    val ifu_stall = !first_inst & !RegNext(wbu.io.out.valid) & !RegNext(exu.io.branch.valid)

    io.valid     := withClock(clock){
        // 在写回valid的下一个周期才能写入寄存器，所以在下一个周期向difftest提交指令
        (!reset.asBool()) && ifu.io.out.valid
    }
    when(wbu.io.out.valid) {
        first_inst := false.B
    }
    ifu.io.stall := ifu_stall
}