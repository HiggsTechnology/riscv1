package Core.CtrlBlock

import Core.{Config, ExuCommit, MicroOp, CtrlFlow, RedirectIO}
import Core.Config.{ExuNum, PhyRegIdxWidth, RSNum}
import Core.CtrlBlock.DISPATCH.{Dispatch, DispatchQueue}
import Core.CtrlBlock.IDU.IDU
import Core.CtrlBlock.ROB.{ROB, ROBPtr}
import Core.CtrlBlock.Rename.{BusyTable, Rename}
import chisel3._
import chisel3.util._
import utils._
//class ControlBlockIO extends Bundle{
//  val in              = Vec(2, Flipped(Valid(new Pc_Instr)))
//  val out             = Vec(2, Flipped(ValidIO(new MicroOp)))
//  val rs_num_out      = Vec(2, Output(UInt(log2Up(ExuNum).W)))
//  val rs_can_allocate = Vec(ExuNum, Input(Bool()))
//  val pregValid       = Vec(4, Output(Bool()))
//  val commit          = Vec(2, Flipped(ValidIO(new CommitIO)))
//}

class ControlBlockIN extends Bundle{
  val pcinstr         = Vec(2, Flipped(DecoupledIO(new CtrlFlow)))
  val rs_can_allocate = Vec(RSNum, Input(Bool()))
  val exuCommit       = Vec(ExuNum, Flipped(ValidIO(new ExuCommit)))
  val redirect        = Flipped(ValidIO(new RedirectIO))
}
class ControlBlockOUT extends Bundle{
  val microop         = Vec(2, (ValidIO(new MicroOp)))
  val rs_num_out      = Vec(2, Output(UInt(log2Up(RSNum).W)))
  val pregValid       = Vec(4, Output(Bool()))
  val debug_int_rat = Vec(32, Output(UInt(PhyRegIdxWidth.W)))
  val predict_robPtr = Output(new ROBPtr)
  val flush_commit = Output(Bool())
}
class ControlBlockIO extends Bundle{
  val in              = new ControlBlockIN
  val out             = new ControlBlockOUT
}

class ControlBlock(is_sim: Boolean) extends Module with Config{
  val io           = IO(new ControlBlockIO)
  // Only one IDU accept interrupt
  val decoders      = Seq(
    Module(new IDU),
    Module(new IDU)
  )

  val rename        = Module(new Rename)
  val intBusyTable  = Module(new BusyTable(numReadPorts = 2 * 2, numWritePorts = ExuNum))
  val dispatch      = Module(new Dispatch)
  val disQueue      = Module(new DispatchQueue)
  val rob           = Module(new ROB(is_sim = is_sim))

  val flush = io.in.redirect.valid && io.in.redirect.bits.mispred
  //io.in.pcinstr(0).ready := disQueue.io.out.can_allocate
  //io.in.pcinstr(1).ready := disQueue.io.out.can_allocate
  //Decoder & Backend Commit To Rename
  io.out.flush_commit := rob.io.flush_out
  rename.io.in.flush             := rob.io.flush_out
  intBusyTable.io.flush          := rob.io.flush_out
  for(i <- 0 until 2){
    decoders(i).io.in              <> io.in.pcinstr(i)
  }
  val validReg1A = RegInit(false.B)
  when(rename.io.in.cfctrl(0).fire || !validReg1A || flush){
    validReg1A := decoders(0).io.out.valid
  }
  val validReg1B = RegInit(decoders(1).io.out.valid)
  val dataReg1A  = RegInit(decoders(0).io.out.bits)
  val dataReg1B  = RegInit(decoders(1).io.out.bits)
  when(rename.io.in.cfctrl(0).fire || !validReg1A || flush){
    validReg1B := decoders(1).io.out.valid
    dataReg1A  := decoders(0).io.out.bits
    dataReg1B  := decoders(1).io.out.bits
  }
  decoders(0).io.out.ready := rename.io.in.cfctrl(0).fire || !validReg1A || flush
  decoders(1).io.out.ready := rename.io.in.cfctrl(0).fire || !validReg1A || flush

  rename.io.in.cfctrl(0).valid := validReg1A && !flush
  rename.io.in.cfctrl(1).valid := validReg1B && !flush
  rename.io.in.cfctrl(0).bits  := dataReg1A
  rename.io.in.cfctrl(1).bits  := dataReg1B


