package Core.ExuBlock

import Core.Config.PhyRegIdxWidth
import Core.ExuBlock.FU.{ALU, BRU, CSR, LSU}
import Core.CtrlBlock.IDU.{SrcType1, SrcType2}
import Core.ExuBlock.MemReg.Regfile
import Core.ExuBlock.OrderQueue.OrderQueue
import Core.ExuBlock.RS.RS
import Core.{BRU_OUTIO, CommitIO, Config, FuOutPut, MicroOp}
import chisel3._
import chisel3.util._
import difftest.{DifftestArchIntRegState, DifftestInstrCommit}
import utils._



class ExuBlockIO extends Bundle with Config {
  val in = Vec(2, Flipped(ValidIO(new MicroOp)))///此模块里，DispatchQueue在外部，给到OrderQueue
  val rs_num_in = Vec(2, Input(UInt(log2Up(ExuNum).W)))///此模块里，给到rs的序号，OrderQueueBook
  val busytablein = Vec(4,Input(Bool()))///0、1、3、4两条指令一个Commit、发射出来指令的物理地址到Busytable

  val redirect  = ValidIO(new BRU_OUTIO)///BRU可能Redirect_OUTIO,与朱航他们讨论
  val out = Vec(2,ValidIO(new CommitIO))///满足两条指令同时写回...decouple多一层是否准备好
  ///能用上val rs_emptySize = Vec(ExuNum,Output(UInt(log2Up(rsSize).W)))
  val rs_can_allocate = Vec(ExuNum,Output(Bool()))

  val debug_int_rat = Vec(32, Input(UInt(PhyRegIdxWidth.W)))
}

///1,,写到orderqueue,保留站,指针给保留站
///2,,orderq控制指令的发射
///3,,做执行单元运算，写回结果，包括写回保留站、重命名(包括busytable)、寄存器
class ExuBlock extends Module with Config{
  val io  = IO(new ExuBlockIO)
  //k i
  val csrrs = Module(new RS(size = rsSize, rsNum = 0, nFu = ExuNum, name = "CSRRS"))
  val brurs = Module(new RS(size = rsSize, rsNum = 1, nFu = ExuNum, name = "BRURS"))
  val alu1rs = Module(new RS(size = rsSize, rsNum = 2, nFu = ExuNum, name = "ALU1RS"))///nFu,循环判断是否为
  val alu2rs = Module(new RS(size = rsSize, rsNum = 3, nFu = ExuNum, name = "ALU2RS"))
  ///val lsurs = Module(new RS(size = rsSize, rsNum = 4, nFu = ExuNum, name = "LSURS"))
  val csr = Module(new CSR)
  val bru = Module(new BRU)
  val alu1 = Module(new ALU)
  val alu2 = Module(new ALU)
  ///val lsu = Module(new LSU)
  val orderqueue = Module(new OrderQueue)
  val preg = Module(new Regfile(4,2,128))///新写
  private val preg_data = Wire(Vec(2,Vec(2,UInt(XLEN.W))))
  private val src_in = Wire(Vec(2,Vec(2,UInt(XLEN.W))))
  private val ExuResult = Wire(Vec(2,ValidIO(new FuOutPut)))
  private val first_inst = Wire(Vec(6, Bool()))
  private val second_inst = Wire(Vec(6, Bool()))
  private val first_num =  ParallelPriorityEncoder(first_inst)
  private val second_num = ParallelPriorityEncoder(second_inst)
  private val FuOutPut_default = Wire(ValidIO(new FuOutPut))
  private val commit = Wire(Vec(2,ValidIO(new CommitIO)))
  ////val ReservationStaions = Seq(alu1rs,alu2rs,brurs,csrrs,lsurs)

  //val dispatchqueue = Module(new DispatchQueue)
  ///从目前设计来看，需要的是

  //orderqueue
  orderqueue.io.rs_num := io.rs_num_in
  orderqueue.io.in := io.in

