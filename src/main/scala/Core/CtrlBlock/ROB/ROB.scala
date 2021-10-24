package Core.CtrlBlock.ROB

import Core.{CommitIO, Config, CsrCommitIO, ExuCommit, MicroOp, MisPredictIO, PcInst, RedirectIO, TrapIO}
import Core.ExuBlock.FU.{BRUOpType, CsrRegDefine}
import Core.Config.robSize
import Core.CtrlBlock.IDU.{FuncOpType, FuncType}
import Core.ExuBlock.FU
import difftest.{DiffCSRStateIO, DifftestArchEvent, DifftestCSRState, DifftestInstrCommit, DifftestTrapEvent}
import chisel3._
import chisel3.util._
import chisel3.util.experimental.BoringUtils
import utils._

object MOUOpType {
  def fence  = "b00".U
  def fencei = "b01".U
  def sfence_vma = "b10".U
}

class ROBIO extends Bundle with Config {//todo:
  val in  = Vec(2, Flipped(ValidIO(new MicroOp)))
  val enqPtr = Vec(2, Output(new ROBPtr))
  val can_allocate = Output(Bool())

  val exuCommit = Vec(ExuNum,Flipped(ValidIO(new ExuCommit)))
  val redirect = Flipped(ValidIO(new RedirectIO))//BRU告诉ROB
  val commit = Vec(2,ValidIO(new CommitIO))
  val flush_out = Output(Bool())

  val predict = Output(new ROBPtr)//预测执行的分支指令位置

  //todo:isbranch;br_taken用没用分支指令里的立即数；bru里面加判断是否mispredict;microop里有isbranch
  //io.in(i).bits.ctrl.funcType===FuncType.bru
}

class ROB_data extends Bundle with Config {
  val pc    : UInt = UInt(XLEN.W)
  val instr  : UInt = UInt(INST_WIDTH)
  val ROBIdx     = new ROBPtr
  val is_br      = Bool()
  val funcOpType = FuncOpType.uwidth
  val funcType   = FuncType.uwidth
  val pdest      = UInt(PhyRegIdxWidth.W)
  val old_pdest  = UInt(PhyRegIdxWidth.W)
  val rfrd       = UInt(5.W)
  val rfWen      = Bool()

}


class ROBPtr extends CircularQueuePtr[ROBPtr](robSize) with HasCircularQueuePtrHelper{
  override def cloneType = (new ROBPtr).asInstanceOf[this.type]
}


class ROB(is_sim: Boolean) extends Module with Config with HasCircularQueuePtrHelper {
  val io = IO(new ROBIO)//todo:根据错误预测的分支指令是否提交给IBF信号能否输出

  val skip      = RegInit(VecInit(Seq.fill(robSize)(false.B)))
  val valid     = RegInit(VecInit(Seq.fill(robSize)(false.B)))
  val wb        = RegInit(VecInit(Seq.fill(robSize)(false.B)))//todo:添加isbranch寄存器
  val mispred   = RegInit(VecInit(Seq.fill(robSize)(false.B)))//todo:等到分支指令robIdx走到第一个再冲刷
  val res       = RegInit(VecInit(Seq.fill(robSize)(0.U(XLEN.W))))
  val data      = RegInit(VecInit(Seq.fill(robSize)(0.U.asTypeOf(new ROB_data))))
  val csrState  = RegInit(VecInit(Seq.fill(robSize)(0.U.asTypeOf(Flipped(new CsrCommitIO)))))

  val enq_vec = RegInit(VecInit((0 until 2).map(_.U.asTypeOf(new ROBPtr))))///循环指针，enq发射阶段进来的信号在orderqueue的位置
  val deq_vec = RegInit(VecInit((0 until 2).map(_.U.asTypeOf(new ROBPtr))))///出列的指针

  val validEntries = distanceBetween(enq_vec(0), deq_vec(0))

  val interruptVec : UInt = WireInit(0.U(TrapConfig.InterruptVecWidth.W))
  val interruptValid : Bool = interruptVec.orR()
  val resp_interrupt = valid(deq_vec(0).value) && interruptValid
  BoringUtils.addSink(interruptVec, "interruptVec")

  val respInter_reg = RegInit(false.B)
  when(resp_interrupt){
    respInter_reg := true.B
  }.otherwise{
    respInter_reg := false.B
  }


