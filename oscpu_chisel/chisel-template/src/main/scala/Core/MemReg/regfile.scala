package Core.MemReg

import chisel3._
import utils.Config

class RegfileFunc extends Config {
  val regs = RegInit(VecInit(Seq.fill(32)(0.U(XLEN.W))))

  def write(addr: UInt, data: UInt): Unit = {
    when(addr =/= 0.U) {
      regs(addr) := data
    }
    ()
  }

  def read(addr: UInt): UInt = {
    regs(addr)
  }
}
 
class RegReadIO_1 extends Bundle with Config {
  val addr = Input(UInt(5.W))
  val data = Output(UInt(XLEN.W))
}

class RegReadIO_2 extends Bundle with Config {
  val addr = Input(UInt(5.W))
  val data = Output(UInt(XLEN.W))
}

class RegWriteIO extends Bundle with Config {
  val addr = Input(UInt(5.W))
  val data = Input(UInt(XLEN.W))
  val ena  = Input(Bool())
}

class RegfileIO extends Bundle {
  val src1  = new RegReadIO_1 
  val src2  = new RegReadIO_2 
  val rd   = new RegWriteIO
}

class Regfile extends Module {
  val io = IO(new RegfileIO)
  val regfile = new RegfileFunc

  // io.src1.addr := regfile.read(io.src1.addr)
  printf("Print during simulation: io.rd.ena is %d\n", io.rd.ena)
  printf("Print during simulation: io.rd.addr is %d\n", io.rd.addr)
  printf("Print during simulation: io.rd.data is %x\n", io.rd.data)
  printf("Print during simulation: regfile.regs(io.rd.addr) is %x\n", regfile.read(io.rd.addr))
  when(io.rd.ena) {
    regfile.write(io.rd.addr, io.rd.data)
  }
  io.src1.data := regfile.read(io.src1.addr)
  io.src2.data := regfile.read(io.src2.addr)
  printf("Print during simulation: io.src1.data is %x\n", io.src1.data)

  val mod = Module(new difftest.DifftestArchIntRegState)
  mod.io.clock := clock
  mod.io.coreid := 0.U
  mod.io.gpr := RegNext(regfile.regs)
}