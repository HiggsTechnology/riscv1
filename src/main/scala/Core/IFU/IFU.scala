package Core.IFU

import Bus.{SimpleBus, SimpleBusParameter, SimpleReqBundle, SimpleRespBundle}
import Core.AXI4.AXI4Parameters
import Core.Cache.{CacheReq, CacheResp}
import Core.{BPU_Update, Config, CtrlFlow, RedirectIO}
import chisel3._
import chisel3.util._


class IFUIO extends Bundle {
  val in  = Flipped(ValidIO(new BPU_Update))  //branch
  val out = Vec(2, DecoupledIO(new CtrlFlow))
  val redirect = Flipped(ValidIO(new RedirectIO))
  val toMem = Vec(2, new SimpleBus)
//  val cachereq  = Vec(2,DecoupledIO(new SimpleReqBundle))
//  val cacheresp = Vec(2,Flipped(ValidIO(new SimpleRespBundle)))
  //  val ifu2rw = new IFU2RW
}

class IFU extends Module with Config {
  val io = IO(new IFUIO)
  val pc = RegInit((if(is_sim) PC_START_sim else PC_START_soc).U(XLEN.W) + 8.U)
  val mispred = io.redirect.bits.mispred
  val bpu = Module(new BPU)
  val continue = (io.out(0).ready || (io.redirect.valid && mispred)) && io.toMem(0).req.ready && io.toMem(1).req.ready
  bpu.io.continue := continue
  // object IFUState {
  //   val continue :: stall :: Nil = Enum(2)
  // }
  // val ifuState = RegInit(IFUState.continue)

  //stage1
  val flushCnt = RegInit(0.U(2.W))

  val ifu_redirect = Wire(Bool())
  
  val pcVec    = Wire(Vec(FETCH_WIDTH, UInt(XLEN.W)))

  //后端重定向优先级最高，第三拍转跳指令
  when(flushCnt===2.U){
    pcVec(0) := pc
  }.otherwise{
    pcVec(0) := Mux(io.redirect.valid && mispred, io.redirect.bits.new_pc, Mux(ifu_redirect, bpu.io.jump_pc3, Mux(bpu.io.br_taken(0) || bpu.io.br_taken(1), bpu.io.jump_pc, pc)))
  }
  for(i <- 1 until FETCH_WIDTH){
    pcVec(i) := pcVec(i-1) + 4.U
  }
  bpu.io.pc := pcVec
  when(continue) {
    pc := pcVec(0) + 8.U
    when(io.redirect.valid && mispred){
      flushCnt := 1.U
    }.elsewhen(flushCnt =/= 0.U){
      flushCnt := flushCnt - 1.U
    }
  }.elsewhen(io.redirect.valid && mispred){
    //printf("IFU redirect pc %x, pcVec(0) %x\n",pc,pcVec(0))
    pc := pcVec(0)
    flushCnt := 2.U
  }


  //stage
  val pcVec2_init =  Wire(Vec(FETCH_WIDTH, UInt(XLEN.W)))
  for(i <- 0 until FETCH_WIDTH){
    pcVec2_init(i) := pcVec(i) - 8.U
  }
  val pcVec2 = RegInit(pcVec2_init)
  when(continue){
    pcVec2 :=pcVec
  }
  for(i <- 0 until FETCH_WIDTH){
    io.toMem(i).req.valid := continue //&& RegNext(!reset.asBool)
    io.toMem(i).req.bits.addr := pcVec2(i)
    io.toMem(i).req.bits.isWrite  := false.B
    io.toMem(i).req.bits.wmask    := DontCare
    io.toMem(i).req.bits.data     := DontCare
    io.toMem(i).req.bits.size     := SimpleBusParameter.SIZE.bytes8
    io.toMem(i).resp.ready        := true.B
  }


  //stage3
  val pcVec3    = RegInit(pcVec2)
  val br_taken2 = RegInit(bpu.io.br_taken)
  val jump_pc2  = RegInit(bpu.io.jump_pc)
  when(continue){
    pcVec3    := pcVec2
    br_taken2 := bpu.io.br_taken
    jump_pc2  := bpu.io.jump_pc
  }

  val instrReg3 = RegInit(VecInit(Seq.fill(FETCH_WIDTH)(0.U(INST_WIDTH))))

  val instrVec3 = Wire(Vec(2,UInt(INST_WIDTH)))
  for(i <- 0 until FETCH_WIDTH){
    when(io.toMem(i).resp.valid){
      instrReg3(i) := io.toMem(i).resp.bits.data(31,0)
    }
    instrVec3(i) := Mux(io.toMem(i).resp.valid, io.toMem(i).resp.bits.data(31,0), instrReg3(i))
  }


  val preDecVec= Seq.fill(FETCH_WIDTH)(Module(new PreDecode))
  for(i <- 0 until FETCH_WIDTH){
    preDecVec(i).io.instr:= instrVec3(i)

    bpu.io.predecode(i).valid := io.toMem(i).resp.valid
    bpu.io.predecode(i).bits.pc3 := pcVec3(i)
    bpu.io.predecode(i).bits.is_br := preDecVec(i).io.is_br
    bpu.io.predecode(i).bits.offset := preDecVec(i).io.offset
    bpu.io.predecode(i).bits.br_type := preDecVec(i).io.br_type
    bpu.io.predecode(i).bits.is_ret := preDecVec(i).io.is_ret
    bpu.io.predecode(i).bits.iscall := preDecVec(i).io.iscall
    bpu.io.outfire(i) := io.out(i).fire

    io.out(i).bits.br_taken := bpu.io.br_taken3(i)
    io.out(i).bits.gshare_idx := bpu.io.gshare_idx(i)
    io.out(i).bits.gshare_pred := bpu.io.gshare_pred(i)
    io.out(i).bits.pht_pred := bpu.io.pc_pred(i)
    io.out(i).bits.btbtarget := bpu.io.btbtarget(i)
    io.out(i).bits.rastarget := bpu.io.rastarget(i)

    io.out(i).bits.pc    := pcVec3(i)
    io.out(i).bits.instr := instrVec3(i)
    io.out(i).bits.is_br := preDecVec(i).io.is_br

  }

