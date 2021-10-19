package Core.AXI4

import Core.Config
import chisel3._
import chisel3.util._
//import chisel3.util.{Arbiter, LockingArbiter, Enum, is, switch}


class CrossbarIO extends Bundle with Config{
  val in    : Vec[AXI4IO] = Vec(2, Flipped(new AXI4IO))
  val out   : AXI4IO      = new AXI4IO
}

class Crossbar extends Module with Config {
  val io = IO(new CrossbarIO)

  object State {
    val idle :: resp :: Nil = Enum(2)
  }

  private val rState = RegInit(State.idle)
  private val readArb = Module(new Arbiter(chiselTypeOf(io.in(0).ar.bits), 2))

  (readArb.io.in zip io.in.map(_.ar)).foreach{ case (arb, in) => arb <> in }
  private val curAReadReq = readArb.io.out
  private val chosenRead  = readArb.io.chosen

  // ar
  io.out.ar.bits  <> curAReadReq.bits
  io.out.ar.bits.id := chosenRead
  io.out.ar.valid := curAReadReq.valid && (rState === State.idle)
  curAReadReq.ready := io.out.ar.ready && (rState === State.idle)

  // r: resp
  // 连接所有的读写回应到master
  io.in.foreach(_.r.valid := false.B)
  io.in.foreach(_.r.bits := io.out.r.bits)
  io.out.r.ready := false.B
  // 处理r valid/ready
  when(io.out.r.valid) {
    (io.in(io.out.r.bits.id).r, io.out.r) match {
      case (l, r) =>
        l.valid := r.valid
        r.ready := l.ready
    }
  }

  when(rState === State.idle) {
    when(curAReadReq.fire()) { rState := State.resp }
  }.elsewhen(rState === State.resp) {
    when(io.out.r.fire() && io.out.r.bits.last) { rState := State.idle }
  }


  io.out.aw <> io.in(1).aw
  io.out.w  <> io.in(1).w
  io.out.b  <> io.in(1).b

  io.in(0).aw.ready := false.B
  io.in(0).w.ready  := false.B
  io.in(0).b.valid  := false.B
  io.in(0).b.bits   := DontCare
}

class CROSSBAR_Nto1IO(ro_num : Int, rw_num : Int) extends Bundle with Config {
  val in : Vec[AXI4IO] = Vec(ro_num + rw_num, Flipped(new AXI4IO))
  val out : AXI4IO = new AXI4IO
  // todo: 区分readonly端口和readwrite端口
  //  val in_ro : Vec[AXI4IO] = Vec(ro_num, Flipped(new AXI4IO))
  //  val in_rw : Vec[AXI4IO] = Vec(rw_num, Flipped(new AXI4IO))

}

// todo: 是否分成读写两个Nto1模块
class CROSSBAR_Nto1(ro_num : Int, rw_num : Int) extends Module with Config {
  val io : CROSSBAR_Nto1IO = IO(new CROSSBAR_Nto1IO(ro_num, rw_num))
  // todo: 区分readonly端口和readwrite端口

  // todo: 使用LockingArbiter支持burst模式的总线独占，并处理可能的相邻周期的争用冲突，因为master发送请求需要不止一个周期
  object State {
    val idle :: resp :: Nil = Enum(2)
  }

  private val rState = RegInit(State.idle)


  // 带锁的仲裁器，如果满足锁定条件就锁定8个周期
  private val readArb = Module(new Arbiter(chiselTypeOf(io.in(0).ar.bits), ro_num + rw_num))
  (readArb.io.in zip io.in.map(_.ar)).foreach { case (arb, in) => arb <> in }

  private val curAReadReq = readArb.io.out
  private val chosenRead = readArb.io.chosen

  // ar
  io.out.ar.bits <> curAReadReq.bits
  io.out.ar.bits.id := chosenRead
  io.out.ar.valid := curAReadReq.valid && (rState === State.idle)
  curAReadReq.ready := io.out.ar.ready && (rState === State.idle)
  // r resp
  // 连接所有的读写回应到master
  io.in.foreach(_.r.valid := false.B)
  io.in.foreach(_.r.bits := io.out.r.bits)
  io.out.r.ready := false.B

