
// mill -i __.test.runMain core.TopMain -td ./build
package Core

import Core.AXI4.{AXI4IO, IFURW}
import Core.CtrlBlock.IDU.IDU
import Core.ExuBlock.MemReg.{RegWriteIO, Regfile}
import Core.IFU.IFU
import chisel3._

class TOPIO extends Bundle {
    val out   = new Pc_Instr
    val valid = Output(Bool())
    val diffreg = Flipped(new RegWriteIO)
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

    io.axi4             <>  ifuaxi.io.ifu2crossbar
    ifuaxi.io.ifuin         <>  ifu.io.ifu2rw
    ifu.io.out              <>  idu.io.in
    idu.io.out              <>  dis.io.in
    dis.io.out              <>  exu.io.in
    exu.io.reg_write_back   <>  wbu.io.in
    exu.io.branch           <>  ifu.io.in
    wbu.io.out              <>  reg.io.rd
    reg.io.src1.addr        := idu.io.out.bits.ctrl.rfSrc1
    reg.io.src2.addr        := idu.io.out.bits.ctrl.rfSrc2
    dis.io.src1             := reg.io.src1.data
    dis.io.src2             := reg.io.src2.data

    io.diffreg              <>  wbu.io.out

    io.out.pc    := ifu.io.out.bits.pc
    io.out.instr := ifu.io.out.bits.instr
    io.valid     := withClock(clock){
    ~reset.asBool()
    }
}