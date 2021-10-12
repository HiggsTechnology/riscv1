package top
import Sim._
import Sim.SimTop
import Core.riscv_cpu
import chisel3.stage.{ChiselGeneratorAnnotation, ChiselStage}

object TopMain extends App {

  (new ChiselStage).execute(
    args,

    Seq(
      ChiselGeneratorAnnotation(() => new SimTop()),
      firrtl.stage.RunFirrtlTransformAnnotation(new AddModulePrefix()),
      ModulePrefixAnnotation("ysyx_210062_")
    )
  )
  (new ChiselStage).execute(
    args,
    Seq(
      ChiselGeneratorAnnotation(() => new riscv_cpu())
    )
  )
}
