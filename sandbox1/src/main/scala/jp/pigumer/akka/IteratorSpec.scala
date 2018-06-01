package jp.pigumer.akka

import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Keep, Sink, Source}

import scala.concurrent.ExecutionContextExecutor
import scala.util.Success

object IteratorSpec extends App {

  implicit val system: ActorSystem = ActorSystem("IteratorSpec")

  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  val logger: LoggingAdapter = Logging(system, this.getClass)

  val iterator: Iterator[Int] = new Iterator[Int] {

    var n = 0

    override def hasNext: Boolean = true
    override def next(): Int = {
      n = n + 1
      n
    }
  }

  val result = Source.fromIterator(() ⇒ iterator).takeWhile(_ != 5).toMat(Sink.seq)(Keep.right).run

  result.onComplete {
    case Success(r) ⇒
      logger.info(s"$r")
      system.terminate()
    case _ ⇒
      system.terminate()
  }
}
