
package Core.TOP

import Bus.AXI4.{AXI4IO, CROSSBAR_Nto1, IFURW, LSURW}
import Core.DISPATCH.IDUtoEXU
import Core.EXU.EXU
import Core.IDU.IDU
import Core.IFU.IFU
import Core.WBU.WBU
import Core.MemReg.{RegWriteIO, Regfile}
import Bus.AXI4.AXI4IO
import Bus.MMIO
import Devices.Clint.ClintIO
import utils.{Pc_Instr, SimpleSyncBus, bool2int}
import chisel3._
import difftest.UARTIO
import utils.BasicIOType.OutBool

class RVCoreIO(use_axi: Boolean = true) extends Bundle {
  val out   = new Pc_Instr
  val inst_valid : Bool = Output(Bool())
  val difftest_skip : Bool = OutBool()
  val diff_reg = new RegWriteIO
  val axi4 : AXI4IO = if(use_axi) new AXI4IO else null
  val clint : ClintIO = Flipped(new ClintIO)
  val to_uart = new SimpleSyncBus
  //axi4 bundle out
}

class RVCore(
   ifu_use_axi: Boolean = false,
   lsu_use_axi: Boolean = false,
   need_difftest: Boolean = false,
  ) extends Module {

  val num_axi4_master: Int = bool2int(ifu_use_axi) + bool2int(lsu_use_axi)

  val use_axi = ifu_use_axi || lsu_use_axi


  val io  = IO(new RVCoreIO(use_axi))
  val ifu = Module(new IFU(ifu_use_axi))
  val idu = Module(new IDU)
  val dis = Module(new IDUtoEXU)
  val exu = Module(new EXU(use_axi = lsu_use_axi, need_difftest = need_difftest))
  val wbu = Module(new WBU)
  val reg = Module(new Regfile(need_difftest))
  val ifuaxi : IFURW = if(ifu_use_axi) Module(new IFURW) else null
  val lsuaxi : LSURW = if(lsu_use_axi) Module(new LSURW) else null
  val crossbar_xto1 : CROSSBAR_Nto1 = if(num_axi4_master > 1) Module(new CROSSBAR_Nto1(1,1)) else null
  val mmio : MMIO = if(num_axi4_master > 1) Module(new MMIO(3)) else null

  if (ifu_use_axi && num_axi4_master == 1) {
    io.axi4                 <>  ifuaxi.io.to_crossbar
    ifuaxi.io.ifuin         <>  ifu.io.ifu2rw
  }

  if (lsu_use_axi && num_axi4_master == 1) {
    io.axi4               <>  lsuaxi.io.to_crossbar
    lsuaxi.io.lsuin       <>  exu.io.lsu2rw
  }

  if (num_axi4_master > 1) {
    io.axi4                 <>  crossbar_xto1.io.out

    crossbar_xto1.io.in(0)  <>  ifuaxi.io.to_crossbar
    ifuaxi.io.ifuin         <>  ifu.io.ifu2rw

    crossbar_xto1.io.in(1)  <>  lsuaxi.io.to_crossbar
    lsuaxi.io.lsuin         <>  mmio.io.slave(0)
    mmio.io.master          <>  exu.io.lsu2rw

    io.clint.in             <>  mmio.io.slave(1)
    io.clint.out            <>  exu.io.clint

    io.difftest_skip        := mmio.io.difftest_skip

    io.to_uart              <>  mmio.io.slave(2)
  }

  ifu.io.out              <>  idu.io.in
  idu.io.out              <>  dis.io.in
  dis.io.out              <>  exu.io.in
  exu.io.reg_write_back   <>  wbu.io.in
  exu.io.branch           <>  ifu.io.bru
  wbu.io.out              <>  reg.io.rd
  reg.io.src1.addr        :=  idu.io.out.bits.ctrl.rfSrc1
  reg.io.src2.addr        :=  idu.io.out.bits.ctrl.rfSrc2
  dis.io.src1             :=  reg.io.src1.data
  dis.io.src2             :=  reg.io.src2.data

  exu.io.inst_inc.valid   := wbu.io.out.valid
  exu.io.inst_inc.bits.value := 1.U   // 暂时每个周期提交一个指令
  exu.io.difftest_trapcode  <>  idu.io.trapcode

  io.diff_reg               <>  wbu.io.out.bits

  io.out.pc    := ifu.io.out.bits.pc
  io.out.instr := ifu.io.out.bits.instr


  // Todo: 改为通过流水线模块控制
  // 当写回和pc修改都完成后，可以继续取指
  val first_inst = RegInit(true.B)

  // 适应AXI4和ramhelper两种不同的stall策略
  var ifu_stall : Bool = false.B
  if (ifu_use_axi) {
    ifu_stall = RegInit(false.B)
    withClock((~clock.asUInt).asBool.asClock()){
      ifu_stall := !wbu.io.out.valid
    }
  } else {
    ifu_stall = !wbu.io.out.valid
  }

  io.inst_valid     := withClock(clock){
    // 在写回valid的下一个周期才能写入寄存器，所以在下一个周期向difftest提交指令
    (!reset.asBool()) && wbu.io.out.valid
  }
  when(wbu.io.out.valid) {
    first_inst := false.B
  }
  ifu.io.stall := ifu_stall

//  printf("reset:%d, exu.io.out.valid:%d, " +
//    "wbu.io.out.valid:%d, io.valid: %d, " +
//    "ifu_stall: %d, pc: %x, inst: %x\n",
//    reset.asBool(), exu.io.reg_write_back.valid, wbu.io.out.valid, io.inst_valid, ifu_stall, ifu.io.out.bits.pc, ifu.io.out.bits.instr)
}