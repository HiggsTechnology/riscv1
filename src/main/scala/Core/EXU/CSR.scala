package Core.EXU

import Core.Config.Config
import Core.Define.{Exception, Interrupts}
import Core.Difftest.DifftestTrapIO
import Core.IDU.FuncOpType
import Devices.Clint.ClintOutPort
import chisel3._
import chisel3.internal.firrtl.Width
import Privilege.{supportSupervisor, supportUser}
import chisel3.util.experimental.BoringUtils.addSource
import chisel3.util.{Cat, Enum, Fill, MuxLookup, PriorityEncoder, Valid, is, log2Ceil, switch}
import difftest.{DifftestArchEvent, DifftestCSRState, DifftestTrapEvent}
import utils.{BRU_OUTIO, CfCtrl, InstInc}

/**
 * CSR 操作码
 */
object CsrOpType {
  def RW    : UInt = "b00001".U(FuncOpType.width)
  def RS    : UInt = "b00010".U(FuncOpType.width)
  def RC    : UInt = "b00011".U(FuncOpType.width)
  def RWI   : UInt = "b00101".U(FuncOpType.width)
  def RSI   : UInt = "b00110".U(FuncOpType.width)
  def RCI   : UInt = "b00111".U(FuncOpType.width)
  def ECALL : UInt = "b10000".U(FuncOpType.width)
  def EBREAK: UInt = "b10001".U(FuncOpType.width)
  def MRET  : UInt = "b11000".U(FuncOpType.width)
  def SRET  : UInt = "b11001".U(FuncOpType.width)
  def URET  : UInt = "b11011".U(FuncOpType.width)
  def isJmp(op: UInt)   : Bool = op(4).asBool()
  def isRet(op: UInt)   : Bool = op(3).asBool()
  def isCsri(op: UInt)  : Bool = op(2).asBool()
}

/**
 * CSR 模块
 * @param need_difftest: 是否需要提交difftest Trap
 */
