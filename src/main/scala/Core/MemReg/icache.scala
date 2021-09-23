package Core.MemReg

import Core.AXI4.AXI4Parameters.dataBits
import Core.AXI4.{AXI4IO, AXIParameter}
import Core.Config.Config
import chisel3._
import chisel3.util._


sealed trait CacheConfig extends AXIParameter{
  def TotalSize = 32 //Kb
  def Ways = 4
  def LineSize = 64 // byte
  def Sets = TotalSize * 1024 / LineSize / Ways
  def OffsetBits = log2Up(LineSize) //对应的是字节标号
  def IndexBits = log2Up(Sets)
  def TagBits = 32 - OffsetBits - IndexBits
  def CacheDataBits = LineSize*8
  def retTimes = CacheDataBits/dataBits
  def addrBundle = new Bundle {
    val tag        = UInt(TagBits.W)
    val index      = UInt(IndexBits.W)
    val byteOffset = UInt(OffsetBits.W)
  }
}

sealed class MetaBundle extends Bundle with CacheConfig {
  val tag    = UInt(TagBits.W)
  val valid  = RegInit(false.B)
}

sealed class DataBundle extends Bundle with Config{
  val data = UInt((XLEN*8).W)//通过offset偏移移位一次读取8字节
}

class ifu2icacheIO extends Bundle with Config{
  val pc    = Output(UInt(XLEN.W))
  val instr = Input(UInt(INST_WIDTH))
}

class CacheIO extends Bundle with Config{
  val in  = Flipped(Decoupled(new ifu2icacheIO))
  val out = new AXI4IO
}

sealed class CacheLineNode(Number:Int){
  val no = Number                //cacheLine编号
  var next: CacheLineNode = null //指向后一个结点
  var pre: CacheLineNode = null  //指向前一个结点
}



sealed class DoubleLinkedList extends Bundle with CacheConfig {
  var map:Map[Int,CacheLineNode]=Map()
  var head = new CacheLineNode(-1)
  var tail = new CacheLineNode(-2)
  head.next = tail
  tail.pre = head
  map+=(-1->head)
  map+=(-2->tail)
  //初始化链表操作
  for(i <- 0 until Ways){
    var lineNode = new CacheLineNode(i)
    addToHead(lineNode)
    map+=(i->lineNode)
  }

  private def addToHead(node:CacheLineNode){
    node.next=head.next
    node.pre=head
    head.next.pre=node
    head.next=node
  }

  private def removeNode(node:CacheLineNode){
    node.pre.next=node.next
    node.next.pre=node.pre
    return
  }

  def visitCacheLine(Number:Int){
    var node = this.map.apply(Number)
    removeNode(node)
    addToHead(node)
  }
}



//ifu发送PC，接收inst， 连接至 icache
//icache通过AXI4与mem交互
class ICache extends Module with Config with CacheConfig with AXIParameter{
  val io = IO(new CacheIO)
  //将addr即PC以tag,idx,offset顺序拆解
  val addr = io.in.bits.pc.asTypeOf(addrBundle) //输入PC信号拆分为addr
  //以mem类型定义meta，data共计Sets个
  val metaArray = Mem(Sets,Vec(Ways,new MetaBundle))
  val dataArray = Mem(Sets,Vec(Ways,new DataBundle))
  val s_idle :: s_metaRead :: s_memReadReq :: s_memReadResp :: Nil = Enum(4)
  val state = RegInit(s_idle)
  //  val lrutab = RegInit(Vec(Ways,0.U(10.W)))
  //  lrutab.indexOf(lrutab.min)
  //s_idle即cache就绪，接收IFU的读取请求，以metaRead方式拿到meta
  io.in.ready := (state === s_idle)
  val metaReadEnable = io.in.fire() && (state === s_idle)
  val addrReg = RegEnable(addr, metaReadEnable) //存入addrReg
  var line = 0
  val hit = false.B
  // reading SeqMem has 1 cycle latency, there tag should be compared in the next cycle
  // and the address should be latched
  //进入读遍历，在idx指定set下，遍历Ways，对比tag判断是否hit，并将ways标记为line
  for( i <- 0 to Ways){
    val validRead = metaArray.read(addrReg.index)(i).valid
    val tagRead   = metaArray.read(addrReg
      .index)(i).tag
    when (validRead && (tagRead === addrReg.tag) === true.B) {
      hit   := true.B
      line  = i
    }
  }

