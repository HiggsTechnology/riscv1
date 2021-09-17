package Core.IFU

import Core.{Config, Pc_Instr}
import Core.Config.IBufSize
import chisel3._
import chisel3.util._
import utils._

class IbufPtr extends CircularQueuePtr[IbufPtr](IBufSize){
  override def cloneType = (new IbufPtr).asInstanceOf[this.type]
}

class IBufferIO extends Bundle with Config {
  val flush = Input(Bool())
  val in    = Vec(2, Flipped(DecoupledIO(new Pc_Instr)))
  val out   = Vec(2, DecoupledIO(new Pc_Instr))
}

class Ibuffer extends Module with HasCircularQueuePtrHelper {
  val io = IO(new IBufferIO)

  val data  = Mem(IBufSize, new Pc_Instr)
  val valid = RegInit(VecInit(Seq.fill(IBufSize)(false.B)))

  val enq_vec = RegInit(VecInit((0 until 2).map(_.U.asTypeOf(new IbufPtr))))
  val deq_vec = RegInit(VecInit((0 until 2).map(_.U.asTypeOf(new IbufPtr))))

  val validEntries = distanceBetween(enq_vec(0), deq_vec(0))

  //Enq
  val numEnq   = PopCount(io.in.map(_.valid))
  val allowEnq = RegInit(true.B)
  allowEnq  := validEntries + numEnq + 2.U <= IBufSize.U

  for(i <- 0 until 2){
    io.in(i).ready := allowEnq
  }

  for (i <- 0 until 2) {
    data(enq_vec(i).value)  := io.in(i).bits
    valid(enq_vec(i).value) := io.in(i).fire
  }

  enq_vec := VecInit(enq_vec.map(_ + PopCount(io.in.map(_.fire))))

  //Deq
  for(i <- 0 until 2){
    io.out(i).bits  := data(deq_vec(i).value)
    io.out(i).valid := valid(deq_vec(i).value)
    when(io.out(i).fire){valid(deq_vec(i).value) := false.B}
  }

  val numDeq = PopCount(io.out.map(_.fire))
  deq_vec := VecInit(deq_vec.map(_ + numDeq))
  
  //flush
  when (io.flush) {
    allowEnq := true.B
    enq_vec  := VecInit((0 until 2).map(_.U.asTypeOf(new IbufPtr)))
    deq_vec  := VecInit((0 until 2).map(_.U.asTypeOf(new IbufPtr)))
    valid    := VecInit(Seq.fill(IBufSize)(false.B))
  }

  printf("IBF enqvalid %d %d, enq_vec %d %d\n", io.in(0).fire, io.in(1).fire, enq_vec(0).value, enq_vec(1).value)
  printf("IBF deqvalid %d %d, deq_vec %d %d\n", io.out(0).fire, io.out(1).fire, deq_vec(0).value, deq_vec(1).value)
  printf("IBF deqvalid %d %d\n",io.out(0).valid,io.out(1).valid)
  for(i <- 0 until IBufSize){
    printf("IBF %d: valid %d, pc %x, inst %x\n",i.U, valid(i),data(i).pc,data(i).instr)
  }


}
