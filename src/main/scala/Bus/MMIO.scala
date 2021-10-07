package Bus

import Core.AXI4.AXI4Parameters.AXI_PROT
import Core.AXI4.{AXI4IO, AXI4Parameters}
import Core.Cache.{CacheReq, CacheResp}
import chisel3._
import chisel3.util._
import utils.{OutBool, OutUInt}
import Core.Config.MMIOConfig

object SimpleBusParameter {
  object SIZE {
    def EnumSize = 4
    def Width = log2Up(EnumSize)
    val bytes1 :: bytes2 :: bytes4 :: bytes8 :: Nil = Enum(EnumSize)
  }
}

// MMIO的通用简单读写端口
class SimpleReqBundle extends Bundle {
  val isWrite  : Bool = OutBool()
  val addr      : UInt = OutUInt(32)
  val data     : UInt = OutUInt(64)
  val wmask     : UInt = OutUInt(8)
  val size      : UInt = OutUInt(3)
  def toCacheReq() : CacheReq = {
    val res = new CacheReq
    res.addr := addr
    res.isWrite := isWrite
    res.data := data
    res.wmask := wmask
    res
  }
}

class SimpleRespBundle extends Bundle {
  val data     : UInt = OutUInt(64)
  def toCacheResp() : CacheResp = {
    val res = new CacheResp
    res.data := data
    res
  }
}

class SimpleBus extends Bundle {
  val req : DecoupledIO[SimpleReqBundle] = Decoupled(new SimpleReqBundle)
  val resp : DecoupledIO[SimpleRespBundle] = Flipped(Decoupled(new SimpleRespBundle))

  def toAXI4 : AXI4IO = {
    val axi4 = Wire(new AXI4IO())
    axi4.aw.valid         := req.valid && req.bits.isWrite
    axi4.aw.bits.addr     := req.bits.addr
    axi4.aw.bits.prot     := AXI_PROT.PRIVILEGED | AXI_PROT.SECURE | AXI_PROT.DATA
    axi4.aw.bits.id       := 0.U
    axi4.aw.bits.user     := 0.U
    axi4.aw.bits.len      := 0.U
    axi4.aw.bits.size     := req.bits.size
    axi4.aw.bits.burst    := AXI4Parameters.BURST_INCR
    axi4.aw.bits.lock     := 0.U
    axi4.aw.bits.cache    := 0.U
    axi4.aw.bits.qos      := 0.U
    axi4.aw.bits.region   := 0.U
    axi4.w.valid          := req.valid && req.bits.isWrite
    axi4.w.bits.data      := req.bits.data
    axi4.w.bits.strb      := req.bits.wmask
    axi4.w.bits.last      := true.B
    axi4.w.bits.user      := 0.U
    axi4.b.ready          := resp.ready
    axi4.ar.valid         := req.valid && !req.bits.isWrite
    axi4.ar.bits.addr     := req.bits.addr
    axi4.ar.bits.prot     := AXI_PROT.PRIVILEGED | AXI_PROT.SECURE | AXI_PROT.DATA
    axi4.ar.bits.id       := 0.U
    axi4.ar.bits.user     := 0.U
    axi4.ar.bits.len      := 0.U
    axi4.ar.bits.size     := req.bits.size
    axi4.ar.bits.burst    := AXI4Parameters.BURST_INCR
    axi4.ar.bits.lock     := 0.U
    axi4.ar.bits.cache    := 0.U
    axi4.ar.bits.qos      := 0.U
    axi4.ar.bits.region   := 0.U
    axi4.r.ready          := resp.ready
    req.ready             := (axi4.ar.ready && !req.bits.isWrite) || (axi4.aw.ready && req.bits.isWrite)
    resp.valid            := axi4.r.valid || axi4.b.valid // 难以区分，除非约定req是同步的
    resp.bits.data        := axi4.r.bits.data
    axi4
  }
}

