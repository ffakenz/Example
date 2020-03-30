import sbt.Keys._
import org.scalafmt.sbt.ScalafmtPlugin.autoImport.{scalafmtConfig, scalafmtOnCompile}
import sbt._

object Settings extends Dependencies with CommonScalac {
  val modulesSettings = Seq(
    scalacOptions ++= scalacSettings,
    scalaVersion := scalaVersionUsed,
    resolvers ++= commonResolvers,
    libraryDependencies ++= mainDeps,
    libraryDependencies ++= testDeps map (_ % Test)
  )

  lazy val scalaFmtSettings = Seq(
    scalafmtOnCompile := true,
    scalafmtConfig := file(".scalafmt.conf")
  )

  lazy val testSettings = Seq(
    Test / parallelExecution := false,
    Test / fork := true,
    Test / javaOptions += "-Xmx2G",
    triggeredMessage := Watched.clearWhenTriggered,
    autoStartServer := false,
    shellPrompt := (_ => name.value)
  )

  lazy val mainSettings = Seq(
    fork in run := true, // Calling sbt with -D parameters does not have effects because of `true`
    mainClass in (Compile, run) := Some("Main")
  )

  scalacOptions ++= Seq(
    "-feature",
    "-unchecked",
    "-language:higherKinds",
    "-language:postfixOps",
    "-deprecation"
  ) ++ scalacSettings
}