  val bru_flush = io.redirect.valid && io.redirect.bits.mispred
  when(bru_flush){
    mispred(io.redirect.bits.ROBIdx.value) := true.B
    for(i <- 0 until robSize) {
      when(isBefore(io.redirect.bits.ROBIdx,data(i).ROBIdx)){
        valid(i) := false.B
      }
    }
  }

  val br_pred = Wire(Vec(robSize,Bool()))
  val br_pred_reorder = Wire(Vec(robSize,Bool()))
  for(i <- 0 until robSize) {
    // val ret = data(i).ctrl.funcOpType === BRUOpType.jalr && data(i).cf.instr(11,7) === 0.U && (data(i).cf.instr(19,15) === 1.U || data(i).cf.instr(19,15) === 5.U)
    // br_pred(i) := valid(i) && (data(i).cf.is_br && !ret && data(i).ctrl.funcOpType =/= BRUOpType.jal) && !wb(i)
    br_pred(i) := valid(i) && (data(i).is_br && data(i).funcOpType =/= BRUOpType.jal) && !wb(i)
  }
  when(enq_vec(0).value > deq_vec(0).value){
    br_pred_reorder := DontCare
    val pred_num = ParallelPriorityEncoder(br_pred)
    io.predict := Mux(br_pred.asUInt.orR, data(pred_num).ROBIdx, enq_vec(0))
  }.otherwise{
    for(i <- 0 until robSize){
      val k = Wire(UInt(log2Up(robSize).W))
      when(i.U < robSize.U - enq_vec(0).value){
        k := i.U + enq_vec(0).value
      }.otherwise{
        k := i.U + enq_vec(0).value - robSize.U
      }
      br_pred_reorder(i) := br_pred(k)
    }
    val pred_num = ParallelPriorityEncoder(br_pred_reorder)
    io.predict := Mux(br_pred_reorder.asUInt.orR, data((enq_vec(0)+pred_num).value).ROBIdx, enq_vec(0))
  }


  //enqueue

  val numEnq   = PopCount(io.in.map(_.valid))
  val can_allocate = RegInit(true.B)
  can_allocate  := (validEntries + numEnq + 2.U((2+log2Up(robSize)).W)) <= robSize.U

  val allowEnq = can_allocate && !bru_flush
  io.can_allocate := allowEnq

  for (i <- 0 until 2) {
    when(io.in(i).valid && io.in(0).valid && allowEnq){
      valid(enq_vec(i).value) := io.in(i).valid && io.in(0).valid && allowEnq
      wb(enq_vec(i).value) := false.B
      skip(enq_vec(i).value)            := false.B
      mispred(enq_vec(i).value)         := false.B
      data(enq_vec(i).value).pc         := io.in(i).bits.cf.pc
      data(enq_vec(i).value).instr      := io.in(i).bits.cf.instr
      data(enq_vec(i).value).is_br      := io.in(i).bits.cf.is_br
      data(enq_vec(i).value).rfrd       := io.in(i).bits.ctrl.rfrd
      data(enq_vec(i).value).rfWen      := io.in(i).bits.ctrl.rfWen
      data(enq_vec(i).value).funcOpType := io.in(i).bits.ctrl.funcOpType
      data(enq_vec(i).value).funcType   := io.in(i).bits.ctrl.funcType
      data(enq_vec(i).value).pdest      := io.in(i).bits.pdest
      data(enq_vec(i).value).old_pdest  := io.in(i).bits.old_pdest
      data(enq_vec(i).value).ROBIdx     := enq_vec(i)
    }
  }

  for (i <- 0 until 2){
    io.enqPtr(i) := enq_vec(i)
  }

  val vaild_enq = VecInit(io.in.map(_.valid && allowEnq))
  enq_vec := Mux(bru_flush, VecInit((1 until 3).map(io.redirect.bits.ROBIdx + _.U)) ,VecInit(enq_vec.map(_ + PopCount(vaild_enq))))

  //writeback
  for(i <- 0 until ExuNum){
    when(io.exuCommit(i).valid){
      wb(io.exuCommit(i).bits.ROBIdx.value) := true.B
      skip(io.exuCommit(i).bits.ROBIdx.value) := io.exuCommit(i).bits.skip
      res(io.exuCommit(i).bits.ROBIdx.value) := io.exuCommit(i).bits.res
    }
  }

