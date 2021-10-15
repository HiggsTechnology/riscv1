package Core.Cache

import chisel3._
import chisel3.util._
import chisel3.experimental._
import utils.{InBool, OutBool, InUInt, OutUInt}

trait SyncRamConfig {
  def Bits = 128;
  def WordDepth = 64;
  def AddrWidth = 6;
  def WenWidth = 128;
}



class SyncRamIO extends Bundle with SyncRamConfig {
  val rData: UInt = OutUInt(Bits)

  val en: Bool = InBool()
  val idx: UInt = InUInt(AddrWidth)
  val wen: Bool = InBool()
  val wMask: UInt = InUInt(WenWidth)
  val wData: UInt = InUInt(Bits)
}

class SyncRam extends Module with SyncRamConfig {
  val io = IO(new SramIO)
  val array = SyncReadMem(WordDepth, UInt(Bits.W))

  val readEn = io.en && !io.wen
  val writeEn = io.en && io.wen

  when (writeEn) { array.write(io.idx, io.wData) }

  io.rData := array.read(io.idx, readEn)

}