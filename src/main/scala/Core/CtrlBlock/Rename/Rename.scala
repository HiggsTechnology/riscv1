package Core.CtrlBlock.Rename

import Core.{CfCtrl, CommitIO, MicroOp}
import chisel3._
import chisel3.util._
import Core.utils._



class Rename extends Module with Config with HasCircularQueuePtrHelper{
  val io = IO(new Bundle() {
    val flush = Input(Bool())
    val commit = Vec(2, Flipped(ValidIO(new CommitIO)))
    // from decode buffer
    val in = Vec(2, Flipped(DecoupledIO(new CfCtrl)))
    // to dispatch1
    val out = Vec(2, DecoupledIO(new MicroOp))
    // for debug printing
    val debug_int_rat = Vec(32, Output(UInt(PhyRegIdxWidth.W)))
  })

  val intFreeList = Module(new FreeList)

  val intRat = Module(new RenameTable(float = false))

  intFreeList.io.flush := io.flush
  intRat.io.flush := io.flush

  val canOut = io.out(0).ready && intFreeList.io.req.canAlloc
  intFreeList.io.req.doAlloc := io.out(0).ready

  def needDestReg[T <: CfCtrl](x: T): Bool = {
    x.ctrl.rfWen && (x.ctrl.ldest =/= 0.U)
  }

  def needDestRegCommit[T <: CommitIO]( x: T): Bool = {
    x.rfWen && (x.ldest =/= 0.U)
  }

  val uops = Wire(Vec(2, new MicroOp))
  uops.foreach( uop => {
    uop.srcState(0) := DontCare
    uop.srcState(1) := DontCare
  })

  val needIntDest = Wire(Vec(2, Bool()))
  val hasValid = Cat(io.in.map(_.valid)).orR

  for (i <- 0 until 2) {
    uops(i).cf := io.in(i).bits.cf
    uops(i).ctrl := io.in(i).bits.ctrl
    uops(i).data := io.in(i).bits.data

    val inValid = io.in(i).valid
    needIntDest(i) := inValid && needDestReg(io.in(i).bits)
    intFreeList.io.req.allocReqs(i) := needIntDest(i)

    io.in(i).ready := !hasValid || canOut

    for(k <- 0 until 3){
      val rportIdx = i * 3 + k
      if(k != 2){
        intRat.io.readPorts(rportIdx).addr := uops(i).ctrl.lsrc(k)
        uops(i).psrc(k) := intRat.io.readPorts(rportIdx).rdata
      } else {
        intRat.io.readPorts(rportIdx).addr := uops(i).ctrl.ldest
        uops(i).old_pdest := intRat.io.readPorts(rportIdx).rdata
      }
    }

    uops(i).pdest := Mux(uops(i).ctrl.ldest===0.U, 0.U, intFreeList.io.req.pdests(i))
  }

  for(i <- 0 until 2){
    if(io.in(0).bits.ctrl.ldest === io.in(1).bits.ctrl.lsrc(i) && io.in(1).bits.ctrl.srcType(i) === SrcType.reg) uops(1).psrc(i) := uops(0).pdest
  }

  for(i <- 0 until 2){
    intRat.io.specWritePorts(i).addr := uops(i).ctrl.ldest
    intRat.io.specWritePorts(i).wdata := uops(i).pdest
  }
  intRat.io.specWritePorts(0) := needIntDest(0) && !(uops(0).ldest === uops(1).ldest && needIntDest(1))
  intRat.io.specWritePorts(1) := needIntDest(1)


  val commitDestValid = Wire(Vec(2, Bool()))
  for(i <- 0 until 2){
    commitDestValid(i) := io.commit(i).vaild && needDestRegCommit(io.commit(i))
  }

  for(i <- 0 until 2){
    intRat.io.archWritePorts(i).addr := io.commit(i).ldest
    intRat.io.archWritePorts(i).wdata := io.commit(i).pdest
  }
  intRat.io.archWritePorts(0) := commitDestValid(0) && !(io.commit(0).ldest === io.commit(1).ldest && commitDestValid(1))
  intRat.io.archWritePorts(1) := commitDestValid(1)

  for(i <- 0 until 2){
    intFreeList.io.deallocReqs(i)  := commitDestValid(i)
    intFreeList.io.deallocPregs(i) := io.commit(i).old_pdest
  }

  io.debug_int_rat := intRat.io.debug_rdata
}