package Core.ExuBlock.RS

import Core.Config.{ExuNum, rsSize}
import Core.CtrlBlock.IDU.FuncType
import Core.CtrlBlock.ROB.ROBPtr
import Core.ExuBlock.ExuBlockConfig.{JumpRsBruNo, JumpRsCsrNo}
import Core.{Config, FuInPut, FuOutPut, MicroOp}
import chisel3._
import chisel3.util._
import utils._


class RSPtr extends CircularQueuePtr[RSPtr](rsSize) with HasCircularQueuePtrHelper{
  override def cloneType = (new RSPtr).asInstanceOf[this.type]
}

/**
 * 顺序执行使用的保留站
 * 其实只有Jump在用，Todo: 考虑更名
 * @param slave_num: 执行单元数
 */
class RsInorderIO(slave_num: Int) extends Bundle with Config{
  //in
  val in : ValidIO[MicroOp] = Flipped(ValidIO(new MicroOp))
  val SrcIn : Vec[UInt] = Vec(2,Input(UInt(XLEN.W)))
  val ExuResult : Vec[ValidIO[FuOutPut]] = Vec(ExuNum, Flipped(ValidIO(new FuOutPut)))
  val out : Vec[ValidIO[FuInPut]] = Vec(slave_num, ValidIO(Flipped(new FuInPut)))
  val full : Bool = Output(Bool())
  val flush : Bool = Input(Bool())
  val mispred_robPtr : ROBPtr = Input(new ROBPtr)
}

class RsInorder(
  slave_num: Int,
  size: Int = 2,
  rsNum: Int = 0,
  nFu: Int = ExuNum,
  dispatchSize: Int =2,
  name: String = "unnamedRS"
) extends Module with Config with HasCircularQueuePtrHelper {
  val io = IO(new RsInorderIO(slave_num = slave_num))
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
    for(j <- 0 until ExuNum){
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
    for(i <- 0 until ExuNum){
      val exurfWen    = io.ExuResult(i).bits.uop.ctrl.rfWen
      val psrc1Rdy = io.ExuResult(i).bits.uop.pdest === io.in.bits.psrc(0)
      val psrc2Rdy = io.ExuResult(i).bits.uop.pdest === io.in.bits.psrc(1)
      when(io.ExuResult(i).valid && exurfWen && psrc1Rdy && !io.in.bits.srcState(0)){
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

  private val to_bru = io.out(JumpRsBruNo)
  private val to_csr = io.out(JumpRsCsrNo)

  // Todo: 简化端口定义，解决这个DontCare
  to_bru.bits := DontCare
  to_csr.bits := DontCare
  when(dispatchReady) {
    when (decode(deq_vec.value).ctrl.funcType === FuncType.bru) {
      to_bru.valid := true.B
      to_bru.bits.uop := decode(deq_vec.value)
      to_bru.bits.src(0) := src1(deq_vec.value)
      to_bru.bits.src(1) := src2(deq_vec.value)
      valid(deq_vec.value) := false.B
      deq_vec := deq_vec + 1.U
      to_csr := 0.U.asTypeOf(ValidIO(new FuInPut))
    }.elsewhen(decode(deq_vec.value).ctrl.funcType === FuncType.csr) {
      to_csr.valid := true.B
      to_csr.bits.uop := decode(deq_vec.value)
      to_csr.bits.src(0) := src1(deq_vec.value)
      to_csr.bits.src(1) := src2(deq_vec.value)
      valid(deq_vec.value) := false.B
      deq_vec := deq_vec + 1.U
      to_bru := 0.U.asTypeOf(ValidIO(new FuInPut))
    }.otherwise {
      to_csr := 0.U.asTypeOf(ValidIO(new FuInPut))
      to_bru := 0.U.asTypeOf(ValidIO(new FuInPut))
      assert(false.B, "RsInorder get invalid funcType: %d", io.in.bits.ctrl.funcType)
    }
  }.otherwise{
    to_bru.valid := false.B
    to_csr.valid := false.B
  }

  io.full := rsFull

  //     printf("RS inorder enq %d, enqvalid %d, deq %d, deqvalid %d\n",enq_vec.value, io.in.valid && !io.full, deq_vec.value, dispatchReady)

  //     for(i <- 0 until size){
  //        printf("rsNum:%d, %d: valid %d, src1 %d %x, src2 %d %x, ROB %d\n",rsNum.U,i.U,valid(i),srcState1(i),src1(i),srcState2(i),src2(i),decode(i).ROBIdx.value)
  //        printf("pc %x, inst %x \n",decode(i).cf.pc,decode(i).cf.instr)
  //     }



}