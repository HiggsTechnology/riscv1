package Core.IFU


import Core.{BRU_OUTIO, Config, Pc_Instr}
import chisel3._
import chisel3.util._

class RAMHelper extends BlackBox {
  val io = IO(new Bundle {
    val clk = Input(Clock())
    val en = Input(Bool())
    val rIdx = Input(UInt(64.W))
    val rdata = Output(UInt(64.W))
    val wIdx = Input(UInt(64.W))
    val wdata = Input(UInt(64.W))
    val wmask = Input(UInt(64.W))
    val wen = Input(Bool())
  })
}

class IFUIO extends Bundle {
  val in  = Flipped(ValidIO(new BRU_OUTIO))  //branch
  val out = Vec(2, DecoupledIO(new Pc_Instr))
  //  val ifu2rw = new IFU2RW
}

class IFU extends Module with Config {
  val io = IO(new IFUIO)
  val pc = RegInit(PC_START.U(XLEN.W))

  val bpu = Module(new BPU)

  // object IFUState {
  //   val continue :: stall :: Nil = Enum(2)
  // }
  // val ifuState = RegInit(IFUState.continue)

  //stage1
  val ifu_redirect = Wire(Bool())
  
  val pcVec    = Wire(Vec(FETCH_WIDTH, UInt(XLEN.W)))

  pcVec(0) := Mux(io.in.valid && (io.in.bits.mispred), io.in.bits.new_pc, Mux(ifu_redirect, bpu.io.jump_pc3, Mux(bpu.io.br_taken(0) || bpu.io.br_taken(1), bpu.io.jump_pc, pc)))
  for(i <- 1 until FETCH_WIDTH){
    pcVec(i) := pcVec(i-1) + 4.U
  }
  bpu.io.pc := pcVec
  pc := pcVec(0) + 8.U


  //stage2
  val pcVec2 = RegNext(pcVec)

  val instrVec = Wire(Vec(FETCH_WIDTH, UInt(INST_WIDTH)))
  val rdataVec = Wire(Vec(FETCH_WIDTH, UInt(XLEN.W)))
  val ramVec   = Seq.fill(FETCH_WIDTH)(Module(new RAMHelper))
  for(i <- 0 until FETCH_WIDTH){
    // 依次从pc处开始取指
    ramVec(i).io.clk   := clock
    ramVec(i).io.en    := !reset.asBool
    ramVec(i).io.rIdx  := (pcVec2(i) - PC_START.U) >> 3
    rdataVec(i)        := ramVec(i).io.rdata
    ramVec(i).io.wIdx  := DontCare
    ramVec(i).io.wen   := false.B
    ramVec(i).io.wdata := DontCare
    ramVec(i).io.wmask := DontCare
    instrVec(i)        := Mux(pcVec2(i)(2),rdataVec(i)(63,32),rdataVec(i)(31,0))
  }

  //stage3
  val pcVec3 = RegNext(pcVec2)
  val instrVec3 = RegNext(instrVec)
  val instrValid = RegNext(!reset.asBool) && RegNext(RegNext(!reset.asBool))
  val br_taken2 = RegNext(bpu.io.br_taken)
  val jump_pc2 = RegNext(bpu.io.jump_pc)


  val preDecVec= Seq.fill(FETCH_WIDTH)(Module(new PreDecode))
  for(i <- 0 until FETCH_WIDTH){
    preDecVec(i).io.instr:= instrVec3(i)

    bpu.io.predecode(i).valid := instrValid
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
    io.out(i).bits.pc_pred := bpu.io.pc_pred(i)
    io.out(i).bits.btbtarget := bpu.io.btbtarget(i)

    io.out(i).bits.pc    := pcVec3(i)
    io.out(i).bits.instr := instrVec3(i)
    io.out(i).bits.is_br := preDecVec(i).io.is_br

  }

  ifu_redirect := ((jump_pc2 =/= bpu.io.jump_pc3) || !br_taken2.asUInt.orR) && (bpu.io.br_taken3.asUInt.orR) && io.out(0).valid
  val ifu_redirect3 = RegNext(ifu_redirect)

  val flush = io.in.valid && io.in.bits.mispred
  val flush2 = flush || RegNext(flush)

  io.out(0).valid := instrValid && !flush2 && !ifu_redirect3
  io.out(1).valid := instrValid && !flush2 && !ifu_redirect3 && !bpu.io.br_taken3(0)


  bpu.io.pred_update.valid := io.in.valid && io.in.bits.is_B
  bpu.io.pred_update.bits.taken := io.in.bits.taken
  bpu.io.pred_update.bits.gshare_idx := io.in.bits.gshare_idx
  bpu.io.pred_update.bits.pc_idx := io.in.bits.pc(ghrBits+1,2)
  bpu.io.pred_update.bits.gshare_mispred := io.in.bits.gshare_mispred
  bpu.io.pred_update.bits.pc_mispred := io.in.bits.pc_mispred

  bpu.io.ras_update.target := io.in.bits.pc + 4.U
  bpu.io.ras_update.is_ret := io.in.valid && io.in.bits.is_ret
  bpu.io.ras_update.iscall := io.in.valid && io.in.bits.is_call

  bpu.io.btb_update.valid := io.in.bits.btb_update
  bpu.io.btb_update.bits.br_type := "b01".U
  bpu.io.btb_update.bits.targets := io.in.bits.new_pc
  bpu.io.btb_update.bits.br_pc := io.in.bits.pc

  bpu.io.flush := io.in.valid && io.in.bits.mispred

  // printf("-------- stage 1 --------\n")
  // printf("IFU in redirect valid %d\n",io.in.valid && (io.in.bits.mispred))
  // printf("IFU pcReg %x, reset %d\n", pc, reset.asBool)
  // printf("pcVec %x %x\n",pcVec(0),pcVec(1))

  // printf("-------- stage 2 --------\n")
  // printf("bpu pred valid %d %d, jump_pc2 %x\n",bpu.io.br_taken(0),bpu.io.br_taken(1),bpu.io.jump_pc)
  // printf("pcVec2 %x %x\n",pcVec2(0),pcVec2(1))
  // printf("instrVec %x %x\n",instrVec(0),instrVec(1))

  // printf("-------- stage 3 --------\n")
  // printf("preDecode1: inst %x, is_br %d, br_type %d, is_ret %d, offset %x\n",instrVec3(0),preDecVec(0).io.is_br, preDecVec(0).io.br_type, preDecVec(0).io.is_ret, preDecVec(0).io.offset)
  // printf("preDecode2: inst %x, is_br %d, br_type %d, is_ret %d, offset %x\n",instrVec3(1),preDecVec(1).io.is_br, preDecVec(1).io.br_type, preDecVec(1).io.is_ret, preDecVec(1).io.offset)
  // printf("IFU branch predict %d %d, bpu jump pc %x\n",bpu.io.br_taken3(0),bpu.io.br_taken3(1),bpu.io.jump_pc3)
  // printf("inst1: vaild %d, pc %x, inst %x \n",io.out(0).valid,io.out(0).bits.pc,io.out(0).bits.instr)
  // printf("inst2: vaild %d, pc %x, inst %x \n",io.out(1).valid,io.out(1).bits.pc,io.out(1).bits.instr)
  // printf("IBF in.out.ready %d %d\n",io.out(0).ready,io.out(1).ready)

  // printf("ifu_redirect %d, ifu_redirect3 %d\n",ifu_redirect,ifu_redirect3)
  // printf("--------one cycle--------\n\n")

}