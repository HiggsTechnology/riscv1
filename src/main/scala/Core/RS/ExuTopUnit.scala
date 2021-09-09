package Core.RS

import chisel3._
import chisel3.util._
import utils._
import Core.EXU.ALU
import Core.EXU.BRU
import Core.EXU.CSR
import Core.EXU.LSU

trait rsConfig{
  rsSize :Int = 2
}

class EXU_OUTIO extends Bundle {
  val reg_write_back = Flipped(new RegWriteIO)///原四级写反没用到
  val pdest = Output(UInt(PhyRegIdxWidth.W))
}
///CommitIO,逻辑、物理地址数据，还要加回到重命名模块、BusyTable


class ExuTOPIO extends Bundle with Config with rsConfig{
  val in = Vec(2, ValidIO(new MicroOp))///此模块里，DispatchQueue在外部，给到OrderQueue
  val rs_num_in = Vec(2, Input(UInt(log2Up(ExuNUM).W)))///此模块里，给到rs的序号，OrderQueueBook
  val busytablein = Vec(4,Input(Bool()))///0、1、3、4两条指令一个Commit、发射出来指令的物理地址到Busytable
  ///val writeBusyin = Vec(2,new writeBusyIO))///Input地址，Output布尔
  ///val readBusyin = Vec(ExuNum,Vec(rsSize,new readBusyIO)))///Input地址，Output布尔
  ///val rs_RegRead = Vec(ExuNum,Vec(rsSize,flipped(new RegReadIO)))//一个Commit、发射出来指令的物理地址到Busytable

  //val redirect         = new BRU_OUTIO///BRU可能Redirect_OUTIO,与朱航他们讨论
  val out = Vec(2,ValidIO(new CommitIO))///满足两条指令同时写回...decouple多一层是否准备好
  ///能用上val rs_emptySize = Vec(ExuNum,Output(UInt(log2Up(rsSize).W)))
  val rsFull = Vec(ExuNUM,Output(Bool()))
  //val SrcOut = Vec(2,Output(UInt(XLEN.W)))///Src输出, valid在MicroOp
  ///val valid = Output(Vec(2,Bool())) ///握手
}

