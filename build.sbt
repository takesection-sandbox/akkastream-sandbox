import Dependencies._
import sbt.Keys._

lazy val commonSettings = Seq(
  version := "0.0.1-SNAPSHOT",
  scalaVersion := "2.12.6"
)

lazy val root = (project in file("."))
  .aggregate(
    sandbox1,
    sandbox2)

lazy val sandbox1 = (project in file("sandbox1"))
  .enablePlugins(JavaAppPackaging, AshScriptPlugin, DockerPlugin)
  .settings(commonSettings: _*)
  .settings(
    name := "sandbox",
    libraryDependencies ++= Seq(
      akkaHttp,
      akkaStream,
      akkaStreamTestKit,

      logback,

      specs2
    ),
    dockerBaseImage := "java:8-jdk-alpine",
    daemonUser in Docker := "root",
    mainClass in assembly := Some("jp.pigumer.akka.HelloWorld")
  )

lazy val sandbox2 = (project in file("sandbox2"))
  .settings(commonSettings: _*)
  .settings(
    name := "sandbox2",
    libraryDependencies ++= Seq(
      dynamodb,
      specs2
    )
  )
