package Bus.AXI4

import Core.Config.Config
import chisel3._
import chisel3.util.{Arbiter, Enum, is, switch}


class CROSSBAR_Nto1IO(ro_num: Int, rw_num: Int) extends Bundle with Config{
  val in    : Vec[AXI4IO] = Vec(ro_num + rw_num, Flipped(new AXI4IO))
  val out   : AXI4IO      = new AXI4IO
  // todo: 区分readonly端口和readwrite端口
  //  val in_ro : Vec[AXI4IO] = Vec(ro_num, Flipped(new AXI4IO))
  //  val in_rw : Vec[AXI4IO] = Vec(rw_num, Flipped(new AXI4IO))

}

// todo: 是否分成读写两个Nto1模块
class CROSSBAR_Nto1(ro_num: Int, rw_num: Int) extends Module with Config {
  val io: CROSSBAR_Nto1IO = IO(new CROSSBAR_Nto1IO(ro_num, rw_num))
  // todo: 区分readonly端口和readwrite端口

  // todo: 使用LockingArbiter支持burst模式的总线独占，并处理可能的相邻周期的争用冲突，因为master发送请求需要不止一个周期
  object State {
    val idle :: resp :: Nil = Enum(2)
  }

  private val rState = RegInit(State.idle)
  private val wState = RegInit(State.idle)

  // 仲裁模块不含寄存器，延迟0周期
  private val readArb = Module(new Arbiter(chiselTypeOf(io.in(0).ar.bits), ro_num + rw_num))
  private val writeArb = Module(new Arbiter(chiselTypeOf(io.in(0).aw.bits), ro_num + rw_num))

  (readArb.io.in zip io.in.map(_.ar)).foreach{ case (arb, in) => arb <> in }
  (writeArb.io.in zip io.in.map(_.aw)).foreach{ case (arb, in) => arb <> in }

  private val curAReadReq = readArb.io.out
  private val curAWriteReq = writeArb.io.out
  private val chosenRead  = readArb.io.chosen
  private val chosenWrite = writeArb.io.chosen

  // ar
  io.out.ar.bits  <> curAReadReq.bits
  io.out.ar.bits.id := chosenRead
  io.out.ar.valid := curAReadReq.valid && (rState === State.idle)
  curAReadReq.ready := io.out.ar.ready && (rState === State.idle)
  // aw
  io.out.aw.bits  <> curAWriteReq.bits
  io.out.aw.bits.id := chosenWrite
  io.out.aw.valid := curAWriteReq.valid && (wState === State.idle)
  curAWriteReq.ready := io.out.aw.ready && (wState === State.idle)
  // w
  io.out.w.valid  := io.in(chosenWrite).w.valid && (wState === State.idle)
  io.out.w.bits   <> io.in(chosenWrite).w.bits
  io.in.foreach(_.w.ready := false.B)
  io.in(chosenWrite).w.ready := io.out.w.ready && (wState === State.idle)
  // r/b: resp
  // 连接所有的读写回应到master
  io.in.foreach(_.r.valid := false.B)
  io.in.foreach(_.r.bits := io.out.r.bits)
  io.in.foreach(_.b.valid := false.B)
  io.in.foreach(_.b.bits := io.out.b.bits)
  io.out.r.ready := false.B
  io.out.b.ready := false.B
  // 处理r/b valid/ready
  when(io.out.r.valid) {
    (io.in(io.out.r.bits.id).r, io.out.r) match {
      case (l, r) =>
        l.valid := r.valid
        r.ready := l.ready
    }
  }

  when(io.out.b.valid) {
    (io.in(io.out.b.bits.id).b, io.out.b) match {
      case (l, r) =>
        l.valid := r.valid
        r.ready := l.ready
    }
  }

  //------------------------------状态机-----------------------------------

  switch (rState) {
    is(State.idle) { when(curAReadReq.fire()) { rState := State.resp } }
    is(State.resp) { when(io.out.r.fire() && io.out.r.bits.last) { rState := State.idle } }
  }

  switch (wState) {
    is(State.idle) { when(curAWriteReq.fire())  { wState := State.resp } }
    is(State.resp) { when(io.out.b.fire())      { wState := State.idle } }
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