class CSR(

) extends Module with CsrRegDefine {
  class CSRIO(
  ) extends Bundle {
    class CSROutPort extends Bundle {
      val rdata     : UInt          = Output(UInt(DATA_WIDTH))
      val jmp       : BRU_OUTIO     = new BRU_OUTIO
    }
    val in : Valid[CfCtrl] = Flipped(Valid(new CfCtrl))
    val out : Valid[CSROutPort] = Valid(new CSROutPort)
    val inst_inc : Valid[InstInc] = Flipped(Valid(new InstInc))
    val clint : ClintOutPort = Flipped(new ClintOutPort)
    val intr_jmp  : BRU_OUTIO     = new BRU_OUTIO
  }

  val io : CSRIO = IO(new CSRIO())

  // --------------------------- 准备数据 -------------------------

  private val op = io.in.bits.ctrl.funcOpType
  // 写CSR用的数据，如果是CSRR[SCW]I则使用立即数零拓展，否则使用寄存器数，多路选择器在IDUtoEXU中完成
  private val src = io.in.bits.data.src1
  // 读写CSR的地址
  private val addr = io.in.bits.data.imm(CSR_ADDR_LEN - 1, 0)
  private val pc = io.in.bits.cf.pc
  private val ena = io.in.valid
  // 为了用Enum，被迫下划线命名枚举。。。bullshxt
  private val mode_u::mode_s::mode_h::mode_m::Nil = Enum(4)
  private val currentPriv = RegInit(UInt(2.W), mode_m)

  private val rdata = MuxLookup(addr, 0.U(MXLEN.W), readOnlyMap++readWriteMap).asUInt
  private val wdata = MuxLookup(op, 0.U, Array(
    CsrOpType.RW  ->  src,
    CsrOpType.RWI ->  src,
    CsrOpType.RS  ->  (rdata | src),
    CsrOpType.RSI ->  (rdata | src),
    CsrOpType.RC  ->  (rdata & (~src).asUInt()),
    CsrOpType.RCI ->  (rdata & (~src).asUInt())
  ))
  mcycle := mcycle + 1.U

  private val inst_valid = io.inst_inc.valid
  when (inst_valid) {
    minstret := minstret + io.inst_inc.bits.value
  }
  private val is_mret = CsrOpType.MRET === op
//  private val is_sret = CsrOpType.SRET === op
//  private val is_uret = CsrOpType.URET === op
  private val is_jmp : Bool = CsrOpType.isJmp(op)
  private val is_ret = CsrOpType.isRet(op) & is_jmp
  private val new_pc = WireInit(0.U(ADDR_WIDTH))
  dontTouch(new_pc)
  private val trap_valid = WireInit(false.B)
  private val exception_valid = io.in.valid && op === CsrOpType.ECALL
  when(ena && !is_jmp) {
    new_pc := 0.U
    trap_valid := false.B
    switch(addr) {
      is(CsrAddr.mstatus)    {
        val mstatus_new = WireInit(wdata.asTypeOf(new Status))
        // todo 分别把各特权级允许写的字段一一连线
        if (supportUser) {
          status.MPRV  :=  mstatus_new.MPRV
          status.MPP   :=  legalizePrivilege(mstatus_new.MPP)
        }
        status.IE     := mstatus_new.IE
        status.PIE    := mstatus_new.PIE
        status.FS     := mstatus_new.FS

      }
      is(CsrAddr.medeleg)   { medeleg   := wdata  }
      is(CsrAddr.mideleg)   { mideleg   := wdata  }
      is(CsrAddr.mie)       {  ie       := wdata.asTypeOf(new InterruptField) }
      is(CsrAddr.mtvec)     { mtvec     := wdata  }
      is(CsrAddr.mcounteren){ mcounteren:= wdata  }
      is(CsrAddr.mscratch)  { mscratch  := wdata  }
      is(CsrAddr.mepc)      { mepc      := wdata  }
      is(CsrAddr.mcause)    { mcause    := wdata  }
      is(CsrAddr.mtval)     { mtval     := wdata  }
      // todo map pmpcfg[0~15]
      is(CsrAddr.mcycle)    { mcycle    := wdata  }
      is(CsrAddr.minstret)  { minstret  := wdata  }
      // todo map mhpmcounter[3~31]
      // todo map Machine Counter Setup, Debug/Trace Registers, Debug Mode Registers
    }
  }.elsewhen(ena && is_jmp){
    // handle output
    trap_valid := true.B
    new_pc := Mux(is_ret,
      real_epc(),
      real_mtvec()
    )
    // handle internal
    when (op === CsrOpType.ECALL) {
      when (currentPriv === mode_m) {
        mepc := pc
        mcause := Exception.MECall.U
      }
      status.IE.M := false.B        // xIE设为0
      status.PIE.M := status.IE.M   // xPIE设为xIE的值
      status.MPP := currentPriv     // xPPi设为之前的特权级
    }.elsewhen(is_mret) {
      currentPriv := mstatus.MPP    // 特权模式修改为y模式
      status.PIE.M := true.B        // xPIE设为1
      status.IE.M := mstatus.PIE.M  // xIE设为xPIE
      // todo: 给CSR加上U模式，这里为了和NEMU的行为同步，将NEMU中改成了mode_m
      status.MPP := (if (supportUser) mode_u else mode_m)
    }
  }

  // 中断相关定义
  private val interruptPc = real_mtvec()
  private val interruptVec = mie(11, 0) & mip.asUInt()(11,0) & Fill(12, mstatus.IE.M)
  private val interruptValid = interruptVec.asUInt.orR()
  private val interruptNo = Mux(interruptValid, PriorityEncoder(interruptVec), 0.U)
  private val interruptCause = (interruptValid.asUInt << (XLEN - 1).U).asUInt | interruptNo
  // --------------------------- 时钟中断 --------------------------
  // todo: 使用BoringUtil.addSink/addSource在CSR和clint之间添加飞线解决
//  ip.t.M := io.clint.mtip
  mtime := io.clint.mtime

  when (currentPriv === mode_m) {
    // ip.t.M:      mtip: M mode出现时钟中断
    // ie.t.M:      mtie: M mode允许时钟中断
    // status.IE.M: mie:  M mode允许中断
    when(interruptValid) {
      // 暂定实时响应中断
      // todo: 以流水线运行时，记录未提交的最早pc值
      mepc    := pc
      mcause  := interruptCause
      status.IE.M := false.B         // xIE设为0
      status.PIE.M := status.IE.M
      status.MPP  := currentPriv
    }
  }

  // 非流水线状态，立即完成
  io.out.valid            := io.in.valid
  io.out.bits.jmp.new_pc  := new_pc
  io.out.bits.jmp.ena     := trap_valid
  io.out.bits.rdata       := rdata

  io.intr_jmp.new_pc := interruptPc
  io.intr_jmp.ena := interruptValid


  private val csrCommit = Module(new DifftestCSRState)
  csrCommit.io.clock          := clock
  csrCommit.io.coreid         := 0.U
  csrCommit.io.priviledgeMode := RegNext(currentPriv)
  csrCommit.io.mstatus        := RegNext(mstatus.asUInt())
  csrCommit.io.sstatus        := RegNext(sstatus.asUInt())
  csrCommit.io.mepc           := RegNext(mepc)
  csrCommit.io.sepc           := RegNext(0.U)
  csrCommit.io.mtval          := RegNext(mtval)
  csrCommit.io.stval          := RegNext(0.U)
  csrCommit.io.mtvec          := RegNext(mtvec)
  csrCommit.io.stvec          := RegNext(0.U)
  csrCommit.io.mcause         := RegNext(mcause)
  csrCommit.io.scause         := RegNext(0.U)
  csrCommit.io.satp           := RegNext(0.U)
  csrCommit.io.mip            := RegNext(0.U)
  csrCommit.io.mie            := RegNext(mie)
  csrCommit.io.mscratch       := RegNext(mscratch)
  csrCommit.io.sscratch       := RegNext(0.U)
  csrCommit.io.mideleg        := RegNext(mideleg)
  csrCommit.io.medeleg        := RegNext(medeleg)

  addSource(RegNext(io.in.bits.cf.pc),  "difftest_trapEvent_pc")
  addSource(RegNext(mcycle),            "difftest_trapEvent_cycleCnt")
  addSource(RegNext(minstret),          "difftest_trapEvent_instrCnt")

  addSource(RegNext(Mux(interruptValid, interruptNo, 0.U)), "difftest_intrNO")
  addSource(RegNext(Mux(exception_valid, 0.U, 0.U)), "difftest_cause")
  addSource(RegNext(io.in.bits.cf.pc), "difftest_exceptionPC")
  addSource(RegNext(io.in.bits.cf.instr), "difftest_exceptionInst")

  def legalizePrivilege(priv: UInt): UInt =
    if (supportUser)
      Fill(2, priv(0))
    else
      Privilege.Level.M

  def real_epc () : UInt = {
    MuxLookup(currentPriv, 0.U, Array(
    mode_m -> mepc,
    // todo: add mode s&u
    ))
  }

  def real_mtvec () : UInt = {
    MuxLookup(mtvec_mode, 0.U, Array(
      MtvecMode.Direct -> Cat(mtvec_base(61,0), 0.U(2.W)),
      MtvecMode.Vectored -> Cat(mtvec_base + mcause, 0.U(2.W))
    ))
  }

  when (io.in.valid) {
    printf("csr enable\n")
  }
}