  ifu_redirect := (((jump_pc2 =/= bpu.io.jump_pc3) || !br_taken2.asUInt.orR) && (bpu.io.br_taken3.asUInt.orR)) && io.out(0).valid
  val ifu_redirect3 = RegInit(ifu_redirect)
  when(continue){
    ifu_redirect3 := ifu_redirect
  }

  val flush = io.redirect.valid && mispred
  //val flush2 = flush || RegNext(flush)

  val inst_not_enq = Seq.fill(2)(RegInit(false.B))
  for(i <- 0 until 2){
    when(io.out(i).valid && !io.out(i).ready && !flush){
      inst_not_enq(i) := true.B
    }.elsewhen(io.out(i).fire){
      inst_not_enq(i) := false.B
    }.elsewhen(flush){
      inst_not_enq(i) := false.B
    }
  }


  io.out(0).valid := !flush && flushCnt === 0.U && !ifu_redirect3 && (io.toMem(0).resp.valid || inst_not_enq(0))
  io.out(1).valid := !flush && flushCnt === 0.U && !ifu_redirect3 && (io.toMem(1).resp.valid || inst_not_enq(1)) && !bpu.io.br_taken3(0)


  bpu.io.pred_update.valid := io.in.valid && io.in.bits.is_B
  bpu.io.pred_update.bits.taken := io.in.bits.taken
  bpu.io.pred_update.bits.gshare_idx := io.in.bits.gshare_idx
  bpu.io.pred_update.bits.pc_idx := io.in.bits.pc(ghrBits+1,2)
  bpu.io.pred_update.bits.gshare_mispred := io.in.bits.gshare_mispred
  bpu.io.pred_update.bits.pht_mispred := io.in.bits.pht_mispred

  bpu.io.ras_update.target := io.in.bits.pc + 4.U
  bpu.io.ras_update.is_ret := io.in.valid && io.in.bits.is_ret
  bpu.io.ras_update.iscall := io.in.valid && io.in.bits.is_call

  bpu.io.btb_update.valid := io.in.bits.btb_update && io.in.valid
  bpu.io.btb_update.bits.br_type := "b01".U
  bpu.io.btb_update.bits.targets := io.in.bits.new_pc
  bpu.io.btb_update.bits.br_pc := io.in.bits.pc

  bpu.io.flush := io.in.valid && mispred
  bpu.io.ras_flush := io.in.bits.ras_flush

  // when(io.toMem(0).req.ready && io.toMem(1).req.ready){
  // printf("-------- stage 1 --------\n")
  // printf("IFU in redirect valid %d\n",io.redirect.valid && (io.redirect.bits.mispred))
  // printf("IFU pcReg %x, reset %d\n", pc, reset.asBool)
  // printf("pcVec %x %x\n",pcVec(0),pcVec(1))

  // printf("-------- stage 2 --------\n")
  // printf("bpu pred valid %d %d, jump_pc2 %x\n",bpu.io.br_taken(0),bpu.io.br_taken(1),bpu.io.jump_pc)
  // printf("pcVec2 %x %x\n",pcVec2(0),pcVec2(1))

  // printf("-------- stage 3 --------\n")
  // printf("preDecode1: inst %x, is_br %d, br_type %d, is_ret %d, offset %x\n",instrVec3(0),preDecVec(0).io.is_br, preDecVec(0).io.br_type, preDecVec(0).io.is_ret, preDecVec(0).io.offset)
  // printf("preDecode2: inst %x, is_br %d, br_type %d, is_ret %d, offset %x\n",instrVec3(1),preDecVec(1).io.is_br, preDecVec(1).io.br_type, preDecVec(1).io.is_ret, preDecVec(1).io.offset)
  // printf("IFU branch predict %d %d, bpu jump pc %x\n",bpu.io.br_taken3(0),bpu.io.br_taken3(1),bpu.io.jump_pc3)
  // printf("inst1: vaild %d, pc %x, inst %x \n",io.out(0).valid,io.out(0).bits.pc,io.out(0).bits.instr)
  // printf("inst2: vaild %d, pc %x, inst %x \n",io.out(1).valid,io.out(1).bits.pc,io.out(1).bits.instr)
  // printf("IBF in.out.ready %d %d\n",io.out(0).ready,io.out(1).ready)

  // printf("ifu_redirect %d, ifu_redirect3 %d\n",ifu_redirect,ifu_redirect3)
  // printf("--------one cycle--------\n\n")
  // }
//  when(io.out(0).valid && io.out(1).valid){
//    printf("IFU: out valid \n pc out 1 is %x, 2 is %x, inst out 1 is %x out 2 is %x \n",io.out(0).bits.pc,io.out(1).bits.pc,io.out(0).bits.instr,io.out(1).bits.instr)
//  }


}