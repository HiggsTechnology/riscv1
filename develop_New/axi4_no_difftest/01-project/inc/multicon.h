//multicon.h
#define _MULTICON_BASE 0x90040000
#define CONSOLE_ADDR	*((volatile unsigned int *)(_MULTICON_BASE + 0x008))
#define HALT_ADDR	*((volatile unsigned int *)(_MULTICON_BASE + 0x00C))
#define GPIO_ADDR	*((volatile unsigned int *)(_MULTICON_BASE + 0x010))


#define _SMC_BASE 0x08000000
#define SMC_ADDR_0	*((volatile int *)(_SMC_BASE + 0x000))
#define SMC_ADDR_1	*((volatile int *)(_SMC_BASE + 0x008))

#define _RAM_BASE 0x00000000
#define APPDADDR_START	(volatile int *)(_RAM_BASE + 0x000)

#define _DDR_BASE 0x40000000

#define _ROM_BASE 0x80000000
