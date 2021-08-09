/*
 * This code is a minimal hardware described in Chisel.
 * 
 * And Gate: the FPGA version of Hello World
 */

package oscpu

import chisel3._
import chisel3.util._

/**
 * The And Gate component.
 */

object Types {
  val addi :: subi :: Nil = Enum(2)
}

