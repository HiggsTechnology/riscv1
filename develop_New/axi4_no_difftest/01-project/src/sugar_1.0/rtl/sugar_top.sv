

module sugar_top #(
	parameter bootrom_file = "" 
)(
	input          clk,    // Clock
	input          rst_n,  // Asynchronous reset active low


	output         o_uart0_tx,
	input          i_uart0_rx,
	output         o_uart1_tx,
	input          i_uart1_rx,

	input          i_ram_init_done,
	input          i_ram_init_error,

  output         o_gpio


);

   wire          timer_irq;
   wire          uart_irq;
   //MULTIOCON APB signals,like gpios,timer,etc..
   wire          sysc_apb_psel;
   wire          sysc_apb_penable;
   wire          sysc_apb_pwrite;
   wire  [31:0]  sysc_apb_paddr;
   wire  [31:0]  sysc_apb_pwdata;
   wire  [31:0]  sysc_apb_prdata;
   wire          sysc_apb_pready;
   //Flash mem Ctl
   wire          fmc_apb_psel;
   wire          fmc_apb_penable;
   wire          fmc_apb_pwrite;
   wire  [31:0]  fmc_apb_paddr;
   wire  [31:0]  fmc_apb_pwdata;
   wire  [31:0]  fmc_apb_prdata;
   wire          fmc_apb_pready;
   //uart0 APB signals
   wire          uart0_apb_psel;
   wire          uart0_apb_penable;
   wire          uart0_apb_pwrite;
   wire  [31:0]  uart0_apb_paddr;
   wire  [31:0]  uart0_apb_pwdata;
   wire          uart0_apb_wb_ack;
   wire  [7:0]   uart0_apb_wb_rdt;
   //uart1 APB signals
   wire          uart1_apb_psel;
   wire          uart1_apb_penable;
   wire          uart1_apb_pwrite;
   wire  [31:0]  uart1_apb_paddr;
   wire  [31:0]  uart1_apb_pwdata;
   wire  [31:0]  uart1_apb_prdata;
   wire          uart1_apb_pready; 
   wire          uart1_irq;

parameter IDWIDTH  = 7;
parameter ROM_SIZE = 32'h40000;
parameter RAM_SIZE = 32'h40000;


