
// mill -i __.test.runMain core.TopMain -td ./build
package Core.TOP

import Core.AXI4.{AXI4, AXI4LSU}
import Core.DISPATCH.IDUtoEXU
import Core.EXU.EXU
import Core.IDU.IDU
import Core.IFU.IFU
import Core.WBU.WBU
import Core.Config.Config
import Core.MemReg.{RegWriteIO, Regfile}
import chisel3._
import utils.Pc_Instr

class TOPIO extends Bundle with Config{
    val ifuout = new AXI4
    val lsuout = new AXI4LSU
//------------------debug---------------------
    val valid = Output(Bool())
    val pc_out = Output(UInt(DATA_WIDTH))
    val inst_in = Input(UInt(INST_WIDTH))
//------------------debug---------------------
}

class Top extends Module {
    val io: TOPIO = IO(new TOPIO)
    val ifu: IFU = Module(new IFU)
    val idu: IDU = Module(new IDU)
    val dis: IDUtoEXU = Module(new IDUtoEXU)
    val exu: EXU = Module(new EXU)
    val wbu: WBU = Module(new WBU)
    val reg: Regfile = Module(new Regfile)

    ifu.io.out              <>  idu.io.in
    idu.io.out              <>  dis.io.in
    dis.io.out              <>  exu.io.in
    exu.io.reg_write_back   <>  wbu.io.in
    exu.io.branch           <>  ifu.io.in
    wbu.io.out              <>  reg.io.rd
    reg.io.src1.addr := idu.io.out.ctrl.rfSrc1
    reg.io.src2.addr := idu.io.out.ctrl.rfSrc2
    dis.io.src1      := reg.io.src1.data
    dis.io.src2      := reg.io.src2.data

    io.ifuout    <> ifu.io.ifuaxi
    io.lsuout    <> exu.io.lsuaxi
//------------------debug---------------------
    
    io.pc_out := ifu.io.out.pc
    ifu.io.ifuaxi.r.bits.data := io.inst_in
    io.valid     := withClock(clock){
    ~reset.asBool()
    }
//------------------debug---------------------

}