package Core.Define

import Core.Config.Config.MXLEN
import chisel3.{Bool, UInt, fromStringToLiteral}

object Interrupts {
  private def interruptField : BigInt = BigInt(1) << (MXLEN - 1)

  def UserSoft : BigInt = 0 | interruptField

  def SupervisorSoft : BigInt = 1 | interruptField

  def MachineSoft : BigInt = 3 | interruptField

  def UserTime : BigInt = 4 | interruptField

  def SupervisorTime : BigInt = 5 | interruptField

  def MachineTime : BigInt = 7 | interruptField

  def UserExtern : BigInt = 8 | interruptField

  def SupervisorExtern : BigInt = 9 | interruptField

  def MachineExtern : BigInt = 11 | interruptField

  def isUser(cause : UInt) : Bool = cause(1, 0) === "b00".U

  def isSupervisor(cause : UInt) : Bool = cause(1, 0) === "b01".U

  def isMachine(cause : UInt) : Bool = cause(1, 0) === "b11".U

  def isSoft(cause : UInt) : Bool = cause(3, 2) === "b00".U

  def isTime(cause : UInt) : Bool = cause(3, 2) === "b01".U

  def isExtern(cause : UInt) : Bool = cause(3, 2) === "b10".U
}
