package Core.IFU

import chisel3._
import chisel3.util._
import utils._

val IBufSize  = 16

class IbufPtr extends CircularQueuePtr[IbufPtr](IBufSize){
  override def cloneType = (new IbufPtr).asInstanceOf[this.type]
}

class IBufferIO extends Bundle with Config {
  val flush = Input(Bool())

  val in = Vec(2, DecoupledIO(new Pc_Instr))
  val out = Vec(2, DecoupledIO(new Pc_Instr))
}

class Ibuffer extends Module with Config with HasCircularQueuePtrHelper {
  val io = IO(new IBufferIO)

  val data = Mem(IBufSize, new Pc_Instr)
  val valid = RegInit(VecInit(Seq.fill(IBufSize)(false.B)))

  val enq_vec = RegInit(VecInit((0 until 2).map(_.U.asTypeOf(new IbufPtr))))
  val deq_vec = RegInit(VecInit((0 until 2).map(_.U.asTypeOf(new IbufPtr))))

  val validEntries = distanceBetween(enq_vec(0), deq_vec(0))

  //Enq
  val numEnq = PopCount(io.in.map(_.valid))
  val allowEnq = RegInit(true.B)
  allowEnq := validEntries + numEnq + 2.U <= IBufSize.U

  for(i <- 0 until 2){
    io.in.ready := allowEnq
  }

  for (i <- 0 until 2) {
    data(enq_vec(i).value) := in(i).bits
    valid(enq_vec(i).value) := in(i).fire
  }

  enq_vec := VecInit(enq_vec.map(_ + PopCount(io.in.map(_.fire))))

  //Deq
  for(i <- 0 until 2){
    io.out.bits := data(deq_vec(i).value)
    io.out.valid := valid(deq_vec(i).value)
  }

  val numDeq = PopCount(io.out.map(_.fire))

  deq_vec := VecInit(deq_vec.map(_ + numDeq))


  //flush
  when (io.flush) {
    allowEnq := true.B
    enq_vec := VecInit((0 until 2).map(_.U.asTypeOf(new IbufPtr)))
    deq_vec := VecInit((0 until 2).map(_.U.asTypeOf(new IbufPtr)))
    valid := VecInit(Seq.fill(IBufSize)(false.B))
  }

}
