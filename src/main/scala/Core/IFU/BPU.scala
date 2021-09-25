package Core.IFU

import Core.Config
import chisel3._
import chisel3.util._
import utils._

class pred_update extends Bundle with Config{
  val taken = Output(Bool())
  val gshare_idx = Output(UInt(ghrBits.W))
  val pc_idx = Output(UInt(ghrBits.W))
  val gshare_mispred = Output(Bool())
  val pc_mispred = Output(Bool())
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
  val gshare_pred = Vec(2,Output(Bool()))
  val pc_pred = Vec(2,Output(Bool()))

  val pred_update = Flipped(ValidIO(new pred_update))
  val ras_update = Input(new RASupdate)
  val flush = Input(Bool())
}

class BPU extends Module with Config{
  val io = IO(new BPUIO)

  val ras = Module(new RAS)

  val pred_select = RegInit(0.U(3.W))

  //pc
  val PHT = Mem(GPHT_Size, UInt(2.W))
  val PHT_taken = Wire(Vec(FETCH_WIDTH, Bool()))
  for(i <- 0 until FETCH_WIDTH){
    PHT_taken(i) := PHT.read(io.pc(i)(ghrBits+1,2))(1)
  }

  //gshare
  val ghr = RegInit(0.U(ghrBits.W))
  val ghr_commit = RegInit(0.U(ghrBits.W))

  val GPHT_Idx = Wire(Vec(FETCH_WIDTH, UInt(ghrBits.W)))
  for(i <- 0 until FETCH_WIDTH){
    GPHT_Idx(i) := io.pc(i)(ghrBits+1,2) ^ ghr_commit
    io.gshare_idx(i) := io.pc(i)(ghrBits+1,2) ^ ghr_commit
  }

  val GPHT = Mem(GPHT_Size, UInt(2.W))
  val GPHT_taken = Wire(Vec(FETCH_WIDTH, Bool()))

  for(i <- 0 until FETCH_WIDTH){
    GPHT_taken(i) := GPHT.read(GPHT_Idx(i))(1)
  }

  val is_B = Wire(Vec(FETCH_WIDTH, Bool()))
  for(i <- 0 until FETCH_WIDTH){
    is_B(i) := io.is_br(i) && io.br_type(i) === BRtype.B
  }

  when((is_B(0) && !is_B(1)) || (!is_B(0) && is_B(1))){
    val taken = Mux(is_B(0), GPHT_taken(0), GPHT_taken(1))
    ghr := Cat(ghr(ghrBits-2,0), taken)
  }.elsewhen(is_B(0) && is_B(1)){
    ghr := Cat(ghr(ghrBits-2,1), GPHT_taken(0), GPHT_taken(1))
  }

  io.pc_pred := PHT_taken
  io.gshare_pred := GPHT_taken

  //branch taken
  for(i <- 0 until FETCH_WIDTH){
    io.br_taken(i) := io.is_br(i) && (io.br_type(i) === BRtype.R || io.br_type(i) === BRtype.J || (io.br_type(i) === BRtype.B && Mux(pred_select(2), GPHT_taken(i), PHT_taken(i))))
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

  when(io.pred_update.valid){
    val idx = io.pred_update.bits.gshare_idx
    val taken = io.pred_update.bits.taken
    val cnt = GPHT(idx)
    val newCnt = Mux(taken, cnt + 1.U, cnt - 1.U)
    val wen = (taken && (cnt =/= "b11".U)) || (!taken && (cnt =/= "b00".U))
    when (wen) {
      GPHT(idx) := newCnt
    }

    ghr_commit := Cat(ghr_commit(ghrBits-2,0), taken)

    val pc_idx = io.pred_update.bits.pc_idx
    val pc_cnt = PHT(pc_idx)
    val pc_newCnt = Mux(taken, pc_cnt + 1.U, pc_cnt - 1.U)
    val pc_wen = (taken && (pc_cnt =/= "b11".U)) || (!taken && (pc_cnt =/= "b00".U))
    when (pc_wen) {
      PHT(pc_idx) := pc_newCnt
    }

    when(io.pred_update.bits.pc_mispred && !io.pred_update.bits.gshare_mispred && pred_select =/= "b111".U){
      pred_select := pred_select + 1.U
    }.elsewhen(!io.pred_update.bits.pc_mispred && io.pred_update.bits.gshare_mispred && pred_select =/= "b000".U){
      pred_select := pred_select - 1.U
    }


  }

  when(io.flush){
    ghr := ghr_commit
  }

}