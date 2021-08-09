// Verilated -*- C++ -*-
// DESCRIPTION: Verilator output: Design implementation internals
// See VSimTop.h for the primary calling header

#include "VSimTop.h"
#include "VSimTop__Syms.h"

#include "verilated_dpi.h"

//==========

VerilatedContext* VSimTop::contextp() {
    return __VlSymsp->_vm_contextp__;
}

void VSimTop::eval_step() {
    VL_DEBUG_IF(VL_DBG_MSGF("+++++TOP Evaluate VSimTop::eval\n"); );
    VSimTop__Syms* __restrict vlSymsp = this->__VlSymsp;  // Setup global symbol table
    VSimTop* const __restrict vlTOPp VL_ATTR_UNUSED = vlSymsp->TOPp;
#ifdef VL_DEBUG
    // Debug assertions
    _eval_debug_assertions();
#endif  // VL_DEBUG
    // Initialize
    if (VL_UNLIKELY(!vlSymsp->__Vm_didInit)) _eval_initial_loop(vlSymsp);
    // Evaluate till stable
    int __VclockLoop = 0;
    QData __Vchange = 1;
    do {
        VL_DEBUG_IF(VL_DBG_MSGF("+ Clock loop\n"););
        _eval(vlSymsp);
        if (VL_UNLIKELY(++__VclockLoop > 100)) {
            // About to fail, so enable debug to see what's not settling.
            // Note you must run make with OPT=-DVL_DEBUG for debug prints.
            int __Vsaved_debug = Verilated::debug();
            Verilated::debug(1);
            __Vchange = _change_request(vlSymsp);
            Verilated::debug(__Vsaved_debug);
            VL_FATAL_MT("/home/bread/chisel-template/build/../build/SimTop.v", 1427, "",
                "Verilated model didn't converge\n"
                "- See https://verilator.org/warn/DIDNOTCONVERGE");
        } else {
            __Vchange = _change_request(vlSymsp);
        }
    } while (VL_UNLIKELY(__Vchange));
}

void VSimTop::_eval_initial_loop(VSimTop__Syms* __restrict vlSymsp) {
    vlSymsp->__Vm_didInit = true;
    _eval_initial(vlSymsp);
    // Evaluate till stable
    int __VclockLoop = 0;
    QData __Vchange = 1;
    do {
        _eval_settle(vlSymsp);
        _eval(vlSymsp);
        if (VL_UNLIKELY(++__VclockLoop > 100)) {
            // About to fail, so enable debug to see what's not settling.
            // Note you must run make with OPT=-DVL_DEBUG for debug prints.
            int __Vsaved_debug = Verilated::debug();
            Verilated::debug(1);
            __Vchange = _change_request(vlSymsp);
            Verilated::debug(__Vsaved_debug);
            VL_FATAL_MT("/home/bread/chisel-template/build/../build/SimTop.v", 1427, "",
                "Verilated model didn't DC converge\n"
                "- See https://verilator.org/warn/DIDNOTCONVERGE");
        } else {
            __Vchange = _change_request(vlSymsp);
        }
    } while (VL_UNLIKELY(__Vchange));
}

