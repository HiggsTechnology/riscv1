package Core.CtrlBlock.Rename

import Core.Config
import Core.Config.NRPhyRegs
import chisel3._
import chisel3.util._
import utils._
//定义FreeList指针 继承CircularQueuePtr，深度为NRPhyRegs-32
class FreeListPtr extends CircularQueuePtr[FreeListPtr](NRPhyRegs-32) with Config {
}
object FreeListPtr {
  def apply(f: Bool, v:UInt): FreeListPtr = {
    val ptr = Wire(new FreeListPtr)
    ptr.flag := f
    ptr.value := v
    ptr
  }
}
class FreeListIO extends Bundle with Config{
  val flush = Input(Bool())
  val req = new Bundle {
    // need to alloc (not actually do the allocation)
    val allocReqs = Vec(2, Input(Bool()))
    // response pdest according to alloc
    val pdests    = Vec(2, Output(UInt(PhyRegIdxWidth.W)))
    // alloc new phy regs// freelist can alloc
    val canAlloc  = Output(Bool())
    // actually do the allocation
    val doAlloc   = Input(Bool())
  }
  // dealloc phy regs
  val deallocReqs =  Input(Vec(2, Bool()))
  val deallocPregs = Input(Vec(2, UInt(PhyRegIdxWidth.W)))
}
class FreeList extends Module with Config with HasCircularQueuePtrHelper {
  val io = IO(new FreeListIO)
  val FL_SIZE = NRPhyRegs - 32
  // 初始话freeList 初始值为32~127
  val freeList = RegInit(VecInit(Seq.tabulate(FL_SIZE)(i => (i+32).U(PhyRegIdxWidth.W))))
  // 定义出入队列指针
  val headPtr = RegInit(FreeListPtr(false.B, 0.U))
  val tailPtr = RegInit(FreeListPtr(true.B, 0.U))
  // dealloc: 入队操作
  // 从后端接收到指令的old_pdest，进入freeList，释放出旧寄存器地址
  // 释放出旧寄存器地址以freeList的视角来看是入队，freeList接收的越多代表空闲的越多
  // 通过采用当前指针+offset偏移的方法，确定释放出旧寄存器地址在freeList中的存储位置
  for(i <- 0 until 2){
    val offset = if(i == 0) 0.U else PopCount(io.deallocReqs.take(i))
    val ptr = tailPtr + offset
    val idx = ptr.value
    when(io.deallocReqs(i)){
      freeList(idx) := io.deallocPregs(i)
    }
  }
  // 接收old_pdest后，尾指针前移且更新尾指针位置
  val tailPtrNext = tailPtr + PopCount(io.deallocReqs)
  tailPtr := tailPtrNext

  // allocate: 出队操作
  // 由于双发射，当前头指针+1 & +2位置为两指令分配的指针位置
  // 取出当前指针+1 & +2 位置以及存储的寄存器编号
  val allocatePtrs = (0 until 2).map(i => headPtr + i.U)
  val allocatePdests = VecInit(allocatePtrs.map(ptr => freeList(ptr.value)))
  // 在allocReqs分配请求的情况下，pdest得到新寄存器编号
  for(i <- 0 until 2){
    io.req.pdests(i) := allocatePdests(/*if (i == 0) 0.U else */PopCount(io.req.allocReqs.take(i)))
  }
  // 在canAlloc---队列未满的条件 以及 doAlloc---请求分配均有效的情况下，头指针前移
  val headPtrAllocate = headPtr + PopCount(io.req.allocReqs)+
  val freeRegs = Wire(UInt())
  freeRegs := distanceBetween(tailPtr, headPtrNext)
  io.req.canAlloc := RegNext(freeRegs >= 2.U)
  val headPtrNext = Mux(io.req.canAlloc && io.req.doAlloc, headPtrAllocate, headPtr)
  // 非冲刷则输出
  headPtr := Mux(io.flush,FreeListPtr(!tailPtrNext.flag, tailPtrNext.value),headPtrNext)
}
