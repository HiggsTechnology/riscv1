package Core.AXI4
import Core.Config
import chisel3._


class CROSSBARIO extends Bundle with Config{
  val ifurwin : AXI4IO = Flipped(new AXI4IO)
  val lsurwin : AXI4IO = Flipped(new AXI4IO)
  val out     = new AXI4IO
}