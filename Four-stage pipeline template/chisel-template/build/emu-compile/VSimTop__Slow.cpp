// Verilated -*- C++ -*-
// DESCRIPTION: Verilator output: Design implementation internals
// See VSimTop.h for the primary calling header

#include "VSimTop.h"
#include "VSimTop__Syms.h"

#include "verilated_dpi.h"

//==========

VSimTop::VSimTop(VerilatedContext* _vcontextp__, const char* _vcname__)
    : VerilatedModule{_vcname__}
 {
    VSimTop__Syms* __restrict vlSymsp = __VlSymsp = new VSimTop__Syms(_vcontextp__, this, name());
    VSimTop* const __restrict vlTOPp VL_ATTR_UNUSED = vlSymsp->TOPp;
    VL_CELL(__PVT____024unit, VSimTop___024unit);
    // Reset internal values

    // Reset structure values
    _ctor_var_reset(this);
}

void VSimTop::__Vconfigure(VSimTop__Syms* vlSymsp, bool first) {
    if (false && first) {}  // Prevent unused
    this->__VlSymsp = vlSymsp;
    if (false && this->__VlSymsp) {}  // Prevent unused
    vlSymsp->_vm_contextp__->timeunit(-12);
    vlSymsp->_vm_contextp__->timeprecision(-12);
}

VSimTop::~VSimTop() {
    VL_DO_CLEAR(delete __VlSymsp, __VlSymsp = nullptr);
}

