// THIS FILE IS AUTOGENERATED BY axi_intercon_gen
// ANY MANUAL CHANGES WILL BE LOST
wire  [2:0] ifu_arid;
wire [31:0] ifu_araddr;
wire  [7:0] ifu_arlen;
wire  [2:0] ifu_arsize;
wire  [1:0] ifu_arburst;
wire        ifu_arlock;
wire  [3:0] ifu_arcache;
wire  [2:0] ifu_arprot;
wire  [3:0] ifu_arregion;
wire  [3:0] ifu_arqos;
wire        ifu_arvalid;
wire        ifu_arready;
wire  [2:0] ifu_rid;
wire [63:0] ifu_rdata;
wire  [1:0] ifu_rresp;
wire        ifu_rlast;
wire        ifu_rvalid;
wire        ifu_rready;
wire  [3:0] lsu_awid;
wire [31:0] lsu_awaddr;
wire  [7:0] lsu_awlen;
wire  [2:0] lsu_awsize;
wire  [1:0] lsu_awburst;
wire        lsu_awlock;
wire  [3:0] lsu_awcache;
wire  [2:0] lsu_awprot;
wire  [3:0] lsu_awregion;
wire  [3:0] lsu_awqos;
wire        lsu_awvalid;
wire        lsu_awready;
wire  [3:0] lsu_arid;
wire [31:0] lsu_araddr;
wire  [7:0] lsu_arlen;
wire  [2:0] lsu_arsize;
wire  [1:0] lsu_arburst;
wire        lsu_arlock;
wire  [3:0] lsu_arcache;
wire  [2:0] lsu_arprot;
wire  [3:0] lsu_arregion;
wire  [3:0] lsu_arqos;
wire        lsu_arvalid;
wire        lsu_arready;
wire [63:0] lsu_wdata;
wire  [7:0] lsu_wstrb;
wire        lsu_wlast;
wire        lsu_wvalid;
wire        lsu_wready;
wire  [3:0] lsu_bid;
wire  [1:0] lsu_bresp;
wire        lsu_bvalid;
wire        lsu_bready;
wire  [3:0] lsu_rid;
wire [63:0] lsu_rdata;
wire  [1:0] lsu_rresp;
wire        lsu_rlast;
wire        lsu_rvalid;
wire        lsu_rready;
wire  [4:0] rom_awid;
wire [31:0] rom_awaddr;
wire  [7:0] rom_awlen;
wire  [2:0] rom_awsize;
wire  [1:0] rom_awburst;
wire        rom_awlock;
wire  [3:0] rom_awcache;
wire  [2:0] rom_awprot;
wire  [3:0] rom_awregion;
wire  [3:0] rom_awqos;
wire        rom_awvalid;
wire        rom_awready;
wire  [4:0] rom_arid;
wire [31:0] rom_araddr;
wire  [7:0] rom_arlen;
wire  [2:0] rom_arsize;
wire  [1:0] rom_arburst;
wire        rom_arlock;
wire  [3:0] rom_arcache;
wire  [2:0] rom_arprot;
wire  [3:0] rom_arregion;
wire  [3:0] rom_arqos;
wire        rom_arvalid;
wire        rom_arready;
wire [63:0] rom_wdata;
wire  [7:0] rom_wstrb;
wire        rom_wlast;
wire        rom_wvalid;
wire        rom_wready;
wire  [4:0] rom_bid;
wire  [1:0] rom_bresp;
wire        rom_bvalid;
wire        rom_bready;
wire  [4:0] rom_rid;
wire [63:0] rom_rdata;
wire  [1:0] rom_rresp;
wire        rom_rlast;
wire        rom_rvalid;
wire        rom_rready;
wire  [4:0] uart_awid;
wire [31:0] uart_awaddr;
wire  [7:0] uart_awlen;
wire  [2:0] uart_awsize;
wire  [1:0] uart_awburst;
wire        uart_awlock;
wire  [3:0] uart_awcache;
wire  [2:0] uart_awprot;
wire  [3:0] uart_awregion;
wire  [3:0] uart_awqos;
wire        uart_awvalid;
wire        uart_awready;
wire  [4:0] uart_arid;
wire [31:0] uart_araddr;
wire  [7:0] uart_arlen;
wire  [2:0] uart_arsize;
wire  [1:0] uart_arburst;
wire        uart_arlock;
wire  [3:0] uart_arcache;
wire  [2:0] uart_arprot;
wire  [3:0] uart_arregion;
wire  [3:0] uart_arqos;
wire        uart_arvalid;
wire        uart_arready;
wire [63:0] uart_wdata;
wire  [7:0] uart_wstrb;
wire        uart_wlast;
wire        uart_wvalid;
wire        uart_wready;
wire  [4:0] uart_bid;
wire  [1:0] uart_bresp;
wire        uart_bvalid;
wire        uart_bready;
wire  [4:0] uart_rid;
wire [63:0] uart_rdata;
wire  [1:0] uart_rresp;
wire        uart_rlast;
wire        uart_rvalid;
wire        uart_rready;
wire  [4:0] ram_awid;
wire [31:0] ram_awaddr;
wire  [7:0] ram_awlen;
wire  [2:0] ram_awsize;
wire  [1:0] ram_awburst;
wire        ram_awlock;
wire  [3:0] ram_awcache;
wire  [2:0] ram_awprot;
wire  [3:0] ram_awregion;
wire  [3:0] ram_awqos;
wire        ram_awvalid;
wire        ram_awready;
wire  [4:0] ram_arid;
wire [31:0] ram_araddr;
wire  [7:0] ram_arlen;
wire  [2:0] ram_arsize;
wire  [1:0] ram_arburst;
wire        ram_arlock;
wire  [3:0] ram_arcache;
wire  [2:0] ram_arprot;
wire  [3:0] ram_arregion;
wire  [3:0] ram_arqos;
wire        ram_arvalid;
wire        ram_arready;
wire [63:0] ram_wdata;
wire  [7:0] ram_wstrb;
wire        ram_wlast;
wire        ram_wvalid;
wire        ram_wready;
wire  [4:0] ram_bid;
wire  [1:0] ram_bresp;
wire        ram_bvalid;
wire        ram_bready;
wire  [4:0] ram_rid;
wire [63:0] ram_rdata;
wire  [1:0] ram_rresp;
wire        ram_rlast;
wire        ram_rvalid;
wire        ram_rready;

