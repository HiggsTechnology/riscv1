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

  object IFUState {
    val continue :: stall :: Nil = Enum(2)
  }

  val ifuState = RegInit(IFUState.continue)
  // 不断取指，直到IBF满了 || jalr
  val outFireCount = PopCount(io.out.map(_.fire))
  // ifuState := Mux(outFireCount===0.U, IFUState.stall, IFUState.continue)
  //RAMHelper
  val pcVec    = Wire(Vec(FETCH_WIDTH, UInt(XLEN.W)))
  val instrVec = Wire(Vec(FETCH_WIDTH, UInt(INST_WIDTH)))
  val rdataVec = Wire(Vec(FETCH_WIDTH, UInt(XLEN.W)))
  val ramVec   = Seq.fill(FETCH_WIDTH)(Module(new RAMHelper)) // 多个module还是一个module用多次？
  val preDecVec= Seq.fill(FETCH_WIDTH)(Module(new PreDecode))
  pcVec(0) := Mux(io.in.valid && (io.in.bits.mispred || (io.in.bits.is_jalr && !io.in.bits.is_ret)), io.in.bits.new_pc, pc)
  // io.flush := io.in.valid
  // 一旦进来的信号valid，则表明之前预测错了，则冲刷
  for(i <- 1 until FETCH_WIDTH){
    pcVec(i) := pcVec(i-1) + 4.U
  }

  for(i <- 0 until FETCH_WIDTH){
    // 依次从pc处开始取指
    ramVec(i).io.clk   := clock
    ramVec(i).io.en    := !reset.asBool
    ramVec(i).io.rIdx  := (pcVec(i) - PC_START.U) >> 3
    rdataVec(i)        := ramVec(i).io.rdata
    ramVec(i).io.wIdx  := DontCare
    ramVec(i).io.wen   := false.B
    ramVec(i).io.wdata := DontCare
    ramVec(i).io.wmask := DontCare
    instrVec(i)        := Mux(pcVec(i)(2),rdataVec(i)(63,32),rdataVec(i)(31,0))

    preDecVec(i).io.instr:= instrVec(i)

    bpu.io.pc(i) := pcVec(i)
    bpu.io.is_br(i) := preDecVec(i).io.is_br
    bpu.io.offset(i) := preDecVec(i).io.offset
    bpu.io.br_type(i) := preDecVec(i).io.br_type
    bpu.io.is_ret(i) := preDecVec(i).io.is_ret
    bpu.io.iscall(i) := preDecVec(i).io.iscall
    bpu.io.outfire(i) := io.out(i).fire

    // when(preDecVec(i).io.brtype===BRtype.B){
    io.out(i).bits.br_taken := bpu.io.br_taken(i)
    io.out(i).bits.gshare_idx := bpu.io.gshare_idx(i)
    // }
    io.out(i).bits.pc    := pcVec(i)
    io.out(i).bits.instr := instrVec(i)
    io.out(i).bits.is_br := preDecVec(i).io.is_br
    // io.out(i).valid      := true.B
  }

  //状态机
  // def isJump(x: UInt) :Bool = {
  //   val opcode = x(6,0)
  //   opcode === "b1101111".U || opcode === "b1100111".U || opcode === "b1100011".U
  // }
  //取指令时
  when(ifuState === IFUState.continue){
    // when(preDecVec(0).io.br_type===BRtype.R){
    //   io.out(0).vaild := true.B
    //   io.out(1).vaild := false.B
    //   pc := pc + outFireCount * 4.U
    //   ifuState := IFUState.stall
    // }.elsewhen(preDecVec(1).io.br_type===BRtype.R){
    //   io.out(0).vaild := true.B
    //   io.out(1).vaild := true.B
    //   pc := pc + outFireCount * 4.U
    //   ifuState := IFUState.stall
    // }
    when(preDecVec(0).io.is_br && bpu.io.br_taken(0)){
      io.out(0).valid := true.B
      io.out(1).valid := false.B
      when(io.in.valid && io.in.bits.mispred){
        pc := pcVec(0)
      }.elsewhen(io.out(0).ready){
        pc := Mux(bpu.io.br_taken(0), bpu.io.jump_pc, pcVec(0) + 4.U)
      }
      //     ifuState := IFUState.stall
    }.elsewhen(preDecVec(1).io.is_br && bpu.io.br_taken(1)){
      io.out(0).valid := true.B
      io.out(1).valid := true.B
      when(io.in.valid && io.in.bits.mispred){
        pc := pcVec(0)
      }.elsewhen(io.out(0).ready){
        pc := Mux(bpu.io.br_taken(1), bpu.io.jump_pc, pcVec(1) + 4.U)
      }
      //     ifuState := IFUState.stall
    }.otherwise{
      io.out(0).valid := true.B
      io.out(1).valid := true.B
      pc := pcVec(0) + outFireCount * 4.U
    }

    when((preDecVec(0).io.is_br && preDecVec(0).io.br_type===BRtype.R && !preDecVec(0).io.is_ret && io.out(0).fire) || (preDecVec(1).io.is_br && preDecVec(1).io.br_type===BRtype.R && !preDecVec(1).io.is_ret && io.out(1).fire)){
      ifuState := IFUState.stall
      //pc := pcVec(0) + outFireCount * 4.U
    }
    // pc := pc + PopCount(io.out.map(_.fire)) * 4.U//指向下一条指令
    //等待分支结果时
  }.elsewhen(ifuState === IFUState.stall){
    io.out(0).valid := false.B
    io.out(1).valid := false.B
    when(io.in.valid && (io.in.bits.mispred || (io.in.bits.is_jalr && !io.in.bits.is_ret))){
      pc := io.in.bits.new_pc
      ifuState := IFUState.continue
    }
  }.otherwise{
    io.out(0).valid := false.B
    io.out(1).valid := false.B
  }

  bpu.io.gshare_update.valid := io.in.valid && io.in.bits.is_B
  bpu.io.gshare_update.bits.taken := io.in.bits.taken
  bpu.io.gshare_update.bits.idx := io.in.bits.gshare_idx

  bpu.io.ras_update.target := io.in.bits.pc + 4.U
  bpu.io.ras_update.is_ret := io.in.valid && io.in.bits.is_ret
  bpu.io.ras_update.iscall := io.in.valid && io.in.bits.is_call

  bpu.io.flush := io.in.valid && io.in.bits.mispred

  
  // printf("IFU stall %d\n", ifuState === IFUState.stall)
  // printf("preDecode1: inst %x, is_br %d, br_type %d, is_ret %d, offset %x\n",instrVec(0),preDecVec(0).io.is_br, preDecVec(0).io.br_type, preDecVec(0).io.is_ret, preDecVec(0).io.offset)
  // printf("preDecode2: inst %x, is_br %d, br_type %d, is_ret %d, offset %x\n",instrVec(1),preDecVec(1).io.is_br, preDecVec(1).io.br_type, preDecVec(1).io.is_ret, preDecVec(1).io.offset)
  // printf("IFU branch predict %d %d, bpu jump pc %x\n",bpu.io.br_taken(0),bpu.io.br_taken(1),bpu.io.jump_pc)
  // printf("inst1: vaild %d, pc %x, inst %x \n",io.out(0).valid,io.out(0).bits.pc,io.out(0).bits.instr)
  // printf("inst2: vaild %d, pc %x, inst %x \n",io.out(1).valid,io.out(1).bits.pc,io.out(1).bits.instr)
  // printf("IFU pcReg %x\n", pc)
  // printf("IBF in.out.ready %d %d\n",io.out(0).ready,io.out(1).ready)
  // printf("--------one cycle--------\n")

}