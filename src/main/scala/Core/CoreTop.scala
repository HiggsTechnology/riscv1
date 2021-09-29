
// mill -i __.test.runMain core.TopMain -td ./build
package Core

import Core.CtrlBlock.{ControlBlock, ControlBlockOUT}
import Core.ExuBlock.ExuBlock
import Core.IFU.{IFU, Ibuffer}
import chisel3._
import chisel3.util.ValidIO
import difftest._

class SimTopIO extends Bundle {
  val logCtrl = new LogCtrlIO
  val perfInfo = new PerfInfoIO
  val uart = new UARTIO
}

class SimTop extends Module {
    val io       = IO(new SimTopIO)
    io.uart.in.valid  := false.B
    io.uart.out.valid := false.B
    io.uart.out.ch  := 0.U

    val ifu      = Module(new IFU)
    val ibf      = Module(new Ibuffer)
    val ctrlblock = Module(new ControlBlock)
    val exublock = Module(new ExuBlock)

    ifu.io.redirect                 :=  exublock.io.redirect
    ifu.io.in                       :=  exublock.io.bpu_update
    ibf.io.in                       <>  ifu.io.out
    ibf.io.flush                    :=  exublock.io.redirect.valid && exublock.io.redirect.bits.mispred
    ibf.io.flush_commit             :=  ctrlblock.io.out.flush_commit
    ctrlblock.io.in.pcinstr         <>  ibf.io.out

    exublock.io.in                  <>  ctrlblock.io.out.microop
    exublock.io.rs_num_in           <>  ctrlblock.io.out.rs_num_out
    exublock.io.busytablein         <>  ctrlblock.io.out.pregValid
    ctrlblock.io.in.rs_can_allocate <>  exublock.io.rs_can_allocate
    //todo:ctrlblock里的信号传给exublock的lsq，分支robIdx
    ctrlblock.io.in.exuCommit          <>  exublock.io.exuCommit
    ctrlblock.io.in.redirect := exublock.io.redirect
    exublock.io.predict_robPtr := ctrlblock.io.out.predict_robPtr

    exublock.io.debug_int_rat := ctrlblock.io.out.debug_int_rat

    dontTouch(ifu.io)
    dontTouch(ibf.io)
    dontTouch(ctrlblock.io)
    dontTouch(exublock.io)
    //    io.valid     := withClock(clock){
    //    ~reset.asBool()
    //    }
}