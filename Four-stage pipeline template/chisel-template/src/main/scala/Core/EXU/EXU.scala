package Core.EXU

import Core.IDU.FuncType
import Core.MemReg.RegWriteIO
import chisel3._
import chisel3.util._
import utils.{BRU_OUTIO, CfCtrl, Config}

class EXUIO extends Bundle {
    val in             = Flipped(new CfCtrl)
    val reg_write_back = Flipped(new RegWriteIO)
    val branch         = new BRU_OUTIO
}

class EXU extends Module with Config {
    val io = IO(new EXUIO)
    val alu = Module(new ALU)
    val lsu = Module(new LSU)
    val bru = Module(new BRU)

    val alu_ena = Wire(Bool())
    val lsu_ena = Wire(Bool())
    val bru_ena = Wire(Bool())
    alu_ena := io.in.ctrl.funcType === FuncType.alu
    lsu_ena := io.in.ctrl.funcType === FuncType.lsu
    bru_ena := io.in.ctrl.funcType === FuncType.bru
    lsu.io.valid := lsu_ena

    alu.io.in <> io.in
    lsu.io.in <> io.in
    bru.io.in <> io.in

    val wb_ena = Wire(Bool())
    when(alu_ena) {
        wb_ena := true.B
    }.elsewhen(lsu_ena) {
        wb_ena := Mux(LSUOpType.isLoad(io.in.ctrl.funcOpType), true.B, false.B)
    }.otherwise {
        wb_ena := Mux(BRUOpType.isJal_r(io.in.ctrl.funcOpType), true.B, false.B)
    }

    when(alu_ena) {
        io.reg_write_back.data := alu.io.out.aluRes
        // printf("Print during simulation: alu.io.out.aluRes is %d\n", alu.io.out.aluRes)
        io.reg_write_back.ena  := wb_ena
        io.reg_write_back.addr := io.in.ctrl.rfrd
    }.elsewhen(lsu_ena && LSUOpType.isLoad(io.in.ctrl.funcOpType)) {
        io.reg_write_back.data := lsu.io.out.rdata
        // printf("Print during simulation: lsu.io.out.rdata is %d\n", lsu.io.out.rdata)
        io.reg_write_back.ena  := wb_ena
        io.reg_write_back.addr := io.in.ctrl.rfrd
    }.otherwise {
        io.reg_write_back.data := io.in.cf.pc + 4.U
        // printf("Print during simulation: io.in.cf.pc is %d\n", io.in.cf.pc)
        io.reg_write_back.ena  := wb_ena
        io.reg_write_back.addr := io.in.ctrl.rfrd
    }

    io.branch.valid := bru.io.out.valid
    io.branch.newPC := bru.io.out.newPC
}