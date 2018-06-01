import sbt._

object Dependencies {
  lazy val akkaHttp = "com.typesafe.akka" %% "akka-http"   % "10.1.1"
  lazy val akkaStream = "com.typesafe.akka" %% "akka-stream" % "2.5.11"
  lazy val akkaStreamTestKit = "com.typesafe.akka" %% "akka-stream-testkit" % "2.5.11" % Test

  lazy val logback = "ch.qos.logback" % "logback-classic" % "1.2.3"

  lazy val specs2 = "org.specs2" %% "specs2-core" % "4.2.0" % Test

  lazy val dynamodb = "com.amazonaws" % "aws-java-sdk-dynamodb" % "1.11.338"

}