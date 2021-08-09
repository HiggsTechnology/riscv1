module IFU(
  input         clock,
  input         reset,
  input  [63:0] io_in_newPC,
  input         io_in_valid,
  output [63:0] io_out_pc,
  output [31:0] io_out_instr
);
`ifdef RANDOMIZE_REG_INIT
  reg [63:0] _RAND_0;
`endif // RANDOMIZE_REG_INIT
  wire  ram_clk; // @[IFU.scala 15:19]
  wire  ram_en; // @[IFU.scala 15:19]
  wire [63:0] ram_rIdx; // @[IFU.scala 15:19]
  wire [63:0] ram_rdata; // @[IFU.scala 15:19]
  wire [63:0] ram_wIdx; // @[IFU.scala 15:19]
  wire [63:0] ram_wdata; // @[IFU.scala 15:19]
  wire [63:0] ram_wmask; // @[IFU.scala 15:19]
  wire  ram_wen; // @[IFU.scala 15:19]
  reg [63:0] pc; // @[IFU.scala 11:19]
  wire [63:0] _pc_T_1 = io_in_newPC + 64'h4; // @[IFU.scala 12:45]
  wire [63:0] _pc_T_3 = pc + 64'h4; // @[IFU.scala 12:55]
  wire [63:0] _idx_T_1 = pc - 64'h80000000; // @[IFU.scala 19:17]
  wire [60:0] idx = _idx_T_1[63:3]; // @[IFU.scala 19:31]
  RAMHelper ram ( // @[IFU.scala 15:19]
    .clk(ram_clk),
    .en(ram_en),
    .rIdx(ram_rIdx),
    .rdata(ram_rdata),
    .wIdx(ram_wIdx),
    .wdata(ram_wdata),
    .wmask(ram_wmask),
    .wen(ram_wen)
  );
  assign io_out_pc = pc; // @[IFU.scala 13:13]
  assign io_out_instr = pc[2] ? ram_rdata[63:32] : ram_rdata[31:0]; // @[IFU.scala 28:22]
  assign ram_clk = clock; // @[IFU.scala 16:14]
  assign ram_en = ~reset; // @[IFU.scala 17:17]
  assign ram_rIdx = {{3'd0}, idx}; // @[IFU.scala 19:31]
  assign ram_wIdx = 64'h0;
  assign ram_wdata = 64'h0;
  assign ram_wmask = 64'h0;
  assign ram_wen = 1'h0; // @[IFU.scala 24:15]
  always @(posedge clock) begin
    if (reset) begin // @[IFU.scala 11:19]
      pc <= 64'h80000000; // @[IFU.scala 11:19]
    end else if (io_in_valid) begin // @[IFU.scala 12:19]
      pc <= _pc_T_1;
    end else begin
      pc <= _pc_T_3;
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
  pc = _RAND_0[63:0];
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
  output        io_out_ctrl_src1Type,
  output        io_out_ctrl_src2Type,
  output [2:0]  io_out_ctrl_funcType,
  output [6:0]  io_out_ctrl_funcOpType,
  output [4:0]  io_out_ctrl_rfrd,
  output [63:0] io_out_data_imm
);
  wire [4:0] rdAddr = io_in_instr[11:7]; // @[IDU.scala 15:74]
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
  wire  _io_out_ctrl_src1Type_T = 3'h4 == instrType; // @[utils.scala 8:34]
  wire  _io_out_ctrl_src1Type_T_2 = 3'h2 == instrType; // @[utils.scala 8:34]
  wire  _io_out_ctrl_src1Type_T_3 = 3'h1 == instrType; // @[utils.scala 8:34]
  wire  _io_out_ctrl_src1Type_T_4 = 3'h6 == instrType; // @[utils.scala 8:34]
  wire  _io_out_ctrl_src1Type_T_5 = 3'h7 == instrType; // @[utils.scala 8:34]
  wire  _io_out_ctrl_src1Type_T_6 = 3'h0 == instrType; // @[utils.scala 8:34]
  wire [11:0] imm_lo = io_in_instr[31:20]; // @[IDU.scala 40:29]
  wire  imm_signBit = imm_lo[11]; // @[utils.scala 14:20]
  wire [51:0] imm_hi = imm_signBit ? 52'hfffffffffffff : 52'h0; // @[Bitwise.scala 72:12]
  wire [63:0] _imm_T_1 = {imm_hi,imm_lo}; // @[Cat.scala 30:58]
  wire [6:0] imm_hi_1 = io_in_instr[31:25]; // @[IDU.scala 41:33]
  wire [11:0] imm_lo_2 = {imm_hi_1,rdAddr}; // @[Cat.scala 30:58]
  wire  imm_signBit_1 = imm_lo_2[11]; // @[utils.scala 14:20]
  wire [51:0] imm_hi_2 = imm_signBit_1 ? 52'hfffffffffffff : 52'h0; // @[Bitwise.scala 72:12]
  wire [63:0] _imm_T_3 = {imm_hi_2,imm_hi_1,rdAddr}; // @[Cat.scala 30:58]
  wire  imm_hi_hi_hi = io_in_instr[31]; // @[IDU.scala 42:33]
  wire  imm_hi_hi_lo = io_in_instr[7]; // @[IDU.scala 42:44]
  wire [5:0] imm_hi_lo = io_in_instr[30:25]; // @[IDU.scala 42:54]
  wire [3:0] imm_lo_hi = io_in_instr[11:8]; // @[IDU.scala 42:69]
  wire [12:0] imm_lo_4 = {imm_hi_hi_hi,imm_hi_hi_lo,imm_hi_lo,imm_lo_hi,1'h0}; // @[Cat.scala 30:58]
  wire  imm_signBit_2 = imm_lo_4[12]; // @[utils.scala 14:20]
  wire [50:0] imm_hi_4 = imm_signBit_2 ? 51'h7ffffffffffff : 51'h0; // @[Bitwise.scala 72:12]
  wire [63:0] _imm_T_5 = {imm_hi_4,imm_hi_hi_hi,imm_hi_hi_lo,imm_hi_lo,imm_lo_hi,1'h0}; // @[Cat.scala 30:58]
  wire [19:0] imm_hi_5 = io_in_instr[31:12]; // @[IDU.scala 43:33]
  wire [31:0] imm_lo_5 = {imm_hi_5,12'h0}; // @[Cat.scala 30:58]
  wire  imm_signBit_3 = imm_lo_5[31]; // @[utils.scala 14:20]
  wire [31:0] imm_hi_6 = imm_signBit_3 ? 32'hffffffff : 32'h0; // @[Bitwise.scala 72:12]
  wire [63:0] _imm_T_7 = {imm_hi_6,imm_hi_5,12'h0}; // @[Cat.scala 30:58]
  wire [7:0] imm_hi_hi_lo_1 = io_in_instr[19:12]; // @[IDU.scala 44:44]
  wire  imm_hi_lo_1 = io_in_instr[20]; // @[IDU.scala 44:59]
  wire [9:0] imm_lo_hi_1 = io_in_instr[30:21]; // @[IDU.scala 44:70]
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
  assign io_out_cf_pc = io_in_pc; // @[IDU.scala 18:24]
  assign io_out_ctrl_src1Type = _io_out_ctrl_src1Type_T_4 | _io_out_ctrl_src1Type_T_5 | _io_out_ctrl_src1Type_T_6; // @[Mux.scala 27:72]
  assign io_out_ctrl_src2Type = _io_out_ctrl_src1Type_T | _io_out_ctrl_src1Type_T_4 | _io_out_ctrl_src1Type_T_5 |
    _io_out_ctrl_src1Type_T_6; // @[Mux.scala 27:72]
  assign io_out_ctrl_funcType = _T_1 ? 3'h0 : _T_193; // @[Lookup.scala 33:37]
  assign io_out_ctrl_funcOpType = _T_1 ? 7'h40 : _T_241; // @[Lookup.scala 33:37]
  assign io_out_ctrl_rfrd = instrType[2] ? rdAddr : 5'h0; // @[IDU.scala 22:32]
  assign io_out_data_imm = _imm_T_22 | _imm_T_19; // @[Mux.scala 27:72]
endmodule
module IDUtoEXU(
  input  [63:0] io_in_cf_pc,
  input         io_in_ctrl_src1Type,
  input         io_in_ctrl_src2Type,
  input  [2:0]  io_in_ctrl_funcType,
  input  [6:0]  io_in_ctrl_funcOpType,
  input  [4:0]  io_in_ctrl_rfrd,
  input  [63:0] io_in_data_imm,
  output [63:0] io_out_cf_pc,
  output [2:0]  io_out_ctrl_funcType,
  output [6:0]  io_out_ctrl_funcOpType,
  output [4:0]  io_out_ctrl_rfrd,
  output [63:0] io_out_data_src1,
  output [63:0] io_out_data_src2,
  output [63:0] io_out_data_imm
);
  assign io_out_cf_pc = io_in_cf_pc; // @[IDUtoEXU.scala 25:17]
  assign io_out_ctrl_funcType = io_in_ctrl_funcType; // @[IDUtoEXU.scala 26:17]
  assign io_out_ctrl_funcOpType = io_in_ctrl_funcOpType; // @[IDUtoEXU.scala 26:17]
  assign io_out_ctrl_rfrd = io_in_ctrl_rfrd; // @[IDUtoEXU.scala 26:17]
  assign io_out_data_src1 = io_in_ctrl_src1Type ? io_in_cf_pc : 64'h0; // @[Mux.scala 27:72]
  assign io_out_data_src2 = io_in_ctrl_src2Type ? io_in_data_imm : 64'h0; // @[Mux.scala 27:72]
  assign io_out_data_imm = io_in_data_imm; // @[IDUtoEXU.scala 24:21]
endmodule
module ALU(
  input  [6:0]  io_in_ctrl_funcOpType,
  input  [63:0] io_in_data_src1,
  input  [63:0] io_in_data_src2,
  output [63:0] io_out_aluRes
);
  wire [5:0] shamt = io_in_ctrl_funcOpType[5] ? {{1'd0}, io_in_data_src2[4:0]} : io_in_data_src2[5:0]; // @[ALU.scala 39:18]
  wire [63:0] _T_1 = io_in_data_src1 + io_in_data_src2; // @[ALU.scala 41:28]
  wire [126:0] _GEN_0 = {{63'd0}, io_in_data_src1}; // @[ALU.scala 42:28]
  wire [126:0] _T_2 = _GEN_0 << shamt; // @[ALU.scala 42:28]
  wire  lo = $signed(io_in_data_src1) < $signed(io_in_data_src2); // @[ALU.scala 43:60]
  wire [63:0] _T_5 = {63'h0,lo}; // @[Cat.scala 30:58]
  wire  _T_6 = io_in_data_src1 < io_in_data_src2; // @[ALU.scala 44:28]
  wire [63:0] _T_7 = io_in_data_src1 ^ io_in_data_src2; // @[ALU.scala 45:28]
  wire [63:0] _T_8 = io_in_data_src1 >> shamt; // @[ALU.scala 46:28]
  wire [63:0] _T_9 = io_in_data_src1 | io_in_data_src2; // @[ALU.scala 47:28]
  wire [63:0] _T_10 = io_in_data_src1 & io_in_data_src2; // @[ALU.scala 48:28]
  wire [63:0] _T_12 = io_in_data_src1 - io_in_data_src2; // @[ALU.scala 49:28]
  wire [63:0] _T_15 = $signed(io_in_data_src1) >>> shamt; // @[ALU.scala 50:46]
  wire [31:0] _T_24 = io_in_data_src1[31:0] >> shamt; // @[ALU.scala 54:34]
  wire [31:0] _T_26 = io_in_data_src1[31:0]; // @[ALU.scala 55:41]
  wire [31:0] _T_28 = $signed(_T_26) >>> shamt; // @[ALU.scala 55:60]
  wire [63:0] _res_T_1 = 7'h40 == io_in_ctrl_funcOpType ? _T_1 : 64'h0; // @[Mux.scala 80:57]
  wire [126:0] _res_T_3 = 7'h1 == io_in_ctrl_funcOpType ? _T_2 : {{63'd0}, _res_T_1}; // @[Mux.scala 80:57]
  wire [126:0] _res_T_5 = 7'h2 == io_in_ctrl_funcOpType ? {{63'd0}, _T_5} : _res_T_3; // @[Mux.scala 80:57]
  wire [126:0] _res_T_7 = 7'h3 == io_in_ctrl_funcOpType ? {{126'd0}, _T_6} : _res_T_5; // @[Mux.scala 80:57]
  wire [126:0] _res_T_9 = 7'h4 == io_in_ctrl_funcOpType ? {{63'd0}, _T_7} : _res_T_7; // @[Mux.scala 80:57]
  wire [126:0] _res_T_11 = 7'h5 == io_in_ctrl_funcOpType ? {{63'd0}, _T_8} : _res_T_9; // @[Mux.scala 80:57]
  wire [126:0] _res_T_13 = 7'h6 == io_in_ctrl_funcOpType ? {{63'd0}, _T_9} : _res_T_11; // @[Mux.scala 80:57]
  wire [126:0] _res_T_15 = 7'h7 == io_in_ctrl_funcOpType ? {{63'd0}, _T_10} : _res_T_13; // @[Mux.scala 80:57]
  wire [126:0] _res_T_17 = 7'h8 == io_in_ctrl_funcOpType ? {{63'd0}, _T_12} : _res_T_15; // @[Mux.scala 80:57]
  wire [126:0] _res_T_19 = 7'hd == io_in_ctrl_funcOpType ? {{63'd0}, _T_15} : _res_T_17; // @[Mux.scala 80:57]
  wire [126:0] _res_T_21 = 7'h60 == io_in_ctrl_funcOpType ? {{63'd0}, _T_1} : _res_T_19; // @[Mux.scala 80:57]
  wire [126:0] _res_T_23 = 7'h28 == io_in_ctrl_funcOpType ? {{63'd0}, _T_12} : _res_T_21; // @[Mux.scala 80:57]
  wire [126:0] _res_T_25 = 7'h21 == io_in_ctrl_funcOpType ? {{63'd0}, _T_5} : _res_T_23; // @[Mux.scala 80:57]
  wire [126:0] _res_T_27 = 7'h25 == io_in_ctrl_funcOpType ? {{95'd0}, _T_24} : _res_T_25; // @[Mux.scala 80:57]
  wire [126:0] _res_T_29 = 7'h2d == io_in_ctrl_funcOpType ? {{95'd0}, _T_28} : _res_T_27; // @[Mux.scala 80:57]
  wire [126:0] _res_T_31 = 7'hf == io_in_ctrl_funcOpType ? {{63'd0}, io_in_data_src2} : _res_T_29; // @[Mux.scala 80:57]
  wire [63:0] res = _res_T_31[63:0]; // @[ALU.scala 59:24 ALU.scala 60:7]
  wire [31:0] io_out_aluRes_lo = res[31:0]; // @[ALU.scala 83:78]
  wire  io_out_aluRes_signBit = io_out_aluRes_lo[31]; // @[utils.scala 14:20]
  wire [31:0] io_out_aluRes_hi = io_out_aluRes_signBit ? 32'hffffffff : 32'h0; // @[Bitwise.scala 72:12]
  wire [63:0] _io_out_aluRes_T_2 = {io_out_aluRes_hi,io_out_aluRes_lo}; // @[Cat.scala 30:58]
  assign io_out_aluRes = io_in_ctrl_funcOpType[5] ? _io_out_aluRes_T_2 : res; // @[ALU.scala 83:23]
