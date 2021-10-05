package Core.ExuBlock.FU

import Core.CtrlBlock.ROB.ROBPtr
import Core.{Config, FuInPut, FuOutPut}
import chisel3._
import chisel3.util._
import chisel3.util.experimental.BoringUtils
import utils._


class DUIO extends Bundle {
  val in  = Flipped(ValidIO(new FuInPut))
  val out = ValidIO(new FuOutPut)
  val flush = Input(Bool())//l/d会改变数据，所以必须确认存在分支的位置，需要补充一个predict.ROBIdx，而其它EXU只需关注是否在mispred.ROBIdx之后
  val mispred_robPtr = Input(new ROBPtr)
  val DivIdle = Output(Bool())
}

class DivIO(val len: Int) extends Bundle {
  val in = Flipped(ValidIO(Vec(2, Output(UInt(len.W)))))
  val flush  = Input(Bool())
  //val sign = Input(Bool())
  val out = ValidIO(Output(UInt((len).W)))//todo: Now Quotient, don't need remainder
  //val res1 = Output(UInt((len).W))
  //val res2 = Output(UInt((len).W))
  val DivIdle = Output(Bool())
}

class Radix8Divider(len: Int = 64) extends Module {
  val io = IO(new DivIO(len))


  val s_idle :: s_shift :: s_compute ::  Nil = Enum(3)
  val state = RegInit(s_idle)
  //io.in.ready := (state === s_idle)
  val newReq = (state === s_idle) && io.in.fire()
  val (a, b) = (io.in.bits(0), io.in.bits(1))
  val shiftReg = Reg(UInt((len * 2).W))
  val (hi,lo) = (shiftReg(len * 2-1, len),shiftReg(len - 1, 0))

  val aReg = RegEnable(a, newReq)
  val bReg = RegEnable(b, newReq)

  val cnt = Reg(UInt(log2Up(len+1).W))

  when(newReq){
    val canSkipShift = (len.U | Log2(b)) - Log2(a)
    cnt := Mux(canSkipShift >= len.U, len.U, canSkipShift)
    state := s_shift
  }.elsewhen (state === s_shift) {
    shiftReg := aReg << cnt
    state := s_compute
  }.elsewhen (state === s_compute) {
    when(cnt === len.U){
      val sr1 = Wire(UInt(len.W))
      val x1enough = hi.asUInt >= bReg.asUInt
      sr1 := Mux(x1enough, hi - bReg, hi)
      shiftReg := Cat(sr1,lo(len-2,0),x1enough)
      state := s_idle
    }.elsewhen(cnt === (len-1).U){
      val sr2 = Wire(UInt(len.W))
      val sr1 = Wire(UInt(len.W))
      val x2enough = hi.asUInt >= bReg.asUInt
      sr2 := Cat(Mux(x2enough, hi - bReg, hi)(len - 2, 0), lo(len-1))
      val x1enough = sr2.asUInt >= bReg.asUInt
      sr1 := Mux(x1enough, sr2 - bReg, sr2)
      shiftReg := Cat(sr1,lo(len-3,0),x2enough,x1enough)
      state := s_idle
    }.elsewhen(cnt === (len-2).U){
      val sr4 = Wire(UInt(len.W))
      val sr2 = Wire(UInt(len.W))
      val sr1 = Wire(UInt(len.W))
      val x4enough = hi.asUInt >= bReg.asUInt
      sr4 := Cat(Mux(x4enough, hi - bReg, hi)(len - 2, 0), lo(len-1))
      val x2enough = sr4.asUInt >= bReg.asUInt
      sr2 := Cat(Mux(x2enough, sr4 - bReg, sr4)(len - 2, 0), lo(len-2))
      val x1enough = sr2.asUInt >= bReg.asUInt
      sr1 := Mux(x1enough, sr2 - bReg, sr2)
      shiftReg := Cat(sr1, lo(len-4,0), x4enough, x2enough, x1enough)
      state := s_idle
    }.otherwise{
      val sr4 = Wire(UInt(len.W))
      val sr2 = Wire(UInt(len.W))
      val sr1 = Wire(UInt(len.W))
      val x4enough = hi.asUInt >= bReg.asUInt
      sr4 := Cat(Mux(x4enough, hi - bReg, hi)(len - 2, 0), lo(len-1))
      val x2enough = sr4.asUInt >= bReg.asUInt
      sr2 := Cat(Mux(x2enough, sr4 - bReg, sr4)(len - 2, 0), lo(len-2))
      val x1enough = sr2.asUInt >= bReg.asUInt
      sr1 := Cat(Mux(x1enough, sr2 - bReg, sr2)(len - 2, 0), lo(len-3))
      shiftReg := Cat(sr1, lo(len-4,0), x4enough, x2enough, x1enough)
      cnt := cnt + 3.U
    }
  }
  val kill = state=/=s_idle && io.flush
  when(kill){
    state := s_idle
  }
  io.out.valid := RegNext(state) === s_compute && state === s_idle && RegNext(!io.flush)
  io.DivIdle := (state === s_idle)//todo:compare through ROBIdx
  io.out.bits := shiftReg(len-1,0)
  //io.res1 := shiftReg(len-1,0)
  //io.res2 := shiftReg(len*2-1,len)//remainder

}

