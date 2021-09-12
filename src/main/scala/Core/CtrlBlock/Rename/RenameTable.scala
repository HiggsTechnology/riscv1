package Core.CtrlBlock.Rename

import Core.Config
import Core.Config.PhyRegIdxWidth
import chisel3._
import chisel3.util._
import util._

class RatReadPort extends Bundle {
  val addr = Input(UInt(5.W))
  val rdata = Output(UInt(PhyRegIdxWidth.W))
}

class RatWritePort extends Bundle {
  val wen = Input(Bool())
  val addr = Input(UInt(5.W))
  val wdata = Input(UInt(PhyRegIdxWidth.W))
}

class RenameTableIO(float: Boolean) extends Bundle with Config{
  val flush = Input(Bool())
  val readPorts = Vec({if(float) 4 else 3} * 2, new RatReadPort)
  val specWritePorts = Vec(2, new RatWritePort)
  val archWritePorts = Vec(2, new RatWritePort)
  val debug_rdata = Vec(32, Output(UInt(PhyRegIdxWidth.W)))
}
class RenameTable(float: Boolean) extends Module with Config {
  val io = IO(new RenameTableIO(float: Boolean))
  // speculative rename table
  val spec_table = RegInit(VecInit(Seq.tabulate(32)(i => i.U(PhyRegIdxWidth.W))))

  // arch state rename table
  val arch_table = RegInit(VecInit(Seq.tabulate(32)(i => i.U(PhyRegIdxWidth.W))))
  //遍历specWritePorts ，在en有效时，传参至spec_table

  for (w <- io.specWritePorts){
    when (w.wen && (!io.flush)) {
      spec_table(w.addr) := w.wdata
    }
  }
  //zipWithIndex 遍历有序集合，循环计数   ？6个readport
  for((r, i) <- io.readPorts.zipWithIndex){
    r.rdata := spec_table(r.addr)
  }

  for(w <- io.archWritePorts){
    when(w.wen){ arch_table(w.addr) := w.wdata }
  }
  when (io.flush) {
    spec_table := arch_table
    // spec table needs to be updated when flushPipe
    for (w <- io.archWritePorts) {
      when(w.wen){ spec_table(w.addr) := w.wdata }
    }
  }
  io.debug_rdata := arch_table
}