class MMIO(num_master: Int, num_slave: Int, is_sim: Boolean) extends Module {
  class MMIO_IO extends Bundle {
    val master : Vec[SimpleBus] = Vec(num_master, Flipped(new SimpleBus))
    val slave : Vec[SimpleBus] = Vec(num_slave, new SimpleBus)
    val difftest_skip : Bool = OutBool()
  }
  val io : MMIO_IO = IO(new MMIO_IO)

  val crossbar = Module(new MMIOCrossbar(
    num_master = num_master, num_slave = num_slave,
    addrConfig = if(is_sim) MMIOConfig.simAddrMap else MMIOConfig.realAddrMap,
    is_sim = is_sim
  ))

  (crossbar.io.in zip io.master).foreach{ case(cb_in, io_master) => cb_in <> io_master }
  (crossbar.io.out zip io.slave).foreach{ case(cb_out, io_slave) => cb_out <> io_slave }

  // 告诉difftest不要比较 Todo:这个skip逻辑不对，存在并行，skip需要和对应的访存指令绑定
  private val skip = io.slave(1).req.valid || io.slave(2).req.valid
  io.difftest_skip := skip
}

class MMIOCrossbar(num_master: Int, num_slave: Int, addrConfig: List[((Long, Long), Boolean)], is_sim: Boolean) extends Module {
  class MMIOCrossbarIO extends Bundle {
    val in : Vec[SimpleBus] = Vec(num_master, Flipped(new SimpleBus))
    val out : Vec[SimpleBus] = Vec(num_slave, new SimpleBus)
  }
  val io : MMIOCrossbarIO = IO(new MMIOCrossbarIO)

  val cb1toN = Seq.fill(num_master)(Module(new MMIOCrossbar1toN(addrConfig)))
  val cbNto1forClint = Module(new MMIOCrossbarNto1(num_master))
  val cbNto1forSimUart = if (is_sim) Module(new MMIOCrossbarNto1(num_master)) else null
  val cbNto1forAXI4 = Module(new MMIOCrossbarNto1(num_master))

  (cb1toN zip io.in).foreach { case (cb, io_in) => cb.io.in <> io_in }
  cb1toN(0).io.out(0) <> io.out(0)    // DCache in(0)
  cb1toN(1).io.out(0) <> io.out(1)    // DCache in(1)
  if (is_sim) {
    (cb1toN zip cbNto1forClint.io.in).foreach{ case (cb_out, cb_in) => cb_out.io.out(1) <> cb_in }
    cbNto1forClint.io.out <> io.out(2)  // Clint
    (cb1toN zip cbNto1forSimUart.io.in).foreach{ case (cb_out, cb_in) => cb_out.io.out(2) <> cb_in }
    cbNto1forSimUart.io.out <> io.out(3)// SimUart
    (cb1toN zip cbNto1forAXI4.io.in).foreach{ case (cb_out, cb_in) => cb_out.io.out(3) <> cb_in }
    cbNto1forAXI4.io.out <> io.out(4) // AXI4
  }
  else {
    (cb1toN zip cbNto1forClint.io.in).foreach{ case (cb_out, cb_in) => cb_out.io.out(1) <> cb_in }
    cbNto1forClint.io.out <> io.out(2)  // Clint
    (cb1toN zip cbNto1forAXI4.io.in).foreach{ case (cb_out, cb_in) => cb_out.io.out(2) <> cb_in }
    cbNto1forAXI4.io.out <> io.out(3) // AXI4
  }

}

class MMIOCrossbarNto1(num_master: Int) extends Module {
  class MMIOCrossbarNto1IO extends Bundle {
    val in : Vec[SimpleBus] = Vec(num_master, Flipped(new SimpleBus))
    val out = new SimpleBus
  }
  val io : MMIOCrossbarNto1IO = IO(new MMIOCrossbarNto1IO)

  // 一个状态机，每次只允许一个信号通过
  object State {
    val idle :: readResp :: writeResp :: Nil = Enum(3)
  }
  val state = RegInit(State.idle)

