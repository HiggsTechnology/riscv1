package Core.CtrlBlock.Rename

import Core.Config
import Core.Config.PhyRegIdxWidth
import chisel3._
import chisel3.util._
import util._

class RatReadPort extends Bundle {
  val addr  = Input(UInt(5.W))
  val rdata = Output(UInt(PhyRegIdxWidth.W))
}
class RatWritePort extends Bundle {
  val wen   = Input(Bool())
  val addr  = Input(UInt(5.W))
  val wdata = Input(UInt(PhyRegIdxWidth.W))
}
class RenameTableIO(float: Boolean) extends Bundle with Config{
  val flush          = Input(Bool())
  val readPorts      = Vec({if(float) 4 else 3} * 2, new RatReadPort)
  val specWritePorts = Vec(2, new RatWritePort)
  val archWritePorts = Vec(2, new RatWritePort)
  val debug_rdata    = Vec(32, Output(UInt(PhyRegIdxWidth.W)))
}
class RenameTable(float: Boolean) extends Module with Config {
  val io = IO(new RenameTableIO(float: Boolean))
  // 定义两个寄存器表用于存储寄存器中数据，spec_table 用于第一次指令分配接入MicroOp
  val spec_table = RegInit(VecInit(Seq.tabulate(32)(i => i.U(PhyRegIdxWidth.W))))
  val arch_table = RegInit(VecInit(Seq.tabulate(32)(i => i.U(PhyRegIdxWidth.W))))
  // 传入新物理寄存器标号wdata至逻辑寄存器位置w.addr，将当前addr位置的寄存器标号取出作为新物理寄存器
  for (w <- io.specWritePorts){
    when (w.wen && (!io.flush)) {
      spec_table(w.addr) := w.wdata
    }
  }
  for((r, i) <- io.readPorts.zipWithIndex){
    r.rdata := spec_table(r.addr)
  }
  // arch_table 接入指令完成时commit数据
  for(w <- io.archWritePorts){
    when(w.wen){
      arch_table(w.addr) := w.wdata
    }
  }
  when (io.flush) {
    spec_table := arch_table
    // spec table needs to be updated when flushPipe
    for (w <- io.archWritePorts) {
      when(w.wen){ spec_table(w.addr) := w.wdata }
    }
  }
  io.debug_rdata := arch_table

  // for(i <- 0 until 32){
  //   printf("Rename Table %d is %d\n",i.U,spec_table(i))
  // }
}
