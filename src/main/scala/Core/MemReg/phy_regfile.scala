package Core.MemReg

import Core.Config.Config
import chisel3._


class RegfileFunc(numPreg: Int) extends Config {
  val regs = RegInit(VecInit(Seq.fill(numPreg)(0.U(XLEN.W))))

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
 
class RegReadIO extends Bundle with Config {
  val addr = Input(UInt(PhyRegIdxWidth.W))
  val data = Output(UInt(XLEN.W))
}

class RegWriteIO extends Bundle with Config {
  val addr = Input(UInt(PhyRegIdxWidth.W))
  val data = Input(UInt(XLEN.W))
  val ena  = Input(Bool())
}

class RegfileIO(numReadPorts: Int, numWritePorts: Int) extends Bundle {
  val read  = Vec(numReadPorts, new RegReadIO)
  val write = Vec(numWritePorts, new RegWriteIO)
}

class Regfile(numReadPorts: Int, numWritePorts: Int, numPreg: Int) extends Module {
  val io = IO(new RegfileIO(numReadPorts, numWritePorts))
  val regfile = new RegfileFunc(numPreg)

  for(i <- 0 until numWritePorts){
    when(io.write(i).ena) {
      regfile.write(io.write(i).addr, io.write(i).data)
    }
  }

  for(i <- 0 until numReadPorts){
    io.read(i).data := regfile.read(io.read(i).addr)
  }

}