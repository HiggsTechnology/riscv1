package Core.IFU

import Core.Config
import chisel3._
import chisel3.util._
import utils._

class btbupdate extends Bundle with Config{
  val br_pc = Output(UInt(VAddrBits.W))
  val targets = Output(UInt(VAddrBits.W))
  val br_type = Output(UInt(2.W))
}

class pred_update extends Bundle with Config{
  val taken = Output(Bool())
  val gshare_idx = Output(UInt(ghrBits.W))
  val pc_idx = Output(UInt(ghrBits.W))
  val gshare_mispred = Output(Bool())
  val pc_mispred = Output(Bool())
}

class preDecode extends Bundle with Config{
  val pc3 = Output(UInt(XLEN.W))
  val is_br = Output(Bool())
  val offset = Output(UInt(XLEN.W))//偏移量，+4.U或立即数或立即数+寄存器数
  val br_type = Output(BRtype())
  val is_ret = Output(Bool())
  val iscall = Output(Bool())
}

class BPUIO extends Bundle with Config{
  //input
  val ibf_ready = Input(Bool())
  //stage1
  val pc = Vec(2,Input(UInt(XLEN.W)))

  //stage3
  val predecode = Vec(2, Flipped(ValidIO(new preDecode)))

  val outfire = Vec(2,Input(Bool()))

  //output
  //stage2
  val br_taken = Vec(2,Output(Bool()))
  val jump_pc = Output(UInt(XLEN.W))  //btb & pht

  //stage3
  val br_taken3 = Vec(2,Output(Bool()))
  val jump_pc3 = Output(UInt(XLEN.W))  //btb & pht

  val gshare_idx = Vec(2,Output(UInt(ghrBits.W)))
  val gshare_pred = Vec(2,Output(Bool()))
  val pc_pred = Vec(2,Output(Bool()))
  val btbtarget = Vec(2,Output(UInt(XLEN.W)))

  //update
  val pred_update = Flipped(ValidIO(new pred_update))
  val ras_update = Input(new RASupdate)
  val btb_update = Flipped(ValidIO(new btbupdate))
  val flush = Input(Bool())
}

class BPU extends Module with Config{
  val io = IO(new BPUIO)

  val btb = Module(new BTB)
  val ras = Module(new RAS)


  //stage1
  //gshare
  val ghr = RegInit(0.U(ghrBits.W))//ghrBits=10, global history register

  val GPHT_Idx1 = Wire(Vec(FETCH_WIDTH, UInt(ghrBits.W)))//Global Pattern History Table,两位饱和计数器，taken+1，no taken -1
  val pc1 = Wire(Vec(FETCH_WIDTH, UInt(XLEN.W)))

  for(i <- 0 until FETCH_WIDTH) {
    GPHT_Idx1(i) := io.pc(i)(ghrBits + 1, 2) ^ ghr//非压缩指令，所有指令后两位都相同，所以只需判断2～11位
    pc1(i) := io.pc(i)
  }




//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////




  //stage2
  val GPHT_Idx2 = RegEnable(GPHT_Idx1,io.ibf_ready)
  val pc2 = RegEnable(pc1,io.ibf_ready)

  val pred_select = RegInit(0.U(3.W))//第2位为高则选GPHT

  val PHT = Mem(GPHT_Size, UInt(2.W))
  val PHT_taken = Wire(Vec(FETCH_WIDTH, Bool()))
  for(i <- 0 until FETCH_WIDTH){
    PHT_taken(i) := PHT.read(pc2(i)(ghrBits+1,2))(1)//PHT_Idx= pc2(i)(11,2)
  }

  val GPHT = Mem(GPHT_Size, UInt(2.W))//GPHT有异或操作，PHT无异或操作
  val GPHT_taken = Wire(Vec(FETCH_WIDTH, Bool()))
  for(i <- 0 until FETCH_WIDTH){
    GPHT_taken(i) := GPHT.read(GPHT_Idx2(i))(1)
  }

  val bimPred = Wire(Vec(FETCH_WIDTH, Bool()))

  btb.io.in.pc := pc2


  val br_taken2 = Wire(Vec(FETCH_WIDTH, Bool()))
  for(i <- 0 until FETCH_WIDTH){
    val notB_btb        = btb.io.resp.br_type(i) =/= BTBtype.B   //不是B指令
    val isB_GPHTget_btb = btb.io.resp.br_type(i) === BTBtype.B && bimPred(i)  //是B指令且GPHT预测此条B指令跳转
    bimPred(i)   := Mux(pred_select(2), GPHT_taken(i), PHT_taken(i))  //刚开做了GPHT，后做了PHT，后为了比较性能，比较发现没有差异，固定为GPHT//直接定义用GPHT来看
    br_taken2(i) := btb.io.resp.hits(i) && (notB_btb || isB_GPHTget_btb)  //要么是绝对转跳，要么是分支转跳，但是预测跳转
  }
  io.jump_pc  := Mux(br_taken2(0), btb.io.resp.targets(0), btb.io.resp.targets(1))   //从BTB取到跳转地址
  io.br_taken := br_taken2               //根据BTB和GHPT预测是否跳转

