package Core.ExuBlock


import Bus.{SimpleReqBundle, SimpleRespBundle, SimpleBus}
import Core.ExuBlock.FU.{ALU, BRU, CSR, DU, LSU, MU}
import Core.CtrlBlock.IDU.{FuncType, SrcType1, SrcType2}
import Core.CtrlBlock.ROB.ROBPtr
import Core.ExuBlock.Mem.LSQ
import Core.ExuBlock.MemReg.Regfile
import Core.ExuBlock.RS.{RS, RS_DU, RsInorder}
import Core.{BPU_Update, Config, ExuCommit, FuOutPut, MicroOp, RSType, RedirectIO}
import chisel3._
import chisel3.util._
import difftest.DifftestArchIntRegState
import utils._


trait ExuBlockConfig extends Config {
  def JumpRsSlaveNum = 2
  def JumpRsBruNo = 0
  def JumpRsCsrNo = 1
}
object ExuBlockConfig extends ExuBlockConfig

class ExuBlockIO extends Bundle with Config {
  val in = Vec(2, Flipped(ValidIO(new MicroOp)))///此模块里，DispatchQueue在外部，给到OrderQueue
  val rs_num_in = Vec(2, Input(UInt(log2Up(RSNum+1).W)))///此模块里，给到rs的序号，OrderQueueBook//-1: bru/csr -> jumprs
  val busytablein = Vec(4,Input(Bool()))///0、1、3、4两条指令一个Commit、发射出来指令的物理地址到Busytable

  val predict_robPtr = Input(new ROBPtr)
  val redirect  = ValidIO(new RedirectIO)///BRU可能Redirect_OUTIO,与朱航他们讨论
  val bpu_update = ValidIO(new BPU_Update)
  val exuCommit = Vec(ExuNum,ValidIO(new ExuCommit))

  val rs_can_allocate = Vec(RSNum,Output(Bool()))

  val debug_int_rat = Vec(32, Input(UInt(PhyRegIdxWidth.W)))

  val toMem = new SimpleBus
}

///1,,写到orderqueue,保留站,指针给保留站
///2,,orderq控制指令的发射
///3,,做执行单元运算，写回结果，包括写回保留站、重命名(包括busytable)、寄存器
class ExuBlock(is_sim: Boolean) extends Module with ExuBlockConfig{
  val io  = IO(new ExuBlockIO)
  val jumprs = Module(new RsInorder(slave_num = JumpRsSlaveNum, size = rsSize, rsNum = 0, nFu = ExuNum, name = "JUMPRS"))
  val alu1rs = Module(new RS(size = rsSize, rsNum = 1, nFu = ExuNum, name = "ALU1RS"))///nFu,循环判断是否为
  val alu2rs = Module(new RS(size = rsSize, rsNum = 2, nFu = ExuNum, name = "ALU2RS"))
  //val murs   = Module(new RS(size = rsSize, rsNum = 4, nFu = ExuNum, name = "MURS"))
  //val durs   = Module(new RS_DU(size = rsSize, rsNum = 5, nFu = ExuNum, name = "DURS"))
  val lsq = Module(new LSQ)
  val csr = Module(new CSR(is_sim = is_sim))   // ExuRes 0
  val bru = Module(new BRU)   // ExuRes 1
  val alu1 = Module(new ALU)  // ExuRes 2
  val alu2 = Module(new ALU)  // ExuRes 3
  //val mu   = Module(new MU)   // ExuRes 4
  //val du   = Module(new DU)   // ExuRes 5
  val lsu1 = Module(new LSU)  // ExuRes 6
 // val lsu2 = Module(new LSU)
  // 双发射，2*2读端口，6个执行单元，6个写端口，Todo: 限制写端口数量简化布线
  val preg = Module(new Regfile(numReadPorts = 4,numWritePorts = ExuNum,numPreg = 128))///新写
  private val preg_data = Wire(Vec(2,Vec(2,UInt(XLEN.W))))
  private val src_in = Wire(Vec(2,Vec(2,UInt(XLEN.W))))
  private val ExuResult = Wire(Vec(ExuNum,ValidIO(new FuOutPut)))
  //todo:bru信号传出mispredict


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

