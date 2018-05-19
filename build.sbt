import Dependencies._
import sbt.Keys._

lazy val root = (project in file("."))
  .enablePlugins(JavaAppPackaging, AshScriptPlugin, DockerPlugin)
  .settings(
    name := "sandbox",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "2.12.4",
    libraryDependencies ++= Seq(
      akkaHttp,
      akkaStream,
    ),
    dockerBaseImage := "java:8-jdk-alpine",
    daemonUser in Docker := "root",
    mainClass in assembly := Some("jp.pigumer.akka.HelloWorld")
  )
