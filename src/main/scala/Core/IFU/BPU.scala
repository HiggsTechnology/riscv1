package Core.IFU

import Core.Config
import chisel3._
import chisel3.util._
import utils._

class Gshare_update extends Bundle with Config{
  val taken = Output(Bool())
  val idx = Output(UInt(ghrBits.W))
}

class BPUIO extends Bundle with Config{
  val pc = Vec(2,Input(UInt(XLEN.W)))

  val is_br = Vec(2,Input(Bool()))
  val offset = Vec(2,Input(UInt(XLEN.W)))
  val br_type = Vec(2,Input(BRtype()))
  val is_ret = Vec(2,Input(Bool()))
  val iscall = Vec(2,Input(Bool()))
  
  val outfire = Vec(2,Input(Bool()))

  val br_taken = Vec(2,Output(Bool()))
  val jump_pc = Output(UInt(XLEN.W))
  val gshare_idx = Vec(2,Output(UInt(ghrBits.W)))

  val gshare_update = Flipped(ValidIO(new Gshare_update))
  val ras_update = Input(new RASupdate)
  val flush = Input(Bool())
}

class BPU extends Module with Config{
  val io = IO(new BPUIO)

  val ras = Module(new RAS)

  val ghr = RegInit(0.U(ghrBits.W))

  val GPHT_Idx = Wire(Vec(FETCH_WIDTH, UInt(ghrBits.W)))
  for(i <- 0 until FETCH_WIDTH){
    GPHT_Idx(i) := io.pc(i)(ghrBits+1,2) ^ ghr
    io.gshare_idx(i) := io.pc(i)(ghrBits+1,2) ^ ghr
  }

  val GPHT = Mem(GPHT_Size, UInt(2.W))
  val GPHT_taken = Wire(Vec(FETCH_WIDTH, Bool()))

  for(i <- 0 until FETCH_WIDTH){
    GPHT_taken(i) := GPHT.read(GPHT_Idx(i))(1)
  }

  for(i <- 0 until FETCH_WIDTH){
    io.br_taken(i) := io.is_br(i) && (io.br_type(i) === BRtype.R || io.br_type(i) === BRtype.J || (io.br_type(i) === BRtype.B && GPHT_taken(i)))
  }

  val is_call = Wire(Vec(FETCH_WIDTH, Bool()))
  for(i <- 0 until FETCH_WIDTH){
    is_call(i) := (io.br_type(i) === BRtype.J && io.iscall(i)) || (io.br_type(i) === BRtype.R && io.iscall(i))
  }

  ras.io.push.iscall := Mux(io.br_taken(0), io.outfire(0) && io.is_br(0) && is_call(0), io.outfire(1) && io.is_br(1) && is_call(1))
  ras.io.push.target := Mux(is_call(0), io.pc(0), io.pc(1)) + 4.U
  ras.io.is_ret := Mux(io.br_taken(0), io.outfire(0) && io.is_br(0) && io.is_ret(0), io.outfire(1) && io.is_br(1) && io.is_ret(1))
  ras.io.update := io.ras_update
  ras.io.flush := io.flush

  io.jump_pc := Mux(io.br_taken(0), Mux(io.is_ret(0), ras.io.target, io.pc(0)+io.offset(0)), Mux(io.is_ret(1), ras.io.target, io.pc(1)+io.offset(1)))

  when(io.gshare_update.valid){
    val idx = io.gshare_update.bits.idx
    val taken = io.gshare_update.bits.taken
    val cnt = GPHT(idx)
    val newCnt = Mux(taken, cnt + 1.U, cnt - 1.U)
    val wen = (taken && (cnt =/= "b11".U)) || (!taken && (cnt =/= "b00".U))
    when (wen) {
      GPHT(idx) := newCnt
    }

    ghr := Cat(ghr(ghrBits-2,0), taken)
  }

}