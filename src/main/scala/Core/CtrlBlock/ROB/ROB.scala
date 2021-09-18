package Core.CtrlBlock.ROB

import Core.Config.robSize
import Core.{CommitIO, Config, MicroOp, ExuCommit}
import difftest.{DifftestInstrCommit, DifftestTrapEvent}
import chisel3._
import chisel3.util._
import utils._

class ROBIO extends Bundle {
  val in  = Vec(2, Flipped(ValidIO(new MicroOp)))
  val enqPtr = Vec(2, Output(new ROBPtr))
  val can_allocate = Output(Bool())

  val exuCommit = Vec(6,Flipped(ValidIO(new ExuCommit)))
  val commit = Vec(2,ValidIO(new CommitIO))

}


class ROBPtr extends CircularQueuePtr[ROBPtr](robSize) with HasCircularQueuePtrHelper{
  override def cloneType = (new ROBPtr).asInstanceOf[this.type]
}

class ROB extends Module with Config with HasCircularQueuePtrHelper {
  val io = IO(new ROBIO)

  val valid     = RegInit(VecInit(Seq.fill(robSize)(false.B)))
  val wb        = RegInit(VecInit(Seq.fill(robSize)(false.B)))
  val res       = Mem(robSize, UInt(XLEN.W))
  val data      = Mem(robSize, new MicroOp)

  val enq_vec = RegInit(VecInit((0 until 2).map(_.U.asTypeOf(new ROBPtr))))///循环指针，enq发射阶段进来的信号在orderqueue的位置
  val deq_vec = RegInit(VecInit((0 until 2).map(_.U.asTypeOf(new ROBPtr))))///出列的指针

  val validEntries = distanceBetween(enq_vec(0), deq_vec(0))

  //enqueue

  val numEnq   = PopCount(io.in.map(_.valid))
  val allowEnq = RegInit(true.B)
  allowEnq  := validEntries + numEnq + 2.U <= robSize.U
  io.can_allocate := allowEnq

  for (i <- 0 until 2) {
    valid(enq_vec(i).value) := io.in(i).valid && io.in(0).valid && allowEnq
    wb(enq_vec(i).value) := false.B
    data(enq_vec(i).value) := io.in(i).bits
    data(enq_vec(i).value).ROBIdx := enq_vec(i)
  }

  for (i <- 0 until 2){
    io.enqPtr(i) := enq_vec(i)
  }

  val vaild_enq = VecInit(io.in.map(_.valid && allowEnq))
  enq_vec := VecInit(enq_vec.map(_ + PopCount(vaild_enq)))

  //writeback
  for(i <- 0 until 6){
    when(io.exuCommit(i).valid){
      wb(io.exuCommit(i).bits.ROBIdx.value) := true.B
      res(io.exuCommit(i).bits.ROBIdx.value) := io.exuCommit(i).bits.res
    }
  }

  //dequeue
  val commitReady = Wire(Vec(2,Bool()))
  commitReady(0) := valid(deq_vec(0).value) && wb(deq_vec(0).value)
  commitReady(1) := valid(deq_vec(1).value) && wb(deq_vec(1).value) && commitReady(0)

  for(i <- 0 until 2){
    io.commit(i).valid := commitReady(i)
    io.commit(i).bits.pdest := data(deq_vec(i).value).pdest
    io.commit(i).bits.old_pdest := data(deq_vec(i).value).old_pdest
    io.commit(i).bits.ldest := data(deq_vec(i).value).ctrl.rfrd
    io.commit(i).bits.rfWen := data(deq_vec(i).value).ctrl.rfWen
    when(commitReady(i)){valid(deq_vec(i).value) := false.B}
  }

  deq_vec := VecInit(deq_vec.map(_ + PopCount(commitReady)))

  for(i <- 0 until 2) {
    val instrCommit = Module(new DifftestInstrCommit)
    instrCommit.io.clock := clock
    instrCommit.io.coreid := 0.U
    instrCommit.io.index := i.U
    instrCommit.io.skip := false.B
    instrCommit.io.isRVC := false.B
    instrCommit.io.scFailed := false.B

    instrCommit.io.valid := RegNext(commitReady(i))
    instrCommit.io.pc := RegNext(data(deq_vec(i).value).cf.pc)
    instrCommit.io.instr := RegNext(data(deq_vec(i).value).cf.instr)
    instrCommit.io.wen := RegNext(data(deq_vec(i).value).ctrl.rfWen)
    instrCommit.io.wdata := RegNext(res(deq_vec(i).value))
    instrCommit.io.wdest := RegNext(data(deq_vec(i).value).ctrl.rfrd)
  }

  val hitTrap = Wire(Vec(2, Bool()))
  for(i <- 0 until 2){
    hitTrap(i) := data(deq_vec(i).value).cf.instr === BigInt("0000006b",16).U
  }

  val trap = Module(new DifftestTrapEvent)
  trap.io.clock := clock
  trap.io.coreid := 0.U
  trap.io.valid := hitTrap(0) || hitTrap(1)
  trap.io.code := 0.U
  trap.io.pc := Mux(hitTrap(0), data(deq_vec(0).value).cf.pc, data(deq_vec(1).value).cf.pc)
  trap.io.cycleCnt := 0.U
  trap.io.instrCnt := 0.U

  // printf("ROB enqvalid %d %d, enq_vec %d %d\n", io.in(0).valid && allowEnq, io.in(1).valid && allowEnq, enq_vec(0).value, enq_vec(1).value)
  // printf("ROB deqvalid %d %d, deq_vec %d %d\n", commitReady(0), commitReady(1), deq_vec(0).value, deq_vec(1).value)
  // for(i <- 0 until robSize){
  //   printf("ROB %d: valid %d, wb %d, pc %x, inst %x\n",i.U, valid(i), wb(i),data(i).cf.pc,data(i).cf.instr)
  // }

}


