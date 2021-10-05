package Core.IFU

import chisel3._
import chisel3.util._
import Core.Config

class RASEntry extends Bundle with Config{
  val retAddr = UInt(VAddrBits.W)
  val ctr = UInt(10.W) // layer of nested call functions
}

object RASEntry {
  def apply(retAddr: UInt, ctr: UInt): RASEntry = {
    val e = Wire(new RASEntry)
    e.retAddr := retAddr
    e.ctr := ctr
    e
  }
}

class RASupdate extends Bundle with Config {
  val target = UInt(VAddrBits.W)
  val iscall = Bool()
  val is_ret = Bool()
}

class RAS extends Module with Config {
  class RASPush extends Bundle{
    val target = UInt(VAddrBits.W)
    val iscall = Bool()
  }
  val io = IO(new Bundle {
    val is_ret = Input(Bool())
    val target = Output(UInt(VAddrBits.W))//输出目标指令

    val push = Input(new RASPush)

    val update = Input(new RASupdate)
    val flush  = Input(Bool())
    val ras_flush  = Input(Bool())
  })


  
  val stack = Mem(RasSize, new RASEntry)
  val sp = RegInit(0.U(log2Up(RasSize).W))

  val top = stack.read(sp)
  io.target := top.retAddr

  when(io.push.iscall){
    when(top.retAddr===io.push.target){
      stack.write(sp, RASEntry(top.retAddr, top.ctr + 1.U))
    }.otherwise{
      stack.write(sp + 1.U, RASEntry(io.push.target, 1.U))
      sp := sp + 1.U
    }
  }

  when(io.is_ret){
    when(top.ctr===1.U){
      sp := sp - 1.U
    }.otherwise{
      stack.write(sp, RASEntry(top.retAddr, top.ctr - 1.U))
    }
  }

  val stack_commit = Mem(RasSize, new RASEntry)//新建第二个表
  val sp_commit = RegInit(0.U(log2Up(RasSize).W))
  val top_commit = stack_commit.read(sp_commit)

  when(io.update.iscall){
    when(top_commit.retAddr===io.update.target){
      stack_commit.write(sp_commit, RASEntry(top_commit.retAddr, top_commit.ctr + 1.U))
    }.otherwise{
      stack_commit.write(sp_commit + 1.U, RASEntry(io.update.target, 1.U))//指针+1，计数层置为1
      sp_commit := sp_commit + 1.U
    }
  }

  when(io.update.is_ret){
    when(top_commit.ctr===1.U){
      sp_commit := sp_commit - 1.U//已调用完毕，指针回退
    }.otherwise{
      stack_commit.write(sp_commit, RASEntry(top_commit.retAddr, top_commit.ctr - 1.U))//不是第一层，地址写入commit表，计数层减1
    }
  }

  when(io.flush){
    for(i <- 0 until RasSize){
      stack(i) := stack_commit(i)//冲刷时，stack根据commit更新
    }
    sp := sp_commit

    when(io.update.iscall){
      when(top_commit.retAddr===io.update.target){
        stack(sp_commit).ctr := stack_commit(sp_commit).ctr + 1.U
      }.otherwise{
        sp := sp_commit + 1.U
        stack(sp_commit + 1.U).retAddr := io.update.target
        stack(sp_commit + 1.U).ctr := 1.U
      }
    }
  }

  when(io.ras_flush){
    sp := 0.U
    sp_commit := 0.U
    for(i <- 0 until RasSize){
      stack_commit(i).retAddr := 0.U
      stack_commit(i).ctr := 0.U
      stack(i).retAddr := 0.U
      stack(i).ctr := 0.U
    }
  }
  // when(io.printf){
  // printf("RAS    sp %d,     io.call %d,     io.ret %d\n",sp, io.push.iscall, io.is_ret)
  // for(i <- 0 until RasSize){
  //   printf("RAS %d: target %x, cnt %d\n", i.U, stack(i).retAddr, stack(i).ctr)

  // }
  // printf("sp_commit %d, commit_call %d, commit_ret %d\n",sp_commit,io.update.iscall,io.update.is_ret)
  // for(i <- 0 until RasSize){
  //   printf("RAS commit %d: target %x, cnt %d\n", i.U, stack_commit(i).retAddr, stack_commit(i).ctr)
  // }
  // }

}
