package top

import Sim.SimTop
import Core.riscv_cpu
import chisel3.stage.{ChiselGeneratorAnnotation, ChiselStage}

object TopMain extends App {

  (new ChiselStage).execute(
    args,
    Seq(
      ChiselGeneratorAnnotation(() => new SimTop())
    )
  )
  (new ChiselStage).execute(
    args,
    Seq(
      ChiselGeneratorAnnotation(() => new riscv_cpu())
    )
  )
}
