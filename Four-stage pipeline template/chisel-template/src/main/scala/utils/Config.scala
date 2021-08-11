package utils

import chisel3._
import chisel3.util._
import utils._
trait Config{
  val XLEN       = 64
  val INST_WIDTH = 32
  val pc_start   =  0x80000000L
}