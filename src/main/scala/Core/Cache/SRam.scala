package Core.Cache

import chisel3._
import chisel3.util._
import chisel3.experimental._
import utils.{InBool, OutBool, InUInt, OutUInt}

trait SramConfig {
  def Bits = 128;
  def WordDepth = 64;
  def AddrWidth = 6;
  def WenWidth = 128;
}

// ref: https://github.com/OSCPU/ysyxSoC
class S011HD1P_X32Y2D128_BW extends ExtModule with HasExtModuleResource with SramConfig {
  val CLK = IO(Input(Clock()))
  val CEN = IO(Input(Bool()))
  val WEN = IO(Input(Bool()))
  val BWEN = IO(Input(UInt(WenWidth.W)))
  val A = IO(Input(UInt(AddrWidth.W)))
  val D = IO(Input(UInt(Bits.W)))
  val Q = IO(Output(UInt(Bits.W)))

  addResource("/vsrc/ysyxRam.v")
}



class SramIO extends Bundle with SramConfig {
  val rData: UInt = OutUInt(Bits)

  val en: Bool = InBool()
  val idx: UInt = InUInt(AddrWidth)
  val wen: Bool = InBool()
  val wMask: UInt = InUInt(WenWidth)
  val wData: UInt = InUInt(Bits)
}

class SRam extends Module {
  val io = IO(new SramIO)
  val sram: S011HD1P_X32Y2D128_BW = Module(new S011HD1P_X32Y2D128_BW)
  io.rData  := sram.Q
  sram.CLK  := clock
  sram.CEN  := !io.en
  sram.A    := io.idx
  sram.WEN  := !io.wen
  sram.BWEN := ~io.wMask
  sram.D    := io.wData
}