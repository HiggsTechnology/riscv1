package Core.IFU


import chisel3._
import chisel3.util._
import Core.Config
import Core.Config.{FETCH_WIDTH, VAddrBits}
import utils.TableAddr


//object BRtype {
//  def B = "b00".U  // branch
//  def R = "b01".U  // jalr
//  def N = "b10".U  // err instr
//  def J = "b11".U  // jal
//
//  def apply() = UInt(2.W)
//}
object BTBtype {
  def B = "b00".U  // branch
  def R = "b01".U  // indirect  //jalr
  def ret = "b10".U  // return
  def J = "b11".U  // jump        //jal

  def apply() = UInt(2.W)
}

class BTBResp extends Bundle with Config {
  val targets = Vec(FETCH_WIDTH, UInt(VAddrBits.W))
  val hits = Vec(FETCH_WIDTH, Bool())
  val br_type = Vec(FETCH_WIDTH, UInt(2.W))
}

class BTBUpdate extends Bundle {
  val br_pc = Vec(FETCH_WIDTH + 1, UInt(VAddrBits.W))
  val targets = Vec(FETCH_WIDTH + 1, UInt(VAddrBits.W))
  val needUpdate = Vec(FETCH_WIDTH + 1, Bool())
  val br_type = Vec(FETCH_WIDTH + 1, UInt(2.W))
}

class BTB extends Module with Config {

  val io = IO(new Bundle {
    val in = new Bundle {
      val pc = Vec(FETCH_WIDTH, Input(UInt(VAddrBits.W)))
    }
    val resp = Output(new BTBResp)

    val update = Input(new BTBUpdate)
  })

  val btbAddr = new TableAddr(log2Up(btbRows))//btbRows=256/4=2^6

  class btbEntry extends Bundle with Config {
    val tag = UInt(btbAddr.tagBits.W)
    val _type = UInt(2.W)//B,R,ret,J
    val target = UInt(VAddrBits.W)
  }

  val btb_valid = Seq.fill(4)(RegInit(VecInit(Seq.fill(btbRows)(false.B))))
  val btb = Seq.fill(4)(Mem(btbRows, new btbEntry))
  //TODO: read simultaneously
  for (j <- 0 until FETCH_WIDTH) {
    val btbRead = Wire(Vec(BtbWays, new btbEntry))
    for (i <- 0 until BtbWays) {
      btbRead(i) := btb(i)(btbAddr.getIdx(io.in.pc(j)))
    }

    val btbHit = Wire(Vec(BtbWays, Bool()))
    for (i <- 0 until BtbWays) {
      btbHit(i) := btbRead(i).tag === btbAddr.getTag(io.in.pc(j)) && btb_valid(i)(btbAddr.getIdx(io.in.pc(j)))
    }

    val brInfo = PriorityMux(btbHit, btbRead)
    io.resp.targets(j) := brInfo.target
    io.resp.hits(j) := btbHit.asUInt.orR
    io.resp.br_type(j) := brInfo._type

    // when(io.in.pc(j)===0x80000010L.U){
    //   printf("pc:80000010, tag %x, hit %d %d %d %d\n",btbAddr.getTag(io.in.pc(j)),btbHit(0),btbHit(1),btbHit(2),btbHit(3))
    // }
  }

  //update btb
  val writeWay = RegInit(VecInit(Seq.fill(btbRows)(0.U(log2Up(BtbWays).W))))

  val updateIdx = Wire(Vec(FETCH_WIDTH + 1, UInt(log2Up(btbRows).W)))
  val selectWay = Wire(Vec(FETCH_WIDTH + 1, UInt(log2Up(BtbWays).W)))

  for (i <- 0 until FETCH_WIDTH + 1) {
    updateIdx(i) := btbAddr.getIdx(io.update.br_pc(i))
    //    val btbHit_valid = Bool()
    val btbHit = Wire(Vec(BtbWays, Bool()))
    for (j <- 0 until BtbWays) {
      btbHit(j) := btbAddr.getTag(io.update.br_pc(i)) === btb(j)(updateIdx(i)).tag && btb_valid(j)(updateIdx(i))
    }
    val hitway = PriorityEncoder(btbHit)
    for (j <- 0 until BtbWays) {
      when(btbHit.asUInt.orR && hitway === j.U && io.update.needUpdate(i)){
        btb_valid(j)(updateIdx(i)) := false.B
      }
    }
  }


  selectWay(0) := writeWay(updateIdx(0))
  selectWay(1) := writeWay(updateIdx(1)) + (updateIdx(0) === updateIdx(1) && io.update.needUpdate(0))
  selectWay(2) := writeWay(updateIdx(2)) + (updateIdx(0) === updateIdx(2) && io.update.needUpdate(0)) + (updateIdx(1) === updateIdx(2) && io.update.needUpdate(1))

  for (i <- 0 until FETCH_WIDTH + 1) {
    val btbWrite = Wire(new btbEntry)
    btbWrite.tag := btbAddr.getTag(io.update.br_pc(i))
    btbWrite.target := io.update.targets(i)
    btbWrite._type := io.update.br_type(i)

    for (j <- 0 until BtbWays) {
      when(selectWay(i) === j.U && io.update.needUpdate(i)) {
        //printf("btb update%d, way %d, pc %x, idx %d, tag %x, target %x, type %d\n",i.U,j.U,io.update.br_pc(i),updateIdx(i),btbWrite.tag,io.update.targets(i),btbWrite._type)
        btb(j)(updateIdx(i)) := btbWrite
        btb_valid(j)(updateIdx(i)) := true.B
      }
    }
  }

  when(io.update.needUpdate(2)) {
    writeWay(updateIdx(2)) := selectWay(2) + 1.U
  }
  when(io.update.needUpdate(1) && (updateIdx(1) =/= updateIdx(2) || !io.update.needUpdate(2))) {
    writeWay(updateIdx(1)) := selectWay(1) + 1.U
  }
  when(io.update.needUpdate(0) && (updateIdx(0) =/= updateIdx(1) || !io.update.needUpdate(1)) && (updateIdx(0) =/= updateIdx(2) || !io.update.needUpdate(2))) {
    writeWay(updateIdx(0)) := selectWay(0) + 1.U
  }

  // for(i <- 0 until btbRows){
  //   if(i == 4){
  //   for(j <- 0 until BtbWays){
  //     printf("BTB row %d, way %d, valid %d, tag %x, type %x, target %x\n",i.U, j.U, btb_valid(j)(i), btb(j)(i).tag, btb(j)(i)._type, btb(j)(i).target)
  //   }
  //   printf("\n")
  //   }
  // }
}


