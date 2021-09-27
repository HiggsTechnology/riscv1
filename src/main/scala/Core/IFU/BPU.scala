package Core.IFU

import Core.Config
import chisel3._
import chisel3.util._
import utils._

class btbupdate extends Bundle with Config{
  val br_pc = Output(UInt(VAddrBits.W))
  val targets = Output(UInt(VAddrBits.W))
  val br_type = Output(UInt(2.W))
}

class pred_update extends Bundle with Config{
  val taken = Output(Bool())
  val gshare_idx = Output(UInt(ghrBits.W))
  val pc_idx = Output(UInt(ghrBits.W))
  val gshare_mispred = Output(Bool())
  val pc_mispred = Output(Bool())
}

class preDecode extends Bundle with Config{
  val pc3 = Output(UInt(XLEN.W))
  val is_br = Output(Bool())
  val offset = Output(UInt(XLEN.W))
  val br_type = Output(BRtype())
  val is_ret = Output(Bool())
  val iscall = Output(Bool())
}

class BPUIO extends Bundle with Config{
  //input
  //stage1
  val pc = Vec(2,Input(UInt(XLEN.W)))

  //stage3
  val predecode = Vec(2, Flipped(ValidIO(new preDecode)))

  val outfire = Vec(2,Input(Bool()))

  //output
  //stage2
  val br_taken = Vec(2,Output(Bool()))
  val jump_pc = Output(UInt(XLEN.W))  //btb & pht

  //stage3
  val br_taken3 = Vec(2,Output(Bool()))
  val jump_pc3 = Output(UInt(XLEN.W))  //btb & pht

  val gshare_idx = Vec(2,Output(UInt(ghrBits.W)))
  val gshare_pred = Vec(2,Output(Bool()))
  val pc_pred = Vec(2,Output(Bool()))
  val btbtarget = Vec(2,Output(UInt(XLEN.W)))

  //update
  val pred_update = Flipped(ValidIO(new pred_update))
  val ras_update = Input(new RASupdate)
  val btb_update = Flipped(ValidIO(new btbupdate))
  val flush = Input(Bool())
}

class BPU extends Module with Config{
  val io = IO(new BPUIO)

  val btb = Module(new BTB)
  val ras = Module(new RAS)
  //stage1

  //gshare
  val ghr = RegInit(0.U(ghrBits.W))

  val GPHT_Idx1 = Wire(Vec(FETCH_WIDTH, UInt(ghrBits.W)))
  val pc1 = Wire(Vec(FETCH_WIDTH, UInt(XLEN.W)))

  for(i <- 0 until FETCH_WIDTH) {
    GPHT_Idx1(i) := io.pc(i)(ghrBits + 1, 2) ^ ghr
    pc1(i) := io.pc(i)
  }


  //stage2
  val GPHT_Idx2 = RegNext(GPHT_Idx1)
  val pc2 = RegNext(pc1)

  val pred_select = RegInit(0.U(3.W))//第2位为高则选GPHT

  val PHT = Mem(GPHT_Size, UInt(2.W))
  val PHT_taken = Wire(Vec(FETCH_WIDTH, Bool()))
  for(i <- 0 until FETCH_WIDTH){
    PHT_taken(i) := PHT.read(pc2(i)(ghrBits+1,2))(1)
  }

  val GPHT = Mem(GPHT_Size, UInt(2.W))
  val GPHT_taken = Wire(Vec(FETCH_WIDTH, Bool()))
  for(i <- 0 until FETCH_WIDTH){
    GPHT_taken(i) := GPHT.read(GPHT_Idx2(i))(1)
  }

  val bimPred = Wire(Vec(FETCH_WIDTH, Bool()))

  btb.io.in.pc := pc2


  val br_taken2 = Wire(Vec(FETCH_WIDTH, Bool()))
  for(i <- 0 until FETCH_WIDTH){
    bimPred(i) := Mux(pred_select(2), GPHT_taken(i), PHT_taken(i))  //直接定义用GPHT来看
    br_taken2(i) := btb.io.resp.hits(i) && (btb.io.resp.br_type(i) =/= BTBtype.B || (btb.io.resp.br_type(i) === BTBtype.B && bimPred(i)))  //要么是绝对转跳，要么是分支转跳，但是预测地址相同
  }
  io.jump_pc := Mux(br_taken2(0), btb.io.resp.targets(0), btb.io.resp.targets(1))
  io.br_taken := br_taken2

  val btb_hit2 = Wire(Vec(FETCH_WIDTH, Bool()))
  val btbtarget2 = Wire(Vec(FETCH_WIDTH, UInt(VAddrBits.W)))
  val br_type2 = Wire(Vec(FETCH_WIDTH, UInt(2.W)))
  for(i <- 0 until FETCH_WIDTH){
    btb_hit2(i) := btb.io.resp.hits(i)
    btbtarget2(i) := btb.io.resp.targets(i)
    br_type2(i) := btb.io.resp.br_type(i)
  }

  //stage3
  val GPHT_taken3 = RegNext(GPHT_taken)
  val PHT_taken3 = RegNext(PHT_taken)
  val GPHT_Idx3 = RegNext(GPHT_Idx2)
  val pc3 = RegNext(pc2) //跳转指令的pc
  val bimPred3 = RegNext(bimPred)
  val br_taken3 = RegNext(br_taken2)
  val jump3 = RegNext(io.jump_pc)
  val btb_hit3 = RegNext(btb_hit2)
  val btbtarget3 = RegNext(btbtarget2)   //跳转到的pc
  val br_type3 = RegNext(br_type2)

