package Core.MemReg

import Core.AXI4.AXI4IO
import Core.Config.Config
import chisel3._
import chisel3.util._
import utils.Pc_Instr

trait CacheConfig{
  def TotalSize = 32
  def Ways = 4
  def LineSize = 64 // byte
  def Sets = TotalSize * 1024 / LineSize / Ways
  def OffsetBits = log2Up(LineSize)
  def IndexBits = log2Up(Sets)
  def TagBits = 32 - OffsetBits - IndexBits
  def addrBundle = new Bundle {
    val tag = UInt(TagBits.W)
    val index = UInt(IndexBits.W)
    val byteOffset = UInt(OffsetBits.W)
  }
}

class ifu2icacheIO extends Bundle with Config{
  val pc    = Output(UInt(XLEN.W))
  val instr = Input(UInt(INST_WIDTH))
}
class CacheIO extends Bundle with Config{
  val in  = Flipped(Decoupled(new ifu2icacheIO))
  val out = new AXI4IO
}
class ICache extends Module with  Config with CacheConfig {
  val io = IO(new CacheIO)
  val addr = io.in.bits.pc.asTypeOf(addrBundle)

  val tagArray = Mem(Sets, UInt(TagBits.W))
  val valids = RegInit(VecInit(Seq.fill(Sets)(false.B)))
  //  val valids = RegInit(0.U(Sets.W))
  val dataArray = Mem(Sets, UInt((LineSize * 8).W))
  val s_idle :: s_metaRead :: s_memReadReq :: s_memReadResp :: Nil = Enum(4)
  val state = RegInit(s_idle)

  // read metadata
  io.in.ready := (state === s_idle)
  val metaReadEnable = io.in.fire() && (state === s_idle)
  val addrReg = RegEnable(addr, metaReadEnable)
  val tagRead = RegEnable(tagArray.read(addr.index), metaReadEnable)
  val dataRead = RegEnable(dataArray.read(addr.index), metaReadEnable)
  // reading SeqMem has 1 cycle latency, there tag should be compared in the next cycle
  // and the address should be latched
//  val validRead = valids(addrReg.index)
  val hit = valids(addrReg.index) && (addrReg.tag === tagRead)

  // if miss, access memory,sent to mem
  io.out := DontCare
  io.out.ar.valid := state === s_memReadReq
  io.out.ar.bits.addr := addrReg.asUInt
  io.out.ar.bits.size := "b10".U //2 ways
  io.out.r.ready := state === s_memReadResp
  io.out.w.valid := false.B
  // refill
  val metaWriteEnable = (state === s_memReadResp) && io.out.r.fire() && !metaReadEnable
  when(metaWriteEnable) {
    tagArray.write(addrReg.index, addrReg.tag)
    valids(addrReg.index) := true.B
    dataArray.write(addrReg.index, io.out.r.bits.data)
  }

  // return data
  val retData = Mux(hit && (state === s_metaRead), dataRead, io.out.r.bits.data)
  io.in.bits.instr := retData //.asTypeOf(Vec(LineSize / 4, UInt(32.W)))(addrReg.wordIndex)
  io.in.valid := (hit && (state === s_metaRead)) || ((state === s_memReadResp && io.out.r.fire()))

  switch(state) {
    is(s_idle) {
      when(io.in.fire()) {
        state := s_metaRead
      }
    }

    is(s_metaRead) {
      state := Mux(hit, s_idle, s_memReadReq)
    }

    is(s_memReadReq) {
      when(io.out.ar.fire()) {
        state := s_memReadResp
      }
    }

    is(s_memReadResp) {
      when(io.out.r.fire()) {
        state := s_idle
      }
    }
  }
}

