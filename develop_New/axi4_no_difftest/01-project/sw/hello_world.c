#include "../inc/multicon.h"

void print_str (char *str);

void main()
{
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