  //根据predecode，以及stage2的GPHT、PHT，计算分支预测结果
  val br_taken_predecode = Wire(Vec(FETCH_WIDTH, Bool()))
  val is_call = Wire(Vec(FETCH_WIDTH, Bool()))
  for(i <- 0 until FETCH_WIDTH){
    is_call(i) := (io.predecode(i).bits.br_type === BRtype.J && io.predecode(i).bits.iscall) || (io.predecode(i).bits.br_type === BRtype.R && io.predecode(i).bits.iscall)
    br_taken_predecode(i) := io.predecode(i).bits.is_br && (io.predecode(i).bits.br_type =/= BRtype.B || (io.predecode(i).bits.br_type === BRtype.B && bimPred3(i)))
  }

  //传入RAS
  ras.io.push.iscall := Mux(br_taken_predecode(0), io.outfire(0) && io.predecode(0).bits.is_br && is_call(0), io.outfire(1) && io.predecode(1).bits.is_br && is_call(1))
  ras.io.push.target := Mux(is_call(0), io.predecode(0).bits.pc3, io.predecode(1).bits.pc3) + 4.U
  ras.io.is_ret := Mux(br_taken_predecode(0), io.outfire(0) && io.predecode(0).bits.is_br && io.predecode(0).bits.is_ret, io.outfire(1) && io.predecode(1).bits.is_br && io.predecode(1).bits.is_ret)
  ras.io.update := io.ras_update
  ras.io.flush := io.flush

  //输出predecode & ras的转跳信息,供IFU检查stage2的正确性，以确定是否输入IBF
  //printf("predecode pc %x %x, offset %x %x\n",io.predecode(0).bits.pc3,io.predecode(1).bits.pc3,io.predecode(0).bits.offset,io.predecode(1).bits.offset)
  val target3 = Wire(Vec(FETCH_WIDTH, UInt(VAddrBits.W)))
  
  for(i <- 0 until FETCH_WIDTH){
    target3(i) := Mux(io.predecode(i).bits.is_ret, ras.io.target, Mux(io.predecode(i).bits.br_type === BRtype.R && btb_hit3(i), btbtarget3(i), io.predecode(i).bits.pc3 + io.predecode(i).bits.offset))
  }
  io.jump_pc3 := Mux(br_taken_predecode(0), target3(0), target3(1))
  io.br_taken3 := br_taken_predecode

  io.gshare_idx := GPHT_Idx3
  io.gshare_pred := GPHT_taken3
  io.pc_pred := PHT_taken3
  for(i <- 0 until FETCH_WIDTH){
    io.btbtarget(i) := Mux(btb_hit3(i), btbtarget3(i), io.predecode(i).bits.pc3 + io.predecode(i).bits.offset)
  }
  //btb update
  val pre_br_type = Wire(Vec(2,UInt(2.W)))
  for(i <- 0 until FETCH_WIDTH){
    pre_br_type(i) := Mux(io.predecode(i).bits.is_ret, "b10".U, io.predecode(i).bits.br_type)
  }

  val btb_needUpdate = Wire(Vec(2,Bool()))
  val btb_needUpdate_part = Wire(Vec(2,Bool()))
  for(i <- 0 until FETCH_WIDTH) {
    btb_needUpdate_part(i) := (io.predecode(i).bits.is_br && io.predecode(i).bits.br_type =/= BRtype.R) && (!btb_hit3(i) || br_type3(i) =/= pre_br_type(i) || btbtarget3(i) =/= io.predecode(i).bits.pc3 + io.predecode(i).bits.offset) //跳转的类型相同，地址相同，并且命中
    btb_needUpdate(i) := (io.predecode(i).bits.is_ret && (!btb_hit3(i) || br_type3(i) =/= pre_br_type(i) || btbtarget3(i) =/= ras.io.target)) || btb_needUpdate_part(i)    //返回的
  }

  for(i <- 0 until FETCH_WIDTH){
    btb.io.update.br_type(i) := pre_br_type(i)
    btb.io.update.needUpdate(i) := btb_needUpdate(i)
    btb.io.update.targets(i) := Mux(io.predecode(i).bits.is_ret, ras.io.target, io.predecode(i).bits.pc3 + io.predecode(i).bits.offset)
    btb.io.update.br_pc(i) := io.predecode(i).bits.pc3
  }
  btb.io.update.br_type(2) := io.btb_update.bits.br_type
  btb.io.update.needUpdate(2) := io.btb_update.valid
  btb.io.update.targets(2) := io.btb_update.bits.targets
  btb.io.update.br_pc(2) := io.btb_update.bits.br_pc

  // printf("btb1 hit %d, target %x, type %x\n",btb_hit3(0),btbtarget3(0),br_type3(0))
  // printf("btb2 hit %d, target %x, type %x\n",btb_hit3(1),btbtarget3(1),br_type3(1))
  // printf("btb update %d %d\n",btb_needUpdate(0),btb_needUpdate(1))


  //GPHT, PHT, RAS update
  when(io.pred_update.valid){
    val idx = io.pred_update.bits.gshare_idx
    val taken = io.pred_update.bits.taken
    val cnt = GPHT(idx)
    val newCnt = Mux(taken, cnt + 1.U, cnt - 1.U)
    val wen = (taken && (cnt =/= "b11".U)) || (!taken && (cnt =/= "b00".U))
    when (wen) {
      GPHT(idx) := newCnt
    }

    ghr := Cat(ghr(ghrBits-2,0), taken)

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

}