void VSimTop::_initial__TOP__1(VSimTop__Syms* __restrict vlSymsp) {
    VL_DEBUG_IF(VL_DBG_MSGF("+    VSimTop::_initial__TOP__1\n"); );
    VSimTop* const __restrict vlTOPp VL_ATTR_UNUSED = vlSymsp->TOPp;
    // Body
    vlTOPp->io_uart_out_valid = 0U;
    vlTOPp->io_uart_out_ch = 0U;
    vlTOPp->io_uart_in_valid = 0U;
    vlTOPp->SimTop__DOT___RAND_0 = VL_RANDOM_I(32);
    vlTOPp->SimTop__DOT__REG = (1U & vlTOPp->SimTop__DOT___RAND_0);
    vlTOPp->SimTop__DOT___RAND_1 = VL_RANDOM_I(32);
    vlTOPp->SimTop__DOT__REG_1 = (1U & vlTOPp->SimTop__DOT___RAND_1);
    vlTOPp->SimTop__DOT___RAND_2 = (((QData)((IData)(
                                                     VL_RANDOM_I(32))) 
                                     << 0x20U) | (QData)((IData)(
                                                                 VL_RANDOM_I(32))));
    vlTOPp->SimTop__DOT__REG_2 = vlTOPp->SimTop__DOT___RAND_2;
    vlTOPp->SimTop__DOT___RAND_3 = (((QData)((IData)(
                                                     VL_RANDOM_I(32))) 
                                     << 0x20U) | (QData)((IData)(
                                                                 VL_RANDOM_I(32))));
    vlTOPp->SimTop__DOT__REG_3 = vlTOPp->SimTop__DOT___RAND_3;
    vlTOPp->SimTop__DOT___RAND_4 = VL_RANDOM_I(32);
    vlTOPp->SimTop__DOT__REG_4 = vlTOPp->SimTop__DOT___RAND_4;
    vlTOPp->SimTop__DOT___RAND_5 = VL_RANDOM_I(32);
    vlTOPp->SimTop__DOT__REG_5 = vlTOPp->SimTop__DOT___RAND_5;
    vlTOPp->SimTop__DOT___RAND_6 = VL_RANDOM_I(32);
    vlTOPp->SimTop__DOT__REG_6 = (1U & vlTOPp->SimTop__DOT___RAND_6);
    vlTOPp->SimTop__DOT___RAND_7 = VL_RANDOM_I(32);
    vlTOPp->SimTop__DOT__REG_7 = (1U & vlTOPp->SimTop__DOT___RAND_7);
    vlTOPp->SimTop__DOT___RAND_8 = (((QData)((IData)(
                                                     VL_RANDOM_I(32))) 
                                     << 0x20U) | (QData)((IData)(
                                                                 VL_RANDOM_I(32))));
    vlTOPp->SimTop__DOT__REG_8 = vlTOPp->SimTop__DOT___RAND_8;
    vlTOPp->SimTop__DOT___RAND_9 = (((QData)((IData)(
                                                     VL_RANDOM_I(32))) 
                                     << 0x20U) | (QData)((IData)(
                                                                 VL_RANDOM_I(32))));
    vlTOPp->SimTop__DOT__REG_9 = vlTOPp->SimTop__DOT___RAND_9;
    vlTOPp->SimTop__DOT___RAND_10 = VL_RANDOM_I(32);
    vlTOPp->SimTop__DOT__REG_10 = (0x1fU & vlTOPp->SimTop__DOT___RAND_10);
    vlTOPp->SimTop__DOT___RAND_11 = VL_RANDOM_I(32);
    vlTOPp->SimTop__DOT__REG_11 = (0x1fU & vlTOPp->SimTop__DOT___RAND_11);
    vlTOPp->SimTop__DOT___RAND_12 = VL_RANDOM_I(32);
    vlTOPp->SimTop__DOT__REG_12 = vlTOPp->SimTop__DOT___RAND_12;
    vlTOPp->SimTop__DOT___RAND_13 = VL_RANDOM_I(32);
    vlTOPp->SimTop__DOT__REG_13 = vlTOPp->SimTop__DOT___RAND_13;
    vlTOPp->SimTop__DOT___RAND_14 = (((QData)((IData)(
                                                      VL_RANDOM_I(32))) 
                                      << 0x20U) | (QData)((IData)(
                                                                  VL_RANDOM_I(32))));
    vlTOPp->SimTop__DOT__REG_14 = vlTOPp->SimTop__DOT___RAND_14;
    vlTOPp->SimTop__DOT___RAND_15 = (((QData)((IData)(
                                                      VL_RANDOM_I(32))) 
                                      << 0x20U) | (QData)((IData)(
                                                                  VL_RANDOM_I(32))));
    vlTOPp->SimTop__DOT__REG_15 = vlTOPp->SimTop__DOT___RAND_15;
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_0 
        = (((QData)((IData)(VL_RANDOM_I(32))) << 0x20U) 
           | (QData)((IData)(VL_RANDOM_I(32))));
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_0 
        = vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_0;
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_1 
        = (((QData)((IData)(VL_RANDOM_I(32))) << 0x20U) 
           | (QData)((IData)(VL_RANDOM_I(32))));
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_1 
        = vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_1;
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_2 
        = (((QData)((IData)(VL_RANDOM_I(32))) << 0x20U) 
           | (QData)((IData)(VL_RANDOM_I(32))));
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_2 
        = vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_2;
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_3 
        = (((QData)((IData)(VL_RANDOM_I(32))) << 0x20U) 
           | (QData)((IData)(VL_RANDOM_I(32))));
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_3 
        = vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_3;
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_4 
        = (((QData)((IData)(VL_RANDOM_I(32))) << 0x20U) 
           | (QData)((IData)(VL_RANDOM_I(32))));
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_4 
        = vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_4;
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_5 
        = (((QData)((IData)(VL_RANDOM_I(32))) << 0x20U) 
           | (QData)((IData)(VL_RANDOM_I(32))));
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_5 
        = vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_5;
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_6 
        = (((QData)((IData)(VL_RANDOM_I(32))) << 0x20U) 
           | (QData)((IData)(VL_RANDOM_I(32))));
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_6 
        = vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_6;
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_7 
        = (((QData)((IData)(VL_RANDOM_I(32))) << 0x20U) 
           | (QData)((IData)(VL_RANDOM_I(32))));
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_7 
        = vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_7;
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_8 
        = (((QData)((IData)(VL_RANDOM_I(32))) << 0x20U) 
           | (QData)((IData)(VL_RANDOM_I(32))));
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_8 
        = vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_8;
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_9 
        = (((QData)((IData)(VL_RANDOM_I(32))) << 0x20U) 
           | (QData)((IData)(VL_RANDOM_I(32))));
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_9 
        = vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_9;
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_10 
        = (((QData)((IData)(VL_RANDOM_I(32))) << 0x20U) 
           | (QData)((IData)(VL_RANDOM_I(32))));
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_10 
        = vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_10;
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_11 
        = (((QData)((IData)(VL_RANDOM_I(32))) << 0x20U) 
           | (QData)((IData)(VL_RANDOM_I(32))));
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_11 
        = vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_11;
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_12 
        = (((QData)((IData)(VL_RANDOM_I(32))) << 0x20U) 
           | (QData)((IData)(VL_RANDOM_I(32))));
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_12 
        = vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_12;
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_13 
        = (((QData)((IData)(VL_RANDOM_I(32))) << 0x20U) 
           | (QData)((IData)(VL_RANDOM_I(32))));
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_13 
        = vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_13;
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_14 
        = (((QData)((IData)(VL_RANDOM_I(32))) << 0x20U) 
           | (QData)((IData)(VL_RANDOM_I(32))));
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_14 
        = vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_14;
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_15 
        = (((QData)((IData)(VL_RANDOM_I(32))) << 0x20U) 
           | (QData)((IData)(VL_RANDOM_I(32))));
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_15 
        = vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_15;
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_16 
        = (((QData)((IData)(VL_RANDOM_I(32))) << 0x20U) 
           | (QData)((IData)(VL_RANDOM_I(32))));
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_16 
        = vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_16;
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_17 
        = (((QData)((IData)(VL_RANDOM_I(32))) << 0x20U) 
           | (QData)((IData)(VL_RANDOM_I(32))));
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_17 
        = vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_17;
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_18 
        = (((QData)((IData)(VL_RANDOM_I(32))) << 0x20U) 
           | (QData)((IData)(VL_RANDOM_I(32))));
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_18 
        = vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_18;
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_19 
        = (((QData)((IData)(VL_RANDOM_I(32))) << 0x20U) 
           | (QData)((IData)(VL_RANDOM_I(32))));
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_19 
        = vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_19;
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_20 
        = (((QData)((IData)(VL_RANDOM_I(32))) << 0x20U) 
           | (QData)((IData)(VL_RANDOM_I(32))));
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_20 
        = vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_20;
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_21 
        = (((QData)((IData)(VL_RANDOM_I(32))) << 0x20U) 
           | (QData)((IData)(VL_RANDOM_I(32))));
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_21 
        = vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_21;
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_22 
        = (((QData)((IData)(VL_RANDOM_I(32))) << 0x20U) 
           | (QData)((IData)(VL_RANDOM_I(32))));
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_22 
        = vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_22;
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_23 
        = (((QData)((IData)(VL_RANDOM_I(32))) << 0x20U) 
           | (QData)((IData)(VL_RANDOM_I(32))));
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_23 
        = vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_23;
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_24 
        = (((QData)((IData)(VL_RANDOM_I(32))) << 0x20U) 
           | (QData)((IData)(VL_RANDOM_I(32))));
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_24 
        = vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_24;
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_25 
        = (((QData)((IData)(VL_RANDOM_I(32))) << 0x20U) 
           | (QData)((IData)(VL_RANDOM_I(32))));
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_25 
        = vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_25;
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_26 
        = (((QData)((IData)(VL_RANDOM_I(32))) << 0x20U) 
           | (QData)((IData)(VL_RANDOM_I(32))));
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_26 
        = vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_26;
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_27 
        = (((QData)((IData)(VL_RANDOM_I(32))) << 0x20U) 
           | (QData)((IData)(VL_RANDOM_I(32))));
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_27 
        = vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_27;
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_28 
        = (((QData)((IData)(VL_RANDOM_I(32))) << 0x20U) 
           | (QData)((IData)(VL_RANDOM_I(32))));
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_28 
        = vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_28;
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_29 
        = (((QData)((IData)(VL_RANDOM_I(32))) << 0x20U) 
           | (QData)((IData)(VL_RANDOM_I(32))));
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_29 
        = vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_29;
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_30 
        = (((QData)((IData)(VL_RANDOM_I(32))) << 0x20U) 
           | (QData)((IData)(VL_RANDOM_I(32))));
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_30 
        = vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_30;
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_31 
        = (((QData)((IData)(VL_RANDOM_I(32))) << 0x20U) 
           | (QData)((IData)(VL_RANDOM_I(32))));
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_31 
        = vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_31;
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_32 
        = (((QData)((IData)(VL_RANDOM_I(32))) << 0x20U) 
           | (QData)((IData)(VL_RANDOM_I(32))));
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__REG_0 
        = vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_32;
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_33 
        = (((QData)((IData)(VL_RANDOM_I(32))) << 0x20U) 
           | (QData)((IData)(VL_RANDOM_I(32))));
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__REG_1 
        = vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_33;
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_34 
        = (((QData)((IData)(VL_RANDOM_I(32))) << 0x20U) 
           | (QData)((IData)(VL_RANDOM_I(32))));
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__REG_2 
        = vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_34;
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_35 
        = (((QData)((IData)(VL_RANDOM_I(32))) << 0x20U) 
           | (QData)((IData)(VL_RANDOM_I(32))));
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__REG_3 
        = vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_35;
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_36 
        = (((QData)((IData)(VL_RANDOM_I(32))) << 0x20U) 
           | (QData)((IData)(VL_RANDOM_I(32))));
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__REG_4 
        = vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_36;
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_37 
        = (((QData)((IData)(VL_RANDOM_I(32))) << 0x20U) 
           | (QData)((IData)(VL_RANDOM_I(32))));
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__REG_5 
        = vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_37;
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_38 
        = (((QData)((IData)(VL_RANDOM_I(32))) << 0x20U) 
           | (QData)((IData)(VL_RANDOM_I(32))));
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__REG_6 
        = vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_38;
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_39 
        = (((QData)((IData)(VL_RANDOM_I(32))) << 0x20U) 
           | (QData)((IData)(VL_RANDOM_I(32))));
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__REG_7 
        = vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_39;
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_40 
        = (((QData)((IData)(VL_RANDOM_I(32))) << 0x20U) 
           | (QData)((IData)(VL_RANDOM_I(32))));
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__REG_8 
        = vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_40;
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_41 
        = (((QData)((IData)(VL_RANDOM_I(32))) << 0x20U) 
           | (QData)((IData)(VL_RANDOM_I(32))));
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__REG_9 
        = vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_41;
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_42 
        = (((QData)((IData)(VL_RANDOM_I(32))) << 0x20U) 
           | (QData)((IData)(VL_RANDOM_I(32))));
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__REG_10 
        = vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_42;
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_43 
        = (((QData)((IData)(VL_RANDOM_I(32))) << 0x20U) 
           | (QData)((IData)(VL_RANDOM_I(32))));
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__REG_11 
        = vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_43;
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_44 
        = (((QData)((IData)(VL_RANDOM_I(32))) << 0x20U) 
           | (QData)((IData)(VL_RANDOM_I(32))));
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__REG_12 
        = vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_44;
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_45 
        = (((QData)((IData)(VL_RANDOM_I(32))) << 0x20U) 
           | (QData)((IData)(VL_RANDOM_I(32))));
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__REG_13 
        = vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_45;
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_46 
        = (((QData)((IData)(VL_RANDOM_I(32))) << 0x20U) 
           | (QData)((IData)(VL_RANDOM_I(32))));
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__REG_14 
        = vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_46;
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_47 
        = (((QData)((IData)(VL_RANDOM_I(32))) << 0x20U) 
           | (QData)((IData)(VL_RANDOM_I(32))));
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__REG_15 
        = vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_47;
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_48 
        = (((QData)((IData)(VL_RANDOM_I(32))) << 0x20U) 
           | (QData)((IData)(VL_RANDOM_I(32))));
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__REG_16 
        = vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_48;
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_49 
        = (((QData)((IData)(VL_RANDOM_I(32))) << 0x20U) 
           | (QData)((IData)(VL_RANDOM_I(32))));
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__REG_17 
        = vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_49;
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_50 
        = (((QData)((IData)(VL_RANDOM_I(32))) << 0x20U) 
           | (QData)((IData)(VL_RANDOM_I(32))));
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__REG_18 
        = vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_50;
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_51 
        = (((QData)((IData)(VL_RANDOM_I(32))) << 0x20U) 
           | (QData)((IData)(VL_RANDOM_I(32))));
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__REG_19 
        = vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_51;
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_52 
        = (((QData)((IData)(VL_RANDOM_I(32))) << 0x20U) 
           | (QData)((IData)(VL_RANDOM_I(32))));
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__REG_20 
        = vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_52;
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_53 
        = (((QData)((IData)(VL_RANDOM_I(32))) << 0x20U) 
           | (QData)((IData)(VL_RANDOM_I(32))));
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__REG_21 
        = vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_53;
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_54 
        = (((QData)((IData)(VL_RANDOM_I(32))) << 0x20U) 
           | (QData)((IData)(VL_RANDOM_I(32))));
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__REG_22 
        = vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_54;
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_55 
        = (((QData)((IData)(VL_RANDOM_I(32))) << 0x20U) 
           | (QData)((IData)(VL_RANDOM_I(32))));
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__REG_23 
        = vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_55;
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_56 
        = (((QData)((IData)(VL_RANDOM_I(32))) << 0x20U) 
           | (QData)((IData)(VL_RANDOM_I(32))));
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__REG_24 
        = vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_56;
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_57 
        = (((QData)((IData)(VL_RANDOM_I(32))) << 0x20U) 
           | (QData)((IData)(VL_RANDOM_I(32))));
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__REG_25 
        = vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_57;
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_58 
        = (((QData)((IData)(VL_RANDOM_I(32))) << 0x20U) 
           | (QData)((IData)(VL_RANDOM_I(32))));
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__REG_26 
        = vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_58;
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_59 
        = (((QData)((IData)(VL_RANDOM_I(32))) << 0x20U) 
           | (QData)((IData)(VL_RANDOM_I(32))));
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__REG_27 
        = vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_59;
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_60 
        = (((QData)((IData)(VL_RANDOM_I(32))) << 0x20U) 
           | (QData)((IData)(VL_RANDOM_I(32))));
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__REG_28 
        = vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_60;
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_61 
        = (((QData)((IData)(VL_RANDOM_I(32))) << 0x20U) 
           | (QData)((IData)(VL_RANDOM_I(32))));
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__REG_29 
        = vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_61;
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_62 
        = (((QData)((IData)(VL_RANDOM_I(32))) << 0x20U) 
           | (QData)((IData)(VL_RANDOM_I(32))));
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__REG_30 
        = vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_62;
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_63 
        = (((QData)((IData)(VL_RANDOM_I(32))) << 0x20U) 
           | (QData)((IData)(VL_RANDOM_I(32))));
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__REG_31 
        = vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_63;
    vlTOPp->SimTop__DOT__rvcore__DOT__ifu__DOT___RAND_0 
        = (((QData)((IData)(VL_RANDOM_I(32))) << 0x20U) 
           | (QData)((IData)(VL_RANDOM_I(32))));
    vlTOPp->SimTop__DOT__rvcore__DOT__ifu__DOT__pc 
        = vlTOPp->SimTop__DOT__rvcore__DOT__ifu__DOT___RAND_0;
}