///1,,写到orderqueue,保留站,指针给保留站
///2,,orderq控制指令的发射
///3,,做执行单元运算，写回结果，包括写回保留站、重命名(包括busytable)、寄存器
class ExuTop extends Module with {
  val io  = IO(new ExuTOPIO)
  //val exu = Module(new EXU)///原四级需要修改，添加为1csr 1jump 2alu 1lsu，并为执行单元编号，与rsNum相同
  val csrrs = Module(new RS(size = rsSize, rsNum = 0, nFu = ExuNUM, name = "CSRRS")))
  val brurs = Module(new RS(size = rsSize, rsNum = 1, nFu = ExuNUM, name = "BRURS")))
  val alu1rs = Module(new RS(size = rsSize, rsNum = 2, nFu = ExuNUM, name = "ALU1RS"))///nFu,循环判断是否为
  val alu2rs = Module(new RS(size = rsSize, rsNum = 3, nFu = ExuNUM, name = "ALU2RS")))
  val lsurs = Module(new RS(size = rsSize, rsNum = 4, nFu = ExuNUM, name = "LSURS")))
  val csr = Module(new CSR)
  val bru = Module(new BRU)
  val alu1 = Module(new ALU)
  val alu2 = Module(new ALU)
  val lsu = Module(new LSU)
  val orderqueue = Module(new OrderQueue)
  ////val ReservationStaions = Seq(alu1rs,alu2rs,brurs,csrrs,lsurs)

  //val dispatchqueue = Module(new DispatchQueue)
  ///从目前设计来看，需要的是


  //orderqueue
  orderqueue.io.rs_num_in := io.rs_num_in
  orderqueue.io.in := io.in

  //读寄存器数据选择通路src1、src2
  //lq//RS应该需要侦听当前入队指令的物理寄存器
  val preg = Module(new Regfile(4,2,128))///新写
  val preg_data = Wire(Vec(2,Vec(2,UInt(XLEN.W))))
  for(i <- 0 until 2){
    preg.io.read(2*i).addr := io.in(i).psrc(0)
    preg.io.read(2*i+1).addr := io.in(i).psrc(1)
    preg_data(i)(0) := preg.io.read(2*i).data
    preg_data(i)(1) := preg.io.read(2*i+1).data
  }

  val src_in = Wire(Vec(2,Vec(2,UInt(XLEN.W))))
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

  for(i <- 0 until 2){
    when(io.rs_num_in(i)===0.U && io.in(i).valid){
      csrrs.io.in <> io.in(i) //in orderqueue rs  读寄存器
      orderqueue.io.enqPtr := csrrs.io.Enqptr
      csrrs.io.in.srcState(0) := io.busytablein(2*i)
      csrrs.io.in.srcState(1) := io.busytablein(2*i+1)
      //寄存器的输入
      csrrs.io.SrcIn := src_in(i)
    }
    when(io.rs_num_in(i)===1.U && io.in(i).valid){
      brurs.io.in <> io.in(i) //in orderqueue rs  读寄存器
      orderqueue.io.enqPtr := brurs.io.Enqptr
      brurs.io.in.srcState(0) := io.busytablein(2*i)
      brurs.io.in.srcState(1) := io.busytablein(2*i+1)
      //寄存器的输入
      brurs.io.SrcIn := src_in(i)
    }
    when(io.rs_num_in(i)===2.U && io.in(i).valid){
      alu1rs.io.in <> io.in(i) //in orderqueue rs  读寄存器
      orderqueue.io.enqPtr := alu1rs.io.Enqptr
      alu1rs.io.in.srcState(0) := io.busytablein(2*i)
      alu1rs.io.in.srcState(1) := io.busytablein(2*i+1)
      //寄存器的输入
      alu1rs.io.SrcIn := src_in(i)
    }
    when(io.rs_num_in(i)===3.U && io.in(i).valid){
      alu2rs.io.in <> io.in(i) //in orderqueue rs  读寄存器
      orderqueue.io.enqPtr := alu2rs.io.Enqptr
      alu2rs.io.in.srcState(0) := io.busytablein(2*i)
      alu2rs.io.in.srcState(1) := io.busytablein(2*i+1)
      //寄存器的输入
      alu2rs.io.SrcIn := rs_preg(i)
    }
    when(rs_num_in(i)===4.U && io.in(i).valid){
      lsurs.io.in <> io.in(i) //in orderqueue rs  读寄存器
      orderqueue.io.enqPtr := lsurs.io.Enqptr
      lsurs.io.in.srcState(0) := io.busytablein(2*i)
      lsurs.io.in.srcState(1) := io.busytablein(2*i+1)
      //寄存器的输入
      lsurs.io.SrcIn := rs_preg(i)
    }
  }

  //2,,orderq控制指令的发射
  csrrs.io.DispatchOrder := orderqueue.io.out
  brurs.io.DispatchOrder := orderqueue.io.out
  alu1rs.io.DispatchOrder := orderqueue.io.out
  alu2rs.io.DispatchOrder := orderqueue.io.out
  lsurs.io.DispatchOrder := orderqueue.io.out

  ///3,,做执行单元运算，写回结果，包括写回保留站、重命名(包括busytable)、寄存器

  //rs to exu
  //执行单元运算，有decouple，直接连接
  csr.io.in <> csrrs.io.out
  csr.io.src := csrrs.io.SrcOut
  bru.io.in <> brurs.io.out
  bru.io.src := brurs.io.SrcOut
  alu1.io.in <> alu1rs.io.out
  alu1.io.src := alu1rs.io.SrcOut
  alu2.io.in <> alu2rs.io.out
  alu2.io.src := alu2rs.io.SrcOut
  lsu.io.in <> lsurs.io.out
  lsu.io.src := lsurs.io.SrcOut

  //exu res write back
  ///我建议在MicroOp中加入orderque指针，或者
  ///根据保留站序号，两条指令都是同一执行单元时此拍只能执行一条
  val ExuResult = Wire(Vec(2,new CommitIO))

  //先找到输出为有效的功能单元（通知保留站），要把有效的位变成rs序号，比较orderqueuePtr的大小，顺序写回寄存器，再按顺序通过commitIO输出
  for(i <- 0 until 2){
    rs_num_in(i)
    ///val func = io.in(i).bits.decode.ctrl.funcType
    ///val op = io.in(i).bits.decode.ctrl.funcOpType
    lsu.io.lsu2rw <> DontCare
    val alu_ena = func === FuncType.alu
    val lsu_ena = func === FuncType.lsu
    val bru_ena = func === FuncType.bru
    val csr_ena = func === FuncType.csr

    lsu.io.valid := lsu_ena
    // csr 维护内部状态需要启用信号
    csr.io.ena  := csr_ena
    alu.io.in <> io.in.bits
    lsu.io.in <> io.in.bits
    bru.io.in <> io.in.bits
    csr.io.in <> io.in.bits

    private val wb_ena = Wire(Bool())
    private val wdata = Wire(UInt(XLEN.W))
    wb_ena := MuxLookup(func, false.B, Array(
      FuncType.alu -> true.B,
      FuncType.lsu -> LSUOpType.isLoad(op),
      FuncType.bru -> BRUOpType.isJalr(io.in.bits.ctrl.funcOpType),
      // csrr*[i]指令都需要写入寄存器堆，ecall ebreak mret等指令的rd对应位置为x0，置true也没有影响
      FuncType.csr -> true.B
    ))
    wdata := MuxLookup(func, 0.U(XLEN.W), Array(
      FuncType.alu -> alu.io.out.aluRes,
      FuncType.lsu -> lsu.io.out.rdata,
      FuncType.bru -> (io.in.bits.cf.pc + 4.U),
      FuncType.csr -> csr.io.out.rdata
    ))
    // 当译码信号有效时才写入
    io.reg_write_back.ena   := wb_ena & io.in.valid
    io.reg_write_back.addr  := io.in.bits.ctrl.rfrd
    io.reg_write_back.data  := wdata

    // lsu和csr都会影响pc的值
    io.branch.valid := io.in.valid
    io.branch <> MuxLookup(func, 0.U.asTypeOf(new BRU_OUTIO), Array(
      FuncType.bru -> bru.io.out,
      FuncType.csr -> csr.io.out.jmp
    ))

  }



  ///当前周期事情，在rs内比较是否
  //lookuptree选择功能单元，得到两个CommitIO，是否valid在rs与功能单元握手
  //写回rename、保留站、寄存器；如果乱序，第一步写回rs和rob，rob顺序写回reg和rename

  val exu_seq = Seq(csr,bru,alu1,alu2,lsu)
  for (i <- 0 until ExuNUM){
    alu1rs.ExuResult(i) <> exu_seq(i).CommitIO
  }
  for (i <- 0 until ExuNUM){
    alu2rs.ExuResult(i) <> exu_seq(i).CommitIO
  }
  for (i <- 0 until ExuNUM){
    brurs.ExuResult(i) <> exu_seq(i).CommitIO
  }
  for (i <- 0 until ExuNUM){
    csrrs.ExuResult(i) <> exu_seq(i).CommitIO
  }
  for (i <- 0 until ExuNUM){
    lsurs.ExuResult(i) <> exu_seq(i).CommitIO
  }

  val rs_seq = Seq(csrrs,brurs,alu1rs,alu2rs,lsurs)
  //reg和rs
  for (i <- 0 until ExuNUM){
      rs_seq(i).readBusy <> io.rs_
      rs_seq(i).RegRead <> io.rs_RegRead(i)
  }

  ///保留站侦听所有功能单元输出，选择有用信号

  // 更改rename

}