VL_INLINE_OPT void VSimTop::_sequent__TOP__2(VSimTop__Syms* __restrict vlSymsp) {
    VL_DEBUG_IF(VL_DBG_MSGF("+    VSimTop::_sequent__TOP__2\n"); );
    VSimTop* const __restrict vlTOPp VL_ATTR_UNUSED = vlSymsp->TOPp;
    // Body
    vlSymsp->TOP____024unit.__Vdpiimwrap_ram_write_helper_TOP____024unit(0ULL, 0ULL, 0ULL, 0U);
    vlSymsp->TOP____024unit.__Vdpiimwrap_v_difftest_CSRState_TOP____024unit(0U, 0U, 0ULL, 0ULL, 0ULL, 0ULL, 0ULL, 0ULL, 0ULL, 0ULL, 0ULL, 0ULL, 0ULL, 0ULL, 0ULL, 0ULL, 0ULL, 0ULL, 0ULL);
    vlSymsp->TOP____024unit.__Vdpiimwrap_ram_write_helper_TOP____024unit(
                                                                         (0x1fffffffffffffffULL 
                                                                          & ((vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__lsu__DOT__addr 
                                                                              - 0x80000000ULL) 
                                                                             >> 3U)), 
                                                                         (((((0U 
                                                                              == 
                                                                              (3U 
                                                                               & (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType)))
                                                                              ? 
                                                                             (((QData)((IData)(
                                                                                (0xffU 
                                                                                & (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__dis_io_out_data_src2)))) 
                                                                               << 0x38U) 
                                                                              | (((QData)((IData)(
                                                                                (0xffU 
                                                                                & (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__dis_io_out_data_src2)))) 
                                                                                << 0x30U) 
                                                                                | (((QData)((IData)(
                                                                                (0xffU 
                                                                                & (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__dis_io_out_data_src2)))) 
                                                                                << 0x28U) 
                                                                                | (((QData)((IData)(
                                                                                (0xffU 
                                                                                & (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__dis_io_out_data_src2)))) 
                                                                                << 0x20U) 
                                                                                | (((QData)((IData)(
                                                                                (0xffU 
                                                                                & (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__dis_io_out_data_src2)))) 
                                                                                << 0x18U) 
                                                                                | (((QData)((IData)(
                                                                                (0xffU 
                                                                                & (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__dis_io_out_data_src2)))) 
                                                                                << 0x10U) 
                                                                                | (((QData)((IData)(
                                                                                (0xffU 
                                                                                & (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__dis_io_out_data_src2)))) 
                                                                                << 8U) 
                                                                                | (QData)((IData)(
                                                                                (0xffU 
                                                                                & (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__dis_io_out_data_src2)))))))))))
                                                                              : 0ULL) 
                                                                            | ((1U 
                                                                                == 
                                                                                (3U 
                                                                                & (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType)))
                                                                                ? 
                                                                               (((QData)((IData)(
                                                                                (0xffffU 
                                                                                & (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__dis_io_out_data_src2)))) 
                                                                                << 0x30U) 
                                                                                | (((QData)((IData)(
                                                                                (0xffffU 
                                                                                & (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__dis_io_out_data_src2)))) 
                                                                                << 0x20U) 
                                                                                | (((QData)((IData)(
                                                                                (0xffffU 
                                                                                & (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__dis_io_out_data_src2)))) 
                                                                                << 0x10U) 
                                                                                | (QData)((IData)(
                                                                                (0xffffU 
                                                                                & (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__dis_io_out_data_src2)))))))
                                                                                : 0ULL)) 
                                                                           | ((2U 
                                                                               == 
                                                                               (3U 
                                                                                & (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType)))
                                                                               ? 
                                                                              (((QData)((IData)(vlTOPp->SimTop__DOT__rvcore__DOT__dis_io_out_data_src2)) 
                                                                                << 0x20U) 
                                                                               | (QData)((IData)(vlTOPp->SimTop__DOT__rvcore__DOT__dis_io_out_data_src2)))
                                                                               : 0ULL)) 
                                                                          | ((3U 
                                                                              == 
                                                                              (3U 
                                                                               & (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType)))
                                                                              ? vlTOPp->SimTop__DOT__rvcore__DOT__dis_io_out_data_src2
                                                                              : 0ULL)), (QData)((IData)(
                                                                                (0x7fffU 
                                                                                & (((((0U 
                                                                                == 
                                                                                (3U 
                                                                                & (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))) 
                                                                                | ((1U 
                                                                                == 
                                                                                (3U 
                                                                                & (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType)))
                                                                                 ? 3U
                                                                                 : 0U)) 
                                                                                | ((2U 
                                                                                == 
                                                                                (3U 
                                                                                & (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType)))
                                                                                 ? 0xfU
                                                                                 : 0U)) 
                                                                                | ((3U 
                                                                                == 
                                                                                (3U 
                                                                                & (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType)))
                                                                                 ? 0xffU
                                                                                 : 0U)) 
                                                                                << 
                                                                                (7U 
                                                                                & (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__lsu__DOT__addr)))))), 
                                                                         (((1U 
                                                                            == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcType)) 
                                                                           & ((IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType) 
                                                                              >> 3U)) 
                                                                          & (1U 
                                                                             == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcType))));
    vlSymsp->TOP____024unit.__Vdpiimwrap_v_difftest_ArchIntRegState_TOP____024unit(0U, vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_0, vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_1, vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_2, vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_3, vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_4, vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_5, vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_6, vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_7, vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_8, vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_9, vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_10, vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_11, vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_12, vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_13, vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_14, vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_15, vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_16, vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_17, vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_18, vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_19, vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_20, vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_21, vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_22, vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_23, vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_24, vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_25, vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_26, vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_27, vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_28, vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_29, vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_30, vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_31);
    if (vlTOPp->SimTop__DOT__REG_1) {
        vlSymsp->TOP____024unit.__Vdpiimwrap_v_difftest_InstrCommit_TOP____024unit(0U, 0U, (IData)(vlTOPp->SimTop__DOT__REG_1), vlTOPp->SimTop__DOT__REG_3, vlTOPp->SimTop__DOT__REG_5, 0U, 0U, 0U, (IData)(vlTOPp->SimTop__DOT__REG_7), vlTOPp->SimTop__DOT__REG_11, vlTOPp->SimTop__DOT__REG_9);
    }
    if (vlTOPp->reset) {
        vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_0 = 0ULL;
    } else if (vlTOPp->SimTop__DOT__rvcore__DOT__exu_io_reg_write_back_ena) {
        if ((0U != (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfrd))) {
            if ((0U == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfrd))) {
                vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_0 
                    = vlTOPp->SimTop__DOT__rvcore__DOT__exu_io_reg_write_back_data;
            }
        }
    }
    if (vlTOPp->reset) {
        vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_1 = 0ULL;
    } else if (vlTOPp->SimTop__DOT__rvcore__DOT__exu_io_reg_write_back_ena) {
        if ((0U != (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfrd))) {
            if ((1U == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfrd))) {
                vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_1 
                    = vlTOPp->SimTop__DOT__rvcore__DOT__exu_io_reg_write_back_data;
            }
        }
    }
    if (vlTOPp->reset) {
        vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_2 = 0ULL;
    } else if (vlTOPp->SimTop__DOT__rvcore__DOT__exu_io_reg_write_back_ena) {
        if ((0U != (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfrd))) {
            if ((2U == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfrd))) {
                vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_2 
                    = vlTOPp->SimTop__DOT__rvcore__DOT__exu_io_reg_write_back_data;
            }
        }
    }
    if (vlTOPp->reset) {
        vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_3 = 0ULL;
    } else if (vlTOPp->SimTop__DOT__rvcore__DOT__exu_io_reg_write_back_ena) {
        if ((0U != (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfrd))) {
            if ((3U == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfrd))) {
                vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_3 
                    = vlTOPp->SimTop__DOT__rvcore__DOT__exu_io_reg_write_back_data;
            }
        }
    }
    if (vlTOPp->reset) {
        vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_4 = 0ULL;
    } else if (vlTOPp->SimTop__DOT__rvcore__DOT__exu_io_reg_write_back_ena) {
        if ((0U != (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfrd))) {
            if ((4U == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfrd))) {
                vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_4 
                    = vlTOPp->SimTop__DOT__rvcore__DOT__exu_io_reg_write_back_data;
            }
        }
    }
    if (vlTOPp->reset) {
        vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_5 = 0ULL;
    } else if (vlTOPp->SimTop__DOT__rvcore__DOT__exu_io_reg_write_back_ena) {
        if ((0U != (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfrd))) {
            if ((5U == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfrd))) {
                vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_5 
                    = vlTOPp->SimTop__DOT__rvcore__DOT__exu_io_reg_write_back_data;
            }
        }
    }
    if (vlTOPp->reset) {
        vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_6 = 0ULL;
    } else if (vlTOPp->SimTop__DOT__rvcore__DOT__exu_io_reg_write_back_ena) {
        if ((0U != (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfrd))) {
            if ((6U == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfrd))) {
                vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_6 
                    = vlTOPp->SimTop__DOT__rvcore__DOT__exu_io_reg_write_back_data;
            }
        }
    }
    if (vlTOPp->reset) {
        vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_7 = 0ULL;
    } else if (vlTOPp->SimTop__DOT__rvcore__DOT__exu_io_reg_write_back_ena) {
        if ((0U != (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfrd))) {
            if ((7U == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfrd))) {
                vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_7 
                    = vlTOPp->SimTop__DOT__rvcore__DOT__exu_io_reg_write_back_data;
            }
        }
    }
    if (vlTOPp->reset) {
        vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_8 = 0ULL;
    } else if (vlTOPp->SimTop__DOT__rvcore__DOT__exu_io_reg_write_back_ena) {
        if ((0U != (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfrd))) {
            if ((8U == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfrd))) {
                vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_8 
                    = vlTOPp->SimTop__DOT__rvcore__DOT__exu_io_reg_write_back_data;
            }
        }
    }
    if (vlTOPp->reset) {
        vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_9 = 0ULL;
    } else if (vlTOPp->SimTop__DOT__rvcore__DOT__exu_io_reg_write_back_ena) {
        if ((0U != (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfrd))) {
            if ((9U == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfrd))) {
                vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_9 
                    = vlTOPp->SimTop__DOT__rvcore__DOT__exu_io_reg_write_back_data;
            }
        }
    }
    if (vlTOPp->reset) {
        vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_10 = 0ULL;
    } else if (vlTOPp->SimTop__DOT__rvcore__DOT__exu_io_reg_write_back_ena) {
        if ((0U != (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfrd))) {
            if ((0xaU == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfrd))) {
                vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_10 
                    = vlTOPp->SimTop__DOT__rvcore__DOT__exu_io_reg_write_back_data;
            }
        }
    }
    if (vlTOPp->reset) {
        vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_11 = 0ULL;
    } else if (vlTOPp->SimTop__DOT__rvcore__DOT__exu_io_reg_write_back_ena) {
        if ((0U != (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfrd))) {
            if ((0xbU == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfrd))) {
                vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_11 
                    = vlTOPp->SimTop__DOT__rvcore__DOT__exu_io_reg_write_back_data;
            }
        }
    }
    if (vlTOPp->reset) {
        vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_12 = 0ULL;
    } else if (vlTOPp->SimTop__DOT__rvcore__DOT__exu_io_reg_write_back_ena) {
        if ((0U != (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfrd))) {
            if ((0xcU == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfrd))) {
                vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_12 
                    = vlTOPp->SimTop__DOT__rvcore__DOT__exu_io_reg_write_back_data;
            }
        }
    }
    if (vlTOPp->reset) {
        vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_13 = 0ULL;
    } else if (vlTOPp->SimTop__DOT__rvcore__DOT__exu_io_reg_write_back_ena) {
        if ((0U != (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfrd))) {
            if ((0xdU == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfrd))) {
                vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_13 
                    = vlTOPp->SimTop__DOT__rvcore__DOT__exu_io_reg_write_back_data;
            }
        }
    }
    if (vlTOPp->reset) {
        vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_14 = 0ULL;
    } else if (vlTOPp->SimTop__DOT__rvcore__DOT__exu_io_reg_write_back_ena) {
        if ((0U != (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfrd))) {
            if ((0xeU == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfrd))) {
                vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_14 
                    = vlTOPp->SimTop__DOT__rvcore__DOT__exu_io_reg_write_back_data;
            }
        }
    }
    if (vlTOPp->reset) {
        vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_15 = 0ULL;
    } else if (vlTOPp->SimTop__DOT__rvcore__DOT__exu_io_reg_write_back_ena) {
        if ((0U != (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfrd))) {
            if ((0xfU == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfrd))) {
                vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_15 
                    = vlTOPp->SimTop__DOT__rvcore__DOT__exu_io_reg_write_back_data;
            }
        }
    }
    if (vlTOPp->reset) {
        vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_16 = 0ULL;
    } else if (vlTOPp->SimTop__DOT__rvcore__DOT__exu_io_reg_write_back_ena) {
        if ((0U != (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfrd))) {
            if ((0x10U == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfrd))) {
                vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_16 
                    = vlTOPp->SimTop__DOT__rvcore__DOT__exu_io_reg_write_back_data;
            }
        }
    }
    if (vlTOPp->reset) {
        vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_17 = 0ULL;
    } else if (vlTOPp->SimTop__DOT__rvcore__DOT__exu_io_reg_write_back_ena) {
        if ((0U != (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfrd))) {
            if ((0x11U == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfrd))) {
                vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_17 
                    = vlTOPp->SimTop__DOT__rvcore__DOT__exu_io_reg_write_back_data;
            }
        }
    }
    if (vlTOPp->reset) {
        vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_18 = 0ULL;
    } else if (vlTOPp->SimTop__DOT__rvcore__DOT__exu_io_reg_write_back_ena) {
        if ((0U != (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfrd))) {
            if ((0x12U == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfrd))) {
                vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_18 
                    = vlTOPp->SimTop__DOT__rvcore__DOT__exu_io_reg_write_back_data;
            }
        }
    }
    if (vlTOPp->reset) {
        vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_19 = 0ULL;
    } else if (vlTOPp->SimTop__DOT__rvcore__DOT__exu_io_reg_write_back_ena) {
        if ((0U != (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfrd))) {
            if ((0x13U == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfrd))) {
                vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_19 
                    = vlTOPp->SimTop__DOT__rvcore__DOT__exu_io_reg_write_back_data;
            }
        }
    }
    if (vlTOPp->reset) {
        vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_20 = 0ULL;
    } else if (vlTOPp->SimTop__DOT__rvcore__DOT__exu_io_reg_write_back_ena) {
        if ((0U != (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfrd))) {
            if ((0x14U == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfrd))) {
                vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_20 
                    = vlTOPp->SimTop__DOT__rvcore__DOT__exu_io_reg_write_back_data;
            }
        }
    }
    if (vlTOPp->reset) {
        vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_21 = 0ULL;
    } else if (vlTOPp->SimTop__DOT__rvcore__DOT__exu_io_reg_write_back_ena) {
        if ((0U != (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfrd))) {
            if ((0x15U == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfrd))) {
                vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_21 
                    = vlTOPp->SimTop__DOT__rvcore__DOT__exu_io_reg_write_back_data;
            }
        }
    }
    if (vlTOPp->reset) {
        vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_22 = 0ULL;
    } else if (vlTOPp->SimTop__DOT__rvcore__DOT__exu_io_reg_write_back_ena) {
        if ((0U != (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfrd))) {
            if ((0x16U == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfrd))) {
                vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_22 
                    = vlTOPp->SimTop__DOT__rvcore__DOT__exu_io_reg_write_back_data;
            }
        }
    }
    if (vlTOPp->reset) {
        vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_23 = 0ULL;
    } else if (vlTOPp->SimTop__DOT__rvcore__DOT__exu_io_reg_write_back_ena) {
        if ((0U != (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfrd))) {
            if ((0x17U == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfrd))) {
                vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_23 
                    = vlTOPp->SimTop__DOT__rvcore__DOT__exu_io_reg_write_back_data;
            }
        }
    }
    if (vlTOPp->reset) {
        vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_24 = 0ULL;
    } else if (vlTOPp->SimTop__DOT__rvcore__DOT__exu_io_reg_write_back_ena) {
        if ((0U != (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfrd))) {
            if ((0x18U == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfrd))) {
                vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_24 
                    = vlTOPp->SimTop__DOT__rvcore__DOT__exu_io_reg_write_back_data;
            }
        }
    }
    if (vlTOPp->reset) {
        vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_25 = 0ULL;
    } else if (vlTOPp->SimTop__DOT__rvcore__DOT__exu_io_reg_write_back_ena) {
        if ((0U != (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfrd))) {
            if ((0x19U == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfrd))) {
                vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_25 
                    = vlTOPp->SimTop__DOT__rvcore__DOT__exu_io_reg_write_back_data;
            }
        }
    }
    if (vlTOPp->reset) {
        vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_26 = 0ULL;
    } else if (vlTOPp->SimTop__DOT__rvcore__DOT__exu_io_reg_write_back_ena) {
        if ((0U != (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfrd))) {
            if ((0x1aU == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfrd))) {
                vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_26 
                    = vlTOPp->SimTop__DOT__rvcore__DOT__exu_io_reg_write_back_data;
            }
        }
    }
    if (vlTOPp->reset) {
        vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_27 = 0ULL;
    } else if (vlTOPp->SimTop__DOT__rvcore__DOT__exu_io_reg_write_back_ena) {
        if ((0U != (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfrd))) {
            if ((0x1bU == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfrd))) {
                vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_27 
                    = vlTOPp->SimTop__DOT__rvcore__DOT__exu_io_reg_write_back_data;
            }
        }
    }
    if (vlTOPp->reset) {
        vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_28 = 0ULL;
    } else if (vlTOPp->SimTop__DOT__rvcore__DOT__exu_io_reg_write_back_ena) {
        if ((0U != (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfrd))) {
            if ((0x1cU == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfrd))) {
                vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_28 
                    = vlTOPp->SimTop__DOT__rvcore__DOT__exu_io_reg_write_back_data;
            }
        }
    }
    if (vlTOPp->reset) {
        vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_29 = 0ULL;
    } else if (vlTOPp->SimTop__DOT__rvcore__DOT__exu_io_reg_write_back_ena) {
        if ((0U != (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfrd))) {
            if ((0x1dU == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfrd))) {
                vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_29 
                    = vlTOPp->SimTop__DOT__rvcore__DOT__exu_io_reg_write_back_data;
            }
        }
    }
    if (vlTOPp->reset) {
        vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_30 = 0ULL;
    } else if (vlTOPp->SimTop__DOT__rvcore__DOT__exu_io_reg_write_back_ena) {
        if ((0U != (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfrd))) {
            if ((0x1eU == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfrd))) {
                vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_30 
                    = vlTOPp->SimTop__DOT__rvcore__DOT__exu_io_reg_write_back_data;
            }
        }
    }
    if (vlTOPp->reset) {
        vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_31 = 0ULL;
    } else if (vlTOPp->SimTop__DOT__rvcore__DOT__exu_io_reg_write_back_ena) {
        if ((0U != (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfrd))) {
            if ((0x1fU == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfrd))) {
                vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_31 
                    = vlTOPp->SimTop__DOT__rvcore__DOT__exu_io_reg_write_back_data;
            }
        }
    }
    vlTOPp->SimTop__DOT__REG_1 = vlTOPp->SimTop__DOT__REG;
    vlTOPp->SimTop__DOT__REG_7 = vlTOPp->SimTop__DOT__REG_6;
    vlTOPp->SimTop__DOT__REG_11 = vlTOPp->SimTop__DOT__REG_10;
    vlTOPp->SimTop__DOT__REG_9 = vlTOPp->SimTop__DOT__REG_8;
    vlTOPp->SimTop__DOT__REG_5 = vlTOPp->SimTop__DOT__REG_4;
    vlTOPp->SimTop__DOT__REG_3 = vlTOPp->SimTop__DOT__REG_2;
    vlTOPp->SimTop__DOT__REG = (1U & (~ (IData)(vlTOPp->reset)));
    vlTOPp->SimTop__DOT__REG_6 = vlTOPp->SimTop__DOT__rvcore__DOT__exu_io_reg_write_back_ena;
    vlTOPp->SimTop__DOT__REG_10 = vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfrd;
    vlTOPp->SimTop__DOT__REG_8 = vlTOPp->SimTop__DOT__rvcore__DOT__exu_io_reg_write_back_data;
    vlTOPp->SimTop__DOT__REG_4 = vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr;
    vlTOPp->SimTop__DOT__REG_2 = vlTOPp->SimTop__DOT__rvcore__DOT__ifu__DOT__pc;
    vlTOPp->SimTop__DOT__rvcore__DOT__ifu__DOT__pc 
        = ((IData)(vlTOPp->reset) ? 0x80000000ULL : 
           (((5U == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcType)) 
             & ((((((((0x58U == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType)) 
                      | (0x5aU == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))) 
                     | ((0x10U == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType)) 
                        & (vlTOPp->SimTop__DOT__rvcore__DOT__dis_io_out_data_src1 
                           == vlTOPp->SimTop__DOT__rvcore__DOT__dis_io_out_data_src2))) 
                    | ((0x11U == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType)) 
                       & (vlTOPp->SimTop__DOT__rvcore__DOT__dis_io_out_data_src1 
                          != vlTOPp->SimTop__DOT__rvcore__DOT__dis_io_out_data_src2))) 
                   | ((0x14U == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType)) 
                      & VL_LTS_IQQ(1,64,64, vlTOPp->SimTop__DOT__rvcore__DOT__dis_io_out_data_src1, vlTOPp->SimTop__DOT__rvcore__DOT__dis_io_out_data_src2))) 
                  | ((0x15U == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType)) 
                     & VL_GTES_IQQ(1,64,64, vlTOPp->SimTop__DOT__rvcore__DOT__dis_io_out_data_src1, vlTOPp->SimTop__DOT__rvcore__DOT__dis_io_out_data_src2))) 
                 | ((0x16U == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType)) 
                    & (vlTOPp->SimTop__DOT__rvcore__DOT__dis_io_out_data_src1 
                       < vlTOPp->SimTop__DOT__rvcore__DOT__dis_io_out_data_src2))) 
                | ((0x17U == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType)) 
                   & (vlTOPp->SimTop__DOT__rvcore__DOT__dis_io_out_data_src1 
                      >= vlTOPp->SimTop__DOT__rvcore__DOT__dis_io_out_data_src2))))
             ? (4ULL + ((0x5aU == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                         ? (vlTOPp->SimTop__DOT__rvcore__DOT__dis_io_out_data_src1 
                            + vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_data_imm)
                         : vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__bru__DOT___io_out_newPC_T_4))
             : vlTOPp->SimTop__DOT__rvcore__DOT__ifu__DOT___pc_T_3));
    vlTOPp->SimTop__DOT__rvcore__DOT__ifu__DOT___pc_T_3 
        = (4ULL + vlTOPp->SimTop__DOT__rvcore__DOT__ifu__DOT__pc);
}

