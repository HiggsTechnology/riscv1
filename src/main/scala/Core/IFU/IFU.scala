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
  pcVec(0) := Mux(io.in.valid && (io.in.bits.mispred || io.in.bits.is_jalr), Mux(io.in.bits.taken, io.in.bits.new_pc, pc), pc)
  // io.flush := io.in.valid
  // 一旦进来的信号valid，则表明之前预测错了，则冲刷
  for(i <- 1 until FETCH_WIDTH){
    pcVec(i) := pcVec(i-1) + 4.U
  }

  for(i <- 0 until FETCH_WIDTH){
    // 依次从pc处开始取指
    ramVec(i).io.clk   := clock
    ramVec(i).io.en    := true.B
    ramVec(i).io.rIdx  := (pcVec(i) - PC_START.U) >> 3
    rdataVec(i)        := ramVec(i).io.rdata
    ramVec(i).io.wIdx  := DontCare
    ramVec(i).io.wen   := false.B
    ramVec(i).io.wdata := DontCare
    ramVec(i).io.wmask := DontCare
    instrVec(i)        := Mux(pcVec(i)(2),rdataVec(i)(63,32),rdataVec(i)(31,0))

    preDecVec(i).io.instr:= instrVec(i)

    // when(preDecVec(i).io.brtype===BRtype.B){
    io.out(i).bits.br_taken := preDecVec(i).io.br_taken
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
    when(preDecVec(0).io.is_br){
      io.out(0).valid := true.B
      io.out(1).valid := false.B
      pc := pcVec(0) + preDecVec(0).io.offset
      //     ifuState := IFUState.stall
    }.elsewhen(preDecVec(1).io.is_br){
      io.out(0).valid := true.B
      io.out(1).valid := true.B
      pc := pcVec(1) + preDecVec(1).io.offset
      //     ifuState := IFUState.stall
    }.otherwise{
      io.out(0).valid := true.B
      io.out(1).valid := true.B
      pc := pcVec(0) + outFireCount * 4.U
    }

    when(preDecVec(0).io.br_type===BRtype.R || preDecVec(1).io.br_type===BRtype.R){
      ifuState := IFUState.stall
      pc := pcVec(0) + outFireCount * 4.U
    }
    // pc := pc + PopCount(io.out.map(_.fire)) * 4.U//指向下一条指令
    //等待分支结果时
  }.elsewhen(ifuState === IFUState.stall){
    io.out(0).valid := false.B
    io.out(1).valid := false.B
    when(io.in.valid && (io.in.bits.mispred || io.in.bits.is_jalr)){
      pc := Mux(io.in.bits.taken, io.in.bits.new_pc, pc)
      ifuState := IFUState.continue
    }
  }.otherwise{
    io.out(0).valid := false.B
    io.out(1).valid := false.B
  }

}