private object CsrAddr {
  def ADDR_W    : Width = 12.W
  // Machine Information Registers
  // 0xF11~0xF14
  def mvendorid : UInt  = 0xF11.U(ADDR_W)
  def marchid   : UInt  = 0xF12.U(ADDR_W)
  def mimpid    : UInt  = 0xF13.U(ADDR_W)
  def mhartid   : UInt  = 0xF14.U(ADDR_W)

  // Machine Trap Setup
  // 0x300~0x306
  def mstatus   : UInt  = 0x300.U(ADDR_W)
  def misa      : UInt  = 0x301.U(ADDR_W)
  def medeleg   : UInt  = 0x302.U(ADDR_W)
  def mideleg   : UInt  = 0x303.U(ADDR_W)
  def mie       : UInt  = 0x304.U(ADDR_W)
  def mtvec     : UInt  = 0x305.U(ADDR_W)
  def mcounteren: UInt  = 0x306.U(ADDR_W)

  // Machine Trap Handling
  // 0x340~0x344
  def mscratch  : UInt  = 0x340.U(ADDR_W)
  def mepc      : UInt  = 0x341.U(ADDR_W)
  def mcause    : UInt  = 0x342.U(ADDR_W)
  def mtval     : UInt  = 0x343.U(ADDR_W)
  def mip       : UInt  = 0x344.U(ADDR_W)

