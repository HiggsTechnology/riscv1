1.1 根据飞书中环境安装1.0配置基础scala,chisel,mill,verilator等环境。
1.2 download submodules
    git submodule init
    git submodule update
1.3 revise line 18 in ThirdParty/difftest/Makefile
    DESIGN_DIR ?= .. -> DESIGN_DIR ?= ../..
2.1 设置 NEMU_HOME 环境变量为NEMU所在目录 
    e.g. echo export NEMU_HOME=$(pwd)/NEMU >> ~/.bashrc
         source ~/.bashrc
2.2 设置 AM_HOME 环境变量为AM所在目录 
    e.g. echo export AM_HOME=$(pwd)/abstract-machine >> ~/.bashrc
         source ~/.bashrc
3. 设置NOOP_HOME为项目根目录 
    例如 export NOOP_HOME=/riscv1/chisel-template
进入am-kernels/tests/cpu-test文件目录下
4. 编译c文件
    make ARCH=riscv64-mycpu ALL=add
进入chisel-template文件目录下
4. 生产Verilog文件
    mill -i __.test.runMain Core.TOP.TopMain -td ./build
5. 生产difftest emu
    make -C difftest emu
6. make NEMU
    ./build.sh -b -d

6. 对add.bin进行仿真
    build/emu -i ./ThirdParty/am-kernels/tests/cpu-tests/build/add-riscv64-mycpu.bin
