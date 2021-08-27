
//********************************************************************************
//
// Function: System controller
// Comments:
//
//********************************************************************************

module apb_multicon
   (input wire  	       clk,
    input wire  	       rst_n,
    output reg [3:0]       o_gpio,
    output reg 		       o_timer_irq,
    input wire 		       i_ram_init_done,
    input wire 		       i_ram_init_error,
    input wire                 i_psel,
    input wire                 i_penable,
    input wire [11:0] 	       i_paddr,
    input wire                 i_pwrite,
    input wire [31:0] 	       i_pwdata,
    output wire [31:0] 	       o_prdata,
    output wire 	       o_pready);

   wire 	 reg_we;
   wire 	 reg_re;
   wire [11:2] 	 reg_addr;
   wire [31:0] 	 reg_wdata;
   reg [31:0] 	 reg_rdata;

   reg [63:0] 	 mtime;
   reg [63:0] 	 mtimecmp;
`ifdef SIMPRINT
   reg [1023:0]  signature_file;
   integer 	f = 0;
   initial begin
      if ($value$plusargs("signature=%s", signature_file)) begin
	 $display("Writing signature to %0s", signature_file);
	 f = $fopen(signature_file, "w");
      end
   end
`endif

`ifndef VERSION_DIRTY
 `define VERSION_DIRTY 1
`endif
`ifndef VERSION_MAJOR
 `define VERSION_MAJOR 255
`endif
`ifndef VERSION_MINOR
 `define VERSION_MINOR 255
`endif
`ifndef VERSION_REV
 `define VERSION_REV 255
`endif
`ifndef VERSION_SHA
 `define VERSION_SHA deadbeef
`endif

   wire [31:0] version;

   assign version[31]    = `VERSION_DIRTY;
   assign version[30:24] = 7'd0;
   assign version[23:16] = `VERSION_MAJOR;
   assign version[15: 8] = `VERSION_MINOR;
   assign version[ 7: 0] = `VERSION_REV;

   localparam [2:0]
     REG_VERSION  = 3'd0,
     REG_SHA      = 3'd1,
     REG_SIMPRINT = 3'd4,
     REG_SIMEXIT  = 3'd5;
   //0 = ver
   //4 = sha
   //8 = simprint/RAM status
   //C = simexit
   //10 = gpio
   //18 = timer/timecmp

   assign reg_we    = i_psel & i_penable & i_pwrite;
   assign reg_re    = i_psel & ~i_pwrite;
   assign reg_addr  = i_paddr[11:2];
   assign reg_wdata = i_pwdata;
   assign o_prdata  = reg_rdata;
   assign o_pready  = 1'b1;

   always @(posedge clk) begin
      if (reg_we) begin
	case (reg_addr[5:2])
`ifdef SIMPRINT
	  2: begin
		$fwrite(f, "%c", reg_wdata[7:0]);
		$write("%c", reg_wdata[7:0]);
	     end
	  3: begin
		$display("\nFinito");
		$finish;
	     end
`endif
	  4 : o_gpio  <= reg_wdata[3:0]; //changed by wenqiu.zhang 2020.7.28
	  6 : mtimecmp[31:0]  <= reg_wdata[31:0];
	  7 : mtimecmp[63:32] <= reg_wdata[31:0];
	endcase
      end

      if (reg_re) begin
        case (reg_addr[5:2])
	 	 0 : reg_rdata <= {version};
	 	 1 : reg_rdata <= {32'h`VERSION_SHA};
	 	 2 : reg_rdata <= {14'd0, i_ram_init_error, i_ram_init_done, 16'd0};
	 	 4 : reg_rdata <= {28'h0, o_gpio};
	 	 6 : reg_rdata <= mtimecmp[31:0];
	 	 7 : reg_rdata <= mtimecmp[63:32];
	 	 8 : reg_rdata <= mtime[31:0];
	 	 9 : reg_rdata <= mtime[63:32];
	 	 default : reg_rdata <= 0;
       endcase
      end

      mtime <= mtime + 64'd1;
      o_timer_irq <= (mtime >= mtimecmp);
      if (!rst_n) begin
	 mtime <= 64'd0;
	 mtimecmp <= 64'd0;
      end
   end
endmodule