void VSimTop::_settle__TOP__3(VSimTop__Syms* __restrict vlSymsp) {
    VL_DEBUG_IF(VL_DBG_MSGF("+    VSimTop::_settle__TOP__3\n"); );
    VSimTop* const __restrict vlTOPp VL_ATTR_UNUSED = vlSymsp->TOPp;
    // Variables
    VlWide<4>/*127:0*/ __Vtemp2;
    VlWide<4>/*127:0*/ __Vtemp3;
    VlWide<4>/*127:0*/ __Vtemp5;
    VlWide<4>/*127:0*/ __Vtemp6;
    VlWide<4>/*127:0*/ __Vtemp7;
    VlWide<4>/*127:0*/ __Vtemp8;
    VlWide<4>/*127:0*/ __Vtemp9;
    VlWide<4>/*127:0*/ __Vtemp10;
    VlWide<4>/*127:0*/ __Vtemp11;
    VlWide<4>/*127:0*/ __Vtemp12;
    VlWide<4>/*127:0*/ __Vtemp13;
    VlWide<4>/*127:0*/ __Vtemp14;
    VlWide<4>/*127:0*/ __Vtemp15;
    VlWide<4>/*127:0*/ __Vtemp16;
    VlWide<4>/*127:0*/ __Vtemp43;
    VlWide<4>/*127:0*/ __Vtemp44;
    // Body
    vlTOPp->SimTop__DOT__rvcore__DOT__ifu__DOT___pc_T_1 
        = (4ULL + vlTOPp->SimTop__DOT__rvcore__DOT__ifu__DOT__pc);
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
    vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_src1Type 
        = (((6U == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu__DOT__instrType)) 
            | (7U == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu__DOT__instrType))) 
           | (0U == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu__DOT__instrType)));
    vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_src2Type 
        = ((((4U == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu__DOT__instrType)) 
             | (6U == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu__DOT__instrType))) 
            | (7U == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu__DOT__instrType))) 
           | (0U == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu__DOT__instrType)));
    vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__bru__DOT___io_out_newPC_T_4 
        = (vlTOPp->SimTop__DOT__rvcore__DOT__ifu__DOT__pc 
           + vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_data_imm);
    vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfSrc1 
        = ((IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_src1Type)
            ? 0U : (0x1fU & (vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr 
                             >> 0xfU)));
    vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfSrc2 
        = ((IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_src2Type)
            ? 0U : (0x1fU & (vlTOPp->SimTop__DOT__rvcore__DOT__ifu_io_out_instr 
                             >> 0x14U)));
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___GEN_117 
        = ((0x15U == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfSrc1))
            ? vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_21
            : ((0x14U == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfSrc1))
                ? vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_20
                : ((0x13U == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfSrc1))
                    ? vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_19
                    : ((0x12U == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfSrc1))
                        ? vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_18
                        : ((0x11U == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfSrc1))
                            ? vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_17
                            : ((0x10U == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfSrc1))
                                ? vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_16
                                : ((0xfU == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfSrc1))
                                    ? vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_15
                                    : ((0xeU == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfSrc1))
                                        ? vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_14
                                        : ((0xdU == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfSrc1))
                                            ? vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_13
                                            : ((0xcU 
                                                == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfSrc1))
                                                ? vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_12
                                                : (
                                                   (0xbU 
                                                    == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfSrc1))
                                                    ? vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_11
                                                    : 
                                                   ((0xaU 
                                                     == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfSrc1))
                                                     ? vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_10
                                                     : 
                                                    ((9U 
                                                      == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfSrc1))
                                                      ? vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_9
                                                      : 
                                                     ((8U 
                                                       == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfSrc1))
                                                       ? vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_8
                                                       : 
                                                      ((7U 
                                                        == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfSrc1))
                                                        ? vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_7
                                                        : 
                                                       ((6U 
                                                         == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfSrc1))
                                                         ? vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_6
                                                         : 
                                                        ((5U 
                                                          == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfSrc1))
                                                          ? vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_5
                                                          : 
                                                         ((4U 
                                                           == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfSrc1))
                                                           ? vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_4
                                                           : 
                                                          ((3U 
                                                            == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfSrc1))
                                                            ? vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_3
                                                            : 
                                                           ((2U 
                                                             == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfSrc1))
                                                             ? vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_2
                                                             : 
                                                            ((1U 
                                                              == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfSrc1))
                                                              ? vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_1
                                                              : vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_0)))))))))))))))))))));
    vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___GEN_149 
        = ((0x15U == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfSrc2))
            ? vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_21
            : ((0x14U == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfSrc2))
                ? vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_20
                : ((0x13U == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfSrc2))
                    ? vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_19
                    : ((0x12U == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfSrc2))
                        ? vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_18
                        : ((0x11U == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfSrc2))
                            ? vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_17
                            : ((0x10U == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfSrc2))
                                ? vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_16
                                : ((0xfU == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfSrc2))
                                    ? vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_15
                                    : ((0xeU == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfSrc2))
                                        ? vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_14
                                        : ((0xdU == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfSrc2))
                                            ? vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_13
                                            : ((0xcU 
                                                == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfSrc2))
                                                ? vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_12
                                                : (
                                                   (0xbU 
                                                    == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfSrc2))
                                                    ? vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_11
                                                    : 
                                                   ((0xaU 
                                                     == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfSrc2))
                                                     ? vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_10
                                                     : 
                                                    ((9U 
                                                      == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfSrc2))
                                                      ? vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_9
                                                      : 
                                                     ((8U 
                                                       == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfSrc2))
                                                       ? vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_8
                                                       : 
                                                      ((7U 
                                                        == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfSrc2))
                                                        ? vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_7
                                                        : 
                                                       ((6U 
                                                         == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfSrc2))
                                                         ? vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_6
                                                         : 
                                                        ((5U 
                                                          == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfSrc2))
                                                          ? vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_5
                                                          : 
                                                         ((4U 
                                                           == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfSrc2))
                                                           ? vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_4
                                                           : 
                                                          ((3U 
                                                            == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfSrc2))
                                                            ? vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_3
                                                            : 
                                                           ((2U 
                                                             == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfSrc2))
                                                             ? vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_2
                                                             : 
                                                            ((1U 
                                                              == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfSrc2))
                                                              ? vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_1
                                                              : vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_0)))))))))))))))))))));
    vlTOPp->SimTop__DOT__rvcore__DOT__dis_io_out_data_src1 
        = (((IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_src1Type)
             ? 0ULL : ((0x1fU == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfSrc1))
                        ? vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_31
                        : ((0x1eU == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfSrc1))
                            ? vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_30
                            : ((0x1dU == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfSrc1))
                                ? vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_29
                                : ((0x1cU == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfSrc1))
                                    ? vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_28
                                    : ((0x1bU == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfSrc1))
                                        ? vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_27
                                        : ((0x1aU == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfSrc1))
                                            ? vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_26
                                            : ((0x19U 
                                                == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfSrc1))
                                                ? vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_25
                                                : (
                                                   (0x18U 
                                                    == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfSrc1))
                                                    ? vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_24
                                                    : 
                                                   ((0x17U 
                                                     == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfSrc1))
                                                     ? vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_23
                                                     : 
                                                    ((0x16U 
                                                      == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfSrc1))
                                                      ? vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_22
                                                      : vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___GEN_117))))))))))) 
           | ((IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_src1Type)
               ? vlTOPp->SimTop__DOT__rvcore__DOT__ifu__DOT__pc
               : 0ULL));
    vlTOPp->SimTop__DOT__rvcore__DOT__dis_io_out_data_src2 
        = (((IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_src2Type)
             ? 0ULL : ((0x1fU == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfSrc2))
                        ? vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_31
                        : ((0x1eU == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfSrc2))
                            ? vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_30
                            : ((0x1dU == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfSrc2))
                                ? vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_29
                                : ((0x1cU == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfSrc2))
                                    ? vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_28
                                    : ((0x1bU == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfSrc2))
                                        ? vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_27
                                        : ((0x1aU == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfSrc2))
                                            ? vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_26
                                            : ((0x19U 
                                                == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfSrc2))
                                                ? vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_25
                                                : (
                                                   (0x18U 
                                                    == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfSrc2))
                                                    ? vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_24
                                                    : 
                                                   ((0x17U 
                                                     == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfSrc2))
                                                     ? vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_23
                                                     : 
                                                    ((0x16U 
                                                      == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfSrc2))
                                                      ? vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT__regs_22
                                                      : vlTOPp->SimTop__DOT__rvcore__DOT__reg___DOT___GEN_149))))))))))) 
           | ((IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_src2Type)
               ? vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_data_imm
               : 0ULL));
    vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__lsu__DOT__addr 
        = (vlTOPp->SimTop__DOT__rvcore__DOT__dis_io_out_data_src1 
           + vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_data_imm);
    vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__alu__DOT___T_1 
        = (vlTOPp->SimTop__DOT__rvcore__DOT__dis_io_out_data_src1 
           + vlTOPp->SimTop__DOT__rvcore__DOT__dis_io_out_data_src2);
    vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__alu__DOT___T_12 
        = (vlTOPp->SimTop__DOT__rvcore__DOT__dis_io_out_data_src1 
           - vlTOPp->SimTop__DOT__rvcore__DOT__dis_io_out_data_src2);
    vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__alu__DOT__shamt 
        = (0x3fU & ((0x20U & (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                     ? (0x1fU & (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__dis_io_out_data_src2))
                     : (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__dis_io_out_data_src2)));
    vlSymsp->TOP____024unit.__Vdpiimwrap_ram_read_helper_TOP____024unit(
                                                                        (1U 
                                                                         == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcType)), 
                                                                        (0x1fffffffffffffffULL 
                                                                         & ((vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__lsu__DOT__addr 
                                                                             - 0x80000000ULL) 
                                                                            >> 3U)), vlTOPp->__Vfunc_ram_read_helper__2__Vfuncout);
    vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__lsu__DOT__ram_rdata 
        = vlTOPp->__Vfunc_ram_read_helper__2__Vfuncout;
    VL_EXTEND_WQ(127,64, __Vtemp2, vlTOPp->SimTop__DOT__rvcore__DOT__dis_io_out_data_src1);
    VL_SHIFTL_WWI(127,127,6, __Vtemp3, __Vtemp2, (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__alu__DOT__shamt));
    vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__alu__DOT___T_2[0U] 
        = __Vtemp3[0U];
    vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__alu__DOT___T_2[1U] 
        = __Vtemp3[1U];
    vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__alu__DOT___T_2[2U] 
        = __Vtemp3[2U];
    vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__alu__DOT___T_2[3U] 
        = (0x7fffffffU & __Vtemp3[3U]);
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
    VL_EXTEND_WI(127,32, __Vtemp5, ((0x1fU >= (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__alu__DOT__shamt))
                                     ? ((IData)(vlTOPp->SimTop__DOT__rvcore__DOT__dis_io_out_data_src1) 
                                        >> (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__alu__DOT__shamt))
                                     : 0U));
    VL_EXTEND_WQ(127,64, __Vtemp6, vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__alu__DOT___T_12);
    VL_EXTEND_WQ(127,64, __Vtemp7, vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__alu__DOT___T_1);
    VL_EXTEND_WQ(127,64, __Vtemp8, VL_SHIFTRS_QQI(64,64,6, vlTOPp->SimTop__DOT__rvcore__DOT__dis_io_out_data_src1, (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__alu__DOT__shamt)));
    VL_EXTEND_WQ(127,64, __Vtemp9, vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__alu__DOT___T_12);
    VL_EXTEND_WQ(127,64, __Vtemp10, (vlTOPp->SimTop__DOT__rvcore__DOT__dis_io_out_data_src1 
                                     & vlTOPp->SimTop__DOT__rvcore__DOT__dis_io_out_data_src2));
    VL_EXTEND_WQ(127,64, __Vtemp11, (vlTOPp->SimTop__DOT__rvcore__DOT__dis_io_out_data_src1 
                                     | vlTOPp->SimTop__DOT__rvcore__DOT__dis_io_out_data_src2));
    VL_EXTEND_WQ(127,64, __Vtemp12, (vlTOPp->SimTop__DOT__rvcore__DOT__dis_io_out_data_src1 
                                     >> (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__alu__DOT__shamt)));
    VL_EXTEND_WQ(127,64, __Vtemp13, (vlTOPp->SimTop__DOT__rvcore__DOT__dis_io_out_data_src1 
                                     ^ vlTOPp->SimTop__DOT__rvcore__DOT__dis_io_out_data_src2));
    VL_EXTEND_WI(127,1, __Vtemp14, (vlTOPp->SimTop__DOT__rvcore__DOT__dis_io_out_data_src1 
                                    < vlTOPp->SimTop__DOT__rvcore__DOT__dis_io_out_data_src2));
    VL_EXTEND_WI(127,1, __Vtemp15, VL_LTS_IQQ(1,64,64, vlTOPp->SimTop__DOT__rvcore__DOT__dis_io_out_data_src1, vlTOPp->SimTop__DOT__rvcore__DOT__dis_io_out_data_src2));
    VL_EXTEND_WQ(127,64, __Vtemp16, ((0x40U == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                                      ? vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__alu__DOT___T_1
                                      : 0ULL));
    if ((0x25U == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))) {
        vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__alu__DOT___res_T_27[0U] 
            = __Vtemp5[0U];
        vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__alu__DOT___res_T_27[1U] 
            = __Vtemp5[1U];
        vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__alu__DOT___res_T_27[2U] 
            = __Vtemp5[2U];
        vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__alu__DOT___res_T_27[3U] 
            = __Vtemp5[3U];
    } else {
        vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__alu__DOT___res_T_27[0U] 
            = ((0x21U == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                ? vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__alu__DOT___T_2[0U]
                : ((0x28U == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                    ? __Vtemp6[0U] : ((0x60U == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                                       ? __Vtemp7[0U]
                                       : ((0xdU == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                                           ? __Vtemp8[0U]
                                           : ((8U == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                                               ? __Vtemp9[0U]
                                               : ((7U 
                                                   == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                                                   ? 
                                                  __Vtemp10[0U]
                                                   : 
                                                  ((6U 
                                                    == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                                                    ? 
                                                   __Vtemp11[0U]
                                                    : 
                                                   ((5U 
                                                     == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                                                     ? 
                                                    __Vtemp12[0U]
                                                     : 
                                                    ((4U 
                                                      == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                                                      ? 
                                                     __Vtemp13[0U]
                                                      : 
                                                     ((3U 
                                                       == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                                                       ? 
                                                      __Vtemp14[0U]
                                                       : 
                                                      ((2U 
                                                        == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                                                        ? 
                                                       __Vtemp15[0U]
                                                        : 
                                                       ((1U 
                                                         == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                                                         ? 
                                                        vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__alu__DOT___T_2[0U]
                                                         : 
                                                        __Vtemp16[0U]))))))))))));
        vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__alu__DOT___res_T_27[1U] 
            = ((0x21U == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                ? vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__alu__DOT___T_2[1U]
                : ((0x28U == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                    ? __Vtemp6[1U] : ((0x60U == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                                       ? __Vtemp7[1U]
                                       : ((0xdU == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                                           ? __Vtemp8[1U]
                                           : ((8U == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                                               ? __Vtemp9[1U]
                                               : ((7U 
                                                   == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                                                   ? 
                                                  __Vtemp10[1U]
                                                   : 
                                                  ((6U 
                                                    == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                                                    ? 
                                                   __Vtemp11[1U]
                                                    : 
                                                   ((5U 
                                                     == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                                                     ? 
                                                    __Vtemp12[1U]
                                                     : 
                                                    ((4U 
                                                      == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                                                      ? 
                                                     __Vtemp13[1U]
                                                      : 
                                                     ((3U 
                                                       == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                                                       ? 
                                                      __Vtemp14[1U]
                                                       : 
                                                      ((2U 
                                                        == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                                                        ? 
                                                       __Vtemp15[1U]
                                                        : 
                                                       ((1U 
                                                         == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                                                         ? 
                                                        vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__alu__DOT___T_2[1U]
                                                         : 
                                                        __Vtemp16[1U]))))))))))));
        vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__alu__DOT___res_T_27[2U] 
            = ((0x21U == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                ? vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__alu__DOT___T_2[2U]
                : ((0x28U == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                    ? __Vtemp6[2U] : ((0x60U == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                                       ? __Vtemp7[2U]
                                       : ((0xdU == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                                           ? __Vtemp8[2U]
                                           : ((8U == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                                               ? __Vtemp9[2U]
                                               : ((7U 
                                                   == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                                                   ? 
                                                  __Vtemp10[2U]
                                                   : 
                                                  ((6U 
                                                    == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                                                    ? 
                                                   __Vtemp11[2U]
                                                    : 
                                                   ((5U 
                                                     == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                                                     ? 
                                                    __Vtemp12[2U]
                                                     : 
                                                    ((4U 
                                                      == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                                                      ? 
                                                     __Vtemp13[2U]
                                                      : 
                                                     ((3U 
                                                       == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                                                       ? 
                                                      __Vtemp14[2U]
                                                       : 
                                                      ((2U 
                                                        == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                                                        ? 
                                                       __Vtemp15[2U]
                                                        : 
                                                       ((1U 
                                                         == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                                                         ? 
                                                        vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__alu__DOT___T_2[2U]
                                                         : 
                                                        __Vtemp16[2U]))))))))))));
        vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__alu__DOT___res_T_27[3U] 
            = ((0x21U == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                ? vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__alu__DOT___T_2[3U]
                : ((0x28U == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                    ? __Vtemp6[3U] : ((0x60U == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                                       ? __Vtemp7[3U]
                                       : ((0xdU == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                                           ? __Vtemp8[3U]
                                           : ((8U == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                                               ? __Vtemp9[3U]
                                               : ((7U 
                                                   == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                                                   ? 
                                                  __Vtemp10[3U]
                                                   : 
                                                  ((6U 
                                                    == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                                                    ? 
                                                   __Vtemp11[3U]
                                                    : 
                                                   ((5U 
                                                     == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                                                     ? 
                                                    __Vtemp12[3U]
                                                     : 
                                                    ((4U 
                                                      == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                                                      ? 
                                                     __Vtemp13[3U]
                                                      : 
                                                     ((3U 
                                                       == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                                                       ? 
                                                      __Vtemp14[3U]
                                                       : 
                                                      ((2U 
                                                        == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                                                        ? 
                                                       __Vtemp15[3U]
                                                        : 
                                                       ((1U 
                                                         == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                                                         ? 
                                                        vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__alu__DOT___T_2[3U]
                                                         : 
                                                        __Vtemp16[3U]))))))))))));
    }
    VL_EXTEND_WQ(127,64, __Vtemp43, vlTOPp->SimTop__DOT__rvcore__DOT__dis_io_out_data_src2);
    VL_EXTEND_WI(127,32, __Vtemp44, ((0x1fU >= (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__alu__DOT__shamt))
                                      ? VL_SHIFTRS_III(32,32,6, (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__dis_io_out_data_src1), (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__alu__DOT__shamt))
                                      : (- ((IData)(vlTOPp->SimTop__DOT__rvcore__DOT__dis_io_out_data_src1) 
                                            >> 0x1fU))));
    if ((0xfU == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))) {
        vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__alu__DOT___res_T_31[0U] 
            = __Vtemp43[0U];
        vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__alu__DOT___res_T_31[1U] 
            = __Vtemp43[1U];
        vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__alu__DOT___res_T_31[2U] 
            = __Vtemp43[2U];
        vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__alu__DOT___res_T_31[3U] 
            = __Vtemp43[3U];
    } else {
        vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__alu__DOT___res_T_31[0U] 
            = ((0x2dU == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                ? __Vtemp44[0U] : vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__alu__DOT___res_T_27[0U]);
        vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__alu__DOT___res_T_31[1U] 
            = ((0x2dU == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                ? __Vtemp44[1U] : vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__alu__DOT___res_T_27[1U]);
        vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__alu__DOT___res_T_31[2U] 
            = ((0x2dU == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                ? __Vtemp44[2U] : vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__alu__DOT___res_T_27[2U]);
        vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__alu__DOT___res_T_31[3U] 
            = ((0x2dU == (IData)(vlTOPp->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType))
                ? __Vtemp44[3U] : vlTOPp->SimTop__DOT__rvcore__DOT__exu__DOT__alu__DOT___res_T_27[3U]);
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

void VSimTop::_eval_initial(VSimTop__Syms* __restrict vlSymsp) {
    VL_DEBUG_IF(VL_DBG_MSGF("+    VSimTop::_eval_initial\n"); );
    VSimTop* const __restrict vlTOPp VL_ATTR_UNUSED = vlSymsp->TOPp;
    // Body
    vlTOPp->_initial__TOP__1(vlSymsp);
    vlTOPp->__Vclklast__TOP__clock = vlTOPp->clock;
}

void VSimTop::final() {
    VL_DEBUG_IF(VL_DBG_MSGF("+    VSimTop::final\n"); );
    // Variables
    VSimTop__Syms* __restrict vlSymsp = this->__VlSymsp;
    VSimTop* const __restrict vlTOPp VL_ATTR_UNUSED = vlSymsp->TOPp;
}

void VSimTop::_eval_settle(VSimTop__Syms* __restrict vlSymsp) {
    VL_DEBUG_IF(VL_DBG_MSGF("+    VSimTop::_eval_settle\n"); );
    VSimTop* const __restrict vlTOPp VL_ATTR_UNUSED = vlSymsp->TOPp;
    // Body
    vlTOPp->_settle__TOP__3(vlSymsp);
}

void VSimTop::_ctor_var_reset(VSimTop* self) {
    VL_DEBUG_IF(VL_DBG_MSGF("+    VSimTop::_ctor_var_reset\n"); );
    // Body
    if (false && self) {}  // Prevent unused
    self->clock = VL_RAND_RESET_I(1);
    self->reset = VL_RAND_RESET_I(1);
    self->io_logCtrl_log_begin = VL_RAND_RESET_Q(64);
    self->io_logCtrl_log_end = VL_RAND_RESET_Q(64);
    self->io_logCtrl_log_level = VL_RAND_RESET_Q(64);
    self->io_perfInfo_clean = VL_RAND_RESET_I(1);
    self->io_perfInfo_dump = VL_RAND_RESET_I(1);
    self->io_uart_out_valid = VL_RAND_RESET_I(1);
    self->io_uart_out_ch = VL_RAND_RESET_I(8);
    self->io_uart_in_valid = VL_RAND_RESET_I(1);
    self->io_uart_in_ch = VL_RAND_RESET_I(8);
    self->SimTop__DOT___RAND_0 = VL_RAND_RESET_I(32);
    self->SimTop__DOT___RAND_1 = VL_RAND_RESET_I(32);
    self->SimTop__DOT___RAND_2 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT___RAND_3 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT___RAND_4 = VL_RAND_RESET_I(32);
    self->SimTop__DOT___RAND_5 = VL_RAND_RESET_I(32);
    self->SimTop__DOT___RAND_6 = VL_RAND_RESET_I(32);
    self->SimTop__DOT___RAND_7 = VL_RAND_RESET_I(32);
    self->SimTop__DOT___RAND_8 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT___RAND_9 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT___RAND_10 = VL_RAND_RESET_I(32);
    self->SimTop__DOT___RAND_11 = VL_RAND_RESET_I(32);
    self->SimTop__DOT___RAND_12 = VL_RAND_RESET_I(32);
    self->SimTop__DOT___RAND_13 = VL_RAND_RESET_I(32);
    self->SimTop__DOT___RAND_14 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT___RAND_15 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__REG = VL_RAND_RESET_I(1);
    self->SimTop__DOT__REG_1 = VL_RAND_RESET_I(1);
    self->SimTop__DOT__REG_2 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__REG_3 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__REG_4 = VL_RAND_RESET_I(32);
    self->SimTop__DOT__REG_5 = VL_RAND_RESET_I(32);
    self->SimTop__DOT__REG_6 = VL_RAND_RESET_I(1);
    self->SimTop__DOT__REG_7 = VL_RAND_RESET_I(1);
    self->SimTop__DOT__REG_8 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__REG_9 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__REG_10 = VL_RAND_RESET_I(5);
    self->SimTop__DOT__REG_11 = VL_RAND_RESET_I(5);
    self->SimTop__DOT__REG_12 = VL_RAND_RESET_I(32);
    self->SimTop__DOT__REG_13 = VL_RAND_RESET_I(32);
    self->SimTop__DOT__REG_14 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__REG_15 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__ifu_io_out_instr = VL_RAND_RESET_I(32);
    self->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_src1Type = VL_RAND_RESET_I(1);
    self->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_src2Type = VL_RAND_RESET_I(1);
    self->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcType = VL_RAND_RESET_I(3);
    self->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType = VL_RAND_RESET_I(7);
    self->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfSrc1 = VL_RAND_RESET_I(5);
    self->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfSrc2 = VL_RAND_RESET_I(5);
    self->SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfrd = VL_RAND_RESET_I(5);
    self->SimTop__DOT__rvcore__DOT__idu_io_out_data_imm = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__dis_io_out_data_src1 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__dis_io_out_data_src2 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__exu_io_reg_write_back_data = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__exu_io_reg_write_back_ena = VL_RAND_RESET_I(1);
    self->SimTop__DOT__rvcore__DOT__ifu__DOT___RAND_0 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__ifu__DOT__ram_rdata = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__ifu__DOT__pc = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__ifu__DOT___pc_T_1 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__idu__DOT___T_112 = VL_RAND_RESET_I(3);
    self->SimTop__DOT__rvcore__DOT__idu__DOT___T_127 = VL_RAND_RESET_I(3);
    self->SimTop__DOT__rvcore__DOT__idu__DOT___T_142 = VL_RAND_RESET_I(3);
    self->SimTop__DOT__rvcore__DOT__idu__DOT__instrType = VL_RAND_RESET_I(3);
    self->SimTop__DOT__rvcore__DOT__idu__DOT___T_165 = VL_RAND_RESET_I(1);
    self->SimTop__DOT__rvcore__DOT__idu__DOT___T_180 = VL_RAND_RESET_I(3);
    self->SimTop__DOT__rvcore__DOT__idu__DOT___T_208 = VL_RAND_RESET_I(7);
    self->SimTop__DOT__rvcore__DOT__idu__DOT___T_223 = VL_RAND_RESET_I(7);
    self->SimTop__DOT__rvcore__DOT__idu__DOT___T_238 = VL_RAND_RESET_I(7);
    self->SimTop__DOT__rvcore__DOT__exu__DOT__alu__DOT__shamt = VL_RAND_RESET_I(6);
    self->SimTop__DOT__rvcore__DOT__exu__DOT__alu__DOT___T_1 = VL_RAND_RESET_Q(64);
    VL_RAND_RESET_W(127, self->SimTop__DOT__rvcore__DOT__exu__DOT__alu__DOT___T_2);
    self->SimTop__DOT__rvcore__DOT__exu__DOT__alu__DOT___T_12 = VL_RAND_RESET_Q(64);
    VL_RAND_RESET_W(127, self->SimTop__DOT__rvcore__DOT__exu__DOT__alu__DOT___res_T_27);
    VL_RAND_RESET_W(127, self->SimTop__DOT__rvcore__DOT__exu__DOT__alu__DOT___res_T_31);
    self->SimTop__DOT__rvcore__DOT__exu__DOT__lsu__DOT__ram_rdata = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__exu__DOT__lsu__DOT__addr = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__exu__DOT__lsu__DOT__rdataSel = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__exu__DOT__bru__DOT___io_out_newPC_T_4 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_0 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_1 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_2 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_3 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_4 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_5 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_6 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_7 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_8 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_9 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_10 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_11 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_12 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_13 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_14 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_15 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_16 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_17 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_18 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_19 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_20 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_21 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_22 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_23 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_24 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_25 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_26 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_27 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_28 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_29 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_30 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_31 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_32 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_33 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_34 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_35 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_36 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_37 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_38 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_39 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_40 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_41 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_42 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_43 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_44 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_45 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_46 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_47 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_48 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_49 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_50 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_51 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_52 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_53 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_54 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_55 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_56 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_57 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_58 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_59 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_60 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_61 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_62 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT___RAND_63 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT__regs_0 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT__regs_1 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT__regs_2 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT__regs_3 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT__regs_4 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT__regs_5 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT__regs_6 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT__regs_7 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT__regs_8 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT__regs_9 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT__regs_10 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT__regs_11 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT__regs_12 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT__regs_13 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT__regs_14 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT__regs_15 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT__regs_16 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT__regs_17 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT__regs_18 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT__regs_19 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT__regs_20 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT__regs_21 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT__regs_22 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT__regs_23 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT__regs_24 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT__regs_25 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT__regs_26 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT__regs_27 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT__regs_28 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT__regs_29 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT__regs_30 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT__regs_31 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT___GEN_117 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT___GEN_149 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT__REG_0 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT__REG_1 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT__REG_2 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT__REG_3 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT__REG_4 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT__REG_5 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT__REG_6 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT__REG_7 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT__REG_8 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT__REG_9 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT__REG_10 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT__REG_11 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT__REG_12 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT__REG_13 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT__REG_14 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT__REG_15 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT__REG_16 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT__REG_17 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT__REG_18 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT__REG_19 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT__REG_20 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT__REG_21 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT__REG_22 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT__REG_23 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT__REG_24 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT__REG_25 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT__REG_26 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT__REG_27 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT__REG_28 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT__REG_29 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT__REG_30 = VL_RAND_RESET_Q(64);
    self->SimTop__DOT__rvcore__DOT__reg___DOT__REG_31 = VL_RAND_RESET_Q(64);
    self->__Vfunc_ram_read_helper__0__Vfuncout = 0;
    self->__Vfunc_ram_read_helper__2__Vfuncout = 0;
}