class DU extends Module with Config with HasCircularQueuePtrHelper {
  val io   = IO(new DUIO)
  val div  = Module(new Radix8Divider(XLEN))
  val src1 = Wire(UInt(XLEN.W))
  val src2 = Wire(UInt(XLEN.W))
  val funcOpType = io.in.bits.uop.ctrl.funcOpType
  val isDiv = MDUOpType.isDiv(funcOpType)
  val isDivSign = MDUOpType.isDivSign(funcOpType)
  val isW = MDUOpType.isW(funcOpType)
  val src = new MDUbit(UInt(XLEN.W))
  val ROBIdx = Reg(new ROBPtr)
  when(io.in.valid){
    ROBIdx := io.in.bits.uop.ROBIdx
  }//in RS_DU, ensure that when io.in.valid, last is done

  src1 := io.in.bits.src(0)
  src2 := io.in.bits.src(1)
  def isMinus64(x:UInt):Bool = x(XLEN-1)  //通过补码判断是否为负数
  def isMinus32(x:UInt):Bool = x(32-1)

  div.io.in.valid := io.in.valid
  div.io.flush := io.flush

  val (resMinus:Bool) = LookupTree(funcOpType, List(
    MDUOpType.div     ->   (isMinus64(src1) ^ isMinus64(src2)),
    MDUOpType.divu    ->   false.B,
    MDUOpType.divw    ->   (isMinus32(src1) ^ isMinus32(src2)),
    MDUOpType.divuw   ->   false.B
  ))

//  val (div.io.in.bits) = LookupTree(funcOpType, List(
//    MDUOpType.div     ->   (src.single(src1), src.single(src2)),
//    MDUOpType.divu    ->   (src1, src2),
//    MDUOpType.divw    ->   (src.half(src1), src.half(src2)),
//    MDUOpType.divuw   ->   (src1(31,0), src2(31,0)),
//  ))
  div.io.in.bits(0) := LookupTree(funcOpType, List(
    MDUOpType.div     ->   src.single(src1),
    MDUOpType.divu    ->   src1,
    MDUOpType.divw    ->   src.half(src1),
    MDUOpType.divuw   ->   src1(31,0)
  ))
  div.io.in.bits(1) := LookupTree(funcOpType, List(
    MDUOpType.div     ->   src.single(src2),
    MDUOpType.divu    ->   src2,
    MDUOpType.divw    ->   src.half(src2),
    MDUOpType.divuw   ->   src2(31,0)
  ))

  val res = Mux(resMinus, -div.io.out.bits, div.io.out.bits)
//  val res = LookupTree(funcOpType, List(
//    MDUOpType.div     ->   res1(63,0),
//    MDUOpType.divu    ->   res1(63,0),
//    MDUOpType.divw    ->   res1(31,0),
//    MDUOpType.divuw   ->   res1(31,0)
//  ))

  io.out.bits.res := Mux(isW, SignExt(res(31,0), 64), res)

  io.out.bits.uop := io.in.bits.uop
  io.out.valid    := div.io.out.valid && (!io.flush || (io.flush && isAfter(io.mispred_robPtr,ROBIdx)))
  //是flush当拍直接kill，不会给出div.io.out.valid
  io.DivIdle    := div.io.DivIdle

}


