package Core.CtrlBlock

import Core.Config.ExuNum
import Core.{CommitIO, MicroOp, Pc_Instr}
import chisel3._
import chisel3.util._
import utils._


class ControlBlockIO extends Bundle{
  val in  = Vec(2, Flipped(Valid(new Pc_Instr)))
  val out = Vec(2, Flipped(ValidIO(new MicroOp)))
  val rs_num_out = Vec(2, Output(UInt(log2Up(ExuNum).W)))
  val rs_can_allocate = Vec(ExuNum, Input(Bool()))

  val pregValid = Vec(4, Output(Bool()))
  val commit = Vec(2, Flipped(ValidIO(new CommitIO)))
}


class ControlBlock extends Module with Config{
  val io = IO(new ControlBlockIO)

  val decoders = Seq.fill(2)(Module(new IDU))
  val rename = Module(new Rename)
  val intBusyTable = Module(new BusyTable(4, 2))
  val dispatch = Module(new Dispatch)
  val disQueue = Module(new DispatchQueue)

  //decoder to rename
  for(i <- 0 until 2){
    decoders(i).io.in := io.in(i)
    rename.io.in(i) := decoders(i).io.out
  }

  //rename to dispatch
  dispatch.io.in := rename.io.out
  rename.io.commit := io.commit

  //Busytable
  for(i <- 0 until 2){
    //read
    val readportNUM = 2*i
    intBusyTable.io.read(readportNUM).req := io.out(i).bits.psrc(0)
    intBusyTable.io.read(readportNUM+1).req := io.out(i).bits.psrc(1)
    io.pregValid(readportNUM) := intBusyTable.io.read(readportNUM).resp
    io.pregValid(readportNUM+1) := intBusyTable.io.read(readportNUM+1).resp

    //alloc
    intBusyTable.io.allocPregs(i).vaild := rename.io.out(i).valid
    intBusyTable.io.allocPregs(i).bits := rename.io.out(i).bits.pdest

    //commit
    intBusyTable.io.wbPregs(i).vaild := io.commit(i).valid
    intBusyTable.io.wbPregs(i).bits := io.commit(i).bits.pdest
  }


  //dispatch to dispatch queue
  dispatch.io.can_allocate := disQueue.io.can_allocate
  disQueue.io.in := dispatch.io.out
  disQueue.io.rs_num_in := dispatch.io.rs_num_out

  //dispatch queue out
  disQueue.io.rs_can_allocate := io.rs_can_allocate
  io.out := disQueue.io.out
  io.rs_num_out := disQueue.io.rs_num_out

}