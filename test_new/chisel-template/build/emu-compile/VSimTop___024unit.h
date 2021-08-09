// Verilated -*- C++ -*-
// DESCRIPTION: Verilator output: Design internal header
// See VSimTop.h for the primary calling header

#ifndef VERILATED_VSIMTOP___024UNIT_H_
#define VERILATED_VSIMTOP___024UNIT_H_  // guard

#include "verilated_heavy.h"
#include "VSimTop__Dpi.h"

//==========

class VSimTop__Syms;

//----------

VL_MODULE(VSimTop___024unit) {
  public:

    // INTERNAL VARIABLES
  private:
    VSimTop__Syms* __VlSymsp;  // Symbol table
  public:

    // CONSTRUCTORS
  private:
    VL_UNCOPYABLE(VSimTop___024unit);  ///< Copying not allowed
  public:
    VSimTop___024unit(const char* name = "TOP");
    ~VSimTop___024unit();

    // INTERNAL METHODS
    void __Vconfigure(VSimTop__Syms* symsp, bool first);
    void __Vdpiimwrap_amo_helper_TOP____024unit(CData/*7:0*/ cmd, QData/*63:0*/ addr, QData/*63:0*/ wdata, CData/*7:0*/ mask, QData/*63:0*/ (&amo_helper__Vfuncrtn));
    void __Vdpiimwrap_pte_helper_TOP____024unit(QData/*63:0*/ satp, QData/*63:0*/ vpn, QData/*63:0*/ (&pte), CData/*7:0*/ (&level), CData/*7:0*/ (&pte_helper__Vfuncrtn));
    void __Vdpiimwrap_ram_read_helper_TOP____024unit(CData/*0:0*/ en, QData/*63:0*/ rIdx, QData/*63:0*/ (&ram_read_helper__Vfuncrtn));
    void __Vdpiimwrap_ram_write_helper_TOP____024unit(QData/*63:0*/ wIdx, QData/*63:0*/ wdata, QData/*63:0*/ wmask, CData/*0:0*/ wen);
    void __Vdpiimwrap_v_difftest_ArchEvent_TOP____024unit(CData/*7:0*/ coreid, IData/*31:0*/ intrNo, IData/*31:0*/ cause, QData/*63:0*/ exceptionPC);
    void __Vdpiimwrap_v_difftest_ArchFpRegState_TOP____024unit(CData/*7:0*/ coreid, QData/*63:0*/ fpr_0, QData/*63:0*/ fpr_1, QData/*63:0*/ fpr_2, QData/*63:0*/ fpr_3, QData/*63:0*/ fpr_4, QData/*63:0*/ fpr_5, QData/*63:0*/ fpr_6, QData/*63:0*/ fpr_7, QData/*63:0*/ fpr_8, QData/*63:0*/ fpr_9, QData/*63:0*/ fpr_10, QData/*63:0*/ fpr_11, QData/*63:0*/ fpr_12, QData/*63:0*/ fpr_13, QData/*63:0*/ fpr_14, QData/*63:0*/ fpr_15, QData/*63:0*/ fpr_16, QData/*63:0*/ fpr_17, QData/*63:0*/ fpr_18, QData/*63:0*/ fpr_19, QData/*63:0*/ fpr_20, QData/*63:0*/ fpr_21, QData/*63:0*/ fpr_22, QData/*63:0*/ fpr_23, QData/*63:0*/ fpr_24, QData/*63:0*/ fpr_25, QData/*63:0*/ fpr_26, QData/*63:0*/ fpr_27, QData/*63:0*/ fpr_28, QData/*63:0*/ fpr_29, QData/*63:0*/ fpr_30, QData/*63:0*/ fpr_31);
    void __Vdpiimwrap_v_difftest_ArchIntRegState_TOP____024unit(CData/*7:0*/ coreid, QData/*63:0*/ gpr_0, QData/*63:0*/ gpr_1, QData/*63:0*/ gpr_2, QData/*63:0*/ gpr_3, QData/*63:0*/ gpr_4, QData/*63:0*/ gpr_5, QData/*63:0*/ gpr_6, QData/*63:0*/ gpr_7, QData/*63:0*/ gpr_8, QData/*63:0*/ gpr_9, QData/*63:0*/ gpr_10, QData/*63:0*/ gpr_11, QData/*63:0*/ gpr_12, QData/*63:0*/ gpr_13, QData/*63:0*/ gpr_14, QData/*63:0*/ gpr_15, QData/*63:0*/ gpr_16, QData/*63:0*/ gpr_17, QData/*63:0*/ gpr_18, QData/*63:0*/ gpr_19, QData/*63:0*/ gpr_20, QData/*63:0*/ gpr_21, QData/*63:0*/ gpr_22, QData/*63:0*/ gpr_23, QData/*63:0*/ gpr_24, QData/*63:0*/ gpr_25, QData/*63:0*/ gpr_26, QData/*63:0*/ gpr_27, QData/*63:0*/ gpr_28, QData/*63:0*/ gpr_29, QData/*63:0*/ gpr_30, QData/*63:0*/ gpr_31);
    void __Vdpiimwrap_v_difftest_AtomicEvent_TOP____024unit(CData/*7:0*/ coreid, CData/*0:0*/ atomicResp, QData/*63:0*/ atomicAddr, QData/*63:0*/ atomicData, CData/*7:0*/ atomicMask, CData/*7:0*/ atomicFuop, QData/*63:0*/ atomicOut);
    void __Vdpiimwrap_v_difftest_CSRState_TOP____024unit(CData/*7:0*/ coreid, CData/*7:0*/ priviledgeMode, QData/*63:0*/ mstatus, QData/*63:0*/ sstatus, QData/*63:0*/ mepc, QData/*63:0*/ sepc, QData/*63:0*/ mtval, QData/*63:0*/ stval, QData/*63:0*/ mtvec, QData/*63:0*/ stvec, QData/*63:0*/ mcause, QData/*63:0*/ scause, QData/*63:0*/ satp, QData/*63:0*/ mip, QData/*63:0*/ mie, QData/*63:0*/ mscratch, QData/*63:0*/ sscratch, QData/*63:0*/ mideleg, QData/*63:0*/ medeleg);
    void __Vdpiimwrap_v_difftest_InstrCommit_TOP____024unit(CData/*7:0*/ coreid, CData/*7:0*/ index, CData/*0:0*/ valid, QData/*63:0*/ pc, IData/*31:0*/ instr, CData/*0:0*/ skip, CData/*0:0*/ isRVC, CData/*0:0*/ scFailed, CData/*0:0*/ wen, CData/*7:0*/ wdest, QData/*63:0*/ wdata);
    void __Vdpiimwrap_v_difftest_LoadEvent_TOP____024unit(CData/*7:0*/ coreid, CData/*7:0*/ index, CData/*0:0*/ valid, QData/*63:0*/ paddr, CData/*7:0*/ opType, CData/*7:0*/ fuType);
    void __Vdpiimwrap_v_difftest_PtwEvent_TOP____024unit(CData/*7:0*/ coreid, CData/*0:0*/ ptwResp, QData/*63:0*/ ptwAddr, QData/*63:0*/ ptwData_0, QData/*63:0*/ ptwData_1, QData/*63:0*/ ptwData_2, QData/*63:0*/ ptwData_3);
    void __Vdpiimwrap_v_difftest_SbufferEvent_TOP____024unit(CData/*7:0*/ coreid, CData/*0:0*/ sbufferResp, QData/*63:0*/ sbufferAddr, CData/*7:0*/ sbufferData_0, CData/*7:0*/ sbufferData_1, CData/*7:0*/ sbufferData_2, CData/*7:0*/ sbufferData_3, CData/*7:0*/ sbufferData_4, CData/*7:0*/ sbufferData_5, CData/*7:0*/ sbufferData_6, CData/*7:0*/ sbufferData_7, CData/*7:0*/ sbufferData_8, CData/*7:0*/ sbufferData_9, CData/*7:0*/ sbufferData_10, CData/*7:0*/ sbufferData_11, CData/*7:0*/ sbufferData_12, CData/*7:0*/ sbufferData_13, CData/*7:0*/ sbufferData_14, CData/*7:0*/ sbufferData_15, CData/*7:0*/ sbufferData_16, CData/*7:0*/ sbufferData_17, CData/*7:0*/ sbufferData_18, CData/*7:0*/ sbufferData_19, CData/*7:0*/ sbufferData_20, CData/*7:0*/ sbufferData_21, CData/*7:0*/ sbufferData_22, CData/*7:0*/ sbufferData_23, CData/*7:0*/ sbufferData_24, CData/*7:0*/ sbufferData_25, CData/*7:0*/ sbufferData_26, CData/*7:0*/ sbufferData_27, CData/*7:0*/ sbufferData_28, CData/*7:0*/ sbufferData_29, CData/*7:0*/ sbufferData_30, CData/*7:0*/ sbufferData_31, CData/*7:0*/ sbufferData_32, CData/*7:0*/ sbufferData_33, CData/*7:0*/ sbufferData_34, CData/*7:0*/ sbufferData_35, CData/*7:0*/ sbufferData_36, CData/*7:0*/ sbufferData_37, CData/*7:0*/ sbufferData_38, CData/*7:0*/ sbufferData_39, CData/*7:0*/ sbufferData_40, CData/*7:0*/ sbufferData_41, CData/*7:0*/ sbufferData_42, CData/*7:0*/ sbufferData_43, CData/*7:0*/ sbufferData_44, CData/*7:0*/ sbufferData_45, CData/*7:0*/ sbufferData_46, CData/*7:0*/ sbufferData_47, CData/*7:0*/ sbufferData_48, CData/*7:0*/ sbufferData_49, CData/*7:0*/ sbufferData_50, CData/*7:0*/ sbufferData_51, CData/*7:0*/ sbufferData_52, CData/*7:0*/ sbufferData_53, CData/*7:0*/ sbufferData_54, CData/*7:0*/ sbufferData_55, CData/*7:0*/ sbufferData_56, CData/*7:0*/ sbufferData_57, CData/*7:0*/ sbufferData_58, CData/*7:0*/ sbufferData_59, CData/*7:0*/ sbufferData_60, CData/*7:0*/ sbufferData_61, CData/*7:0*/ sbufferData_62, CData/*7:0*/ sbufferData_63, QData/*63:0*/ sbufferMask);
    void __Vdpiimwrap_v_difftest_StoreEvent_TOP____024unit(CData/*7:0*/ coreid, CData/*7:0*/ index, CData/*0:0*/ valid, QData/*63:0*/ storeAddr, QData/*63:0*/ storeData, CData/*7:0*/ storeMask);
    void __Vdpiimwrap_v_difftest_TrapEvent_TOP____024unit(CData/*7:0*/ coreid, CData/*0:0*/ valid, CData/*7:0*/ code, QData/*63:0*/ pc, QData/*63:0*/ cycleCnt, QData/*63:0*/ instrCnt);
    void __Vdpiimwrap_xs_assert_TOP____024unit(QData/*63:0*/ line);
  private:
    static void _ctor_var_reset(VSimTop___024unit* self) VL_ATTR_COLD;
} VL_ATTR_ALIGNED(VL_CACHE_LINE_BYTES);

//----------


#endif  // guard
