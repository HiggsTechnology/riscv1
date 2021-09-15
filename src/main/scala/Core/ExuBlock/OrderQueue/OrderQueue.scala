package Core.ExuBlock.OrderQueue

import Core.Config.{ExuNum, OrderQueueSize, PhyRegIdxWidth}
import Core.CtrlBlock.IDU.{SrcType1, SrcType2}
import Core.{Config, MicroOp}
import chisel3._
import chisel3.util._
import utils._



class RSDispatch extends Bundle{
  val valid = Bool()
  val dispatchNUM = new OrderQueuePtr///UInt(log2Up(OrderQueueSize).W)///第N条指令在OrderQueue里的序号
  val validNext = Bool()///第一条指令序号为N,那么第一个valid就是rs里这第N条能不能发射给执行单元，
}

class OrderQueueIO extends Bundle {
  val in  = Vec(2, Flipped(ValidIO(new MicroOp)))
  val rs_num = Vec(2, Input(UInt(log2Up(ExuNum).W)))
  val enqPtr = Vec(2, Output(new OrderQueuePtr))
  val out = Output(new RSDispatch)
}

class OrderQueueData extends Bundle {
  val valid  = Bool()
  val rs_num = UInt(log2Up(ExuNum).W)
  val pdest = UInt(PhyRegIdxWidth.W)
  val rfWen = Bool()
  val needLast = Bool()//是否需要上一条指令
}

class OrderQueuePtr extends CircularQueuePtr[OrderQueuePtr](OrderQueueSize) with HasCircularQueuePtrHelper{
  override def cloneType = (new OrderQueuePtr).asInstanceOf[this.type]
}

class OrderQueue extends Module with Config with HasCircularQueuePtrHelper {
  val io = IO(new OrderQueueIO)

  val data = Mem(OrderQueueSize, new OrderQueueData)

  val enq_vec = RegInit(VecInit((0 until 2).map(_.U.asTypeOf(new OrderQueuePtr))))///循环指针，enq发射阶段进来的信号在orderqueue的位置
  val deq_vec = RegInit(VecInit((0 until 2).map(_.U.asTypeOf(new OrderQueuePtr))))///出列的指针

  val isEmpty = enq_vec(0) === deq_vec(0)

  //enqueue
  for (i <- 0 until 2) {
    val pre_pdest = data(enq_vec(i).value-1.U).pdest////1.W???
    val rfWen = data(enq_vec(i).value-1.U).rfWen

    data(enq_vec(i).value).valid := io.in(i).valid && io.in(0).valid
    data(enq_vec(i).value).rs_num := io.rs_num(i)
    data(enq_vec(i).value).pdest := io.in(i).bits.pdest
    data(enq_vec(i).value).rfWen := io.in(i).bits.ctrl.rfWen
    data(enq_vec(i).value).needLast := data(enq_vec(i).value-1.U).valid && rfWen && ((pre_pdest === io.in(i).bits.psrc(0) && io.in(i).bits.ctrl.src1Type === SrcType1.reg)||(pre_pdest === io.in(i).bits.psrc(1) && io.in(i).bits.ctrl.src2Type === SrcType2.reg))
  }

  for (i <- 0 until 2){
    io.enqPtr(i) := enq_vec(i)
  }

  val vaild_enq = VecInit(io.in.map(_.valid))
  enq_vec := VecInit(enq_vec.map(_ + PopCount(vaild_enq)))

  //dequeue

  io.out.dispatchNUM := deq_vec(0)
  val validEntries = distanceBetween(enq_vec(0), deq_vec(0))///判断Orderqueue里还有几个元素
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