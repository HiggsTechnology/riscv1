TOOLCHAIN_PREFIX ?= riscv32-unknown-elf-

crt0.o: crt0.S
	$(TOOLCHAIN_PREFIX)as -march=rv32i -mabi=ilp32 -o$@ $< 

%.o: %.c ../inc/multicon.h ../inc/uart.h
	$(TOOLCHAIN_PREFIX)gcc -march=rv32i -mabi=ilp32 -c -O3 -o$@ $(@:%.o=%.c)

%.S: %.c
	$(TOOLCHAIN_PREFIX)gcc -march=rv32i -mabi=ilp32 -o$@ $< -S -O3

%.elf: %.o crt0.o link.ld
	$(TOOLCHAIN_PREFIX)ld -nostdlib -T link.ld -o$@ $(@:%.elf=%.o) crt0.o
#$(TOOLCHAIN_PREFIX)ld -nostdlib -T link.ld -o$@ $^
#$(TOOLCHAIN_PREFIX)gcc -nostartfiles -march=rv32i -mabi=ilp32 -Tlink.ld -o$@ $< -Wl,-emain 
#$(TOOLCHAIN_PREFIX)gcc -march=rv32i -mabi=ilp32 -Tlink.ld -o$@ $<
%.vh: %.bin
	python3 makehex.py $< > $@
	cp $@ ../sim-vcs/$@
%.bin: %.elf
	$(TOOLCHAIN_PREFIX)objcopy -O binary $< $@
	$(TOOLCHAIN_PREFIX)objdump -S -s -x $< > $*.lst
clean:
	rm -f *.elf *.bin *.vh *.lst

