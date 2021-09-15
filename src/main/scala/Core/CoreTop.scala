
// mill -i __.test.runMain core.TopMain -td ./build
package Core

import Core.CtrlBlock.{ControlBlock, ControlBlockOUT}
import Core.ExuBlock.ExuBlock
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

class CoreTop extends Module {
    val io       = IO(new TOPIO)
    io.out := DontCare
    val ifu      = Module(new IFU)
    val ibf      = Module(new Ibuffer)
    val ctrlblock = Module(new ControlBlock)
    val exublock = Module(new ExuBlock)

    ifu.io.in                       <>  exublock.io.redirect
    ibf.io.in                       <>  ifu.io.out
    ibf.io.flush                    :=  false.B
    ctrlblock.io.in.pcinstr         <>  ibf.io.out

    exublock.io.in                  <>  ctrlblock.io.out.microop
    exublock.io.rs_num_in           <>  ctrlblock.io.out.rs_num_out
    exublock.io.busytablein         <>  ctrlblock.io.out.pregValid
    ctrlblock.io.in.rs_can_allocate <>  exublock.io.rs_can_allocate

    ctrlblock.io.in.commit          <>  exublock.io.out

    exublock.io.debug_int_rat := ctrlblock.io.out.debug_int_rat

    dontTouch(ifu.io)
    dontTouch(ibf.io)
    dontTouch(ctrlblock.io)
    dontTouch(exublock.io)
    //    io.valid     := withClock(clock){
    //    ~reset.asBool()
    //    }
}