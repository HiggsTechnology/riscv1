package Core.ExuBlock.RS

import Core.Config.{OrderQueueSize, PhyRegIdxWidth, XLEN}
import Core.ExuBlock.OrderQueue
import Core.ExuBlock.OrderQueue.{OrderQueuePtr, RSDispatch}
import Core.{CommitIO, FuInPut, FuOutPut, MicroOp}
import chisel3._
import chisel3.util._
import utils._


trait HasRSConst{
  // val rsSize = 4
  val rsCommitWidth = 1///
}


/////In: MicroOp Src DispathOrder ExuResult
/////Out: MicroOp Src
////empty、full,以后需要加空位数、
// Reservation Station for Out Of Order Execution Backend
///rsNum指令进入序号，
class RS(size: Int = 2, rsNum: Int = 0, nFu: Int = 5, dispatchSize: Int =2, name: String = "unnamedRS") extends Module with HasRSConst {
  val io = IO(new Bundle {
    //in
    val in = Flipped(ValidIO(new MicroOp))/////封装
    ///val Enqptr = Input(UInt(log2Up(OrderQueueSize).W))///////入列指针，本次入列指令的编号
    val SrcIn = Vec(2,Input(UInt(XLEN.W)))///Src, valid在MicroOp
    //侦听，，比较rs valid是要true，然后去比pdest与psrc是不是对的，srcState一定要false才能写。即三个条件，for循环同时做
    val ExuResult = Vec(2, Flipped(ValidIO(new FuOutPut)))///new 物理地址prf、写回结果、valid
    //out
    val DispatchOrder = Input(new RSDispatch)///发射指令编号，当前指令valid、下一条valid////for循环对比（dispatchNUM与保留站Enqptr是否一致,rs_valid order_valid）｜｜（dispatchNUM+1与保留站Enqptr是否一致 rs_valid order_next_valid）//几位布尔选择策略
    ///找到序号后，检查操作数是否准备好，做个前递选择通路，一旦srcState是false，就把ExuResult。。三选一，一种是保留站数据，还有两个是ExuResult的1和2
    val out = ValidIO(Flipped(new FuInPut))///Vec(2, ValidIO(Flipped(new FuInPut)))//不是一进一出吗
    ///val out = Decoupled(new MicroOp)///
    ///val SrcOut = Vec(2,Output(UInt(XLEN.W)))///Src输出, valid在MicroOp
    ////val emptySize = Output(UInt(log2Up(size).W))///output if RS is empty, io.empty :=rsEmpty
    val full = Output(Bool())///output if RS is full,>=2
  })///一次发射两条，保留站接收一条，考虑保留站加个队列

  ////need
  val rsSize = size
  val decode  = Mem(rsSize, Flipped(new MicroOp)) ///Mem(rsSize, Flipped(new FuInPut))给改成MicroOp了
  val valid   = RegInit(VecInit(Seq.fill(rsSize)(false.B)))
  val srcState1 = RegInit(VecInit(Seq.fill(rsSize)(false.B)))
  val srcState2 = RegInit(VecInit(Seq.fill(rsSize)(false.B)))
  ///val psrc1 = Reg(Vec(rsSize, UInt(PhyRegIdxWidth.W)))///物理地址
  ///val psrc2 = Reg(Vec(rsSize, UInt(PhyRegIdxWidth.W)))
  val src1 = Reg(Vec(rsSize, UInt(XLEN.W)))
  val src2 = Reg(Vec(rsSize, UInt(XLEN.W)))
  ///val Enqptr = Reg(Vec(rsSize, UInt(log2Up(OrderQueueSize).W)))
  val instRdy = WireInit(VecInit(List.tabulate(rsSize)(i => srcState1(i) && srcState2(i) && valid(i))))///

  val isSecondSeq = WireInit(VecInit(Seq.fill(rsSize)(false.B)))
  ////val dispatchNUM = Wire(UInt(log2Up(OrderQueueSize).W))
  ///val rsEmptySize = rsSize.asUInt - ParallelAND(valid.asUInt) //上一拍的空位
  ///val rsEmpty = !valid.asUInt.orR///
  val rsFull = valid.asUInt.andR///
  ///val priorityMask = RegInit(VecInit(Seq.fill(rsSize)(VecInit(Seq.fill(rsSize)(false.B)))))//定义了二阶向量的寄存器向量
  ////need

  ///val rsAllowin = !rsFull

  // RS enqueue
  ///io.in.ready := rsAllowin
  val enqueueSelect = ParallelPriorityEncoder(valid.map(!_))