  //若hit，选中第i路
  val dataRead = (RegEnable(dataArray.read(addrReg.index)(line), metaReadEnable))
  val dataout = dataRead.asUInt >> (addrReg.byteOffset*8.U)
  //  lrutab(line) := lrttab(line) + 1.U

  //进入memReadReq态即代表miss，通过axi4请求mem读取
  io.out := DontCare
  io.out.ar.valid := state === s_memReadReq
  io.out.ar.bits.addr := addrReg.asUInt // PC地址进行burst读取
  io.out.ar.bits.size := LineSize.U // TODO 存疑：是否LineSize?
  io.out.r.ready := state === s_memReadResp
  io.out.w := DontCare
  io.out.aw:= DontCare
  io.out.b := DontCare

  //refill, take random substitution strategy
  //val victimWay = if (Ways > 1) (1.U << LFSR64()(log2Up(Ways)-1,0)) else "b0".U

  //采用LRU算法选择剔除块
  val cacheListVec=Vec(Sets,new DoubleLinkedList)
  when(hit === true.B){
    cacheListVec(addrReg.index).visitCacheLine(line)
  }
  val victimWay = if(Ways > 1) cacheListVec(addrReg.index).tail.pre.no else 0


  val metaWriteEnable = (state === s_memReadResp) && io.out.r.fire() && !metaReadEnable
  //TODO 以offset 进行取回data
  when(metaWriteEnable&&io.out.r.bits.last) {
    val metaArray = Mem(Sets,Vec(Ways,new MetaBundle))
    metaArray(addrReg.index)(victimWay).tag   := addrReg.tag //TODO mem.write使用
    metaArray(addrReg.index)(victimWay).valid := true.B
    dataArray(addrReg.index)(victimWay)       := memData.asUInt
  }

  //返回数据
  val memData = RegInit(Vec(retTimes,0.U(DataBits.W))) //retTimes = CacheDataBits/DataBits (axiDataBits)
  when(!io.out.r.bits.last && io.out.r.fire){
    memData(0) := io.out.r.bits.data
    for (i <- 1 until retTimes){
      memData(i) := memData(i-1)
    }
  }.otherwise{
    for(i<- 0 until retTimes){
      memData(i) := 0.U
    }
  }
  val chooseRet = RegInit(0.U(XLEN.W))
  chooseRet := memData.asUInt >> addrReg.byteOffset*8.U//TODO asUInt 拼接的正确性？
  val memRetData = RegEnable(chooseRet,io.out.r.bits.last)
  val retData = Mux(hit && (state === s_metaRead), dataRead, memRetData)
  io.in.bits.instr := retData //.asTypeOf(Vec(LineSize / 4, UInt(32.W)))(addrReg.wordIndex)
  io.in.valid := (hit && (state === s_metaRead)) || (state === s_memReadResp && io.out.r.fire())

  //-------------------------------------状态机------------------------------------------------
  switch(state) {
    is(s_idle) {
      when(io.in.fire()) {
        state := s_metaRead
      }
    }
    is(s_metaRead) {
      state := Mux(hit, s_idle, s_memReadReq)//若hit即完成本次读取回归idle，否则需要icache从mem读取并且refill icache
    }
    //对mem通过axi4.ar 发送读请求
    is(s_memReadReq) {
      when(io.out.ar.fire()) {
        state := s_memReadResp
      }
    }
    //进入mem回应态，通过axi4.r 返回数据
    is(s_memReadResp) {
      when(io.out.r.fire()&&io.out.r.bits.last) {
        state := s_idle
      }
    }
  }
}

