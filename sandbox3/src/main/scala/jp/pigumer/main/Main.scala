package jp.pigumer.main

import java.nio.charset.StandardCharsets

import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Keep, Sink, Source}
import akka.util.ByteString

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContextExecutor, Future}
import scala.util.{Failure, Success}

object Main extends App {
  implicit val system: ActorSystem = ActorSystem("Sandbox3")

  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  val logger: LoggingAdapter = Logging(system, "Sandbox3")

  val source = Source(List(
    ByteString("foo\n"),
    ByteString("bar\n"),
    ByteString("baz\n")
  ))
  val result = for {
    byteString <- source.toMat(Sink.fold(ByteString())((a, b) => a ++ b))(Keep.right).run()
    array <- Future(byteString.toArray)
    _ <- Future(println(new String(array, StandardCharsets.UTF_8)))
  } yield ()
  result.onComplete {
    case Success(_) =>
      system.terminate()
    case Failure(throwable) =>
      throwable.printStackTrace
      system.terminate()
  }
  Await.ready(result, 10 seconds)
}
