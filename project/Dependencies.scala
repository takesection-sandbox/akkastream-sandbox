import sbt._

object Dependencies {
  private val AkkaStreamVersion = "2.5.11"

  lazy val akkaHttp = "com.typesafe.akka" %% "akka-http" % "10.1.1"
  lazy val akkaStream = "com.typesafe.akka" %% "akka-stream" % AkkaStreamVersion
  lazy val akkaStreamTestKit = "com.typesafe.akka" %% "akka-stream-testkit" % AkkaStreamVersion % Test

  lazy val logback = "ch.qos.logback" % "logback-classic" % "1.2.3"

  lazy val specs2 = "org.specs2" %% "specs2-core" % "4.2.0" % Test

  private val AwsJavaSdkVersion = "1.11.338"
  lazy val dynamodb = "com.amazonaws" % "aws-java-sdk-dynamodb" % AwsJavaSdkVersion
  lazy val cloudformation = "com.amazonaws" % "aws-java-sdk-cloudformation" % AwsJavaSdkVersion
}
