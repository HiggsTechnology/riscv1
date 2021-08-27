void main()
{
	volatile int *ptr = (volatile int*) 0x80001008;
	*ptr = 123;  //write 123 to the int located at 0x80001008
}
