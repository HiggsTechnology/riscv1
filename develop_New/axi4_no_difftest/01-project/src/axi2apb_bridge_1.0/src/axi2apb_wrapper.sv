// SPDX-License-Identifier: Apache-2.0
// Copyright 2019 Western Digital Corporation or its affiliates.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

//********************************************************************************
// $Id$
//
// Function: Wrapper for axi2apb instantiation
// Comments:
//
//********************************************************************************

`default_nettype none

module axi2apb_wrapper
  #(parameter ID_WIDTH = 0,
    parameter INIT_FILE = "")
  (input wire 		      clk,
   input wire 		      rst_n,

   input wire [ID_WIDTH-1:0]  i_awid,
   //input wire [11:0]        i_awaddr,
   input wire [31:0]          i_awaddr,
   input wire [7:0]           i_awlen,
   input wire [2:0]           i_awsize,
   input wire [1:0]           i_awburst,
   input wire                 i_awvalid,
   output wire                o_awready,

   input wire [ID_WIDTH-1:0]  i_arid,
   input wire [31:0]          i_araddr,
   input wire [7:0]           i_arlen,
   input wire [2:0]           i_arsize,
   input wire [1:0]           i_arburst,
   input wire                 i_arvalid,
   output wire                o_arready,

   input wire [63:0]          i_wdata,
   input wire [7:0]           i_wstrb,
   input wire                 i_wlast,
   input wire                 i_wvalid,
   output wire                o_wready,

   output wire [ID_WIDTH-1:0] o_bid,
   output wire [1:0]          o_bresp,
   output wire                o_bvalid,
   input wire                 i_bready,

   output wire                dla0_apb_psel,
   output wire                dla0_apb_penable,
   output wire                dla0_apb_pwrite,
   output wire  [31:0]        dla0_apb_paddr,
   output wire  [31:0]        dla0_apb_pwdata,
   input  wire  [31:0]        dla0_apb_prdata,
   input  wire                dla0_apb_pready,

   output wire                sysc_apb_psel,
   output wire                sysc_apb_penable,
   output wire                sysc_apb_pwrite,
   output wire  [31:0]        sysc_apb_paddr,
   output wire  [31:0]        sysc_apb_pwdata,
   input  wire  [31:0]        sysc_apb_prdata,
   input  wire                sysc_apb_pready,

   output wire                fmc_apb_psel,
   output wire                fmc_apb_penable,
   output wire                fmc_apb_pwrite,
   output wire  [31:0]        fmc_apb_paddr,
   output wire  [31:0]        fmc_apb_pwdata,
   input  wire  [31:0]        fmc_apb_prdata,
   input  wire                fmc_apb_pready,


   output wire                uart0_apb_psel,
   output wire                uart0_apb_penable,
   output wire                uart0_apb_pwrite,
   output wire  [31:0]        uart0_apb_paddr,
   output wire  [31:0]        uart0_apb_pwdata,
   input  wire                uart0_apb_wb_ack,
   input  wire  [7:0]         uart0_apb_wb_rdt,

   output wire                uart1_apb_psel,
   output wire                uart1_apb_penable,
   output wire                uart1_apb_pwrite,
   output wire  [31:0]        uart1_apb_paddr,
   output wire  [31:0]        uart1_apb_pwdata,
   input  wire  [31:0]        uart1_apb_prdata,
   input  wire                uart1_apb_pready,

   output wire [ID_WIDTH-1:0] o_rid,
   output wire [63:0] 	      o_rdata,
   output wire [1:0] 	      o_rresp,
   output wire 		         o_rlast,
   output wire 		         o_rvalid,
   input wire 		            i_rready);


   localparam APB_NUM_SLAVES = 8;

   wire [16:0] 		               paddr;
   wire [31:0] 		               pwdata;
   wire 		                        pwrite;
   wire [APB_NUM_SLAVES-1:0]        psel;
   wire [APB_NUM_SLAVES-1:0][31:0]  prdata;
   wire 		                        penable;
   wire [APB_NUM_SLAVES-1:0]        pready;
   wire [APB_NUM_SLAVES-1:0]        pslverr;


   axi2apb_bridge
     #(
       .AXI4_ADDRESS_WIDTH (20),
       .AXI4_ID_WIDTH    (ID_WIDTH),
       .AXI4_USER_WIDTH  (1),
       .BUFF_DEPTH_SLAVE (2),
       .APB_NUM_SLAVES   (APB_NUM_SLAVES),
       .APB_ADDR_WIDTH   (17)
       )
   u_axi2apb_bridge
     (
      .ACLK       (clk),
      .ARESETn    (rst_n),
      .test_en_i  (1'b0),

      .AWID_i     (i_awid        ),
      .AWADDR_i   (i_awaddr      ),
      .AWLEN_i    (i_awlen       ),
      .AWSIZE_i   (3'b0          ),
      .AWBURST_i  (i_awburst     ),
      .AWLOCK_i   (1'd0          ),
      .AWCACHE_i  (4'd0          ),
      .AWPROT_i   (3'd0          ),
      .AWREGION_i (4'd0          ),
      .AWUSER_i   (1'd0          ),
      .AWQOS_i    (4'd0          ),
      .AWVALID_i  (i_awvalid     ),
      .AWREADY_o  (o_awready     ),

      .WDATA_i    (i_wdata       ),
      .WSTRB_i    (i_wstrb       ),
      .WLAST_i    (i_wlast       ),
      .WUSER_i    (1'd0          ),
      .WVALID_i   (i_wvalid      ),
      .WREADY_o   (o_wready      ),

      .BID_o      (o_bid         ),
      .BRESP_o    (o_bresp       ),
      .BVALID_o   (o_bvalid      ),
      .BUSER_o    (              ),
      .BREADY_i   (i_bready      ),

      .ARID_i     (i_arid        ),
      .ARADDR_i   (i_araddr      ),
      .ARLEN_i    (i_arlen       ),
      .ARSIZE_i   (i_arsize      ),
      .ARBURST_i  (i_arburst     ),
      .ARLOCK_i   (1'd0          ),
      .ARCACHE_i  (4'd0          ),
      .ARPROT_i   (3'd0          ),
      .ARREGION_i (4'd0          ),
      .ARUSER_i   (1'd0          ),
      .ARQOS_i    (4'd0          ),
      .ARVALID_i  (i_arvalid     ),
      .ARREADY_o  (o_arready     ),

      .RID_o      (o_rid         ),
      .RDATA_o    (o_rdata       ),
      .RRESP_o    (o_rresp       ),
      .RLAST_o    (o_rlast       ),
      .RUSER_o    (              ),
      .RVALID_o   (o_rvalid      ),
      .RREADY_i   (i_rready      ),

      .PENABLE    (penable     ),
      .PWRITE     (pwrite      ),
      .PADDR      (paddr       ),
      .PSEL       (psel        ),
      .PWDATA     (pwdata      ),
      .PRDATA     (prdata      ),
      .PREADY     (pready      ),
      .PSLVERR    (pslverr     ));

   
   assign pready[0] = !uart0_apb_penable | uart0_apb_wb_ack;
   assign pready[1] = dla0_apb_pready;
   assign pready[2] = sysc_apb_pready;
   assign pready[3] = fmc_apb_pready;
   assign pready[4] = uart1_apb_pready;
   assign pready[7:5] = 3'b0;

   assign pslverr = 8'b0;

   assign prdata[0][31:0] = {24'h0, uart0_apb_wb_rdt};
   assign prdata[1][31:0] = dla0_apb_prdata;
   assign prdata[2][31:0] = sysc_apb_prdata;
   assign prdata[3][31:0] = fmc_apb_prdata;
   assign prdata[4][31:0] = uart1_apb_prdata;
   assign prdata[5][31:0] = 32'b0;
   assign prdata[6][31:0] = 32'b0;
   assign prdata[7][31:0] = 32'b0;

   
   assign uart0_apb_psel    = psel[0];
   assign uart0_apb_penable = penable;
   assign uart0_apb_pwrite  = pwrite;
   assign uart0_apb_paddr   = {15'b0,paddr};
   assign uart0_apb_pwdata  = pwdata;
  
   assign dla0_apb_psel    = psel[1];
   assign dla0_apb_penable = penable;
   assign dla0_apb_pwrite  = pwrite;
   assign dla0_apb_paddr   = {15'b0,paddr};
   assign dla0_apb_pwdata  = pwdata;

   assign sysc_apb_psel    = psel[2];
   assign sysc_apb_penable = penable;
   assign sysc_apb_pwrite  = pwrite;
   assign sysc_apb_paddr   = {15'b0,paddr};
   assign sysc_apb_pwdata  = pwdata;

   assign fmc_apb_psel    = psel[3];
   assign fmc_apb_penable = penable;
   assign fmc_apb_pwrite  = pwrite;
   assign fmc_apb_paddr   = {15'b0,paddr};
   assign fmc_apb_pwdata  = pwdata;

   assign uart1_apb_psel      = psel[4];
   assign uart1_apb_penable   = penable;
   assign uart1_apb_pwrite    = pwrite;
   assign uart1_apb_paddr     = {15'b0,paddr};
   assign uart1_apb_pwdata    = pwdata;


endmodule

`default_nettype wire

