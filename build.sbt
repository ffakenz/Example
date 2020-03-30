import Settings._
import sbt.Keys.scalaVersion

lazy val commonSettings = Seq(
  version := "1.0",
  scalaVersion := Dependencies.scalaVersion
)

lazy val global = project
  .in(file("."))
  .settings(commonSettings)
  .settings(modulesSettings)
  .settings(mainSettings)
  .settings(testSettings)
  .settings(scalaFmtSettings)
  .settings(testSettings)
  .aggregate(
    common
  )

lazy val common = project
  .settings(commonSettings)
  .settings(modulesSettings)
  .settings(
    version := "0.0.1",
    name := "common"
  )

lazy val poc = project
  .settings(commonSettings)
  .settings(modulesSettings)
  .settings(
    name := "poc"
  )
  .dependsOn(
    common % "compile->compile;test->test"
  )
  .settings(
    mainClass := Some("Main")
  )
  .settings(
    mainClass in (Compile, run) := Some("Main")
  )

CommandAliases.aliases