  val btb_hit2     = Wire(Vec(FETCH_WIDTH, Bool()))
  val btbtarget2   = Wire(Vec(FETCH_WIDTH, UInt(VAddrBits.W)))
  val br_type2     = Wire(Vec(FETCH_WIDTH, UInt(2.W)))
  for(i <- 0 until FETCH_WIDTH){                         //将btb的信号传到第三拍
    btb_hit2(i)   := btb.io.resp.hits(i)
    btbtarget2(i) := btb.io.resp.targets(i)
    br_type2(i)   := btb.io.resp.br_type(i)
  }



  //////////////////////////////////////////////////////////////////////////////////////////////////////////
  //stage3
  val GPHT_taken3          =   RegEnable(GPHT_taken,io.ibf_ready)
  val PHT_taken3           =   RegEnable(PHT_taken,io.ibf_ready)
  val GPHT_Idx3            =   RegEnable(GPHT_Idx2,io.ibf_ready)
  val pc3                  =   RegEnable(pc2,io.ibf_ready) //跳转指令的pc
  val bimPred3             =   RegEnable(bimPred,io.ibf_ready)
  val br_taken3            =   RegEnable(br_taken2,io.ibf_ready)
  val jump3                =   RegEnable(io.jump_pc,io.ibf_ready)
  val btb_hit3             =   RegEnable(btb_hit2,io.ibf_ready)
  val btbtarget3           =   RegEnable(btbtarget2,io.ibf_ready)   //跳转到的pc
  val br_type3             =   RegEnable(br_type2,io.ibf_ready)
  //根据predecode，以及stage2的GPHT、PHT，计算分支预测结果
  val br_taken_predecode   =   Wire(Vec(FETCH_WIDTH, Bool()))
  val is_call              =   Wire(Vec(FETCH_WIDTH, Bool()))
  for(i <- 0 until FETCH_WIDTH){
    val jal_iscall         =   io.predecode(i).bits.br_type === BRtype.J && io.predecode(i).bits.iscall
    val jalr_iscall        =   io.predecode(i).bits.br_type === BRtype.R && io.predecode(i).bits.iscall
    val is_br_pre          =   io.predecode(i).bits.is_br
    val notB_pre           =   io.predecode(i).bits.br_type =/= BRtype.B
    val isB_GPHTget_pre    =   io.predecode(i).bits.br_type === BRtype.B && bimPred3(i)

    is_call(i)            :=   jal_iscall || jalr_iscall      //jal或者jalr为call指令时
    br_taken_predecode(i) :=   is_br_pre && (notB_pre || isB_GPHTget_pre)  //从预译码判断是否跳转，比BTB更加靠谱
  }

  //传入RAS
  val call_br_0       =    io.outfire(0) && io.predecode(0).bits.is_br && is_call(0)
  val call_br_1       =    io.outfire(1) && io.predecode(1).bits.is_br && is_call(1)
  val ret_br_0        =    io.outfire(0) && io.predecode(0).bits.is_br && io.predecode(0).bits.is_ret
  val ret_br_1        =    io.outfire(1) && io.predecode(1).bits.is_br && io.predecode(1).bits.is_ret
  val pc_br_0         =    io.predecode(0).bits.pc3
  val pc_br_1         =    io.predecode(1).bits.pc3
  ras.io.push.iscall :=    Mux(br_taken_predecode(0), call_br_0, call_br_1)
  ras.io.push.target :=    Mux(is_call(0), pc_br_0, pc_br_1) + 4.U
  ras.io.is_ret      :=    Mux(br_taken_predecode(0), ret_br_0, ret_br_1)
  ras.io.update      :=    io.ras_update
  ras.io.flush       :=    io.flush

  //输出predecode & ras的转跳信息,供IFU检查stage2的正确性，以确定是否输入IBF
  //printf("predecode pc %x %x, offset %x %x\n",io.predecode(0).bits.pc3,io.predecode(1).bits.pc3,io.predecode(0).bits.offset,io.predecode(1).bits.offset)
  val target3 = Wire(Vec(FETCH_WIDTH, UInt(VAddrBits.W)))
  
  for(i <- 0 until FETCH_WIDTH){
    val is_ret_pre    = io.predecode(i).bits.is_ret     //是否为ret指令
    val jalr_hit_pre  = io.predecode(i).bits.br_type === BRtype.R && btb_hit3(i)  //第一次遇到给一个pc+4的值，bru会mispredict更新btb。第二次遇到btb正确的值
    val pc_offset_pre = io.predecode(i).bits.pc3 + io.predecode(i).bits.offset    //预译码送过来的PC+IMM
    target3(i) := Mux(is_ret_pre, ras.io.target, Mux(jalr_hit_pre, btbtarget3(i), pc_offset_pre))
  }//return优先级最高，然后R型去检测btb，未命中则添加偏移量
  io.jump_pc3 := Mux(br_taken_predecode(0), target3(0), target3(1))
  io.br_taken3 := br_taken_predecode

