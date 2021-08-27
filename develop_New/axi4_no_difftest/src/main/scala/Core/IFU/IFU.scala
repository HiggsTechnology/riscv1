package Core.IFU
import Core.AXI4.{AXI4Parameters, AXI4}
import Core.Config.Config
import chisel3._
// import utils.{BRU_OUTIO, Pc_Instr}
import utils._


class IFUIO extends Bundle {
  val in  = Flipped(new BRU_OUTIO)  //branch
  val out = Flipped(new Pc_Instr)
  val ifuaxi = new AXI4
}
class IFU extends Module with Config {
  val io: IFUIO = IO(new IFUIO)
  val axi4: AXI4 = io.ifuaxi
  val (ar,r) = (axi4.ar.bits,axi4.r.bits)
  val axi_na =  0.U
  val axi_ar = 1.U
  val axi_r = 2.U

  val inflight_type = RegInit(axi_ar)
  private def setState(axi_type: UInt) = {//, id: UInt
    inflight_type := axi_type
  }
  private def resetState() = {
    inflight_type := axi_ar
  }
  private def isState(state: UInt) = {
    inflight_type === state
  }
  private def isInflight() = {
    !isState(axi_ar)
  }

  //handshake signal
  val ar_hs = axi4.ar.valid && axi4.ar.ready
  val r_hs  = axi4.r.valid && axi4.r.ready

  val pc = RegInit(PC_START.U(XLEN.W))

  axi4.ar.bits.id    := 1.U
  axi4.ar.bits.len   := 0.U
  axi4.ar.bits.size  := 1.U(3.W)
  axi4.ar.bits.burst := AXI4Parameters.BURST_WRAP
  axi4.ar.bits.lock  := false.B
  axi4.ar.bits.cache := 0.U
  axi4.ar.bits.qos   := 0.U
  axi4.ar.bits.user  := 0.U
  axi4.ar.bits.addr := pc
  axi4.ar.bits.prot := 0.U
  axi4.ar.bits.region := 0.U
  when(ar_hs) {
    pc := Mux(io.in.valid, io.in.new_pc + 4.U, pc + 4.U) //io.in.isJump --->  io.in.valid
    setState(axi_r)
  }

  val instr: UInt = RegInit(0.U(XLEN.W))
  when(r_hs){
    instr       := axi4.r.bits.data
    setState(axi_ar)
  }
  axi4.r.ready  := isState(axi_r)
  axi4.ar.valid := isState(axi_ar)

  io.out.pc := pc
  io.out.instr := instr
  io.ifuaxi.w <> DontCare
  io.ifuaxi.aw <> DontCare
  io.ifuaxi.b <> DontCare
}