  rename.io.in.commit         := rob.io.commit

  val ready3 = disQueue.io.out.can_allocate && rob.io.can_allocate
  val validReg2A = RegInit(false.B)
  when((validReg2A && ready3) || !validReg2A || flush){
    validReg2A := rename.io.out.microop(0).valid
  }
  val validReg2B = RegInit(false.B)
  val dataReg2A  = RegInit(rename.io.out.microop(0).bits)
  val dataReg2B  = RegInit(rename.io.out.microop(1).bits)
  when((validReg2A && ready3) || !validReg2A || flush){
    validReg2B := rename.io.out.microop(1).valid
    dataReg2A  := rename.io.out.microop(0).bits
    dataReg2B  := rename.io.out.microop(1).bits
  }



  //ROB && Dispatch Ready
  rename.io.out.microop(0).ready := (validReg2A && ready3) || !validReg2A || flush
  rename.io.out.microop(1).ready := (validReg2A && ready3) || !validReg2A || flush
  //Rename To ROB
  rob.io.in(0).valid := validReg2A && !flush && disQueue.io.out.can_allocate
  rob.io.in(1).valid := validReg2B && !flush && disQueue.io.out.can_allocate
  rob.io.in(0).bits := dataReg2A
  rob.io.in(1).bits := dataReg2B

  rob.io.exuCommit := io.in.exuCommit
  rob.io.redirect := io.in.redirect
  io.out.predict_robPtr := rob.io.predict
  //Rename To Dispatch
  dispatch.io.in.microop_in(0).valid := validReg2A && !flush && rob.io.can_allocate
  dispatch.io.in.microop_in(1).valid := validReg2B && !flush && rob.io.can_allocate
  dispatch.io.in.microop_in(0).bits  := dataReg2A
  dispatch.io.in.microop_in(1).bits  := dataReg2B

  for(i <- 0 until 2){
    dispatch.io.in.microop_in(i).bits.ROBIdx := rob.io.enqPtr(i)
  }

  //Dispatch To DispatchQueue
  dispatch.io.in.can_allocate    := disQueue.io.out.can_allocate
  disQueue.io.in.microop_in      := dispatch.io.out.microop_out
  disQueue.io.in.rs_num_in       := dispatch.io.out.rs_num_out
  disQueue.io.flush              := io.in.redirect.valid && io.in.redirect.bits.mispred
  //Dispatch Queue To Out
  disQueue.io.in.rs_can_allocate := io.in.rs_can_allocate
  io.out.microop                 := disQueue.io.out.microop_out
  io.out.rs_num_out              := disQueue.io.out.rs_num_out
  //Busytable To Out
  for(i <- 0 until 2){
    //read
    val readportNUM = 2*i
    intBusyTable.io.read(readportNUM).req   := io.out.microop(i).bits.psrc(0)
    intBusyTable.io.read(readportNUM+1).req := io.out.microop(i).bits.psrc(1)
    io.out.pregValid(readportNUM)           := intBusyTable.io.read(readportNUM).resp || io.out.microop(i).bits.psrc(0) === 0.U
    io.out.pregValid(readportNUM+1)         := intBusyTable.io.read(readportNUM+1).resp || io.out.microop(i).bits.psrc(1) === 0.U
    //alloc
    intBusyTable.io.allocPregs(i).valid     := rename.io.out.microop(i).valid
    intBusyTable.io.allocPregs(i).bits      := rename.io.out.microop(i).bits.pdest
  }
  for(i <- 0 until ExuNum){
    //commit
    intBusyTable.io.wbPregs(i).valid        := io.in.exuCommit(i).valid
    intBusyTable.io.wbPregs(i).bits         := io.in.exuCommit(i).bits.pdest
  }

  io.out.debug_int_rat := rename.io.out.debug_int_rat

  //printf("CtrlBlock io.valid %d %d, decode.valid %d %d, rename.valid %d %d\n", io.in.pcinstr(0).valid, io.in.pcinstr(1).valid, decoders(0).io.out.valid, decoders(1).io.out.valid, rename.io.out.microop(0).valid, rename.io.out.microop(1).valid)
//  for(i <- 0 until ExuNum){
//    when(io.in.exuCommit(i).valid){
//      printf("CTRL BLOCK: exu %d commit res is %x \n",i.U, io.in.exuCommit(i).bits.res)
//    }
//  }
}