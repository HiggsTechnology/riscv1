#include "../inc/multicon.h"

#define  CSRS(csr,val)  asm("csrs "#csr", %0" : : "r" (val))
#define  CSRC(csr,val)  asm("csrc "#csr", %0" : : "r" (val))
#define  CSRW(csr,val)  asm("csrw "#csr", %0" : : "r" (val))
#define  CSRR(csr,val)  asm("csrr %0, "#csr   : "=r" (val) :)

#define  FMC_MODE_CFG   0x90060000
#define  FMC_TIMING_CFG 0x90060004
#define  FMC_FIFO_STS   0x90060010
#define  FMC_MISC_STS   0x90060014
#define  FMC_FIFO_TX    0x90060030
#define  FMC_FIFO_RX    0x90060040

void print_str (char *str);
void flash_busy_wait();

const char str_tx [64] = "\n==> Done! This is a test program for Flash programing!\n";

void main()
{
  char str_rx [64];
  int it, ir;

  // wait FMC idle
  unsigned int sts_fifo, sts_misc;
  do {
    sts_fifo = *((volatile unsigned int *)FMC_FIFO_STS);
    sts_misc = *((volatile unsigned int *)FMC_MISC_STS);
  } while (sts_fifo != 0 || sts_misc != 0);

  // changed mode to manual
  *((volatile unsigned int *)FMC_MODE_CFG) = 0x1;

  //print_str("\n==> Erasing the sector...\n");
  //
  //// send Write Enable
  //*((volatile unsigned int *)FMC_FIFO_TX) = 0x006;  // write enable instruction
  //*((volatile unsigned int *)FMC_FIFO_TX) = 0x300;  // eot

  //// erase the sector
  //*((volatile unsigned int *)FMC_FIFO_TX) = 0x020;  // page program instruction
  //*((volatile unsigned int *)FMC_FIFO_TX) = 0x001;  // address
  //*((volatile unsigned int *)FMC_FIFO_TX) = 0x080;  // address
  //*((volatile unsigned int *)FMC_FIFO_TX) = 0x000;  // address
  //*((volatile unsigned int *)FMC_FIFO_TX) = 0x300;  // eot

  //// wait for erase done
  //flash_busy_wait();

  print_str("\n==> Programming the page...\n");
  
  // send Write Enable
  *((volatile unsigned int *)FMC_FIFO_TX) = 0x006;  // write enable instruction
  *((volatile unsigned int *)FMC_FIFO_TX) = 0x300;  // eot

  // program flash
  *((volatile unsigned int *)FMC_FIFO_TX) = 0x002;  // page program instruction
  *((volatile unsigned int *)FMC_FIFO_TX) = 0x001;  // address
  *((volatile unsigned int *)FMC_FIFO_TX) = 0x088;  // address
  *((volatile unsigned int *)FMC_FIFO_TX) = 0x000;  // address
  for (it = 0; it < sizeof(str_tx); it++) {
    do {
      sts_fifo = *((volatile unsigned int *)FMC_FIFO_STS);
    } while ((sts_fifo >> 8) >= 15);
    *((volatile unsigned int *)FMC_FIFO_TX) = str_tx[it];
  }
  *((volatile unsigned int *)FMC_FIFO_TX) = 0x300;  // eot

  do {
    sts_fifo = *((volatile unsigned int *)FMC_FIFO_STS);
  } while (sts_fifo != 0);

  // wait for program done
  flash_busy_wait();

  print_str("\n==> Reading the page...\n");

  // read flash
  //*((volatile unsigned int *)FMC_FIFO_TX) = 0x003;  // read data instruction
  *((volatile unsigned int *)FMC_FIFO_TX) = 0x00B;  // fast read instruction
  *((volatile unsigned int *)FMC_FIFO_TX) = 0x001;  // address
  *((volatile unsigned int *)FMC_FIFO_TX) = 0x088;  // address
  *((volatile unsigned int *)FMC_FIFO_TX) = 0x000;  // address
  *((volatile unsigned int *)FMC_FIFO_TX) = 0x207;  // 8 dummy cyc for fast read

  it = 0; ir = 0;
  while (ir < sizeof(str_tx)) {
    sts_fifo = *((volatile unsigned int *)FMC_FIFO_STS);
    if (it < sizeof(str_tx) && (sts_fifo >> 8) < 15) {
      *((volatile unsigned int *)FMC_FIFO_TX) = 0x100;  // receive byte
      it ++;
    }
    if (it == sizeof(str_tx)) {
      *((volatile unsigned int *)FMC_FIFO_TX) = 0x300;  // eot
      it ++;
    }
    if ((sts_fifo & 0xff) != 0) {
      str_rx[ir++] = *((volatile unsigned int *)FMC_FIFO_RX);
    }
  }

  //check the read str
  print_str (str_rx);

  HALT_ADDR = 0;
}

void flash_busy_wait ()
{
  unsigned char rcv_byte;
  unsigned int  sts_fifo;
  do {
    *((volatile unsigned int *)FMC_FIFO_TX) = 0x005;  // read status-1 instruction
    *((volatile unsigned int *)FMC_FIFO_TX) = 0x100;  // receive byte
    *((volatile unsigned int *)FMC_FIFO_TX) = 0x300;  // eot
    do {
      sts_fifo = *((volatile unsigned int *)FMC_FIFO_STS);
    } while ((sts_fifo & 0xff) == 0);  // wait for receive byte
    rcv_byte = *((volatile unsigned int *)FMC_FIFO_RX);
  } while ((rcv_byte & 0x01) != 0);    // wait for BUSY bit
}

void print_str (char *str)
{
        while (*str != '\0') {
		CONSOLE_ADDR = *str;
        	str++;
        }
}