  //侦听执行单元结果
  for (i <- 0 until rsSize){
    for(j <- 0 until 2){
      when(valid(i) && io.ExuResult(j).valid && (io.ExuResult(j).bits.uop.pdest === decode(i).psrc(0)) && (srcState1(i)===false.B)){
        src1(i) := io.ExuResult(j).bits.res
        srcState1(i) := true.B
      }
      when(valid(i) && io.ExuResult(j).valid && (io.ExuResult(j).bits.uop.pdest === decode(i).psrc(1)) && (srcState2(i)===false.B)){
        src2(i) := io.ExuResult(j).bits.res
        srcState2(i) := true.B

      }
    }
  }

  when(io.in.valid){
    decode(enqueueSelect) := io.in.bits
    valid(enqueueSelect) := true.B////
    srcState1(enqueueSelect) := io.in.bits.srcState(0)
    srcState2(enqueueSelect) := io.in.bits.srcState(1)
    src1(enqueueSelect) := io.SrcIn(0)
    src2(enqueueSelect) := io.SrcIn(1)

    for(i <- 0 until 2){
      when(io.ExuResult(i).valid && (io.ExuResult(i).bits.uop.pdest === io.in.bits.psrc(0))&&io.in.bits.srcState(0)===false.B){
        src1(enqueueSelect) := io.ExuResult(i).bits.res
        srcState1(enqueueSelect) := true.B
      }
      when(io.ExuResult(i).valid && (io.ExuResult(i).bits.uop.pdest === io.in.bits.psrc(1))&&io.in.bits.srcState(1)===false.B){
        src2(enqueueSelect) := io.ExuResult(i).bits.res
        srcState2(enqueueSelect) := true.B
      }
    }
  }

  // RS dequeue  ////所有执行单元的输出结果若valid，物理寄存器地址和所有源寄存器物理地址对比，一旦相等，结果写到保留站，同时srcState置为true
  ///dispatchNUM := io.DispatchOrder.dispatchNUM
  val dequeueSelectBool = Wire(Vec(rsSize,Bool()))
  ///val srcbusSeq = VecInit(Seq.fill(rsSize)(VecInit(Seq.fill(2)(false.B))))//
  for (i <- 0 until rsSize) {
    dequeueSelectBool(i) := (decode(i).OQIdx===io.DispatchOrder.dispatchNUM && valid(i) && io.DispatchOrder.valid)||((decode(i).OQIdx===(io.DispatchOrder.dispatchNUM+1.U)) && valid(i) && io.DispatchOrder.validNext)///位亦或，之后调试
    isSecondSeq(i) := ((decode(i).OQIdx===(io.DispatchOrder.dispatchNUM+1.U)) && valid(i) && io.DispatchOrder.validNext)
  }
  ///val dequeueSelect = Wire(UInt(log2Up(size).W))//log2Up用以设定位宽
  val dequeueSelect = ParallelPriorityEncoder(dequeueSelectBool)//返回相等的编号////PriorityEncoder(instRdy)注意，这几步的线变量是否会多余？？
  val dispatchReady = instRdy(dequeueSelect) ///instRdy(dispatchNUM)///  ///数据类型是否有问题？
  val isSecond = isSecondSeq(dequeueSelect)

  io.out.bits := DontCare
  io.out.valid := false.B
  when(dispatchReady) {
    io.out.valid := dispatchReady////rsReadygo // && validNext(dequeueSelect)
    io.out.bits.uop := decode(dequeueSelect)
    io.out.bits.src(0) := src1(dequeueSelect)
    io.out.bits.src(1) := src2(dequeueSelect)
    io.out.bits.isSecond := isSecond
    //释放当前工作站
    valid(dequeueSelect) := false.B
    ////io.emptySize := rsEmptySize///这一拍释放后的空位，debug时打印看一下
  }

  ///io.empty := rsEmpty
  io.full := rsFull

  // printf("rsNum:%d enq, valid %d, pc %x, inst %x, OQ %d\n",rsNum.U, io.in.valid, io.in.bits.cf.pc, io.in.bits.cf.instr, io.in.bits.OQIdx.value)


  // printf("rsNum:%d, rs_disNum %d, valid %d, isSecond %d,\n",rsNum.U,dequeueSelect,io.out.valid,io.out.bits.isSecond)
  // for(i <- 0 until size){
  //   printf("rsNum:%d, %d: valid %d, src1 %d %x, src2 %d %x, OQ %d\n",rsNum.U,i.U,valid(i),srcState1(i),src1(i),srcState2(i),src2(i),decode(i).OQIdx.value)
  //   printf("pc %x, inst %x \n",decode(i).cf.pc,decode(i).cf.instr)
  // }
  if(rsNum==1){
    for(i <- 0 until size){
      printf("rsNum:%d, %d: valid %d, src1 %d %x, src2 %d %x, OQ %d\n",rsNum.U,i.U,valid(i),srcState1(i),src1(i),srcState2(i),src2(i),decode(i).OQIdx.value)
      printf("pc %x, inst %x \n",decode(i).cf.pc,decode(i).cf.instr)
    }
  }

}


