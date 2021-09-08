
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
import Core.AXI4.{AXI4IO, IFURW, LSURW}

class TOPIO(use_axi: Boolean = true) extends Bundle {
  val out   = new Pc_Instr
  val valid = Output(Bool())
  val diffreg = new RegWriteIO
  val axi4 = if(use_axi) new AXI4IO else null
  //axi4 bundle out
}

class Top(
   ifu_use_axi: Boolean = false,
   lsu_use_axi: Boolean = false
  ) extends Module {
  val use_axi = ifu_use_axi || lsu_use_axi
  val io  = IO(new TOPIO(use_axi))
  val ifu = Module(new IFU(ifu_use_axi))
  val idu = Module(new IDU)
  val dis = Module(new IDUtoEXU)
  val exu = Module(new EXU(lsu_use_axi))
  val wbu = Module(new WBU)
  val reg = Module(new Regfile)
  val ifuaxi = if(ifu_use_axi) Module(new IFURW) else null
  val lsuaxi = if(lsu_use_axi) Module(new LSURW) else null
  if (ifu_use_axi) {
    io.axi4                 <>  ifuaxi.io.ifu2crossbar
    ifuaxi.io.ifuin         <>  ifu.io.ifu2rw
  }

  if (lsu_use_axi) {
    io.axi4               <>  lsuaxi.io.lsu2crossbar
    lsuaxi.io.lsuin       <>  exu.io.lsu2rw
  }

  ifu.io.out              <>  idu.io.in
  idu.io.out              <>  dis.io.in
  dis.io.out              <>  exu.io.in
  exu.io.reg_write_back   <>  wbu.io.in
  exu.io.branch           <>  ifu.io.bru
  wbu.io.out              <>  reg.io.rd
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

  val ifu_stall = !wbu.io.out.valid

  io.valid     := withClock(clock){
    // 在写回valid的下一个周期才能写入寄存器，所以在下一个周期向difftest提交指令
    (!reset.asBool()) && wbu.io.out.valid
  }
  when(wbu.io.out.valid) {
    first_inst := false.B
  }
  ifu.io.stall := ifu_stall

  printf("reset:%d, exu.io.out.valid:%d, " +
    "wbu.io.out.valid:%d, io.valid: %d, " +
    "ifu_stall: %d, pc: %x, inst: %x\n",
    reset.asBool(), exu.io.reg_write_back.valid, wbu.io.out.valid, io.valid, ifu_stall, ifu.io.out.bits.pc, ifu.io.out.bits.instr);
}