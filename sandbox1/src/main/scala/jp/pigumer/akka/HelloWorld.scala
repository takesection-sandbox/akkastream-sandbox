package jp.pigumer.akka

import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import akka.stream.scaladsl.{Flow, Keep, Sink, Source}
import akka.stream.{ActorMaterializer, Attributes}

import scala.concurrent.ExecutionContextExecutor
import scala.util.{Failure, Success}

object HelloWorld extends App {

  implicit val system: ActorSystem = ActorSystem("HelloWorld")

  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  val logger: LoggingAdapter = Logging(system, "HelloWorld")

  val source = Source(1 to 10).groupBy(3, _ % 3).async
    .log("source")
    .withAttributes(Attributes.logLevels(onElement = Logging.InfoLevel))
  val flow = Flow[Int].map { i ⇒
    Thread.sleep((10 - i) * 200)
    i
  }.async
    .log("flow", elem ⇒ s"[$elem]")
    .withAttributes(Attributes.logLevels(onElement = Logging.InfoLevel))

  val runnable =
    source.via(flow).mergeSubstreams.toMat(Sink.ignore)(Keep.right)

  runnable.run().onComplete {
    case Success(_) ⇒
      logger.info("success")
      system.terminate()
    case Failure(cause) ⇒
      logger.error(cause, "run")
      system.terminate()
  }
}