`include "axi_intercon.vh"
  

 Top i_top
 ( .clock(clk)
  ,.reset(~rst_n)
//******************IFU AXI interface,read only******************//
  ,.io_ifuout_aw_ready(1'b0)
  ,.io_ifuout_aw_valid()
  ,.io_ifuout_aw_bits_addr()
  ,.io_ifuout_aw_bits_prot()
  ,.io_ifuout_aw_bits_id()
  ,.io_ifuout_aw_bits_user()
  ,.io_ifuout_aw_bits_len()
  ,.io_ifuout_aw_bits_size()
  ,.io_ifuout_aw_bits_burst()
  ,.io_ifuout_aw_bits_lock()
  ,.io_ifuout_aw_bits_cache()
  ,.io_ifuout_aw_bits_qos()

  ,.io_ifuout_w_ready(1'b0)
  ,.io_ifuout_w_valid()
  ,.io_ifuout_w_bits_data()
  ,.io_ifuout_w_bits_strb()
  ,.io_ifuout_w_bits_last()

  ,.io_ifuout_b_ready()
  ,.io_ifuout_b_valid('b0)
  ,.io_ifuout_b_bits_resp('b0)
  ,.io_ifuout_b_bits_id('b0)
  ,.io_ifuout_b_bits_user('b0)

  ,.io_ifuout_ar_ready(ifu_arready)
  ,.io_ifuout_ar_valid(ifu_arvalid)
  ,.io_ifuout_ar_bits_addr(ifu_araddr)
  ,.io_ifuout_ar_bits_prot(ifu_arprot)
  ,.io_ifuout_ar_bits_id(ifu_arid)
  ,.io_ifuout_ar_bits_user()
  ,.io_ifuout_ar_bits_len(ifu_arlen)
  ,.io_ifuout_ar_bits_size(ifu_arsize)
  ,.io_ifuout_ar_bits_burst(ifu_arburst)
  ,.io_ifuout_ar_bits_lock(ifu_arlock)
  ,.io_ifuout_ar_bits_cache(ifu_arcache)
  ,.io_ifuout_ar_bits_qos(ifu_arqos)
  ,.io_ifuout_ar_bits_region(ifu_arregion)

  ,.io_ifuout_r_ready(ifu_rready)
  ,.io_ifuout_r_valid(ifu_rvalid)
  ,.io_ifuout_r_bits_resp(ifu_rresp)
  ,.io_ifuout_r_bits_data(ifu_rdata)
  ,.io_ifuout_r_bits_last(ifu_rlast)
  ,.io_ifuout_r_bits_id(ifu_rid)
  ,.io_ifuout_r_bits_user()

//******************LSU AXI interface******************//
  ,.io_lsuout_aw_ready(lsu_awready)
  ,.io_lsuout_aw_valid(lsu_awvalid)
  ,.io_lsuout_aw_bits_addr(lsu_awaddr)
  ,.io_lsuout_aw_bits_prot(lsu_awprot)
  ,.io_lsuout_aw_bits_id(lsu_awid)
  ,.io_lsuout_aw_bits_user()
  ,.io_lsuout_aw_bits_len(lsu_awlen)
  ,.io_lsuout_aw_bits_size(lsu_awsize)
  ,.io_lsuout_aw_bits_burst(lsu_awburst)
  ,.io_lsuout_aw_bits_lock(lsu_awlock)
  ,.io_lsuout_aw_bits_cache(lsu_awcache)
  ,.io_lsuout_aw_bits_qos(lsu_awqos)
  ,.io_lsuout_aw_bits_region(lsu_awregion)

  ,.io_lsuout_w_ready(lsu_wready)
  ,.io_lsuout_w_valid(lsu_wvalid)
  ,.io_lsuout_w_bits_data(lsu_wdata)
  ,.io_lsuout_w_bits_strb(lsu_wstrb)
  ,.io_lsuout_w_bits_last(lsu_wlast)

  ,.io_lsuout_b_ready(lsu_bready)
  ,.io_lsuout_b_valid(lsu_bvalid)
  ,.io_lsuout_b_bits_resp(lsu_bresp)
  ,.io_lsuout_b_bits_id(lsu_bid)
  ,.io_lsuout_b_bits_user()

  ,.io_lsuout_ar_ready(lsu_arready)
  ,.io_lsuout_ar_valid(lsu_arvalid)
  ,.io_lsuout_ar_bits_addr(lsu_araddr)
  ,.io_lsuout_ar_bits_prot(lsu_arprot)
  ,.io_lsuout_ar_bits_id(lsu_arid)
  ,.io_lsuout_ar_bits_user()
  ,.io_lsuout_ar_bits_len(lsu_arlen)
  ,.io_lsuout_ar_bits_size(lsu_arsize)
  ,.io_lsuout_ar_bits_burst(lsu_arburst)
  ,.io_lsuout_ar_bits_lock(lsu_arlock)
  ,.io_lsuout_ar_bits_cache(lsu_arcache)
  ,.io_lsuout_ar_bits_qos(lsu_arqos)
  ,.io_lsuout_ar_bits_region(lsu_arregion)

  ,.io_lsuout_r_ready(lsu_rready)
  ,.io_lsuout_r_valid(lsu_rvalid)
  ,.io_lsuout_r_bits_resp(lsu_rresp)
  ,.io_lsuout_r_bits_data(lsu_rdata)
  ,.io_lsuout_r_bits_last(lsu_rlast)
  ,.io_lsuout_r_bits_id(lsu_rid)
  ,.io_lsuout_r_bits_user()
);




 axi2apb_wrapper
     #(.ID_WIDTH  (6))
   u_axi2apb_wrapper
    (  .clk              (clk)
      ,.rst_n            (rst_n)
      ,.i_awid           (uart_awid)
      ,.i_awaddr         (uart_awaddr)
      ,.i_awlen          (uart_awlen)
      ,.i_awsize         (uart_awsize)
      ,.i_awburst        (uart_awburst)   
      ,.i_awvalid        (uart_awvalid)
      ,.o_awready        (uart_awready)

      ,.i_arid           (uart_arid)
      ,.i_araddr         (uart_araddr)
      ,.i_arlen          (uart_arlen)
      ,.i_arsize         (uart_arsize)
      ,.i_arburst        (uart_arburst)
      ,.i_arvalid        (uart_arvalid)
      ,.o_arready        (uart_arready)

      ,.i_wdata          (uart_wdata)
      ,.i_wstrb          (uart_wstrb)
      ,.i_wlast          (uart_wlast)
      ,.i_wvalid         (uart_wvalid)
      ,.o_wready         (uart_wready)

      ,.o_rid            (uart_rid)
      ,.o_rdata          (uart_rdata)
      ,.o_rresp          (uart_rresp)
      ,.o_rlast          (uart_rlast)
      ,.o_rvalid         (uart_rvalid)
      ,.i_rready         (uart_rready)

      ,.o_bid            (uart_bid)
      ,.o_bresp          (uart_bresp)
      ,.o_bvalid         (uart_bvalid)
      ,.i_bready         (uart_bready)

      ,.dla0_apb_psel    ()
      ,.dla0_apb_penable ()
      ,.dla0_apb_pwrite  ()
      ,.dla0_apb_paddr   ()
      ,.dla0_apb_pwdata  ()
      ,.dla0_apb_prdata  ('b0)     //|< i
      ,.dla0_apb_pready  ('b0)     //|< i
      //MULTICON interface
      ,.sysc_apb_psel    ()
      ,.sysc_apb_penable ()
      ,.sysc_apb_pwrite  ()
      ,.sysc_apb_paddr   ()
      ,.sysc_apb_pwdata  ()
      ,.sysc_apb_prdata  ('b0)     //|< i
      ,.sysc_apb_pready  ('b0)     //|< i
      //FLASH MEM Ctl interface
      ,.fmc_apb_psel     ()
      ,.fmc_apb_penable  ()
      ,.fmc_apb_pwrite   ()
      ,.fmc_apb_paddr    ()
      ,.fmc_apb_pwdata   ()
      ,.fmc_apb_prdata   ('b0)     //|< i
      ,.fmc_apb_pready   ('b0)     //|< i

      //UART0 interface
      ,.uart0_apb_psel     (uart0_apb_psel    )
      ,.uart0_apb_penable  (uart0_apb_penable )
      ,.uart0_apb_pwrite   (uart0_apb_pwrite  )
      ,.uart0_apb_paddr    (uart0_apb_paddr   )
      ,.uart0_apb_pwdata   (uart0_apb_pwdata  )
      ,.uart0_apb_wb_ack   (uart0_apb_wb_ack  )
      ,.uart0_apb_wb_rdt   (uart0_apb_wb_rdt  )
      //UART1 interface
      ,.uart1_apb_psel     (uart1_apb_psel    )
      ,.uart1_apb_penable  (uart1_apb_penable )
      ,.uart1_apb_pwrite   (uart1_apb_pwrite  )
      ,.uart1_apb_paddr    (uart1_apb_paddr   )
      ,.uart1_apb_pwdata   (uart1_apb_pwdata  )
      ,.uart1_apb_prdata   (uart1_apb_prdata  )
      ,.uart1_apb_pready   (uart1_apb_pready  )
    );



//rom start address 32'h80000000
axi_mem_wrapper #(
	.ID_WIDTH(7),
	.MEM_SIZE(ROM_SIZE),
	.INIT_FILE(bootrom_file))
bootrom
(	.clk(clk)
	,.rst_n(rst_n)

	,.i_awid(rom_awid)
	,.i_awaddr(rom_awaddr)
	,.i_awlen(rom_awlen)
	,.i_awsize(rom_awsize)
	,.i_awburst(rom_awburst)
	,.i_awvalid(rom_awvalid)
	,.o_awready(rom_awready)

	,.i_arid(rom_arid)
	,.i_araddr(rom_araddr)
	,.i_arlen(rom_arlen)
	,.i_arsize(rom_arsize)
	,.i_arburst(rom_arburst)
	,.i_arvalid(rom_arvalid)
 	,.o_arready(rom_arready)

	,.i_wdata(rom_wdata)
	,.i_wstrb(rom_wstrb)
	,.i_wlast(rom_wlast)
	,.i_wvalid(rom_wvalid)
	,.o_wready(rom_wready)

	,.o_bid(rom_bid)
	,.o_bresp(rom_bresp)
	,.o_bvalid(rom_bvalid)
	,.i_bready(rom_bready)

	,.o_rid(rom_rid)
	,.o_rdata(rom_rdata)
	,.o_rresp(rom_rresp)
	,.o_rlast(rom_rlast)
	,.o_rvalid(rom_rvalid)
	,.i_rready(rom_rready)
);

//ram start address 32'h00000000
axi_mem_wrapper #(
	.ID_WIDTH(7),
	.MEM_SIZE(RAM_SIZE),
	.INIT_FILE(""))
ram
(	.clk(clk)
	,.rst_n(rst_n)

	,.i_awid(ram_awid)
	,.i_awaddr(ram_awaddr)
	,.i_awlen(ram_awlen)
	,.i_awsize(ram_awsize)
	,.i_awburst(ram_awburst)
	,.i_awvalid(ram_awvalid)
	,.o_awready(ram_awready)

	,.i_arid(ram_arid)
	,.i_araddr(ram_araddr)
	,.i_arlen(ram_arlen)
	,.i_arsize(ram_arsize)
	,.i_arburst(ram_arburst)
	,.i_arvalid(ram_arvalid)
 	,.o_arready(ram_arready)

	,.i_wdata(ram_wdata)
	,.i_wstrb(ram_wstrb)
	,.i_wlast(ram_wlast)
	,.i_wvalid(ram_wvalid)
	,.o_wready(ram_wready)

	,.o_bid(ram_bid)
	,.o_bresp(ram_bresp)
	,.o_bvalid(ram_bvalid)
	,.i_bready(ram_bready)

	,.o_rid(ram_rid)
	,.o_rdata(ram_rdata)
	,.o_rresp(ram_rresp)
	,.o_rlast(ram_rlast)
	,.o_rvalid(ram_rvalid)
	,.i_rready(ram_rready)
);


apb_multicon multicon
    ( .clk       (clk)
     ,.rst_n     (rst_n)
     ,.o_gpio    (o_gpio)
     ,.o_timer_irq (timer_irq)
     ,.i_ram_init_done  (i_ram_init_done)
     ,.i_ram_init_error (i_ram_init_error)
     ,.i_psel    (sysc_apb_psel       )   //|< i
     ,.i_penable (sysc_apb_penable    )   //|< i
     ,.i_paddr   (sysc_apb_paddr[11:0])   //|< i
     ,.i_pwrite  (sysc_apb_pwrite     )   //|< i
     ,.i_pwdata  (sysc_apb_pwdata     )   //|< i
     ,.o_prdata  (sysc_apb_prdata     )   //|> o
     ,.o_pready  (sysc_apb_pready     )   //|> o
    );

uart_top uart16550_0
     (
      // Wishbone slave interface
      .wb_clk_i	  (clk)
      ,.wb_rst_i	(~rst_n)
      ,.wb_adr_i	(uart0_apb_paddr[5:3])
      ,.wb_dat_i	(uart0_apb_pwdata[7:0])
      ,.wb_we_i	    (uart0_apb_pwrite)
      ,.wb_cyc_i	(uart0_apb_psel)
      ,.wb_stb_i	(uart0_apb_penable)
      ,.wb_sel_i	(4'b0) // Not used in 8-bit mode
      ,.wb_dat_o	(uart0_apb_wb_rdt)
      ,.wb_ack_o	(uart0_apb_wb_ack)

      // Outputs
      ,.int_o     (uart0_irq)
      ,.stx_pad_o (o_uart0_tx)
      ,.rts_pad_o ()
      ,.dtr_pad_o ()

      // Inputs
      ,.srx_pad_i (i_uart0_rx)
      ,.cts_pad_i (1'b0)
      ,.dsr_pad_i (1'b0)
      ,.ri_pad_i  (1'b0)
      ,.dcd_pad_i (1'b0)
      );



apb_uart_sv 
    #(
      .APB_ADDR_WIDTH(17)
    )
    uart16550_1
    (
       .CLK(clk)
      ,.RSTN(rst_n)
      ,.PADDR({uart1_apb_paddr[19:3]})
      ,.PWDATA(uart1_apb_pwdata)
      ,.PWRITE(uart1_apb_pwrite)
      ,.PSEL(uart1_apb_psel)
      ,.PENABLE(uart1_apb_penable)
      ,.PRDATA(uart1_apb_prdata)
      ,.PREADY(uart1_apb_pready)
      ,.PSLVERR()

      //uart interface
      ,.rx_i(i_uart1_rx)
      ,.tx_o(o_uart1_tx)
      ,.event_o(uart1_irq)
    );





endmodule : sugar_top





