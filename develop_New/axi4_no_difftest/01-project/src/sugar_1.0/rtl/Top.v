module IFU(
  input         clock,
  input         reset,
  input  [63:0] io_in_new_pc,
  input         io_in_valid,
  output [63:0] io_out_pc,
  output [31:0] io_out_instr,
  input         io_ifuaxi_ar_ready,
  output        io_ifuaxi_ar_valid,
  output [31:0] io_ifuaxi_ar_bits_addr,
  output        io_ifuaxi_r_ready,
  input         io_ifuaxi_r_valid,
  input  [63:0] io_ifuaxi_r_bits_data
);
`ifdef RANDOMIZE_REG_INIT
  reg [31:0] _RAND_0;
  reg [63:0] _RAND_1;
  reg [63:0] _RAND_2;
`endif // RANDOMIZE_REG_INIT
  reg [1:0] inflight_type; // @[IFU.scala 22:30]
  wire  ar_hs = io_ifuaxi_ar_valid & io_ifuaxi_ar_ready; // @[IFU.scala 37:29]
  wire  r_hs = io_ifuaxi_r_valid & io_ifuaxi_r_ready; // @[IFU.scala 38:28]
  reg [63:0] pc; // @[IFU.scala 40:19]
  wire [63:0] _pc_T_1 = io_in_new_pc + 64'h4; // @[IFU.scala 54:41]
  wire [63:0] _pc_T_3 = pc + 64'h4; // @[IFU.scala 54:51]
  reg [63:0] instr; // @[IFU.scala 58:28]
  assign io_out_pc = pc; // @[IFU.scala 66:13]
  assign io_out_instr = instr[31:0]; // @[IFU.scala 67:16]
  assign io_ifuaxi_ar_valid = inflight_type == 2'h1; // @[IFU.scala 30:19]
  assign io_ifuaxi_ar_bits_addr = pc[31:0]; // @[IFU.scala 50:21]
  assign io_ifuaxi_r_ready = inflight_type == 2'h2; // @[IFU.scala 30:19]
  always @(posedge clock) begin
    if (reset) begin // @[IFU.scala 22:30]
      inflight_type <= 2'h1; // @[IFU.scala 22:30]
    end else if (r_hs) begin // @[IFU.scala 59:13]
      inflight_type <= 2'h1; // @[IFU.scala 24:19]
    end else if (ar_hs) begin // @[IFU.scala 53:15]
      inflight_type <= 2'h2; // @[IFU.scala 24:19]
    end
    if (reset) begin // @[IFU.scala 40:19]
      pc <= 64'h80000000; // @[IFU.scala 40:19]
    end else if (ar_hs) begin // @[IFU.scala 53:15]
      if (io_in_valid) begin // @[IFU.scala 54:14]
        pc <= _pc_T_1;
      end else begin
        pc <= _pc_T_3;
      end
    end
    if (reset) begin // @[IFU.scala 58:28]
      instr <= 64'h0; // @[IFU.scala 58:28]
    end else if (r_hs) begin // @[IFU.scala 59:13]
      instr <= io_ifuaxi_r_bits_data; // @[IFU.scala 60:17]
    end
  end
