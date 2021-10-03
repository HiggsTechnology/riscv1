package Core.CtrlBlock.Rename

import Core.Config
import chisel3._
import chisel3.util._
import utils._

class BusyTableReadIO extends Bundle with Config{
  val req  = Input(UInt(PhyRegIdxWidth.W))
  val resp = Output(Bool())
}
class BusyTableIO(numReadPorts: Int, numWritePorts: Int) extends Bundle with Config{
  val flush      = Input(Bool())
  val allocPregs = Vec(2, Flipped(ValidIO(UInt(PhyRegIdxWidth.W))))
  val wbPregs    = Vec(numWritePorts, Flipped(ValidIO(UInt(PhyRegIdxWidth.W))))
  val read       = Vec(numReadPorts, new BusyTableReadIO)
}
class BusyTable(numReadPorts: Int, numWritePorts: Int) extends Module with Config {
  //定义函数调用ParallelOR实现
  def reqVecToMask(rVec: Vec[Valid[UInt]]): UInt = {
    ParallelOR(rVec.map(v => Mux(v.valid, UIntToOH(v.bits), 0.U)))
  }
  val io = IO(new  BusyTableIO(numReadPorts, numWritePorts))
  val table = RegInit(0.U(NRPhyRegs.W))
  val wbMask = reqVecToMask(io.wbPregs) //io.webPregs = Vec(2,Width)
  //2*width的向量    def reqVecToMask
  // UIntToOH(v.bits) 传入UInt(PhyRegIdxWidth.W)  传回 OH 独热码
  // 两独热码按位取或
  // asUInt
  val allocMask       = reqVecToMask(io.allocPregs)
  val tableAfterWb    = table & (~wbMask).asUInt
  val tableAfterAlloc = tableAfterWb | allocMask
  io.read.map(r => r.resp := !table(r.req))
  table := Cat(tableAfterAlloc(NRPhyRegs-1,1), 0.U)
  when(io.flush){
    table := 0.U(NRPhyRegs.W)
  }
}
