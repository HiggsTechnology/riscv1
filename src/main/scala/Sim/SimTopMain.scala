package Sim

import chisel3.stage.{ChiselGeneratorAnnotation, ChiselStage}

object SimTopMain extends App {
  (new ChiselStage).execute(
    args,
    Seq(
      ChiselGeneratorAnnotation(() => new SimTop())
    )
  )
}
