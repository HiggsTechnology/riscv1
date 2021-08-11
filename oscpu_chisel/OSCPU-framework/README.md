oscpu-framework
├── NEMU //用于difftest参考的cpu模拟器
│ ├──......
├── README.md
├── bin //存放riscv64机器码文件的目录
│ ├── inst.bin
│ ├── inst_diff.bin
│ └──......//可以在am中编译生成更多的.bin文件存入bin中
├── build.sh //编译脚本
├── difftest //香山difftest框架目录
│ ├──......
├── projects //项目代码目录
│ └──cpu_diff_chisel 
│ │ └──vsrc
│ │ │   └──SimTop.v  //mill生存的verilong文件，每次更新直接替换即可




本次上传的OSCPU为新下载的框架后替换完成的版本,可直接进行操作

(1) nemu编译			//可以根据官方教程调整内存为256m

CD oscpu-framwork/NEMU
make

(2) difftest编译		//进入oscpu-framework文件目录下

CD .. 
/build.sh -e cpu_diff_chisel -d -b -s -a "-i add-riscv64-mycpu.bin --dump-wave -b 0" -m "EMU_TRACE=1" 

//得到difftest输出

(3) 回归测试			//运行bin中所有指令文件
     
./build.sh -e cpu_diff_chisel -b -r




建议操作！！！！！！
(1)git clone --recursive -b 2021 https://github.com/OSCPU/oscpu-framework.git  //下载新的oscpu框架
在oscpu-framwork/project/中新建项目，例如
oscpu-framwork/project/cpu_diff_chisel/vsrc/SimTop.v     //放入mill生成的cpu verilog文件
最后进入oscpu-framwork/NEMU中
make        //nemu编译

       
(2)mill编译chisel-template四级
进入chisel-template文件目录下,生产Verilog文件，文件名称为SimTop.v
以chisel-template中四级为例:
在chisel-template/difftest中运行
    mill -i __.test.runMain core.TopMain -td ./build
可以生成chisel-template/difftest/build/SimTop.v     

(3)将生成的新SimTop.v替换oscpu-framework/project/cpu_diff_chisel/vsrc/SimTop.v
difftest编译
./build.sh -e cpu_diff_chisel -d -b -s -a "-i inst_diff.bin --dump-wave -b 0" -m "EMU_TRACE=1" 

	-e cpu_diff_chisel   //指向oscpu-framework/project/cpu_diff_chisel/
	-i inst_diff.bin     //选择oscpu-framework/bin/inst_diff.bin导入


(4)回归测试（运行bin中所有指令文件）
     ./build.sh -e cpu_diff_chisel -b -r
     
     

    