  jumprs.io.in := DontCare
  jumprs.io.in.valid := false.B
  jumprs.io.SrcIn := DontCare
  alu1rs.io.in := DontCare
  alu1rs.io.in.valid := false.B
  alu1rs.io.SrcIn := DontCare
  alu2rs.io.in := DontCare
  alu2rs.io.in.valid := false.B
  alu2rs.io.SrcIn := DontCare
  lsq.io.in := DontCare
  lsq.io.in(0).valid := false.B
  lsq.io.in(1).valid := false.B
  lsq.io.SrcIn := DontCare
//  murs.io.in := DontCare
//  murs.io.in.valid := false.B
//  murs.io.SrcIn := DontCare
//  durs.io.in := DontCare
//  durs.io.in.valid := false.B
//  durs.io.SrcIn := DontCare

  //  printf("rs_num_in0 %d in1 %d\n",io.rs_num_in(0),io.rs_num_in(1))
  //  printf("ExuBlock io.in(0) %d %x %x, io.in(1) %d %x %x\n",io.in(0).valid,io.in(0).bits.cf.pc,io.in(0).bits.cf.instr,io.in(1).valid,io.in(1).bits.cf.pc,io.in(1).bits.cf.instr)
  for(i <- 0 until 2){
    when(io.rs_num_in(i)===RSType.jumprs && io.in(i).valid){
      jumprs.io.in := io.in(i) //in orderqueue rs  读寄存器
      jumprs.io.in.bits.srcState(0) := io.busytablein(2*i) || (io.in(i).bits.ctrl.src1Type =/= SrcType1.reg)
      jumprs.io.in.bits.srcState(1) := io.busytablein(2*i+1) || (io.in(i).bits.ctrl.src2Type =/= SrcType2.reg)
      //寄存器的输入
      jumprs.io.SrcIn := src_in(i)
    }
    when(io.rs_num_in(i)===RSType.alurs && io.in(i).valid){
      alu1rs.io.in := io.in(i) //in orderqueue rs  读寄存器
      alu1rs.io.in.bits.srcState(0) := io.busytablein(2*i) || (io.in(i).bits.ctrl.src1Type =/= SrcType1.reg)
      alu1rs.io.in.bits.srcState(1) := io.busytablein(2*i+1) || (io.in(i).bits.ctrl.src2Type =/= SrcType2.reg)
      //寄存器的输入
      alu1rs.io.SrcIn := src_in(i)
    }
    when(io.rs_num_in(i)===RSType.alurs2 && io.in(i).valid){
      alu2rs.io.in := io.in(i) //in orderqueue rs  读寄存器
      alu2rs.io.in.bits.srcState(0) := io.busytablein(2*i) || (io.in(i).bits.ctrl.src1Type =/= SrcType1.reg)
      alu2rs.io.in.bits.srcState(1) := io.busytablein(2*i+1) || (io.in(i).bits.ctrl.src2Type =/= SrcType2.reg)
      //寄存器的输入
      alu2rs.io.SrcIn := src_in(i)
    }
//    when(io.rs_num_in(i)===RSType.murs && io.in(i).valid){
//      murs.io.in := io.in(i) //in orderqueue rs  读寄存器
//      murs.io.in.bits.srcState(0) := io.busytablein(2*i) || (io.in(i).bits.ctrl.src1Type =/= SrcType1.reg)
//      murs.io.in.bits.srcState(1) := io.busytablein(2*i+1) || (io.in(i).bits.ctrl.src2Type =/= SrcType2.reg)
//      //寄存器的输入
//      murs.io.SrcIn := src_in(i)
//    }
//    when(io.rs_num_in(i)===RSType.durs && io.in(i).valid){
//      durs.io.in := io.in(i) //in orderqueue rs  读寄存器
//      durs.io.in.bits.srcState(0) := io.busytablein(2*i) || (io.in(i).bits.ctrl.src1Type =/= SrcType1.reg)
//      durs.io.in.bits.srcState(1) := io.busytablein(2*i+1) || (io.in(i).bits.ctrl.src2Type =/= SrcType2.reg)
//      //寄存器的输入
//      durs.io.SrcIn := src_in(i)
//    }
  }
  when((io.rs_num_in(0)===RSType.lsurs && io.in(0).valid) && !(io.rs_num_in(1)===RSType.lsurs && io.in(1).valid)){
    lsq.io.in(0) := io.in(0) //in orderqueue rs  读寄存器
    lsq.io.in(0).bits.srcState(0) := io.busytablein(0) || (io.in(0).bits.ctrl.src1Type =/= SrcType1.reg)
    lsq.io.in(0).bits.srcState(1) := io.busytablein(1) || (io.in(0).bits.ctrl.src2Type =/= SrcType2.reg)
    //寄存器的输入
    lsq.io.SrcIn(0) := src_in(0)
  }.elsewhen(!(io.rs_num_in(0)===RSType.lsurs && io.in(0).valid) && (io.rs_num_in(1)===RSType.lsurs && io.in(1).valid)){
    lsq.io.in(0) := io.in(1) //in orderqueue rs  读寄存器
    lsq.io.in(0).bits.srcState(0) := io.busytablein(2) || (io.in(1).bits.ctrl.src1Type =/= SrcType1.reg)
    lsq.io.in(0).bits.srcState(1) := io.busytablein(3) || (io.in(1).bits.ctrl.src2Type =/= SrcType2.reg)
    //寄存器的输入
    lsq.io.SrcIn(0) := src_in(1)
  }.elsewhen((io.rs_num_in(0)===RSType.lsurs && io.in(0).valid) && (io.rs_num_in(1)===RSType.lsurs && io.in(1).valid)){
    lsq.io.in(0) := io.in(0) //in orderqueue rs  读寄存器
    lsq.io.in(0).bits.srcState(0) := io.busytablein(0) || (io.in(0).bits.ctrl.src1Type =/= SrcType1.reg)
    lsq.io.in(0).bits.srcState(1) := io.busytablein(1) || (io.in(0).bits.ctrl.src2Type =/= SrcType2.reg)
    //寄存器的输入
    lsq.io.SrcIn(0) := src_in(0)

    lsq.io.in(1) := io.in(1) //in orderqueue rs  读寄存器
    lsq.io.in(1).bits.srcState(0) := io.busytablein(2) || (io.in(1).bits.ctrl.src1Type =/= SrcType1.reg)
    lsq.io.in(1).bits.srcState(1) := io.busytablein(3) || (io.in(1).bits.ctrl.src2Type =/= SrcType2.reg)
    //寄存器的输入
    lsq.io.SrcIn(1) := src_in(1)
  }

