package jp.pigumer.akka

import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import akka.stream.scaladsl.{Flow, Keep, Sink, Source}
import akka.stream.{ActorMaterializer, Attributes, DelayOverflowStrategy, KillSwitches}

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration._
import scala.util.{Failure, Success}

object KillSwitchSpec extends App {

  implicit val system: ActorSystem = ActorSystem("KillSwitchSpec")

  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  val logger: LoggingAdapter = Logging(system, this.getClass)

  val source = Source(Stream.from(1))
    .delay(1 seconds, DelayOverflowStrategy.backpressure)
    .log("source")
    .withAttributes(Attributes.logLevels(onElement = Logging.InfoLevel))
  val killSwitch = KillSwitches.single[Int]
  val flow = Flow[Int].map { i ⇒
    i
  }
    .log("flow", elem ⇒ s"[$elem]")
    .withAttributes(Attributes.logLevels(onElement = Logging.InfoLevel))

  val (k, done) =
    source
      .viaMat(killSwitch)(Keep.right)
      .via(flow).toMat(Sink.ignore)(Keep.both)
      .run()

  done.onComplete {
    case Success(_) ⇒
      logger.info("success")
      system.terminate()
    case Failure(cause) ⇒
      logger.error(cause, "error")
      system.terminate()
  }

  Thread.sleep(2000)
  k.shutdown()
}
