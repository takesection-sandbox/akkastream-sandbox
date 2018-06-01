package jp.pigumer.akka

import akka.NotUsed
import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import akka.stream._
import akka.stream.scaladsl.{Keep, RestartSource, Sink, Source}

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration._
import scala.util.{Failure, Success}

object RetrySpec extends App {

  implicit val system: ActorSystem = ActorSystem("RetrySpec")

  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  val logger: LoggingAdapter = Logging(system, this.getClass)

  val source: Source[Int, NotUsed] = RestartSource.withBackoff(
    minBackoff = 1 seconds,
    maxBackoff = 1 seconds,
    randomFactor = 0.2,
    maxRestarts = 2
  ) { () ⇒
    Source.single(3)
      .log("source")
      .withAttributes(Attributes.logLevels(onElement = Logging.InfoLevel))
      .map {
        case i @ 3 ⇒
          logger.info(s"retry: $i")
          throw new RuntimeException()
        case i ⇒
          i
      }
  }

  val done =
    source
      .toMat(Sink.ignore)(Keep.right)
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
