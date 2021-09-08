package Core.IFU

import Core.AXI4.AXI4Parameters
import Core.Config.Config
import Core.MemReg.RAMHelper
import chisel3._
import chisel3.util._
import utils.{BRU_OUTIO, IFU2RW, Pc_Instr}

class IFUIO(use_axi:Boolean=false) extends Bundle {
  val bru  = Flipped(Valid(new BRU_OUTIO))  //branch
  val out =  Valid(new Pc_Instr)
  val stall = Input(Bool())
  val ifu2rw = if(use_axi) new IFU2RW else null
}

class IFU(use_axi:Boolean=false) extends Module with Config {
  val io = IO(new IFUIO(use_axi))
  val pc = RegInit(PC_START.U(XLEN.W))
  val pc_next = RegInit((PC_START + 4).U(XLEN.W))


  val inst = WireInit(0.U)

  if (use_axi) {
    when(!io.stall) {
      pc      := Mux(io.bru.bits.ena, io.bru.bits.new_pc, pc + 4.U)
    }
    // pc不会被立即使用，而是在rw中延迟一个周期
    // Todo: 在IFU_RW_AXI把这个延迟的周期删去，取指减少一个周期
    io.ifu2rw.valid   := true.B
    io.ifu2rw.pc      := pc
    val rdata         = io.ifu2rw.rdata
    when(io.ifu2rw.ready) {
      inst := MuxLookup(pc(AXI4Parameters.addrAlignedBits-1,2), 0.U, Array(
        0.U -> rdata(32*1-1, 32*0),
        1.U -> rdata(32*2-1, 32*1),
        2.U -> rdata(32*3-1, 32*2),
        3.U -> rdata(32*4-1, 32*3),
        4.U -> rdata(32*5-1, 32*4),
        5.U -> rdata(32*6-1, 32*5),
        6.U -> rdata(32*7-1, 32*6),
        7.U -> rdata(32*8-1, 32*7),
      ))
    }
    io.out.bits.pc    := pc
    io.out.bits.instr := inst
    // 告知下一个模块，数据可用
    io.out.valid      := io.ifu2rw.ready
  }
  else {
    when(!io.stall) {
      pc        := Mux(io.bru.bits.ena, io.bru.bits.new_pc, pc + 4.U)
    }
    val ram = Module(new RAMHelper)
    ram.io.clk := clock
    ram.io.en  := !reset.asBool()
    val idx = (pc - PC_START.U) >> 3
    ram.io.rIdx := idx
    val rdata = ram.io.rdata
    ram.io.wIdx := DontCare
    ram.io.wen  := false.B
    ram.io.wdata := DontCare
    ram.io.wmask := DontCare

    inst :=   Mux(pc(2),rdata(63,32),rdata(31,0))
    io.out.valid    := true.B

    io.out.bits.pc  := pc
    io.out.bits.instr := inst
  }
  printf("bru: valid: %d, ena: %d, new pc: %x\n", io.bru.valid, io.bru.bits.ena, io.bru.bits.new_pc(31,0))
}