  val arb = Module(new Arbiter(chiselTypeOf(io.in(0).req.bits), num_master))

  // req
  (arb.io.in zip io.in).foreach{ case(arb_in, io_in) =>
    arb_in <> io_in.req
  }
  val selReq = arb.io.out
  val selIdx = Reg(UInt(log2Up(num_master).W))
  io.out.req.bits <> arb.io.out.bits
  io.out.req.valid := arb.io.out.valid && (state === State.idle)
  selReq.ready := io.out.req.ready && (state === State.idle)

  // resp
  io.in.foreach(_.resp.bits := io.out.resp.bits)
  io.in.foreach(_.resp.valid := false.B)
  io.in(selIdx).resp.valid := io.out.resp.valid
  io.out.resp.ready := io.in(selIdx).resp.ready

  when(state === State.idle) {
    when(selReq.fire()) {
      selIdx := arb.io.chosen
      when(selReq.bits.isWrite) {
        state := State.writeResp
      }.elsewhen(!selReq.bits.isWrite) {
        state := State.readResp
      }
    }
  }.elsewhen(state === State.readResp) {
    when(io.out.resp.fire()) {
      state := State.idle
    }
  }.elsewhen(state === State.writeResp) {
    when(io.out.resp.fire()) {
      state := State.idle
    }
  }


}

// 参考nutshell的SimpleBusCrossbar1toN实现
class MMIOCrossbar1toN(addrConfig: List[((Long, Long), Boolean)]) extends Module {
  class MMIOCrossbar1toNIO extends Bundle {
    val in : SimpleBus = Flipped(new SimpleBus)
    val out : Vec[SimpleBus] = Vec(addrConfig.length, new SimpleBus)
  }

  val io : MMIOCrossbar1toNIO = IO(new MMIOCrossbar1toNIO)

  // 一个状态机，每次只允许一个信号通过
  object State {
    val idle :: wait_resp :: Nil = Enum(2)
  }

  val state : UInt = RegInit(State.idle)

  val inAddr : UInt = io.in.req.bits.addr
  val outSelVec : Vec[Bool] = VecInit(addrConfig.map(
    config => (inAddr >= config._1._1.U && inAddr <= config._1._2.U)
  ))

  val outSelIdx : UInt = PriorityEncoder(outSelVec)
  val outSel : SimpleBus = io.out(outSelIdx)
  val outSelIdxResp = RegEnable(outSelIdx, outSel.req.fire() && (state === State.idle))
  val outSelResp = io.out(outSelIdxResp)

  //  printf("req_valid: %b, outSelVec: %b\n", io.in.valid, outSelVec.asUInt())

  when (!(!io.in.req.valid || outSelVec.asUInt.orR)) {
    printf("Warning: address decode error, bad addr = 0x%x\n", inAddr)
  }
//  assert(!io.in.req.valid || outSelVec.asUInt.orR, "address decode error, bad addr = 0x%x\n", inAddr)
  assert(!(io.in.req.valid && outSelVec.asUInt.andR), "address decode error, bad addr = 0x%x\n", inAddr)

  // bind out.req channel
  (io.out zip outSelVec).foreach { case (out, v) => {
    out.req.bits   <> io.in.req.bits
    out.req.valid  := v && (io.in.req.valid && (state === State.idle))
    out.resp.ready := v
  }}

  io.in.resp.valid  := outSelResp.resp.fire() || RegNext(!(!io.in.req.valid || outSelVec.asUInt.orR))
  io.in.resp.bits   <> outSelResp.resp.bits
  outSelResp.resp.ready := io.in.resp.ready
  io.in.req.ready   := outSel.req.ready

  switch (state) {
    is (State.idle)       { when (outSel.req.fire()) { state := State.wait_resp } }
    is (State.wait_resp)  { when (outSelResp.resp.fire()) { state := State.idle } }
  }
}