endmodule
module LSU(
  input         clock,
  input         io_valid,
  input  [2:0]  io_in_ctrl_funcType,
  input  [6:0]  io_in_ctrl_funcOpType,
  input  [63:0] io_in_data_src1,
  input  [63:0] io_in_data_src2,
  input  [63:0] io_in_data_imm,
  output [63:0] io_out_rdata
);
  wire  ram_clk; // @[LSU.scala 61:19]
  wire  ram_en; // @[LSU.scala 61:19]
  wire [63:0] ram_rIdx; // @[LSU.scala 61:19]
  wire [63:0] ram_rdata; // @[LSU.scala 61:19]
  wire [63:0] ram_wIdx; // @[LSU.scala 61:19]
  wire [63:0] ram_wdata; // @[LSU.scala 61:19]
  wire [63:0] ram_wmask; // @[LSU.scala 61:19]
  wire  ram_wen; // @[LSU.scala 61:19]
  wire [63:0] addr = io_in_data_src1 + io_in_data_imm; // @[LSU.scala 57:30]
  wire  isStore = io_in_ctrl_funcOpType[3]; // @[LSU.scala 21:39]
  wire [63:0] _idx_T_1 = addr - 64'h80000000; // @[LSU.scala 66:19]
  wire [60:0] idx = _idx_T_1[63:3]; // @[LSU.scala 66:33]
  wire [63:0] _rdataSel_T_1 = ram_rdata; // @[LSU.scala 71:22]
  wire  _rdataSel_T_9 = 3'h0 == addr[2:0]; // @[utils.scala 8:34]
  wire  _rdataSel_T_10 = 3'h1 == addr[2:0]; // @[utils.scala 8:34]
  wire  _rdataSel_T_11 = 3'h2 == addr[2:0]; // @[utils.scala 8:34]
  wire  _rdataSel_T_12 = 3'h3 == addr[2:0]; // @[utils.scala 8:34]
  wire  _rdataSel_T_13 = 3'h4 == addr[2:0]; // @[utils.scala 8:34]
  wire  _rdataSel_T_14 = 3'h5 == addr[2:0]; // @[utils.scala 8:34]
  wire  _rdataSel_T_15 = 3'h6 == addr[2:0]; // @[utils.scala 8:34]
  wire  _rdataSel_T_16 = 3'h7 == addr[2:0]; // @[utils.scala 8:34]
  wire [63:0] _rdataSel_T_17 = _rdataSel_T_9 ? _rdataSel_T_1 : 64'h0; // @[Mux.scala 27:72]
  wire [55:0] _rdataSel_T_18 = _rdataSel_T_10 ? ram_rdata[63:8] : 56'h0; // @[Mux.scala 27:72]
  wire [47:0] _rdataSel_T_19 = _rdataSel_T_11 ? ram_rdata[63:16] : 48'h0; // @[Mux.scala 27:72]
  wire [39:0] _rdataSel_T_20 = _rdataSel_T_12 ? ram_rdata[63:24] : 40'h0; // @[Mux.scala 27:72]
  wire [31:0] _rdataSel_T_21 = _rdataSel_T_13 ? ram_rdata[63:32] : 32'h0; // @[Mux.scala 27:72]
  wire [23:0] _rdataSel_T_22 = _rdataSel_T_14 ? ram_rdata[63:40] : 24'h0; // @[Mux.scala 27:72]
  wire [15:0] _rdataSel_T_23 = _rdataSel_T_15 ? ram_rdata[63:48] : 16'h0; // @[Mux.scala 27:72]
  wire [7:0] _rdataSel_T_24 = _rdataSel_T_16 ? ram_rdata[63:56] : 8'h0; // @[Mux.scala 27:72]
  wire [63:0] _GEN_0 = {{8'd0}, _rdataSel_T_18}; // @[Mux.scala 27:72]
  wire [63:0] _rdataSel_T_25 = _rdataSel_T_17 | _GEN_0; // @[Mux.scala 27:72]
  wire [63:0] _GEN_1 = {{16'd0}, _rdataSel_T_19}; // @[Mux.scala 27:72]
  wire [63:0] _rdataSel_T_26 = _rdataSel_T_25 | _GEN_1; // @[Mux.scala 27:72]
  wire [63:0] _GEN_2 = {{24'd0}, _rdataSel_T_20}; // @[Mux.scala 27:72]
  wire [63:0] _rdataSel_T_27 = _rdataSel_T_26 | _GEN_2; // @[Mux.scala 27:72]
  wire [63:0] _GEN_3 = {{32'd0}, _rdataSel_T_21}; // @[Mux.scala 27:72]
  wire [63:0] _rdataSel_T_28 = _rdataSel_T_27 | _GEN_3; // @[Mux.scala 27:72]
  wire [63:0] _GEN_4 = {{40'd0}, _rdataSel_T_22}; // @[Mux.scala 27:72]
  wire [63:0] _rdataSel_T_29 = _rdataSel_T_28 | _GEN_4; // @[Mux.scala 27:72]
  wire [63:0] _GEN_5 = {{48'd0}, _rdataSel_T_23}; // @[Mux.scala 27:72]
  wire [63:0] _rdataSel_T_30 = _rdataSel_T_29 | _GEN_5; // @[Mux.scala 27:72]
  wire [63:0] _GEN_6 = {{56'd0}, _rdataSel_T_24}; // @[Mux.scala 27:72]
  wire [63:0] rdataSel = _rdataSel_T_30 | _GEN_6; // @[Mux.scala 27:72]
  wire [7:0] io_out_rdata_lo = rdataSel[7:0]; // @[LSU.scala 82:39]
  wire  io_out_rdata_signBit = io_out_rdata_lo[7]; // @[utils.scala 14:20]
  wire [55:0] io_out_rdata_hi = io_out_rdata_signBit ? 56'hffffffffffffff : 56'h0; // @[Bitwise.scala 72:12]
  wire [63:0] _io_out_rdata_T_1 = {io_out_rdata_hi,io_out_rdata_lo}; // @[Cat.scala 30:58]
  wire [15:0] io_out_rdata_lo_1 = rdataSel[15:0]; // @[LSU.scala 83:39]
  wire  io_out_rdata_signBit_1 = io_out_rdata_lo_1[15]; // @[utils.scala 14:20]
  wire [47:0] io_out_rdata_hi_1 = io_out_rdata_signBit_1 ? 48'hffffffffffff : 48'h0; // @[Bitwise.scala 72:12]
  wire [63:0] _io_out_rdata_T_3 = {io_out_rdata_hi_1,io_out_rdata_lo_1}; // @[Cat.scala 30:58]
  wire [31:0] io_out_rdata_lo_2 = rdataSel[31:0]; // @[LSU.scala 84:39]
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
  wire [1:0] size = io_in_ctrl_funcOpType[1:0]; // @[LSU.scala 95:35]
  wire [7:0] ram_io_wdata_hi = io_in_data_src2[7:0]; // @[LSU.scala 48:30]
  wire [63:0] _ram_io_wdata_T = {ram_io_wdata_hi,ram_io_wdata_hi,ram_io_wdata_hi,ram_io_wdata_hi,ram_io_wdata_hi,
    ram_io_wdata_hi,ram_io_wdata_hi,ram_io_wdata_hi}; // @[Cat.scala 30:58]
  wire [15:0] ram_io_wdata_hi_3 = io_in_data_src2[15:0]; // @[LSU.scala 49:30]
  wire [63:0] _ram_io_wdata_T_1 = {ram_io_wdata_hi_3,ram_io_wdata_hi_3,ram_io_wdata_hi_3,ram_io_wdata_hi_3}; // @[Cat.scala 30:58]
  wire [31:0] ram_io_wdata_hi_5 = io_in_data_src2[31:0]; // @[LSU.scala 50:30]
  wire [63:0] _ram_io_wdata_T_2 = {ram_io_wdata_hi_5,ram_io_wdata_hi_5}; // @[Cat.scala 30:58]
  wire  _ram_io_wdata_T_3 = 2'h0 == size; // @[utils.scala 8:34]
  wire  _ram_io_wdata_T_4 = 2'h1 == size; // @[utils.scala 8:34]
  wire  _ram_io_wdata_T_5 = 2'h2 == size; // @[utils.scala 8:34]
  wire  _ram_io_wdata_T_6 = 2'h3 == size; // @[utils.scala 8:34]
  wire [63:0] _ram_io_wdata_T_7 = _ram_io_wdata_T_3 ? _ram_io_wdata_T : 64'h0; // @[Mux.scala 27:72]
  wire [63:0] _ram_io_wdata_T_8 = _ram_io_wdata_T_4 ? _ram_io_wdata_T_1 : 64'h0; // @[Mux.scala 27:72]
  wire [63:0] _ram_io_wdata_T_9 = _ram_io_wdata_T_5 ? _ram_io_wdata_T_2 : 64'h0; // @[Mux.scala 27:72]
  wire [63:0] _ram_io_wdata_T_10 = _ram_io_wdata_T_6 ? io_in_data_src2 : 64'h0; // @[Mux.scala 27:72]
  wire [63:0] _ram_io_wdata_T_11 = _ram_io_wdata_T_7 | _ram_io_wdata_T_8; // @[Mux.scala 27:72]
  wire [63:0] _ram_io_wdata_T_12 = _ram_io_wdata_T_11 | _ram_io_wdata_T_9; // @[Mux.scala 27:72]
  wire [1:0] _ram_io_wmask_T_5 = _ram_io_wdata_T_4 ? 2'h3 : 2'h0; // @[Mux.scala 27:72]
  wire [3:0] _ram_io_wmask_T_6 = _ram_io_wdata_T_5 ? 4'hf : 4'h0; // @[Mux.scala 27:72]
  wire [7:0] _ram_io_wmask_T_7 = _ram_io_wdata_T_6 ? 8'hff : 8'h0; // @[Mux.scala 27:72]
  wire [1:0] _GEN_7 = {{1'd0}, _ram_io_wdata_T_3}; // @[Mux.scala 27:72]
  wire [1:0] _ram_io_wmask_T_8 = _GEN_7 | _ram_io_wmask_T_5; // @[Mux.scala 27:72]
  wire [3:0] _GEN_8 = {{2'd0}, _ram_io_wmask_T_8}; // @[Mux.scala 27:72]
  wire [3:0] _ram_io_wmask_T_9 = _GEN_8 | _ram_io_wmask_T_6; // @[Mux.scala 27:72]
  wire [7:0] _GEN_9 = {{4'd0}, _ram_io_wmask_T_9}; // @[Mux.scala 27:72]
  wire [7:0] _ram_io_wmask_T_10 = _GEN_9 | _ram_io_wmask_T_7; // @[Mux.scala 27:72]
  wire [14:0] _GEN_10 = {{7'd0}, _ram_io_wmask_T_10}; // @[LSU.scala 43:8]
  wire [14:0] _ram_io_wmask_T_12 = _GEN_10 << addr[2:0]; // @[LSU.scala 43:8]
  RAMHelper ram ( // @[LSU.scala 61:19]
    .clk(ram_clk),
    .en(ram_en),
    .rIdx(ram_rIdx),
    .rdata(ram_rdata),
    .wIdx(ram_wIdx),
    .wdata(ram_wdata),
    .wmask(ram_wmask),
    .wen(ram_wen)
  );
  assign io_out_rdata = _io_out_rdata_T_29 | _io_out_rdata_T_24; // @[Mux.scala 27:72]
  assign ram_clk = clock; // @[LSU.scala 62:14]
  assign ram_en = io_valid; // @[LSU.scala 63:13]
  assign ram_rIdx = {{3'd0}, idx}; // @[LSU.scala 66:33]
  assign ram_wIdx = {{3'd0}, idx}; // @[LSU.scala 66:33]
  assign ram_wdata = _ram_io_wdata_T_12 | _ram_io_wdata_T_10; // @[Mux.scala 27:72]
  assign ram_wmask = {{49'd0}, _ram_io_wmask_T_12}; // @[LSU.scala 43:8]
  assign ram_wen = io_in_ctrl_funcType == 3'h1 & isStore; // @[LSU.scala 93:57]
endmodule
module BRU(
  input  [63:0] io_in_cf_pc,
  input  [2:0]  io_in_ctrl_funcType,
  input  [6:0]  io_in_ctrl_funcOpType,
  input  [63:0] io_in_data_src1,
  input  [63:0] io_in_data_src2,
  input  [63:0] io_in_data_imm,
  output [63:0] io_out_newPC,
  output        io_out_valid
);
  wire  _io_out_valid_T_1 = io_in_data_src1 == io_in_data_src2; // @[BRU.scala 32:31]
  wire  _io_out_valid_T_2 = io_in_data_src1 != io_in_data_src2; // @[BRU.scala 33:31]
  wire  _io_out_valid_T_5 = $signed(io_in_data_src1) < $signed(io_in_data_src2); // @[BRU.scala 34:40]
  wire  _io_out_valid_T_8 = $signed(io_in_data_src1) >= $signed(io_in_data_src2); // @[BRU.scala 35:40]
  wire  _io_out_valid_T_9 = io_in_data_src1 < io_in_data_src2; // @[BRU.scala 36:31]
  wire  _io_out_valid_T_10 = io_in_data_src1 >= io_in_data_src2; // @[BRU.scala 37:31]
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
  wire [63:0] _io_out_newPC_T_2 = io_in_data_src1 + io_in_data_imm; // @[BRU.scala 41:21]
  wire [63:0] _io_out_newPC_T_4 = io_in_cf_pc + io_in_data_imm; // @[BRU.scala 41:51]
  assign io_out_newPC = io_in_ctrl_funcOpType == 7'h5a ? _io_out_newPC_T_2 : _io_out_newPC_T_4; // @[BRU.scala 40:22]
  assign io_out_valid = io_in_ctrl_funcType == 3'h5 & _io_out_valid_T_33; // @[BRU.scala 29:58]
endmodule
module EXU(
  input         clock,
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
  output [63:0] io_branch_newPC,
  output        io_branch_valid
);
  wire [6:0] alu_io_in_ctrl_funcOpType; // @[EXU.scala 13:21]
  wire [63:0] alu_io_in_data_src1; // @[EXU.scala 13:21]
  wire [63:0] alu_io_in_data_src2; // @[EXU.scala 13:21]
  wire [63:0] alu_io_out_aluRes; // @[EXU.scala 13:21]
  wire  lsu_clock; // @[EXU.scala 14:21]
  wire  lsu_io_valid; // @[EXU.scala 14:21]
  wire [2:0] lsu_io_in_ctrl_funcType; // @[EXU.scala 14:21]
  wire [6:0] lsu_io_in_ctrl_funcOpType; // @[EXU.scala 14:21]
  wire [63:0] lsu_io_in_data_src1; // @[EXU.scala 14:21]
  wire [63:0] lsu_io_in_data_src2; // @[EXU.scala 14:21]
  wire [63:0] lsu_io_in_data_imm; // @[EXU.scala 14:21]
  wire [63:0] lsu_io_out_rdata; // @[EXU.scala 14:21]
  wire [63:0] bru_io_in_cf_pc; // @[EXU.scala 15:21]
  wire [2:0] bru_io_in_ctrl_funcType; // @[EXU.scala 15:21]
  wire [6:0] bru_io_in_ctrl_funcOpType; // @[EXU.scala 15:21]
  wire [63:0] bru_io_in_data_src1; // @[EXU.scala 15:21]
  wire [63:0] bru_io_in_data_src2; // @[EXU.scala 15:21]
  wire [63:0] bru_io_in_data_imm; // @[EXU.scala 15:21]
  wire [63:0] bru_io_out_newPC; // @[EXU.scala 15:21]
  wire  bru_io_out_valid; // @[EXU.scala 15:21]
  wire  alu_ena = io_in_ctrl_funcType == 3'h0; // @[EXU.scala 20:36]
  wire  lsu_ena = io_in_ctrl_funcType == 3'h1; // @[EXU.scala 21:36]
  wire  _wb_ena_T_1 = ~io_in_ctrl_funcOpType[3]; // @[LSU.scala 22:34]
  wire  _GEN_0 = lsu_ena ? _wb_ena_T_1 : io_in_ctrl_funcOpType[6]; // @[EXU.scala 32:25 EXU.scala 33:16 EXU.scala 35:16]
  wire [63:0] _io_reg_write_back_data_T_1 = io_in_cf_pc + 64'h4; // @[EXU.scala 47:47]
  wire [63:0] _GEN_2 = lsu_ena & _wb_ena_T_1 ? lsu_io_out_rdata : _io_reg_write_back_data_T_1; // @[EXU.scala 42:68 EXU.scala 43:32 EXU.scala 47:32]
  ALU alu ( // @[EXU.scala 13:21]
    .io_in_ctrl_funcOpType(alu_io_in_ctrl_funcOpType),
    .io_in_data_src1(alu_io_in_data_src1),
    .io_in_data_src2(alu_io_in_data_src2),
    .io_out_aluRes(alu_io_out_aluRes)
  );
  LSU lsu ( // @[EXU.scala 14:21]
    .clock(lsu_clock),
    .io_valid(lsu_io_valid),
    .io_in_ctrl_funcType(lsu_io_in_ctrl_funcType),
    .io_in_ctrl_funcOpType(lsu_io_in_ctrl_funcOpType),
    .io_in_data_src1(lsu_io_in_data_src1),
    .io_in_data_src2(lsu_io_in_data_src2),
    .io_in_data_imm(lsu_io_in_data_imm),
    .io_out_rdata(lsu_io_out_rdata)
  );
  BRU bru ( // @[EXU.scala 15:21]
    .io_in_cf_pc(bru_io_in_cf_pc),
    .io_in_ctrl_funcType(bru_io_in_ctrl_funcType),
    .io_in_ctrl_funcOpType(bru_io_in_ctrl_funcOpType),
    .io_in_data_src1(bru_io_in_data_src1),
    .io_in_data_src2(bru_io_in_data_src2),
    .io_in_data_imm(bru_io_in_data_imm),
    .io_out_newPC(bru_io_out_newPC),
    .io_out_valid(bru_io_out_valid)
  );
  assign io_reg_write_back_addr = io_in_ctrl_rfrd; // @[EXU.scala 38:19 EXU.scala 41:32]
  assign io_reg_write_back_data = alu_ena ? alu_io_out_aluRes : _GEN_2; // @[EXU.scala 38:19 EXU.scala 39:32]
  assign io_reg_write_back_ena = alu_ena | _GEN_0; // @[EXU.scala 30:19 EXU.scala 31:16]
  assign io_branch_newPC = bru_io_out_newPC; // @[EXU.scala 53:21]
  assign io_branch_valid = bru_io_out_valid; // @[EXU.scala 52:21]
  assign alu_io_in_ctrl_funcOpType = io_in_ctrl_funcOpType; // @[EXU.scala 25:15]
  assign alu_io_in_data_src1 = io_in_data_src1; // @[EXU.scala 25:15]
  assign alu_io_in_data_src2 = io_in_data_src2; // @[EXU.scala 25:15]
  assign lsu_clock = clock;
  assign lsu_io_valid = io_in_ctrl_funcType == 3'h1; // @[EXU.scala 21:36]
  assign lsu_io_in_ctrl_funcType = io_in_ctrl_funcType; // @[EXU.scala 26:15]
  assign lsu_io_in_ctrl_funcOpType = io_in_ctrl_funcOpType; // @[EXU.scala 26:15]
  assign lsu_io_in_data_src1 = io_in_data_src1; // @[EXU.scala 26:15]
  assign lsu_io_in_data_src2 = io_in_data_src2; // @[EXU.scala 26:15]
  assign lsu_io_in_data_imm = io_in_data_imm; // @[EXU.scala 26:15]
  assign bru_io_in_cf_pc = io_in_cf_pc; // @[EXU.scala 27:15]
  assign bru_io_in_ctrl_funcType = io_in_ctrl_funcType; // @[EXU.scala 27:15]
  assign bru_io_in_ctrl_funcOpType = io_in_ctrl_funcOpType; // @[EXU.scala 27:15]
  assign bru_io_in_data_src1 = io_in_data_src1; // @[EXU.scala 27:15]
  assign bru_io_in_data_src2 = io_in_data_src2; // @[EXU.scala 27:15]
  assign bru_io_in_data_imm = io_in_data_imm; // @[EXU.scala 27:15]
endmodule
module WBU(
  input  [4:0]  io_in_addr,
  input  [63:0] io_in_data,
  input         io_in_ena,
  output [4:0]  io_out_addr,
  output [63:0] io_out_data,
  output        io_out_ena
);
  assign io_out_addr = io_in_addr; // @[WBU.scala 12:12]
  assign io_out_data = io_in_data; // @[WBU.scala 12:12]
  assign io_out_ena = io_in_ena; // @[WBU.scala 12:12]
endmodule
module Regfile(
  input         clock,
  input         reset,
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
  wire  mod_clock; // @[regfile.scala 51:19]
  wire [7:0] mod_coreid; // @[regfile.scala 51:19]
  wire [63:0] mod_gpr_0; // @[regfile.scala 51:19]
  wire [63:0] mod_gpr_1; // @[regfile.scala 51:19]
  wire [63:0] mod_gpr_2; // @[regfile.scala 51:19]
  wire [63:0] mod_gpr_3; // @[regfile.scala 51:19]
  wire [63:0] mod_gpr_4; // @[regfile.scala 51:19]
  wire [63:0] mod_gpr_5; // @[regfile.scala 51:19]
  wire [63:0] mod_gpr_6; // @[regfile.scala 51:19]
  wire [63:0] mod_gpr_7; // @[regfile.scala 51:19]
  wire [63:0] mod_gpr_8; // @[regfile.scala 51:19]
  wire [63:0] mod_gpr_9; // @[regfile.scala 51:19]
  wire [63:0] mod_gpr_10; // @[regfile.scala 51:19]
  wire [63:0] mod_gpr_11; // @[regfile.scala 51:19]
  wire [63:0] mod_gpr_12; // @[regfile.scala 51:19]
  wire [63:0] mod_gpr_13; // @[regfile.scala 51:19]
  wire [63:0] mod_gpr_14; // @[regfile.scala 51:19]
  wire [63:0] mod_gpr_15; // @[regfile.scala 51:19]
  wire [63:0] mod_gpr_16; // @[regfile.scala 51:19]
  wire [63:0] mod_gpr_17; // @[regfile.scala 51:19]
  wire [63:0] mod_gpr_18; // @[regfile.scala 51:19]
  wire [63:0] mod_gpr_19; // @[regfile.scala 51:19]
  wire [63:0] mod_gpr_20; // @[regfile.scala 51:19]
  wire [63:0] mod_gpr_21; // @[regfile.scala 51:19]
  wire [63:0] mod_gpr_22; // @[regfile.scala 51:19]
  wire [63:0] mod_gpr_23; // @[regfile.scala 51:19]
  wire [63:0] mod_gpr_24; // @[regfile.scala 51:19]
  wire [63:0] mod_gpr_25; // @[regfile.scala 51:19]
  wire [63:0] mod_gpr_26; // @[regfile.scala 51:19]
  wire [63:0] mod_gpr_27; // @[regfile.scala 51:19]
  wire [63:0] mod_gpr_28; // @[regfile.scala 51:19]
  wire [63:0] mod_gpr_29; // @[regfile.scala 51:19]
  wire [63:0] mod_gpr_30; // @[regfile.scala 51:19]
  wire [63:0] mod_gpr_31; // @[regfile.scala 51:19]
  reg [63:0] regs_0; // @[regfile.scala 5:21]
  reg [63:0] regs_1; // @[regfile.scala 5:21]
  reg [63:0] regs_2; // @[regfile.scala 5:21]
  reg [63:0] regs_3; // @[regfile.scala 5:21]
  reg [63:0] regs_4; // @[regfile.scala 5:21]
  reg [63:0] regs_5; // @[regfile.scala 5:21]
  reg [63:0] regs_6; // @[regfile.scala 5:21]
  reg [63:0] regs_7; // @[regfile.scala 5:21]
  reg [63:0] regs_8; // @[regfile.scala 5:21]
  reg [63:0] regs_9; // @[regfile.scala 5:21]
  reg [63:0] regs_10; // @[regfile.scala 5:21]
  reg [63:0] regs_11; // @[regfile.scala 5:21]
  reg [63:0] regs_12; // @[regfile.scala 5:21]
  reg [63:0] regs_13; // @[regfile.scala 5:21]
  reg [63:0] regs_14; // @[regfile.scala 5:21]
  reg [63:0] regs_15; // @[regfile.scala 5:21]
  reg [63:0] regs_16; // @[regfile.scala 5:21]
  reg [63:0] regs_17; // @[regfile.scala 5:21]
  reg [63:0] regs_18; // @[regfile.scala 5:21]
  reg [63:0] regs_19; // @[regfile.scala 5:21]
  reg [63:0] regs_20; // @[regfile.scala 5:21]
  reg [63:0] regs_21; // @[regfile.scala 5:21]
  reg [63:0] regs_22; // @[regfile.scala 5:21]
  reg [63:0] regs_23; // @[regfile.scala 5:21]
  reg [63:0] regs_24; // @[regfile.scala 5:21]
  reg [63:0] regs_25; // @[regfile.scala 5:21]
  reg [63:0] regs_26; // @[regfile.scala 5:21]
  reg [63:0] regs_27; // @[regfile.scala 5:21]
  reg [63:0] regs_28; // @[regfile.scala 5:21]
  reg [63:0] regs_29; // @[regfile.scala 5:21]
  reg [63:0] regs_30; // @[regfile.scala 5:21]
  reg [63:0] regs_31; // @[regfile.scala 5:21]
  DifftestArchIntRegState mod ( // @[regfile.scala 51:19]
    .clock(mod_clock),
    .coreid(mod_coreid),
    .gpr_0(mod_gpr_0),
    .gpr_1(mod_gpr_1),
    .gpr_2(mod_gpr_2),
    .gpr_3(mod_gpr_3),
    .gpr_4(mod_gpr_4),
    .gpr_5(mod_gpr_5),
    .gpr_6(mod_gpr_6),
    .gpr_7(mod_gpr_7),
    .gpr_8(mod_gpr_8),
    .gpr_9(mod_gpr_9),
    .gpr_10(mod_gpr_10),
    .gpr_11(mod_gpr_11),
    .gpr_12(mod_gpr_12),
    .gpr_13(mod_gpr_13),
    .gpr_14(mod_gpr_14),
    .gpr_15(mod_gpr_15),
    .gpr_16(mod_gpr_16),
    .gpr_17(mod_gpr_17),
    .gpr_18(mod_gpr_18),
    .gpr_19(mod_gpr_19),
    .gpr_20(mod_gpr_20),
    .gpr_21(mod_gpr_21),
    .gpr_22(mod_gpr_22),
    .gpr_23(mod_gpr_23),
    .gpr_24(mod_gpr_24),
    .gpr_25(mod_gpr_25),
    .gpr_26(mod_gpr_26),
    .gpr_27(mod_gpr_27),
    .gpr_28(mod_gpr_28),
    .gpr_29(mod_gpr_29),
    .gpr_30(mod_gpr_30),
    .gpr_31(mod_gpr_31)
  );
  assign mod_clock = clock; // @[regfile.scala 52:16]
  assign mod_coreid = 8'h0; // @[regfile.scala 53:17]
  assign mod_gpr_0 = regs_0; // @[regfile.scala 54:14]
  assign mod_gpr_1 = regs_1; // @[regfile.scala 54:14]
  assign mod_gpr_2 = regs_2; // @[regfile.scala 54:14]
  assign mod_gpr_3 = regs_3; // @[regfile.scala 54:14]
  assign mod_gpr_4 = regs_4; // @[regfile.scala 54:14]
  assign mod_gpr_5 = regs_5; // @[regfile.scala 54:14]
  assign mod_gpr_6 = regs_6; // @[regfile.scala 54:14]
  assign mod_gpr_7 = regs_7; // @[regfile.scala 54:14]
  assign mod_gpr_8 = regs_8; // @[regfile.scala 54:14]
  assign mod_gpr_9 = regs_9; // @[regfile.scala 54:14]
  assign mod_gpr_10 = regs_10; // @[regfile.scala 54:14]
  assign mod_gpr_11 = regs_11; // @[regfile.scala 54:14]
  assign mod_gpr_12 = regs_12; // @[regfile.scala 54:14]
  assign mod_gpr_13 = regs_13; // @[regfile.scala 54:14]
  assign mod_gpr_14 = regs_14; // @[regfile.scala 54:14]
  assign mod_gpr_15 = regs_15; // @[regfile.scala 54:14]
  assign mod_gpr_16 = regs_16; // @[regfile.scala 54:14]
  assign mod_gpr_17 = regs_17; // @[regfile.scala 54:14]
  assign mod_gpr_18 = regs_18; // @[regfile.scala 54:14]
  assign mod_gpr_19 = regs_19; // @[regfile.scala 54:14]
  assign mod_gpr_20 = regs_20; // @[regfile.scala 54:14]
  assign mod_gpr_21 = regs_21; // @[regfile.scala 54:14]
  assign mod_gpr_22 = regs_22; // @[regfile.scala 54:14]
  assign mod_gpr_23 = regs_23; // @[regfile.scala 54:14]
  assign mod_gpr_24 = regs_24; // @[regfile.scala 54:14]
  assign mod_gpr_25 = regs_25; // @[regfile.scala 54:14]
  assign mod_gpr_26 = regs_26; // @[regfile.scala 54:14]
  assign mod_gpr_27 = regs_27; // @[regfile.scala 54:14]
  assign mod_gpr_28 = regs_28; // @[regfile.scala 54:14]
  assign mod_gpr_29 = regs_29; // @[regfile.scala 54:14]
  assign mod_gpr_30 = regs_30; // @[regfile.scala 54:14]
  assign mod_gpr_31 = regs_31; // @[regfile.scala 54:14]
  always @(posedge clock) begin
    if (reset) begin // @[regfile.scala 5:21]
      regs_0 <= 64'h0; // @[regfile.scala 5:21]
    end else if (io_rd_ena) begin // @[regfile.scala 47:19]
      if (io_rd_addr != 5'h0) begin // @[regfile.scala 8:24]
        if (5'h0 == io_rd_addr) begin // @[regfile.scala 9:18]
          regs_0 <= io_rd_data; // @[regfile.scala 9:18]
        end
      end
    end
    if (reset) begin // @[regfile.scala 5:21]
      regs_1 <= 64'h0; // @[regfile.scala 5:21]
    end else if (io_rd_ena) begin // @[regfile.scala 47:19]
      if (io_rd_addr != 5'h0) begin // @[regfile.scala 8:24]
        if (5'h1 == io_rd_addr) begin // @[regfile.scala 9:18]
          regs_1 <= io_rd_data; // @[regfile.scala 9:18]
        end
      end
    end
    if (reset) begin // @[regfile.scala 5:21]
      regs_2 <= 64'h0; // @[regfile.scala 5:21]
    end else if (io_rd_ena) begin // @[regfile.scala 47:19]
      if (io_rd_addr != 5'h0) begin // @[regfile.scala 8:24]
        if (5'h2 == io_rd_addr) begin // @[regfile.scala 9:18]
          regs_2 <= io_rd_data; // @[regfile.scala 9:18]
        end
      end
    end
    if (reset) begin // @[regfile.scala 5:21]
      regs_3 <= 64'h0; // @[regfile.scala 5:21]
    end else if (io_rd_ena) begin // @[regfile.scala 47:19]
      if (io_rd_addr != 5'h0) begin // @[regfile.scala 8:24]
        if (5'h3 == io_rd_addr) begin // @[regfile.scala 9:18]
          regs_3 <= io_rd_data; // @[regfile.scala 9:18]
        end
      end
    end
    if (reset) begin // @[regfile.scala 5:21]
      regs_4 <= 64'h0; // @[regfile.scala 5:21]
    end else if (io_rd_ena) begin // @[regfile.scala 47:19]
      if (io_rd_addr != 5'h0) begin // @[regfile.scala 8:24]
        if (5'h4 == io_rd_addr) begin // @[regfile.scala 9:18]
          regs_4 <= io_rd_data; // @[regfile.scala 9:18]
        end
      end
    end
    if (reset) begin // @[regfile.scala 5:21]
      regs_5 <= 64'h0; // @[regfile.scala 5:21]
    end else if (io_rd_ena) begin // @[regfile.scala 47:19]
      if (io_rd_addr != 5'h0) begin // @[regfile.scala 8:24]
        if (5'h5 == io_rd_addr) begin // @[regfile.scala 9:18]
          regs_5 <= io_rd_data; // @[regfile.scala 9:18]
        end
      end
    end
    if (reset) begin // @[regfile.scala 5:21]
      regs_6 <= 64'h0; // @[regfile.scala 5:21]
    end else if (io_rd_ena) begin // @[regfile.scala 47:19]
      if (io_rd_addr != 5'h0) begin // @[regfile.scala 8:24]
        if (5'h6 == io_rd_addr) begin // @[regfile.scala 9:18]
          regs_6 <= io_rd_data; // @[regfile.scala 9:18]
        end
      end
    end
    if (reset) begin // @[regfile.scala 5:21]
      regs_7 <= 64'h0; // @[regfile.scala 5:21]
    end else if (io_rd_ena) begin // @[regfile.scala 47:19]
      if (io_rd_addr != 5'h0) begin // @[regfile.scala 8:24]
        if (5'h7 == io_rd_addr) begin // @[regfile.scala 9:18]
          regs_7 <= io_rd_data; // @[regfile.scala 9:18]
        end
      end
    end
    if (reset) begin // @[regfile.scala 5:21]
      regs_8 <= 64'h0; // @[regfile.scala 5:21]
    end else if (io_rd_ena) begin // @[regfile.scala 47:19]
      if (io_rd_addr != 5'h0) begin // @[regfile.scala 8:24]
        if (5'h8 == io_rd_addr) begin // @[regfile.scala 9:18]
          regs_8 <= io_rd_data; // @[regfile.scala 9:18]
        end
      end
    end
    if (reset) begin // @[regfile.scala 5:21]
      regs_9 <= 64'h0; // @[regfile.scala 5:21]
    end else if (io_rd_ena) begin // @[regfile.scala 47:19]
      if (io_rd_addr != 5'h0) begin // @[regfile.scala 8:24]
        if (5'h9 == io_rd_addr) begin // @[regfile.scala 9:18]
          regs_9 <= io_rd_data; // @[regfile.scala 9:18]
        end
      end
    end
    if (reset) begin // @[regfile.scala 5:21]
      regs_10 <= 64'h0; // @[regfile.scala 5:21]
    end else if (io_rd_ena) begin // @[regfile.scala 47:19]
      if (io_rd_addr != 5'h0) begin // @[regfile.scala 8:24]
        if (5'ha == io_rd_addr) begin // @[regfile.scala 9:18]
          regs_10 <= io_rd_data; // @[regfile.scala 9:18]
        end
      end
    end
    if (reset) begin // @[regfile.scala 5:21]
      regs_11 <= 64'h0; // @[regfile.scala 5:21]
    end else if (io_rd_ena) begin // @[regfile.scala 47:19]
      if (io_rd_addr != 5'h0) begin // @[regfile.scala 8:24]
        if (5'hb == io_rd_addr) begin // @[regfile.scala 9:18]
          regs_11 <= io_rd_data; // @[regfile.scala 9:18]
        end
      end
    end
    if (reset) begin // @[regfile.scala 5:21]
      regs_12 <= 64'h0; // @[regfile.scala 5:21]
    end else if (io_rd_ena) begin // @[regfile.scala 47:19]
      if (io_rd_addr != 5'h0) begin // @[regfile.scala 8:24]
        if (5'hc == io_rd_addr) begin // @[regfile.scala 9:18]
          regs_12 <= io_rd_data; // @[regfile.scala 9:18]
        end
      end
    end
    if (reset) begin // @[regfile.scala 5:21]
      regs_13 <= 64'h0; // @[regfile.scala 5:21]
    end else if (io_rd_ena) begin // @[regfile.scala 47:19]
      if (io_rd_addr != 5'h0) begin // @[regfile.scala 8:24]
        if (5'hd == io_rd_addr) begin // @[regfile.scala 9:18]
          regs_13 <= io_rd_data; // @[regfile.scala 9:18]
        end
      end
    end
    if (reset) begin // @[regfile.scala 5:21]
      regs_14 <= 64'h0; // @[regfile.scala 5:21]
    end else if (io_rd_ena) begin // @[regfile.scala 47:19]
      if (io_rd_addr != 5'h0) begin // @[regfile.scala 8:24]
        if (5'he == io_rd_addr) begin // @[regfile.scala 9:18]
          regs_14 <= io_rd_data; // @[regfile.scala 9:18]
        end
      end
    end
    if (reset) begin // @[regfile.scala 5:21]
      regs_15 <= 64'h0; // @[regfile.scala 5:21]
    end else if (io_rd_ena) begin // @[regfile.scala 47:19]
      if (io_rd_addr != 5'h0) begin // @[regfile.scala 8:24]
        if (5'hf == io_rd_addr) begin // @[regfile.scala 9:18]
          regs_15 <= io_rd_data; // @[regfile.scala 9:18]
        end
      end
    end
    if (reset) begin // @[regfile.scala 5:21]
      regs_16 <= 64'h0; // @[regfile.scala 5:21]
    end else if (io_rd_ena) begin // @[regfile.scala 47:19]
      if (io_rd_addr != 5'h0) begin // @[regfile.scala 8:24]
        if (5'h10 == io_rd_addr) begin // @[regfile.scala 9:18]
          regs_16 <= io_rd_data; // @[regfile.scala 9:18]
        end
      end
    end
    if (reset) begin // @[regfile.scala 5:21]
      regs_17 <= 64'h0; // @[regfile.scala 5:21]
    end else if (io_rd_ena) begin // @[regfile.scala 47:19]
      if (io_rd_addr != 5'h0) begin // @[regfile.scala 8:24]
        if (5'h11 == io_rd_addr) begin // @[regfile.scala 9:18]
          regs_17 <= io_rd_data; // @[regfile.scala 9:18]
        end
      end
    end
    if (reset) begin // @[regfile.scala 5:21]
      regs_18 <= 64'h0; // @[regfile.scala 5:21]
    end else if (io_rd_ena) begin // @[regfile.scala 47:19]
      if (io_rd_addr != 5'h0) begin // @[regfile.scala 8:24]
        if (5'h12 == io_rd_addr) begin // @[regfile.scala 9:18]
          regs_18 <= io_rd_data; // @[regfile.scala 9:18]
        end
      end
    end
    if (reset) begin // @[regfile.scala 5:21]
      regs_19 <= 64'h0; // @[regfile.scala 5:21]
    end else if (io_rd_ena) begin // @[regfile.scala 47:19]
      if (io_rd_addr != 5'h0) begin // @[regfile.scala 8:24]
        if (5'h13 == io_rd_addr) begin // @[regfile.scala 9:18]
          regs_19 <= io_rd_data; // @[regfile.scala 9:18]
        end
      end
    end
    if (reset) begin // @[regfile.scala 5:21]
      regs_20 <= 64'h0; // @[regfile.scala 5:21]
    end else if (io_rd_ena) begin // @[regfile.scala 47:19]
      if (io_rd_addr != 5'h0) begin // @[regfile.scala 8:24]
        if (5'h14 == io_rd_addr) begin // @[regfile.scala 9:18]
          regs_20 <= io_rd_data; // @[regfile.scala 9:18]
        end
      end
    end
    if (reset) begin // @[regfile.scala 5:21]
      regs_21 <= 64'h0; // @[regfile.scala 5:21]
    end else if (io_rd_ena) begin // @[regfile.scala 47:19]
      if (io_rd_addr != 5'h0) begin // @[regfile.scala 8:24]
        if (5'h15 == io_rd_addr) begin // @[regfile.scala 9:18]
          regs_21 <= io_rd_data; // @[regfile.scala 9:18]
        end
      end
    end
    if (reset) begin // @[regfile.scala 5:21]
      regs_22 <= 64'h0; // @[regfile.scala 5:21]
    end else if (io_rd_ena) begin // @[regfile.scala 47:19]
      if (io_rd_addr != 5'h0) begin // @[regfile.scala 8:24]
        if (5'h16 == io_rd_addr) begin // @[regfile.scala 9:18]
          regs_22 <= io_rd_data; // @[regfile.scala 9:18]
        end
      end
    end
    if (reset) begin // @[regfile.scala 5:21]
      regs_23 <= 64'h0; // @[regfile.scala 5:21]
    end else if (io_rd_ena) begin // @[regfile.scala 47:19]
      if (io_rd_addr != 5'h0) begin // @[regfile.scala 8:24]
        if (5'h17 == io_rd_addr) begin // @[regfile.scala 9:18]
          regs_23 <= io_rd_data; // @[regfile.scala 9:18]
        end
      end
    end
    if (reset) begin // @[regfile.scala 5:21]
      regs_24 <= 64'h0; // @[regfile.scala 5:21]
    end else if (io_rd_ena) begin // @[regfile.scala 47:19]
      if (io_rd_addr != 5'h0) begin // @[regfile.scala 8:24]
        if (5'h18 == io_rd_addr) begin // @[regfile.scala 9:18]
          regs_24 <= io_rd_data; // @[regfile.scala 9:18]
        end
      end
    end
    if (reset) begin // @[regfile.scala 5:21]
      regs_25 <= 64'h0; // @[regfile.scala 5:21]
    end else if (io_rd_ena) begin // @[regfile.scala 47:19]
      if (io_rd_addr != 5'h0) begin // @[regfile.scala 8:24]
        if (5'h19 == io_rd_addr) begin // @[regfile.scala 9:18]
          regs_25 <= io_rd_data; // @[regfile.scala 9:18]
        end
      end
    end
    if (reset) begin // @[regfile.scala 5:21]
      regs_26 <= 64'h0; // @[regfile.scala 5:21]
    end else if (io_rd_ena) begin // @[regfile.scala 47:19]
      if (io_rd_addr != 5'h0) begin // @[regfile.scala 8:24]
        if (5'h1a == io_rd_addr) begin // @[regfile.scala 9:18]
          regs_26 <= io_rd_data; // @[regfile.scala 9:18]
        end
      end
    end
    if (reset) begin // @[regfile.scala 5:21]
      regs_27 <= 64'h0; // @[regfile.scala 5:21]
    end else if (io_rd_ena) begin // @[regfile.scala 47:19]
      if (io_rd_addr != 5'h0) begin // @[regfile.scala 8:24]
        if (5'h1b == io_rd_addr) begin // @[regfile.scala 9:18]
          regs_27 <= io_rd_data; // @[regfile.scala 9:18]
        end
      end
    end
    if (reset) begin // @[regfile.scala 5:21]
      regs_28 <= 64'h0; // @[regfile.scala 5:21]
    end else if (io_rd_ena) begin // @[regfile.scala 47:19]
      if (io_rd_addr != 5'h0) begin // @[regfile.scala 8:24]
        if (5'h1c == io_rd_addr) begin // @[regfile.scala 9:18]
          regs_28 <= io_rd_data; // @[regfile.scala 9:18]
        end
      end
    end
    if (reset) begin // @[regfile.scala 5:21]
      regs_29 <= 64'h0; // @[regfile.scala 5:21]
    end else if (io_rd_ena) begin // @[regfile.scala 47:19]
      if (io_rd_addr != 5'h0) begin // @[regfile.scala 8:24]
        if (5'h1d == io_rd_addr) begin // @[regfile.scala 9:18]
          regs_29 <= io_rd_data; // @[regfile.scala 9:18]
        end
      end
    end
    if (reset) begin // @[regfile.scala 5:21]
      regs_30 <= 64'h0; // @[regfile.scala 5:21]
    end else if (io_rd_ena) begin // @[regfile.scala 47:19]
      if (io_rd_addr != 5'h0) begin // @[regfile.scala 8:24]
        if (5'h1e == io_rd_addr) begin // @[regfile.scala 9:18]
          regs_30 <= io_rd_data; // @[regfile.scala 9:18]
        end
      end
    end
    if (reset) begin // @[regfile.scala 5:21]
      regs_31 <= 64'h0; // @[regfile.scala 5:21]
    end else if (io_rd_ena) begin // @[regfile.scala 47:19]
      if (io_rd_addr != 5'h0) begin // @[regfile.scala 8:24]
        if (5'h1f == io_rd_addr) begin // @[regfile.scala 9:18]
          regs_31 <= io_rd_data; // @[regfile.scala 9:18]
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
  output [63:0] io_out_pc,
  output [31:0] io_out_instr,
  output        io_valid,
  output [4:0]  io_diffreg_addr,
  output [63:0] io_diffreg_data,
  output        io_diffreg_ena
);
  wire  ifu_clock; // @[Top.scala 16:21]
  wire  ifu_reset; // @[Top.scala 16:21]
  wire [63:0] ifu_io_in_newPC; // @[Top.scala 16:21]
  wire  ifu_io_in_valid; // @[Top.scala 16:21]
  wire [63:0] ifu_io_out_pc; // @[Top.scala 16:21]
  wire [31:0] ifu_io_out_instr; // @[Top.scala 16:21]
  wire [63:0] idu_io_in_pc; // @[Top.scala 17:21]
  wire [31:0] idu_io_in_instr; // @[Top.scala 17:21]
  wire [63:0] idu_io_out_cf_pc; // @[Top.scala 17:21]
  wire  idu_io_out_ctrl_src1Type; // @[Top.scala 17:21]
  wire  idu_io_out_ctrl_src2Type; // @[Top.scala 17:21]
  wire [2:0] idu_io_out_ctrl_funcType; // @[Top.scala 17:21]
  wire [6:0] idu_io_out_ctrl_funcOpType; // @[Top.scala 17:21]
  wire [4:0] idu_io_out_ctrl_rfrd; // @[Top.scala 17:21]
  wire [63:0] idu_io_out_data_imm; // @[Top.scala 17:21]
  wire [63:0] dis_io_in_cf_pc; // @[Top.scala 18:21]
  wire  dis_io_in_ctrl_src1Type; // @[Top.scala 18:21]
  wire  dis_io_in_ctrl_src2Type; // @[Top.scala 18:21]
  wire [2:0] dis_io_in_ctrl_funcType; // @[Top.scala 18:21]
  wire [6:0] dis_io_in_ctrl_funcOpType; // @[Top.scala 18:21]
  wire [4:0] dis_io_in_ctrl_rfrd; // @[Top.scala 18:21]
  wire [63:0] dis_io_in_data_imm; // @[Top.scala 18:21]
  wire [63:0] dis_io_out_cf_pc; // @[Top.scala 18:21]
  wire [2:0] dis_io_out_ctrl_funcType; // @[Top.scala 18:21]
  wire [6:0] dis_io_out_ctrl_funcOpType; // @[Top.scala 18:21]
  wire [4:0] dis_io_out_ctrl_rfrd; // @[Top.scala 18:21]
  wire [63:0] dis_io_out_data_src1; // @[Top.scala 18:21]
  wire [63:0] dis_io_out_data_src2; // @[Top.scala 18:21]
  wire [63:0] dis_io_out_data_imm; // @[Top.scala 18:21]
  wire  exu_clock; // @[Top.scala 19:21]
  wire [63:0] exu_io_in_cf_pc; // @[Top.scala 19:21]
  wire [2:0] exu_io_in_ctrl_funcType; // @[Top.scala 19:21]
  wire [6:0] exu_io_in_ctrl_funcOpType; // @[Top.scala 19:21]
  wire [4:0] exu_io_in_ctrl_rfrd; // @[Top.scala 19:21]
  wire [63:0] exu_io_in_data_src1; // @[Top.scala 19:21]
  wire [63:0] exu_io_in_data_src2; // @[Top.scala 19:21]
  wire [63:0] exu_io_in_data_imm; // @[Top.scala 19:21]
  wire [4:0] exu_io_reg_write_back_addr; // @[Top.scala 19:21]
  wire [63:0] exu_io_reg_write_back_data; // @[Top.scala 19:21]
  wire  exu_io_reg_write_back_ena; // @[Top.scala 19:21]
  wire [63:0] exu_io_branch_newPC; // @[Top.scala 19:21]
  wire  exu_io_branch_valid; // @[Top.scala 19:21]
  wire [4:0] wbu_io_in_addr; // @[Top.scala 20:21]
  wire [63:0] wbu_io_in_data; // @[Top.scala 20:21]
  wire  wbu_io_in_ena; // @[Top.scala 20:21]
  wire [4:0] wbu_io_out_addr; // @[Top.scala 20:21]
  wire [63:0] wbu_io_out_data; // @[Top.scala 20:21]
  wire  wbu_io_out_ena; // @[Top.scala 20:21]
  wire  reg__clock; // @[Top.scala 21:21]
  wire  reg__reset; // @[Top.scala 21:21]
  wire [4:0] reg__io_rd_addr; // @[Top.scala 21:21]
  wire [63:0] reg__io_rd_data; // @[Top.scala 21:21]
  wire  reg__io_rd_ena; // @[Top.scala 21:21]
  IFU ifu ( // @[Top.scala 16:21]
    .clock(ifu_clock),
    .reset(ifu_reset),
    .io_in_newPC(ifu_io_in_newPC),
    .io_in_valid(ifu_io_in_valid),
    .io_out_pc(ifu_io_out_pc),
    .io_out_instr(ifu_io_out_instr)
  );
  IDU idu ( // @[Top.scala 17:21]
    .io_in_pc(idu_io_in_pc),
    .io_in_instr(idu_io_in_instr),
    .io_out_cf_pc(idu_io_out_cf_pc),
    .io_out_ctrl_src1Type(idu_io_out_ctrl_src1Type),
    .io_out_ctrl_src2Type(idu_io_out_ctrl_src2Type),
    .io_out_ctrl_funcType(idu_io_out_ctrl_funcType),
    .io_out_ctrl_funcOpType(idu_io_out_ctrl_funcOpType),
    .io_out_ctrl_rfrd(idu_io_out_ctrl_rfrd),
    .io_out_data_imm(idu_io_out_data_imm)
  );
  IDUtoEXU dis ( // @[Top.scala 18:21]
    .io_in_cf_pc(dis_io_in_cf_pc),
    .io_in_ctrl_src1Type(dis_io_in_ctrl_src1Type),
    .io_in_ctrl_src2Type(dis_io_in_ctrl_src2Type),
    .io_in_ctrl_funcType(dis_io_in_ctrl_funcType),
    .io_in_ctrl_funcOpType(dis_io_in_ctrl_funcOpType),
    .io_in_ctrl_rfrd(dis_io_in_ctrl_rfrd),
    .io_in_data_imm(dis_io_in_data_imm),
    .io_out_cf_pc(dis_io_out_cf_pc),
    .io_out_ctrl_funcType(dis_io_out_ctrl_funcType),
    .io_out_ctrl_funcOpType(dis_io_out_ctrl_funcOpType),
    .io_out_ctrl_rfrd(dis_io_out_ctrl_rfrd),
    .io_out_data_src1(dis_io_out_data_src1),
    .io_out_data_src2(dis_io_out_data_src2),
    .io_out_data_imm(dis_io_out_data_imm)
  );
  EXU exu ( // @[Top.scala 19:21]
    .clock(exu_clock),
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
    .io_branch_newPC(exu_io_branch_newPC),
    .io_branch_valid(exu_io_branch_valid)
  );
  WBU wbu ( // @[Top.scala 20:21]
    .io_in_addr(wbu_io_in_addr),
    .io_in_data(wbu_io_in_data),
    .io_in_ena(wbu_io_in_ena),
    .io_out_addr(wbu_io_out_addr),
    .io_out_data(wbu_io_out_data),
    .io_out_ena(wbu_io_out_ena)
  );
  Regfile reg_ ( // @[Top.scala 21:21]
    .clock(reg__clock),
    .reset(reg__reset),
    .io_rd_addr(reg__io_rd_addr),
    .io_rd_data(reg__io_rd_data),
    .io_rd_ena(reg__io_rd_ena)
  );
  assign io_out_pc = ifu_io_out_pc; // @[Top.scala 32:18]
  assign io_out_instr = ifu_io_out_instr; // @[Top.scala 33:18]
  assign io_valid = ~reset; // @[Top.scala 35:5]
  assign io_diffreg_addr = wbu_io_out_addr; // @[Top.scala 30:29]
  assign io_diffreg_data = wbu_io_out_data; // @[Top.scala 30:29]
  assign io_diffreg_ena = wbu_io_out_ena; // @[Top.scala 30:29]
  assign ifu_clock = clock;
  assign ifu_reset = reset;
  assign ifu_io_in_newPC = exu_io_branch_newPC; // @[Top.scala 27:29]
  assign ifu_io_in_valid = exu_io_branch_valid; // @[Top.scala 27:29]
  assign idu_io_in_pc = ifu_io_out_pc; // @[Top.scala 23:29]
  assign idu_io_in_instr = ifu_io_out_instr; // @[Top.scala 23:29]
  assign dis_io_in_cf_pc = idu_io_out_cf_pc; // @[Top.scala 24:29]
  assign dis_io_in_ctrl_src1Type = idu_io_out_ctrl_src1Type; // @[Top.scala 24:29]
  assign dis_io_in_ctrl_src2Type = idu_io_out_ctrl_src2Type; // @[Top.scala 24:29]
  assign dis_io_in_ctrl_funcType = idu_io_out_ctrl_funcType; // @[Top.scala 24:29]
  assign dis_io_in_ctrl_funcOpType = idu_io_out_ctrl_funcOpType; // @[Top.scala 24:29]
  assign dis_io_in_ctrl_rfrd = idu_io_out_ctrl_rfrd; // @[Top.scala 24:29]
  assign dis_io_in_data_imm = idu_io_out_data_imm; // @[Top.scala 24:29]
  assign exu_clock = clock;
  assign exu_io_in_cf_pc = dis_io_out_cf_pc; // @[Top.scala 25:29]
  assign exu_io_in_ctrl_funcType = dis_io_out_ctrl_funcType; // @[Top.scala 25:29]
  assign exu_io_in_ctrl_funcOpType = dis_io_out_ctrl_funcOpType; // @[Top.scala 25:29]
  assign exu_io_in_ctrl_rfrd = dis_io_out_ctrl_rfrd; // @[Top.scala 25:29]
  assign exu_io_in_data_src1 = dis_io_out_data_src1; // @[Top.scala 25:29]
  assign exu_io_in_data_src2 = dis_io_out_data_src2; // @[Top.scala 25:29]
  assign exu_io_in_data_imm = dis_io_out_data_imm; // @[Top.scala 25:29]
  assign wbu_io_in_addr = exu_io_reg_write_back_addr; // @[Top.scala 26:29]
  assign wbu_io_in_data = exu_io_reg_write_back_data; // @[Top.scala 26:29]
  assign wbu_io_in_ena = exu_io_reg_write_back_ena; // @[Top.scala 26:29]
  assign reg__clock = clock;
  assign reg__reset = reset;
  assign reg__io_rd_addr = wbu_io_out_addr; // @[Top.scala 28:29]
  assign reg__io_rd_data = wbu_io_out_data; // @[Top.scala 28:29]
  assign reg__io_rd_ena = wbu_io_out_ena; // @[Top.scala 28:29]
endmodule
module SimTop(
  input         clock,
  input         reset,
  input  [63:0] io_logCtrl_log_begin,
  input  [63:0] io_logCtrl_log_end,
  input  [63:0] io_logCtrl_log_level,
  input         io_perfInfo_clean,
  input         io_perfInfo_dump,
  output        io_uart_out_valid,
  output [7:0]  io_uart_out_ch,
  output        io_uart_in_valid,
  input  [7:0]  io_uart_in_ch
);
`ifdef RANDOMIZE_REG_INIT
  reg [31:0] _RAND_0;
  reg [31:0] _RAND_1;
  reg [63:0] _RAND_2;
  reg [63:0] _RAND_3;
  reg [31:0] _RAND_4;
  reg [31:0] _RAND_5;
  reg [31:0] _RAND_6;
  reg [31:0] _RAND_7;
  reg [63:0] _RAND_8;
  reg [63:0] _RAND_9;
  reg [31:0] _RAND_10;
  reg [31:0] _RAND_11;
`endif // RANDOMIZE_REG_INIT
  wire  rvcore_clock; // @[SimTop.scala 20:22]
  wire  rvcore_reset; // @[SimTop.scala 20:22]
  wire [63:0] rvcore_io_out_pc; // @[SimTop.scala 20:22]
  wire [31:0] rvcore_io_out_instr; // @[SimTop.scala 20:22]
  wire  rvcore_io_valid; // @[SimTop.scala 20:22]
  wire [4:0] rvcore_io_diffreg_addr; // @[SimTop.scala 20:22]
  wire [63:0] rvcore_io_diffreg_data; // @[SimTop.scala 20:22]
  wire  rvcore_io_diffreg_ena; // @[SimTop.scala 20:22]
  wire  instrCommit_clock; // @[SimTop.scala 23:27]
  wire [7:0] instrCommit_coreid; // @[SimTop.scala 23:27]
  wire [7:0] instrCommit_index; // @[SimTop.scala 23:27]
  wire  instrCommit_valid; // @[SimTop.scala 23:27]
  wire [63:0] instrCommit_pc; // @[SimTop.scala 23:27]
  wire [31:0] instrCommit_instr; // @[SimTop.scala 23:27]
  wire  instrCommit_skip; // @[SimTop.scala 23:27]
  wire  instrCommit_isRVC; // @[SimTop.scala 23:27]
  wire  instrCommit_scFailed; // @[SimTop.scala 23:27]
  wire  instrCommit_wen; // @[SimTop.scala 23:27]
  wire [63:0] instrCommit_wdata; // @[SimTop.scala 23:27]
  wire [7:0] instrCommit_wdest; // @[SimTop.scala 23:27]
  wire  csrCommit_clock; // @[SimTop.scala 41:25]
  wire [7:0] csrCommit_coreid; // @[SimTop.scala 41:25]
  wire [1:0] csrCommit_priviledgeMode; // @[SimTop.scala 41:25]
  wire [63:0] csrCommit_mstatus; // @[SimTop.scala 41:25]
  wire [63:0] csrCommit_sstatus; // @[SimTop.scala 41:25]
  wire [63:0] csrCommit_mepc; // @[SimTop.scala 41:25]
  wire [63:0] csrCommit_sepc; // @[SimTop.scala 41:25]
  wire [63:0] csrCommit_mtval; // @[SimTop.scala 41:25]
  wire [63:0] csrCommit_stval; // @[SimTop.scala 41:25]
  wire [63:0] csrCommit_mtvec; // @[SimTop.scala 41:25]
  wire [63:0] csrCommit_stvec; // @[SimTop.scala 41:25]
  wire [63:0] csrCommit_mcause; // @[SimTop.scala 41:25]
  wire [63:0] csrCommit_scause; // @[SimTop.scala 41:25]
  wire [63:0] csrCommit_satp; // @[SimTop.scala 41:25]
  wire [63:0] csrCommit_mip; // @[SimTop.scala 41:25]
  wire [63:0] csrCommit_mie; // @[SimTop.scala 41:25]
  wire [63:0] csrCommit_mscratch; // @[SimTop.scala 41:25]
  wire [63:0] csrCommit_sscratch; // @[SimTop.scala 41:25]
  wire [63:0] csrCommit_mideleg; // @[SimTop.scala 41:25]
  wire [63:0] csrCommit_medeleg; // @[SimTop.scala 41:25]
  reg  REG; // @[SimTop.scala 31:42]
  reg  REG_1; // @[SimTop.scala 31:34]
  reg [63:0] REG_2; // @[SimTop.scala 32:42]
  reg [63:0] REG_3; // @[SimTop.scala 32:34]
  reg [31:0] REG_4; // @[SimTop.scala 34:42]
  reg [31:0] REG_5; // @[SimTop.scala 34:34]
  reg  REG_6; // @[SimTop.scala 36:42]
  reg  REG_7; // @[SimTop.scala 36:34]
  reg [63:0] REG_8; // @[SimTop.scala 37:42]
  reg [63:0] REG_9; // @[SimTop.scala 37:34]
  reg [4:0] REG_10; // @[SimTop.scala 38:42]
  reg [4:0] REG_11; // @[SimTop.scala 38:34]
  Top rvcore ( // @[SimTop.scala 20:22]
    .clock(rvcore_clock),
    .reset(rvcore_reset),
    .io_out_pc(rvcore_io_out_pc),
    .io_out_instr(rvcore_io_out_instr),
    .io_valid(rvcore_io_valid),
    .io_diffreg_addr(rvcore_io_diffreg_addr),
    .io_diffreg_data(rvcore_io_diffreg_data),
    .io_diffreg_ena(rvcore_io_diffreg_ena)
  );
  DifftestInstrCommit instrCommit ( // @[SimTop.scala 23:27]
    .clock(instrCommit_clock),
    .coreid(instrCommit_coreid),
    .index(instrCommit_index),
    .valid(instrCommit_valid),
    .pc(instrCommit_pc),
    .instr(instrCommit_instr),
    .skip(instrCommit_skip),
    .isRVC(instrCommit_isRVC),
    .scFailed(instrCommit_scFailed),
    .wen(instrCommit_wen),
    .wdata(instrCommit_wdata),
    .wdest(instrCommit_wdest)
  );
  DifftestCSRState csrCommit ( // @[SimTop.scala 41:25]
    .clock(csrCommit_clock),
    .coreid(csrCommit_coreid),
    .priviledgeMode(csrCommit_priviledgeMode),
    .mstatus(csrCommit_mstatus),
    .sstatus(csrCommit_sstatus),
    .mepc(csrCommit_mepc),
    .sepc(csrCommit_sepc),
    .mtval(csrCommit_mtval),
    .stval(csrCommit_stval),
    .mtvec(csrCommit_mtvec),
    .stvec(csrCommit_stvec),
    .mcause(csrCommit_mcause),
    .scause(csrCommit_scause),
    .satp(csrCommit_satp),
    .mip(csrCommit_mip),
    .mie(csrCommit_mie),
    .mscratch(csrCommit_mscratch),
    .sscratch(csrCommit_sscratch),
    .mideleg(csrCommit_mideleg),
    .medeleg(csrCommit_medeleg)
  );
  assign io_uart_out_valid = 1'h0; // @[SimTop.scala 18:21]
  assign io_uart_out_ch = 8'h0; // @[SimTop.scala 19:19]
  assign io_uart_in_valid = 1'h0; // @[SimTop.scala 17:21]
  assign rvcore_clock = clock;
  assign rvcore_reset = reset;
  assign instrCommit_clock = clock; // @[SimTop.scala 24:24]
  assign instrCommit_coreid = 8'h0; // @[SimTop.scala 25:25]
  assign instrCommit_index = 8'h0; // @[SimTop.scala 26:24]
  assign instrCommit_valid = REG_1; // @[SimTop.scala 31:24]
  assign instrCommit_pc = REG_3; // @[SimTop.scala 32:24]
  assign instrCommit_instr = REG_5; // @[SimTop.scala 34:24]
  assign instrCommit_skip = 1'h0; // @[SimTop.scala 27:23]
  assign instrCommit_isRVC = 1'h0; // @[SimTop.scala 28:24]
  assign instrCommit_scFailed = 1'h0; // @[SimTop.scala 29:27]
  assign instrCommit_wen = REG_7; // @[SimTop.scala 36:24]
  assign instrCommit_wdata = REG_9; // @[SimTop.scala 37:24]
  assign instrCommit_wdest = {{3'd0}, REG_11}; // @[SimTop.scala 38:24]
  assign csrCommit_clock = clock; // @[SimTop.scala 42:31]
  assign csrCommit_coreid = 8'h0;
  assign csrCommit_priviledgeMode = 2'h0; // @[SimTop.scala 43:31]
  assign csrCommit_mstatus = 64'h0; // @[SimTop.scala 44:31]
  assign csrCommit_sstatus = 64'h0; // @[SimTop.scala 45:31]
  assign csrCommit_mepc = 64'h0; // @[SimTop.scala 46:31]
  assign csrCommit_sepc = 64'h0; // @[SimTop.scala 47:31]
  assign csrCommit_mtval = 64'h0; // @[SimTop.scala 48:31]
  assign csrCommit_stval = 64'h0; // @[SimTop.scala 49:31]
  assign csrCommit_mtvec = 64'h0; // @[SimTop.scala 50:31]
  assign csrCommit_stvec = 64'h0; // @[SimTop.scala 51:31]
  assign csrCommit_mcause = 64'h0; // @[SimTop.scala 52:31]
  assign csrCommit_scause = 64'h0; // @[SimTop.scala 53:31]
  assign csrCommit_satp = 64'h0; // @[SimTop.scala 54:31]
  assign csrCommit_mip = 64'h0; // @[SimTop.scala 55:31]
  assign csrCommit_mie = 64'h0; // @[SimTop.scala 56:31]
  assign csrCommit_mscratch = 64'h0; // @[SimTop.scala 57:31]
  assign csrCommit_sscratch = 64'h0; // @[SimTop.scala 58:31]
  assign csrCommit_mideleg = 64'h0; // @[SimTop.scala 59:31]
  assign csrCommit_medeleg = 64'h0; // @[SimTop.scala 60:31]
  always @(posedge clock) begin
    REG <= rvcore_io_valid; // @[SimTop.scala 31:42]
    REG_1 <= REG; // @[SimTop.scala 31:34]
    REG_2 <= rvcore_io_out_pc; // @[SimTop.scala 32:42]
    REG_3 <= REG_2; // @[SimTop.scala 32:34]
    REG_4 <= rvcore_io_out_instr; // @[SimTop.scala 34:42]
    REG_5 <= REG_4; // @[SimTop.scala 34:34]
    REG_6 <= rvcore_io_diffreg_ena; // @[SimTop.scala 36:42]
    REG_7 <= REG_6; // @[SimTop.scala 36:34]
    REG_8 <= rvcore_io_diffreg_data; // @[SimTop.scala 37:42]
    REG_9 <= REG_8; // @[SimTop.scala 37:34]
    REG_10 <= rvcore_io_diffreg_addr; // @[SimTop.scala 38:42]
    REG_11 <= REG_10; // @[SimTop.scala 38:34]
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
  REG = _RAND_0[0:0];
  _RAND_1 = {1{`RANDOM}};
  REG_1 = _RAND_1[0:0];
  _RAND_2 = {2{`RANDOM}};
  REG_2 = _RAND_2[63:0];
  _RAND_3 = {2{`RANDOM}};
  REG_3 = _RAND_3[63:0];
  _RAND_4 = {1{`RANDOM}};
  REG_4 = _RAND_4[31:0];
  _RAND_5 = {1{`RANDOM}};
  REG_5 = _RAND_5[31:0];
  _RAND_6 = {1{`RANDOM}};
  REG_6 = _RAND_6[0:0];
  _RAND_7 = {1{`RANDOM}};
  REG_7 = _RAND_7[0:0];
  _RAND_8 = {2{`RANDOM}};
  REG_8 = _RAND_8[63:0];
  _RAND_9 = {2{`RANDOM}};
  REG_9 = _RAND_9[63:0];
  _RAND_10 = {1{`RANDOM}};
  REG_10 = _RAND_10[4:0];
  _RAND_11 = {1{`RANDOM}};
  REG_11 = _RAND_11[4:0];
`endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`ifdef FIRRTL_AFTER_INITIAL
`FIRRTL_AFTER_INITIAL
`endif
`endif // SYNTHESIS
endmodule
