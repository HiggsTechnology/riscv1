#include "../inc/multicon.h"
#define  READ_REG(addr,ch)ch = *(volatile unsigned char *)(addr)

#define  CSRS(csr,val)  asm("csrs "#csr", %0" : : "r" (val))
#define  CSRC(csr,val)  asm("csrc "#csr", %0" : : "r" (val))
#define  CSRW(csr,val)  asm("csrw "#csr", %0" : : "r" (val))
#define  CSRR(csr,val)  asm("csrr %0, "#csr   : "=r" (val) :

void print_str (char *str);

//void reg_write( int addr , int wdata ){
//    //  volatile int *actual_add    =  addr;
//    //  *actual_add                 = wdata;
//    (*(volatile*)addr) = wdata ;
//  }

void main()
{

//
//PIC
	volatile int *mpiccfg    = (volatile int*) 0xf00c3000;
	volatile int *meigwctrls = (volatile int*) 0xf00c4000;
	volatile int *meigwclrs  = (volatile int*) 0xf00c5000;
	//volatile int *meivt      = (volatile int*) 0x10000bc8;
	volatile int *meipls     = (volatile int*) 0xf00c3004;
	//volatile int *meipt      = (volatile int*) 0x10000bc9;
	//volatile int *meicidpl   = (volatile int*) 0x10000bcb;
	//volatile int *meicurpl   = (volatile int*) 0x10000bcc;
	volatile int *meies      = (volatile int*) 0xf00c2004;
	//volatile int *meies      = (volatile int*) 0xf00c2004;
        volatile int *nvdla_pdp_rdma_s_pointer_0                = (volatile int*) 0x9002a004;
        volatile int *nvdla_pdp_rdma_d_cya_0                    = (volatile int*) 0x9002a04c;
        volatile int *nvdla_pdp_rdma_d_partial_width_in_0       = (volatile int*) 0x9002a040;
        volatile int *nvdla_pdp_rdma_d_src_line_stride_0        = (volatile int*) 0x9002a024;
        volatile int *nvdla_pdp_rdma_d_src_base_addr_high_0     = (volatile int*) 0x9002a024;
        volatile int *nvdla_pdp_rdma_d_pooling_padding_cfg_0    = (volatile int*) 0x9002a03c;
        volatile int *nvdla_pdp_rdma_d_pooling_kernel_cfg_0     = (volatile int*) 0x9002a038;
        volatile int *nvdla_pdp_rdma_d_data_format_0            = (volatile int*) 0x9002a030;
        volatile int *nvdla_pdp_rdma_d_data_cube_in_height_0    = (volatile int*) 0x9002a010;
        volatile int *nvdla_pdp_rdma_d_flying_mode_0            = (volatile int*) 0x9002a018;
        volatile int *nvdla_pdp_rdma_d_src_ram_cfg_0            = (volatile int*) 0x9002a02c;
        volatile int *nvdla_pdp_rdma_d_data_cube_in_channel_0   = (volatile int*) 0x9002a014;
        volatile int *nvdla_pdp_rdma_d_operation_mode_cfg_0     = (volatile int*) 0x9002a034;
        volatile int *nvdla_pdp_rdma_d_data_cube_in_width_0     = (volatile int*) 0x9002a00c;
        volatile int *nvdla_pdp_rdma_d_src_surface_stride_0     = (volatile int*) 0x9002a028;
        volatile int *nvdla_pdp_rdma_d_perf_enable_0            = (volatile int*) 0x9002a044;
        volatile int *nvdla_pdp_rdma_d_src_base_addr_low_0      = (volatile int*) 0x9002a01c;
        volatile int *nvdla_pdp_rdma_d_op_enable_0              = (volatile int*) 0x9002a008;
        volatile int *nvdla_pdp_s_pointer_0                     = (volatile int*) 0x9002a004;
        volatile int *nvdla_pdp_d_data_cube_out_height_0        = (volatile int*) 0x9002b01c;
        volatile int *nvdla_pdp_d_pooling_padding_cfg_0         = (volatile int*) 0x9002b040;
        volatile int *nvdla_pdp_d_dst_surface_stride_0          = (volatile int*) 0x9002b07c;
        volatile int *nvdla_pdp_d_nan_flush_to_zero_0           = (volatile int*) 0x9002b028;
        volatile int *nvdla_pdp_d_recip_kernel_height_0         = (volatile int*) 0x9002b03c;
        volatile int *nvdla_pdp_d_src_base_addr_high_0          = (volatile int*) 0x9002b064;
        volatile int *nvdla_pdp_d_src_base_addr_low_0           = (volatile int*) 0x9002b060;
        volatile int *nvdla_pdp_d_data_cube_out_width_0         = (volatile int*) 0x9002b018;
        volatile int *nvdla_pdp_d_data_cube_in_height_0         = (volatile int*) 0x9002b010;
        volatile int *nvdla_pdp_d_data_cube_in_width_0          = (volatile int*) 0x9002b00c;
        volatile int *nvdla_pdp_d_pooling_padding_value_3_cfg_0 = (volatile int*) 0x9002b04c;
        volatile int *nvdla_pdp_d_cya_0                         = (volatile int*) 0x9002b09c;
        volatile int *nvdla_pdp_d_pooling_padding_value_5_cfg_0 = (volatile int*) 0x9002b054;
        volatile int *nvdla_pdp_d_pooling_kernel_cfg_0          = (volatile int*) 0x9002b034;
        volatile int *nvdla_pdp_d_dst_base_addr_high_0          = (volatile int*) 0x9002b074;
        volatile int *nvdla_pdp_d_perf_enable_0                 = (volatile int*) 0x9002b094;
        volatile int *nvdla_pdp_d_src_surface_stride_0          = (volatile int*) 0x9002b06c;
        volatile int *nvdla_pdp_d_dst_base_addr_low_0           = (volatile int*) 0x9002b070;
        volatile int *nvdla_pdp_d_pooling_padding_value_7_cfg_0 = (volatile int*) 0x9002b05c;
        volatile int *nvdla_pdp_d_pooling_padding_value_4_cfg_0 = (volatile int*) 0x9002b050;
        volatile int *nvdla_pdp_d_dst_line_stride_0             = (volatile int*) 0x9002b078;
        volatile int *nvdla_pdp_d_data_format_0                 = (volatile int*) 0x9002b084;
        volatile int *nvdla_pdp_d_partial_width_out_0           = (volatile int*) 0x9002b030;
        volatile int *nvdla_pdp_d_operation_mode_cfg_0          = (volatile int*) 0x9002b024;
        volatile int *nvdla_pdp_d_pooling_padding_value_6_cfg_0 = (volatile int*) 0x9002b058;
        volatile int *nvdla_pdp_d_partial_width_in_0            = (volatile int*) 0x9002b02c;
        volatile int *nvdla_pdp_d_pooling_padding_value_1_cfg_0 = (volatile int*) 0x9002b044;
        volatile int *nvdla_pdp_d_data_cube_in_channel_0        = (volatile int*) 0x9002b014;
        volatile int *nvdla_pdp_d_pooling_padding_value_2_cfg_0 = (volatile int*) 0x9002b048;
        volatile int *nvdla_pdp_d_data_cube_out_channel_0       = (volatile int*) 0x9002b020;
        volatile int *nvdla_pdp_d_src_line_stride_0             = (volatile int*) 0x9002b068;
        volatile int *nvdla_pdp_d_recip_kernel_width_0          = (volatile int*) 0x9002b038;
        volatile int *nvdla_pdp_d_dst_ram_cfg_0                 = (volatile int*) 0x9002b080;
        volatile int *nvdla_pdp_d_op_enable_0                   = (volatile int*) 0x9002b008;
        volatile int *nvdla_aasfdp_rdma_d_flying_mode_0         = (volatile int*) 0x9002a018;

   //Initialization
     //1.Configure the priority order by writing the priord bit of the mpiccfg
     //register.
        *mpiccfg = 0x0; 

     //2.For each configurable gateway S, set the polarity(polarity field) and
     //type(type field)in the meigwctrlS register and clear the IP bit writing
     //to the gaterway's meigwclrS register.
        *meigwctrls = 0x0;
	*meigwclrs  = 0x0;//

     //3.Set the base address of the external vectored interrupt address table
     //by writing the base field of the meivt register.
        //*meivt = 0xc00;
       CSRW(0xbc8, 0xc00);

     //4.Set the priority level for each external interrupt source S by
     //writing the corresponding prioity field of the meiplS registers.
        *meipls = 0x5;

     //5.Set the priority threshold by writing prithresh field of the meipt
     //register.
        //*meipt = 0x2; 
        CSRW(0xbc9, 0x2);
     //6.Initialize the nesting priority thresholds by writing '0'(or '15'for
     //reversed priority order)to the clidpri field of the meicidpl and the
     //currpri field of the meicurpl registers.
        //*meicidpl = 0x0;
	//*meicurpl = 0x0;
        CSRW(0xbcb, 0x0);
        CSRW(0xbcc, 0x0);

     //7.Enable inertupts for the appropriate external interrupt sources by 
     //setting the inten bit of the meieS registers for each interrupt source S
        *meies = 0x1;
        

//NVDLA
       //load data
//        {
//{offset:0x0, size:16, payload:0xe7 0x3e 0xde 0xd4 0xdd 0x6a 0x17 0xda 0x74 0xf4 0xb9 0x96 0x7b 0x18 0xdb 0x3a},
//{offset:0x10, size:16, payload:0xfa 0x1a 0xe8 0xb2    0x9d 0xe3 0x78 0xca     0xda 0x91 0xbb 0x31    0x50 0x2d 0xbb 0x6e},
//{offset:0x20, size:16, payload:0xc6 0x92 0xa5 0x0a    0x54 0xd0 0xc4 0x52     0x7b 0x43 0x4f 0xde    0x57 0xf7 0x55 0xd0},
//{offset:0x30, size:16, payload:0x35 0x17 0x15 0x1f    0x10 0xe4 0xc9 0x64     0x18 0xf9 0x73 0x35    0xd0 0x76 0x1b 0x6d},
//{offset:0x40, size:16, payload:0x14 0xa1 0xbd 0x4a    0xd3 0x03 0x4b 0xd8     0x22 0x50 0x20 0xdf    0x5d 0x3b 0xbb 0xb2},
//{offset:0x50, size:16, payload:0x16 0xbd 0xec 0x30    0x3b 0xfd 0x78 0xf8     0xbc 0xc7 0xbf 0x56    0x53 0x12 0x49 0xa0},
//{offset:0x60, size:16, payload:0x13 0x80 0x17 0xe3    0x20 0x5c 0xa6 0xff     0x35 0xcc 0xae 0x34    0x31 0xee 0xf2 0xcd},
//{offset:0x70, size:16, payload:0x9a 0xe6 0x73 0xab    0x5f 0x72 0x0b 0xa8     0xa4 0x81 0x77 0xd4    0x0e 0x4f 0x73 0xd7},
//}
   //   reg_write(,0xd4de3ee7);
   *((volatile int *)0x08000000) = 0xd4de3ee7;
   *((volatile int *)0x08000004) = 0xda176add;
   *((volatile int *)0x08000008) = 0x96b9f474;
   *((volatile int *)0x0800000c) = 0x3adb187b;
   *((volatile int *)0x08000010) = 0xb2e81afa;
   *((volatile int *)0x08000014) = 0xca78e39d;
   *((volatile int *)0x08000018) = 0x31bb91da;
   *((volatile int *)0x0800001c) = 0x6ebb2d50;
   *((volatile int *)0x08000020) = 0x0aa592c6;
   *((volatile int *)0x08000024) = 0x52c4d054;
   *((volatile int *)0x08000028) = 0xde4f437b;
   *((volatile int *)0x0800002c) = 0xd055f757;
   *((volatile int *)0x08000030) = 0x1f151735;
   *((volatile int *)0x08000034) = 0x64c9e410;
   *((volatile int *)0x08000038) = 0x3573f918;
   *((volatile int *)0x0800003c) = 0x6d1b76d0;
   *((volatile int *)0x08000040) = 0x4abda114;
   *((volatile int *)0x08000044) = 0xd84b03d3;
   *((volatile int *)0x08000048) = 0xdf205022;
   *((volatile int *)0x0800004c) = 0xb2bb3b5d;
   *((volatile int *)0x08000050) = 0x30ecbd16;
   *((volatile int *)0x08000054) = 0xf878fd3b;
   *((volatile int *)0x08000058) = 0x56bfc7bc;
   *((volatile int *)0x0800005c) = 0xa0491253;
   *((volatile int *)0x08000060) = 0xe3178013;
   *((volatile int *)0x08000064) = 0xffa65c20;
   *((volatile int *)0x08000068) = 0x34aecc35;
   *((volatile int *)0x0800006c) = 0xcdf2ee31;
   *((volatile int *)0x08000070) = 0xab73e69a;
   *((volatile int *)0x08000074) = 0xa80b725f;
   *((volatile int *)0x08000078) = 0xd47781a4;
   *((volatile int *)0x0800007c) = 0xd7734f0e;
       //write reg
   *nvdla_pdp_rdma_s_pointer_0                      = 0x0;
   *nvdla_pdp_rdma_d_cya_0                          = 0xace2c10d;
   *nvdla_pdp_rdma_d_partial_width_in_0             = 0xbb8bb7e;
   *nvdla_pdp_rdma_d_src_line_stride_0              = 0x20;
   *nvdla_pdp_rdma_d_src_base_addr_high_0           = 0x0;
   *nvdla_pdp_rdma_d_pooling_padding_cfg_0          = 0x1;
   *nvdla_pdp_rdma_d_pooling_kernel_cfg_0           = 0x36;
   *nvdla_pdp_rdma_d_data_format_0                  = 0x0;
   *nvdla_pdp_rdma_d_data_cube_in_height_0          = 0x3;
   *nvdla_aasfdp_rdma_d_flying_mode_0               = 0x1;
   *nvdla_pdp_rdma_d_src_ram_cfg_0                  = 0x0;
   *nvdla_pdp_rdma_d_data_cube_in_channel_0         = 0x7;
   *nvdla_pdp_rdma_d_operation_mode_cfg_0           = 0x0;
   *nvdla_pdp_rdma_d_data_cube_in_width_0           = 0x3;
   *nvdla_pdp_rdma_d_src_surface_stride_0           = 0x80;
   *nvdla_pdp_rdma_d_perf_enable_0                  = 0x1;
   *nvdla_pdp_rdma_d_src_base_addr_low_0            = 0x0;
   *nvdla_pdp_rdma_d_op_enable_0                    = 0x1;
   *nvdla_pdp_s_pointer_0                           = 0x0;
   *nvdla_pdp_d_data_cube_out_height_0              = 0x0;
   *nvdla_pdp_d_pooling_padding_cfg_0               = 0x1013;
   *nvdla_pdp_d_dst_surface_stride_0                = 0x850;
   *nvdla_pdp_d_nan_flush_to_zero_0                 = 0x1;
   *nvdla_pdp_d_recip_kernel_height_0               = 0x79f;
   *nvdla_pdp_d_src_base_addr_high_0                = 0x0;
   *nvdla_pdp_d_src_base_addr_low_0                 = 0x0;
   *nvdla_pdp_d_data_cube_out_width_0               = 0x0;
   *nvdla_pdp_d_data_cube_in_height_0               = 0x3;
   *nvdla_pdp_d_data_cube_in_width_0                = 0x3;
   *nvdla_pdp_d_pooling_padding_value_3_cfg_0       = 0x34af9;
   *nvdla_pdp_d_cya_0                               = 0xf4a0ce1d;
   *nvdla_pdp_d_pooling_padding_value_5_cfg_0       = 0x71346;
   *nvdla_pdp_d_pooling_kernel_cfg_0                = 0xe30506;
   *nvdla_pdp_d_dst_base_addr_high_0                = 0x0;
   *nvdla_pdp_d_perf_enable_0                       = 0x1;
   *nvdla_pdp_d_src_surface_stride_0                = 0x80;
   *nvdla_pdp_d_dst_base_addr_low_0                 = 0x10000;
   *nvdla_pdp_d_pooling_padding_value_7_cfg_0       = 0x7618b;
   *nvdla_pdp_d_pooling_padding_value_4_cfg_0       = 0xbf61;
   *nvdla_pdp_d_dst_line_stride_0                   = 0x278;
   *nvdla_pdp_d_data_format_0                       = 0x0;
   *nvdla_pdp_d_partial_width_out_0                 = 0x36b6c745;
   *nvdla_pdp_d_operation_mode_cfg_0                = 0x11;
   *nvdla_pdp_d_pooling_padding_value_6_cfg_0       = 0x1b15e;
   *nvdla_pdp_d_partial_width_in_0                  = 0xbb8bb7e;
   *nvdla_pdp_d_pooling_padding_value_1_cfg_0       = 0x15943;
   *nvdla_pdp_d_data_cube_in_channel_0              = 0x7;
   *nvdla_pdp_d_pooling_padding_value_2_cfg_0       = 0x14fab;
   *nvdla_pdp_d_data_cube_out_channel_0             = 0x7;
   *nvdla_pdp_d_src_line_stride_0                   = 0x20;
   *nvdla_pdp_d_recip_kernel_width_0                = 0x6bbb;
   *nvdla_pdp_d_dst_ram_cfg_0                       = 0x0;
   *nvdla_pdp_d_op_enable_0                         = 0x1;
//*((volatile int *)0x90000008) = 0x2;  // set UART IER.IE_THRE
	int intr;
	int i=0;
	while (i<1)
	{
        //READ_REG(0xf00c1000,intr);
        intr = *((volatile int *)0xf00c1000);
	if(intr==0x4)
	  i++;
	else 
	  i = 0;
	}
        char *str;

        str = "hello world, this is function test!\n";
        print_str (str);

	// end simulation
        str = "test will end simulation";
        print_str (str);
        HALT_ADDR = 0;
}

//void start()
//{
////	volatile int *mrac  = (volatile int*) 0x100007c0;
////	*mrac = 0x00080000; //MRAC, bit[19:18]=2'b10 (sideeffect of region 9)
//	CSRW(0x7c0, 0x00080000);
//	asm ("fence; " ::);
//	main();
//
//}

void print_str (char *str)
{
        while (*str != '\0') {
		CONSOLE_ADDR = *str;
        	str++;
        }
	
}