  // Machine Memory Protection
  // 0x3A0~0x3A3, 0x3B0~0x3BF
  def pmpcfg(idx: Int) : UInt = {
    require(idx >= 0 && idx < 4)
    (0x3A0 + idx).U(ADDR_W)
  }
  def pmpaddr(idx: Int) : UInt = {
    require(idx >= 0 && idx < 16)
    (0x3B0 + idx).U(ADDR_W)
  }

  // Machine Counters and Timers
  // 0xB00~0xB1F
  def mcycle    : UInt  = 0xB00.U(ADDR_W)
  def mtime     : UInt  = 0xB01.U(ADDR_W)
  def minstret  : UInt  = 0xB02.U(ADDR_W)
  def mhpmcounter(idx : Int) : UInt = {
    require(idx >= 3 && idx < 32)
    (0xB00 + idx).U(ADDR_W)
  }

  // Machine Counter Setup
  // 0x320, 0x323~0x33F
  def mhpmevent(idx: Int) : UInt = {
    require(idx >= 3 && idx < 32)
    (0x320 + idx).U(ADDR_W)
  }
  def mcountinhibit   : UInt = 0x320.U(ADDR_W)

  // Debug/Trace Registers(shared with Debug Mode)
  // 0x7A0~0x7A3
  val tselect   : UInt  = 0x7A0.U(ADDR_W)
  val tdata1    : UInt  = 0x7A1.U(ADDR_W)
  val tdata2    : UInt  = 0x7A2.U(ADDR_W)
  val tdata3    : UInt  = 0x7A3.U(ADDR_W)

  // Debug Mode Registers
  // 0x7B0~0x7B3
  val dcsr      : UInt  = 0x7B0.U(ADDR_W)
  val dpc       : UInt  = 0x7B1.U(ADDR_W)
  val dscratch0 : UInt  = 0x7B2.U(ADDR_W)
  val dscratch1 : UInt  = 0x7B3.U(ADDR_W)
}

trait CsrRegDefine extends Config {
  class PrivilegeMode extends Bundle {
    val M : Bool = Output(Bool())
    val H : Bool = Output(Bool())
    val S : Bool = Output(Bool())
    val U : Bool = Output(Bool())
  }

  // 参考XiangShan的实现，利用if-else实现对RV32与RV64的选择支持
  class Status extends Bundle with Config {
    val SD    : UInt = Output(UInt(1.W))
    val PAD0  : UInt = if (MXLEN == 64) Output(UInt((MXLEN - 37).W)) else null
    val SXL   : UInt = if (MXLEN == 64) Output(UInt(2.W)) else null
    val UXL   : UInt = if (MXLEN == 64) Output(UInt(2.W)) else null
    val PAD1  : UInt = if (MXLEN == 64) Output(UInt(9.W)) else Output(UInt(8.W))
    val TSR   : UInt = Output(UInt(1.W))
    val TW    : UInt = Output(UInt(1.W))
    val TVM   : UInt = Output(UInt(1.W))
    val MXR   : UInt = Output(UInt(1.W))
    val SUM   : UInt = Output(UInt(1.W))
    val MPRV  : UInt = Output(UInt(1.W))
    val XS    : UInt = Output(UInt(2.W))
    val FS    : UInt = Output(UInt(2.W))
    val MPP   : UInt = Output(UInt(2.W))
    val HPP   : UInt = Output(UInt(2.W))
    val SPP   : UInt = Output(UInt(1.W))
    val PIE   = new PrivilegeMode
    val IE    = new PrivilegeMode
  }