  io.gshare_idx := GPHT_Idx3
  io.gshare_pred := GPHT_taken3
  io.pc_pred := PHT_taken3
  for(i <- 0 until FETCH_WIDTH){
    io.btbtarget(i) := Mux(btb_hit3(i), btbtarget3(i), io.predecode(i).bits.pc3 + io.predecode(i).bits.offset)
  }
  //btb update
  val pre_br_type = Wire(Vec(2,UInt(2.W)))
  for(i <- 0 until FETCH_WIDTH){
    pre_br_type(i) := Mux(io.predecode(i).bits.is_ret, "b10".U, io.predecode(i).bits.br_type)//历史遗留问题，第三阶段里is_ret用"b10".U标记
  }

  val btb_needUpdate      = Wire(Vec(2,Bool()))//btb记录了pc、pc对应的跳转类型、对应的跳转目标地址
  //val btb_needUpdate_part = Wire(Vec(2,Bool()))//btb_needUpdate太长拆成两行  后端的执行单元发现指令跳转目标错误或者方向错误时会刷新流水线, 同时更新BTB相应的表项.
  for(i <- 0 until FETCH_WIDTH) {
    val br_type          =   io.predecode(i).bits.is_br && (io.predecode(i).bits.br_type =/= BRtype.R || io.predecode(i).bits.is_ret)
    val type_mis         =   br_type3(i) =/= pre_br_type(i)
    val target_mis       =   btbtarget3(i) =/= Mux(io.predecode(i).bits.is_ret, ras.io.target, io.predecode(i).bits.pc3 + io.predecode(i).bits.offset)//跳转的类型相同，地址相同，并且命中
    btb_needUpdate(i)    :=  br_type && (!btb_hit3(i) || type_mis || target_mis)//返回的
    //btb_needUpdate_part(i)   := br_type && (!btb_hit3(i) || type_mis || btbtarget3(i) =/= io.predecode(i).bits.pc3 + io.predecode(i).bits.offset) //跳转的类型相同，地址相同，并且命中
    //btb_needUpdate(i)        := (io.predecode(i).bits.is_ret && (!btb_hit3(i) || br_type3(i) =/= pre_br_type(i) || btbtarget3(i) =/= ras.io.target)) || btb_needUpdate_part(i)    //返回的
  }

  for(i <- 0 until FETCH_WIDTH){
    btb.io.update.br_type(i)    :=  pre_br_type(i)
    btb.io.update.needUpdate(i) :=  btb_needUpdate(i)
    btb.io.update.targets(i)    :=  Mux(io.predecode(i).bits.is_ret, ras.io.target, io.predecode(i).bits.pc3 + io.predecode(i).bits.offset)
    btb.io.update.br_pc(i)      :=  io.predecode(i).bits.pc3
  }
  btb.io.update.br_type(2)      :=  io.btb_update.bits.br_type
  btb.io.update.needUpdate(2)   :=  io.btb_update.valid
  btb.io.update.targets(2)      :=  io.btb_update.bits.targets
  btb.io.update.br_pc(2)        :=  io.btb_update.bits.br_pc

  // printf("btb1 hit %d, target %x, type %x\n",btb_hit3(0),btbtarget3(0),br_type3(0))
  // printf("btb2 hit %d, target %x, type %x\n",btb_hit3(1),btbtarget3(1),br_type3(1))
  // printf("btb update %d %d\n",btb_needUpdate(0),btb_needUpdate(1))


  //GPHT, PHT, RAS update
  when(io.pred_update.valid){
    val idx = io.pred_update.bits.gshare_idx
    val taken = io.pred_update.bits.taken
    val cnt = GPHT(idx)
    val newCnt = Mux(taken, cnt + 1.U, cnt - 1.U)
    val wen = (taken && (cnt =/= "b11".U)) || (!taken && (cnt =/= "b00".U))
    when (wen) {
      GPHT(idx) := newCnt
    }

    ghr := Cat(ghr(ghrBits-2,0), taken)

    val pc_idx = io.pred_update.bits.pc_idx
    val pc_cnt = PHT(pc_idx)
    val pc_newCnt = Mux(taken, pc_cnt + 1.U, pc_cnt - 1.U)
    val pc_wen = (taken && (pc_cnt =/= "b11".U)) || (!taken && (pc_cnt =/= "b00".U))
    when (pc_wen) {
      PHT(pc_idx) := pc_newCnt
    }

    when(io.pred_update.bits.pc_mispred && !io.pred_update.bits.gshare_mispred && pred_select =/= "b111".U){
      pred_select := pred_select + 1.U
    }.elsewhen(!io.pred_update.bits.pc_mispred && io.pred_update.bits.gshare_mispred && pred_select =/= "b000".U){
      pred_select := pred_select - 1.U
    }
  }

}