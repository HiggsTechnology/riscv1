
// mill -i __.test.runMain core.TopMain -td ./build
package Core

import Core.CtrlBlock.{ControlBlock, ControlBlockOUT}
import Core.IFU.{IFU, Ibuffer}
import chisel3._
import chisel3.util.ValidIO

class TOPIN extends Bundle {
    val bruin = Flipped(ValidIO(new BRU_OUTIO))
}
class TOPOUT extends Bundle {
    val ctrlBlockOut = new ControlBlockOUT
}

class TOPIO extends Bundle {
    val in  = new TOPIN
    val out = new TOPOUT
}

class Top extends Module {
    val io       = IO(new TOPIO)
    val ifu      = Module(new IFU)
    val ibf      = Module(new Ibuffer)
    val ctrlblock = Module(new ControlBlock)
    ifu.io.in                       <>  io.in.bruin
    ibf.io.in                       <>  ifu.io.out
    ibf.io.flush                    :=  false.B
    ctrlblock.io.in.pcinstr         <>  ibf.io.out
    ctrlblock.io.in.commit          <>  DontCare
    ctrlblock.io.in.rs_can_allocate <>  DontCare
    io.out.ctrlBlockOut             <>  ctrlblock.io.out

    //    io.valid     := withClock(clock){
    //    ~reset.asBool()
    //    }
}