package Core.ExuBlock.RS

import Core.Config.{OrderQueueSize, PhyRegIdxWidth, XLEN}
import Core.CtrlBlock.ROB.ROBPtr
import Core.{CommitIO, Config, FuInPut, FuOutPut, MicroOp}
import chisel3._
import chisel3.util._
import utils._





class RS(size: Int = 2, rsNum: Int = 0, nFu: Int = 5, dispatchSize: Int =2, name: String = "unnamedRS") extends Module with Config with HasCircularQueuePtrHelper {
  val io = IO(new Bundle {
    //in
    val in = Flipped(ValidIO(new MicroOp))

    val SrcIn = Vec(2,Input(UInt(XLEN.W)))

    val ExuResult = Vec(6, Flipped(ValidIO(new FuOutPut)))

    val out = ValidIO(Flipped(new FuInPut))

    val full = Output(Bool())

    val flush = Input(Bool())
    val mispred_robPtr = Input(new ROBPtr)
  })

  val decode  = Mem(rsSize, new MicroOp)
  val valid   = RegInit(VecInit(Seq.fill(rsSize)(false.B)))
  val srcState1 = RegInit(VecInit(Seq.fill(rsSize)(false.B)))
  val srcState2 = RegInit(VecInit(Seq.fill(rsSize)(false.B)))

  val src1 = Reg(Vec(rsSize, UInt(XLEN.W)))
  val src2 = Reg(Vec(rsSize, UInt(XLEN.W)))

  val instRdy = WireInit(VecInit(List.tabulate(rsSize)(i => srcState1(i) && srcState2(i) && valid(i))))///

  val rsFull = valid.asUInt.andR


  val enqueueSelect = ParallelPriorityEncoder(valid.map(!_))
  val dequeueSelect = ParallelPriorityEncoder(instRdy)

  //侦听执行单元结果
  for (i <- 0 until rsSize){
    for(j <- 0 until 6){
      when(valid(i) && io.ExuResult(j).valid && io.ExuResult(j).bits.uop.ctrl.rfWen && (io.ExuResult(j).bits.uop.pdest === decode(i).psrc(0)) && (srcState1(i)===false.B)){
        src1(i) := io.ExuResult(j).bits.res
        srcState1(i) := true.B
      }
      when(valid(i) && io.ExuResult(j).valid && io.ExuResult(j).bits.uop.ctrl.rfWen &&  (io.ExuResult(j).bits.uop.pdest === decode(i).psrc(1)) && (srcState2(i)===false.B)){
        src2(i) := io.ExuResult(j).bits.res
        srcState2(i) := true.B

      }
    }
  }

  when(io.in.valid){
    decode(enqueueSelect) := io.in.bits
    valid(enqueueSelect) := true.B
    srcState1(enqueueSelect) := io.in.bits.srcState(0)
    srcState2(enqueueSelect) := io.in.bits.srcState(1)
    src1(enqueueSelect) := io.SrcIn(0)
    src2(enqueueSelect) := io.SrcIn(1)

    for(i <- 0 until 6){
      when(io.ExuResult(i).valid && io.ExuResult(i).bits.uop.ctrl.rfWen &&  (io.ExuResult(i).bits.uop.pdest === io.in.bits.psrc(0))&&io.in.bits.srcState(0)===false.B){
        src1(enqueueSelect) := io.ExuResult(i).bits.res
        srcState1(enqueueSelect) := true.B
      }
      when(io.ExuResult(i).valid && io.ExuResult(i).bits.uop.ctrl.rfWen && (io.ExuResult(i).bits.uop.pdest === io.in.bits.psrc(1))&&io.in.bits.srcState(1)===false.B){
        src2(enqueueSelect) := io.ExuResult(i).bits.res
        srcState2(enqueueSelect) := true.B
      }
    }
  }

  when(io.flush){
    for(i <- 0 until rsSize) {
      when(isBefore(io.mispred_robPtr,decode(i).ROBIdx)){
        valid(i) := false.B
      }
    }
  }

  val dispatchReady = instRdy(dequeueSelect)

  io.out.bits := DontCare
  io.out.valid := false.B
  when(dispatchReady) {
    io.out.valid := dispatchReady
    io.out.bits.uop := decode(dequeueSelect)
    io.out.bits.src(0) := src1(dequeueSelect)
    io.out.bits.src(1) := src2(dequeueSelect)
    valid(dequeueSelect) := false.B
  }

  ///io.empty := rsEmpty
  io.full := rsFull

  // printf("rsNum:%d enq, valid %d, pc %x, inst %x, OQ %d\n",rsNum.U, io.in.valid, io.in.bits.cf.pc, io.in.bits.cf.instr, io.in.bits.OQIdx.value)


  // printf("rsNum:%d, rs_disNum %d, valid %d, isSecond %d,\n",rsNum.U,dequeueSelect,io.out.valid,io.out.bits.isSecond)
  // for(i <- 0 until size){
  //   printf("rsNum:%d, %d: valid %d, src1 %d %x, src2 %d %x, OQ %d\n",rsNum.U,i.U,valid(i),srcState1(i),src1(i),srcState2(i),src2(i),decode(i).OQIdx.value)
  //   printf("pc %x, inst %x \n",decode(i).cf.pc,decode(i).cf.instr)
  // }
//  if(rsNum==1){
//    for(i <- 0 until size){
//      printf("rsNum:%d, %d: valid %d, src1 %d %x, src2 %d %x, OQ %d\n",rsNum.U,i.U,valid(i),srcState1(i),src1(i),srcState2(i),src2(i),decode(i).OQIdx.value)
//      printf("pc %x, inst %x \n",decode(i).cf.pc,decode(i).cf.instr)
//    }
//  }

}