  //读寄存器数据选择通路src1、src2
  //lq//RS应该需要侦听当前入队指令的物理寄存器
  for(i <- 0 until 2){
    preg.io.read(2*i).addr := io.in(i).bits.psrc(0)
    preg.io.read(2*i+1).addr := io.in(i).bits.psrc(1)
    preg_data(i)(0) := preg.io.read(2*i).data
    preg_data(i)(1) := preg.io.read(2*i+1).data
  }


  for(i <- 0 until 2){
    src_in(i)(0) := LookupTree(io.in(i).bits.ctrl.src1Type, List(
      SrcType1.reg  -> preg_data(i)(0),
      SrcType1.pc   -> io.in(i).bits.cf.pc,
      SrcType1.uimm -> io.in(i).bits.data.uimm_ext
    ))
    src_in(i)(1) := LookupTree(io.in(i).bits.ctrl.src2Type, List(
      SrcType2.reg  -> preg_data(i)(1),
      SrcType2.imm  -> io.in(i).bits.data.imm
    ))
  }

  csrrs.io.in := DontCare
  csrrs.io.in.valid := false.B
  csrrs.io.SrcIn := DontCare
  brurs.io.in := DontCare
  brurs.io.in.valid := false.B
  brurs.io.SrcIn := DontCare
  alu1rs.io.in := DontCare
  alu1rs.io.in.valid := false.B
  alu1rs.io.SrcIn := DontCare
  alu2rs.io.in := DontCare
  alu2rs.io.in.valid := false.B
  alu2rs.io.SrcIn := DontCare
  ///lsurs.io.in := DontCare
  ///lsurs.io.in.valid := false.B
  ///lsurs.io.SrcIn := DontCare

  for(i <- 0 until 2){
    when(io.rs_num_in(i)===0.U && io.in(i).valid){
      csrrs.io.in := io.in(i) //in orderqueue rs  读寄存器
      csrrs.io.in.bits.OQIdx := orderqueue.io.enqPtr(i)
      csrrs.io.in.bits.srcState(0) := io.busytablein(2*i) || (io.in(i).bits.ctrl.src1Type =/= SrcType1.reg)
      csrrs.io.in.bits.srcState(1) := io.busytablein(2*i+1) || (io.in(i).bits.ctrl.src2Type =/= SrcType2.reg)
      //寄存器的输入
      csrrs.io.SrcIn := src_in(i)
    }
    when(io.rs_num_in(i)===1.U && io.in(i).valid){
      brurs.io.in := io.in(i) //in orderqueue rs  读寄存器
      brurs.io.in.bits.OQIdx := orderqueue.io.enqPtr(i)
      brurs.io.in.bits.srcState(0) := io.busytablein(2*i)
      brurs.io.in.bits.srcState(1) := io.busytablein(2*i+1)
      //寄存器的输入
      brurs.io.SrcIn := src_in(i)
    }
    when(io.rs_num_in(i)===2.U && io.in(i).valid){
      alu1rs.io.in := io.in(i) //in orderqueue rs  读寄存器
      alu1rs.io.in.bits.OQIdx := orderqueue.io.enqPtr(i)
      alu1rs.io.in.bits.srcState(0) := io.busytablein(2*i)
      alu1rs.io.in.bits.srcState(1) := io.busytablein(2*i+1)
      //寄存器的输入
      alu1rs.io.SrcIn := src_in(i)
    }
    when(io.rs_num_in(i)===3.U && io.in(i).valid){
      alu2rs.io.in := io.in(i) //in orderqueue rs  读寄存器
      alu2rs.io.in.bits.OQIdx := orderqueue.io.enqPtr(i)
      alu2rs.io.in.bits.srcState(0) := io.busytablein(2*i)
      alu2rs.io.in.bits.srcState(1) := io.busytablein(2*i+1)
      //寄存器的输入
      alu2rs.io.SrcIn := src_in(i)
    }
    when(io.rs_num_in(i)===4.U && io.in(i).valid){
      ///lsurs.io.in := io.in(i) //in orderqueue rs  读寄存器
      ///alu2rs.io.in.bits.OQIdx := orderqueue.io.enqPtr(i)
      ///lsurs.io.in.bits.srcState(0) := io.busytablein(2*i)
      ///lsurs.io.in.bits.srcState(1) := io.busytablein(2*i+1)
      //寄存器的输入
      ///lsurs.io.SrcIn := src_in(i)
    }
  }

