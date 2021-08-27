`default_nettype none

module sugar_tb
  #(parameter bootrom_file  = "")
  (  );




   reg   clk = 1'b0;
   reg   rst = 1'b1;
   always #20 clk <= !clk;
   initial #100 rst <= 1'b0;


   wire  o_gpio;
   wire  o_uart0_tx;
   wire  o_uart1_tx;


   reg [1023:0] ram_init_file;

   initial begin
      if (|$test$plusargs("jtag_vpi_enable")) begin
        $display("JTAG VPI enabled. Not loading RAM");
        $readmemh("blank.vh", sugar_top.ram.ram.mem);
      end
      else if ($value$plusargs("ram_init_file=%s", ram_init_file)) begin
         $display("Loading RAM contents from %0s", ram_init_file);
         $readmemh(ram_init_file, sugar_top.ram.ram.mem);
      end
   end

   reg [1023:0] rom_init_file;

   initial begin
      if ($value$plusargs("rom_init_file=%s", rom_init_file)) begin
         $display("Loading ROM contents from %0s", rom_init_file);
         $readmemh(rom_init_file, sugar_top.bootrom.ram.mem);
      end else if (!(|bootrom_file))
        //Jump to address 0 if no bootloader is selected
        sugar_top.bootrom.ram.mem[0] = 64'h0000000000000067;
        sugar_top.bootrom.ram.mem[1] = 64'h0000000000000000;
        sugar_top.bootrom.ram.mem[2] = 64'h0000000000000000;
        sugar_top.bootrom.ram.mem[3] = 64'h0000000000000000;
        sugar_top.bootrom.ram.mem[4] = 64'h0000000000000000;
        sugar_top.bootrom.ram.mem[5] = 64'h0000000000000000;
        sugar_top.bootrom.ram.mem[6] = 64'h0000000000000000;
   end



  sugar_top
     #(.bootrom_file (bootrom_file))
   u_sugar_top
    (  .clk                 (clk)
      ,.rst_n               (~rst)    
      ,.i_uart0_rx          (1'b1)
      ,.o_uart0_tx          (o_uart0_tx)
      ,.i_uart1_rx          (1'b1)
      ,.o_uart1_tx          (o_uart1_tx)
      ,.i_ram_init_done     (1'b1)
      ,.i_ram_init_error    (1'b0)
      ,.o_gpio              (o_gpio)
    );

`ifdef DUMP_FSDB
initial begin
  $fsdbDumpfile("trace.fsdb");
  $fsdbDumpvars(0, sugar_tb , "+all", "+struct", "+parameter");
end
`endif

endmodule

`default_nettype wire

