package Core.CtrlBlock.Rename

import Core.CtrlBlock.IDU.{SrcType1, SrcType2}
import Core.{CfCtrl, CommitIO, Config, MicroOp}
import chisel3._
import chisel3.util._
import utils._

class RenameIO extends Bundle with Config with HasCircularQueuePtrHelper{
  // from decode buffer
  val in = Vec(2, Flipped(DecoupledIO(new CfCtrl)))
  // to dispatch1
  val out = Vec(2, DecoupledIO(new MicroOp))
  val flush = Input(Bool())
  val commit = Vec(2, Flipped(ValidIO(new CommitIO)))
  // for debug printing
  val debug_int_rat = Vec(32, Output(UInt(PhyRegIdxWidth.W)))
}

class Rename extends Module with Config with HasCircularQueuePtrHelper{
  val io = IO(new RenameIO)
  val intFreeList = Module(new FreeList)
  val intRat = Module(new RenameTable(float = false))
  //TODO valid io
  intFreeList.io.flush := io.flush
  intRat.io.flush := io.flush

  val canOut = io.out(0).ready && intFreeList.io.req.canAlloc
  intFreeList.io.req.doAlloc := io.out(0).ready

  def needDestReg[T <: CfCtrl](x: T): Bool = {
    x.ctrl.rfWen && (x.ctrl.rfrd =/= 0.U)
  }

  def needDestRegCommit[T <: CommitIO]( x: T): Bool = {
    x.rfWen && (x.ldest =/= 0.U)
  }

  val uops = Wire(Vec(2, new MicroOp)) //CfCtrl 额外添加5个信号
  uops.foreach( uop => {
    uop.srcState(0) := DontCare
    uop.srcState(1) := DontCare
  })
  val needIntDest = Wire(Vec(2, Bool()))
  val hasValid = Cat(io.in.map(_.valid)).orR
  //输入两路decode信息是否valid：Cat(valid(1),valid(2))并位与

  for (i <- 0 until 2) {//源decode出保持不变
    uops(i).cf := io.in(i).bits.cf
    uops(i).ctrl := io.in(i).bits.ctrl
    uops(i).data := io.in(i).bits.data

    val inValid = io.in(i).valid
    needIntDest(i) := inValid && needDestReg(io.in(i).bits) //invalid & 写有效 目标寄存器非0
    intFreeList.io.req.allocReqs(i) := needIntDest(i)
    io.in(i).ready := !hasValid || canOut
    for(k <- 0 until 3){
      val rportIdx = i * 3 + k
      if(k != 2){
        intRat.io.readPorts(rportIdx).addr := uops(i).ctrl.rfSrc(k)
        uops(i).psrc(k) := intRat.io.readPorts(rportIdx).rdata
      } else {
        intRat.io.readPorts(rportIdx).addr := uops(i).ctrl.rfrd
        uops(i).old_pdest := intRat.io.readPorts(rportIdx).rdata
      }
    }
    uops(i).pdest := Mux(uops(i).ctrl.rfrd===0.U, 0.U, intFreeList.io.req.pdests(i))
  }
  when(io.in(0).bits.ctrl.rfrd===io.in(1).bits.ctrl.rfSrc(0) && io.in(1).bits.ctrl.src1Type === SrcType1.reg){
    uops(1).psrc(0) := uops(0).pdest
  }
  when(io.in(0).bits.ctrl.rfrd===io.in(1).bits.ctrl.rfSrc(1) && io.in(1).bits.ctrl.src2Type === SrcType2.reg){
    uops(1).psrc(1) := uops(0).pdest
  }
  //  for(i <- 0 until 2){
  //    if(io.in(0).bits.ctrl.rfrd === io.in(1).bits.ctrl.rfSrc(i) && io.in(1).bits.ctrl.srcType(i) === SrcType.reg) uops(1).psrc(i) := uops(0).pdest
  //  }
  for(i <- 0 until 2){
    intRat.io.specWritePorts(i).addr := uops(i).ctrl.rfrd
    intRat.io.specWritePorts(i).wdata := uops(i).pdest
  }
  intRat.io.specWritePorts(0) := needIntDest(0) && !(uops(0).ctrl.rfSrc(0) === uops(1).ctrl.rfrd && needIntDest(1))
  intRat.io.specWritePorts(1) := needIntDest(1)

  val commitDestValid = Wire(Vec(2, Bool()))
  for(i <- 0 until 2){
    commitDestValid(i) := io.commit(i).valid && needDestRegCommit(io.commit(i))
  }
  for(i <- 0 until 2){
    intRat.io.archWritePorts(i).addr  := io.commit(i).bits.ldest
    intRat.io.archWritePorts(i).wdata := io.commit(i).bits.pdest
  }
  intRat.io.archWritePorts(0) := commitDestValid(0) && !(io.commit(0).bits.ldest === io.commit(1).bits.ldest && commitDestValid(1))
  intRat.io.archWritePorts(1) := commitDestValid(1)

  for(i <- 0 until 2){
    intFreeList.io.deallocReqs(i)  := commitDestValid(i)
    intFreeList.io.deallocPregs(i) := io.commit(i).bits.old_pdest
  }
  io.debug_int_rat := intRat.io.debug_rdata
}