  object FloatDirtyStatus {
    val off :: initial :: clean :: dirty :: Nil = Enum(4)
    def isOff(FS: UInt)   : Bool = FS === off
    def isDirty(FS: UInt) : Bool = FS === dirty
  }

  object ExtensionDirtyStatus {
    val all_off :: some_on :: some_clean :: some_dirty :: Nil = Enum(4)
    def isOff(XS: UInt)   : Bool = XS === all_off
    def isDirty(XS: UInt) : Bool = XS === some_dirty
  }

  // 参考NutShell的实现
  class InterruptField extends Bundle {
    val e = new PrivilegeMode
    val t = new PrivilegeMode
    val s = new PrivilegeMode
  }
  protected val CSR_DATA_W : Width = MXLEN.W
  // Machine Information Registers
  // 0xF11~0xF14
  val mvendorid     : UInt = VendorID         .U(CSR_DATA_W)
  val marchid       : UInt = ArchitectureID   .U(CSR_DATA_W)
  val mimpid        : UInt = ImplementationID .U(CSR_DATA_W)
  val mhartid       : UInt = HardwareThreadID .U(CSR_DATA_W)

  // Machine Trap Setup
  // 0x300~0x306
  val status        : Status = RegInit(0.U.asTypeOf(new Status))
  val misa          : UInt = RegInit(MISA.U(CSR_DATA_W))
  val medeleg       : UInt = RegInit(0.U(CSR_DATA_W))
  val mideleg       : UInt = RegInit(0.U(CSR_DATA_W))
  val ie            = RegInit(0.U.asTypeOf(new InterruptField))
  val mie           : UInt = WireInit(ie.asUInt())
  val mtvec         : UInt = RegInit(0.U(CSR_DATA_W))
  val mcounteren    : UInt = RegInit(0.U(CSR_DATA_W))

  // Machine Trap Handling
  // 0x340~0x344
  val mscratch      : UInt = RegInit(0.U(CSR_DATA_W))
  val mepc          : UInt = RegInit(0.U(CSR_DATA_W))
  val mcause        : UInt = RegInit(0.U(CSR_DATA_W))
  val mtval         : UInt = RegInit(0.U(CSR_DATA_W))
  val ip            = WireInit(0.U.asTypeOf(new InterruptField))
  val mip           : UInt = WireInit(ip.asUInt())
  // Machine Memory Protection
  // 0x3A0~0x3A3, 0x3B0~0x3BF
  val pmpcfg        : Vec[UInt] = RegInit(VecInit(Seq.fill( 4)(0.U(CSR_DATA_W))))
  val pmpaddr       : Vec[UInt] = RegInit(VecInit(Seq.fill(16)(0.U(CSR_DATA_W))))

  // Machine Counters and Timers
  // 0xB00~0xB1F
  val mhpmcounter   : Vec[UInt] = RegInit(VecInit(Seq.fill(32)(0.U(CSR_DATA_W))))
  val mcycle        : UInt = mhpmcounter(0)
  val mtime         : UInt = WireInit(0.U)
  val minstret      : UInt = mhpmcounter(2)

  // Machine Counter Setup
  // 0x320, 0x323~0x33F
  val mhpmevent     : Vec[UInt] = RegInit(VecInit(Seq.fill(32)(0.U(CSR_DATA_W))))
  val mcountinhibit : UInt = mhpmevent(0)

  // Debug/Trace Registers(shared with Debug Mode)
  // 0x7A0~0x7A3
  val tselect       : UInt = RegInit(0.U(CSR_DATA_W))
  val tdata1        : UInt = RegInit(0.U(CSR_DATA_W))
  val tdata2        : UInt = RegInit(0.U(CSR_DATA_W))
  val tdata3        : UInt = RegInit(0.U(CSR_DATA_W))

  // Debug Mode Registers
  // 0x7B0~0x7B3
  val dcsr          : UInt = RegInit(0.U(CSR_DATA_W))
  val dpc           : UInt = RegInit(0.U(CSR_DATA_W))
  val dscratch0     : UInt = RegInit(0.U(CSR_DATA_W))
  val dscratch1     : UInt = RegInit(0.U(CSR_DATA_W))