  private val csrCommitIO = WireInit(0.U.asTypeOf(new CsrCommitIO))

  val currentCsrState = RegInit(csrCommitIO)

  BoringUtils.addSink(csrCommitIO, "difftestCsrCommitIO")
  when(io.exuCommit(0).valid){
    csrState(io.exuCommit(0).bits.ROBIdx.value) := csrCommitIO
    //printf("wb csr idx %d, mtvec %x\n",io.exuCommit(0).bits.ROBIdx.value, csrCommitIO.mtvec)
  }



  //dequeue
  val commitReady = Wire(Vec(2,Bool()))
  commitReady(0) := !resp_interrupt && !bru_flush && valid(deq_vec(0).value) && (wb(deq_vec(0).value) || data(deq_vec(0).value).funcType === FuncType.mou)
  commitReady(1) := !resp_interrupt && !bru_flush && valid(deq_vec(1).value) && (wb(deq_vec(1).value) || data(deq_vec(1).value).funcType === FuncType.mou) && commitReady(0)

  val commitIsCsr = Wire(Vec(2,Bool()))

  for(i <- 0 until 2){
    commitIsCsr(i) := data(deq_vec(i).value).funcType === FuncType.csr
  }

  val commitCsrState = Wire(new CsrCommitIO)
  // commitCsrState(0) := Mux(commitIsCsr(0), csrState(deq_vec(0).value),currentCsrState)
  // commitCsrState(1) := Mux(commitIsCsr(1), csrState(deq_vec(1).value),commitCsrState(0))

  when((commitReady(0) && commitIsCsr(0)) || (commitReady(1) && commitIsCsr(1))){
    currentCsrState := Mux((commitReady(1) && commitIsCsr(1)), csrState(deq_vec(1).value), csrState(deq_vec(0).value))
  }.elsewhen (respInter_reg) {
    currentCsrState := csrCommitIO  // 啥问题
  }
  commitCsrState := Mux((commitReady(1) && commitIsCsr(1)), csrState(deq_vec(1).value), Mux((commitReady(0) && commitIsCsr(0)),csrState(deq_vec(0).value),currentCsrState))

  for(i <- 0 until 2){
    io.commit(i).valid := commitReady(i)
    io.commit(i).bits.pdest := data(deq_vec(i).value).pdest
    io.commit(i).bits.old_pdest := data(deq_vec(i).value).old_pdest
    io.commit(i).bits.ldest := data(deq_vec(i).value).rfrd
    io.commit(i).bits.rfWen := data(deq_vec(i).value).rfWen
    when(commitReady(i)){
      valid(deq_vec(i).value) := false.B
      mispred(deq_vec(i).value) := false.B
    }
  }
  when((commitReady(0) && mispred(deq_vec(0).value)) || (commitReady(1) && mispred(deq_vec(1).value)) || respInter_reg){
    io.flush_out := true.B
  }.otherwise{
    io.flush_out := false.B
  }

  deq_vec := VecInit(deq_vec.map(_ + PopCount(commitReady)))

  val trap = WireInit(0.U.asTypeOf(new TrapIO))
  BoringUtils.addSource(trap, "ROBTrap")
  trap.interruptValid := resp_interrupt
  trap.interruptVec := interruptVec
  trap.epc   := data(deq_vec(0).value).pc
  trap.einst := data(deq_vec(0).value).instr
  trap.ROBIdx := (deq_vec(0) - 1.U)
  val trapMstatusReg = RegInit(commitCsrState.mstatus)
  trapMstatusReg := trapMstatusReg
  trap.mstatus := trapMstatusReg//RegNext(commitCsrState.mstatus)  // 中断使用待提交的mstatus状态，而不是CSR中的mstatus状态

  //difftest
  val cycleCnt = RegInit(0.U(64.W))
  cycleCnt := cycleCnt + 1.U
  val instrCnt = RegInit(0.U(64.W))
  instrCnt := instrCnt + PopCount(commitReady)

