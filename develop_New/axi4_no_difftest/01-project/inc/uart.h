//APB 0x80002000-0x80002FFF
//#define _UART_ADDR_BASE 0x80002000
//#define _DLA_ADDR_BASE  0x80002400
#define _UART_ADDR_BASE 0x90000000
#define _DLA_ADDR_BASE  0x90020000
#define _UART1_ADDR_BASE 0x90080000


// UART register
#define UART_REG_RB  *((volatile unsigned char *)(_UART_ADDR_BASE + 0x0*8)) // receiver buffer
#define UART_REG_TR  *((volatile unsigned char *)(_UART_ADDR_BASE + 0x0*8)) // transmitter
#define UART_REG_IE  *((volatile unsigned char *)(_UART_ADDR_BASE + 0x1*8)) // Interrupt enable
#define UART_REG_II  *((volatile unsigned char *)(_UART_ADDR_BASE + 0x2*8)) // Interrupt identification
#define UART_REG_FC  *((volatile unsigned char *)(_UART_ADDR_BASE + 0x2*8)) // FIFO control
#define UART_REG_LC  *((volatile unsigned char *)(_UART_ADDR_BASE + 0x3*8)) // Line Control
#define UART_REG_MC  *((volatile unsigned char *)(_UART_ADDR_BASE + 0x4*8)) // Modem control
#define UART_REG_LS  *((volatile unsigned char *)(_UART_ADDR_BASE + 0x5*8)) // Line status
#define UART_REG_MS  *((volatile unsigned char *)(_UART_ADDR_BASE + 0x6*8)) // Modem status
#define UART_REG_SR  *((volatile unsigned char *)(_UART_ADDR_BASE + 0x7*8)) // Scratch register
#define UART_REG_DL1 *((volatile unsigned char *)(_UART_ADDR_BASE + 0x0*8)) // Divisor latch bytes (1-2)
#define UART_REG_DL2 *((volatile unsigned char *)(_UART_ADDR_BASE + 0x1*8))


// UART1 register
#define UART1_REG_RB  *((volatile unsigned char *)(_UART1_ADDR_BASE + 0x0*8)) // receiver buffer
#define UART1_REG_TR  *((volatile unsigned char *)(_UART1_ADDR_BASE + 0x0*8)) // transmitter
#define UART1_REG_IE  *((volatile unsigned char *)(_UART1_ADDR_BASE + 0x1*8)) // Interrupt enable
#define UART1_REG_II  *((volatile unsigned char *)(_UART1_ADDR_BASE + 0x2*8)) // Interrupt identification
#define UART1_REG_FC  *((volatile unsigned char *)(_UART1_ADDR_BASE + 0x2*8)) // FIFO control
#define UART1_REG_LC  *((volatile unsigned char *)(_UART1_ADDR_BASE + 0x3*8)) // Line Control
#define UART1_REG_MC  *((volatile unsigned char *)(_UART1_ADDR_BASE + 0x4*8)) // Modem control
#define UART1_REG_LS  *((volatile unsigned char *)(_UART1_ADDR_BASE + 0x5*8)) // Line status
#define UART1_REG_MS  *((volatile unsigned char *)(_UART1_ADDR_BASE + 0x6*8)) // Modem status
#define UART1_REG_SR  *((volatile unsigned char *)(_UART1_ADDR_BASE + 0x7*8)) // Scratch register
#define UART1_REG_DLL *((volatile unsigned char *)(_UART1_ADDR_BASE + 0x0*8)) // Divisor latch bytes (1-2)
#define UART1_REG_DLH *((volatile unsigned char *)(_UART1_ADDR_BASE + 0x1*8))

#define CDMA_REG_0 *((volatile unsigned char *)(_DLA_ADDR_BASE + 0x0*8))

