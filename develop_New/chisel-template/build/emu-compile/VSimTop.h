// Verilated -*- C++ -*-
// DESCRIPTION: Verilator output: Primary design header
//
// This header should be included by all source files instantiating the design.
// The class here is then constructed to instantiate the design.
// See the Verilator manual for examples.

#ifndef VERILATED_VSIMTOP_H_
#define VERILATED_VSIMTOP_H_  // guard

#include "verilated_heavy.h"
#include "VSimTop__Dpi.h"

//==========

class VSimTop__Syms;
class VSimTop___024unit;


//----------

VL_MODULE(VSimTop) {
  public:
    // CELLS
    // Public to allow access to /*verilator_public*/ items;
    // otherwise the application code can consider these internals.
    VSimTop___024unit* __PVT____024unit;

    // PORTS
    // The application code writes and reads these signals to
    // propagate new values into/out from the Verilated model.
    VL_IN8(clock,0,0);
    VL_IN8(reset,0,0);
    VL_IN8(io_perfInfo_clean,0,0);
    VL_IN8(io_perfInfo_dump,0,0);
    VL_OUT8(io_uart_out_valid,0,0);
    VL_OUT8(io_uart_out_ch,7,0);
    VL_OUT8(io_uart_in_valid,0,0);
    VL_IN8(io_uart_in_ch,7,0);
    VL_IN64(io_logCtrl_log_begin,63,0);
    VL_IN64(io_logCtrl_log_end,63,0);
    VL_IN64(io_logCtrl_log_level,63,0);

    // LOCAL SIGNALS
    // Internals; generally not touched by application code
    // Anonymous structures to workaround compiler member-count bugs
    struct {
        CData/*0:0*/ SimTop__DOT__REG;
        CData/*0:0*/ SimTop__DOT__REG_1;
        CData/*0:0*/ SimTop__DOT__REG_6;
        CData/*0:0*/ SimTop__DOT__REG_7;
        CData/*4:0*/ SimTop__DOT__REG_10;
        CData/*4:0*/ SimTop__DOT__REG_11;
        CData/*0:0*/ SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_src1Type;
        CData/*0:0*/ SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_src2Type;
        CData/*2:0*/ SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcType;
        CData/*6:0*/ SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_funcOpType;
        CData/*4:0*/ SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfSrc1;
        CData/*4:0*/ SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfSrc2;
        CData/*4:0*/ SimTop__DOT__rvcore__DOT__idu_io_out_ctrl_rfrd;
        CData/*0:0*/ SimTop__DOT__rvcore__DOT__exu_io_reg_write_back_ena;
        CData/*2:0*/ SimTop__DOT__rvcore__DOT__idu__DOT___T_112;
        CData/*2:0*/ SimTop__DOT__rvcore__DOT__idu__DOT___T_127;
        CData/*2:0*/ SimTop__DOT__rvcore__DOT__idu__DOT___T_142;
        CData/*2:0*/ SimTop__DOT__rvcore__DOT__idu__DOT__instrType;
        CData/*0:0*/ SimTop__DOT__rvcore__DOT__idu__DOT___T_165;
        CData/*2:0*/ SimTop__DOT__rvcore__DOT__idu__DOT___T_180;
        CData/*6:0*/ SimTop__DOT__rvcore__DOT__idu__DOT___T_208;
        CData/*6:0*/ SimTop__DOT__rvcore__DOT__idu__DOT___T_223;
        CData/*6:0*/ SimTop__DOT__rvcore__DOT__idu__DOT___T_238;
        CData/*5:0*/ SimTop__DOT__rvcore__DOT__exu__DOT__alu__DOT__shamt;
        IData/*31:0*/ SimTop__DOT___RAND_0;
        IData/*31:0*/ SimTop__DOT___RAND_1;
        IData/*31:0*/ SimTop__DOT___RAND_4;
        IData/*31:0*/ SimTop__DOT___RAND_5;
        IData/*31:0*/ SimTop__DOT___RAND_6;
        IData/*31:0*/ SimTop__DOT___RAND_7;
        IData/*31:0*/ SimTop__DOT___RAND_10;
        IData/*31:0*/ SimTop__DOT___RAND_11;
        IData/*31:0*/ SimTop__DOT___RAND_12;
        IData/*31:0*/ SimTop__DOT___RAND_13;
        IData/*31:0*/ SimTop__DOT__REG_4;
        IData/*31:0*/ SimTop__DOT__REG_5;
        IData/*31:0*/ SimTop__DOT__REG_12;
        IData/*31:0*/ SimTop__DOT__REG_13;
        IData/*31:0*/ SimTop__DOT__rvcore__DOT__ifu_io_out_instr;
        VlWide<4>/*126:0*/ SimTop__DOT__rvcore__DOT__exu__DOT__alu__DOT___T_2;
        VlWide<4>/*126:0*/ SimTop__DOT__rvcore__DOT__exu__DOT__alu__DOT___res_T_27;
        VlWide<4>/*126:0*/ SimTop__DOT__rvcore__DOT__exu__DOT__alu__DOT___res_T_31;
        QData/*63:0*/ SimTop__DOT___RAND_2;
        QData/*63:0*/ SimTop__DOT___RAND_3;
        QData/*63:0*/ SimTop__DOT___RAND_8;
        QData/*63:0*/ SimTop__DOT___RAND_9;
        QData/*63:0*/ SimTop__DOT___RAND_14;
        QData/*63:0*/ SimTop__DOT___RAND_15;
        QData/*63:0*/ SimTop__DOT__REG_2;
        QData/*63:0*/ SimTop__DOT__REG_3;
        QData/*63:0*/ SimTop__DOT__REG_8;
        QData/*63:0*/ SimTop__DOT__REG_9;
        QData/*63:0*/ SimTop__DOT__REG_14;
        QData/*63:0*/ SimTop__DOT__REG_15;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__idu_io_out_data_imm;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__dis_io_out_data_src1;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__dis_io_out_data_src2;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__exu_io_reg_write_back_data;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__ifu__DOT___RAND_0;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__ifu__DOT__ram_rdata;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__ifu__DOT__pc;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__ifu__DOT___pc_T_1;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__exu__DOT__alu__DOT___T_1;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__exu__DOT__alu__DOT___T_12;
    };
    struct {
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__exu__DOT__lsu__DOT__ram_rdata;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__exu__DOT__lsu__DOT__addr;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__exu__DOT__lsu__DOT__rdataSel;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__exu__DOT__bru__DOT___io_out_newPC_T_4;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT___RAND_0;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT___RAND_1;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT___RAND_2;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT___RAND_3;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT___RAND_4;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT___RAND_5;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT___RAND_6;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT___RAND_7;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT___RAND_8;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT___RAND_9;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT___RAND_10;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT___RAND_11;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT___RAND_12;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT___RAND_13;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT___RAND_14;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT___RAND_15;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT___RAND_16;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT___RAND_17;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT___RAND_18;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT___RAND_19;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT___RAND_20;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT___RAND_21;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT___RAND_22;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT___RAND_23;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT___RAND_24;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT___RAND_25;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT___RAND_26;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT___RAND_27;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT___RAND_28;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT___RAND_29;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT___RAND_30;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT___RAND_31;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT___RAND_32;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT___RAND_33;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT___RAND_34;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT___RAND_35;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT___RAND_36;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT___RAND_37;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT___RAND_38;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT___RAND_39;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT___RAND_40;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT___RAND_41;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT___RAND_42;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT___RAND_43;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT___RAND_44;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT___RAND_45;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT___RAND_46;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT___RAND_47;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT___RAND_48;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT___RAND_49;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT___RAND_50;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT___RAND_51;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT___RAND_52;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT___RAND_53;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT___RAND_54;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT___RAND_55;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT___RAND_56;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT___RAND_57;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT___RAND_58;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT___RAND_59;
    };
    struct {
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT___RAND_60;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT___RAND_61;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT___RAND_62;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT___RAND_63;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT__regs_0;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT__regs_1;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT__regs_2;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT__regs_3;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT__regs_4;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT__regs_5;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT__regs_6;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT__regs_7;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT__regs_8;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT__regs_9;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT__regs_10;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT__regs_11;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT__regs_12;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT__regs_13;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT__regs_14;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT__regs_15;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT__regs_16;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT__regs_17;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT__regs_18;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT__regs_19;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT__regs_20;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT__regs_21;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT__regs_22;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT__regs_23;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT__regs_24;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT__regs_25;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT__regs_26;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT__regs_27;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT__regs_28;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT__regs_29;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT__regs_30;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT__regs_31;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT___GEN_117;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT___GEN_149;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT__REG_0;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT__REG_1;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT__REG_2;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT__REG_3;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT__REG_4;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT__REG_5;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT__REG_6;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT__REG_7;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT__REG_8;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT__REG_9;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT__REG_10;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT__REG_11;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT__REG_12;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT__REG_13;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT__REG_14;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT__REG_15;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT__REG_16;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT__REG_17;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT__REG_18;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT__REG_19;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT__REG_20;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT__REG_21;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT__REG_22;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT__REG_23;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT__REG_24;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT__REG_25;
    };
    struct {
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT__REG_26;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT__REG_27;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT__REG_28;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT__REG_29;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT__REG_30;
        QData/*63:0*/ SimTop__DOT__rvcore__DOT__reg___DOT__REG_31;
    };

    // LOCAL VARIABLES
    // Internals; generally not touched by application code
    CData/*0:0*/ __Vclklast__TOP__clock;
    QData/*63:0*/ __Vfunc_ram_read_helper__0__Vfuncout;
    QData/*63:0*/ __Vfunc_ram_read_helper__2__Vfuncout;

    // INTERNAL VARIABLES
    // Internals; generally not touched by application code
    VSimTop__Syms* __VlSymsp;  // Symbol table

    // CONSTRUCTORS
  private:
    VL_UNCOPYABLE(VSimTop);  ///< Copying not allowed
  public:
    /// Construct the model; called by application code
    /// If contextp is null, then the model will use the default global context
    /// If name is "", then makes a wrapper with a
    /// single model invisible with respect to DPI scope names.
    VSimTop(VerilatedContext* contextp, const char* name = "TOP");
    VSimTop(const char* name = "TOP")
      : VSimTop(nullptr, name) {}
    /// Destroy the model; called (often implicitly) by application code
    ~VSimTop();

    // API METHODS
    /// Return current simulation context for this model.
    /// Used to get to e.g. simulation time via contextp()->time()
    VerilatedContext* contextp();
    /// Evaluate the model.  Application must call when inputs change.
    void eval() { eval_step(); }
    /// Evaluate when calling multiple units/models per time step.
    void eval_step();
    /// Evaluate at end of a timestep for tracing, when using eval_step().
    /// Application must call after all eval() and before time changes.
    void eval_end_step() {}
    /// Simulation complete, run final blocks.  Application must call on completion.
    void final();

    // INTERNAL METHODS
    static void _eval_initial_loop(VSimTop__Syms* __restrict vlSymsp);
    void __Vconfigure(VSimTop__Syms* symsp, bool first);
  private:
    static QData _change_request(VSimTop__Syms* __restrict vlSymsp);
    static QData _change_request_1(VSimTop__Syms* __restrict vlSymsp);
  public:
    static void _combo__TOP__4(VSimTop__Syms* __restrict vlSymsp);
  private:
    static void _ctor_var_reset(VSimTop* self) VL_ATTR_COLD;
  public:
    static void _eval(VSimTop__Syms* __restrict vlSymsp);
  private:
#ifdef VL_DEBUG
    void _eval_debug_assertions();
#endif  // VL_DEBUG
  public:
    static void _eval_initial(VSimTop__Syms* __restrict vlSymsp) VL_ATTR_COLD;
    static void _eval_settle(VSimTop__Syms* __restrict vlSymsp) VL_ATTR_COLD;
    static void _initial__TOP__1(VSimTop__Syms* __restrict vlSymsp) VL_ATTR_COLD;
    static void _sequent__TOP__2(VSimTop__Syms* __restrict vlSymsp);
    static void _settle__TOP__3(VSimTop__Syms* __restrict vlSymsp) VL_ATTR_COLD;
} VL_ATTR_ALIGNED(VL_CACHE_LINE_BYTES);

//----------


#endif  // guard
