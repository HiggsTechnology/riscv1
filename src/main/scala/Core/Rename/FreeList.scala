package Core.Rename

import chisel3._
import chisel3.util._
import utils._

class FreeListPtr extends CircularQueuePtr[FreeListPtr](
  NRPhyRegs - 32
)

object FreeListPtr {
  def apply(f: Bool, v:UInt): FreeListPtr = {
    val ptr = Wire(new FreeListPtr)
    ptr.flag := f
    ptr.value := v
    ptr
  }
}

class FreeList extends Module with Config{
  val io = IO(new Bundle() {
    val flush = Input(Bool())

    val req = new Bundle {
      // need to alloc (not actually do the allocation)
      val allocReqs = Vec(2, Input(Bool()))
      // response pdest according to alloc
      val pdests = Vec(2, Output(UInt(PhyRegIdxWidth.W)))
      // alloc new phy regs// freelist can alloc
      val canAlloc = Output(Bool())
      // actually do the allocation
      val doAlloc = Input(Bool())
    }


    // dealloc phy regs
    val deallocReqs = Input(Vec(2, Bool()))
    val deallocPregs = Input(Vec(2, UInt(PhyRegIdxWidth.W)))
  })

  val FL_SIZE = NRPhyRegs - 32

  // init: [32, 127]
  val freeList = RegInit(VecInit(Seq.tabulate(FL_SIZE)(i => (i+32).U(PhyRegIdxWidth.W))))
  val headPtr = RegInit(FreeListPtr(false.B, 0.U))
  val tailPtr = RegInit(FreeListPtr(true.B, 0.U))

  // dealloc: commited instructions's 'old_pdest' enqueue
  for(i <- 0 until 2){
    val offset = if(i == 0) 0.U else PopCount(io.deallocReqs.take(i))
    val ptr = tailPtr + offset
    val idx = ptr.value
    when(io.deallocReqs(i)){
      freeList(idx) := io.deallocPregs(i)
    }
  }
  val tailPtrNext = tailPtr + PopCount(io.deallocReqs)
  tailPtr := tailPtrNext

  // allocate new pregs to rename instructions

  // number of free regs in freelist
  val freeRegs = Wire(UInt())
  // use RegNext for better timing
  io.req.canAlloc := RegNext(freeRegs >= 2.U)


  val allocatePtrs = (0 until 2).map(i => headPtr + i.U)
  val allocatePdests = VecInit(allocatePtrs.map(ptr => freeList(ptr.value)))

  for(i <- 0 until 2){
    io.req.pdests(i) := allocatePdests(/*if (i == 0) 0.U else */PopCount(io.req.allocReqs.take(i)))
  }
  val headPtrAllocate = headPtr + PopCount(io.req.allocReqs)
  val headPtrNext = Mux(io.req.canAlloc && io.req.doAlloc, headPtrAllocate, headPtr)
  freeRegs := distanceBetween(tailPtr, headPtrNext)

  headPtr := Mux(io.flush,FreeListPtr(!tailPtrNext.flag, tailPtrNext.value),headPtrNext)



}