  //2,,orderq控制指令的发射
  csrrs.io.DispatchOrder := orderqueue.io.out
  brurs.io.DispatchOrder := orderqueue.io.out
  alu1rs.io.DispatchOrder := orderqueue.io.out
  alu2rs.io.DispatchOrder := orderqueue.io.out
  ///lsurs.io.DispatchOrder := orderqueue.io.out

  ///3,,做执行单元运算，写回结果，包括写回保留站、重命名(包括busytable)、寄存器

  //rs to exu
  //执行单元运算，有decouple，直接连接
  csr.io.in <> csrrs.io.out
  bru.io.in <> brurs.io.out
  alu1.io.in <> alu1rs.io.out
  alu2.io.in <> alu2rs.io.out
  ///lsu.io.in <> lsurs.io.out


  //exu res write back
  ///我建议在MicroOp中加入orderque指针，或者
  ///根据保留站序号，两条指令都是同一执行单元时此拍只能执行一条

  //先找到输出为有效的功能单元（通知保留站），要把有效的位变成rs序号，比较orderqueuePtr的大小，顺序写回寄存器，再按顺序通过commitIO输出
  //并行通知保留站、寄存器
  //-------------找输出为有效的功能单元--------------
  first_inst(0) := false.B
  first_inst(1) := (csr.io.out.valid && !csr.io.out.bits.isSecond) || (csr.io.jmp.valid && !csr.io.out.bits.isSecond)
  first_inst(2) := (bru.io.out.valid && !bru.io.out.bits.isSecond) || (bru.io.jmp.valid && !bru.io.out.bits.isSecond)
  first_inst(3) := alu1.io.out.valid && !alu1.io.out.bits.isSecond
  first_inst(4) := alu2.io.out.valid && !alu2.io.out.bits.isSecond
  first_inst(5) := false.B///lsu.io.out.valid && !lsu.io.out.bits.isSecond///
  second_inst(0) := false.B
  second_inst(1) := (csr.io.out.valid && csr.io.out.bits.isSecond) || (csr.io.jmp.valid && !csr.io.out.bits.isSecond)
  second_inst(2) := (bru.io.out.valid && bru.io.out.bits.isSecond) || (bru.io.jmp.valid && bru.io.out.bits.isSecond)
  second_inst(3) := alu1.io.out.valid && alu1.io.out.bits.isSecond
  second_inst(4) := alu2.io.out.valid && alu2.io.out.bits.isSecond
  second_inst(5) := false.B///lsu.io.out.valid && lsu.io.out.bits.isSecond


  //执行单元的提交使用CommitIO,在ExuBlock里使用FuOutPut决定指令
  FuOutPut_default := DontCare
  FuOutPut_default.valid := false.B
  ExuResult(0):= MuxLookup(first_num,FuOutPut_default,Array(
     1.U -> csr.io.out,///---------------这里接口是改成commit形式还是保留原形式，根据执行单元类型选择？
     2.U -> bru.io.out,
     3.U -> alu1.io.out,
     4.U -> alu2.io.out,
     ///5.U -> lsu.io.out
   ))
  ExuResult(1):= MuxLookup(second_num,FuOutPut_default,Array(
    1.U -> csr.io.out,
    2.U -> bru.io.out,
    3.U -> alu1.io.out,
    4.U -> alu2.io.out,
    ///5.U -> lsu.io.out
  ))