// Register and memory initialization
`ifdef RANDOMIZE_GARBAGE_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_INVALID_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_REG_INIT
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_MEM_INIT
`define RANDOMIZE
`endif
`ifndef RANDOM
`define RANDOM $random
`endif
`ifdef RANDOMIZE_MEM_INIT
  integer initvar;
`endif
`ifndef SYNTHESIS
`ifdef FIRRTL_BEFORE_INITIAL
`FIRRTL_BEFORE_INITIAL
`endif
initial begin
  `ifdef RANDOMIZE
    `ifdef INIT_RANDOM
      `INIT_RANDOM
    `endif
    `ifndef VERILATOR
      `ifdef RANDOMIZE_DELAY
        #`RANDOMIZE_DELAY begin end
      `else
        #0.002 begin end
      `endif
    `endif
`ifdef RANDOMIZE_REG_INIT
  _RAND_0 = {1{`RANDOM}};
  inflight_type = _RAND_0[1:0];
  _RAND_1 = {2{`RANDOM}};
  pc = _RAND_1[63:0];
  _RAND_2 = {2{`RANDOM}};
  instr = _RAND_2[63:0];
`endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`ifdef FIRRTL_AFTER_INITIAL
`FIRRTL_AFTER_INITIAL
`endif
`endif // SYNTHESIS
endmodule
module IDU(
  input  [63:0] io_in_pc,
  input  [31:0] io_in_instr,
  output [63:0] io_out_cf_pc,
  output [1:0]  io_out_ctrl_src1Type,
  output [1:0]  io_out_ctrl_src2Type,
  output [2:0]  io_out_ctrl_funcType,
  output [6:0]  io_out_ctrl_funcOpType,
  output [4:0]  io_out_ctrl_rfSrc1,
  output [4:0]  io_out_ctrl_rfSrc2,
  output [4:0]  io_out_ctrl_rfrd,
  output [63:0] io_out_data_imm,
  output [63:0] io_out_data_uimm_ext
);
  wire [4:0] src1Addr = io_in_instr[19:15]; // @[IDU.scala 19:44]
  wire [4:0] src2Addr = io_in_instr[24:20]; // @[IDU.scala 19:59]
  wire [4:0] rdAddr = io_in_instr[11:7]; // @[IDU.scala 19:74]
  wire [31:0] _T = io_in_instr & 32'h707f; // @[Lookup.scala 31:38]
  wire  _T_1 = 32'h13 == _T; // @[Lookup.scala 31:38]
  wire [31:0] _T_2 = io_in_instr & 32'hfc00707f; // @[Lookup.scala 31:38]
  wire  _T_3 = 32'h1013 == _T_2; // @[Lookup.scala 31:38]
  wire  _T_5 = 32'h2013 == _T; // @[Lookup.scala 31:38]
  wire  _T_7 = 32'h3013 == _T; // @[Lookup.scala 31:38]
  wire  _T_9 = 32'h4013 == _T; // @[Lookup.scala 31:38]
  wire  _T_11 = 32'h5013 == _T_2; // @[Lookup.scala 31:38]
  wire  _T_13 = 32'h6013 == _T; // @[Lookup.scala 31:38]
  wire  _T_15 = 32'h7013 == _T; // @[Lookup.scala 31:38]
  wire  _T_17 = 32'h40005013 == _T_2; // @[Lookup.scala 31:38]
  wire [31:0] _T_18 = io_in_instr & 32'hfe00707f; // @[Lookup.scala 31:38]
  wire  _T_19 = 32'h33 == _T_18; // @[Lookup.scala 31:38]
  wire  _T_21 = 32'h1033 == _T_18; // @[Lookup.scala 31:38]
  wire  _T_23 = 32'h2033 == _T_18; // @[Lookup.scala 31:38]
  wire  _T_25 = 32'h3033 == _T_18; // @[Lookup.scala 31:38]
  wire  _T_27 = 32'h4033 == _T_18; // @[Lookup.scala 31:38]
  wire  _T_29 = 32'h5033 == _T_18; // @[Lookup.scala 31:38]
  wire  _T_31 = 32'h6033 == _T_18; // @[Lookup.scala 31:38]
  wire  _T_33 = 32'h7033 == _T_18; // @[Lookup.scala 31:38]
  wire  _T_35 = 32'h40000033 == _T_18; // @[Lookup.scala 31:38]
  wire  _T_37 = 32'h40005033 == _T_18; // @[Lookup.scala 31:38]
  wire [31:0] _T_38 = io_in_instr & 32'h7f; // @[Lookup.scala 31:38]
  wire  _T_39 = 32'h17 == _T_38; // @[Lookup.scala 31:38]
  wire  _T_41 = 32'h37 == _T_38; // @[Lookup.scala 31:38]
  wire  _T_43 = 32'h6f == _T_38; // @[Lookup.scala 31:38]
  wire  _T_45 = 32'h67 == _T; // @[Lookup.scala 31:38]
  wire  _T_47 = 32'h63 == _T; // @[Lookup.scala 31:38]
  wire  _T_49 = 32'h1063 == _T; // @[Lookup.scala 31:38]
  wire  _T_51 = 32'h4063 == _T; // @[Lookup.scala 31:38]
  wire  _T_53 = 32'h5063 == _T; // @[Lookup.scala 31:38]
  wire  _T_55 = 32'h6063 == _T; // @[Lookup.scala 31:38]
  wire  _T_57 = 32'h7063 == _T; // @[Lookup.scala 31:38]
  wire  _T_59 = 32'h3 == _T; // @[Lookup.scala 31:38]
  wire  _T_61 = 32'h1003 == _T; // @[Lookup.scala 31:38]
  wire  _T_63 = 32'h2003 == _T; // @[Lookup.scala 31:38]
  wire  _T_65 = 32'h4003 == _T; // @[Lookup.scala 31:38]
  wire  _T_67 = 32'h5003 == _T; // @[Lookup.scala 31:38]
  wire  _T_69 = 32'h23 == _T; // @[Lookup.scala 31:38]
  wire  _T_71 = 32'h1023 == _T; // @[Lookup.scala 31:38]
  wire  _T_73 = 32'h2023 == _T; // @[Lookup.scala 31:38]
  wire  _T_75 = 32'h1b == _T; // @[Lookup.scala 31:38]
  wire  _T_77 = 32'h101b == _T_18; // @[Lookup.scala 31:38]
  wire  _T_79 = 32'h501b == _T_18; // @[Lookup.scala 31:38]
  wire  _T_81 = 32'h4000501b == _T_18; // @[Lookup.scala 31:38]
  wire  _T_83 = 32'h103b == _T_18; // @[Lookup.scala 31:38]
  wire  _T_85 = 32'h503b == _T_18; // @[Lookup.scala 31:38]
  wire  _T_87 = 32'h4000503b == _T_18; // @[Lookup.scala 31:38]
  wire  _T_89 = 32'h3b == _T_18; // @[Lookup.scala 31:38]
  wire  _T_91 = 32'h4000003b == _T_18; // @[Lookup.scala 31:38]
  wire  _T_93 = 32'h6003 == _T; // @[Lookup.scala 31:38]
  wire  _T_95 = 32'h3003 == _T; // @[Lookup.scala 31:38]
  wire  _T_97 = 32'h3023 == _T; // @[Lookup.scala 31:38]
  wire [1:0] _T_98 = _T_97 ? 2'h2 : 2'h0; // @[Lookup.scala 33:37]
  wire [2:0] _T_99 = _T_95 ? 3'h4 : {{1'd0}, _T_98}; // @[Lookup.scala 33:37]
  wire [2:0] _T_100 = _T_93 ? 3'h4 : _T_99; // @[Lookup.scala 33:37]
  wire [2:0] _T_101 = _T_91 ? 3'h5 : _T_100; // @[Lookup.scala 33:37]
  wire [2:0] _T_102 = _T_89 ? 3'h5 : _T_101; // @[Lookup.scala 33:37]
  wire [2:0] _T_103 = _T_87 ? 3'h5 : _T_102; // @[Lookup.scala 33:37]
  wire [2:0] _T_104 = _T_85 ? 3'h5 : _T_103; // @[Lookup.scala 33:37]
  wire [2:0] _T_105 = _T_83 ? 3'h5 : _T_104; // @[Lookup.scala 33:37]
  wire [2:0] _T_106 = _T_81 ? 3'h4 : _T_105; // @[Lookup.scala 33:37]
  wire [2:0] _T_107 = _T_79 ? 3'h4 : _T_106; // @[Lookup.scala 33:37]
  wire [2:0] _T_108 = _T_77 ? 3'h4 : _T_107; // @[Lookup.scala 33:37]
  wire [2:0] _T_109 = _T_75 ? 3'h4 : _T_108; // @[Lookup.scala 33:37]
  wire [2:0] _T_110 = _T_73 ? 3'h2 : _T_109; // @[Lookup.scala 33:37]
  wire [2:0] _T_111 = _T_71 ? 3'h2 : _T_110; // @[Lookup.scala 33:37]
  wire [2:0] _T_112 = _T_69 ? 3'h2 : _T_111; // @[Lookup.scala 33:37]
  wire [2:0] _T_113 = _T_67 ? 3'h4 : _T_112; // @[Lookup.scala 33:37]
  wire [2:0] _T_114 = _T_65 ? 3'h4 : _T_113; // @[Lookup.scala 33:37]
  wire [2:0] _T_115 = _T_63 ? 3'h4 : _T_114; // @[Lookup.scala 33:37]
  wire [2:0] _T_116 = _T_61 ? 3'h4 : _T_115; // @[Lookup.scala 33:37]
  wire [2:0] _T_117 = _T_59 ? 3'h4 : _T_116; // @[Lookup.scala 33:37]
  wire [2:0] _T_118 = _T_57 ? 3'h1 : _T_117; // @[Lookup.scala 33:37]
  wire [2:0] _T_119 = _T_55 ? 3'h1 : _T_118; // @[Lookup.scala 33:37]
  wire [2:0] _T_120 = _T_53 ? 3'h1 : _T_119; // @[Lookup.scala 33:37]
  wire [2:0] _T_121 = _T_51 ? 3'h1 : _T_120; // @[Lookup.scala 33:37]
  wire [2:0] _T_122 = _T_49 ? 3'h1 : _T_121; // @[Lookup.scala 33:37]
  wire [2:0] _T_123 = _T_47 ? 3'h1 : _T_122; // @[Lookup.scala 33:37]
  wire [2:0] _T_124 = _T_45 ? 3'h4 : _T_123; // @[Lookup.scala 33:37]
  wire [2:0] _T_125 = _T_43 ? 3'h7 : _T_124; // @[Lookup.scala 33:37]
  wire [2:0] _T_126 = _T_41 ? 3'h6 : _T_125; // @[Lookup.scala 33:37]
  wire [2:0] _T_127 = _T_39 ? 3'h6 : _T_126; // @[Lookup.scala 33:37]
  wire [2:0] _T_128 = _T_37 ? 3'h5 : _T_127; // @[Lookup.scala 33:37]
  wire [2:0] _T_129 = _T_35 ? 3'h5 : _T_128; // @[Lookup.scala 33:37]
  wire [2:0] _T_130 = _T_33 ? 3'h5 : _T_129; // @[Lookup.scala 33:37]
  wire [2:0] _T_131 = _T_31 ? 3'h5 : _T_130; // @[Lookup.scala 33:37]
  wire [2:0] _T_132 = _T_29 ? 3'h5 : _T_131; // @[Lookup.scala 33:37]
  wire [2:0] _T_133 = _T_27 ? 3'h5 : _T_132; // @[Lookup.scala 33:37]
  wire [2:0] _T_134 = _T_25 ? 3'h5 : _T_133; // @[Lookup.scala 33:37]
  wire [2:0] _T_135 = _T_23 ? 3'h5 : _T_134; // @[Lookup.scala 33:37]
  wire [2:0] _T_136 = _T_21 ? 3'h5 : _T_135; // @[Lookup.scala 33:37]
  wire [2:0] _T_137 = _T_19 ? 3'h5 : _T_136; // @[Lookup.scala 33:37]
  wire [2:0] _T_138 = _T_17 ? 3'h4 : _T_137; // @[Lookup.scala 33:37]
  wire [2:0] _T_139 = _T_15 ? 3'h4 : _T_138; // @[Lookup.scala 33:37]
  wire [2:0] _T_140 = _T_13 ? 3'h4 : _T_139; // @[Lookup.scala 33:37]
  wire [2:0] _T_141 = _T_11 ? 3'h4 : _T_140; // @[Lookup.scala 33:37]
  wire [2:0] _T_142 = _T_9 ? 3'h4 : _T_141; // @[Lookup.scala 33:37]
  wire [2:0] _T_143 = _T_7 ? 3'h4 : _T_142; // @[Lookup.scala 33:37]
  wire [2:0] _T_144 = _T_5 ? 3'h4 : _T_143; // @[Lookup.scala 33:37]
  wire [2:0] _T_145 = _T_3 ? 3'h4 : _T_144; // @[Lookup.scala 33:37]
  wire [2:0] instrType = _T_1 ? 3'h4 : _T_145; // @[Lookup.scala 33:37]
  wire  _T_149 = _T_91 ? 1'h0 : _T_93 | (_T_95 | _T_97); // @[Lookup.scala 33:37]
  wire  _T_150 = _T_89 ? 1'h0 : _T_149; // @[Lookup.scala 33:37]
  wire  _T_151 = _T_87 ? 1'h0 : _T_150; // @[Lookup.scala 33:37]
  wire  _T_152 = _T_85 ? 1'h0 : _T_151; // @[Lookup.scala 33:37]
  wire  _T_153 = _T_83 ? 1'h0 : _T_152; // @[Lookup.scala 33:37]
  wire  _T_154 = _T_81 ? 1'h0 : _T_153; // @[Lookup.scala 33:37]
  wire  _T_155 = _T_79 ? 1'h0 : _T_154; // @[Lookup.scala 33:37]
  wire  _T_156 = _T_77 ? 1'h0 : _T_155; // @[Lookup.scala 33:37]
  wire  _T_157 = _T_75 ? 1'h0 : _T_156; // @[Lookup.scala 33:37]
  wire  _T_165 = _T_59 | (_T_61 | (_T_63 | (_T_65 | (_T_67 | (_T_69 | (_T_71 | (_T_73 | _T_157))))))); // @[Lookup.scala 33:37]
  wire [2:0] _T_166 = _T_57 ? 3'h5 : {{2'd0}, _T_165}; // @[Lookup.scala 33:37]
  wire [2:0] _T_167 = _T_55 ? 3'h5 : _T_166; // @[Lookup.scala 33:37]
  wire [2:0] _T_168 = _T_53 ? 3'h5 : _T_167; // @[Lookup.scala 33:37]
  wire [2:0] _T_169 = _T_51 ? 3'h5 : _T_168; // @[Lookup.scala 33:37]
  wire [2:0] _T_170 = _T_49 ? 3'h5 : _T_169; // @[Lookup.scala 33:37]
  wire [2:0] _T_171 = _T_47 ? 3'h5 : _T_170; // @[Lookup.scala 33:37]
  wire [2:0] _T_172 = _T_45 ? 3'h5 : _T_171; // @[Lookup.scala 33:37]
  wire [2:0] _T_173 = _T_43 ? 3'h5 : _T_172; // @[Lookup.scala 33:37]
  wire [2:0] _T_174 = _T_41 ? 3'h0 : _T_173; // @[Lookup.scala 33:37]
  wire [2:0] _T_175 = _T_39 ? 3'h0 : _T_174; // @[Lookup.scala 33:37]
  wire [2:0] _T_176 = _T_37 ? 3'h0 : _T_175; // @[Lookup.scala 33:37]
  wire [2:0] _T_177 = _T_35 ? 3'h0 : _T_176; // @[Lookup.scala 33:37]
  wire [2:0] _T_178 = _T_33 ? 3'h0 : _T_177; // @[Lookup.scala 33:37]
  wire [2:0] _T_179 = _T_31 ? 3'h0 : _T_178; // @[Lookup.scala 33:37]
  wire [2:0] _T_180 = _T_29 ? 3'h0 : _T_179; // @[Lookup.scala 33:37]
  wire [2:0] _T_181 = _T_27 ? 3'h0 : _T_180; // @[Lookup.scala 33:37]
  wire [2:0] _T_182 = _T_25 ? 3'h0 : _T_181; // @[Lookup.scala 33:37]
  wire [2:0] _T_183 = _T_23 ? 3'h0 : _T_182; // @[Lookup.scala 33:37]
  wire [2:0] _T_184 = _T_21 ? 3'h0 : _T_183; // @[Lookup.scala 33:37]
  wire [2:0] _T_185 = _T_19 ? 3'h0 : _T_184; // @[Lookup.scala 33:37]
  wire [2:0] _T_186 = _T_17 ? 3'h0 : _T_185; // @[Lookup.scala 33:37]
  wire [2:0] _T_187 = _T_15 ? 3'h0 : _T_186; // @[Lookup.scala 33:37]
  wire [2:0] _T_188 = _T_13 ? 3'h0 : _T_187; // @[Lookup.scala 33:37]
  wire [2:0] _T_189 = _T_11 ? 3'h0 : _T_188; // @[Lookup.scala 33:37]
  wire [2:0] _T_190 = _T_9 ? 3'h0 : _T_189; // @[Lookup.scala 33:37]
  wire [2:0] _T_191 = _T_7 ? 3'h0 : _T_190; // @[Lookup.scala 33:37]
  wire [2:0] _T_192 = _T_5 ? 3'h0 : _T_191; // @[Lookup.scala 33:37]
  wire [2:0] _T_193 = _T_3 ? 3'h0 : _T_192; // @[Lookup.scala 33:37]
  wire [2:0] funcType = _T_1 ? 3'h0 : _T_193; // @[Lookup.scala 33:37]
  wire [6:0] _T_194 = _T_97 ? 7'hb : 7'h40; // @[Lookup.scala 33:37]
  wire [6:0] _T_195 = _T_95 ? 7'h3 : _T_194; // @[Lookup.scala 33:37]
  wire [6:0] _T_196 = _T_93 ? 7'h6 : _T_195; // @[Lookup.scala 33:37]
  wire [6:0] _T_197 = _T_91 ? 7'h28 : _T_196; // @[Lookup.scala 33:37]
  wire [6:0] _T_198 = _T_89 ? 7'h60 : _T_197; // @[Lookup.scala 33:37]
  wire [6:0] _T_199 = _T_87 ? 7'h2d : _T_198; // @[Lookup.scala 33:37]
  wire [6:0] _T_200 = _T_85 ? 7'h25 : _T_199; // @[Lookup.scala 33:37]
  wire [6:0] _T_201 = _T_83 ? 7'h21 : _T_200; // @[Lookup.scala 33:37]
  wire [6:0] _T_202 = _T_81 ? 7'h2d : _T_201; // @[Lookup.scala 33:37]
  wire [6:0] _T_203 = _T_79 ? 7'h25 : _T_202; // @[Lookup.scala 33:37]
  wire [6:0] _T_204 = _T_77 ? 7'h21 : _T_203; // @[Lookup.scala 33:37]
  wire [6:0] _T_205 = _T_75 ? 7'h60 : _T_204; // @[Lookup.scala 33:37]
  wire [6:0] _T_206 = _T_73 ? 7'ha : _T_205; // @[Lookup.scala 33:37]
  wire [6:0] _T_207 = _T_71 ? 7'h9 : _T_206; // @[Lookup.scala 33:37]
  wire [6:0] _T_208 = _T_69 ? 7'h8 : _T_207; // @[Lookup.scala 33:37]
  wire [6:0] _T_209 = _T_67 ? 7'h5 : _T_208; // @[Lookup.scala 33:37]
  wire [6:0] _T_210 = _T_65 ? 7'h4 : _T_209; // @[Lookup.scala 33:37]
  wire [6:0] _T_211 = _T_63 ? 7'h2 : _T_210; // @[Lookup.scala 33:37]
  wire [6:0] _T_212 = _T_61 ? 7'h1 : _T_211; // @[Lookup.scala 33:37]
  wire [6:0] _T_213 = _T_59 ? 7'h0 : _T_212; // @[Lookup.scala 33:37]
  wire [6:0] _T_214 = _T_57 ? 7'h17 : _T_213; // @[Lookup.scala 33:37]
  wire [6:0] _T_215 = _T_55 ? 7'h16 : _T_214; // @[Lookup.scala 33:37]
  wire [6:0] _T_216 = _T_53 ? 7'h15 : _T_215; // @[Lookup.scala 33:37]
  wire [6:0] _T_217 = _T_51 ? 7'h14 : _T_216; // @[Lookup.scala 33:37]
  wire [6:0] _T_218 = _T_49 ? 7'h11 : _T_217; // @[Lookup.scala 33:37]
  wire [6:0] _T_219 = _T_47 ? 7'h10 : _T_218; // @[Lookup.scala 33:37]
  wire [6:0] _T_220 = _T_45 ? 7'h5a : _T_219; // @[Lookup.scala 33:37]
  wire [6:0] _T_221 = _T_43 ? 7'h58 : _T_220; // @[Lookup.scala 33:37]
  wire [6:0] _T_222 = _T_41 ? 7'hf : _T_221; // @[Lookup.scala 33:37]
  wire [6:0] _T_223 = _T_39 ? 7'h40 : _T_222; // @[Lookup.scala 33:37]
  wire [6:0] _T_224 = _T_37 ? 7'hd : _T_223; // @[Lookup.scala 33:37]
  wire [6:0] _T_225 = _T_35 ? 7'h8 : _T_224; // @[Lookup.scala 33:37]
  wire [6:0] _T_226 = _T_33 ? 7'h7 : _T_225; // @[Lookup.scala 33:37]
  wire [6:0] _T_227 = _T_31 ? 7'h6 : _T_226; // @[Lookup.scala 33:37]
  wire [6:0] _T_228 = _T_29 ? 7'h5 : _T_227; // @[Lookup.scala 33:37]
  wire [6:0] _T_229 = _T_27 ? 7'h4 : _T_228; // @[Lookup.scala 33:37]
  wire [6:0] _T_230 = _T_25 ? 7'h3 : _T_229; // @[Lookup.scala 33:37]
  wire [6:0] _T_231 = _T_23 ? 7'h2 : _T_230; // @[Lookup.scala 33:37]
  wire [6:0] _T_232 = _T_21 ? 7'h1 : _T_231; // @[Lookup.scala 33:37]
  wire [6:0] _T_233 = _T_19 ? 7'h40 : _T_232; // @[Lookup.scala 33:37]
  wire [6:0] _T_234 = _T_17 ? 7'hd : _T_233; // @[Lookup.scala 33:37]
  wire [6:0] _T_235 = _T_15 ? 7'h7 : _T_234; // @[Lookup.scala 33:37]
  wire [6:0] _T_236 = _T_13 ? 7'h6 : _T_235; // @[Lookup.scala 33:37]
  wire [6:0] _T_237 = _T_11 ? 7'h5 : _T_236; // @[Lookup.scala 33:37]
  wire [6:0] _T_238 = _T_9 ? 7'h4 : _T_237; // @[Lookup.scala 33:37]
  wire [6:0] _T_239 = _T_7 ? 7'h3 : _T_238; // @[Lookup.scala 33:37]
  wire [6:0] _T_240 = _T_5 ? 7'h2 : _T_239; // @[Lookup.scala 33:37]
  wire [6:0] _T_241 = _T_3 ? 7'h1 : _T_240; // @[Lookup.scala 33:37]
  wire [6:0] funcOpType = _T_1 ? 7'h40 : _T_241; // @[Lookup.scala 33:37]
  wire  _io_out_ctrl_src1Type_T = 3'h4 == instrType; // @[utils.scala 8:34]
  wire  _io_out_ctrl_src1Type_T_2 = 3'h2 == instrType; // @[utils.scala 8:34]
  wire  _io_out_ctrl_src1Type_T_3 = 3'h1 == instrType; // @[utils.scala 8:34]
  wire  _io_out_ctrl_src1Type_T_4 = 3'h6 == instrType; // @[utils.scala 8:34]
  wire  _io_out_ctrl_src1Type_T_5 = 3'h7 == instrType; // @[utils.scala 8:34]
  wire  _io_out_ctrl_src1Type_T_6 = 3'h0 == instrType; // @[utils.scala 8:34]
  wire  _io_out_ctrl_src1Type_T_19 = _io_out_ctrl_src1Type_T_4 | _io_out_ctrl_src1Type_T_5 | _io_out_ctrl_src1Type_T_6; // @[Mux.scala 27:72]
  wire  _io_out_ctrl_src2Type_T_19 = _io_out_ctrl_src1Type_T | _io_out_ctrl_src1Type_T_4 | _io_out_ctrl_src1Type_T_5 |
    _io_out_ctrl_src1Type_T_6; // @[Mux.scala 27:72]
  wire [63:0] _uimm_ext_T_4 = {59'h0,src1Addr}; // @[Cat.scala 30:58]
  wire [11:0] imm_lo = io_in_instr[31:20]; // @[IDU.scala 49:29]
  wire  imm_signBit = imm_lo[11]; // @[utils.scala 14:20]
  wire [51:0] imm_hi = imm_signBit ? 52'hfffffffffffff : 52'h0; // @[Bitwise.scala 72:12]
  wire [63:0] _imm_T_1 = {imm_hi,imm_lo}; // @[Cat.scala 30:58]
  wire [6:0] imm_hi_1 = io_in_instr[31:25]; // @[IDU.scala 50:33]
  wire [11:0] imm_lo_2 = {imm_hi_1,rdAddr}; // @[Cat.scala 30:58]
  wire  imm_signBit_1 = imm_lo_2[11]; // @[utils.scala 14:20]
  wire [51:0] imm_hi_2 = imm_signBit_1 ? 52'hfffffffffffff : 52'h0; // @[Bitwise.scala 72:12]
  wire [63:0] _imm_T_3 = {imm_hi_2,imm_hi_1,rdAddr}; // @[Cat.scala 30:58]
  wire  imm_hi_hi_hi = io_in_instr[31]; // @[IDU.scala 51:33]
  wire  imm_hi_hi_lo = io_in_instr[7]; // @[IDU.scala 51:44]
  wire [5:0] imm_hi_lo = io_in_instr[30:25]; // @[IDU.scala 51:54]
  wire [3:0] imm_lo_hi = io_in_instr[11:8]; // @[IDU.scala 51:69]
  wire [12:0] imm_lo_4 = {imm_hi_hi_hi,imm_hi_hi_lo,imm_hi_lo,imm_lo_hi,1'h0}; // @[Cat.scala 30:58]
  wire  imm_signBit_2 = imm_lo_4[12]; // @[utils.scala 14:20]
  wire [50:0] imm_hi_4 = imm_signBit_2 ? 51'h7ffffffffffff : 51'h0; // @[Bitwise.scala 72:12]
  wire [63:0] _imm_T_5 = {imm_hi_4,imm_hi_hi_hi,imm_hi_hi_lo,imm_hi_lo,imm_lo_hi,1'h0}; // @[Cat.scala 30:58]
  wire [19:0] imm_hi_5 = io_in_instr[31:12]; // @[IDU.scala 52:33]
  wire [31:0] imm_lo_5 = {imm_hi_5,12'h0}; // @[Cat.scala 30:58]
  wire  imm_signBit_3 = imm_lo_5[31]; // @[utils.scala 14:20]
  wire [31:0] imm_hi_6 = imm_signBit_3 ? 32'hffffffff : 32'h0; // @[Bitwise.scala 72:12]
  wire [63:0] _imm_T_7 = {imm_hi_6,imm_hi_5,12'h0}; // @[Cat.scala 30:58]
  wire [7:0] imm_hi_hi_lo_1 = io_in_instr[19:12]; // @[IDU.scala 53:44]
  wire  imm_hi_lo_1 = io_in_instr[20]; // @[IDU.scala 53:59]
  wire [9:0] imm_lo_hi_1 = io_in_instr[30:21]; // @[IDU.scala 53:70]
  wire [20:0] imm_lo_7 = {imm_hi_hi_hi,imm_hi_hi_lo_1,imm_hi_lo_1,imm_lo_hi_1,1'h0}; // @[Cat.scala 30:58]
  wire  imm_signBit_4 = imm_lo_7[20]; // @[utils.scala 14:20]
  wire [42:0] imm_hi_8 = imm_signBit_4 ? 43'h7ffffffffff : 43'h0; // @[Bitwise.scala 72:12]
  wire [63:0] _imm_T_9 = {imm_hi_8,imm_hi_hi_hi,imm_hi_hi_lo_1,imm_hi_lo_1,imm_lo_hi_1,1'h0}; // @[Cat.scala 30:58]
  wire [63:0] _imm_T_15 = _io_out_ctrl_src1Type_T ? _imm_T_1 : 64'h0; // @[Mux.scala 27:72]
  wire [63:0] _imm_T_16 = _io_out_ctrl_src1Type_T_2 ? _imm_T_3 : 64'h0; // @[Mux.scala 27:72]
  wire [63:0] _imm_T_17 = _io_out_ctrl_src1Type_T_3 ? _imm_T_5 : 64'h0; // @[Mux.scala 27:72]
  wire [63:0] _imm_T_18 = _io_out_ctrl_src1Type_T_4 ? _imm_T_7 : 64'h0; // @[Mux.scala 27:72]
  wire [63:0] _imm_T_19 = _io_out_ctrl_src1Type_T_5 ? _imm_T_9 : 64'h0; // @[Mux.scala 27:72]
  wire [63:0] _imm_T_20 = _imm_T_15 | _imm_T_16; // @[Mux.scala 27:72]
  wire [63:0] _imm_T_21 = _imm_T_20 | _imm_T_17; // @[Mux.scala 27:72]
  wire [63:0] _imm_T_22 = _imm_T_21 | _imm_T_18; // @[Mux.scala 27:72]
  assign io_out_cf_pc = io_in_pc; // @[IDU.scala 23:24]
  assign io_out_ctrl_src1Type = {{1'd0}, _io_out_ctrl_src1Type_T_19}; // @[Mux.scala 27:72]
  assign io_out_ctrl_src2Type = {{1'd0}, _io_out_ctrl_src2Type_T_19}; // @[Mux.scala 27:72]
  assign io_out_ctrl_funcType = _T_1 ? 3'h0 : _T_193; // @[Lookup.scala 33:37]
  assign io_out_ctrl_funcOpType = _T_1 ? 7'h40 : _T_241; // @[Lookup.scala 33:37]
  assign io_out_ctrl_rfSrc1 = io_out_ctrl_src1Type == 2'h0 ? src1Addr : 5'h0; // @[IDU.scala 25:32]
  assign io_out_ctrl_rfSrc2 = io_out_ctrl_src2Type == 2'h0 ? src2Addr : 5'h0; // @[IDU.scala 26:32]
  assign io_out_ctrl_rfrd = instrType[2] ? rdAddr : 5'h0; // @[IDU.scala 27:32]
  assign io_out_data_imm = _imm_T_22 | _imm_T_19; // @[Mux.scala 27:72]
  assign io_out_data_uimm_ext = funcType == 3'h3 & funcOpType[2] ? _uimm_ext_T_4 : 64'h0; // @[IDU.scala 44:29]
endmodule
module IDUtoEXU(
  input  [63:0] io_in_cf_pc,
  input  [1:0]  io_in_ctrl_src1Type,
  input  [1:0]  io_in_ctrl_src2Type,
  input  [2:0]  io_in_ctrl_funcType,
  input  [6:0]  io_in_ctrl_funcOpType,
  input  [4:0]  io_in_ctrl_rfrd,
  input  [63:0] io_in_data_imm,
  input  [63:0] io_in_data_uimm_ext,
  input  [63:0] io_src1,
  input  [63:0] io_src2,
  output [63:0] io_out_cf_pc,
  output [2:0]  io_out_ctrl_funcType,
  output [6:0]  io_out_ctrl_funcOpType,
  output [4:0]  io_out_ctrl_rfrd,
  output [63:0] io_out_data_src1,
  output [63:0] io_out_data_src2,
  output [63:0] io_out_data_imm
);
  wire  _io_out_data_src1_T = 2'h0 == io_in_ctrl_src1Type; // @[utils.scala 8:34]
  wire  _io_out_data_src1_T_1 = 2'h1 == io_in_ctrl_src1Type; // @[utils.scala 8:34]
  wire  _io_out_data_src1_T_2 = 2'h2 == io_in_ctrl_src1Type; // @[utils.scala 8:34]
  wire [63:0] _io_out_data_src1_T_3 = _io_out_data_src1_T ? io_src1 : 64'h0; // @[Mux.scala 27:72]
  wire [63:0] _io_out_data_src1_T_4 = _io_out_data_src1_T_1 ? io_in_cf_pc : 64'h0; // @[Mux.scala 27:72]
  wire [63:0] _io_out_data_src1_T_5 = _io_out_data_src1_T_2 ? io_in_data_uimm_ext : 64'h0; // @[Mux.scala 27:72]
  wire [63:0] _io_out_data_src1_T_6 = _io_out_data_src1_T_3 | _io_out_data_src1_T_4; // @[Mux.scala 27:72]
  wire  _io_out_data_src2_T = 2'h0 == io_in_ctrl_src2Type; // @[utils.scala 8:34]
  wire  _io_out_data_src2_T_1 = 2'h1 == io_in_ctrl_src2Type; // @[utils.scala 8:34]
  wire [63:0] _io_out_data_src2_T_2 = _io_out_data_src2_T ? io_src2 : 64'h0; // @[Mux.scala 27:72]
  wire [63:0] _io_out_data_src2_T_3 = _io_out_data_src2_T_1 ? io_in_data_imm : 64'h0; // @[Mux.scala 27:72]
  assign io_out_cf_pc = io_in_cf_pc; // @[IDUtoEXU.scala 39:17]
  assign io_out_ctrl_funcType = io_in_ctrl_funcType; // @[IDUtoEXU.scala 40:17]
  assign io_out_ctrl_funcOpType = io_in_ctrl_funcOpType; // @[IDUtoEXU.scala 40:17]
  assign io_out_ctrl_rfrd = io_in_ctrl_rfrd; // @[IDUtoEXU.scala 40:17]
  assign io_out_data_src1 = _io_out_data_src1_T_6 | _io_out_data_src1_T_5; // @[Mux.scala 27:72]
  assign io_out_data_src2 = _io_out_data_src2_T_2 | _io_out_data_src2_T_3; // @[Mux.scala 27:72]
  assign io_out_data_imm = io_in_data_imm; // @[IDUtoEXU.scala 38:21]
endmodule
module ALU(
  input  [6:0]  io_in_ctrl_funcOpType,
  input  [63:0] io_in_data_src1,
  input  [63:0] io_in_data_src2,
  output [63:0] io_out_aluRes
);
  wire [5:0] shamt = io_in_ctrl_funcOpType[5] ? {{1'd0}, io_in_data_src2[4:0]} : io_in_data_src2[5:0]; // @[ALU.scala 41:18]
  wire [63:0] _res_T_1 = io_in_data_src1 + io_in_data_src2; // @[ALU.scala 43:32]
  wire [126:0] _GEN_0 = {{63'd0}, io_in_data_src1}; // @[ALU.scala 44:32]
  wire [126:0] _res_T_2 = _GEN_0 << shamt; // @[ALU.scala 44:32]
  wire  res_lo = $signed(io_in_data_src1) < $signed(io_in_data_src2); // @[ALU.scala 45:64]
  wire [63:0] _res_T_5 = {63'h0,res_lo}; // @[Cat.scala 30:58]
  wire  _res_T_6 = io_in_data_src1 < io_in_data_src2; // @[ALU.scala 46:32]
  wire [63:0] _res_T_7 = io_in_data_src1 ^ io_in_data_src2; // @[ALU.scala 47:32]
  wire [63:0] _res_T_8 = io_in_data_src1 >> shamt; // @[ALU.scala 48:32]
  wire [63:0] _res_T_9 = io_in_data_src1 | io_in_data_src2; // @[ALU.scala 49:32]
  wire [63:0] _res_T_10 = io_in_data_src1 & io_in_data_src2; // @[ALU.scala 50:32]
  wire [63:0] _res_T_12 = io_in_data_src1 - io_in_data_src2; // @[ALU.scala 51:32]
  wire [63:0] _res_T_15 = $signed(io_in_data_src1) >>> shamt; // @[ALU.scala 52:50]
  wire [31:0] _res_T_22 = io_in_data_src1[31:0] >> shamt; // @[ALU.scala 56:38]
  wire [31:0] _res_T_24 = io_in_data_src1[31:0]; // @[ALU.scala 57:45]
  wire [31:0] _res_T_26 = $signed(_res_T_24) >>> shamt; // @[ALU.scala 57:64]
  wire  _res_T_27 = 7'h40 == io_in_ctrl_funcOpType; // @[utils.scala 8:34]
  wire  _res_T_28 = 7'h1 == io_in_ctrl_funcOpType; // @[utils.scala 8:34]
  wire  _res_T_29 = 7'h2 == io_in_ctrl_funcOpType; // @[utils.scala 8:34]
  wire  _res_T_30 = 7'h3 == io_in_ctrl_funcOpType; // @[utils.scala 8:34]
  wire  _res_T_31 = 7'h4 == io_in_ctrl_funcOpType; // @[utils.scala 8:34]
  wire  _res_T_32 = 7'h5 == io_in_ctrl_funcOpType; // @[utils.scala 8:34]
  wire  _res_T_33 = 7'h6 == io_in_ctrl_funcOpType; // @[utils.scala 8:34]
  wire  _res_T_34 = 7'h7 == io_in_ctrl_funcOpType; // @[utils.scala 8:34]
  wire  _res_T_35 = 7'h8 == io_in_ctrl_funcOpType; // @[utils.scala 8:34]
  wire  _res_T_36 = 7'hd == io_in_ctrl_funcOpType; // @[utils.scala 8:34]
  wire  _res_T_37 = 7'h60 == io_in_ctrl_funcOpType; // @[utils.scala 8:34]
  wire  _res_T_38 = 7'h28 == io_in_ctrl_funcOpType; // @[utils.scala 8:34]
  wire  _res_T_39 = 7'h21 == io_in_ctrl_funcOpType; // @[utils.scala 8:34]
  wire  _res_T_40 = 7'h25 == io_in_ctrl_funcOpType; // @[utils.scala 8:34]
  wire  _res_T_41 = 7'h2d == io_in_ctrl_funcOpType; // @[utils.scala 8:34]
  wire  _res_T_42 = 7'hf == io_in_ctrl_funcOpType; // @[utils.scala 8:34]
  wire [63:0] _res_T_43 = _res_T_27 ? _res_T_1 : 64'h0; // @[Mux.scala 27:72]
  wire [126:0] _res_T_44 = _res_T_28 ? _res_T_2 : 127'h0; // @[Mux.scala 27:72]
  wire [63:0] _res_T_45 = _res_T_29 ? _res_T_5 : 64'h0; // @[Mux.scala 27:72]
  wire  _res_T_46 = _res_T_30 & _res_T_6; // @[Mux.scala 27:72]
  wire [63:0] _res_T_47 = _res_T_31 ? _res_T_7 : 64'h0; // @[Mux.scala 27:72]
  wire [63:0] _res_T_48 = _res_T_32 ? _res_T_8 : 64'h0; // @[Mux.scala 27:72]
  wire [63:0] _res_T_49 = _res_T_33 ? _res_T_9 : 64'h0; // @[Mux.scala 27:72]
  wire [63:0] _res_T_50 = _res_T_34 ? _res_T_10 : 64'h0; // @[Mux.scala 27:72]
  wire [63:0] _res_T_51 = _res_T_35 ? _res_T_12 : 64'h0; // @[Mux.scala 27:72]
  wire [63:0] _res_T_52 = _res_T_36 ? _res_T_15 : 64'h0; // @[Mux.scala 27:72]
  wire [63:0] _res_T_53 = _res_T_37 ? _res_T_1 : 64'h0; // @[Mux.scala 27:72]
  wire [63:0] _res_T_54 = _res_T_38 ? _res_T_12 : 64'h0; // @[Mux.scala 27:72]
  wire [126:0] _res_T_55 = _res_T_39 ? _res_T_2 : 127'h0; // @[Mux.scala 27:72]
  wire [31:0] _res_T_56 = _res_T_40 ? _res_T_22 : 32'h0; // @[Mux.scala 27:72]
  wire [31:0] _res_T_57 = _res_T_41 ? _res_T_26 : 32'h0; // @[Mux.scala 27:72]
  wire [63:0] _res_T_58 = _res_T_42 ? io_in_data_src2 : 64'h0; // @[Mux.scala 27:72]
  wire [126:0] _GEN_2 = {{63'd0}, _res_T_43}; // @[Mux.scala 27:72]
  wire [126:0] _res_T_59 = _GEN_2 | _res_T_44; // @[Mux.scala 27:72]
  wire [126:0] _GEN_3 = {{63'd0}, _res_T_45}; // @[Mux.scala 27:72]
  wire [126:0] _res_T_60 = _res_T_59 | _GEN_3; // @[Mux.scala 27:72]
  wire [126:0] _GEN_4 = {{126'd0}, _res_T_46}; // @[Mux.scala 27:72]
  wire [126:0] _res_T_61 = _res_T_60 | _GEN_4; // @[Mux.scala 27:72]
  wire [126:0] _GEN_5 = {{63'd0}, _res_T_47}; // @[Mux.scala 27:72]
  wire [126:0] _res_T_62 = _res_T_61 | _GEN_5; // @[Mux.scala 27:72]
  wire [126:0] _GEN_6 = {{63'd0}, _res_T_48}; // @[Mux.scala 27:72]
  wire [126:0] _res_T_63 = _res_T_62 | _GEN_6; // @[Mux.scala 27:72]
  wire [126:0] _GEN_7 = {{63'd0}, _res_T_49}; // @[Mux.scala 27:72]
  wire [126:0] _res_T_64 = _res_T_63 | _GEN_7; // @[Mux.scala 27:72]
  wire [126:0] _GEN_8 = {{63'd0}, _res_T_50}; // @[Mux.scala 27:72]
  wire [126:0] _res_T_65 = _res_T_64 | _GEN_8; // @[Mux.scala 27:72]
  wire [126:0] _GEN_9 = {{63'd0}, _res_T_51}; // @[Mux.scala 27:72]
  wire [126:0] _res_T_66 = _res_T_65 | _GEN_9; // @[Mux.scala 27:72]
  wire [126:0] _GEN_10 = {{63'd0}, _res_T_52}; // @[Mux.scala 27:72]
  wire [126:0] _res_T_67 = _res_T_66 | _GEN_10; // @[Mux.scala 27:72]
  wire [126:0] _GEN_11 = {{63'd0}, _res_T_53}; // @[Mux.scala 27:72]
  wire [126:0] _res_T_68 = _res_T_67 | _GEN_11; // @[Mux.scala 27:72]
  wire [126:0] _GEN_12 = {{63'd0}, _res_T_54}; // @[Mux.scala 27:72]
  wire [126:0] _res_T_69 = _res_T_68 | _GEN_12; // @[Mux.scala 27:72]
  wire [126:0] _res_T_70 = _res_T_69 | _res_T_55; // @[Mux.scala 27:72]
  wire [126:0] _GEN_13 = {{95'd0}, _res_T_56}; // @[Mux.scala 27:72]
  wire [126:0] _res_T_71 = _res_T_70 | _GEN_13; // @[Mux.scala 27:72]
  wire [126:0] _GEN_14 = {{95'd0}, _res_T_57}; // @[Mux.scala 27:72]
  wire [126:0] _res_T_72 = _res_T_71 | _GEN_14; // @[Mux.scala 27:72]
  wire [126:0] _GEN_15 = {{63'd0}, _res_T_58}; // @[Mux.scala 27:72]
  wire [126:0] res = _res_T_72 | _GEN_15; // @[Mux.scala 27:72]
  wire [31:0] io_out_aluRes_lo = res[31:0]; // @[ALU.scala 61:78]
  wire  io_out_aluRes_signBit = io_out_aluRes_lo[31]; // @[utils.scala 14:20]
  wire [31:0] io_out_aluRes_hi = io_out_aluRes_signBit ? 32'hffffffff : 32'h0; // @[Bitwise.scala 72:12]
  wire [63:0] _io_out_aluRes_T_2 = {io_out_aluRes_hi,io_out_aluRes_lo}; // @[Cat.scala 30:58]
  wire [126:0] _io_out_aluRes_T_3 = io_in_ctrl_funcOpType[5] ? {{63'd0}, _io_out_aluRes_T_2} : res; // @[ALU.scala 61:23]
  assign io_out_aluRes = _io_out_aluRes_T_3[63:0]; // @[ALU.scala 61:17]
endmodule
module LSU(
  input         clock,
  input         reset,
  input  [6:0]  io_in_ctrl_funcOpType,
  input  [63:0] io_in_data_src1,
  input  [63:0] io_in_data_src2,
  input  [63:0] io_in_data_imm,
  output [63:0] io_out_rdata,
  input         io_lsuaxi_aw_ready,
  output        io_lsuaxi_aw_valid,
  output [31:0] io_lsuaxi_aw_bits_addr,
  input         io_lsuaxi_w_ready,
  output        io_lsuaxi_w_valid,
  output [63:0] io_lsuaxi_w_bits_data,
  output        io_lsuaxi_b_ready,
  input         io_lsuaxi_b_valid,
  input         io_lsuaxi_ar_ready,
  output        io_lsuaxi_ar_valid,
  output [31:0] io_lsuaxi_ar_bits_addr,
  output        io_lsuaxi_r_ready,
  input         io_lsuaxi_r_valid,
  input  [63:0] io_lsuaxi_r_bits_data
);
`ifdef RANDOMIZE_REG_INIT
  reg [31:0] _RAND_0;
  reg [63:0] _RAND_1;
  reg [63:0] _RAND_2;
`endif // RANDOMIZE_REG_INIT
  wire [63:0] addr = io_in_data_src1 + io_in_data_imm; // @[LSU.scala 60:30]
  wire  isStore = io_in_ctrl_funcOpType[3]; // @[LSU.scala 24:39]
  wire  isLoad = ~isStore; // @[LSU.scala 25:34]
  reg [2:0] inflight_type; // @[LSU.scala 73:30]
  wire  ar_hs = io_lsuaxi_ar_valid & io_lsuaxi_ar_ready; // @[LSU.scala 86:28]
  wire  r_hs = io_lsuaxi_r_valid & io_lsuaxi_r_ready; // @[LSU.scala 87:26]
  wire  b_hs = io_lsuaxi_b_valid & io_lsuaxi_r_ready; // @[LSU.scala 88:26]
  wire  aw_hs = io_lsuaxi_aw_valid & io_lsuaxi_aw_ready; // @[LSU.scala 89:28]
  wire  w_hs = io_lsuaxi_w_valid & io_lsuaxi_w_ready; // @[LSU.scala 90:26]
  wire  _T = inflight_type == 3'h0; // @[LSU.scala 81:19]
  wire  _T_3 = _T & isStore; // @[LSU.scala 97:35]
  wire [2:0] _GEN_0 = _T & isStore ? 3'h3 : inflight_type; // @[LSU.scala 97:46 LSU.scala 75:19 LSU.scala 73:30]
  wire [2:0] _GEN_3 = _T & isLoad ? 3'h1 : _GEN_0; // @[LSU.scala 92:39 LSU.scala 75:19]
  wire  _GEN_5 = _T & isLoad ? 1'h0 : _T_3; // @[LSU.scala 92:39 LSU.scala 95:18]
  reg [63:0] rdata; // @[LSU.scala 108:22]
  wire [63:0] _GEN_6 = ar_hs ? 64'h0 : addr; // @[LSU.scala 126:14 LSU.scala 127:23 LSU.scala 125:20]
  wire [2:0] _GEN_7 = ar_hs ? 3'h2 : _GEN_3; // @[LSU.scala 126:14 LSU.scala 75:19]
  wire [2:0] _GEN_9 = r_hs ? 3'h0 : _GEN_7; // @[LSU.scala 131:13 LSU.scala 75:19]
  wire [1:0] size = io_in_ctrl_funcOpType[1:0]; // @[LSU.scala 139:35]
  wire [7:0] wdata_lo = io_in_data_src2[7:0]; // @[LSU.scala 52:30]
  wire [63:0] _wdata_T = {56'h0,wdata_lo}; // @[Cat.scala 30:58]
  wire [15:0] wdata_lo_1 = io_in_data_src2[15:0]; // @[LSU.scala 53:30]
  wire [63:0] _wdata_T_1 = {48'h0,wdata_lo_1}; // @[Cat.scala 30:58]
  wire [31:0] wdata_lo_2 = io_in_data_src2[31:0]; // @[LSU.scala 54:30]
  wire [63:0] _wdata_T_2 = {32'h0,wdata_lo_2}; // @[Cat.scala 30:58]
  wire  _wdata_T_5 = 2'h0 == size; // @[utils.scala 8:34]
  wire  _wdata_T_6 = 2'h1 == size; // @[utils.scala 8:34]
  wire  _wdata_T_7 = 2'h2 == size; // @[utils.scala 8:34]
  wire  _wdata_T_8 = 2'h3 == size; // @[utils.scala 8:34]
  wire [63:0] _wdata_T_9 = _wdata_T_5 ? _wdata_T : 64'h0; // @[Mux.scala 27:72]
  wire [63:0] _wdata_T_10 = _wdata_T_6 ? _wdata_T_1 : 64'h0; // @[Mux.scala 27:72]
  wire [63:0] _wdata_T_11 = _wdata_T_7 ? _wdata_T_2 : 64'h0; // @[Mux.scala 27:72]
  wire [63:0] _wdata_T_12 = _wdata_T_8 ? io_in_data_src2 : 64'h0; // @[Mux.scala 27:72]
  wire [63:0] _wdata_T_13 = _wdata_T_9 | _wdata_T_10; // @[Mux.scala 27:72]
  wire [63:0] _wdata_T_14 = _wdata_T_13 | _wdata_T_11; // @[Mux.scala 27:72]
  reg [63:0] aw_addr; // @[LSU.scala 151:24]
  wire  _rdataSel_T_9 = 3'h0 == addr[2:0]; // @[utils.scala 8:34]
  wire  _rdataSel_T_10 = 3'h1 == addr[2:0]; // @[utils.scala 8:34]
  wire  _rdataSel_T_11 = 3'h2 == addr[2:0]; // @[utils.scala 8:34]
  wire  _rdataSel_T_12 = 3'h3 == addr[2:0]; // @[utils.scala 8:34]
  wire  _rdataSel_T_13 = 3'h4 == addr[2:0]; // @[utils.scala 8:34]
  wire  _rdataSel_T_14 = 3'h5 == addr[2:0]; // @[utils.scala 8:34]
  wire  _rdataSel_T_15 = 3'h6 == addr[2:0]; // @[utils.scala 8:34]
  wire  _rdataSel_T_16 = 3'h7 == addr[2:0]; // @[utils.scala 8:34]
  wire [63:0] _rdataSel_T_17 = _rdataSel_T_9 ? rdata : 64'h0; // @[Mux.scala 27:72]
  wire [55:0] _rdataSel_T_18 = _rdataSel_T_10 ? rdata[63:8] : 56'h0; // @[Mux.scala 27:72]
  wire [47:0] _rdataSel_T_19 = _rdataSel_T_11 ? rdata[63:16] : 48'h0; // @[Mux.scala 27:72]
  wire [39:0] _rdataSel_T_20 = _rdataSel_T_12 ? rdata[63:24] : 40'h0; // @[Mux.scala 27:72]
  wire [31:0] _rdataSel_T_21 = _rdataSel_T_13 ? rdata[63:32] : 32'h0; // @[Mux.scala 27:72]
  wire [23:0] _rdataSel_T_22 = _rdataSel_T_14 ? rdata[63:40] : 24'h0; // @[Mux.scala 27:72]
  wire [15:0] _rdataSel_T_23 = _rdataSel_T_15 ? rdata[63:48] : 16'h0; // @[Mux.scala 27:72]
  wire [7:0] _rdataSel_T_24 = _rdataSel_T_16 ? rdata[63:56] : 8'h0; // @[Mux.scala 27:72]
  wire [63:0] _GEN_16 = {{8'd0}, _rdataSel_T_18}; // @[Mux.scala 27:72]
  wire [63:0] _rdataSel_T_25 = _rdataSel_T_17 | _GEN_16; // @[Mux.scala 27:72]
  wire [63:0] _GEN_17 = {{16'd0}, _rdataSel_T_19}; // @[Mux.scala 27:72]
  wire [63:0] _rdataSel_T_26 = _rdataSel_T_25 | _GEN_17; // @[Mux.scala 27:72]
  wire [63:0] _GEN_18 = {{24'd0}, _rdataSel_T_20}; // @[Mux.scala 27:72]
  wire [63:0] _rdataSel_T_27 = _rdataSel_T_26 | _GEN_18; // @[Mux.scala 27:72]
  wire [63:0] _GEN_19 = {{32'd0}, _rdataSel_T_21}; // @[Mux.scala 27:72]
  wire [63:0] _rdataSel_T_28 = _rdataSel_T_27 | _GEN_19; // @[Mux.scala 27:72]
  wire [63:0] _GEN_20 = {{40'd0}, _rdataSel_T_22}; // @[Mux.scala 27:72]
  wire [63:0] _rdataSel_T_29 = _rdataSel_T_28 | _GEN_20; // @[Mux.scala 27:72]
  wire [63:0] _GEN_21 = {{48'd0}, _rdataSel_T_23}; // @[Mux.scala 27:72]
  wire [63:0] _rdataSel_T_30 = _rdataSel_T_29 | _GEN_21; // @[Mux.scala 27:72]
  wire [63:0] _GEN_22 = {{56'd0}, _rdataSel_T_24}; // @[Mux.scala 27:72]
  wire [63:0] rdataSel = _rdataSel_T_30 | _GEN_22; // @[Mux.scala 27:72]
  wire [7:0] io_out_rdata_lo = rdataSel[7:0]; // @[LSU.scala 185:39]
  wire  io_out_rdata_signBit = io_out_rdata_lo[7]; // @[utils.scala 14:20]
  wire [55:0] io_out_rdata_hi = io_out_rdata_signBit ? 56'hffffffffffffff : 56'h0; // @[Bitwise.scala 72:12]
  wire [63:0] _io_out_rdata_T_1 = {io_out_rdata_hi,io_out_rdata_lo}; // @[Cat.scala 30:58]
  wire [15:0] io_out_rdata_lo_1 = rdataSel[15:0]; // @[LSU.scala 186:39]
  wire  io_out_rdata_signBit_1 = io_out_rdata_lo_1[15]; // @[utils.scala 14:20]
  wire [47:0] io_out_rdata_hi_1 = io_out_rdata_signBit_1 ? 48'hffffffffffff : 48'h0; // @[Bitwise.scala 72:12]
  wire [63:0] _io_out_rdata_T_3 = {io_out_rdata_hi_1,io_out_rdata_lo_1}; // @[Cat.scala 30:58]
  wire [31:0] io_out_rdata_lo_2 = rdataSel[31:0]; // @[LSU.scala 187:39]
  wire  io_out_rdata_signBit_2 = io_out_rdata_lo_2[31]; // @[utils.scala 14:20]
  wire [31:0] io_out_rdata_hi_2 = io_out_rdata_signBit_2 ? 32'hffffffff : 32'h0; // @[Bitwise.scala 72:12]
  wire [63:0] _io_out_rdata_T_5 = {io_out_rdata_hi_2,io_out_rdata_lo_2}; // @[Cat.scala 30:58]
  wire [63:0] _io_out_rdata_T_8 = {56'h0,io_out_rdata_lo}; // @[Cat.scala 30:58]
  wire [63:0] _io_out_rdata_T_9 = {48'h0,io_out_rdata_lo_1}; // @[Cat.scala 30:58]
  wire [63:0] _io_out_rdata_T_10 = {32'h0,io_out_rdata_lo_2}; // @[Cat.scala 30:58]
  wire  _io_out_rdata_T_11 = 7'h0 == io_in_ctrl_funcOpType; // @[utils.scala 8:34]
  wire  _io_out_rdata_T_12 = 7'h1 == io_in_ctrl_funcOpType; // @[utils.scala 8:34]
  wire  _io_out_rdata_T_13 = 7'h2 == io_in_ctrl_funcOpType; // @[utils.scala 8:34]
  wire  _io_out_rdata_T_14 = 7'h3 == io_in_ctrl_funcOpType; // @[utils.scala 8:34]
  wire  _io_out_rdata_T_15 = 7'h4 == io_in_ctrl_funcOpType; // @[utils.scala 8:34]
  wire  _io_out_rdata_T_16 = 7'h5 == io_in_ctrl_funcOpType; // @[utils.scala 8:34]
  wire  _io_out_rdata_T_17 = 7'h6 == io_in_ctrl_funcOpType; // @[utils.scala 8:34]
  wire [63:0] _io_out_rdata_T_18 = _io_out_rdata_T_11 ? _io_out_rdata_T_1 : 64'h0; // @[Mux.scala 27:72]
  wire [63:0] _io_out_rdata_T_19 = _io_out_rdata_T_12 ? _io_out_rdata_T_3 : 64'h0; // @[Mux.scala 27:72]
  wire [63:0] _io_out_rdata_T_20 = _io_out_rdata_T_13 ? _io_out_rdata_T_5 : 64'h0; // @[Mux.scala 27:72]
  wire [63:0] _io_out_rdata_T_21 = _io_out_rdata_T_14 ? rdataSel : 64'h0; // @[Mux.scala 27:72]
  wire [63:0] _io_out_rdata_T_22 = _io_out_rdata_T_15 ? _io_out_rdata_T_8 : 64'h0; // @[Mux.scala 27:72]
  wire [63:0] _io_out_rdata_T_23 = _io_out_rdata_T_16 ? _io_out_rdata_T_9 : 64'h0; // @[Mux.scala 27:72]
  wire [63:0] _io_out_rdata_T_24 = _io_out_rdata_T_17 ? _io_out_rdata_T_10 : 64'h0; // @[Mux.scala 27:72]
  wire [63:0] _io_out_rdata_T_25 = _io_out_rdata_T_18 | _io_out_rdata_T_19; // @[Mux.scala 27:72]
  wire [63:0] _io_out_rdata_T_26 = _io_out_rdata_T_25 | _io_out_rdata_T_20; // @[Mux.scala 27:72]
  wire [63:0] _io_out_rdata_T_27 = _io_out_rdata_T_26 | _io_out_rdata_T_21; // @[Mux.scala 27:72]
  wire [63:0] _io_out_rdata_T_28 = _io_out_rdata_T_27 | _io_out_rdata_T_22; // @[Mux.scala 27:72]
  wire [63:0] _io_out_rdata_T_29 = _io_out_rdata_T_28 | _io_out_rdata_T_23; // @[Mux.scala 27:72]
  assign io_out_rdata = _io_out_rdata_T_29 | _io_out_rdata_T_24; // @[Mux.scala 27:72]
  assign io_lsuaxi_aw_valid = b_hs ? 1'h0 : _GEN_5; // @[LSU.scala 166:13 LSU.scala 168:18]
  assign io_lsuaxi_aw_bits_addr = aw_addr[31:0]; // @[LSU.scala 152:20]
  assign io_lsuaxi_w_valid = b_hs ? 1'h0 : _GEN_5; // @[LSU.scala 166:13 LSU.scala 168:18]
  assign io_lsuaxi_w_bits_data = _wdata_T_14 | _wdata_T_12; // @[Mux.scala 27:72]
  assign io_lsuaxi_b_ready = inflight_type == 3'h5; // @[LSU.scala 81:19]
  assign io_lsuaxi_ar_valid = 1'h0; // @[LSU.scala 119:21]
  assign io_lsuaxi_ar_bits_addr = _GEN_6[31:0];
  assign io_lsuaxi_r_ready = inflight_type == 3'h2; // @[LSU.scala 81:19]
  always @(posedge clock) begin
    if (reset) begin // @[LSU.scala 73:30]
      inflight_type <= 3'h0; // @[LSU.scala 73:30]
    end else if (b_hs) begin // @[LSU.scala 166:13]
      inflight_type <= 3'h0; // @[LSU.scala 75:19]
    end else if (w_hs) begin // @[LSU.scala 161:14]
      inflight_type <= 3'h5; // @[LSU.scala 75:19]
    end else if (aw_hs) begin // @[LSU.scala 153:14]
      inflight_type <= 3'h4; // @[LSU.scala 75:19]
    end else begin
      inflight_type <= _GEN_9;
    end
    if (reset) begin // @[LSU.scala 108:22]
      rdata <= 64'h0; // @[LSU.scala 108:22]
    end else if (r_hs) begin // @[LSU.scala 131:13]
      rdata <= io_lsuaxi_r_bits_data; // @[LSU.scala 132:11]
    end
    if (reset) begin // @[LSU.scala 151:24]
      aw_addr <= 64'h0; // @[LSU.scala 151:24]
    end else if (aw_hs) begin // @[LSU.scala 153:14]
      aw_addr <= addr; // @[LSU.scala 154:14]
    end
  end
// Register and memory initialization
`ifdef RANDOMIZE_GARBAGE_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_INVALID_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_REG_INIT
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_MEM_INIT
`define RANDOMIZE
`endif
`ifndef RANDOM
`define RANDOM $random
`endif
`ifdef RANDOMIZE_MEM_INIT
  integer initvar;
`endif
`ifndef SYNTHESIS
`ifdef FIRRTL_BEFORE_INITIAL
`FIRRTL_BEFORE_INITIAL
`endif
initial begin
  `ifdef RANDOMIZE
    `ifdef INIT_RANDOM
      `INIT_RANDOM
    `endif
    `ifndef VERILATOR
      `ifdef RANDOMIZE_DELAY
        #`RANDOMIZE_DELAY begin end
      `else
        #0.002 begin end
      `endif
    `endif
`ifdef RANDOMIZE_REG_INIT
  _RAND_0 = {1{`RANDOM}};
  inflight_type = _RAND_0[2:0];
  _RAND_1 = {2{`RANDOM}};
  rdata = _RAND_1[63:0];
  _RAND_2 = {2{`RANDOM}};
  aw_addr = _RAND_2[63:0];
`endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`ifdef FIRRTL_AFTER_INITIAL
`FIRRTL_AFTER_INITIAL
`endif
`endif // SYNTHESIS
endmodule
module BRU(
  input  [63:0] io_in_cf_pc,
  input  [2:0]  io_in_ctrl_funcType,
  input  [6:0]  io_in_ctrl_funcOpType,
  input  [63:0] io_in_data_src1,
  input  [63:0] io_in_data_src2,
  input  [63:0] io_in_data_imm,
  output [63:0] io_out_new_pc,
  output        io_out_valid
);
  wire  _io_out_valid_T_1 = io_in_data_src1 == io_in_data_src2; // @[BRU.scala 37:31]
  wire  _io_out_valid_T_2 = io_in_data_src1 != io_in_data_src2; // @[BRU.scala 38:31]
  wire  _io_out_valid_T_5 = $signed(io_in_data_src1) < $signed(io_in_data_src2); // @[BRU.scala 39:40]
  wire  _io_out_valid_T_8 = $signed(io_in_data_src1) >= $signed(io_in_data_src2); // @[BRU.scala 40:40]
  wire  _io_out_valid_T_9 = io_in_data_src1 < io_in_data_src2; // @[BRU.scala 41:31]
  wire  _io_out_valid_T_10 = io_in_data_src1 >= io_in_data_src2; // @[BRU.scala 42:31]
  wire  _io_out_valid_T_11 = 7'h58 == io_in_ctrl_funcOpType; // @[utils.scala 8:34]
  wire  _io_out_valid_T_12 = 7'h5a == io_in_ctrl_funcOpType; // @[utils.scala 8:34]
  wire  _io_out_valid_T_13 = 7'h10 == io_in_ctrl_funcOpType; // @[utils.scala 8:34]
  wire  _io_out_valid_T_14 = 7'h11 == io_in_ctrl_funcOpType; // @[utils.scala 8:34]
  wire  _io_out_valid_T_15 = 7'h14 == io_in_ctrl_funcOpType; // @[utils.scala 8:34]
  wire  _io_out_valid_T_16 = 7'h15 == io_in_ctrl_funcOpType; // @[utils.scala 8:34]
  wire  _io_out_valid_T_17 = 7'h16 == io_in_ctrl_funcOpType; // @[utils.scala 8:34]
  wire  _io_out_valid_T_18 = 7'h17 == io_in_ctrl_funcOpType; // @[utils.scala 8:34]
  wire  _io_out_valid_T_33 = _io_out_valid_T_11 | _io_out_valid_T_12 | _io_out_valid_T_13 & _io_out_valid_T_1 |
    _io_out_valid_T_14 & _io_out_valid_T_2 | _io_out_valid_T_15 & _io_out_valid_T_5 | _io_out_valid_T_16 &
    _io_out_valid_T_8 | _io_out_valid_T_17 & _io_out_valid_T_9 | _io_out_valid_T_18 & _io_out_valid_T_10; // @[Mux.scala 27:72]
  wire [63:0] _io_out_new_pc_T_2 = io_in_data_src1 + io_in_data_imm; // @[BRU.scala 46:21]
  wire [63:0] _io_out_new_pc_T_4 = io_in_cf_pc + io_in_data_imm; // @[BRU.scala 46:51]
  assign io_out_new_pc = io_in_ctrl_funcOpType == 7'h5a ? _io_out_new_pc_T_2 : _io_out_new_pc_T_4; // @[BRU.scala 45:23]
  assign io_out_valid = io_in_ctrl_funcType == 3'h5 & _io_out_valid_T_33; // @[BRU.scala 34:58]
endmodule
module CSR(
  input         clock,
  input         reset,
  input         io_ena,
  input  [63:0] io_in_cf_pc,
  input  [6:0]  io_in_ctrl_funcOpType,
  input  [63:0] io_in_data_src2,
  input  [63:0] io_in_data_imm,
  output [63:0] io_out_rdata,
  output [63:0] io_out_jmp_new_pc,
  output        io_out_jmp_valid
);
`ifdef RANDOMIZE_REG_INIT
  reg [31:0] _RAND_0;
  reg [31:0] _RAND_1;
  reg [63:0] _RAND_2;
  reg [63:0] _RAND_3;
  reg [63:0] _RAND_4;
  reg [63:0] _RAND_5;
  reg [63:0] _RAND_6;
  reg [63:0] _RAND_7;
  reg [63:0] _RAND_8;
  reg [63:0] _RAND_9;
  reg [63:0] _RAND_10;
  reg [63:0] _RAND_11;
  reg [63:0] _RAND_12;
  reg [63:0] _RAND_13;
`endif // RANDOMIZE_REG_INIT
  reg  status_PIE_M; // @[CSR.scala 263:39]
  reg  status_IE_M; // @[CSR.scala 263:39]
  reg [63:0] medeleg; // @[CSR.scala 265:37]
  reg [63:0] mideleg; // @[CSR.scala 266:37]
  reg [63:0] mie; // @[CSR.scala 267:37]
  reg [63:0] mtvec; // @[CSR.scala 268:37]
  reg [63:0] mcounteren; // @[CSR.scala 269:37]
  reg [63:0] mscratch; // @[CSR.scala 273:37]
  reg [63:0] mepc; // @[CSR.scala 274:37]
  reg [63:0] mcause; // @[CSR.scala 275:37]
  reg [63:0] mtval; // @[CSR.scala 276:37]
  reg [63:0] mip; // @[CSR.scala 277:37]
  reg [63:0] mhpmcounter_0; // @[CSR.scala 286:42]
  reg [63:0] mhpmcounter_2; // @[CSR.scala 286:42]
  wire [61:0] mtvec_base = mtvec[63:2]; // @[CSR.scala 313:35]
  wire [1:0] mtvec_mode = mtvec[1:0]; // @[CSR.scala 314:35]
  wire [63:0] _T = {49'h0,6'hc,1'h0,status_PIE_M,1'h0,2'h0,status_IE_M,3'h0}; // @[CSR.scala 338:43]
  wire [11:0] addr = io_in_data_imm[11:0]; // @[CSR.scala 49:36]
  wire  _rdata_T_8 = 12'h300 == addr; // @[Mux.scala 80:60]
  wire [63:0] _rdata_T_9 = 12'h300 == addr ? _T : 64'h0; // @[Mux.scala 80:57]
  wire [63:0] _rdata_T_11 = 12'h301 == addr ? 64'h8000000000000008 : _rdata_T_9; // @[Mux.scala 80:57]
  wire  _rdata_T_12 = 12'h302 == addr; // @[Mux.scala 80:60]
  wire [63:0] _rdata_T_13 = 12'h302 == addr ? medeleg : _rdata_T_11; // @[Mux.scala 80:57]
  wire  _rdata_T_14 = 12'h303 == addr; // @[Mux.scala 80:60]
  wire [63:0] _rdata_T_15 = 12'h303 == addr ? mideleg : _rdata_T_13; // @[Mux.scala 80:57]
  wire  _rdata_T_16 = 12'h304 == addr; // @[Mux.scala 80:60]
  wire [63:0] _rdata_T_17 = 12'h304 == addr ? mie : _rdata_T_15; // @[Mux.scala 80:57]
  wire  _rdata_T_18 = 12'h305 == addr; // @[Mux.scala 80:60]
  wire [63:0] _rdata_T_19 = 12'h305 == addr ? mtvec : _rdata_T_17; // @[Mux.scala 80:57]
  wire  _rdata_T_20 = 12'h306 == addr; // @[Mux.scala 80:60]
  wire [63:0] _rdata_T_21 = 12'h306 == addr ? mcounteren : _rdata_T_19; // @[Mux.scala 80:57]
  wire  _rdata_T_22 = 12'h340 == addr; // @[Mux.scala 80:60]
  wire [63:0] _rdata_T_23 = 12'h340 == addr ? mscratch : _rdata_T_21; // @[Mux.scala 80:57]
  wire  _rdata_T_24 = 12'h341 == addr; // @[Mux.scala 80:60]
  wire [63:0] _rdata_T_25 = 12'h341 == addr ? mepc : _rdata_T_23; // @[Mux.scala 80:57]
  wire  _rdata_T_26 = 12'h342 == addr; // @[Mux.scala 80:60]
  wire [63:0] _rdata_T_27 = 12'h342 == addr ? mcause : _rdata_T_25; // @[Mux.scala 80:57]
  wire  _rdata_T_28 = 12'h343 == addr; // @[Mux.scala 80:60]
  wire [63:0] _rdata_T_29 = 12'h343 == addr ? mtval : _rdata_T_27; // @[Mux.scala 80:57]
  wire  _rdata_T_30 = 12'h344 == addr; // @[Mux.scala 80:60]
  wire [63:0] _rdata_T_31 = 12'h344 == addr ? mip : _rdata_T_29; // @[Mux.scala 80:57]
  wire  _rdata_T_32 = 12'hb00 == addr; // @[Mux.scala 80:60]
  wire [63:0] _rdata_T_33 = 12'hb00 == addr ? mhpmcounter_0 : _rdata_T_31; // @[Mux.scala 80:57]
  wire  _rdata_T_34 = 12'hb02 == addr; // @[Mux.scala 80:60]
  wire [63:0] rdata = 12'hb02 == addr ? mhpmcounter_2 : _rdata_T_33; // @[Mux.scala 80:57]
  wire [63:0] _wdata_T = rdata | io_in_data_src2; // @[CSR.scala 61:30]
  wire [63:0] _wdata_T_2 = ~io_in_data_src2; // @[CSR.scala 63:33]
  wire [63:0] _wdata_T_3 = rdata & _wdata_T_2; // @[CSR.scala 63:30]
  wire [63:0] _wdata_T_7 = 7'h1 == io_in_ctrl_funcOpType ? io_in_data_src2 : 64'h0; // @[Mux.scala 80:57]
  wire [63:0] _wdata_T_9 = 7'h5 == io_in_ctrl_funcOpType ? io_in_data_src2 : _wdata_T_7; // @[Mux.scala 80:57]
  wire [63:0] _wdata_T_11 = 7'h2 == io_in_ctrl_funcOpType ? _wdata_T : _wdata_T_9; // @[Mux.scala 80:57]
  wire [63:0] _wdata_T_13 = 7'h6 == io_in_ctrl_funcOpType ? _wdata_T : _wdata_T_11; // @[Mux.scala 80:57]
  wire [63:0] _wdata_T_15 = 7'h3 == io_in_ctrl_funcOpType ? _wdata_T_3 : _wdata_T_13; // @[Mux.scala 80:57]
  wire [63:0] wdata = 7'h7 == io_in_ctrl_funcOpType ? _wdata_T_3 : _wdata_T_15; // @[Mux.scala 80:57]
  wire [63:0] _mhpmcounter_0_T_1 = mhpmcounter_0 + 64'h1; // @[CSR.scala 66:20]
  wire [63:0] _mhpmcounter_2_T_1 = mhpmcounter_2 + 64'h1; // @[CSR.scala 70:26]
  wire  is_mret = 7'h18 == io_in_ctrl_funcOpType; // @[CSR.scala 72:40]
  wire  is_jmp = io_in_ctrl_funcOpType[4]; // @[CSR.scala 25:36]
  wire  is_ret = io_in_ctrl_funcOpType[3] & is_jmp; // @[CSR.scala 76:44]
  wire  mstatus_new_IE_M = wdata[3]; // @[CSR.scala 85:50]
  wire  mstatus_new_PIE_M = wdata[7]; // @[CSR.scala 85:50]
  wire [63:0] _GEN_1 = _rdata_T_34 ? wdata : _mhpmcounter_2_T_1; // @[Conditional.scala 39:67 CSR.scala 106:41]
  wire [63:0] _GEN_2 = _rdata_T_32 ? wdata : _mhpmcounter_0_T_1; // @[Conditional.scala 39:67 CSR.scala 105:41 CSR.scala 66:10]
  wire [63:0] _GEN_3 = _rdata_T_32 ? _mhpmcounter_2_T_1 : _GEN_1; // @[Conditional.scala 39:67]
  wire [63:0] _GEN_4 = _rdata_T_30 ? wdata : mip; // @[Conditional.scala 39:67 CSR.scala 103:41 CSR.scala 277:37]
  wire [63:0] _GEN_5 = _rdata_T_30 ? _mhpmcounter_0_T_1 : _GEN_2; // @[Conditional.scala 39:67 CSR.scala 66:10]
  wire [63:0] _GEN_6 = _rdata_T_30 ? _mhpmcounter_2_T_1 : _GEN_3; // @[Conditional.scala 39:67]
  wire [63:0] _GEN_7 = _rdata_T_28 ? wdata : mtval; // @[Conditional.scala 39:67 CSR.scala 102:41 CSR.scala 276:37]
  wire [63:0] _GEN_8 = _rdata_T_28 ? mip : _GEN_4; // @[Conditional.scala 39:67 CSR.scala 277:37]
  wire [63:0] _GEN_9 = _rdata_T_28 ? _mhpmcounter_0_T_1 : _GEN_5; // @[Conditional.scala 39:67 CSR.scala 66:10]
  wire [63:0] _GEN_10 = _rdata_T_28 ? _mhpmcounter_2_T_1 : _GEN_6; // @[Conditional.scala 39:67]
  wire [63:0] _GEN_11 = _rdata_T_26 ? wdata : mcause; // @[Conditional.scala 39:67 CSR.scala 101:41 CSR.scala 275:37]
  wire [63:0] _GEN_12 = _rdata_T_26 ? mtval : _GEN_7; // @[Conditional.scala 39:67 CSR.scala 276:37]
  wire [63:0] _GEN_13 = _rdata_T_26 ? mip : _GEN_8; // @[Conditional.scala 39:67 CSR.scala 277:37]
  wire [63:0] _GEN_14 = _rdata_T_26 ? _mhpmcounter_0_T_1 : _GEN_9; // @[Conditional.scala 39:67 CSR.scala 66:10]
  wire [63:0] _GEN_15 = _rdata_T_26 ? _mhpmcounter_2_T_1 : _GEN_10; // @[Conditional.scala 39:67]
  wire [63:0] _GEN_16 = _rdata_T_24 ? wdata : mepc; // @[Conditional.scala 39:67 CSR.scala 100:41 CSR.scala 274:37]
  wire [63:0] _GEN_17 = _rdata_T_24 ? mcause : _GEN_11; // @[Conditional.scala 39:67 CSR.scala 275:37]
  wire [63:0] _GEN_18 = _rdata_T_24 ? mtval : _GEN_12; // @[Conditional.scala 39:67 CSR.scala 276:37]
  wire [63:0] _GEN_19 = _rdata_T_24 ? mip : _GEN_13; // @[Conditional.scala 39:67 CSR.scala 277:37]
  wire [63:0] _GEN_20 = _rdata_T_24 ? _mhpmcounter_0_T_1 : _GEN_14; // @[Conditional.scala 39:67 CSR.scala 66:10]
  wire [63:0] _GEN_21 = _rdata_T_24 ? _mhpmcounter_2_T_1 : _GEN_15; // @[Conditional.scala 39:67]
  wire [63:0] _GEN_22 = _rdata_T_22 ? wdata : mscratch; // @[Conditional.scala 39:67 CSR.scala 99:41 CSR.scala 273:37]
  wire [63:0] _GEN_23 = _rdata_T_22 ? mepc : _GEN_16; // @[Conditional.scala 39:67 CSR.scala 274:37]
  wire [63:0] _GEN_24 = _rdata_T_22 ? mcause : _GEN_17; // @[Conditional.scala 39:67 CSR.scala 275:37]
  wire [63:0] _GEN_25 = _rdata_T_22 ? mtval : _GEN_18; // @[Conditional.scala 39:67 CSR.scala 276:37]
  wire [63:0] _GEN_26 = _rdata_T_22 ? mip : _GEN_19; // @[Conditional.scala 39:67 CSR.scala 277:37]
  wire [63:0] _GEN_27 = _rdata_T_22 ? _mhpmcounter_0_T_1 : _GEN_20; // @[Conditional.scala 39:67 CSR.scala 66:10]
  wire [63:0] _GEN_28 = _rdata_T_22 ? _mhpmcounter_2_T_1 : _GEN_21; // @[Conditional.scala 39:67]
  wire [63:0] _GEN_29 = _rdata_T_20 ? wdata : mcounteren; // @[Conditional.scala 39:67 CSR.scala 98:41 CSR.scala 269:37]
  wire [63:0] _GEN_30 = _rdata_T_20 ? mscratch : _GEN_22; // @[Conditional.scala 39:67 CSR.scala 273:37]
  wire [63:0] _GEN_31 = _rdata_T_20 ? mepc : _GEN_23; // @[Conditional.scala 39:67 CSR.scala 274:37]
  wire [63:0] _GEN_32 = _rdata_T_20 ? mcause : _GEN_24; // @[Conditional.scala 39:67 CSR.scala 275:37]
  wire [63:0] _GEN_33 = _rdata_T_20 ? mtval : _GEN_25; // @[Conditional.scala 39:67 CSR.scala 276:37]
  wire [63:0] _GEN_34 = _rdata_T_20 ? mip : _GEN_26; // @[Conditional.scala 39:67 CSR.scala 277:37]
  wire [63:0] _GEN_35 = _rdata_T_20 ? _mhpmcounter_0_T_1 : _GEN_27; // @[Conditional.scala 39:67 CSR.scala 66:10]
  wire [63:0] _GEN_36 = _rdata_T_20 ? _mhpmcounter_2_T_1 : _GEN_28; // @[Conditional.scala 39:67]
  wire [63:0] _GEN_37 = _rdata_T_18 ? wdata : mtvec; // @[Conditional.scala 39:67 CSR.scala 97:41 CSR.scala 268:37]
  wire [63:0] _GEN_38 = _rdata_T_18 ? mcounteren : _GEN_29; // @[Conditional.scala 39:67 CSR.scala 269:37]
  wire [63:0] _GEN_39 = _rdata_T_18 ? mscratch : _GEN_30; // @[Conditional.scala 39:67 CSR.scala 273:37]
  wire [63:0] _GEN_40 = _rdata_T_18 ? mepc : _GEN_31; // @[Conditional.scala 39:67 CSR.scala 274:37]
  wire [63:0] _GEN_41 = _rdata_T_18 ? mcause : _GEN_32; // @[Conditional.scala 39:67 CSR.scala 275:37]
  wire [63:0] _GEN_42 = _rdata_T_18 ? mtval : _GEN_33; // @[Conditional.scala 39:67 CSR.scala 276:37]
  wire [63:0] _GEN_43 = _rdata_T_18 ? mip : _GEN_34; // @[Conditional.scala 39:67 CSR.scala 277:37]
  wire [63:0] _GEN_44 = _rdata_T_18 ? _mhpmcounter_0_T_1 : _GEN_35; // @[Conditional.scala 39:67 CSR.scala 66:10]
  wire [63:0] _GEN_45 = _rdata_T_18 ? _mhpmcounter_2_T_1 : _GEN_36; // @[Conditional.scala 39:67]
  wire [63:0] _GEN_46 = _rdata_T_16 ? wdata : mie; // @[Conditional.scala 39:67 CSR.scala 96:41 CSR.scala 267:37]
  wire [63:0] _GEN_47 = _rdata_T_16 ? mtvec : _GEN_37; // @[Conditional.scala 39:67 CSR.scala 268:37]
  wire [63:0] _GEN_48 = _rdata_T_16 ? mcounteren : _GEN_38; // @[Conditional.scala 39:67 CSR.scala 269:37]
  wire [63:0] _GEN_49 = _rdata_T_16 ? mscratch : _GEN_39; // @[Conditional.scala 39:67 CSR.scala 273:37]
  wire [63:0] _GEN_50 = _rdata_T_16 ? mepc : _GEN_40; // @[Conditional.scala 39:67 CSR.scala 274:37]
  wire [63:0] _GEN_51 = _rdata_T_16 ? mcause : _GEN_41; // @[Conditional.scala 39:67 CSR.scala 275:37]
  wire [63:0] _GEN_52 = _rdata_T_16 ? mtval : _GEN_42; // @[Conditional.scala 39:67 CSR.scala 276:37]
  wire [63:0] _GEN_53 = _rdata_T_16 ? mip : _GEN_43; // @[Conditional.scala 39:67 CSR.scala 277:37]
  wire [63:0] _GEN_54 = _rdata_T_16 ? _mhpmcounter_0_T_1 : _GEN_44; // @[Conditional.scala 39:67 CSR.scala 66:10]
  wire [63:0] _GEN_55 = _rdata_T_16 ? _mhpmcounter_2_T_1 : _GEN_45; // @[Conditional.scala 39:67]
  wire [63:0] _GEN_56 = _rdata_T_14 ? wdata : mideleg; // @[Conditional.scala 39:67 CSR.scala 95:41 CSR.scala 266:37]
  wire [63:0] _GEN_57 = _rdata_T_14 ? mie : _GEN_46; // @[Conditional.scala 39:67 CSR.scala 267:37]
  wire [63:0] _GEN_58 = _rdata_T_14 ? mtvec : _GEN_47; // @[Conditional.scala 39:67 CSR.scala 268:37]
  wire [63:0] _GEN_59 = _rdata_T_14 ? mcounteren : _GEN_48; // @[Conditional.scala 39:67 CSR.scala 269:37]
  wire [63:0] _GEN_60 = _rdata_T_14 ? mscratch : _GEN_49; // @[Conditional.scala 39:67 CSR.scala 273:37]
  wire [63:0] _GEN_61 = _rdata_T_14 ? mepc : _GEN_50; // @[Conditional.scala 39:67 CSR.scala 274:37]
  wire [63:0] _GEN_62 = _rdata_T_14 ? mcause : _GEN_51; // @[Conditional.scala 39:67 CSR.scala 275:37]
  wire [63:0] _GEN_63 = _rdata_T_14 ? mtval : _GEN_52; // @[Conditional.scala 39:67 CSR.scala 276:37]
  wire [63:0] _GEN_64 = _rdata_T_14 ? mip : _GEN_53; // @[Conditional.scala 39:67 CSR.scala 277:37]
  wire [63:0] _GEN_65 = _rdata_T_14 ? _mhpmcounter_0_T_1 : _GEN_54; // @[Conditional.scala 39:67 CSR.scala 66:10]
  wire [63:0] _GEN_66 = _rdata_T_14 ? _mhpmcounter_2_T_1 : _GEN_55; // @[Conditional.scala 39:67]
  wire  _T_16 = io_ena & is_jmp; // @[CSR.scala 110:18]
  wire [63:0] _new_pc_T_2 = {mtvec_base,2'h0}; // @[Cat.scala 30:58]
  wire [63:0] _GEN_131 = {{2'd0}, mtvec_base}; // @[CSR.scala 121:46]
  wire [63:0] new_pc_hi_1 = _GEN_131 + mcause; // @[CSR.scala 121:46]
  wire [65:0] _new_pc_T_4 = {new_pc_hi_1,2'h0}; // @[Cat.scala 30:58]
  wire [63:0] _new_pc_T_6 = 2'h0 == mtvec_mode ? _new_pc_T_2 : 64'h0; // @[Mux.scala 80:57]
  wire [65:0] _new_pc_T_8 = 2'h1 == mtvec_mode ? _new_pc_T_4 : {{2'd0}, _new_pc_T_6}; // @[Mux.scala 80:57]
  wire [65:0] _new_pc_T_9 = is_ret ? {{2'd0}, mepc} : _new_pc_T_8; // @[CSR.scala 113:18]
  wire  _GEN_96 = is_mret | status_PIE_M; // @[CSR.scala 133:25 CSR.scala 135:20 CSR.scala 263:39]
  wire  _GEN_97 = is_mret ? status_PIE_M : status_IE_M; // @[CSR.scala 133:25 CSR.scala 136:19 CSR.scala 263:39]
  wire [65:0] _GEN_106 = io_ena & is_jmp ? _new_pc_T_9 : 66'h0; // @[CSR.scala 110:28 CSR.scala 113:12]
  wire [65:0] _GEN_113 = io_ena & ~is_jmp ? 66'h0 : _GEN_106; // @[CSR.scala 80:24 CSR.scala 81:12]
  wire [63:0] new_pc = _GEN_113[63:0];
  assign io_out_rdata = 12'hb02 == addr ? mhpmcounter_2 : _rdata_T_33; // @[Mux.scala 80:57]
  assign io_out_jmp_new_pc = new_pc; // @[CSR.scala 141:21]
  assign io_out_jmp_valid = io_ena & ~is_jmp ? 1'h0 : _T_16; // @[CSR.scala 80:24 CSR.scala 82:16]
  always @(posedge clock) begin
    if (reset) begin // @[CSR.scala 263:39]
      status_PIE_M <= 1'h0; // @[CSR.scala 263:39]
    end else if (io_ena & ~is_jmp) begin // @[CSR.scala 80:24]
      if (_rdata_T_8) begin // @[Conditional.scala 40:58]
        status_PIE_M <= mstatus_new_PIE_M; // @[CSR.scala 92:22]
      end
    end else if (io_ena & is_jmp) begin // @[CSR.scala 110:28]
      if (io_in_ctrl_funcOpType == 7'h10) begin // @[CSR.scala 125:35]
        status_PIE_M <= status_IE_M; // @[CSR.scala 131:20]
      end else begin
        status_PIE_M <= _GEN_96;
      end
    end
    if (reset) begin // @[CSR.scala 263:39]
      status_IE_M <= 1'h0; // @[CSR.scala 263:39]
    end else if (io_ena & ~is_jmp) begin // @[CSR.scala 80:24]
      if (_rdata_T_8) begin // @[Conditional.scala 40:58]
        status_IE_M <= mstatus_new_IE_M; // @[CSR.scala 91:21]
      end
    end else if (io_ena & is_jmp) begin // @[CSR.scala 110:28]
      if (io_in_ctrl_funcOpType == 7'h10) begin // @[CSR.scala 125:35]
        status_IE_M <= 1'h0; // @[CSR.scala 130:19]
      end else begin
        status_IE_M <= _GEN_97;
      end
    end
    if (reset) begin // @[CSR.scala 265:37]
      medeleg <= 64'h0; // @[CSR.scala 265:37]
    end else if (io_ena & ~is_jmp) begin // @[CSR.scala 80:24]
      if (!(_rdata_T_8)) begin // @[Conditional.scala 40:58]
        if (_rdata_T_12) begin // @[Conditional.scala 39:67]
          medeleg <= wdata; // @[CSR.scala 94:41]
        end
      end
    end
    if (reset) begin // @[CSR.scala 266:37]
      mideleg <= 64'h0; // @[CSR.scala 266:37]
    end else if (io_ena & ~is_jmp) begin // @[CSR.scala 80:24]
      if (!(_rdata_T_8)) begin // @[Conditional.scala 40:58]
        if (!(_rdata_T_12)) begin // @[Conditional.scala 39:67]
          mideleg <= _GEN_56;
        end
      end
    end
    if (reset) begin // @[CSR.scala 267:37]
      mie <= 64'h0; // @[CSR.scala 267:37]
    end else if (io_ena & ~is_jmp) begin // @[CSR.scala 80:24]
      if (!(_rdata_T_8)) begin // @[Conditional.scala 40:58]
        if (!(_rdata_T_12)) begin // @[Conditional.scala 39:67]
          mie <= _GEN_57;
        end
      end
    end
    if (reset) begin // @[CSR.scala 268:37]
      mtvec <= 64'h0; // @[CSR.scala 268:37]
    end else if (io_ena & ~is_jmp) begin // @[CSR.scala 80:24]
      if (!(_rdata_T_8)) begin // @[Conditional.scala 40:58]
        if (!(_rdata_T_12)) begin // @[Conditional.scala 39:67]
          mtvec <= _GEN_58;
        end
      end
    end
    if (reset) begin // @[CSR.scala 269:37]
      mcounteren <= 64'h0; // @[CSR.scala 269:37]
    end else if (io_ena & ~is_jmp) begin // @[CSR.scala 80:24]
      if (!(_rdata_T_8)) begin // @[Conditional.scala 40:58]
        if (!(_rdata_T_12)) begin // @[Conditional.scala 39:67]
          mcounteren <= _GEN_59;
        end
      end
    end
    if (reset) begin // @[CSR.scala 273:37]
      mscratch <= 64'h0; // @[CSR.scala 273:37]
    end else if (io_ena & ~is_jmp) begin // @[CSR.scala 80:24]
      if (!(_rdata_T_8)) begin // @[Conditional.scala 40:58]
        if (!(_rdata_T_12)) begin // @[Conditional.scala 39:67]
          mscratch <= _GEN_60;
        end
      end
    end
    if (reset) begin // @[CSR.scala 274:37]
      mepc <= 64'h0; // @[CSR.scala 274:37]
    end else if (io_ena & ~is_jmp) begin // @[CSR.scala 80:24]
      if (!(_rdata_T_8)) begin // @[Conditional.scala 40:58]
        if (!(_rdata_T_12)) begin // @[Conditional.scala 39:67]
          mepc <= _GEN_61;
        end
      end
    end else if (io_ena & is_jmp) begin // @[CSR.scala 110:28]
      if (io_in_ctrl_funcOpType == 7'h10) begin // @[CSR.scala 125:35]
        mepc <= io_in_cf_pc;
      end
    end
    if (reset) begin // @[CSR.scala 275:37]
      mcause <= 64'h0; // @[CSR.scala 275:37]
    end else if (io_ena & ~is_jmp) begin // @[CSR.scala 80:24]
      if (!(_rdata_T_8)) begin // @[Conditional.scala 40:58]
        if (!(_rdata_T_12)) begin // @[Conditional.scala 39:67]
          mcause <= _GEN_62;
        end
      end
    end else if (io_ena & is_jmp) begin // @[CSR.scala 110:28]
      if (io_in_ctrl_funcOpType == 7'h10) begin // @[CSR.scala 125:35]
        mcause <= 64'hb;
      end
    end
    if (reset) begin // @[CSR.scala 276:37]
      mtval <= 64'h0; // @[CSR.scala 276:37]
    end else if (io_ena & ~is_jmp) begin // @[CSR.scala 80:24]
      if (!(_rdata_T_8)) begin // @[Conditional.scala 40:58]
        if (!(_rdata_T_12)) begin // @[Conditional.scala 39:67]
          mtval <= _GEN_63;
        end
      end
    end
    if (reset) begin // @[CSR.scala 277:37]
      mip <= 64'h0; // @[CSR.scala 277:37]
    end else if (io_ena & ~is_jmp) begin // @[CSR.scala 80:24]
      if (!(_rdata_T_8)) begin // @[Conditional.scala 40:58]
        if (!(_rdata_T_12)) begin // @[Conditional.scala 39:67]
          mip <= _GEN_64;
        end
      end
    end
    if (reset) begin // @[CSR.scala 286:42]
      mhpmcounter_0 <= 64'h0; // @[CSR.scala 286:42]
    end else if (io_ena & ~is_jmp) begin // @[CSR.scala 80:24]
      if (_rdata_T_8) begin // @[Conditional.scala 40:58]
        mhpmcounter_0 <= _mhpmcounter_0_T_1; // @[CSR.scala 66:10]
      end else if (_rdata_T_12) begin // @[Conditional.scala 39:67]
        mhpmcounter_0 <= _mhpmcounter_0_T_1; // @[CSR.scala 66:10]
      end else begin
        mhpmcounter_0 <= _GEN_65;
      end
    end else begin
      mhpmcounter_0 <= _mhpmcounter_0_T_1; // @[CSR.scala 66:10]
    end
    if (reset) begin // @[CSR.scala 286:42]
      mhpmcounter_2 <= 64'h0; // @[CSR.scala 286:42]
    end else if (io_ena & ~is_jmp) begin // @[CSR.scala 80:24]
      if (_rdata_T_8) begin // @[Conditional.scala 40:58]
        mhpmcounter_2 <= _mhpmcounter_2_T_1;
      end else if (_rdata_T_12) begin // @[Conditional.scala 39:67]
        mhpmcounter_2 <= _mhpmcounter_2_T_1;
      end else begin
        mhpmcounter_2 <= _GEN_66;
      end
    end else begin
      mhpmcounter_2 <= _mhpmcounter_2_T_1;
    end
  end
// Register and memory initialization
`ifdef RANDOMIZE_GARBAGE_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_INVALID_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_REG_INIT
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_MEM_INIT
`define RANDOMIZE
`endif
`ifndef RANDOM
`define RANDOM $random
`endif
`ifdef RANDOMIZE_MEM_INIT
  integer initvar;
`endif
`ifndef SYNTHESIS
`ifdef FIRRTL_BEFORE_INITIAL
`FIRRTL_BEFORE_INITIAL
`endif
initial begin
  `ifdef RANDOMIZE
    `ifdef INIT_RANDOM
      `INIT_RANDOM
    `endif
    `ifndef VERILATOR
      `ifdef RANDOMIZE_DELAY
        #`RANDOMIZE_DELAY begin end
      `else
        #0.002 begin end
      `endif
    `endif
`ifdef RANDOMIZE_REG_INIT
  _RAND_0 = {1{`RANDOM}};
  status_PIE_M = _RAND_0[0:0];
  _RAND_1 = {1{`RANDOM}};
  status_IE_M = _RAND_1[0:0];
  _RAND_2 = {2{`RANDOM}};
  medeleg = _RAND_2[63:0];
  _RAND_3 = {2{`RANDOM}};
  mideleg = _RAND_3[63:0];
  _RAND_4 = {2{`RANDOM}};
  mie = _RAND_4[63:0];
  _RAND_5 = {2{`RANDOM}};
  mtvec = _RAND_5[63:0];
  _RAND_6 = {2{`RANDOM}};
  mcounteren = _RAND_6[63:0];
  _RAND_7 = {2{`RANDOM}};
  mscratch = _RAND_7[63:0];
  _RAND_8 = {2{`RANDOM}};
  mepc = _RAND_8[63:0];
  _RAND_9 = {2{`RANDOM}};
  mcause = _RAND_9[63:0];
  _RAND_10 = {2{`RANDOM}};
  mtval = _RAND_10[63:0];
  _RAND_11 = {2{`RANDOM}};
  mip = _RAND_11[63:0];
  _RAND_12 = {2{`RANDOM}};
  mhpmcounter_0 = _RAND_12[63:0];
  _RAND_13 = {2{`RANDOM}};
  mhpmcounter_2 = _RAND_13[63:0];
`endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`ifdef FIRRTL_AFTER_INITIAL
`FIRRTL_AFTER_INITIAL
`endif
`endif // SYNTHESIS
endmodule
module EXU(
  input         clock,
  input         reset,
  input  [63:0] io_in_cf_pc,
  input  [2:0]  io_in_ctrl_funcType,
  input  [6:0]  io_in_ctrl_funcOpType,
  input  [4:0]  io_in_ctrl_rfrd,
  input  [63:0] io_in_data_src1,
  input  [63:0] io_in_data_src2,
  input  [63:0] io_in_data_imm,
  output [4:0]  io_reg_write_back_addr,
  output [63:0] io_reg_write_back_data,
  output        io_reg_write_back_ena,
  output [63:0] io_branch_new_pc,
  output        io_branch_valid,
  input         io_lsuaxi_aw_ready,
  output        io_lsuaxi_aw_valid,
  output [31:0] io_lsuaxi_aw_bits_addr,
  input         io_lsuaxi_w_ready,
  output        io_lsuaxi_w_valid,
  output [63:0] io_lsuaxi_w_bits_data,
  output        io_lsuaxi_b_ready,
  input         io_lsuaxi_b_valid,
  input         io_lsuaxi_ar_ready,
  output [31:0] io_lsuaxi_ar_bits_addr,
  output        io_lsuaxi_r_ready,
  input         io_lsuaxi_r_valid,
  input  [63:0] io_lsuaxi_r_bits_data
);
  wire [6:0] alu_io_in_ctrl_funcOpType; // @[EXU.scala 22:27]
  wire [63:0] alu_io_in_data_src1; // @[EXU.scala 22:27]
  wire [63:0] alu_io_in_data_src2; // @[EXU.scala 22:27]
  wire [63:0] alu_io_out_aluRes; // @[EXU.scala 22:27]
  wire  lsu_clock; // @[EXU.scala 23:27]
  wire  lsu_reset; // @[EXU.scala 23:27]
  wire [6:0] lsu_io_in_ctrl_funcOpType; // @[EXU.scala 23:27]
  wire [63:0] lsu_io_in_data_src1; // @[EXU.scala 23:27]
  wire [63:0] lsu_io_in_data_src2; // @[EXU.scala 23:27]
  wire [63:0] lsu_io_in_data_imm; // @[EXU.scala 23:27]
  wire [63:0] lsu_io_out_rdata; // @[EXU.scala 23:27]
  wire  lsu_io_lsuaxi_aw_ready; // @[EXU.scala 23:27]
  wire  lsu_io_lsuaxi_aw_valid; // @[EXU.scala 23:27]
  wire [31:0] lsu_io_lsuaxi_aw_bits_addr; // @[EXU.scala 23:27]
  wire  lsu_io_lsuaxi_w_ready; // @[EXU.scala 23:27]
  wire  lsu_io_lsuaxi_w_valid; // @[EXU.scala 23:27]
  wire [63:0] lsu_io_lsuaxi_w_bits_data; // @[EXU.scala 23:27]
  wire  lsu_io_lsuaxi_b_ready; // @[EXU.scala 23:27]
  wire  lsu_io_lsuaxi_b_valid; // @[EXU.scala 23:27]
  wire  lsu_io_lsuaxi_ar_ready; // @[EXU.scala 23:27]
  wire  lsu_io_lsuaxi_ar_valid; // @[EXU.scala 23:27]
  wire [31:0] lsu_io_lsuaxi_ar_bits_addr; // @[EXU.scala 23:27]
  wire  lsu_io_lsuaxi_r_ready; // @[EXU.scala 23:27]
  wire  lsu_io_lsuaxi_r_valid; // @[EXU.scala 23:27]
  wire [63:0] lsu_io_lsuaxi_r_bits_data; // @[EXU.scala 23:27]
  wire [63:0] bru_io_in_cf_pc; // @[EXU.scala 24:27]
  wire [2:0] bru_io_in_ctrl_funcType; // @[EXU.scala 24:27]
  wire [6:0] bru_io_in_ctrl_funcOpType; // @[EXU.scala 24:27]
  wire [63:0] bru_io_in_data_src1; // @[EXU.scala 24:27]
  wire [63:0] bru_io_in_data_src2; // @[EXU.scala 24:27]
  wire [63:0] bru_io_in_data_imm; // @[EXU.scala 24:27]
  wire [63:0] bru_io_out_new_pc; // @[EXU.scala 24:27]
  wire  bru_io_out_valid; // @[EXU.scala 24:27]
  wire  csr_clock; // @[EXU.scala 25:27]
  wire  csr_reset; // @[EXU.scala 25:27]
  wire  csr_io_ena; // @[EXU.scala 25:27]
  wire [63:0] csr_io_in_cf_pc; // @[EXU.scala 25:27]
  wire [6:0] csr_io_in_ctrl_funcOpType; // @[EXU.scala 25:27]
  wire [63:0] csr_io_in_data_src2; // @[EXU.scala 25:27]
  wire [63:0] csr_io_in_data_imm; // @[EXU.scala 25:27]
  wire [63:0] csr_io_out_rdata; // @[EXU.scala 25:27]
  wire [63:0] csr_io_out_jmp_new_pc; // @[EXU.scala 25:27]
  wire  csr_io_out_jmp_valid; // @[EXU.scala 25:27]
  wire  _wb_ena_T_1 = ~io_in_ctrl_funcOpType[3]; // @[LSU.scala 25:34]
  wire  _wb_ena_T_6 = 3'h1 == io_in_ctrl_funcType ? _wb_ena_T_1 : 3'h0 == io_in_ctrl_funcType; // @[Mux.scala 80:57]
  wire  _wb_ena_T_8 = 3'h5 == io_in_ctrl_funcType ? io_in_ctrl_funcOpType[6] : _wb_ena_T_6; // @[Mux.scala 80:57]
  wire [63:0] _wdata_T_1 = io_in_cf_pc + 64'h4; // @[EXU.scala 51:38]
  wire [63:0] _wdata_T_3 = 3'h0 == io_in_ctrl_funcType ? alu_io_out_aluRes : 64'h0; // @[Mux.scala 80:57]
  wire [63:0] _wdata_T_5 = 3'h1 == io_in_ctrl_funcType ? lsu_io_out_rdata : _wdata_T_3; // @[Mux.scala 80:57]
  wire [63:0] _wdata_T_7 = 3'h5 == io_in_ctrl_funcType ? _wdata_T_1 : _wdata_T_5; // @[Mux.scala 80:57]
  wire [63:0] _io_branch_T_1_new_pc = 3'h5 == io_in_ctrl_funcType ? bru_io_out_new_pc : 64'h0; // @[Mux.scala 80:57]
  ALU alu ( // @[EXU.scala 22:27]
    .io_in_ctrl_funcOpType(alu_io_in_ctrl_funcOpType),
    .io_in_data_src1(alu_io_in_data_src1),
    .io_in_data_src2(alu_io_in_data_src2),
    .io_out_aluRes(alu_io_out_aluRes)
  );
  LSU lsu ( // @[EXU.scala 23:27]
    .clock(lsu_clock),
    .reset(lsu_reset),
    .io_in_ctrl_funcOpType(lsu_io_in_ctrl_funcOpType),
    .io_in_data_src1(lsu_io_in_data_src1),
    .io_in_data_src2(lsu_io_in_data_src2),
    .io_in_data_imm(lsu_io_in_data_imm),
    .io_out_rdata(lsu_io_out_rdata),
    .io_lsuaxi_aw_ready(lsu_io_lsuaxi_aw_ready),
    .io_lsuaxi_aw_valid(lsu_io_lsuaxi_aw_valid),
    .io_lsuaxi_aw_bits_addr(lsu_io_lsuaxi_aw_bits_addr),
    .io_lsuaxi_w_ready(lsu_io_lsuaxi_w_ready),
    .io_lsuaxi_w_valid(lsu_io_lsuaxi_w_valid),
    .io_lsuaxi_w_bits_data(lsu_io_lsuaxi_w_bits_data),
    .io_lsuaxi_b_ready(lsu_io_lsuaxi_b_ready),
    .io_lsuaxi_b_valid(lsu_io_lsuaxi_b_valid),
    .io_lsuaxi_ar_ready(lsu_io_lsuaxi_ar_ready),
    .io_lsuaxi_ar_valid(lsu_io_lsuaxi_ar_valid),
    .io_lsuaxi_ar_bits_addr(lsu_io_lsuaxi_ar_bits_addr),
    .io_lsuaxi_r_ready(lsu_io_lsuaxi_r_ready),
    .io_lsuaxi_r_valid(lsu_io_lsuaxi_r_valid),
    .io_lsuaxi_r_bits_data(lsu_io_lsuaxi_r_bits_data)
  );
  BRU bru ( // @[EXU.scala 24:27]
    .io_in_cf_pc(bru_io_in_cf_pc),
    .io_in_ctrl_funcType(bru_io_in_ctrl_funcType),
    .io_in_ctrl_funcOpType(bru_io_in_ctrl_funcOpType),
    .io_in_data_src1(bru_io_in_data_src1),
    .io_in_data_src2(bru_io_in_data_src2),
    .io_in_data_imm(bru_io_in_data_imm),
    .io_out_new_pc(bru_io_out_new_pc),
    .io_out_valid(bru_io_out_valid)
  );
  CSR csr ( // @[EXU.scala 25:27]
    .clock(csr_clock),
    .reset(csr_reset),
    .io_ena(csr_io_ena),
    .io_in_cf_pc(csr_io_in_cf_pc),
    .io_in_ctrl_funcOpType(csr_io_in_ctrl_funcOpType),
    .io_in_data_src2(csr_io_in_data_src2),
    .io_in_data_imm(csr_io_in_data_imm),
    .io_out_rdata(csr_io_out_rdata),
    .io_out_jmp_new_pc(csr_io_out_jmp_new_pc),
    .io_out_jmp_valid(csr_io_out_jmp_valid)
  );
  assign io_reg_write_back_addr = io_in_ctrl_rfrd; // @[EXU.scala 55:29]
  assign io_reg_write_back_data = 3'h3 == io_in_ctrl_funcType ? csr_io_out_rdata : _wdata_T_7; // @[Mux.scala 80:57]
  assign io_reg_write_back_ena = 3'h3 == io_in_ctrl_funcType | _wb_ena_T_8; // @[Mux.scala 80:57]
  assign io_branch_new_pc = 3'h3 == io_in_ctrl_funcType ? csr_io_out_jmp_new_pc : _io_branch_T_1_new_pc; // @[Mux.scala 80:57]
  assign io_branch_valid = 3'h3 == io_in_ctrl_funcType ? csr_io_out_jmp_valid : 3'h5 == io_in_ctrl_funcType &
    bru_io_out_valid; // @[Mux.scala 80:57]
  assign io_lsuaxi_aw_valid = lsu_io_lsuaxi_aw_valid; // @[EXU.scala 64:15]
  assign io_lsuaxi_aw_bits_addr = lsu_io_lsuaxi_aw_bits_addr; // @[EXU.scala 64:15]
  assign io_lsuaxi_w_valid = lsu_io_lsuaxi_w_valid; // @[EXU.scala 64:15]
  assign io_lsuaxi_w_bits_data = lsu_io_lsuaxi_w_bits_data; // @[EXU.scala 64:15]
  assign io_lsuaxi_b_ready = lsu_io_lsuaxi_b_ready; // @[EXU.scala 64:15]
  assign io_lsuaxi_ar_bits_addr = lsu_io_lsuaxi_ar_bits_addr; // @[EXU.scala 64:15]
  assign io_lsuaxi_r_ready = lsu_io_lsuaxi_r_ready; // @[EXU.scala 64:15]
  assign alu_io_in_ctrl_funcOpType = io_in_ctrl_funcOpType; // @[EXU.scala 34:15]
  assign alu_io_in_data_src1 = io_in_data_src1; // @[EXU.scala 34:15]
  assign alu_io_in_data_src2 = io_in_data_src2; // @[EXU.scala 34:15]
  assign lsu_clock = clock;
  assign lsu_reset = reset;
  assign lsu_io_in_ctrl_funcOpType = io_in_ctrl_funcOpType; // @[EXU.scala 35:15]
  assign lsu_io_in_data_src1 = io_in_data_src1; // @[EXU.scala 35:15]
  assign lsu_io_in_data_src2 = io_in_data_src2; // @[EXU.scala 35:15]
  assign lsu_io_in_data_imm = io_in_data_imm; // @[EXU.scala 35:15]
  assign lsu_io_lsuaxi_aw_ready = io_lsuaxi_aw_ready; // @[EXU.scala 64:15]
  assign lsu_io_lsuaxi_w_ready = io_lsuaxi_w_ready; // @[EXU.scala 64:15]
  assign lsu_io_lsuaxi_b_valid = io_lsuaxi_b_valid; // @[EXU.scala 64:15]
  assign lsu_io_lsuaxi_ar_ready = io_lsuaxi_ar_ready; // @[EXU.scala 64:15]
  assign lsu_io_lsuaxi_r_valid = io_lsuaxi_r_valid; // @[EXU.scala 64:15]
  assign lsu_io_lsuaxi_r_bits_data = io_lsuaxi_r_bits_data; // @[EXU.scala 64:15]
  assign bru_io_in_cf_pc = io_in_cf_pc; // @[EXU.scala 36:15]
  assign bru_io_in_ctrl_funcType = io_in_ctrl_funcType; // @[EXU.scala 36:15]
  assign bru_io_in_ctrl_funcOpType = io_in_ctrl_funcOpType; // @[EXU.scala 36:15]
  assign bru_io_in_data_src1 = io_in_data_src1; // @[EXU.scala 36:15]
  assign bru_io_in_data_src2 = io_in_data_src2; // @[EXU.scala 36:15]
  assign bru_io_in_data_imm = io_in_data_imm; // @[EXU.scala 36:15]
  assign csr_clock = clock;
  assign csr_reset = reset;
  assign csr_io_ena = io_in_ctrl_funcType == 3'h3; // @[EXU.scala 29:32]
  assign csr_io_in_cf_pc = io_in_cf_pc; // @[EXU.scala 37:15]
  assign csr_io_in_ctrl_funcOpType = io_in_ctrl_funcOpType; // @[EXU.scala 37:15]
  assign csr_io_in_data_src2 = io_in_data_src2; // @[EXU.scala 37:15]
  assign csr_io_in_data_imm = io_in_data_imm; // @[EXU.scala 37:15]
endmodule
module WBU(
  input  [4:0]  io_in_addr,
  input  [63:0] io_in_data,
  input         io_in_ena,
  output [4:0]  io_out_addr,
  output [63:0] io_out_data,
  output        io_out_ena
);
  assign io_out_addr = io_in_addr; // @[WBU.scala 17:12]
  assign io_out_data = io_in_data; // @[WBU.scala 17:12]
  assign io_out_ena = io_in_ena; // @[WBU.scala 17:12]
endmodule
module Regfile(
  input         clock,
  input         reset,
  input  [4:0]  io_src1_addr,
  output [63:0] io_src1_data,
  input  [4:0]  io_src2_addr,
  output [63:0] io_src2_data,
  input  [4:0]  io_rd_addr,
  input  [63:0] io_rd_data,
  input         io_rd_ena
);
`ifdef RANDOMIZE_REG_INIT
  reg [63:0] _RAND_0;
  reg [63:0] _RAND_1;
  reg [63:0] _RAND_2;
  reg [63:0] _RAND_3;
  reg [63:0] _RAND_4;
  reg [63:0] _RAND_5;
  reg [63:0] _RAND_6;
  reg [63:0] _RAND_7;
  reg [63:0] _RAND_8;
  reg [63:0] _RAND_9;
  reg [63:0] _RAND_10;
  reg [63:0] _RAND_11;
  reg [63:0] _RAND_12;
  reg [63:0] _RAND_13;
  reg [63:0] _RAND_14;
  reg [63:0] _RAND_15;
  reg [63:0] _RAND_16;
  reg [63:0] _RAND_17;
  reg [63:0] _RAND_18;
  reg [63:0] _RAND_19;
  reg [63:0] _RAND_20;
  reg [63:0] _RAND_21;
  reg [63:0] _RAND_22;
  reg [63:0] _RAND_23;
  reg [63:0] _RAND_24;
  reg [63:0] _RAND_25;
  reg [63:0] _RAND_26;
  reg [63:0] _RAND_27;
  reg [63:0] _RAND_28;
  reg [63:0] _RAND_29;
  reg [63:0] _RAND_30;
  reg [63:0] _RAND_31;
`endif // RANDOMIZE_REG_INIT
  reg [63:0] regs_0; // @[regfile.scala 7:21]
  reg [63:0] regs_1; // @[regfile.scala 7:21]
  reg [63:0] regs_2; // @[regfile.scala 7:21]
  reg [63:0] regs_3; // @[regfile.scala 7:21]
  reg [63:0] regs_4; // @[regfile.scala 7:21]
  reg [63:0] regs_5; // @[regfile.scala 7:21]
  reg [63:0] regs_6; // @[regfile.scala 7:21]
  reg [63:0] regs_7; // @[regfile.scala 7:21]
  reg [63:0] regs_8; // @[regfile.scala 7:21]
  reg [63:0] regs_9; // @[regfile.scala 7:21]
  reg [63:0] regs_10; // @[regfile.scala 7:21]
  reg [63:0] regs_11; // @[regfile.scala 7:21]
  reg [63:0] regs_12; // @[regfile.scala 7:21]
  reg [63:0] regs_13; // @[regfile.scala 7:21]
  reg [63:0] regs_14; // @[regfile.scala 7:21]
  reg [63:0] regs_15; // @[regfile.scala 7:21]
  reg [63:0] regs_16; // @[regfile.scala 7:21]
  reg [63:0] regs_17; // @[regfile.scala 7:21]
  reg [63:0] regs_18; // @[regfile.scala 7:21]
  reg [63:0] regs_19; // @[regfile.scala 7:21]
  reg [63:0] regs_20; // @[regfile.scala 7:21]
  reg [63:0] regs_21; // @[regfile.scala 7:21]
  reg [63:0] regs_22; // @[regfile.scala 7:21]
  reg [63:0] regs_23; // @[regfile.scala 7:21]
  reg [63:0] regs_24; // @[regfile.scala 7:21]
  reg [63:0] regs_25; // @[regfile.scala 7:21]
  reg [63:0] regs_26; // @[regfile.scala 7:21]
  reg [63:0] regs_27; // @[regfile.scala 7:21]
  reg [63:0] regs_28; // @[regfile.scala 7:21]
  reg [63:0] regs_29; // @[regfile.scala 7:21]
  reg [63:0] regs_30; // @[regfile.scala 7:21]
  reg [63:0] regs_31; // @[regfile.scala 7:21]
  wire [63:0] _GEN_97 = 5'h1 == io_src1_addr ? regs_1 : regs_0; // @[regfile.scala 50:16 regfile.scala 50:16]
  wire [63:0] _GEN_98 = 5'h2 == io_src1_addr ? regs_2 : _GEN_97; // @[regfile.scala 50:16 regfile.scala 50:16]
  wire [63:0] _GEN_99 = 5'h3 == io_src1_addr ? regs_3 : _GEN_98; // @[regfile.scala 50:16 regfile.scala 50:16]
  wire [63:0] _GEN_100 = 5'h4 == io_src1_addr ? regs_4 : _GEN_99; // @[regfile.scala 50:16 regfile.scala 50:16]
  wire [63:0] _GEN_101 = 5'h5 == io_src1_addr ? regs_5 : _GEN_100; // @[regfile.scala 50:16 regfile.scala 50:16]
  wire [63:0] _GEN_102 = 5'h6 == io_src1_addr ? regs_6 : _GEN_101; // @[regfile.scala 50:16 regfile.scala 50:16]
  wire [63:0] _GEN_103 = 5'h7 == io_src1_addr ? regs_7 : _GEN_102; // @[regfile.scala 50:16 regfile.scala 50:16]
  wire [63:0] _GEN_104 = 5'h8 == io_src1_addr ? regs_8 : _GEN_103; // @[regfile.scala 50:16 regfile.scala 50:16]
  wire [63:0] _GEN_105 = 5'h9 == io_src1_addr ? regs_9 : _GEN_104; // @[regfile.scala 50:16 regfile.scala 50:16]
  wire [63:0] _GEN_106 = 5'ha == io_src1_addr ? regs_10 : _GEN_105; // @[regfile.scala 50:16 regfile.scala 50:16]
  wire [63:0] _GEN_107 = 5'hb == io_src1_addr ? regs_11 : _GEN_106; // @[regfile.scala 50:16 regfile.scala 50:16]
  wire [63:0] _GEN_108 = 5'hc == io_src1_addr ? regs_12 : _GEN_107; // @[regfile.scala 50:16 regfile.scala 50:16]
  wire [63:0] _GEN_109 = 5'hd == io_src1_addr ? regs_13 : _GEN_108; // @[regfile.scala 50:16 regfile.scala 50:16]
  wire [63:0] _GEN_110 = 5'he == io_src1_addr ? regs_14 : _GEN_109; // @[regfile.scala 50:16 regfile.scala 50:16]
  wire [63:0] _GEN_111 = 5'hf == io_src1_addr ? regs_15 : _GEN_110; // @[regfile.scala 50:16 regfile.scala 50:16]
  wire [63:0] _GEN_112 = 5'h10 == io_src1_addr ? regs_16 : _GEN_111; // @[regfile.scala 50:16 regfile.scala 50:16]
  wire [63:0] _GEN_113 = 5'h11 == io_src1_addr ? regs_17 : _GEN_112; // @[regfile.scala 50:16 regfile.scala 50:16]
  wire [63:0] _GEN_114 = 5'h12 == io_src1_addr ? regs_18 : _GEN_113; // @[regfile.scala 50:16 regfile.scala 50:16]
  wire [63:0] _GEN_115 = 5'h13 == io_src1_addr ? regs_19 : _GEN_114; // @[regfile.scala 50:16 regfile.scala 50:16]
  wire [63:0] _GEN_116 = 5'h14 == io_src1_addr ? regs_20 : _GEN_115; // @[regfile.scala 50:16 regfile.scala 50:16]
  wire [63:0] _GEN_117 = 5'h15 == io_src1_addr ? regs_21 : _GEN_116; // @[regfile.scala 50:16 regfile.scala 50:16]
  wire [63:0] _GEN_118 = 5'h16 == io_src1_addr ? regs_22 : _GEN_117; // @[regfile.scala 50:16 regfile.scala 50:16]
  wire [63:0] _GEN_119 = 5'h17 == io_src1_addr ? regs_23 : _GEN_118; // @[regfile.scala 50:16 regfile.scala 50:16]
  wire [63:0] _GEN_120 = 5'h18 == io_src1_addr ? regs_24 : _GEN_119; // @[regfile.scala 50:16 regfile.scala 50:16]
  wire [63:0] _GEN_121 = 5'h19 == io_src1_addr ? regs_25 : _GEN_120; // @[regfile.scala 50:16 regfile.scala 50:16]
  wire [63:0] _GEN_122 = 5'h1a == io_src1_addr ? regs_26 : _GEN_121; // @[regfile.scala 50:16 regfile.scala 50:16]
  wire [63:0] _GEN_123 = 5'h1b == io_src1_addr ? regs_27 : _GEN_122; // @[regfile.scala 50:16 regfile.scala 50:16]
  wire [63:0] _GEN_124 = 5'h1c == io_src1_addr ? regs_28 : _GEN_123; // @[regfile.scala 50:16 regfile.scala 50:16]
  wire [63:0] _GEN_125 = 5'h1d == io_src1_addr ? regs_29 : _GEN_124; // @[regfile.scala 50:16 regfile.scala 50:16]
  wire [63:0] _GEN_126 = 5'h1e == io_src1_addr ? regs_30 : _GEN_125; // @[regfile.scala 50:16 regfile.scala 50:16]
  wire [63:0] _GEN_129 = 5'h1 == io_src2_addr ? regs_1 : regs_0; // @[regfile.scala 51:16 regfile.scala 51:16]
  wire [63:0] _GEN_130 = 5'h2 == io_src2_addr ? regs_2 : _GEN_129; // @[regfile.scala 51:16 regfile.scala 51:16]
  wire [63:0] _GEN_131 = 5'h3 == io_src2_addr ? regs_3 : _GEN_130; // @[regfile.scala 51:16 regfile.scala 51:16]
  wire [63:0] _GEN_132 = 5'h4 == io_src2_addr ? regs_4 : _GEN_131; // @[regfile.scala 51:16 regfile.scala 51:16]
  wire [63:0] _GEN_133 = 5'h5 == io_src2_addr ? regs_5 : _GEN_132; // @[regfile.scala 51:16 regfile.scala 51:16]
  wire [63:0] _GEN_134 = 5'h6 == io_src2_addr ? regs_6 : _GEN_133; // @[regfile.scala 51:16 regfile.scala 51:16]
  wire [63:0] _GEN_135 = 5'h7 == io_src2_addr ? regs_7 : _GEN_134; // @[regfile.scala 51:16 regfile.scala 51:16]
  wire [63:0] _GEN_136 = 5'h8 == io_src2_addr ? regs_8 : _GEN_135; // @[regfile.scala 51:16 regfile.scala 51:16]
  wire [63:0] _GEN_137 = 5'h9 == io_src2_addr ? regs_9 : _GEN_136; // @[regfile.scala 51:16 regfile.scala 51:16]
  wire [63:0] _GEN_138 = 5'ha == io_src2_addr ? regs_10 : _GEN_137; // @[regfile.scala 51:16 regfile.scala 51:16]
  wire [63:0] _GEN_139 = 5'hb == io_src2_addr ? regs_11 : _GEN_138; // @[regfile.scala 51:16 regfile.scala 51:16]
  wire [63:0] _GEN_140 = 5'hc == io_src2_addr ? regs_12 : _GEN_139; // @[regfile.scala 51:16 regfile.scala 51:16]
  wire [63:0] _GEN_141 = 5'hd == io_src2_addr ? regs_13 : _GEN_140; // @[regfile.scala 51:16 regfile.scala 51:16]
  wire [63:0] _GEN_142 = 5'he == io_src2_addr ? regs_14 : _GEN_141; // @[regfile.scala 51:16 regfile.scala 51:16]
  wire [63:0] _GEN_143 = 5'hf == io_src2_addr ? regs_15 : _GEN_142; // @[regfile.scala 51:16 regfile.scala 51:16]
  wire [63:0] _GEN_144 = 5'h10 == io_src2_addr ? regs_16 : _GEN_143; // @[regfile.scala 51:16 regfile.scala 51:16]
  wire [63:0] _GEN_145 = 5'h11 == io_src2_addr ? regs_17 : _GEN_144; // @[regfile.scala 51:16 regfile.scala 51:16]
  wire [63:0] _GEN_146 = 5'h12 == io_src2_addr ? regs_18 : _GEN_145; // @[regfile.scala 51:16 regfile.scala 51:16]
  wire [63:0] _GEN_147 = 5'h13 == io_src2_addr ? regs_19 : _GEN_146; // @[regfile.scala 51:16 regfile.scala 51:16]
  wire [63:0] _GEN_148 = 5'h14 == io_src2_addr ? regs_20 : _GEN_147; // @[regfile.scala 51:16 regfile.scala 51:16]
  wire [63:0] _GEN_149 = 5'h15 == io_src2_addr ? regs_21 : _GEN_148; // @[regfile.scala 51:16 regfile.scala 51:16]
  wire [63:0] _GEN_150 = 5'h16 == io_src2_addr ? regs_22 : _GEN_149; // @[regfile.scala 51:16 regfile.scala 51:16]
  wire [63:0] _GEN_151 = 5'h17 == io_src2_addr ? regs_23 : _GEN_150; // @[regfile.scala 51:16 regfile.scala 51:16]
  wire [63:0] _GEN_152 = 5'h18 == io_src2_addr ? regs_24 : _GEN_151; // @[regfile.scala 51:16 regfile.scala 51:16]
  wire [63:0] _GEN_153 = 5'h19 == io_src2_addr ? regs_25 : _GEN_152; // @[regfile.scala 51:16 regfile.scala 51:16]
  wire [63:0] _GEN_154 = 5'h1a == io_src2_addr ? regs_26 : _GEN_153; // @[regfile.scala 51:16 regfile.scala 51:16]
  wire [63:0] _GEN_155 = 5'h1b == io_src2_addr ? regs_27 : _GEN_154; // @[regfile.scala 51:16 regfile.scala 51:16]
  wire [63:0] _GEN_156 = 5'h1c == io_src2_addr ? regs_28 : _GEN_155; // @[regfile.scala 51:16 regfile.scala 51:16]
  wire [63:0] _GEN_157 = 5'h1d == io_src2_addr ? regs_29 : _GEN_156; // @[regfile.scala 51:16 regfile.scala 51:16]
  wire [63:0] _GEN_158 = 5'h1e == io_src2_addr ? regs_30 : _GEN_157; // @[regfile.scala 51:16 regfile.scala 51:16]
  assign io_src1_data = 5'h1f == io_src1_addr ? regs_31 : _GEN_126; // @[regfile.scala 50:16 regfile.scala 50:16]
  assign io_src2_data = 5'h1f == io_src2_addr ? regs_31 : _GEN_158; // @[regfile.scala 51:16 regfile.scala 51:16]
  always @(posedge clock) begin
    if (reset) begin // @[regfile.scala 7:21]
      regs_0 <= 64'h0; // @[regfile.scala 7:21]
    end else if (io_rd_ena) begin // @[regfile.scala 47:19]
      if (io_rd_addr != 5'h0) begin // @[regfile.scala 10:24]
        if (5'h0 == io_rd_addr) begin // @[regfile.scala 11:18]
          regs_0 <= io_rd_data; // @[regfile.scala 11:18]
        end
      end
    end
    if (reset) begin // @[regfile.scala 7:21]
      regs_1 <= 64'h0; // @[regfile.scala 7:21]
    end else if (io_rd_ena) begin // @[regfile.scala 47:19]
      if (io_rd_addr != 5'h0) begin // @[regfile.scala 10:24]
        if (5'h1 == io_rd_addr) begin // @[regfile.scala 11:18]
          regs_1 <= io_rd_data; // @[regfile.scala 11:18]
        end
      end
    end
    if (reset) begin // @[regfile.scala 7:21]
      regs_2 <= 64'h0; // @[regfile.scala 7:21]
    end else if (io_rd_ena) begin // @[regfile.scala 47:19]
      if (io_rd_addr != 5'h0) begin // @[regfile.scala 10:24]
        if (5'h2 == io_rd_addr) begin // @[regfile.scala 11:18]
          regs_2 <= io_rd_data; // @[regfile.scala 11:18]
        end
      end
    end
    if (reset) begin // @[regfile.scala 7:21]
      regs_3 <= 64'h0; // @[regfile.scala 7:21]
    end else if (io_rd_ena) begin // @[regfile.scala 47:19]
      if (io_rd_addr != 5'h0) begin // @[regfile.scala 10:24]
        if (5'h3 == io_rd_addr) begin // @[regfile.scala 11:18]
          regs_3 <= io_rd_data; // @[regfile.scala 11:18]
        end
      end
    end
    if (reset) begin // @[regfile.scala 7:21]
      regs_4 <= 64'h0; // @[regfile.scala 7:21]
    end else if (io_rd_ena) begin // @[regfile.scala 47:19]
      if (io_rd_addr != 5'h0) begin // @[regfile.scala 10:24]
        if (5'h4 == io_rd_addr) begin // @[regfile.scala 11:18]
          regs_4 <= io_rd_data; // @[regfile.scala 11:18]
        end
      end
    end
    if (reset) begin // @[regfile.scala 7:21]
      regs_5 <= 64'h0; // @[regfile.scala 7:21]
    end else if (io_rd_ena) begin // @[regfile.scala 47:19]
      if (io_rd_addr != 5'h0) begin // @[regfile.scala 10:24]
        if (5'h5 == io_rd_addr) begin // @[regfile.scala 11:18]
          regs_5 <= io_rd_data; // @[regfile.scala 11:18]
        end
      end
    end
    if (reset) begin // @[regfile.scala 7:21]
      regs_6 <= 64'h0; // @[regfile.scala 7:21]
    end else if (io_rd_ena) begin // @[regfile.scala 47:19]
      if (io_rd_addr != 5'h0) begin // @[regfile.scala 10:24]
        if (5'h6 == io_rd_addr) begin // @[regfile.scala 11:18]
          regs_6 <= io_rd_data; // @[regfile.scala 11:18]
        end
      end
    end
    if (reset) begin // @[regfile.scala 7:21]
      regs_7 <= 64'h0; // @[regfile.scala 7:21]
    end else if (io_rd_ena) begin // @[regfile.scala 47:19]
      if (io_rd_addr != 5'h0) begin // @[regfile.scala 10:24]
        if (5'h7 == io_rd_addr) begin // @[regfile.scala 11:18]
          regs_7 <= io_rd_data; // @[regfile.scala 11:18]
        end
      end
    end
    if (reset) begin // @[regfile.scala 7:21]
      regs_8 <= 64'h0; // @[regfile.scala 7:21]
    end else if (io_rd_ena) begin // @[regfile.scala 47:19]
      if (io_rd_addr != 5'h0) begin // @[regfile.scala 10:24]
        if (5'h8 == io_rd_addr) begin // @[regfile.scala 11:18]
          regs_8 <= io_rd_data; // @[regfile.scala 11:18]
        end
      end
    end
    if (reset) begin // @[regfile.scala 7:21]
      regs_9 <= 64'h0; // @[regfile.scala 7:21]
    end else if (io_rd_ena) begin // @[regfile.scala 47:19]
      if (io_rd_addr != 5'h0) begin // @[regfile.scala 10:24]
        if (5'h9 == io_rd_addr) begin // @[regfile.scala 11:18]
          regs_9 <= io_rd_data; // @[regfile.scala 11:18]
        end
      end
    end
    if (reset) begin // @[regfile.scala 7:21]
      regs_10 <= 64'h0; // @[regfile.scala 7:21]
    end else if (io_rd_ena) begin // @[regfile.scala 47:19]
      if (io_rd_addr != 5'h0) begin // @[regfile.scala 10:24]
        if (5'ha == io_rd_addr) begin // @[regfile.scala 11:18]
          regs_10 <= io_rd_data; // @[regfile.scala 11:18]
        end
      end
    end
    if (reset) begin // @[regfile.scala 7:21]
      regs_11 <= 64'h0; // @[regfile.scala 7:21]
    end else if (io_rd_ena) begin // @[regfile.scala 47:19]
      if (io_rd_addr != 5'h0) begin // @[regfile.scala 10:24]
        if (5'hb == io_rd_addr) begin // @[regfile.scala 11:18]
          regs_11 <= io_rd_data; // @[regfile.scala 11:18]
        end
      end
    end
    if (reset) begin // @[regfile.scala 7:21]
      regs_12 <= 64'h0; // @[regfile.scala 7:21]
    end else if (io_rd_ena) begin // @[regfile.scala 47:19]
      if (io_rd_addr != 5'h0) begin // @[regfile.scala 10:24]
        if (5'hc == io_rd_addr) begin // @[regfile.scala 11:18]
          regs_12 <= io_rd_data; // @[regfile.scala 11:18]
        end
      end
    end
    if (reset) begin // @[regfile.scala 7:21]
      regs_13 <= 64'h0; // @[regfile.scala 7:21]
    end else if (io_rd_ena) begin // @[regfile.scala 47:19]
      if (io_rd_addr != 5'h0) begin // @[regfile.scala 10:24]
        if (5'hd == io_rd_addr) begin // @[regfile.scala 11:18]
          regs_13 <= io_rd_data; // @[regfile.scala 11:18]
        end
      end
    end
    if (reset) begin // @[regfile.scala 7:21]
      regs_14 <= 64'h0; // @[regfile.scala 7:21]
    end else if (io_rd_ena) begin // @[regfile.scala 47:19]
      if (io_rd_addr != 5'h0) begin // @[regfile.scala 10:24]
        if (5'he == io_rd_addr) begin // @[regfile.scala 11:18]
          regs_14 <= io_rd_data; // @[regfile.scala 11:18]
        end
      end
    end
    if (reset) begin // @[regfile.scala 7:21]
      regs_15 <= 64'h0; // @[regfile.scala 7:21]
    end else if (io_rd_ena) begin // @[regfile.scala 47:19]
      if (io_rd_addr != 5'h0) begin // @[regfile.scala 10:24]
        if (5'hf == io_rd_addr) begin // @[regfile.scala 11:18]
          regs_15 <= io_rd_data; // @[regfile.scala 11:18]
        end
      end
    end
    if (reset) begin // @[regfile.scala 7:21]
      regs_16 <= 64'h0; // @[regfile.scala 7:21]
    end else if (io_rd_ena) begin // @[regfile.scala 47:19]
      if (io_rd_addr != 5'h0) begin // @[regfile.scala 10:24]
        if (5'h10 == io_rd_addr) begin // @[regfile.scala 11:18]
          regs_16 <= io_rd_data; // @[regfile.scala 11:18]
        end
      end
    end
    if (reset) begin // @[regfile.scala 7:21]
      regs_17 <= 64'h0; // @[regfile.scala 7:21]
    end else if (io_rd_ena) begin // @[regfile.scala 47:19]
      if (io_rd_addr != 5'h0) begin // @[regfile.scala 10:24]
        if (5'h11 == io_rd_addr) begin // @[regfile.scala 11:18]
          regs_17 <= io_rd_data; // @[regfile.scala 11:18]
        end
      end
    end
    if (reset) begin // @[regfile.scala 7:21]
      regs_18 <= 64'h0; // @[regfile.scala 7:21]
    end else if (io_rd_ena) begin // @[regfile.scala 47:19]
      if (io_rd_addr != 5'h0) begin // @[regfile.scala 10:24]
        if (5'h12 == io_rd_addr) begin // @[regfile.scala 11:18]
          regs_18 <= io_rd_data; // @[regfile.scala 11:18]
        end
      end
    end
    if (reset) begin // @[regfile.scala 7:21]
      regs_19 <= 64'h0; // @[regfile.scala 7:21]
    end else if (io_rd_ena) begin // @[regfile.scala 47:19]
      if (io_rd_addr != 5'h0) begin // @[regfile.scala 10:24]
        if (5'h13 == io_rd_addr) begin // @[regfile.scala 11:18]
          regs_19 <= io_rd_data; // @[regfile.scala 11:18]
        end
      end
    end
    if (reset) begin // @[regfile.scala 7:21]
      regs_20 <= 64'h0; // @[regfile.scala 7:21]
    end else if (io_rd_ena) begin // @[regfile.scala 47:19]
      if (io_rd_addr != 5'h0) begin // @[regfile.scala 10:24]
        if (5'h14 == io_rd_addr) begin // @[regfile.scala 11:18]
          regs_20 <= io_rd_data; // @[regfile.scala 11:18]
        end
      end
    end
    if (reset) begin // @[regfile.scala 7:21]
      regs_21 <= 64'h0; // @[regfile.scala 7:21]
    end else if (io_rd_ena) begin // @[regfile.scala 47:19]
      if (io_rd_addr != 5'h0) begin // @[regfile.scala 10:24]
        if (5'h15 == io_rd_addr) begin // @[regfile.scala 11:18]
          regs_21 <= io_rd_data; // @[regfile.scala 11:18]
        end
      end
    end
    if (reset) begin // @[regfile.scala 7:21]
      regs_22 <= 64'h0; // @[regfile.scala 7:21]
    end else if (io_rd_ena) begin // @[regfile.scala 47:19]
      if (io_rd_addr != 5'h0) begin // @[regfile.scala 10:24]
        if (5'h16 == io_rd_addr) begin // @[regfile.scala 11:18]
          regs_22 <= io_rd_data; // @[regfile.scala 11:18]
        end
      end
    end
    if (reset) begin // @[regfile.scala 7:21]
      regs_23 <= 64'h0; // @[regfile.scala 7:21]
    end else if (io_rd_ena) begin // @[regfile.scala 47:19]
      if (io_rd_addr != 5'h0) begin // @[regfile.scala 10:24]
        if (5'h17 == io_rd_addr) begin // @[regfile.scala 11:18]
          regs_23 <= io_rd_data; // @[regfile.scala 11:18]
        end
      end
    end
    if (reset) begin // @[regfile.scala 7:21]
      regs_24 <= 64'h0; // @[regfile.scala 7:21]
    end else if (io_rd_ena) begin // @[regfile.scala 47:19]
      if (io_rd_addr != 5'h0) begin // @[regfile.scala 10:24]
        if (5'h18 == io_rd_addr) begin // @[regfile.scala 11:18]
          regs_24 <= io_rd_data; // @[regfile.scala 11:18]
        end
      end
    end
    if (reset) begin // @[regfile.scala 7:21]
      regs_25 <= 64'h0; // @[regfile.scala 7:21]
    end else if (io_rd_ena) begin // @[regfile.scala 47:19]
      if (io_rd_addr != 5'h0) begin // @[regfile.scala 10:24]
        if (5'h19 == io_rd_addr) begin // @[regfile.scala 11:18]
          regs_25 <= io_rd_data; // @[regfile.scala 11:18]
        end
      end
    end
    if (reset) begin // @[regfile.scala 7:21]
      regs_26 <= 64'h0; // @[regfile.scala 7:21]
    end else if (io_rd_ena) begin // @[regfile.scala 47:19]
      if (io_rd_addr != 5'h0) begin // @[regfile.scala 10:24]
        if (5'h1a == io_rd_addr) begin // @[regfile.scala 11:18]
          regs_26 <= io_rd_data; // @[regfile.scala 11:18]
        end
      end
    end
    if (reset) begin // @[regfile.scala 7:21]
      regs_27 <= 64'h0; // @[regfile.scala 7:21]
    end else if (io_rd_ena) begin // @[regfile.scala 47:19]
      if (io_rd_addr != 5'h0) begin // @[regfile.scala 10:24]
        if (5'h1b == io_rd_addr) begin // @[regfile.scala 11:18]
          regs_27 <= io_rd_data; // @[regfile.scala 11:18]
        end
      end
    end
    if (reset) begin // @[regfile.scala 7:21]
      regs_28 <= 64'h0; // @[regfile.scala 7:21]
    end else if (io_rd_ena) begin // @[regfile.scala 47:19]
      if (io_rd_addr != 5'h0) begin // @[regfile.scala 10:24]
        if (5'h1c == io_rd_addr) begin // @[regfile.scala 11:18]
          regs_28 <= io_rd_data; // @[regfile.scala 11:18]
        end
      end
    end
    if (reset) begin // @[regfile.scala 7:21]
      regs_29 <= 64'h0; // @[regfile.scala 7:21]
    end else if (io_rd_ena) begin // @[regfile.scala 47:19]
      if (io_rd_addr != 5'h0) begin // @[regfile.scala 10:24]
        if (5'h1d == io_rd_addr) begin // @[regfile.scala 11:18]
          regs_29 <= io_rd_data; // @[regfile.scala 11:18]
        end
      end
    end
    if (reset) begin // @[regfile.scala 7:21]
      regs_30 <= 64'h0; // @[regfile.scala 7:21]
    end else if (io_rd_ena) begin // @[regfile.scala 47:19]
      if (io_rd_addr != 5'h0) begin // @[regfile.scala 10:24]
        if (5'h1e == io_rd_addr) begin // @[regfile.scala 11:18]
          regs_30 <= io_rd_data; // @[regfile.scala 11:18]
        end
      end
    end
    if (reset) begin // @[regfile.scala 7:21]
      regs_31 <= 64'h0; // @[regfile.scala 7:21]
    end else if (io_rd_ena) begin // @[regfile.scala 47:19]
      if (io_rd_addr != 5'h0) begin // @[regfile.scala 10:24]
        if (5'h1f == io_rd_addr) begin // @[regfile.scala 11:18]
          regs_31 <= io_rd_data; // @[regfile.scala 11:18]
        end
      end
    end
  end
// Register and memory initialization
`ifdef RANDOMIZE_GARBAGE_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_INVALID_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_REG_INIT
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_MEM_INIT
`define RANDOMIZE
`endif
`ifndef RANDOM
`define RANDOM $random
`endif
`ifdef RANDOMIZE_MEM_INIT
  integer initvar;
`endif
`ifndef SYNTHESIS
`ifdef FIRRTL_BEFORE_INITIAL
`FIRRTL_BEFORE_INITIAL
`endif
initial begin
  `ifdef RANDOMIZE
    `ifdef INIT_RANDOM
      `INIT_RANDOM
    `endif
    `ifndef VERILATOR
      `ifdef RANDOMIZE_DELAY
        #`RANDOMIZE_DELAY begin end
      `else
        #0.002 begin end
      `endif
    `endif
`ifdef RANDOMIZE_REG_INIT
  _RAND_0 = {2{`RANDOM}};
  regs_0 = _RAND_0[63:0];
  _RAND_1 = {2{`RANDOM}};
  regs_1 = _RAND_1[63:0];
  _RAND_2 = {2{`RANDOM}};
  regs_2 = _RAND_2[63:0];
  _RAND_3 = {2{`RANDOM}};
  regs_3 = _RAND_3[63:0];
  _RAND_4 = {2{`RANDOM}};
  regs_4 = _RAND_4[63:0];
  _RAND_5 = {2{`RANDOM}};
  regs_5 = _RAND_5[63:0];
  _RAND_6 = {2{`RANDOM}};
  regs_6 = _RAND_6[63:0];
  _RAND_7 = {2{`RANDOM}};
  regs_7 = _RAND_7[63:0];
  _RAND_8 = {2{`RANDOM}};
  regs_8 = _RAND_8[63:0];
  _RAND_9 = {2{`RANDOM}};
  regs_9 = _RAND_9[63:0];
  _RAND_10 = {2{`RANDOM}};
  regs_10 = _RAND_10[63:0];
  _RAND_11 = {2{`RANDOM}};
  regs_11 = _RAND_11[63:0];
  _RAND_12 = {2{`RANDOM}};
  regs_12 = _RAND_12[63:0];
  _RAND_13 = {2{`RANDOM}};
  regs_13 = _RAND_13[63:0];
  _RAND_14 = {2{`RANDOM}};
  regs_14 = _RAND_14[63:0];
  _RAND_15 = {2{`RANDOM}};
  regs_15 = _RAND_15[63:0];
  _RAND_16 = {2{`RANDOM}};
  regs_16 = _RAND_16[63:0];
  _RAND_17 = {2{`RANDOM}};
  regs_17 = _RAND_17[63:0];
  _RAND_18 = {2{`RANDOM}};
  regs_18 = _RAND_18[63:0];
  _RAND_19 = {2{`RANDOM}};
  regs_19 = _RAND_19[63:0];
  _RAND_20 = {2{`RANDOM}};
  regs_20 = _RAND_20[63:0];
  _RAND_21 = {2{`RANDOM}};
  regs_21 = _RAND_21[63:0];
  _RAND_22 = {2{`RANDOM}};
  regs_22 = _RAND_22[63:0];
  _RAND_23 = {2{`RANDOM}};
  regs_23 = _RAND_23[63:0];
  _RAND_24 = {2{`RANDOM}};
  regs_24 = _RAND_24[63:0];
  _RAND_25 = {2{`RANDOM}};
  regs_25 = _RAND_25[63:0];
  _RAND_26 = {2{`RANDOM}};
  regs_26 = _RAND_26[63:0];
  _RAND_27 = {2{`RANDOM}};
  regs_27 = _RAND_27[63:0];
  _RAND_28 = {2{`RANDOM}};
  regs_28 = _RAND_28[63:0];
  _RAND_29 = {2{`RANDOM}};
  regs_29 = _RAND_29[63:0];
  _RAND_30 = {2{`RANDOM}};
  regs_30 = _RAND_30[63:0];
  _RAND_31 = {2{`RANDOM}};
  regs_31 = _RAND_31[63:0];
`endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`ifdef FIRRTL_AFTER_INITIAL
`FIRRTL_AFTER_INITIAL
`endif
`endif // SYNTHESIS
endmodule
module Top(
  input         clock,
  input         reset,
  input         io_ifuout_aw_ready,
  output        io_ifuout_aw_valid,
  output [31:0] io_ifuout_aw_bits_addr,
  output [2:0]  io_ifuout_aw_bits_prot,
  output [2:0]  io_ifuout_aw_bits_id,
  output        io_ifuout_aw_bits_user,
  output [7:0]  io_ifuout_aw_bits_len,
  output [2:0]  io_ifuout_aw_bits_size,
  output [1:0]  io_ifuout_aw_bits_burst,
  output        io_ifuout_aw_bits_lock,
  output [3:0]  io_ifuout_aw_bits_cache,
  output [3:0]  io_ifuout_aw_bits_qos,
  output [3:0]  io_ifuout_aw_bits_region,
  input         io_ifuout_w_ready,
  output        io_ifuout_w_valid,
  output [63:0] io_ifuout_w_bits_data,
  output [7:0]  io_ifuout_w_bits_strb,
  output        io_ifuout_w_bits_last,
  output        io_ifuout_b_ready,
  input         io_ifuout_b_valid,
  input  [1:0]  io_ifuout_b_bits_resp,
  input  [2:0]  io_ifuout_b_bits_id,
  input         io_ifuout_b_bits_user,
  input         io_ifuout_ar_ready,
  output        io_ifuout_ar_valid,
  output [31:0] io_ifuout_ar_bits_addr,
  output [2:0]  io_ifuout_ar_bits_prot,
  output [2:0]  io_ifuout_ar_bits_id,
  output        io_ifuout_ar_bits_user,
  output [7:0]  io_ifuout_ar_bits_len,
  output [2:0]  io_ifuout_ar_bits_size,
  output [1:0]  io_ifuout_ar_bits_burst,
  output        io_ifuout_ar_bits_lock,
  output [3:0]  io_ifuout_ar_bits_cache,
  output [3:0]  io_ifuout_ar_bits_qos,
  output [3:0]  io_ifuout_ar_bits_region,
  output        io_ifuout_r_ready,
  input         io_ifuout_r_valid,
  input  [1:0]  io_ifuout_r_bits_resp,
  input  [63:0] io_ifuout_r_bits_data,
  input         io_ifuout_r_bits_last,
  input  [2:0]  io_ifuout_r_bits_id,
  input         io_ifuout_r_bits_user,
  input         io_lsuout_aw_ready,
  output        io_lsuout_aw_valid,
  output [31:0] io_lsuout_aw_bits_addr,
  output [2:0]  io_lsuout_aw_bits_prot,
  output [3:0]  io_lsuout_aw_bits_id,
  output        io_lsuout_aw_bits_user,
  output [7:0]  io_lsuout_aw_bits_len,
  output [2:0]  io_lsuout_aw_bits_size,
  output [1:0]  io_lsuout_aw_bits_burst,
  output        io_lsuout_aw_bits_lock,
  output [3:0]  io_lsuout_aw_bits_cache,
  output [3:0]  io_lsuout_aw_bits_qos,
  output [3:0]  io_lsuout_aw_bits_region,
  input         io_lsuout_w_ready,
  output        io_lsuout_w_valid,
  output [63:0] io_lsuout_w_bits_data,
  output [7:0]  io_lsuout_w_bits_strb,
  output        io_lsuout_w_bits_last,
  output        io_lsuout_b_ready,
  input         io_lsuout_b_valid,
  input  [1:0]  io_lsuout_b_bits_resp,
  input  [3:0]  io_lsuout_b_bits_id,
  input         io_lsuout_b_bits_user,
  input         io_lsuout_ar_ready,
  output        io_lsuout_ar_valid,
  output [31:0] io_lsuout_ar_bits_addr,
  output [2:0]  io_lsuout_ar_bits_prot,
  output [3:0]  io_lsuout_ar_bits_id,
  output        io_lsuout_ar_bits_user,
  output [7:0]  io_lsuout_ar_bits_len,
  output [2:0]  io_lsuout_ar_bits_size,
  output [1:0]  io_lsuout_ar_bits_burst,
  output        io_lsuout_ar_bits_lock,
  output [3:0]  io_lsuout_ar_bits_cache,
  output [3:0]  io_lsuout_ar_bits_qos,
  output [3:0]  io_lsuout_ar_bits_region,
  output        io_lsuout_r_ready,
  input         io_lsuout_r_valid,
  input  [1:0]  io_lsuout_r_bits_resp,
  input  [63:0] io_lsuout_r_bits_data,
  input         io_lsuout_r_bits_last,
  input  [3:0]  io_lsuout_r_bits_id,
  input         io_lsuout_r_bits_user
);
  wire  ifu_clock; // @[Top.scala 22:26]
  wire  ifu_reset; // @[Top.scala 22:26]
  wire [63:0] ifu_io_in_new_pc; // @[Top.scala 22:26]
  wire  ifu_io_in_valid; // @[Top.scala 22:26]
  wire [63:0] ifu_io_out_pc; // @[Top.scala 22:26]
  wire [31:0] ifu_io_out_instr; // @[Top.scala 22:26]
  wire  ifu_io_ifuaxi_ar_ready; // @[Top.scala 22:26]
  wire  ifu_io_ifuaxi_ar_valid; // @[Top.scala 22:26]
  wire [31:0] ifu_io_ifuaxi_ar_bits_addr; // @[Top.scala 22:26]
  wire  ifu_io_ifuaxi_r_ready; // @[Top.scala 22:26]
  wire  ifu_io_ifuaxi_r_valid; // @[Top.scala 22:26]
  wire [63:0] ifu_io_ifuaxi_r_bits_data; // @[Top.scala 22:26]
  wire [63:0] idu_io_in_pc; // @[Top.scala 23:26]
  wire [31:0] idu_io_in_instr; // @[Top.scala 23:26]
  wire [63:0] idu_io_out_cf_pc; // @[Top.scala 23:26]
  wire [1:0] idu_io_out_ctrl_src1Type; // @[Top.scala 23:26]
  wire [1:0] idu_io_out_ctrl_src2Type; // @[Top.scala 23:26]
  wire [2:0] idu_io_out_ctrl_funcType; // @[Top.scala 23:26]
  wire [6:0] idu_io_out_ctrl_funcOpType; // @[Top.scala 23:26]
  wire [4:0] idu_io_out_ctrl_rfSrc1; // @[Top.scala 23:26]
  wire [4:0] idu_io_out_ctrl_rfSrc2; // @[Top.scala 23:26]
  wire [4:0] idu_io_out_ctrl_rfrd; // @[Top.scala 23:26]
  wire [63:0] idu_io_out_data_imm; // @[Top.scala 23:26]
  wire [63:0] idu_io_out_data_uimm_ext; // @[Top.scala 23:26]
  wire [63:0] dis_io_in_cf_pc; // @[Top.scala 24:31]
  wire [1:0] dis_io_in_ctrl_src1Type; // @[Top.scala 24:31]
  wire [1:0] dis_io_in_ctrl_src2Type; // @[Top.scala 24:31]
  wire [2:0] dis_io_in_ctrl_funcType; // @[Top.scala 24:31]
  wire [6:0] dis_io_in_ctrl_funcOpType; // @[Top.scala 24:31]
  wire [4:0] dis_io_in_ctrl_rfrd; // @[Top.scala 24:31]
  wire [63:0] dis_io_in_data_imm; // @[Top.scala 24:31]
  wire [63:0] dis_io_in_data_uimm_ext; // @[Top.scala 24:31]
  wire [63:0] dis_io_src1; // @[Top.scala 24:31]
  wire [63:0] dis_io_src2; // @[Top.scala 24:31]
  wire [63:0] dis_io_out_cf_pc; // @[Top.scala 24:31]
  wire [2:0] dis_io_out_ctrl_funcType; // @[Top.scala 24:31]
  wire [6:0] dis_io_out_ctrl_funcOpType; // @[Top.scala 24:31]
  wire [4:0] dis_io_out_ctrl_rfrd; // @[Top.scala 24:31]
  wire [63:0] dis_io_out_data_src1; // @[Top.scala 24:31]
  wire [63:0] dis_io_out_data_src2; // @[Top.scala 24:31]
  wire [63:0] dis_io_out_data_imm; // @[Top.scala 24:31]
  wire  exu_clock; // @[Top.scala 25:26]
  wire  exu_reset; // @[Top.scala 25:26]
  wire [63:0] exu_io_in_cf_pc; // @[Top.scala 25:26]
  wire [2:0] exu_io_in_ctrl_funcType; // @[Top.scala 25:26]
  wire [6:0] exu_io_in_ctrl_funcOpType; // @[Top.scala 25:26]
  wire [4:0] exu_io_in_ctrl_rfrd; // @[Top.scala 25:26]
  wire [63:0] exu_io_in_data_src1; // @[Top.scala 25:26]
  wire [63:0] exu_io_in_data_src2; // @[Top.scala 25:26]
  wire [63:0] exu_io_in_data_imm; // @[Top.scala 25:26]
  wire [4:0] exu_io_reg_write_back_addr; // @[Top.scala 25:26]
  wire [63:0] exu_io_reg_write_back_data; // @[Top.scala 25:26]
  wire  exu_io_reg_write_back_ena; // @[Top.scala 25:26]
  wire [63:0] exu_io_branch_new_pc; // @[Top.scala 25:26]
  wire  exu_io_branch_valid; // @[Top.scala 25:26]
  wire  exu_io_lsuaxi_aw_ready; // @[Top.scala 25:26]
  wire  exu_io_lsuaxi_aw_valid; // @[Top.scala 25:26]
  wire [31:0] exu_io_lsuaxi_aw_bits_addr; // @[Top.scala 25:26]
  wire  exu_io_lsuaxi_w_ready; // @[Top.scala 25:26]
  wire  exu_io_lsuaxi_w_valid; // @[Top.scala 25:26]
  wire [63:0] exu_io_lsuaxi_w_bits_data; // @[Top.scala 25:26]
  wire  exu_io_lsuaxi_b_ready; // @[Top.scala 25:26]
  wire  exu_io_lsuaxi_b_valid; // @[Top.scala 25:26]
  wire  exu_io_lsuaxi_ar_ready; // @[Top.scala 25:26]
  wire [31:0] exu_io_lsuaxi_ar_bits_addr; // @[Top.scala 25:26]
  wire  exu_io_lsuaxi_r_ready; // @[Top.scala 25:26]
  wire  exu_io_lsuaxi_r_valid; // @[Top.scala 25:26]
  wire [63:0] exu_io_lsuaxi_r_bits_data; // @[Top.scala 25:26]
  wire [4:0] wbu_io_in_addr; // @[Top.scala 26:26]
  wire [63:0] wbu_io_in_data; // @[Top.scala 26:26]
  wire  wbu_io_in_ena; // @[Top.scala 26:26]
  wire [4:0] wbu_io_out_addr; // @[Top.scala 26:26]
  wire [63:0] wbu_io_out_data; // @[Top.scala 26:26]
  wire  wbu_io_out_ena; // @[Top.scala 26:26]
  wire  reg__clock; // @[Top.scala 27:30]
  wire  reg__reset; // @[Top.scala 27:30]
  wire [4:0] reg__io_src1_addr; // @[Top.scala 27:30]
  wire [63:0] reg__io_src1_data; // @[Top.scala 27:30]
  wire [4:0] reg__io_src2_addr; // @[Top.scala 27:30]
  wire [63:0] reg__io_src2_data; // @[Top.scala 27:30]
  wire [4:0] reg__io_rd_addr; // @[Top.scala 27:30]
  wire [63:0] reg__io_rd_data; // @[Top.scala 27:30]
  wire  reg__io_rd_ena; // @[Top.scala 27:30]
  IFU ifu ( // @[Top.scala 22:26]
    .clock(ifu_clock),
    .reset(ifu_reset),
    .io_in_new_pc(ifu_io_in_new_pc),
    .io_in_valid(ifu_io_in_valid),
    .io_out_pc(ifu_io_out_pc),
    .io_out_instr(ifu_io_out_instr),
    .io_ifuaxi_ar_ready(ifu_io_ifuaxi_ar_ready),
    .io_ifuaxi_ar_valid(ifu_io_ifuaxi_ar_valid),
    .io_ifuaxi_ar_bits_addr(ifu_io_ifuaxi_ar_bits_addr),
    .io_ifuaxi_r_ready(ifu_io_ifuaxi_r_ready),
    .io_ifuaxi_r_valid(ifu_io_ifuaxi_r_valid),
    .io_ifuaxi_r_bits_data(ifu_io_ifuaxi_r_bits_data)
  );
  IDU idu ( // @[Top.scala 23:26]
    .io_in_pc(idu_io_in_pc),
    .io_in_instr(idu_io_in_instr),
    .io_out_cf_pc(idu_io_out_cf_pc),
    .io_out_ctrl_src1Type(idu_io_out_ctrl_src1Type),
    .io_out_ctrl_src2Type(idu_io_out_ctrl_src2Type),
    .io_out_ctrl_funcType(idu_io_out_ctrl_funcType),
    .io_out_ctrl_funcOpType(idu_io_out_ctrl_funcOpType),
    .io_out_ctrl_rfSrc1(idu_io_out_ctrl_rfSrc1),
    .io_out_ctrl_rfSrc2(idu_io_out_ctrl_rfSrc2),
    .io_out_ctrl_rfrd(idu_io_out_ctrl_rfrd),
    .io_out_data_imm(idu_io_out_data_imm),
    .io_out_data_uimm_ext(idu_io_out_data_uimm_ext)
  );
  IDUtoEXU dis ( // @[Top.scala 24:31]
    .io_in_cf_pc(dis_io_in_cf_pc),
    .io_in_ctrl_src1Type(dis_io_in_ctrl_src1Type),
    .io_in_ctrl_src2Type(dis_io_in_ctrl_src2Type),
    .io_in_ctrl_funcType(dis_io_in_ctrl_funcType),
    .io_in_ctrl_funcOpType(dis_io_in_ctrl_funcOpType),
    .io_in_ctrl_rfrd(dis_io_in_ctrl_rfrd),
    .io_in_data_imm(dis_io_in_data_imm),
    .io_in_data_uimm_ext(dis_io_in_data_uimm_ext),
    .io_src1(dis_io_src1),
    .io_src2(dis_io_src2),
    .io_out_cf_pc(dis_io_out_cf_pc),
    .io_out_ctrl_funcType(dis_io_out_ctrl_funcType),
    .io_out_ctrl_funcOpType(dis_io_out_ctrl_funcOpType),
    .io_out_ctrl_rfrd(dis_io_out_ctrl_rfrd),
    .io_out_data_src1(dis_io_out_data_src1),
    .io_out_data_src2(dis_io_out_data_src2),
    .io_out_data_imm(dis_io_out_data_imm)
  );
  EXU exu ( // @[Top.scala 25:26]
    .clock(exu_clock),
    .reset(exu_reset),
    .io_in_cf_pc(exu_io_in_cf_pc),
    .io_in_ctrl_funcType(exu_io_in_ctrl_funcType),
    .io_in_ctrl_funcOpType(exu_io_in_ctrl_funcOpType),
    .io_in_ctrl_rfrd(exu_io_in_ctrl_rfrd),
    .io_in_data_src1(exu_io_in_data_src1),
    .io_in_data_src2(exu_io_in_data_src2),
    .io_in_data_imm(exu_io_in_data_imm),
    .io_reg_write_back_addr(exu_io_reg_write_back_addr),
    .io_reg_write_back_data(exu_io_reg_write_back_data),
    .io_reg_write_back_ena(exu_io_reg_write_back_ena),
    .io_branch_new_pc(exu_io_branch_new_pc),
    .io_branch_valid(exu_io_branch_valid),
    .io_lsuaxi_aw_ready(exu_io_lsuaxi_aw_ready),
    .io_lsuaxi_aw_valid(exu_io_lsuaxi_aw_valid),
    .io_lsuaxi_aw_bits_addr(exu_io_lsuaxi_aw_bits_addr),
    .io_lsuaxi_w_ready(exu_io_lsuaxi_w_ready),
    .io_lsuaxi_w_valid(exu_io_lsuaxi_w_valid),
    .io_lsuaxi_w_bits_data(exu_io_lsuaxi_w_bits_data),
    .io_lsuaxi_b_ready(exu_io_lsuaxi_b_ready),
    .io_lsuaxi_b_valid(exu_io_lsuaxi_b_valid),
    .io_lsuaxi_ar_ready(exu_io_lsuaxi_ar_ready),
    .io_lsuaxi_ar_bits_addr(exu_io_lsuaxi_ar_bits_addr),
    .io_lsuaxi_r_ready(exu_io_lsuaxi_r_ready),
    .io_lsuaxi_r_valid(exu_io_lsuaxi_r_valid),
    .io_lsuaxi_r_bits_data(exu_io_lsuaxi_r_bits_data)
  );
  WBU wbu ( // @[Top.scala 26:26]
    .io_in_addr(wbu_io_in_addr),
    .io_in_data(wbu_io_in_data),
    .io_in_ena(wbu_io_in_ena),
    .io_out_addr(wbu_io_out_addr),
    .io_out_data(wbu_io_out_data),
    .io_out_ena(wbu_io_out_ena)
  );
  Regfile reg_ ( // @[Top.scala 27:30]
    .clock(reg__clock),
    .reset(reg__reset),
    .io_src1_addr(reg__io_src1_addr),
    .io_src1_data(reg__io_src1_data),
    .io_src2_addr(reg__io_src2_addr),
    .io_src2_data(reg__io_src2_data),
    .io_rd_addr(reg__io_rd_addr),
    .io_rd_data(reg__io_rd_data),
    .io_rd_ena(reg__io_rd_ena)
  );
  assign io_ifuout_aw_valid = 1'h0; // @[Top.scala 40:18]
  assign io_ifuout_aw_bits_addr = 32'h0; // @[Top.scala 40:18]
  assign io_ifuout_aw_bits_prot = 3'h0; // @[Top.scala 40:18]
  assign io_ifuout_aw_bits_id = 3'h0; // @[Top.scala 40:18]
  assign io_ifuout_aw_bits_user = 1'h0; // @[Top.scala 40:18]
  assign io_ifuout_aw_bits_len = 8'h0; // @[Top.scala 40:18]
  assign io_ifuout_aw_bits_size = 3'h0; // @[Top.scala 40:18]
  assign io_ifuout_aw_bits_burst = 2'h0; // @[Top.scala 40:18]
  assign io_ifuout_aw_bits_lock = 1'h0; // @[Top.scala 40:18]
  assign io_ifuout_aw_bits_cache = 4'h0; // @[Top.scala 40:18]
  assign io_ifuout_aw_bits_qos = 4'h0; // @[Top.scala 40:18]
  assign io_ifuout_aw_bits_region = 4'h0; // @[Top.scala 40:18]
  assign io_ifuout_w_valid = 1'h0; // @[Top.scala 40:18]
  assign io_ifuout_w_bits_data = 64'h0; // @[Top.scala 40:18]
  assign io_ifuout_w_bits_strb = 8'h0; // @[Top.scala 40:18]
  assign io_ifuout_w_bits_last = 1'h0; // @[Top.scala 40:18]
  assign io_ifuout_b_ready = 1'h0; // @[Top.scala 40:18]
  assign io_ifuout_ar_valid = ifu_io_ifuaxi_ar_valid; // @[Top.scala 40:18]
  assign io_ifuout_ar_bits_addr = ifu_io_ifuaxi_ar_bits_addr; // @[Top.scala 40:18]
  assign io_ifuout_ar_bits_prot = 3'h0; // @[Top.scala 40:18]
  assign io_ifuout_ar_bits_id = 3'h1; // @[Top.scala 40:18]
  assign io_ifuout_ar_bits_user = 1'h0; // @[Top.scala 40:18]
  assign io_ifuout_ar_bits_len = 8'h0; // @[Top.scala 40:18]
  assign io_ifuout_ar_bits_size = 3'h1; // @[Top.scala 40:18]
  assign io_ifuout_ar_bits_burst = 2'h2; // @[Top.scala 40:18]
  assign io_ifuout_ar_bits_lock = 1'h0; // @[Top.scala 40:18]
  assign io_ifuout_ar_bits_cache = 4'h0; // @[Top.scala 40:18]
  assign io_ifuout_ar_bits_qos = 4'h0; // @[Top.scala 40:18]
  assign io_ifuout_ar_bits_region = 4'h0; // @[Top.scala 40:18]
  assign io_ifuout_r_ready = ifu_io_ifuaxi_r_ready; // @[Top.scala 40:18]
  assign io_lsuout_aw_valid = exu_io_lsuaxi_aw_valid; // @[Top.scala 41:18]
  assign io_lsuout_aw_bits_addr = exu_io_lsuaxi_aw_bits_addr; // @[Top.scala 41:18]
  assign io_lsuout_aw_bits_prot = 3'h0; // @[Top.scala 41:18]
  assign io_lsuout_aw_bits_id = 4'h3; // @[Top.scala 41:18]
  assign io_lsuout_aw_bits_user = 1'h0; // @[Top.scala 41:18]
  assign io_lsuout_aw_bits_len = 8'h0; // @[Top.scala 41:18]
  assign io_lsuout_aw_bits_size = 3'h1; // @[Top.scala 41:18]
  assign io_lsuout_aw_bits_burst = 2'h2; // @[Top.scala 41:18]
  assign io_lsuout_aw_bits_lock = 1'h0; // @[Top.scala 41:18]
  assign io_lsuout_aw_bits_cache = 4'h0; // @[Top.scala 41:18]
  assign io_lsuout_aw_bits_qos = 4'h0; // @[Top.scala 41:18]
  assign io_lsuout_aw_bits_region = 4'h0; // @[Top.scala 41:18]
  assign io_lsuout_w_valid = exu_io_lsuaxi_w_valid; // @[Top.scala 41:18]
  assign io_lsuout_w_bits_data = exu_io_lsuaxi_w_bits_data; // @[Top.scala 41:18]
  assign io_lsuout_w_bits_strb = 8'h0; // @[Top.scala 41:18]
  assign io_lsuout_w_bits_last = 1'h1; // @[Top.scala 41:18]
  assign io_lsuout_b_ready = exu_io_lsuaxi_b_ready; // @[Top.scala 41:18]
  assign io_lsuout_ar_valid = 1'h0; // @[Top.scala 41:18]
  assign io_lsuout_ar_bits_addr = exu_io_lsuaxi_ar_bits_addr; // @[Top.scala 41:18]
  assign io_lsuout_ar_bits_prot = 3'h0; // @[Top.scala 41:18]
  assign io_lsuout_ar_bits_id = 4'h2; // @[Top.scala 41:18]
  assign io_lsuout_ar_bits_user = 1'h0; // @[Top.scala 41:18]
  assign io_lsuout_ar_bits_len = 8'h0; // @[Top.scala 41:18]
  assign io_lsuout_ar_bits_size = 3'h1; // @[Top.scala 41:18]
  assign io_lsuout_ar_bits_burst = 2'h2; // @[Top.scala 41:18]
  assign io_lsuout_ar_bits_lock = 1'h0; // @[Top.scala 41:18]
  assign io_lsuout_ar_bits_cache = 4'h0; // @[Top.scala 41:18]
  assign io_lsuout_ar_bits_qos = 4'h0; // @[Top.scala 41:18]
  assign io_lsuout_ar_bits_region = 4'h0; // @[Top.scala 41:18]
  assign io_lsuout_r_ready = exu_io_lsuaxi_r_ready; // @[Top.scala 41:18]
  assign ifu_clock = clock;
  assign ifu_reset = reset;
  assign ifu_io_in_new_pc = exu_io_branch_new_pc; // @[Top.scala 33:29]
  assign ifu_io_in_valid = exu_io_branch_valid; // @[Top.scala 33:29]
  assign ifu_io_ifuaxi_ar_ready = io_ifuout_ar_ready; // @[Top.scala 40:18]
  assign ifu_io_ifuaxi_r_valid = io_ifuout_r_valid; // @[Top.scala 40:18]
  assign ifu_io_ifuaxi_r_bits_data = io_ifuout_r_bits_data; // @[Top.scala 40:18]
  assign idu_io_in_pc = ifu_io_out_pc; // @[Top.scala 29:29]
  assign idu_io_in_instr = ifu_io_out_instr; // @[Top.scala 29:29]
  assign dis_io_in_cf_pc = idu_io_out_cf_pc; // @[Top.scala 30:29]
  assign dis_io_in_ctrl_src1Type = idu_io_out_ctrl_src1Type; // @[Top.scala 30:29]
  assign dis_io_in_ctrl_src2Type = idu_io_out_ctrl_src2Type; // @[Top.scala 30:29]
  assign dis_io_in_ctrl_funcType = idu_io_out_ctrl_funcType; // @[Top.scala 30:29]
  assign dis_io_in_ctrl_funcOpType = idu_io_out_ctrl_funcOpType; // @[Top.scala 30:29]
  assign dis_io_in_ctrl_rfrd = idu_io_out_ctrl_rfrd; // @[Top.scala 30:29]
  assign dis_io_in_data_imm = idu_io_out_data_imm; // @[Top.scala 30:29]
  assign dis_io_in_data_uimm_ext = idu_io_out_data_uimm_ext; // @[Top.scala 30:29]
  assign dis_io_src1 = reg__io_src1_data; // @[Top.scala 37:22]
  assign dis_io_src2 = reg__io_src2_data; // @[Top.scala 38:22]
  assign exu_clock = clock;
  assign exu_reset = reset;
  assign exu_io_in_cf_pc = dis_io_out_cf_pc; // @[Top.scala 31:29]
  assign exu_io_in_ctrl_funcType = dis_io_out_ctrl_funcType; // @[Top.scala 31:29]
  assign exu_io_in_ctrl_funcOpType = dis_io_out_ctrl_funcOpType; // @[Top.scala 31:29]
  assign exu_io_in_ctrl_rfrd = dis_io_out_ctrl_rfrd; // @[Top.scala 31:29]
  assign exu_io_in_data_src1 = dis_io_out_data_src1; // @[Top.scala 31:29]
  assign exu_io_in_data_src2 = dis_io_out_data_src2; // @[Top.scala 31:29]
  assign exu_io_in_data_imm = dis_io_out_data_imm; // @[Top.scala 31:29]
  assign exu_io_lsuaxi_aw_ready = io_lsuout_aw_ready; // @[Top.scala 41:18]
  assign exu_io_lsuaxi_w_ready = io_lsuout_w_ready; // @[Top.scala 41:18]
  assign exu_io_lsuaxi_b_valid = io_lsuout_b_valid; // @[Top.scala 41:18]
  assign exu_io_lsuaxi_ar_ready = io_lsuout_ar_ready; // @[Top.scala 41:18]
  assign exu_io_lsuaxi_r_valid = io_lsuout_r_valid; // @[Top.scala 41:18]
  assign exu_io_lsuaxi_r_bits_data = io_lsuout_r_bits_data; // @[Top.scala 41:18]
  assign wbu_io_in_addr = exu_io_reg_write_back_addr; // @[Top.scala 32:29]
  assign wbu_io_in_data = exu_io_reg_write_back_data; // @[Top.scala 32:29]
  assign wbu_io_in_ena = exu_io_reg_write_back_ena; // @[Top.scala 32:29]
  assign reg__clock = clock;
  assign reg__reset = reset;
  assign reg__io_src1_addr = idu_io_out_ctrl_rfSrc1; // @[Top.scala 35:22]
  assign reg__io_src2_addr = idu_io_out_ctrl_rfSrc2; // @[Top.scala 36:22]
  assign reg__io_rd_addr = wbu_io_out_addr; // @[Top.scala 34:29]
  assign reg__io_rd_data = wbu_io_out_data; // @[Top.scala 34:29]
  assign reg__io_rd_ena = wbu_io_out_ena; // @[Top.scala 34:29]
endmodule