  /** sub field in CSRs */

  // mtvec
  val mtvec_base    : UInt = mtvec(MXLEN-1, 2)
  val mtvec_mode    : UInt = mtvec(1, 0)
  object MtvecMode {
    def Direct : UInt = 0.U(2.W)
    def Vectored : UInt = 1.U(2.W)
  }

  val readOnlyMap = List (
    CsrAddr.mvendorid   ->  mvendorid   ,
    CsrAddr.marchid     ->  marchid     ,
    CsrAddr.mimpid      ->  mimpid      ,
    CsrAddr.mhartid     ->  mhartid     ,
    CsrAddr.mip         ->  mip         ,
  )

  val mstatus = WireInit(0.U.asTypeOf(new Status))
  mstatus.UXL := (if(supportUser)  (log2Ceil(UXLEN)-4).U else 0.U)
  mstatus.SXL := (if(supportSupervisor) (log2Ceil(SXLEN)-4).U else 0.U)
  mstatus.SPP := (if(!supportSupervisor) 0.U else status.SPP)
  mstatus.MPP := (if(!supportUser) Privilege.Level.M else status.MPP)
  mstatus.IE.U := (if(!supportUser) 0.U else status.IE.U)
  mstatus.IE.S := (if(!supportSupervisor) 0.U else status.IE.S)
  mstatus.IE.M := status.IE.M
  mstatus.PIE.U := (if(!supportUser) 0.U else status.PIE.U)
  mstatus.PIE.S := (if(!supportSupervisor) 0.U else status.PIE.S)
  mstatus.PIE.M := status.PIE.M
  mstatus.FS  := status.FS
  mstatus.SD  := FloatDirtyStatus.isDirty(status.FS) || ExtensionDirtyStatus.isDirty(status.XS)

  val sstatus = WireInit(0.U.asTypeOf(new Status))
  sstatus.FS := status.FS
  sstatus.SD := FloatDirtyStatus.isDirty(status.FS) || ExtensionDirtyStatus.isDirty(status.XS)

  val readWriteMap = List (
    CsrAddr.mstatus     ->  mstatus.asUInt(),
    CsrAddr.misa        ->  misa        ,
    CsrAddr.medeleg     ->  medeleg     , // 异常委托寄存器，将m处理的异常委托给更低的特权级
    CsrAddr.mideleg     ->  mideleg     , // 中断委托寄存器，将m处理的中断委托给更低的特权级
    CsrAddr.mie         ->  mie         ,
    CsrAddr.mtvec       ->  mtvec       ,
    CsrAddr.mcounteren  ->  mcounteren  ,
    CsrAddr.mscratch    ->  mscratch    ,
    CsrAddr.mepc        ->  mepc        ,
    CsrAddr.mcause      ->  mcause      ,
    CsrAddr.mtval       ->  mtval       ,
    // todo map pmpcfg[0~15]
    CsrAddr.mcycle      ->  mcycle      ,
    CsrAddr.minstret    ->  minstret    ,
    // todo map mhpmcounter[3~31]
    // todo map Machine Counter Setup, Debug/Trace Registers, Debug Mode Registers
  )
}

object Privilege extends Config {
  object Level {
    def U : UInt = "b00".U(2.W)
    def S : UInt = "b01".U(2.W)
    def H : UInt = "b10".U(2.W)
    def M : UInt = "b11".U(2.W)
  }
  object Access {
    def RW : UInt = "b00".U(2.W)
    def RO : UInt = "b11".U(2.W)
  }
  object FieldSpec {
    /** Reserved Writed Preserve Values, Reads Ingore Values */
    def WPRI : UInt = "b00".U(2.W)
    /** Write/Read Only Legal Values */
    def WLRL : UInt = "b01".U(2.W)
    /** Write Any Values, Reads Legal Values */
    def WARL : UInt = "b11".U(2.W)
  }
  def supportUser : Boolean = ISAEXT.support('U')
  def supportSupervisor : Boolean = ISAEXT.support('S')
}




