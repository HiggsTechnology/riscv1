package Core.EXU

import Core.Config.Config
import Core.Difftest.DifftestTrapIO
import Core.IDU.FuncType
import Core.MemReg.RegWriteIO
import chisel3._
import chisel3.util._
import utils.{BRU_OUTIO, CfCtrl, InstInc, LSU2RW}

class EXUIO(
  use_axi: Boolean = true,
  need_difftest: Boolean = false
           ) extends Bundle {
  val in : ValidIO[CfCtrl] = Flipped(Valid(new CfCtrl))
  val reg_write_back : ValidIO[RegWriteIO] = Valid(new RegWriteIO)
  val branch : ValidIO[BRU_OUTIO] = Valid(new BRU_OUTIO)
  val lsu2rw : LSU2RW = if(use_axi) new LSU2RW else null
  val difftest_trapcode : ValidIO[DifftestTrapIO] = if(need_difftest) Flipped(Valid(new DifftestTrapIO)) else null
  val inst_inc : Valid[InstInc] = Flipped(Valid(new InstInc))
}

class EXU(
  use_axi:Boolean = true,
  need_difftest: Boolean = false
) extends Module with Config {
  val io : EXUIO = IO(new EXUIO(use_axi = use_axi, need_difftest = need_difftest))
  private val func = io.in.bits.ctrl.funcType
  private val op = io.in.bits.ctrl.funcOpType
  val alu : ALU = Module(new ALU)
  val lsu : LSU = Module(new LSU(use_axi))
  val bru : BRU = Module(new BRU)
  val csr : CSR = Module(new CSR(need_difftest))
  private val alu_ena = func === FuncType.alu
  private val lsu_ena = func === FuncType.lsu
  private val bru_ena = func === FuncType.bru
  private val csr_ena = func === FuncType.csr

  alu.io.in.valid   := alu_ena && io.in.valid
  lsu.io.in.valid   := lsu_ena && io.in.valid
  bru.io.in.valid   := bru_ena && io.in.valid
  csr.io.in.valid   := csr_ena && io.in.valid
  alu.io.in.bits <> io.in.bits
  lsu.io.in.bits <> io.in.bits
  bru.io.in.bits <> io.in.bits
  csr.io.in.bits <> io.in.bits
  csr.io.difftest_trapcode <> io.difftest_trapcode
  csr.io.inst_inc <> io.inst_inc

  private val wb_ena = Wire(Bool())
  private val wdata = Wire(UInt(XLEN.W))
  wb_ena := MuxLookup(func, false.B, Array(
    FuncType.alu -> true.B,
    FuncType.lsu -> LSUOpType.isLoad(op),
    FuncType.bru -> BRUOpType.isJalr(io.in.bits.ctrl.funcOpType),
    // csrr*[i]指令都需要写入寄存器堆，ecall ebreak mret等指令的rd对应位置为x0，置true也没有影响
    FuncType.csr -> true.B
  ))
  wdata := MuxLookup(func, 0.U(XLEN.W), Array(
    FuncType.alu -> alu.io.out.bits.aluRes,
    FuncType.lsu -> lsu.io.out.bits.rdata,
    FuncType.bru -> (io.in.bits.cf.pc + 4.U),
    FuncType.csr -> csr.io.out.bits.rdata
  ))
  // 表明子模块执行完成
  val exu_done = RegInit(false.B)
  withClock((~clock.asUInt()).asBool().asClock()) {
    exu_done := alu.io.out.valid || lsu.io.out.valid || bru.io.out.valid || csr.io.out.valid
  }
  val exu_done_wire = alu.io.out.valid || lsu.io.out.valid || bru.io.out.valid || csr.io.out.valid
  // 当译码信号有效时 且 ALU等子模块运算完成 才写入
  io.reg_write_back.valid     := exu_done_wire
  io.reg_write_back.bits.ena  := wb_ena
  io.reg_write_back.bits.addr := io.in.bits.ctrl.rfrd
  io.reg_write_back.bits.data := wdata

  // lsu和csr都会影响pc的值
  io.branch.valid := bru.io.out.valid || csr.io.out.valid
  io.branch.bits <> MuxLookup(func, 0.U.asTypeOf(new BRU_OUTIO), Array(
    FuncType.bru -> bru.io.out.bits,
    FuncType.csr -> csr.io.out.bits.jmp
  ))

  if(use_axi) {
    io.lsu2rw <> lsu.io.lsu2rw
  }
}