  ///3,,做执行单元运算，写回结果，包括写回保留站、重命名(包括busytable)、寄存器

  //rs to exu
  //执行单元运算，有decouple，直接连接
  bru.io.in  <> jumprs.io.out(JumpRsBruNo)
  csr.io.in  <> jumprs.io.out(JumpRsCsrNo)
  alu1.io.in <> alu1rs.io.out
  alu2.io.in <> alu2rs.io.out
//  mu.io.in   <> murs.io.out
//  du.io.in   <> durs.io.out
  lsu1.io.in <> lsq.io.lsu_in(0)
//  lsu2.io.in <> lsq.io.lsu_in(1)
  lsu1.io.spec_issued := lsq.io.lsu_spec_issued(0)
//  lsu2.io.spec_issued := lsq.io.lsu_spec_issued(1)

  io.toMem <> lsu1.io.toMem
 // io.toMem(1) <> lsu2.io.toMem
//  lsq.io.cache_ready := io.toMem.map(_.req.ready)
  lsq.io.cache_ready(0) := io.toMem.req.ready
  lsq.io.cache_ready(1) := false.B

  //exu res write back
  ///我建议在MicroOp中加入orderque指针，或者
  ///根据保留站序号，两条指令都是同一执行单元时此拍只能执行一条

  //先找到输出为有效的功能单元（通知保留站），要把有效的位变成rs序号，比较orderqueuePtr的大小，顺序写回寄存器，再按顺序通过commitIO输出
  //并行通知保留站、寄存器

  val csr_out_reg = RegInit(0.U.asTypeOf(ValidIO(new FuOutPut)))
  csr_out_reg := csr.io.out
  ExuResult(0) := csr_out_reg
  ExuResult(1) := bru.io.out
  ExuResult(2) := alu1.io.out
  ExuResult(3) := alu2.io.out
//  ExuResult(4) := mu.io.out//when add EXU, need before lsu1/lsu2
//  ExuResult(5) := du.io.out
  ExuResult(4) := lsu1.io.out
  //ExuResult(7) := DontCare//lsu2.io.out
  //ExuResult(7).valid := false.B

  lsq.io.lsu_out(0) := lsu1.io.out
  lsq.io.lsu_out(1) := DontCare//lsu2.io.out //todo: delete redundant lines, don't use DontCare

