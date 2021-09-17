package Sim

import Core.SimTop
import chisel3.stage.{ChiselGeneratorAnnotation, ChiselStage}

object TopMain extends App {
  (new ChiselStage).execute(
    args,
    Seq(
      ChiselGeneratorAnnotation(() => new SimTop())
    )
  )
}