  // 处理r/b valid/ready
  when(io.out.r.valid) {
    (io.in(io.out.r.bits.id).r, io.out.r) match {
      case (l, r) =>
        l.valid := r.valid
        r.ready := l.ready
    }
  }

  val lockWriteFun = ((x: AXI4BundleA) => x.isBurst)

  private val wState = RegInit(State.idle)
  private val writeArb = Module(new Arbiter(chiselTypeOf(io.in(0).aw.bits), ro_num + rw_num))
  (writeArb.io.in zip io.in.map(_.aw)).foreach { case (arb, in) => arb <> in }
  private val curAWriteReq = writeArb.io.out
  private val chosenWrite = writeArb.io.chosen
  // aw
  io.out.aw.bits <> curAWriteReq.bits
  io.out.aw.bits.id := chosenWrite
  io.out.aw.valid := curAWriteReq.valid && (wState === State.idle)
  curAWriteReq.ready := io.out.aw.ready && (wState === State.idle)
  // w
  val writeWayReg = RegInit(chosenWrite)
  when(io.out.aw.fire()){
    writeWayReg := chosenWrite
  }
  val writeCnt = RegInit(0.U(10.W))
  val writeWay = Mux(wState === State.idle, chosenWrite, writeWayReg)
  when(io.out.aw.fire() && !io.in(writeWay).w.fire()){
    writeCnt := io.out.aw.bits.len + 1.U
  }.elsewhen((io.out.w.fire() && writeCnt =/= 0.U) && wState === State.resp){
    writeCnt := writeCnt - 1.U
  }

  io.out.w.valid := io.in(writeWay).w.valid && (writeCnt =/= 0.U || wState === State.idle)
  io.out.w.bits <> io.in(writeWay).w.bits
  io.in.foreach(_.w.ready := false.B)
  io.in(writeWay).w.ready := io.out.w.ready && (writeCnt =/= 0.U || wState === State.idle)
  // b
  io.in.foreach(_.b.valid := false.B)
  io.in.foreach(_.b.bits := io.out.b.bits)
  io.out.b.ready := false.B

  when(io.out.b.valid) {
    (io.in(io.out.b.bits.id).b, io.out.b) match {
      case (l, r) =>
        l.valid := r.valid
        r.ready := l.ready
    }
  }

  //------------------------------状态机-----------------------------------

  switch(rState) {
    is(State.idle) {
      when(curAReadReq.fire()) {
        rState := State.resp
      }
    }
    is(State.resp) {
      when(io.out.r.fire() && io.out.r.bits.last) {
        rState := State.idle
      }
    }
  }

  switch(wState) {
    is(State.idle) {
      when(curAWriteReq.fire()) {
        wState := State.resp
      }
    }
    is(State.resp) {
      when(io.out.b.fire()) {
        wState := State.idle
      }
    }
  }

  //  printf("---------crossbar------------------\n")
  //  printf("crossbarWState: %d ", wState)
  //  when(curAReadReq.valid) { printf("chosenRead: %d", chosenRead) }
  //  when(curAWriteReq.valid) { printf("chosenWrite: %d", chosenWrite) }
  //  printf("aw_valid: %d, aw_ready: %d, w_valid: %d, w_ready: %d, b_valid: %d, b_ready: %d\n",
  //    io.out.aw.valid, io.out.aw.ready, io.out.w.valid, io.out.w.ready, io.out.b.valid, io.out.b.ready
  //  )
  //
  //  when(io.out.ar.fire())  { printf("ar.fire ") }
  //  when(io.out.aw.fire())  { printf("aw.fire ") }
  //  when(io.out.r.fire())   { printf("r.fire ") }
  //  when(io.out.w.fire())   { printf("w.fire ") }
  //  when(io.out.b.fire())   { printf("b.fire ") }
  //  when(io.out.r.valid)    { printf("r.id: %d", io.out.r.bits.id) }
  //  when(io.out.b.valid)    { printf("b.id: %d", io.out.b.bits.id) }
  //  printf("\n")

}

