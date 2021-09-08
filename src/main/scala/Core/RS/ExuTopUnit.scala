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

///1,,写到orderq,保留站，指针给保留站
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
  rs.io.decode.
  for(i <- 0 until 2){
    when(io.rs_num_in(i)===0.U && io.in(i).valid){
      alu1rs.io.in <> io.in(i) //in orderqueue rs  读寄存器
      orderqueue.io.enqPtr <> alu1rs.io.Enqptr
      rs.io.in.srcState(0) := io.busytablein(2*i)
      rs.io.in.srcState(1) := io.busytablein(2*i+1)
      //寄存器的输入
    }
    when(io.rs_num_in(i)===1.U && io.in(i).valid){
      orderqueue.io.out <> alu2rs.io.DispatchOrder
      orderqueue.io.enqPtr <> alu2rs.io.Enqptr
    }
    when(io.rs_num_in(i)===2.U && io.in(i).valid){
      orderqueue.io.out <> brurs.io.DispatchOrder
      orderqueue.io.enqPtr <> brurs.io.Enqptr
    }
    when(io.rs_num_in(i)===3.U && io.in(i).valid){
      orderqueue.io.out <> csrrs.io.DispatchOrder
      orderqueue.io.enqPtr <> csrrs.io.Enqptr
    }
    when(rs_num_in(i)===4.U && io.in(i).valid){
      orderqueue.io.out <> lsurs.io.DispatchOrder
      orderqueue.io.enqPtr <> lsurs.io.Enqptr
    }
  }

  csrrs.io.DispatchOrder := orderqueue.io.out
  alu1rs.io.DispatchOrder := orderqueue.io.out
  alu2rs.io.DispatchOrder := orderqueue.io.out


  //2


  //rs to exu
  alu1.io.in <> alu1rs.out
  alu1.io.src := alu1rs.SrcOut
  alu2.io.in <> alu2rs.out
  alu2.io.src := alu2rs.SrcOut

  //exu res write back
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