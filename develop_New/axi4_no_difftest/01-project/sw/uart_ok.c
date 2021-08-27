#include "../inc/multicon.h"
#include "../inc/uart.h"

void uart_write(char *str);
void uart_ini();
char get_uart_bytes_block();
void put_uart_bytes_block(char *str);
char str[32];

void main()
{
      	char *str1; 	
        uart_ini();
		//str2 = "Hello UART!!!\n";
        //uart_write(str2); 

		//put_uart_bytes_block('!');
		//put_uart_bytes_block('t');
		//put_uart_bytes_block('e');
		//put_uart_bytes_block('s');
		//put_uart_bytes_block('t');
		//put_uart_bytes_block('*');
		str1 = "!test*\n";
		put_uart_bytes_block(str1);
		while(1) {
			for (int i = 0;i<5;i++){
				str[i] = get_uart_bytes_block()+1;
			}
			put_uart_bytes_block(str);
		}
        HALT_ADDR = 0;
}

void start()
{
	main();

}


// ini uart register
void uart_ini()
{
        //115200
        //UART_REG_LC = 0x9b; //{8'b10011011, 24'b0};
        UART_REG_LC =0x83;
		UART_REG_DL2 = 0x0;
		UART_REG_DL1 = 0xe;
        asm ("nop; nop; nop;" ::);
		//UART_REG_LC =0x1b;
        UART_REG_LC = 0x03;
        UART_REG_IE = 0x0;
		UART_REG_FC = 0xc0; 
}

void uart_write(char *str)
{
        while (*str != '\0') {
		UART_REG_TR = *str;
        	str++;
        }
	
}

char get_uart_bytes_block(void)
{
	while(1){
		if (UART_REG_LS & (unsigned char)0x1) {
			return (char)UART_REG_RB;
		}
	}
}


void put_uart_bytes_block(char *str)
{
	while(1){
		if (UART_REG_LS & ((unsigned char)0x1<<5)) {
			while(*str != '\0'){
				UART_REG_TR = *str;
				str++;
			}
			return;
		}
	}
}
