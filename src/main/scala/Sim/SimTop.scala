//
//// mill -i __.test.runMain core.TopMain -td ./build
//package Sim
//
//import Bus.MMIO
//import Core.CtrlBlock.{ControlBlock, ControlBlockOUT}
//import Core.ExuBlock.ExuBlock
//import Core.IFU.{IFU, Ibuffer}
//import chisel3._
//import chisel3.util.ValidIO
//import difftest._
//import Core.Cache.DCache
//import Core.AXI4.{AXI4IO, CROSSBAR_Nto1, Crossbar}
//import Device.{Clint, SimUart}
//
//class SimTopIO extends Bundle {
//  val logCtrl = new LogCtrlIO
//  val perfInfo = new PerfInfoIO
//  val uart = new UARTIO
//  val memAXI_0 = new AXI4IO
//}
//
//class SimTop extends Module {
//  val io       = IO(new SimTopIO)
//
//
//  val ifu      = Module(new IFU)
//  val ibf      = Module(new Ibuffer)
//  val ctrlblock = Module(new ControlBlock)
//  val exublock = Module(new ExuBlock)
//  val icache = Module(new DCache(cacheNum = 0))
//  val dcache = Module(new DCache(cacheNum = 1))
//  val crossbar  = Module(new CROSSBAR_Nto1(1,2))
//  val clint     = Module(new Clint)
//  val simUart   = Module(new SimUart)
//  // master:  2;  2 lsu
//  // slave:   5;  2 DataCache, 1 clint, 1 SimUart, 1 AXI4Crossbar write straightly
//  val mmio     = Module(new MMIO(num_master = 2, num_slave = 5, is_sim = true))
//
//  io.memAXI_0 <> crossbar.io.out
//
//  crossbar.io.in(0) <> icache.io.to_rw
//  crossbar.io.in(1) <> dcache.io.to_rw
//  crossbar.io.in(2) <> mmio.io.slave(4).toAXI4
//
//  icache.io.bus <> ifu.io.toMem
//
//  mmio.io.master(0) <> exublock.io.toMem(0)
//  mmio.io.master(1) <> exublock.io.toMem(1)
//
//  dcache.io.bus(0)  <> mmio.io.slave(0)
//  dcache.io.bus(1)  <> mmio.io.slave(1)
//  clint.io.bus      <> mmio.io.slave(2)
//  simUart.io.bus    <> mmio.io.slave(3)
//  io.uart           <> simUart.io.uart
//
//  ifu.io.redirect                 :=  exublock.io.redirect
//  ifu.io.in                       :=  exublock.io.bpu_update
//  ibf.io.in                       <>  ifu.io.out
//  ibf.io.flush                    :=  exublock.io.redirect.valid && exublock.io.redirect.bits.mispred
//  ibf.io.flush_commit             :=  ctrlblock.io.out.flush_commit
//  ctrlblock.io.in.pcinstr         <>  ibf.io.out
//
//  exublock.io.in                  <>  ctrlblock.io.out.microop
//  exublock.io.rs_num_in           <>  ctrlblock.io.out.rs_num_out
//  exublock.io.busytablein         <>  ctrlblock.io.out.pregValid
//  ctrlblock.io.in.rs_can_allocate <>  exublock.io.rs_can_allocate
//  //todo:ctrlblock里的信号传给exublock的lsq，分支robIdx
//  ctrlblock.io.in.exuCommit          <>  exublock.io.exuCommit
//  ctrlblock.io.in.redirect := exublock.io.redirect
//  exublock.io.predict_robPtr := ctrlblock.io.out.predict_robPtr
//
//  exublock.io.debug_int_rat := ctrlblock.io.out.debug_int_rat
//
//  dontTouch(ifu.io)
//  dontTouch(ibf.io)
//  dontTouch(ctrlblock.io)
//  dontTouch(exublock.io)
//  //    io.valid     := withClock(clock){
//  //    ~reset.asBool()
//  //    }
//}