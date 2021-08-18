package Core.EXU

import Core.Config.Config
import Core.IDU.FuncType
import Core.MemReg.RegWriteIO
import chisel3._
import chisel3.util._
import utils.{BRU_OUTIO, CfCtrl}

class EXUIO extends Bundle {
    val in              : CfCtrl        = Flipped(new CfCtrl)
    val reg_write_back  : RegWriteIO    = Flipped(new RegWriteIO)
    val branch                          = new BRU_OUTIO
}

class EXU extends Module with Config {
    val io : EXUIO = IO(new EXUIO)
    private val func = io.in.ctrl.funcType
    private val op = io.in.ctrl.funcOpType
    val alu : ALU = Module(new ALU)
    val lsu : LSU = Module(new LSU)
    val bru : BRU = Module(new BRU)
    val csr : CSR = Module(new CSR)
    private val alu_ena = func === FuncType.alu
    private val lsu_ena = func === FuncType.lsu
    private val bru_ena = func === FuncType.bru
    private val csr_ena = func === FuncType.csr

    lsu.io.valid := lsu_ena
    // csr 维护内部状态需要启用信号
    csr.io.ena  := csr_ena
    alu.io.in <> io.in
    lsu.io.in <> io.in
    bru.io.in <> io.in
    csr.io.in <> io.in

    private val wb_ena = Wire(Bool())
    private val wdata = Wire(UInt(XLEN.W))
    wb_ena := MuxLookup(func, false.B, Array(
        FuncType.alu -> true.B,
        FuncType.lsu -> LSUOpType.isLoad(op),
        FuncType.bru -> BRUOpType.isJal_r(io.in.ctrl.funcOpType),
        // csrr*[i]指令都需要写入寄存器堆，ecall ebreak mret等指令的rd对应位置为x0，置true也没有影响
        FuncType.csr -> true.B
    ))
    wdata := MuxLookup(func, 0.U(XLEN.W), Array(
        FuncType.alu -> alu.io.out.aluRes,
        FuncType.lsu -> lsu.io.out.rdata,
        FuncType.bru -> (io.in.cf.pc + 4.U),
        FuncType.csr -> csr.io.out.rdata
    ))
    io.reg_write_back.ena   := wb_ena
    io.reg_write_back.addr  := io.in.ctrl.rfrd
    io.reg_write_back.data  := wdata

    // lsu和csr都会影响pc的值
    io.branch <> MuxLookup(func, 0.U.asTypeOf(new BRU_OUTIO), Array(
        FuncType.bru -> bru.io.out,
        FuncType.csr -> csr.io.out.jmp
    ))

}