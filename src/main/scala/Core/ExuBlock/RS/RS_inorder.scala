package Core.ExuBlock.RS

import Core.Config.rsSize
import Core.Config.{OrderQueueSize, PhyRegIdxWidth, XLEN}
import Core.CtrlBlock.ROB.ROBPtr
import Core.{CommitIO, Config, FuInPut, FuOutPut, MicroOp}
import chisel3._
import chisel3.util._
import utils._


class RSPtr extends CircularQueuePtr[RSPtr](rsSize) with HasCircularQueuePtrHelper{
  override def cloneType = (new RSPtr).asInstanceOf[this.type]
}

class RS_inorder(size: Int = 2, rsNum: Int = 0, nFu: Int = 5, dispatchSize: Int =2, name: String = "unnamedRS") extends Module with Config with HasCircularQueuePtrHelper {
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


  val enq_vec = RegInit(0.U.asTypeOf(new RSPtr))
  val deq_vec = RegInit(0.U.asTypeOf(new RSPtr))

  val rsFull = valid.asUInt.andR

  for (i <- 0 until rsSize){
    for(j <- 0 until 6){
      val monitorValid = valid(i) && io.ExuResult(j).valid
      val exurfWen    = io.ExuResult(j).bits.uop.ctrl.rfWen
      val psrc1Rdy = io.ExuResult(j).bits.uop.pdest === decode(i).psrc(0)
      val psrc2Rdy = io.ExuResult(j).bits.uop.pdest === decode(i).psrc(1)
      when(monitorValid && exurfWen && psrc1Rdy && !srcState1(i)){
        src1(i) := io.ExuResult(j).bits.res
        srcState1(i) := true.B
      }
      when(monitorValid && exurfWen &&  psrc2Rdy && !srcState2(i)){
        src2(i) := io.ExuResult(j).bits.res
        srcState2(i) := true.B
      }
    }
  }


  val flushed = WireInit(VecInit(Seq.fill(rsSize)(false.B)))
  when(io.flush){
    for(i <- 0 until rsSize) {
      when(isBefore(io.mispred_robPtr,decode(i).ROBIdx)){
        valid(i) := false.B
        flushed(i) := valid(i) && deq_vec.value =/= i.U
      }
    }
  }

  when(io.in.valid && !io.full){
    decode(enq_vec.value) := io.in.bits
    valid(enq_vec.value) := true.B
    srcState1(enq_vec.value) := io.in.bits.srcState(0)
    srcState2(enq_vec.value) := io.in.bits.srcState(1)
    src1(enq_vec.value) := io.SrcIn(0)
    src2(enq_vec.value) := io.SrcIn(1)

    //入列指令侦听当拍执行单元
    for(i <- 0 until 6){
      val exurfWen    = io.ExuResult(i).bits.uop.ctrl.rfWen
      val psrc1Rdy = io.ExuResult(i).bits.uop.pdest === io.in.bits.psrc(0)
      val psrc2Rdy = io.ExuResult(i).bits.uop.pdest === io.in.bits.psrc(1)
      when(io.ExuResult(i).valid && exurfWen &&  psrc1Rdy && !io.in.bits.srcState(0)){
        src1(enq_vec.value) := io.ExuResult(i).bits.res
        srcState1(enq_vec.value) := true.B
      }
      when(io.ExuResult(i).valid && exurfWen && psrc2Rdy && !io.in.bits.srcState(1)){
        src2(enq_vec.value) := io.ExuResult(i).bits.res
        srcState2(enq_vec.value) := true.B
      }
    }
  }

  enq_vec := Mux(io.flush, enq_vec - PopCount(flushed), enq_vec + (io.in.valid && !io.full))

  val dispatchReady = srcState1(deq_vec.value) && srcState2(deq_vec.value) && valid(deq_vec.value)

  io.out.bits := DontCare
  io.out.valid := false.B
  when(dispatchReady) {
    io.out.valid := dispatchReady
    io.out.bits.uop := decode(deq_vec.value)
    io.out.bits.src(0) := src1(deq_vec.value)
    io.out.bits.src(1) := src2(deq_vec.value)
    valid(deq_vec.value) := false.B
    deq_vec := deq_vec + 1.U
  }

  io.full := rsFull

  //     printf("RS inorder enq %d, enqvalid %d, deq %d, deqvalid %d\n",enq_vec.value, io.in.valid && !io.full, deq_vec.value, dispatchReady)

  //     for(i <- 0 until size){
  //        printf("rsNum:%d, %d: valid %d, src1 %d %x, src2 %d %x, ROB %d\n",rsNum.U,i.U,valid(i),srcState1(i),src1(i),srcState2(i),src2(i),decode(i).ROBIdx.value)
  //        printf("pc %x, inst %x \n",decode(i).cf.pc,decode(i).cf.instr)
  //     }



}