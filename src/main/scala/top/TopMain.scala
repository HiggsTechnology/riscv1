package top
import Sim._
import Sim.SimTop
import Core.ysyx_210062
import chisel3.stage.{ChiselGeneratorAnnotation, ChiselStage}

object TopMain extends App {

  (new ChiselStage).execute(
    args,
    Seq(
      ChiselGeneratorAnnotation(() => new SimTop()),
    )
  )
  (new ChiselStage).execute(
    args,
    Seq(
      ChiselGeneratorAnnotation(() => new ysyx_210062()) ,
      firrtl.stage.RunFirrtlTransformAnnotation(new AddModulePrefix()),
      ModulePrefixAnnotation("ysyx_210062_")
    )
  )
}