  if(is_sim) {
    for(i <- 0 until 2) {
      val instrCommit = Module(new DifftestInstrCommit)
      instrCommit.io.clock := clock
      instrCommit.io.coreid := 0.U
      instrCommit.io.index := i.U
      instrCommit.io.skip := RegNext(skip(deq_vec(i).value))
      instrCommit.io.isRVC := false.B
      instrCommit.io.scFailed := false.B

      instrCommit.io.valid := RegNext(commitReady(i))
      instrCommit.io.pc := RegNext(data(deq_vec(i).value).pc)
      instrCommit.io.instr := RegNext(data(deq_vec(i).value).instr)
      instrCommit.io.wen := RegNext(data(deq_vec(i).value).rfWen)
      instrCommit.io.wdata := RegNext(res(deq_vec(i).value))
      instrCommit.io.wdest := RegNext(data(deq_vec(i).value).rfrd)
    }
  }


  if (is_sim) {
    // DifftestInstr.valid或中断/异常 才比较CSR和regfile，以下实现保证提交的CSR始终正确
    //                        N+1         N               N+2                                >N+1        N+3       N+2
    val csrTrueCommit = Mux(RegNext(resp_interrupt) || RegNext(RegNext(resp_interrupt)), csrCommitIO, RegNext(commitCsrState))



    val difftestCSRState = Module(new DifftestCSRState)
    difftestCSRState.io.clock := clock
    difftestCSRState.io.coreid := 0.U
    difftestCSRState.io.priviledgeMode := csrTrueCommit.priviledgeMode
    difftestCSRState.io.mstatus := csrTrueCommit.mstatus
    difftestCSRState.io.sstatus := csrTrueCommit.sstatus
    difftestCSRState.io.mepc := csrTrueCommit.mepc
    difftestCSRState.io.sepc := csrTrueCommit.sepc
    difftestCSRState.io.mtval := csrTrueCommit.mtval
    difftestCSRState.io.stval := csrTrueCommit.stval
    difftestCSRState.io.mtvec := csrTrueCommit.mtvec
    difftestCSRState.io.stvec := csrTrueCommit.stvec
    difftestCSRState.io.mcause := csrTrueCommit.mcause
    difftestCSRState.io.scause := csrTrueCommit.scause
    difftestCSRState.io.satp := csrTrueCommit.satp
    difftestCSRState.io.mip := csrTrueCommit.mip
    difftestCSRState.io.mie := csrTrueCommit.mie
    difftestCSRState.io.mscratch := csrTrueCommit.mscratch
    difftestCSRState.io.sscratch := csrTrueCommit.sscratch
    difftestCSRState.io.mideleg := csrTrueCommit.mideleg
    difftestCSRState.io.medeleg := csrTrueCommit.medeleg
  }


  val hitTrap = Wire(Vec(2, Bool()))
  for(i <- 0 until 2){
    hitTrap(i) := data(deq_vec(i).value).instr === BigInt("0000006b",16).U && commitReady(i)
  }

  if (is_sim) {
    val difftestTrapEvent = Module(new DifftestTrapEvent)
    difftestTrapEvent.io.clock := clock
    difftestTrapEvent.io.coreid := 0.U
    difftestTrapEvent.io.valid := hitTrap(0) || hitTrap(1)
    difftestTrapEvent.io.code := 0.U
    difftestTrapEvent.io.pc := Mux(hitTrap(0), data(deq_vec(0).value).pc, data(deq_vec(1).value).pc)
    difftestTrapEvent.io.cycleCnt := cycleCnt
    difftestTrapEvent.io.instrCnt := instrCnt
  }




  // printf("ROB enqvalid %d %d, enq_vec %d %d\n", io.in(0).valid && allowEnq, io.in(1).valid && allowEnq, enq_vec(0).value, enq_vec(1).value)
  // printf("ROB deqvalid %d %d, deq_vec %d %d\n", commitReady(0), commitReady(1), deq_vec(0).value, deq_vec(1).value)
  // printf("ROB predict idx %d\n", io.predict.value)

  // printf("csr commit %d %d\n", (commitReady(0) && commitIsCsr(0)),(commitReady(1) && commitIsCsr(1)))
  // printf("commit tvec %x\n",commitCsrState.mtvec)
  // for(i <- 0 until robSize){
  //   printf("ROB %d: valid %d, wb %d, mispred %d, pc %x, inst %x, nospec %d, mtvec %x\n",i.U, valid(i), wb(i),mispred(i),data(i).cf.pc,data(i).cf.instr,isAfter(io.predict,data(i).ROBIdx),csrState(i).mtvec)
  // }



}


