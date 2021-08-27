all: sugar_1.0

sugar_1.0: sugar_1.0.scr
	vcs -full64 -top sugar_tb -f sugar_1.0.scr -o $@  -assert svaext -P ${VERDI_HOME}/share/PLI/VCS/LINUX64/novas.tab ${VERDI_HOME}/share/PLI/VCS/LINUX64/pli.a -debug_access+pp -debug_access+all +define+DUMP_FSDB -kdb -timescale=1ns/100ps +vpi +define+VCS_VPI

sugar_1.0_fmc: nbriscv_1.0.scr
	vcs -full64 -top sugar_tb -f sugar_1.0.scr -o $@  -assert svaext -P ${VERDI_HOME}/share/PLI/VCS/LINUX64/novas.tab ${VERDI_HOME}/share/PLI/VCS/LINUX64/pli.a -debug_access+pp -debug_access+all +define+DUMP_FSDB -kdb -timescale=1ns/100ps +vpi +define+VCS_VPI ../src/jtag_vpi_vcs_0/jtag_vpi_vcs.c -P ../src/jtag_vpi_vcs_0/jtag_vpi.tab -sverilog +define+FMC_ENABLE +define+FMC_BOOT +lint=TFIPC-L +lint=PCWM +lint=VNGS -l vcs_comp_fmc.log

run_fmc_%: nbriscv_1.0_fmc
	./sugar_1.0_fmc -l vcs.log  +ram_init_file=$(@:run_fmc_%=%).vh  +define+DUMP_FSDB

verdi: nbriscv_1.0.scr
	verdi -2012 -ssy -ssv -top nbriscv_tb -f nbriscv_1.0.scr +define+FMC_ENABLE +define+FMC_BOOT &
verdi_idccm:
	verdi -dbdir nbriscv_1.0_idccm.daidir &
run_%: sugar_1.0
	./sugar_1.0 -l vcs.log  +ram_init_file=$(@:run_%=%).vh  +define+DUMP_FSDB

run: sugar_1.0
	./sugar_1.0 -l vcs.log  +ram_init_file=uart_ok.vh  +define+DUMP_FSDB

nbriscv_1.0_idccm: nbriscv_1.0.scr
	vcs -full64 -debug_all -top nbriscv_tb -f nbriscv_1.0.scr ../src/jtag_vpi_vcs_0/jtag_vpi_vcs.c -o $@  -assert svaext -sverilog -P ../src/jtag_vpi_vcs_0/jtag_vpi.tab +define+IDCCM_RUN -fsdb -kdb -timescale=1ns/100ps

idccm_run:nbriscv_1.0_idccm
	./nbriscv_1.0_idccm -l vcs.log  
gui:
	verdi \
   	-simflow \
   	-dbdir sugar_1.0.daidir \
    -ssf trace.fsdb &
clean:
	$(RM) sugar_1.0
	$(RM) sugar_1.0_fmc
	$(RM) sugar_1.0_idccm
	rm -rf sugar_1.0.daidir
	rm -rf sugar_1.0_fmc.daidir
	rm -rf csrc
	rm -rf *.daidir
