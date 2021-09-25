package Bus

import chisel3._
import chisel3.util._
import utils.BasicIOType.{OutBool, OutUInt}
import utils.SimpleSyncBus

// MMIO的通用简单读写端口
class MMIOSimpleReqBundle extends Bundle {
  val is_write  : Bool = OutBool()
  val addr      : UInt = OutUInt(32)
  val wdata     : UInt = OutUInt(64)
  val wstrb     : UInt = OutUInt(8)
  val size      : UInt = OutUInt(3)
}

class MMIOSimpleRespBundle extends Bundle {
  val rdata     : UInt = OutUInt(64)
}

class MMIOSimpleBus extends Bundle {
  val req : DecoupledIO[MMIOSimpleReqBundle] = Decoupled(new MMIOSimpleReqBundle)
  val resp : DecoupledIO[MMIOSimpleRespBundle] = Flipped(Decoupled(new MMIOSimpleRespBundle))
}

class MMIO(num_slave: Int = 3) extends Module {
  class MMIO_IO extends Bundle {
    val master : SimpleSyncBus = Flipped(new SimpleSyncBus)
    val slave : Vec[SimpleSyncBus] = Vec(num_slave, new SimpleSyncBus)
    val difftest_skip : Bool = OutBool()
  }
  val io : MMIO_IO = IO(new MMIO_IO)

  /**
   * address mapping<br/>
   * clint始终在CPU内部<br/>
   * uart仿真时在SimTop发出，接上SoC后与memory统一处理<br/>
   */
  private val addrMap : Map[String, Tuple2[Long, Long]] = Map(
    "clint"     ->  (0x02000000L, 0x0200ffffL), // "clint"
    "uart16550" ->  (0x10000000L, 0x10000fffL), // "uart16550"
    "spi"       ->  (0x10001000L, 0x10001fffL), // "spi"
    "spi-xip"   ->  (0x30000000L, 0x3fffffffL), // "spi-xip"
    "chiplink"  ->  (0x40000000L, 0x7fffffffL), // "chiplink"
    "mem"       ->  (0x80000000L, 0xffffffffL), // "mem"
  )
  private val activateAddrMap = List(
    addrMap("mem"),
    addrMap("clint"),
    addrMap("uart16550"),
  )

  private val crossbar : MMIOCrossbar1toN = Module(new MMIOCrossbar1toN(activateAddrMap))
  crossbar.io.in  <> io.master
  crossbar.io.out(0) <> io.slave(0)
  crossbar.io.out(1) <> io.slave(1)
  crossbar.io.out(2) <> io.slave(2)

  private val skip = RegNext(io.slave(1).valid || io.slave(2).valid)
  io.difftest_skip := skip
}

// 参考nutshell的SimpleBusCrossbar1toN实现
class MMIOCrossbar1toN(addrSpace: List[Tuple2[Long, Long]]) extends Module {
  class MMIOCrossbar1toNIO extends Bundle {
    val in : SimpleSyncBus = Flipped(new SimpleSyncBus)
    val out : Vec[SimpleSyncBus] = Vec(addrSpace.length, new SimpleSyncBus)
  }

  val io : MMIOCrossbar1toNIO = IO(new MMIOCrossbar1toNIO)

  // 一个状态机，每次只允许一个信号通过
  object State {
    val idle :: wait_resp :: Nil = Enum(2)
  }

  val state : UInt = RegInit(State.idle)

  val inAddr : UInt = io.in.addr
  val outSelVec : Vec[Bool] = VecInit(addrSpace.map(
    range => (inAddr >= range._1.U && inAddr <= range._2.U)
  ))
//  printf("req_valid: %b, outSelVec: %b\n", io.in.valid, outSelVec.asUInt())

  val outSelIdx : UInt = PriorityEncoder(outSelVec)
  val outSel : SimpleSyncBus = io.out(outSelIdx)

  assert(!io.in.valid || outSelVec.asUInt.orR, "address decode error, bad addr = 0x%x\n", inAddr)
  assert(!(io.in.valid && outSelVec.asUInt.andR), "address decode error, bad addr = 0x%x\n", inAddr)

  // bind out channel
  (io.out zip outSelVec).foreach { case (o, v) => {
    o.valid     := v && state === State.idle && io.in.valid
    o.addr      := io.in.addr
    o.is_write  := io.in.is_write
    o.wdata     := io.in.wdata
    o.wstrb     := io.in.wstrb
    o.size      := io.in.size
  }}

  switch (state) {
    is (State.idle)       { when (outSel.valid) { state := State.wait_resp } }
    is (State.wait_resp)  { when (outSel.ready) { state := State.idle } }
  }
  io.in.ready   := outSel.ready
  io.in.rdata   := outSel.rdata
}