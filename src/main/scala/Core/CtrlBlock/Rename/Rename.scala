package Core.CtrlBlock.Rename

import Core.CtrlBlock.IDU.{SrcType1, SrcType2}
import Core.{CfCtrl, CommitIO, Config, MicroOp}
import chisel3._
import chisel3.util._
import utils._
class RenameIN extends Bundle with Config {
  val flush  = Input(Bool())
  // from decode buffer
  val cfctrl = Vec(2, Flipped(DecoupledIO(new CfCtrl)))
  // from backend
  val commit = Vec(2, Flipped(ValidIO(new CommitIO)))
}
class RenameOUT extends Bundle with Config {
  // to dispatch1
  val microop = Vec(2, DecoupledIO(new MicroOp))
  // for debug printing
  val debug_int_rat = Vec(32, Output(UInt(PhyRegIdxWidth.W)))
}

class RenameIO extends Bundle with Config {
  val in  = new RenameIN
  val out = new RenameOUT
}
class Rename extends Module with Config{
  def needDestReg[T <: CfCtrl](x: T): Bool = {
    x.ctrl.rfWen && (x.ctrl.rfrd =/= 0.U)
  }
  def needDestRegCommit[T <: CommitIO]( x: T): Bool = {
    x.rfWen && (x.ldest =/= 0.U)
  }
  val io          = IO(new RenameIO)
  val intFreeList = Module(new FreeList)
  val intRat      = Module(new RenameTable(float = false))
  val canOut      = io.out.microop(0).ready && intFreeList.io.req.canAlloc
  val uops        = io.out.microop
  val needIntDest = Wire(Vec(2, Bool()))
  val hasValid    =  Cat(io.in.cfctrl.map(_.valid)).orR
  intFreeList.io.flush       := io.in.flush
  intRat.io.flush            := io.in.flush
  intFreeList.io.req.doAlloc := io.out.microop(0).ready
  for (i<- 0 to 1){
    uops(i).bits.srcState := DontCare
    uops(i).bits.ROBIdx    := DontCare
    uops(i).valid         := io.in.cfctrl(i).valid & intFreeList.io.req.canAlloc
  }

  //输入两路decode信息是否valid：Cat(valid(1),valid(2))并位与

  for (i <- 0 until 2) {//源decode出保持不变
    uops(i).bits.cf   := io.in.cfctrl(i).bits.cf
    uops(i).bits.ctrl := io.in.cfctrl(i).bits.ctrl
    uops(i).bits.data := io.in.cfctrl(i).bits.data

    val inValid                      = io.in.cfctrl(i).valid
    needIntDest(i)                  := inValid && needDestReg(io.in.cfctrl(i).bits) //invalid & 写有效 目标寄存器非0
    intFreeList.io.req.allocReqs(i) := needIntDest(i)
    io.in.cfctrl(i).ready           := !hasValid || canOut
    for(k <- 0 until 3){
      val rportIdx = i * 3 + k
      if(k != 2){
        intRat.io.readPorts(rportIdx).addr := uops(i).bits.ctrl.rfSrc(k)
        uops(i).bits.psrc(k)               := intRat.io.readPorts(rportIdx).rdata
      } else {
        intRat.io.readPorts(rportIdx).addr := uops(i).bits.ctrl.rfrd
        uops(i).bits.old_pdest             := intRat.io.readPorts(rportIdx).rdata
      }
    }
    uops(i).bits.pdest := Mux(uops(i).bits.ctrl.rfrd===0.U, 0.U, intFreeList.io.req.pdests(i))
  }

  val inst0_wen =io.in.cfctrl(0).bits.ctrl.rfWen
  when(io.in.cfctrl(0).bits.ctrl.rfrd===io.in.cfctrl(1).bits.ctrl.rfSrc(0) && io.in.cfctrl(1).bits.ctrl.src1Type === SrcType1.reg && inst0_wen){
    uops(1).bits.psrc(0) := uops(0).bits.pdest
  }
  when(io.in.cfctrl(0).bits.ctrl.rfrd===io.in.cfctrl(1).bits.ctrl.rfSrc(1) && io.in.cfctrl(1).bits.ctrl.src2Type === SrcType2.reg && inst0_wen){
    uops(1).bits.psrc(1) := uops(0).bits.pdest
  }
  when(io.in.cfctrl(0).bits.ctrl.rfrd===io.in.cfctrl(1).bits.ctrl.rfrd && inst0_wen){
    uops(1).bits.old_pdest := uops(0).bits.pdest
  }
  // for(i <- 0 until 2){
  //   printf("Rename stage: pc %x, instr %x, lsrc %d %d, psrc %d %d, ldest %d, pdest %d\n",uops(i).bits.cf.pc,uops(i).bits.cf.instr,uops(i).bits.ctrl.rfSrc(0),uops(i).bits.ctrl.rfSrc(1),uops(i).bits.psrc(0),uops(i).bits.psrc(1),uops(i).bits.ctrl.rfrd,uops(i).bits.pdest)
  //   printf("Rename fire: %d\n",io.out.microop(i).fire)
  // }
  //  for(i <- 0 until 2){
  //    if(io.in(0).bits.ctrl.rfrd === io.in(1).bits.ctrl.rfSrc(i) && io.in(1).bits.ctrl.srcType(i) === SrcType.reg) uops(1).psrc(i) := uops(0).pdest
  //  }
  for(i <- 0 until 2){
    intRat.io.specWritePorts(i).addr  := uops(i).bits.ctrl.rfrd
    intRat.io.specWritePorts(i).wdata := uops(i).bits.pdest
  }
  intRat.io.specWritePorts(0).wen := needIntDest(0) && !(uops(0).bits.ctrl.rfrd === uops(1).bits.ctrl.rfrd && needIntDest(1)) && io.out.microop(0).fire
  intRat.io.specWritePorts(1).wen := needIntDest(1) && io.out.microop(1).fire

  val commitDestValid = Wire(Vec(2, Bool()))
  for(i <- 0 until 2){
    commitDestValid(i) := io.in.commit(i).valid && needDestRegCommit(io.in.commit(i).bits)
  }
  for(i <- 0 until 2){
    intRat.io.archWritePorts(i).addr  := io.in.commit(i).bits.ldest
    intRat.io.archWritePorts(i).wdata := io.in.commit(i).bits.pdest
  }
  intRat.io.archWritePorts(0).wen := commitDestValid(0) && !(io.in.commit(0).bits.ldest === io.in.commit(1).bits.ldest && commitDestValid(1))
  intRat.io.archWritePorts(1).wen := commitDestValid(1)
  // 后端的dealloc请求，传入old pdest释放freeList
  for(i <- 0 until 2){
    intFreeList.io.deallocReqs(i)  := commitDestValid(i)
    intFreeList.io.deallocPregs(i) := io.in.commit(i).bits.old_pdest
  }
  io.out.debug_int_rat := intRat.io.debug_rdata
}