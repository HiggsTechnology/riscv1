package core

import chisel3.stage._
// import Sim._

object TopMain extends App {
  (new ChiselStage).execute(
    args,
    Seq(
      ChiselGeneratorAnnotation(() => new SimTop())
    )
  )
}
