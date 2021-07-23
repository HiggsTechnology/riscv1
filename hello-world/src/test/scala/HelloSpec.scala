import chisel3.iotesters.PeekPokeTester
import org.scalatest._

class HelloSpec extends FlatSpec with Matchers {

  "Hello" should "pass" in {
    chisel3.iotesters.Driver(() => new Hello()) { c =>
      new PeekPokeTester(c) {
      
        poke(c.io.sw, 0)
        step(1)
        expect(c.io.led, 0)

        poke(c.io.sw, 1)
        step(1)
        expect(c.io.led, 0)
        
        poke(c.io.sw, 2)
        step(1)
        expect(c.io.led, 0)

        poke(c.io.sw, 3)
        step(1)
        expect(c.io.led, 1)
        
      }
    } should be (true)
  }

}

