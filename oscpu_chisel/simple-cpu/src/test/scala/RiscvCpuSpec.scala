import chisel3.iotesters.PeekPokeTester
import org.scalatest._

class RiscvCpuSpec extends FlatSpec with Matchers {

  "RiscvCpu" should "pass" in {
    chisel3.iotesters.Driver(() => new oscpu.RiscvCpu()) { c =>
      new PeekPokeTester(c) {
      
        println("Start the cpu")
      
        println("PC: " + peek(c.io.inst_addr).toString)
        println("Ready to fetch: " + peek(c.io.inst_ena).toString)
        poke(c.io.inst, 0x00100093) // inst 0: 1 + zero = reg1 1+0=1
        step(1)
        expect(c.io.reg_out(1), 1)
        
        println("PC: " + peek(c.io.inst_addr).toString)
        println("Ready to fetch: " + peek(c.io.inst_ena).toString)
        poke(c.io.inst, 0x00200093) // inst 1: 2 + zero = reg1 2+0=2
        step(1)
        expect(c.io.reg_out(1), 2)
        
        println("PC: " + peek(c.io.inst_addr).toString)
        println("Ready to fetch: " + peek(c.io.inst_ena).toString)
        poke(c.io.inst, 0x00108093) // inst 2: 1 + reg1 = reg1 1+2=3
        step(1)
        expect(c.io.reg_out(1), 3)
        
        println("PC: " + peek(c.io.inst_addr).toString)
        println("Ready to fetch: " + peek(c.io.inst_ena).toString)
        poke(c.io.inst, 0xfff08093) // inst 3: -1 + reg1 = reg1 -1+3=2
        step(1)
        expect(c.io.reg_out(1), 2)
        
      }
    } should be (true)
  }

}

