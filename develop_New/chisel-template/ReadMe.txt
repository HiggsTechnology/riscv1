1. 根据飞书中环境安装1.0配置基础scala,chisel,mill,verilator等环境。
2. 设置 NEMU_HOME 环境变量为NEMU所在目录
3. 设置NOOP_HOME为项目根目录 
    例如 export NOOP_HOME=/riscv1/chisel-template

进入chisel-template文件目录下
4. 生产Verilog文件
    mill -i __.test.runMain Core.TOP.TopMain -td ./build
5. 生产difftest emu
    make -C difftest emu
6. 对add.bin进行仿真
    build/emu -i ./add-riscv64-mycpu.bin
    build/emu -i ./bit-riscv64-mycpu.bin
    build/emu -i ./div-riscv64-mycpu.bin
    build/emu -i ./bubble-sort-riscv64-mycpu.bin
    build/emu -i ./fib-riscv64-mycpu.bin
    build/emu -i ./mov-c-riscv64-mycpu.bin
    build/emu -i ./load-store-riscv64-mycpu.bin
