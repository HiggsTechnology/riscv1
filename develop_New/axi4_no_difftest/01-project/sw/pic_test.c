#include "../inc/multicon.h"
#define  READ_REG(addr,ch)ch = *(volatile unsigned char *)(addr)

#define  CSRS(csr,val)  asm("csrs "#csr", %0" : : "r" (val))
#define  CSRC(csr,val)  asm("csrc "#csr", %0" : : "r" (val))
#define  CSRW(csr,val)  asm("csrw "#csr", %0" : : "r" (val))
#define  CSRR(csr,val)  asm("csrr %0, "#csr   : "=r" (val) :)

void print_str (char *str);

void main()
{
	volatile int *mpiccfg    = (volatile int*) 0xf00c3000;
	volatile int *meigwctrls = (volatile int*) 0xf00c4000;
	volatile int *meigwclrs  = (volatile int*) 0xf00c5000;
	//volatile int *meivt      = (volatile int*) 0x10000bc8;
	volatile int *meipls     = (volatile int*) 0xf00c3004;
	//volatile int *meipt      = (volatile int*) 0x10000bc9;
	//volatile int *meicidpl   = (volatile int*) 0x10000bcb;
	//volatile int *meicurpl   = (volatile int*) 0x10000bcc;
	volatile int *meies      = (volatile int*) 0xf00c2004;
	//volatile int *meipx      = (volatile int*) 0xf00c1000;



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
        
        *((volatile int *)0x90002008) = 0x2;  // set UART IER.IE_THRE

	int intr;
	int i=0;
	while (i<1)
	{
        //READ_REG(0xf00c1000,intr);
        intr = *((volatile int *)0xf00c1000);
	if(intr==0x2)
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

void start()
{
	main();

}

void print_str (char *str)
{
        while (*str != '\0') {
		CONSOLE_ADDR = *str;
        	str++;
        }
	
}
