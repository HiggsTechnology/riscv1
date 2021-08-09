
// mill -i __.test.runMain core.TopMain -td ./build
package core

import chisel3._
import chisel3.util._

class TOPIO extends Bundle {
    val out   = Flipped(new Pc_Instr)
    val valid = Output(Bool())
    val diffreg = Flipped(new RegWriteIO)
}

class Top extends Module {
    val io  = IO(new TOPIO)
    val ifu = Module(new IFU)
    val idu = Module(new IDU)
    val dis = Module(new IDUtoEXU)
    val exu = Module(new EXU)
    val wbu = Module(new WBU)
    val reg = Module(new Regfile)

    ifu.io.out              <>  idu.io.in
    idu.io.out              <>  dis.io.in
    dis.io.out              <>  exu.io.in
    exu.io.reg_write_back   <>  wbu.io.in
    exu.io.branch           <>  ifu.io.in
    wbu.io.out              <>  reg.io.rd

    io.diffreg              <>  wbu.io.out

    io.out.pc    := ifu.io.out.pc
    io.out.instr := ifu.io.out.instr
    io.valid     := withClock(clock){
    ~reset.asBool()
    }
}