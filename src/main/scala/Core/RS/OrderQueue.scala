package Core.RS

import chisel3._
import chisel3.util._
import utils._

val OrderQueueSize = 32
val ExuNum = 8


object SrcState {
  def busy    = "b0".U
  def rdy     = "b1".U
    // def specRdy = "b10".U // speculative ready, for future use
  def apply() = UInt(1.W)
}

class MicroOp extends CfCtrl {
  val srcState = Vec(2, SrcState())
  val psrc = Vec(2, UInt(PhyRegIdxWidth.W))
  val pdest = UInt(PhyRegIdxWidth.W)
  val old_pdest = UInt(PhyRegIdxWidth.W)//具体的作用有点忘了，不确定顺序双发射是否需要
}

class RSDispatch extends Bundle{
  val valid = Bool()
  val dispatchNUM = UInt(log2Up(OrderQueueSize).W)
  val validNext = Bool()///第一条指令序号为N,那么第一个valid就是rs里这第N条能不能发射给执行单元，
}

class OrderQueueIO extends Bundle {
  val in  = Vec(2, ValidIO(new MicroOp))
  val rs_num = Vec(2, Input(UInt(log2Up(ExuNum).W)))
  val enqPtr = Vec(2, Output(UInt(log2Up(OrderQueueSize).W)))
  val out = Output(new RSDispatch)
}

class OrderQueueData extends Bundle {
  val valid  = Bool()
  val rs_num = UInt(log2Up(ExuNum).W)
  val pdest = UInt(PhyRegIdxWidth.W)
  val needLast = Bool()
}

class OrderQueuePtr extends CircularQueuePtr[OrderQueuePtr](OrderQueueSize) with HasCircularQueuePtrHelper{
  override def cloneType = (new IbufPtr).asInstanceOf[this.type]
}

class OrderQueue extends Module with Config with HasCircularQueuePtrHelper {
  val io = IO(new OrderQueueIO)

  val data = Mem(OrderQueueSize, new OrderQueueData)

  val enq_vec = RegInit(VecInit((0 until 2).map(_.U.asTypeOf(new OrderQueuePtr))))
  val deq_vec = RegInit(VecInit((0 until 2).map(_.U.asTypeOf(new OrderQueuePtr))))
  
  val isEmpty = enq_vec(0) === deq_vec(0)

  //enqueue
  for (i <- 0 until 2) {
    val pre_pdest = data(enq_vec(i).value-1.W).pdest////1.W???
    data(enq_vec(i).value).valid := io.in(i).valid
    data(enq_vec(i).value).rs_num := io.rs_num(i)
    data(enq_vec(i).value).pdest := io.in(i).bits.pdest
    data(enq_vec(i).value).needLast := data(enq_vec(i).value-1.W).valid && ((pre_pdest === io.in(i).bits.psrc(0) && io.in(i).bits.ctrl.src1Type === SrcType1.reg)||(pre_pdest === io.in(i).bits.psrc(1) && io.in(i).bits.ctrl.src2Type === SrcType2.reg))
  }

  for (i <- 0 until 2){
    io.enqPtr(i) := enq_vec(i).value
  }

  val vaild_enq = VecInit(io.in.map(_.vaild))
  enq_vec := VecInit(enq_vec.map(_ + PopCount(vaild_enq)))

  //dequeue

  io.out.dispatchNUM := deq_vec(0).value
  val validEntries = distanceBetween(enq_vec(0), deq_vec(0))
  when(isEmpty){
    io.out.valid := false.B
    io.out.validNext := false.B
  }.elsewhen(validEntries === 1.U || data(deq_vec(1).value).needLast === true.B || data(deq_vec(0).value).rs_num === data(deq_vec(1).value).rs_num){
    io.out.valid := true.B//第N+1要用上一条结果，N和N+1在同一个保留站也不能发射；rs一进一出
    io.out.validNext := false.B
    data(deq_vec(0).value).valid === false.B
  }.otherwise{
    io.out.valid := true.B
    io.out.validNext := true.B
    data(deq_vec(0).value).valid === false.B
    data(deq_vec(1).value).valid === false.B
  }///大于一出

  deq_vec := VecInit(deq_vec.map(_ + io.out.valid + io.out.validNext))

}