package jp.pigumer.akka

import akka.actor.{Actor, ActorSystem, Props}
import akka.event.{Logging, LoggingAdapter}
import akka.stream.scaladsl.{Flow, Keep, Sink, Source}
import akka.stream.{ActorMaterializer, Attributes}
import akka.util.Timeout

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.util.{Failure, Success}


object CompletionTimeoutSpec extends App {

  implicit val system: ActorSystem = ActorSystem("CompletionTimeoutSpec")

  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  val logger: LoggingAdapter = Logging(system, this.getClass)

  val ref = system.actorOf(Props[SlowActor])
  implicit val timeout: Timeout = 60 seconds

  val source = Source(Stream.from(1))
    .log("source")
    .withAttributes(Attributes.logLevels(onElement = Logging.InfoLevel))
  val flow = Flow[Int].ask[Int](ref)
    .completionTimeout(3 seconds)
    .log("flow", elem ⇒ s"$elem")
    .withAttributes(Attributes.logLevels(onElement = Logging.InfoLevel))

  val done =
    source
      .via(flow).toMat(Sink.ignore)(Keep.right)
      .run()

  done.onComplete {
    case Success(_) ⇒
      logger.info("success")
      system.terminate()
    case Failure(cause) ⇒
      logger.error(cause, "error")
      system.terminate()
  }
}
