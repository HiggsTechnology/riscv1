OUTPUT_ARCH( "riscv" )
ENTRY(_start)

MEMORY
{
  ROM  : ORIGIN = 0x80000000, LENGTH = 1M
  RAM  : ORIGIN = 0x00000000, LENGTH = 16M
  ICCM : ORIGIN = 0xFF000000, LENGTH = 128K
  DCCM : ORIGIN = 0xFF080000, LENGTH = 64K
}

_kernel_stack_ = 0xFF090000 ;

SECTIONS
{
  . = 0;
  s_ram : { crt0.o(*)
            *(.text.*)
            *(.text)
            *(.data)
            *(.rodata.*)
            *(*)
          } > RAM
}