axi_intercon axi_intercon
   (.clk_i           (clk),
    .rst_ni          (rst_n),
    .i_ifu_arid      (ifu_arid),
    .i_ifu_araddr    (ifu_araddr),
    .i_ifu_arlen     (ifu_arlen),
    .i_ifu_arsize    (ifu_arsize),
    .i_ifu_arburst   (ifu_arburst),
    .i_ifu_arlock    (ifu_arlock),
    .i_ifu_arcache   (ifu_arcache),
    .i_ifu_arprot    (ifu_arprot),
    .i_ifu_arregion  (ifu_arregion),
    .i_ifu_arqos     (ifu_arqos),
    .i_ifu_arvalid   (ifu_arvalid),
    .o_ifu_arready   (ifu_arready),
    .o_ifu_rid       (ifu_rid),
    .o_ifu_rdata     (ifu_rdata),
    .o_ifu_rresp     (ifu_rresp),
    .o_ifu_rlast     (ifu_rlast),
    .o_ifu_rvalid    (ifu_rvalid),
    .i_ifu_rready    (ifu_rready),
    .i_lsu_awid      (lsu_awid),
    .i_lsu_awaddr    (lsu_awaddr),
    .i_lsu_awlen     (lsu_awlen),
    .i_lsu_awsize    (lsu_awsize),
    .i_lsu_awburst   (lsu_awburst),
    .i_lsu_awlock    (lsu_awlock),
    .i_lsu_awcache   (lsu_awcache),
    .i_lsu_awprot    (lsu_awprot),
    .i_lsu_awregion  (lsu_awregion),
    .i_lsu_awqos     (lsu_awqos),
    .i_lsu_awvalid   (lsu_awvalid),
    .o_lsu_awready   (lsu_awready),
    .i_lsu_arid      (lsu_arid),
    .i_lsu_araddr    (lsu_araddr),
    .i_lsu_arlen     (lsu_arlen),
    .i_lsu_arsize    (lsu_arsize),
    .i_lsu_arburst   (lsu_arburst),
    .i_lsu_arlock    (lsu_arlock),
    .i_lsu_arcache   (lsu_arcache),
    .i_lsu_arprot    (lsu_arprot),
    .i_lsu_arregion  (lsu_arregion),
    .i_lsu_arqos     (lsu_arqos),
    .i_lsu_arvalid   (lsu_arvalid),
    .o_lsu_arready   (lsu_arready),
    .i_lsu_wdata     (lsu_wdata),
    .i_lsu_wstrb     (lsu_wstrb),
    .i_lsu_wlast     (lsu_wlast),
    .i_lsu_wvalid    (lsu_wvalid),
    .o_lsu_wready    (lsu_wready),
    .o_lsu_bid       (lsu_bid),
    .o_lsu_bresp     (lsu_bresp),
    .o_lsu_bvalid    (lsu_bvalid),
    .i_lsu_bready    (lsu_bready),
    .o_lsu_rid       (lsu_rid),
    .o_lsu_rdata     (lsu_rdata),
    .o_lsu_rresp     (lsu_rresp),
    .o_lsu_rlast     (lsu_rlast),
    .o_lsu_rvalid    (lsu_rvalid),
    .i_lsu_rready    (lsu_rready),
    .o_rom_awid      (rom_awid),
    .o_rom_awaddr    (rom_awaddr),
    .o_rom_awlen     (rom_awlen),
    .o_rom_awsize    (rom_awsize),
    .o_rom_awburst   (rom_awburst),
    .o_rom_awlock    (rom_awlock),
    .o_rom_awcache   (rom_awcache),
    .o_rom_awprot    (rom_awprot),
    .o_rom_awregion  (rom_awregion),
    .o_rom_awqos     (rom_awqos),
    .o_rom_awvalid   (rom_awvalid),
    .i_rom_awready   (rom_awready),
    .o_rom_arid      (rom_arid),
    .o_rom_araddr    (rom_araddr),
    .o_rom_arlen     (rom_arlen),
    .o_rom_arsize    (rom_arsize),
    .o_rom_arburst   (rom_arburst),
    .o_rom_arlock    (rom_arlock),
    .o_rom_arcache   (rom_arcache),
    .o_rom_arprot    (rom_arprot),
    .o_rom_arregion  (rom_arregion),
    .o_rom_arqos     (rom_arqos),
    .o_rom_arvalid   (rom_arvalid),
    .i_rom_arready   (rom_arready),
    .o_rom_wdata     (rom_wdata),
    .o_rom_wstrb     (rom_wstrb),
    .o_rom_wlast     (rom_wlast),
    .o_rom_wvalid    (rom_wvalid),
    .i_rom_wready    (rom_wready),
    .i_rom_bid       (rom_bid),
    .i_rom_bresp     (rom_bresp),
    .i_rom_bvalid    (rom_bvalid),
    .o_rom_bready    (rom_bready),
    .i_rom_rid       (rom_rid),
    .i_rom_rdata     (rom_rdata),
    .i_rom_rresp     (rom_rresp),
    .i_rom_rlast     (rom_rlast),
    .i_rom_rvalid    (rom_rvalid),
    .o_rom_rready    (rom_rready),
    .o_uart_awid     (uart_awid),
    .o_uart_awaddr   (uart_awaddr),
    .o_uart_awlen    (uart_awlen),
    .o_uart_awsize   (uart_awsize),
    .o_uart_awburst  (uart_awburst),
    .o_uart_awlock   (uart_awlock),
    .o_uart_awcache  (uart_awcache),
    .o_uart_awprot   (uart_awprot),
    .o_uart_awregion (uart_awregion),
    .o_uart_awqos    (uart_awqos),
    .o_uart_awvalid  (uart_awvalid),
    .i_uart_awready  (uart_awready),
    .o_uart_arid     (uart_arid),
    .o_uart_araddr   (uart_araddr),
    .o_uart_arlen    (uart_arlen),
    .o_uart_arsize   (uart_arsize),
    .o_uart_arburst  (uart_arburst),
    .o_uart_arlock   (uart_arlock),
    .o_uart_arcache  (uart_arcache),
    .o_uart_arprot   (uart_arprot),
    .o_uart_arregion (uart_arregion),
    .o_uart_arqos    (uart_arqos),
    .o_uart_arvalid  (uart_arvalid),
    .i_uart_arready  (uart_arready),
    .o_uart_wdata    (uart_wdata),
    .o_uart_wstrb    (uart_wstrb),
    .o_uart_wlast    (uart_wlast),
    .o_uart_wvalid   (uart_wvalid),
    .i_uart_wready   (uart_wready),
    .i_uart_bid      (uart_bid),
    .i_uart_bresp    (uart_bresp),
    .i_uart_bvalid   (uart_bvalid),
    .o_uart_bready   (uart_bready),
    .i_uart_rid      (uart_rid),
    .i_uart_rdata    (uart_rdata),
    .i_uart_rresp    (uart_rresp),
    .i_uart_rlast    (uart_rlast),
    .i_uart_rvalid   (uart_rvalid),
    .o_uart_rready   (uart_rready),
    .o_ram_awid      (ram_awid),
    .o_ram_awaddr    (ram_awaddr),
    .o_ram_awlen     (ram_awlen),
    .o_ram_awsize    (ram_awsize),
    .o_ram_awburst   (ram_awburst),
    .o_ram_awlock    (ram_awlock),
    .o_ram_awcache   (ram_awcache),
    .o_ram_awprot    (ram_awprot),
    .o_ram_awregion  (ram_awregion),
    .o_ram_awqos     (ram_awqos),
    .o_ram_awvalid   (ram_awvalid),
    .i_ram_awready   (ram_awready),
    .o_ram_arid      (ram_arid),
    .o_ram_araddr    (ram_araddr),
    .o_ram_arlen     (ram_arlen),
    .o_ram_arsize    (ram_arsize),
    .o_ram_arburst   (ram_arburst),
    .o_ram_arlock    (ram_arlock),
    .o_ram_arcache   (ram_arcache),
    .o_ram_arprot    (ram_arprot),
    .o_ram_arregion  (ram_arregion),
    .o_ram_arqos     (ram_arqos),
    .o_ram_arvalid   (ram_arvalid),
    .i_ram_arready   (ram_arready),
    .o_ram_wdata     (ram_wdata),
    .o_ram_wstrb     (ram_wstrb),
    .o_ram_wlast     (ram_wlast),
    .o_ram_wvalid    (ram_wvalid),
    .i_ram_wready    (ram_wready),
    .i_ram_bid       (ram_bid),
    .i_ram_bresp     (ram_bresp),
    .i_ram_bvalid    (ram_bvalid),
    .o_ram_bready    (ram_bready),
    .i_ram_rid       (ram_rid),
    .i_ram_rdata     (ram_rdata),
    .i_ram_rresp     (ram_rresp),
    .i_ram_rlast     (ram_rlast),
    .i_ram_rvalid    (ram_rvalid),
    .o_ram_rready    (ram_rready));
