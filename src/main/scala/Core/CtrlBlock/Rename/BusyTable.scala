package Core.CtrlBlock.Rename

import chisel3._
import chisel3.util._
import utils._

class BusyTableReadIO extends Bundle {
  val req = Input(UInt(PhyRegIdxWidth.W))
  val resp = Output(Bool())
}

class BusyTable(numReadPorts: Int, numWritePorts: Int) extends Module with Config {
  val io = IO(new Bundle() {
    val flush = Input(Bool())
    // set preg state to busy
    val allocPregs = Vec(2, Flipped(ValidIO(UInt(PhyRegIdxWidth.W))))
    // set preg state to ready (write back regfile + roq walk)
    val wbPregs = Vec(numWritePorts, Flipped(ValidIO(UInt(PhyRegIdxWidth.W))))
    // read preg state
    val read = Vec(numReadPorts, new BusyTableReadIO)
  })

  val table = RegInit(0.U(NRPhyRegs.W))

  def reqVecToMask(rVec: Vec[Valid[UInt]]): UInt = {
    ParallelOR(rVec.map(v => Mux(v.valid, UIntToOH(v.bits), 0.U)))
  }

  val wbMask = reqVecToMask(io.wbPregs)
  val allocMask = reqVecToMask(io.allocPregs)

  val tableAfterWb = table & (~wbMask).asUInt
  val tableAfterAlloc = tableAfterWb | allocMask

  io.read.map(r => r.resp := !table(r.req))

  table := tableAfterAlloc

  when(io.flush){
    table := 0.U(NRPhyRegs.W)
  }
}