  io.redirect := DontCare
  io.redirect.valid := false.B
  //选择跳转信号
  when(first_inst(1)){
    when(csr.io.jmp.valid){
      io.redirect.valid := true.B
      io.redirect.bits.new_pc := csr.io.jmp.bits.new_pc///todo:外部连接IFU
      io.redirect.bits.taken := csr.io.jmp.bits.taken
    }
  }.elsewhen(first_inst(2)){
    when(bru.io.jmp.valid){
      io.redirect.valid := true.B
      io.redirect.bits.new_pc := bru.io.jmp.bits.new_pc
      io.redirect.bits.taken := bru.io.jmp.bits.taken
    }
  }
  when(second_inst(1)){
    when(csr.io.jmp.valid){
      io.redirect.valid := true.B
      io.redirect.bits.new_pc := csr.io.jmp.bits.new_pc
      io.redirect.bits.taken := csr.io.jmp.bits.taken
    }
  }.elsewhen(second_inst(2)){
    when(bru.io.jmp.valid){
      io.redirect.valid := true.B
      io.redirect.bits.new_pc := bru.io.jmp.bits.new_pc
      io.redirect.bits.taken := bru.io.jmp.bits.taken
    }
  }

  csrrs.io.ExuResult := ExuResult
  brurs.io.ExuResult := ExuResult
  alu1rs.io.ExuResult := ExuResult
  alu2rs.io.ExuResult := ExuResult
  ///lsurs.io.ExuResult := ExuResult

  for(i <- 0 until 2){
    preg.io.write(i).addr := ExuResult(i).bits.uop.pdest
    preg.io.write(i).ena := ExuResult(i).bits.uop.ctrl.rfWen && ExuResult(i).valid
    preg.io.write(i).data := ExuResult(i).bits.res
    commit(i).valid := ExuResult(i).valid
    commit(i).bits.pdest := ExuResult(i).bits.uop.pdest
    commit(i).bits.old_pdest := ExuResult(i).bits.uop.old_pdest
    commit(i).bits.ldest := ExuResult(i).bits.uop.ctrl.rfrd
    commit(i).bits.rfWen := ExuResult(i).bits.uop.ctrl.rfWen
  }

  ///todo:返回rename?rename在外部例化，直接给io.out不就OK了？
  io.out <> commit

  io.rs_can_allocate(0) := !csrrs.io.full///can_allocate
  io.rs_can_allocate(1) := !brurs.io.full
  io.rs_can_allocate(2) := !alu1rs.io.full
  io.rs_can_allocate(3) := !alu2rs.io.full
  io.rs_can_allocate(4) := false.B///!lsurs.io.full

  for(i <- 0 until 2) {
    val instrCommit = Module(new DifftestInstrCommit)
    instrCommit.io.clock := clock
    instrCommit.io.coreid := 0.U
    instrCommit.io.index := i.U
    instrCommit.io.skip := false.B
    instrCommit.io.isRVC := false.B
    instrCommit.io.scFailed := false.B


    instrCommit.io.valid := RegNext(ExuResult(i).valid)
    instrCommit.io.pc := RegNext(ExuResult(i).bits.uop.cf.pc)
    instrCommit.io.instr := RegNext(ExuResult(i).bits.uop.cf.instr)
    instrCommit.io.wen := RegNext(ExuResult(i).bits.uop.ctrl.rfWen)
    instrCommit.io.wdata := RegNext(ExuResult(i).bits.res)
    instrCommit.io.wdest := RegNext(ExuResult(i).bits.uop.ctrl.rfrd)
  }

  for ((rport, rat) <- preg.io.debug_read.zip(io.debug_int_rat)) {
    rport.addr := rat
  }
  val difftest = Module(new DifftestArchIntRegState)
  difftest.io.clock := clock
  difftest.io.coreid := 0.U
  difftest.io.gpr := VecInit(preg.io.debug_read.map(_.data))


}