  lsq.io.predict_robPtr := io.predict_robPtr
  io.redirect := DontCare
  io.redirect.valid := false.B
  io.bpu_update := DontCare
  io.bpu_update.valid := false.B

  //选择跳转信号,暂时不考虑csr
  when(csr.io.out.valid || csr.io.trapvalid){
    when(csr.io.jmp.valid){
      io.redirect   :=  csr.io.jmp
      io.bpu_update :=  csr.io.bpu_update
    }
  }.elsewhen(bru.io.out.valid){
    when(bru.io.jmp.valid){
      io.redirect   :=  bru.io.jmp
      io.bpu_update :=  bru.io.bpu_update
    }
  }
  lsu1.io.trapvalid := csr.io.trapvalid
 // lsu2.io.trapvalid := csr.io.trapvalid



  jumprs.io.flush  := io.redirect.valid && io.redirect.bits.mispred
  jumprs.io.mispred_robPtr := io.redirect.bits.ROBIdx
  alu1rs.io.flush := io.redirect.valid && io.redirect.bits.mispred
  alu1rs.io.mispred_robPtr := io.redirect.bits.ROBIdx
  alu2rs.io.flush := io.redirect.valid && io.redirect.bits.mispred
  alu2rs.io.mispred_robPtr := io.redirect.bits.ROBIdx
//  murs.io.flush  := io.redirect.valid && io.redirect.bits.mispred
//  murs.io.mispred_robPtr := io.redirect.bits.ROBIdx
//  durs.io.flush  := io.redirect.valid && io.redirect.bits.mispred
//  durs.io.mispred_robPtr := io.redirect.bits.ROBIdx
  lsq.io.flush := io.redirect.valid && io.redirect.bits.mispred
  lsq.io.mispred_robPtr := io.redirect.bits.ROBIdx

  lsu1.io.flush := io.redirect.valid && io.redirect.bits.mispred
//  lsu2.io.flush := io.redirect.valid && io.redirect.bits.mispred
//  mu.io.flush := io.redirect.valid && io.redirect.bits.mispred
//  du.io.flush := io.redirect.valid && io.redirect.bits.mispred
//  du.io.mispred_robPtr := io.redirect.bits.ROBIdx

  jumprs.io.ExuResult := ExuResult
  alu1rs.io.ExuResult := ExuResult
  alu2rs.io.ExuResult := ExuResult
//  murs.io.ExuResult  := ExuResult
//  durs.io.ExuResult  := ExuResult//rs_num和rs_can_allocate按顺序加，ExuResult在LSU之前插入
  for(i <- 0 until (ExuNum-nLSU)){//subtract 2 l/d unit
    lsq.io.ExuResult(i) := ExuResult(i)
  }


  for(i <- 0 until ExuNum){
    preg.io.write(i).addr := ExuResult(i).bits.uop.pdest
    preg.io.write(i).ena := ExuResult(i).bits.uop.ctrl.rfWen && ExuResult(i).valid
    preg.io.write(i).data := ExuResult(i).bits.res
    io.exuCommit(i).valid := ExuResult(i).valid
    io.exuCommit(i).bits.pdest := ExuResult(i).bits.uop.pdest
    io.exuCommit(i).bits.ROBIdx := ExuResult(i).bits.uop.ROBIdx
    io.exuCommit(i).bits.res := ExuResult(i).bits.res
    io.exuCommit(i).bits.skip := false.B
  }
  io.exuCommit(4).bits.skip := lsu1.io.skip
  //io.exuCommit(7).bits.skip := false.B
  io.exuCommit(0).bits.skip := csr.io.skip

  io.rs_can_allocate(0) := !jumprs.io.full///can_allocate
  io.rs_can_allocate(1) := !alu1rs.io.full
  io.rs_can_allocate(2) := !alu2rs.io.full
  io.rs_can_allocate(3) := lsq.io.can_allocate
//  io.rs_can_allocate(4) := !murs.io.full
//  io.rs_can_allocate(5) := !durs.io.full


  for ((rport, rat) <- preg.io.debug_read.zip(io.debug_int_rat)) {
    rport.addr := rat
  }
  if (is_sim) {
    val difftest = Module(new DifftestArchIntRegState)
    difftest.io.clock := clock
    difftest.io.coreid := 0.U
    difftest.io.gpr := VecInit(preg.io.debug_read.map(_.data))
  }
}