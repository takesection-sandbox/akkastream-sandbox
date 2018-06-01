package jp.pigumer.akka

import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Keep, Sink, Source}

import scala.concurrent.ExecutionContextExecutor
import scala.util.Success

object PaginatorSpec extends App {

  implicit val system: ActorSystem = ActorSystem("PaginatorSpec")

  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  val logger: LoggingAdapter = Logging(system, this.getClass)

  val list = Stream.from(1)

  val page = 2L
  val size = 3

  val result = Source(list)
    .grouped(size)
    .zipWithIndex
    .dropWhile {
      case (_, index) ⇒
        index != (page - 1L)
    }
    .takeWhile {
      case (_, index) ⇒
        index != page
    }
    .map(_._1)
    .toMat(Sink.head)(Keep.right)
    .run

  result.onComplete {
    case Success(values) ⇒
      logger.info(s"complated: $values")
      system.terminate()
    case _ ⇒
      system.terminate()
  }

}