VL_INLINE_OPT void VSimTop::_combo__TOP__4(VSimTop__Syms* __restrict vlSymsp) {
    VL_DEBUG_IF(VL_DBG_MSGF("+    VSimTop::_combo__TOP__4\n"); );
    VSimTop* const __restrict vlTOPp VL_ATTR_UNUSED = vlSymsp->TOPp;
    // Variables
    VlWide<4>/*127:0*/ __Vtemp51;
    VlWide<4>/*127:0*/ __Vtemp52;
    VlWide<4>/*127:0*/ __Vtemp53;
    VlWide<4>/*127:0*/ __Vtemp54;
    VlWide<4>/*127:0*/ __Vtemp55;
    VlWide<4>/*127:0*/ __Vtemp56;
    VlWide<4>/*127:0*/ __Vtemp57;
    VlWide<4>/*127:0*/ __Vtemp58;
    VlWide<4>/*127:0*/ __Vtemp59;
    VlWide<4>/*127:0*/ __Vtemp60;
    VlWide<4>/*127:0*/ __Vtemp61;
    VlWide<4>/*127:0*/ __Vtemp62;
    VlWide<4>/*127:0*/ __Vtemp63;
    VlWide<4>/*127:0*/ __Vtemp64;
    VlWide<4>/*127:0*/ __Vtemp65;
    VlWide<4>/*127:0*/ __Vtemp93;
    VlWide<4>/*127:0*/ __Vtemp94;
    // Body
    vlSymsp->TOP____024unit.__Vdpiimwrap_ram_read_helper_TOP____024unit(
                                                                        (1U 
                                                                         & (~ (IData)(vlTOPp->reset))), 
                                                                        (0x1fffffffffffffffULL 
                                                                         & ((vlTOPp->SimTop__DOT__rvcore__DOT__ifu__DOT__pc 
                                                                             - 0x80000000ULL) 
                                                                            >> 3U)), vlTOPp->__Vfunc_ram_read_helper__0__Vfuncout);
    vlTOPp->SimTop__DOT__rvcore__DOT__ifu__DOT__ram_rdata 
        = vlTOPp->__Vfunc_ram_read_helper__0__Vfuncout;
    vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr 
        = ((1U & (IData)((vlTOPp->SimTop__DOT__rvcore__DOT__ifu__DOT__pc 
                          >> 2U))) ? (IData)((vlTOPp->SimTop__DOT__rvcore__DOT__ifu__DOT__ram_rdata 
                                              >> 0x20U))
            : (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__ifu__DOT__ram_rdata));
    vlTOPp->SimTop__DOT__rvcore__DOT__idu__DOT___T_165 
        = ((3U == (0x707fU & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr)) 
           | ((0x1003U == (0x707fU & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr)) 
              | ((0x2003U == (0x707fU & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr)) 
                 | ((0x4003U == (0x707fU & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr)) 
                    | ((0x5003U == (0x707fU & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr)) 
                       | ((0x23U == (0x707fU & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr)) 
                          | ((0x1023U == (0x707fU & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr)) 
                             | ((0x2023U == (0x707fU 
                                             & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr)) 
                                | ((0x1bU != (0x707fU 
                                              & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr)) 
                                   & ((0x101bU != (0xfe00707fU 
                                                   & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr)) 
                                      & ((0x501bU != 
                                          (0xfe00707fU 
                                           & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr)) 
                                         & ((0x4000501bU 
                                             != (0xfe00707fU 
                                                 & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr)) 
                                            & ((0x103bU 
                                                != 
                                                (0xfe00707fU 
                                                 & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr)) 
                                               & ((0x503bU 
                                                   != 
                                                   (0xfe00707fU 
                                                    & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr)) 
                                                  & ((0x4000503bU 
                                                      != 
                                                      (0xfe00707fU 
                                                       & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr)) 
                                                     & ((0x3bU 
                                                         != 
                                                         (0xfe00707fU 
                                                          & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr)) 
                                                        & ((0x4000003bU 
                                                            != 
                                                            (0xfe00707fU 
                                                             & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr)) 
                                                           & ((0x6003U 
                                                               == 
                                                               (0x707fU 
                                                                & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr)) 
                                                              | ((0x3003U 
                                                                  == 
                                                                  (0x707fU 
                                                                   & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr)) 
                                                                 | (0x3023U 
                                                                    == 
                                                                    (0x707fU 
                                                                     & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr)))))))))))))))))))));
    if ((0x23U == (0x707fU & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))) {
        vlTOPp->SimTop__DOT__rvcore__DOT__idu__DOT___T_208 = 8U;
        vlTOPp->SimTop__DOT__rvcore__DOT__idu__DOT___T_112 = 2U;
    } else {
        vlTOPp->SimTop__DOT__rvcore__DOT__idu__DOT___T_208 
            = ((0x1023U == (0x707fU & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                ? 9U : ((0x2023U == (0x707fU & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                         ? 0xaU : ((0x1bU == (0x707fU 
                                              & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                                    ? 0x60U : ((0x101bU 
                                                == 
                                                (0xfe00707fU 
                                                 & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                                                ? 0x21U
                                                : (
                                                   (0x501bU 
                                                    == 
                                                    (0xfe00707fU 
                                                     & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                                                    ? 0x25U
                                                    : 
                                                   ((0x4000501bU 
                                                     == 
                                                     (0xfe00707fU 
                                                      & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                                                     ? 0x2dU
                                                     : 
                                                    ((0x103bU 
                                                      == 
                                                      (0xfe00707fU 
                                                       & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                                                      ? 0x21U
                                                      : 
                                                     ((0x503bU 
                                                       == 
                                                       (0xfe00707fU 
                                                        & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                                                       ? 0x25U
                                                       : 
                                                      ((0x4000503bU 
                                                        == 
                                                        (0xfe00707fU 
                                                         & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                                                        ? 0x2dU
                                                        : 
                                                       ((0x3bU 
                                                         == 
                                                         (0xfe00707fU 
                                                          & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                                                         ? 0x60U
                                                         : 
                                                        ((0x4000003bU 
                                                          == 
                                                          (0xfe00707fU 
                                                           & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                                                          ? 0x28U
                                                          : 
                                                         ((0x6003U 
                                                           == 
                                                           (0x707fU 
                                                            & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                                                           ? 6U
                                                           : 
                                                          ((0x3003U 
                                                            == 
                                                            (0x707fU 
                                                             & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                                                            ? 3U
                                                            : 
                                                           ((0x3023U 
                                                             == 
                                                             (0x707fU 
                                                              & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                                                             ? 0xbU
                                                             : 0x40U))))))))))))));
        vlTOPp->SimTop__DOT__rvcore__DOT__idu__DOT___T_112 
            = ((0x1023U == (0x707fU & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                ? 2U : ((0x2023U == (0x707fU & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                         ? 2U : ((0x1bU == (0x707fU 
                                            & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                                  ? 4U : ((0x101bU 
                                           == (0xfe00707fU 
                                               & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                                           ? 4U : (
                                                   (0x501bU 
                                                    == 
                                                    (0xfe00707fU 
                                                     & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                                                    ? 4U
                                                    : 
                                                   ((0x4000501bU 
                                                     == 
                                                     (0xfe00707fU 
                                                      & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                                                     ? 4U
                                                     : 
                                                    ((0x103bU 
                                                      == 
                                                      (0xfe00707fU 
                                                       & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                                                      ? 5U
                                                      : 
                                                     ((0x503bU 
                                                       == 
                                                       (0xfe00707fU 
                                                        & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                                                       ? 5U
                                                       : 
                                                      ((0x4000503bU 
                                                        == 
                                                        (0xfe00707fU 
                                                         & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                                                        ? 5U
                                                        : 
                                                       ((0x3bU 
                                                         == 
                                                         (0xfe00707fU 
                                                          & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                                                         ? 5U
                                                         : 
                                                        ((0x4000003bU 
                                                          == 
                                                          (0xfe00707fU 
                                                           & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                                                          ? 5U
                                                          : 
                                                         ((0x6003U 
                                                           == 
                                                           (0x707fU 
                                                            & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                                                           ? 4U
                                                           : 
                                                          ((0x3003U 
                                                            == 
                                                            (0x707fU 
                                                             & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                                                            ? 4U
                                                            : 
                                                           ((0x3023U 
                                                             == 
                                                             (0x707fU 
                                                              & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                                                             ? 2U
                                                             : 0U))))))))))))));
    }
    vlTOPp->SimTop__DOT__rvcore__DOT__idu__DOT___T_180 
        = ((0x5033U == (0xfe00707fU & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
            ? 0U : ((0x6033U == (0xfe00707fU & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                     ? 0U : ((0x7033U == (0xfe00707fU 
                                          & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                              ? 0U : ((0x40000033U 
                                       == (0xfe00707fU 
                                           & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                                       ? 0U : ((0x40005033U 
                                                == 
                                                (0xfe00707fU 
                                                 & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                                                ? 0U
                                                : (
                                                   (0x17U 
                                                    == 
                                                    (0x7fU 
                                                     & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                                                    ? 0U
                                                    : 
                                                   ((0x37U 
                                                     == 
                                                     (0x7fU 
                                                      & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                                                     ? 0U
                                                     : 
                                                    ((0x6fU 
                                                      == 
                                                      (0x7fU 
                                                       & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                                                      ? 5U
                                                      : 
                                                     ((0x67U 
                                                       == 
                                                       (0x707fU 
                                                        & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                                                       ? 5U
                                                       : 
                                                      ((0x63U 
                                                        == 
                                                        (0x707fU 
                                                         & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                                                        ? 5U
                                                        : 
                                                       ((0x1063U 
                                                         == 
                                                         (0x707fU 
                                                          & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                                                         ? 5U
                                                         : 
                                                        ((0x4063U 
                                                          == 
                                                          (0x707fU 
                                                           & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                                                          ? 5U
                                                          : 
                                                         ((0x5063U 
                                                           == 
                                                           (0x707fU 
                                                            & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                                                           ? 5U
                                                           : 
                                                          ((0x6063U 
                                                            == 
                                                            (0x707fU 
                                                             & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                                                            ? 5U
                                                            : 
                                                           ((0x7063U 
                                                             == 
                                                             (0x707fU 
                                                              & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                                                             ? 5U
                                                             : (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu__DOT___T_165))))))))))))))));
    if ((0x17U == (0x7fU & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))) {
        vlTOPp->SimTop__DOT__rvcore__DOT__idu__DOT___T_223 = 0x40U;
        vlTOPp->SimTop__DOT__rvcore__DOT__idu__DOT___T_127 = 6U;
    } else {
        vlTOPp->SimTop__DOT__rvcore__DOT__idu__DOT___T_223 
            = ((0x37U == (0x7fU & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                ? 0xfU : ((0x6fU == (0x7fU & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                           ? 0x58U : ((0x67U == (0x707fU 
                                                 & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                                       ? 0x5aU : ((0x63U 
                                                   == 
                                                   (0x707fU 
                                                    & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                                                   ? 0x10U
                                                   : 
                                                  ((0x1063U 
                                                    == 
                                                    (0x707fU 
                                                     & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                                                    ? 0x11U
                                                    : 
                                                   ((0x4063U 
                                                     == 
                                                     (0x707fU 
                                                      & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                                                     ? 0x14U
                                                     : 
                                                    ((0x5063U 
                                                      == 
                                                      (0x707fU 
                                                       & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                                                      ? 0x15U
                                                      : 
                                                     ((0x6063U 
                                                       == 
                                                       (0x707fU 
                                                        & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                                                       ? 0x16U
                                                       : 
                                                      ((0x7063U 
                                                        == 
                                                        (0x707fU 
                                                         & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                                                        ? 0x17U
                                                        : 
                                                       ((3U 
                                                         == 
                                                         (0x707fU 
                                                          & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                                                         ? 0U
                                                         : 
                                                        ((0x1003U 
                                                          == 
                                                          (0x707fU 
                                                           & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                                                          ? 1U
                                                          : 
                                                         ((0x2003U 
                                                           == 
                                                           (0x707fU 
                                                            & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                                                           ? 2U
                                                           : 
                                                          ((0x4003U 
                                                            == 
                                                            (0x707fU 
                                                             & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                                                            ? 4U
                                                            : 
                                                           ((0x5003U 
                                                             == 
                                                             (0x707fU 
                                                              & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                                                             ? 5U
                                                             : (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu__DOT___T_208)))))))))))))));
        vlTOPp->SimTop__DOT__rvcore__DOT__idu__DOT___T_127 
            = ((0x37U == (0x7fU & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                ? 6U : ((0x6fU == (0x7fU & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                         ? 7U : ((0x67U == (0x707fU 
                                            & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                                  ? 4U : ((0x63U == 
                                           (0x707fU 
                                            & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                                           ? 1U : (
                                                   (0x1063U 
                                                    == 
                                                    (0x707fU 
                                                     & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                                                    ? 1U
                                                    : 
                                                   ((0x4063U 
                                                     == 
                                                     (0x707fU 
                                                      & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                                                     ? 1U
                                                     : 
                                                    ((0x5063U 
                                                      == 
                                                      (0x707fU 
                                                       & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                                                      ? 1U
                                                      : 
                                                     ((0x6063U 
                                                       == 
                                                       (0x707fU 
                                                        & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                                                       ? 1U
                                                       : 
                                                      ((0x7063U 
                                                        == 
                                                        (0x707fU 
                                                         & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                                                        ? 1U
                                                        : 
                                                       ((3U 
                                                         == 
                                                         (0x707fU 
                                                          & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                                                         ? 4U
                                                         : 
                                                        ((0x1003U 
                                                          == 
                                                          (0x707fU 
                                                           & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                                                          ? 4U
                                                          : 
                                                         ((0x2003U 
                                                           == 
                                                           (0x707fU 
                                                            & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                                                           ? 4U
                                                           : 
                                                          ((0x4003U 
                                                            == 
                                                            (0x707fU 
                                                             & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                                                            ? 4U
                                                            : 
                                                           ((0x5003U 
                                                             == 
                                                             (0x707fU 
                                                              & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                                                             ? 4U
                                                             : (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu__DOT___T_112)))))))))))))));
    }
    vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcType 
        = ((0x13U == (0x707fU & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
            ? 0U : ((0x1013U == (0xfc00707fU & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                     ? 0U : ((0x2013U == (0x707fU & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                              ? 0U : ((0x3013U == (0x707fU 
                                                   & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                                       ? 0U : ((0x4013U 
                                                == 
                                                (0x707fU 
                                                 & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                                                ? 0U
                                                : (
                                                   (0x5013U 
                                                    == 
                                                    (0xfc00707fU 
                                                     & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                                                    ? 0U
                                                    : 
                                                   ((0x6013U 
                                                     == 
                                                     (0x707fU 
                                                      & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                                                     ? 0U
                                                     : 
                                                    ((0x7013U 
                                                      == 
                                                      (0x707fU 
                                                       & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                                                      ? 0U
                                                      : 
                                                     ((0x40005013U 
                                                       == 
                                                       (0xfc00707fU 
                                                        & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                                                       ? 0U
                                                       : 
                                                      ((0x33U 
                                                        == 
                                                        (0xfe00707fU 
                                                         & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                                                        ? 0U
                                                        : 
                                                       ((0x1033U 
                                                         == 
                                                         (0xfe00707fU 
                                                          & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                                                         ? 0U
                                                         : 
                                                        ((0x2033U 
                                                          == 
                                                          (0xfe00707fU 
                                                           & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                                                          ? 0U
                                                          : 
                                                         ((0x3033U 
                                                           == 
                                                           (0xfe00707fU 
                                                            & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                                                           ? 0U
                                                           : 
                                                          ((0x4033U 
                                                            == 
                                                            (0xfe00707fU 
                                                             & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                                                            ? 0U
                                                            : (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu__DOT___T_180)))))))))))))));
    if ((0x4013U == (0x707fU & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))) {
        vlTOPp->SimTop__DOT__rvcore__DOT__idu__DOT___T_238 = 4U;
        vlTOPp->SimTop__DOT__rvcore__DOT__idu__DOT___T_142 = 4U;
    } else {
        vlTOPp->SimTop__DOT__rvcore__DOT__idu__DOT___T_238 
            = ((0x5013U == (0xfc00707fU & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                ? 5U : ((0x6013U == (0x707fU & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                         ? 6U : ((0x7013U == (0x707fU 
                                              & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                                  ? 7U : ((0x40005013U 
                                           == (0xfc00707fU 
                                               & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                                           ? 0xdU : 
                                          ((0x33U == 
                                            (0xfe00707fU 
                                             & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                                            ? 0x40U
                                            : ((0x1033U 
                                                == 
                                                (0xfe00707fU 
                                                 & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                                                ? 1U
                                                : (
                                                   (0x2033U 
                                                    == 
                                                    (0xfe00707fU 
                                                     & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                                                    ? 2U
                                                    : 
                                                   ((0x3033U 
                                                     == 
                                                     (0xfe00707fU 
                                                      & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                                                     ? 3U
                                                     : 
                                                    ((0x4033U 
                                                      == 
                                                      (0xfe00707fU 
                                                       & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                                                      ? 4U
                                                      : 
                                                     ((0x5033U 
                                                       == 
                                                       (0xfe00707fU 
                                                        & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                                                       ? 5U
                                                       : 
                                                      ((0x6033U 
                                                        == 
                                                        (0xfe00707fU 
                                                         & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                                                        ? 6U
                                                        : 
                                                       ((0x7033U 
                                                         == 
                                                         (0xfe00707fU 
                                                          & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                                                         ? 7U
                                                         : 
                                                        ((0x40000033U 
                                                          == 
                                                          (0xfe00707fU 
                                                           & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                                                          ? 8U
                                                          : 
                                                         ((0x40005033U 
                                                           == 
                                                           (0xfe00707fU 
                                                            & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                                                           ? 0xdU
                                                           : (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu__DOT___T_223)))))))))))))));
        vlTOPp->SimTop__DOT__rvcore__DOT__idu__DOT___T_142 
            = ((0x5013U == (0xfc00707fU & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                ? 4U : ((0x6013U == (0x707fU & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                         ? 4U : ((0x7013U == (0x707fU 
                                              & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                                  ? 4U : ((0x40005013U 
                                           == (0xfc00707fU 
                                               & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                                           ? 4U : (
                                                   (0x33U 
                                                    == 
                                                    (0xfe00707fU 
                                                     & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                                                    ? 5U
                                                    : 
                                                   ((0x1033U 
                                                     == 
                                                     (0xfe00707fU 
                                                      & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                                                     ? 5U
                                                     : 
                                                    ((0x2033U 
                                                      == 
                                                      (0xfe00707fU 
                                                       & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                                                      ? 5U
                                                      : 
                                                     ((0x3033U 
                                                       == 
                                                       (0xfe00707fU 
                                                        & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                                                       ? 5U
                                                       : 
                                                      ((0x4033U 
                                                        == 
                                                        (0xfe00707fU 
                                                         & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                                                        ? 5U
                                                        : 
                                                       ((0x5033U 
                                                         == 
                                                         (0xfe00707fU 
                                                          & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                                                         ? 5U
                                                         : 
                                                        ((0x6033U 
                                                          == 
                                                          (0xfe00707fU 
                                                           & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                                                          ? 5U
                                                          : 
                                                         ((0x7033U 
                                                           == 
                                                           (0xfe00707fU 
                                                            & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                                                           ? 5U
                                                           : 
                                                          ((0x40000033U 
                                                            == 
                                                            (0xfe00707fU 
                                                             & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                                                            ? 5U
                                                            : 
                                                           ((0x40005033U 
                                                             == 
                                                             (0xfe00707fU 
                                                              & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                                                             ? 5U
                                                             : (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu__DOT___T_127)))))))))))))));
    }
    if ((0x13U == (0x707fU & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))) {
        vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType = 0x40U;
        vlTOPp->SimTop__DOT__rvcore__DOT__idu__DOT__instrType = 4U;
    } else {
        vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType 
            = ((0x1013U == (0xfc00707fU & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                ? 1U : ((0x2013U == (0x707fU & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                         ? 2U : ((0x3013U == (0x707fU 
                                              & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                                  ? 3U : (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu__DOT___T_238))));
        vlTOPp->SimTop__DOT__rvcore__DOT__idu__DOT__instrType 
            = ((0x1013U == (0xfc00707fU & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                ? 4U : ((0x2013U == (0x707fU & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                         ? 4U : ((0x3013U == (0x707fU 
                                              & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))
                                  ? 4U : (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu__DOT___T_142))));
    }
    vlTOPp->SimTop__DOT__rvcore__DOT__exu_io_reg_write_back_ena 
        = (1U & ((0U == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcType)) 
                 | ((1U == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcType))
                     ? (~ ((IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType) 
                           >> 3U)) : ((IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType) 
                                      >> 6U))));
    vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfrd 
        = ((4U & (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu__DOT__instrType))
            ? (0x1fU & (vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr 
                        >> 7U)) : 0U);
    vlTOPp->SimTop__DOT__rvcore__DOT__dis_io_out_data_src1 
        = ((((6U == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu__DOT__instrType)) 
             | (7U == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu__DOT__instrType))) 
            | (0U == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu__DOT__instrType)))
            ? vlTOPp->SimTop__DOT__rvcore__DOT__ifu__DOT__pc
            : 0ULL);
    vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_data_imm 
        = ((((((4U == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu__DOT__instrType))
                ? ((((0x80000000U & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr)
                      ? 0xfffffffffffffULL : 0ULL) 
                    << 0xcU) | (QData)((IData)((0xfffU 
                                                & (vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr 
                                                   >> 0x14U)))))
                : 0ULL) | ((2U == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu__DOT__instrType))
                            ? ((((0x80000000U & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr)
                                  ? 0xfffffffffffffULL
                                  : 0ULL) << 0xcU) 
                               | (QData)((IData)(((0xfe0U 
                                                   & (vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr 
                                                      >> 0x14U)) 
                                                  | (0x1fU 
                                                     & (vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr 
                                                        >> 7U))))))
                            : 0ULL)) | ((1U == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu__DOT__instrType))
                                         ? ((((0x80000000U 
                                               & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr)
                                               ? 0x7ffffffffffffULL
                                               : 0ULL) 
                                             << 0xdU) 
                                            | (QData)((IData)(
                                                              ((0x1000U 
                                                                & (vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr 
                                                                   >> 0x13U)) 
                                                               | ((0x800U 
                                                                   & (vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr 
                                                                      << 4U)) 
                                                                  | ((0x7e0U 
                                                                      & (vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr 
                                                                         >> 0x14U)) 
                                                                     | (0x1eU 
                                                                        & (vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr 
                                                                           >> 7U))))))))
                                         : 0ULL)) | 
            ((6U == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu__DOT__instrType))
              ? (((QData)((IData)(((0x80000000U & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr)
                                    ? 0xffffffffU : 0U))) 
                  << 0x20U) | (QData)((IData)((0xfffff000U 
                                               & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr))))
              : 0ULL)) | ((7U == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu__DOT__instrType))
                           ? ((((0x80000000U & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr)
                                 ? 0x7ffffffffffULL
                                 : 0ULL) << 0x15U) 
                              | (QData)((IData)(((0x100000U 
                                                  & (vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr 
                                                     >> 0xbU)) 
                                                 | ((0xff000U 
                                                     & vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr) 
                                                    | ((0x800U 
                                                        & (vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr 
                                                           >> 9U)) 
                                                       | (0x7feU 
                                                          & (vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr 
                                                             >> 0x14U))))))))
                           : 0ULL));
    vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__bru__DOT___io_out_newPC_T_4 
        = (vlTOPp->SimTop__DOT__rvcore__DOT__ifu__DOT__pc 
           + vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_data_imm);
    vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__lsu__DOT__addr 
        = (vlTOPp->SimTop__DOT__rvcore__DOT__dis_io_out_data_src1 
           + vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_data_imm);
    vlTOPp->SimTop__DOT__rvcore__DOT__dis_io_out_data_src2 
        = (((((4U == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu__DOT__instrType)) 
              | (6U == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu__DOT__instrType))) 
             | (7U == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu__DOT__instrType))) 
            | (0U == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu__DOT__instrType)))
            ? vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_data_imm
            : 0ULL);
    vlSymsp->TOP____024unit.__Vdpiimwrap_ram_read_helper_TOP____024unit(
                                                                        (1U 
                                                                         == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcType)), 
                                                                        (0x1fffffffffffffffULL 
                                                                         & ((vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__lsu__DOT__addr 
                                                                             - 0x80000000ULL) 
                                                                            >> 3U)), vlTOPp->__Vfunc_ram_read_helper__2__Vfuncout);
    vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__lsu__DOT__ram_rdata 
        = vlTOPp->__Vfunc_ram_read_helper__2__Vfuncout;
    vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__alu__DOT___T_1 
        = (vlTOPp->SimTop__DOT__rvcore__DOT__dis_io_out_data_src1 
           + vlTOPp->SimTop__DOT__rvcore__DOT__dis_io_out_data_src2);
    vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__alu__DOT__lo 
        = VL_LTS_IQQ(1,64,64, vlTOPp->SimTop__DOT__rvcore__DOT__dis_io_out_data_src1, vlTOPp->SimTop__DOT__rvcore__DOT__dis_io_out_data_src2);
    vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__alu__DOT___T_12 
        = (vlTOPp->SimTop__DOT__rvcore__DOT__dis_io_out_data_src1 
           - vlTOPp->SimTop__DOT__rvcore__DOT__dis_io_out_data_src2);
    vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__alu__DOT__shamt 
        = (0x3fU & ((0x20U & (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                     ? (0x1fU & (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__dis_io_out_data_src2))
                     : (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__dis_io_out_data_src2)));
    vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__lsu__DOT__rdataSel 
        = (((((((((0U == (7U & (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__lsu__DOT__addr)))
                   ? vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__lsu__DOT__ram_rdata
                   : 0ULL) | ((1U == (7U & (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__lsu__DOT__addr)))
                               ? (0xffffffffffffffULL 
                                  & (vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__lsu__DOT__ram_rdata 
                                     >> 8U)) : 0ULL)) 
                | ((2U == (7U & (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__lsu__DOT__addr)))
                    ? (0xffffffffffffULL & (vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__lsu__DOT__ram_rdata 
                                            >> 0x10U))
                    : 0ULL)) | ((3U == (7U & (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__lsu__DOT__addr)))
                                 ? (0xffffffffffULL 
                                    & (vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__lsu__DOT__ram_rdata 
                                       >> 0x18U)) : 0ULL)) 
              | (QData)((IData)(((4U == (7U & (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__lsu__DOT__addr)))
                                  ? (IData)((vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__lsu__DOT__ram_rdata 
                                             >> 0x20U))
                                  : 0U)))) | (QData)((IData)(
                                                             ((5U 
                                                               == 
                                                               (7U 
                                                                & (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__lsu__DOT__addr)))
                                                               ? 
                                                              (0xffffffU 
                                                               & (IData)(
                                                                         (vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__lsu__DOT__ram_rdata 
                                                                          >> 0x28U)))
                                                               : 0U)))) 
            | (QData)((IData)(((6U == (7U & (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__lsu__DOT__addr)))
                                ? (0xffffU & (IData)(
                                                     (vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__lsu__DOT__ram_rdata 
                                                      >> 0x30U)))
                                : 0U)))) | (QData)((IData)(
                                                           ((7U 
                                                             == 
                                                             (7U 
                                                              & (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__lsu__DOT__addr)))
                                                             ? 
                                                            (0xffU 
                                                             & (IData)(
                                                                       (vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__lsu__DOT__ram_rdata 
                                                                        >> 0x38U)))
                                                             : 0U))));
    VL_EXTEND_WI(127,32, __Vtemp51, ((0x1fU >= (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__alu__DOT__shamt))
                                      ? ((IData)(vlTOPp->SimTop__DOT__rvcore__DOT__dis_io_out_data_src1) 
                                         >> (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__alu__DOT__shamt))
                                      : 0U));
    VL_EXTEND_WI(127,1, __Vtemp52, (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__alu__DOT__lo));
    VL_EXTEND_WQ(127,64, __Vtemp53, vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__alu__DOT___T_12);
    VL_EXTEND_WQ(127,64, __Vtemp54, vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__alu__DOT___T_1);
    VL_EXTEND_WQ(127,64, __Vtemp55, VL_SHIFTRS_QQI(64,64,6, vlTOPp->SimTop__DOT__rvcore__DOT__dis_io_out_data_src1, (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__alu__DOT__shamt)));
    VL_EXTEND_WQ(127,64, __Vtemp56, vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__alu__DOT___T_12);
    VL_EXTEND_WQ(127,64, __Vtemp57, (vlTOPp->SimTop__DOT__rvcore__DOT__dis_io_out_data_src1 
                                     & vlTOPp->SimTop__DOT__rvcore__DOT__dis_io_out_data_src2));
    VL_EXTEND_WQ(127,64, __Vtemp58, (vlTOPp->SimTop__DOT__rvcore__DOT__dis_io_out_data_src1 
                                     | vlTOPp->SimTop__DOT__rvcore__DOT__dis_io_out_data_src2));
    VL_EXTEND_WQ(127,64, __Vtemp59, (vlTOPp->SimTop__DOT__rvcore__DOT__dis_io_out_data_src1 
                                     >> (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__alu__DOT__shamt)));
    VL_EXTEND_WQ(127,64, __Vtemp60, (vlTOPp->SimTop__DOT__rvcore__DOT__dis_io_out_data_src1 
                                     ^ vlTOPp->SimTop__DOT__rvcore__DOT__dis_io_out_data_src2));
    VL_EXTEND_WI(127,1, __Vtemp61, (vlTOPp->SimTop__DOT__rvcore__DOT__dis_io_out_data_src1 
                                    < vlTOPp->SimTop__DOT__rvcore__DOT__dis_io_out_data_src2));
    VL_EXTEND_WI(127,1, __Vtemp62, (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__alu__DOT__lo));
    VL_EXTEND_WQ(127,64, __Vtemp63, vlTOPp->SimTop__DOT__rvcore__DOT__dis_io_out_data_src1);
    VL_SHIFTL_WWI(127,127,6, __Vtemp64, __Vtemp63, (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__alu__DOT__shamt));
    VL_EXTEND_WQ(127,64, __Vtemp65, ((0x40U == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                                      ? vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__alu__DOT___T_1
                                      : 0ULL));
    if ((0x25U == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))) {
        vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__alu__DOT___res_T_27[0U] 
            = __Vtemp51[0U];
        vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__alu__DOT___res_T_27[1U] 
            = __Vtemp51[1U];
        vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__alu__DOT___res_T_27[2U] 
            = __Vtemp51[2U];
        vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__alu__DOT___res_T_27[3U] 
            = (0x7fffffffU & __Vtemp51[3U]);
    } else {
        vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__alu__DOT___res_T_27[0U] 
            = ((0x21U == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                ? __Vtemp52[0U] : ((0x28U == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                                    ? __Vtemp53[0U]
                                    : ((0x60U == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                                        ? __Vtemp54[0U]
                                        : ((0xdU == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                                            ? __Vtemp55[0U]
                                            : ((8U 
                                                == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                                                ? __Vtemp56[0U]
                                                : (
                                                   (7U 
                                                    == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                                                    ? 
                                                   __Vtemp57[0U]
                                                    : 
                                                   ((6U 
                                                     == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                                                     ? 
                                                    __Vtemp58[0U]
                                                     : 
                                                    ((5U 
                                                      == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                                                      ? 
                                                     __Vtemp59[0U]
                                                      : 
                                                     ((4U 
                                                       == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                                                       ? 
                                                      __Vtemp60[0U]
                                                       : 
                                                      ((3U 
                                                        == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                                                        ? 
                                                       __Vtemp61[0U]
                                                        : 
                                                       ((2U 
                                                         == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                                                         ? 
                                                        __Vtemp62[0U]
                                                         : 
                                                        ((1U 
                                                          == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                                                          ? 
                                                         __Vtemp64[0U]
                                                          : 
                                                         __Vtemp65[0U]))))))))))));
        vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__alu__DOT___res_T_27[1U] 
            = ((0x21U == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                ? __Vtemp52[1U] : ((0x28U == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                                    ? __Vtemp53[1U]
                                    : ((0x60U == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                                        ? __Vtemp54[1U]
                                        : ((0xdU == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                                            ? __Vtemp55[1U]
                                            : ((8U 
                                                == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                                                ? __Vtemp56[1U]
                                                : (
                                                   (7U 
                                                    == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                                                    ? 
                                                   __Vtemp57[1U]
                                                    : 
                                                   ((6U 
                                                     == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                                                     ? 
                                                    __Vtemp58[1U]
                                                     : 
                                                    ((5U 
                                                      == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                                                      ? 
                                                     __Vtemp59[1U]
                                                      : 
                                                     ((4U 
                                                       == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                                                       ? 
                                                      __Vtemp60[1U]
                                                       : 
                                                      ((3U 
                                                        == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                                                        ? 
                                                       __Vtemp61[1U]
                                                        : 
                                                       ((2U 
                                                         == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                                                         ? 
                                                        __Vtemp62[1U]
                                                         : 
                                                        ((1U 
                                                          == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                                                          ? 
                                                         __Vtemp64[1U]
                                                          : 
                                                         __Vtemp65[1U]))))))))))));
        vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__alu__DOT___res_T_27[2U] 
            = ((0x21U == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                ? __Vtemp52[2U] : ((0x28U == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                                    ? __Vtemp53[2U]
                                    : ((0x60U == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                                        ? __Vtemp54[2U]
                                        : ((0xdU == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                                            ? __Vtemp55[2U]
                                            : ((8U 
                                                == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                                                ? __Vtemp56[2U]
                                                : (
                                                   (7U 
                                                    == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                                                    ? 
                                                   __Vtemp57[2U]
                                                    : 
                                                   ((6U 
                                                     == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                                                     ? 
                                                    __Vtemp58[2U]
                                                     : 
                                                    ((5U 
                                                      == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                                                      ? 
                                                     __Vtemp59[2U]
                                                      : 
                                                     ((4U 
                                                       == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                                                       ? 
                                                      __Vtemp60[2U]
                                                       : 
                                                      ((3U 
                                                        == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                                                        ? 
                                                       __Vtemp61[2U]
                                                        : 
                                                       ((2U 
                                                         == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                                                         ? 
                                                        __Vtemp62[2U]
                                                         : 
                                                        ((1U 
                                                          == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                                                          ? 
                                                         __Vtemp64[2U]
                                                          : 
                                                         __Vtemp65[2U]))))))))))));
        vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__alu__DOT___res_T_27[3U] 
            = (0x7fffffffU & ((0x21U == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                               ? __Vtemp52[3U] : ((0x28U 
                                                   == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                                                   ? 
                                                  __Vtemp53[3U]
                                                   : 
                                                  ((0x60U 
                                                    == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                                                    ? 
                                                   __Vtemp54[3U]
                                                    : 
                                                   ((0xdU 
                                                     == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                                                     ? 
                                                    __Vtemp55[3U]
                                                     : 
                                                    ((8U 
                                                      == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                                                      ? 
                                                     __Vtemp56[3U]
                                                      : 
                                                     ((7U 
                                                       == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                                                       ? 
                                                      __Vtemp57[3U]
                                                       : 
                                                      ((6U 
                                                        == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                                                        ? 
                                                       __Vtemp58[3U]
                                                        : 
                                                       ((5U 
                                                         == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                                                         ? 
                                                        __Vtemp59[3U]
                                                         : 
                                                        ((4U 
                                                          == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                                                          ? 
                                                         __Vtemp60[3U]
                                                          : 
                                                         ((3U 
                                                           == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                                                           ? 
                                                          __Vtemp61[3U]
                                                           : 
                                                          ((2U 
                                                            == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                                                            ? 
                                                           __Vtemp62[3U]
                                                            : 
                                                           ((1U 
                                                             == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                                                             ? 
                                                            __Vtemp64[3U]
                                                             : 
                                                            __Vtemp65[3U])))))))))))));
    }
    VL_EXTEND_WQ(127,64, __Vtemp93, vlTOPp->SimTop__DOT__rvcore__DOT__dis_io_out_data_src2);
    VL_EXTEND_WI(127,32, __Vtemp94, ((0x1fU >= (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__alu__DOT__shamt))
                                      ? VL_SHIFTRS_III(32,32,6, (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__dis_io_out_data_src1), (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__alu__DOT__shamt))
                                      : (- ((IData)(vlTOPp->SimTop__DOT__rvcore__DOT__dis_io_out_data_src1) 
                                            >> 0x1fU))));
    if ((0xfU == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))) {
        vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__alu__DOT___res_T_31[0U] 
            = __Vtemp93[0U];
        vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__alu__DOT___res_T_31[1U] 
            = __Vtemp93[1U];
        vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__alu__DOT___res_T_31[2U] 
            = __Vtemp93[2U];
        vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__alu__DOT___res_T_31[3U] 
            = __Vtemp93[3U];
    } else {
        vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__alu__DOT___res_T_31[0U] 
            = ((0x2dU == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                ? __Vtemp94[0U] : vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__alu__DOT___res_T_27[0U]);
        vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__alu__DOT___res_T_31[1U] 
            = ((0x2dU == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                ? __Vtemp94[1U] : vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__alu__DOT___res_T_27[1U]);
        vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__alu__DOT___res_T_31[2U] 
            = ((0x2dU == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                ? __Vtemp94[2U] : vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__alu__DOT___res_T_27[2U]);
        vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__alu__DOT___res_T_31[3U] 
            = ((0x2dU == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                ? __Vtemp94[3U] : vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__alu__DOT___res_T_27[3U]);
    }
    vlTOPp->SimTop__DOT__rvcore__DOT__exu_io_reg_write_back_data 
        = ((0U == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcType))
            ? ((0x20U & (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                ? (((QData)((IData)(((0x80000000U & 
                                      vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__alu__DOT___res_T_31[0U])
                                      ? 0xffffffffU
                                      : 0U))) << 0x20U) 
                   | (QData)((IData)(vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__alu__DOT___res_T_31[0U])))
                : (((QData)((IData)(vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__alu__DOT___res_T_31[1U])) 
                    << 0x20U) | (QData)((IData)(vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__alu__DOT___res_T_31[0U]))))
            : (((1U == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcType)) 
                & (~ ((IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType) 
                      >> 3U))) ? ((((((((0U == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                                         ? ((((1U & (IData)(
                                                            (vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__lsu__DOT__rdataSel 
                                                             >> 7U)))
                                               ? 0xffffffffffffffULL
                                               : 0ULL) 
                                             << 8U) 
                                            | (QData)((IData)(
                                                              (0xffU 
                                                               & (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__lsu__DOT__rdataSel)))))
                                         : 0ULL) | 
                                       ((1U == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                                         ? ((((1U & (IData)(
                                                            (vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__lsu__DOT__rdataSel 
                                                             >> 0xfU)))
                                               ? 0xffffffffffffULL
                                               : 0ULL) 
                                             << 0x10U) 
                                            | (QData)((IData)(
                                                              (0xffffU 
                                                               & (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__lsu__DOT__rdataSel)))))
                                         : 0ULL)) | 
                                      ((2U == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                                        ? (((QData)((IData)(
                                                            ((1U 
                                                              & (IData)(
                                                                        (vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__lsu__DOT__rdataSel 
                                                                         >> 0x1fU)))
                                                              ? 0xffffffffU
                                                              : 0U))) 
                                            << 0x20U) 
                                           | (QData)((IData)(vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__lsu__DOT__rdataSel)))
                                        : 0ULL)) | 
                                     ((3U == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                                       ? vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__lsu__DOT__rdataSel
                                       : 0ULL)) | (
                                                   (4U 
                                                    == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                                                    ? (QData)((IData)(
                                                                      (0xffU 
                                                                       & (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__lsu__DOT__rdataSel))))
                                                    : 0ULL)) 
                                   | ((5U == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                                       ? (QData)((IData)(
                                                         (0xffffU 
                                                          & (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__lsu__DOT__rdataSel))))
                                       : 0ULL)) | (
                                                   (6U 
                                                    == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                                                    ? (QData)((IData)(vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__lsu__DOT__rdataSel))
                                                    : 0ULL))
                : (4ULL + vlTOPp->SimTop__DOT__rvcore__DOT__ifu__DOT__pc)));
}

void VSimTop::_eval(VSimTop__Syms* __restrict vlSymsp) {
    VL_DEBUG_IF(VL_DBG_MSGF("+    VSimTop::_eval\n"); );
    VSimTop* const __restrict vlTOPp VL_ATTR_UNUSED = vlSymsp->TOPp;
    // Body
    if (((IData)(vlTOPp->clock) & (~ (IData)(vlTOPp->__Vclklast__TOP__clock)))) {
        vlTOPp->_sequent__TOP__2(vlSymsp);
    }
    vlTOPp->_combo__TOP__4(vlSymsp);
    // Final
    vlTOPp->__Vclklast__TOP__clock = vlTOPp->clock;
}

VL_INLINE_OPT QData VSimTop::_change_request(VSimTop__Syms* __restrict vlSymsp) {
    VL_DEBUG_IF(VL_DBG_MSGF("+    VSimTop::_change_request\n"); );
    VSimTop* const __restrict vlTOPp VL_ATTR_UNUSED = vlSymsp->TOPp;
    // Body
    return (vlTOPp->_change_request_1(vlSymsp));
}

VL_INLINE_OPT QData VSimTop::_change_request_1(VSimTop__Syms* __restrict vlSymsp) {
    VL_DEBUG_IF(VL_DBG_MSGF("+    VSimTop::_change_request_1\n"); );
    VSimTop* const __restrict vlTOPp VL_ATTR_UNUSED = vlSymsp->TOPp;
    // Body
    // Change detection
    QData __req = false;  // Logically a bool
    return __req;
}

#ifdef VL_DEBUG
void VSimTop::_eval_debug_assertions() {
    VL_DEBUG_IF(VL_DBG_MSGF("+    VSimTop::_eval_debug_assertions\n"); );
    // Body
    if (VL_UNLIKELY((clock & 0xfeU))) {
        Verilated::overWidthError("clock");}
    if (VL_UNLIKELY((reset & 0xfeU))) {
        Verilated::overWidthError("reset");}
    if (VL_UNLIKELY((io_perfInfo_clean & 0xfeU))) {
        Verilated::overWidthError("io_perfInfo_clean");}
    if (VL_UNLIKELY((io_perfInfo_dump & 0xfeU))) {
        Verilated::overWidthError("io_perfInfo_dump");}
}
#endif  // VL_DEBUG
