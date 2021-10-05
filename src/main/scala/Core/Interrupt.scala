package Core

import chisel3._
import utils.OutBool

class InterruptUnitIO extends Bundle {
  val ip = OutBool()   // time interrupt pending

}

class InterruptUnit extends Module {
  val io = IO(new InterruptUnitIO)


}
