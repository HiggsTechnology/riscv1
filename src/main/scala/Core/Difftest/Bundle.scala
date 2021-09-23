package Core.Difftest

import chisel3._

//class PerformanceIO extends Bundle {
//  val cycleCnt  : UInt = Output(UInt(64.W))
//  val instrCnt  : UInt = Output(UInt(64.W))
//}

class DifftestTrapIO extends Bundle {
  val code      : UInt = Output(UInt(3.W))
}
