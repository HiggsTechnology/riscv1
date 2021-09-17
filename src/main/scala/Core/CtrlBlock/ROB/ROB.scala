package Core.CtrlBlock.ROB

import Core.Config.robSize
import Core.{CommitIO, Config, MicroOp, ExuCommit}
import difftest.{ DifftestInstrCommit}
import chisel3._
import chisel3.util._
import utils._

class ROBIO extends Bundle {
  val in  = Vec(2, Flipped(ValidIO(new MicroOp)))
  val enqPtr = Vec(2, Output(new ROBPtr))
  val can_allocate = Output(Bool())

  val exuCommit = Vec(5,Flipped(ValidIO(new ExuCommit)))
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
    valid(enq_vec(i).value) := io.in(i).valid && io.in(0).valid
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
  for(i <- 0 until 5){
    when(io.exuCommit(i).valid){
      wb(io.exuCommit(i).bits.ROBIdx.value) := true.B
      res(io.exuCommit(i).bits.ROBIdx.value) := io.exuCommit(i).bits.res
    }
  }

  //dequeue
  val CommitReady = Wire(Vec(2,Bool()))
  CommitReady(0) := valid(deq_vec(0).value) && wb(deq_vec(0).value)
  CommitReady(1) := valid(deq_vec(1).value) && wb(deq_vec(1).value) && CommitReady(0)

  for(i <- 0 until 2){
    io.commit(i).valid := CommitReady(i)
    io.commit(i).bits.pdest := data(deq_vec(i).value).pdest
    io.commit(i).bits.old_pdest := data(deq_vec(i).value).old_pdest
    io.commit(i).bits.ldest := data(deq_vec(i).value).ctrl.rfrd
    io.commit(i).bits.rfWen := data(deq_vec(i).value).ctrl.rfWen
  }

  deq_vec := VecInit(deq_vec.map(_ + PopCount(CommitReady)))

  for(i <- 0 until 2) {
    val instrCommit = Module(new DifftestInstrCommit)
    instrCommit.io.clock := clock
    instrCommit.io.coreid := 0.U
    instrCommit.io.index := i.U
    instrCommit.io.skip := false.B
    instrCommit.io.isRVC := false.B
    instrCommit.io.scFailed := false.B

    instrCommit.io.valid := RegNext(CommitReady(i))
    instrCommit.io.pc := RegNext(data(deq_vec(i).value).cf.pc)
    instrCommit.io.instr := RegNext(data(deq_vec(i).value).cf.instr)
    instrCommit.io.wen := RegNext(data(deq_vec(i).value).ctrl.rfWen)
    instrCommit.io.wdata := RegNext(res(deq_vec(i).value))
    instrCommit.io.wdest := RegNext(data(deq_vec(i).value).ctrl.rfrd)
  }

}

