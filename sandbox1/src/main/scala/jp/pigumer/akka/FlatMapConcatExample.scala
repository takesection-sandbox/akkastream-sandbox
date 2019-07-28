package jp.pigumer.akka

import akka.NotUsed
import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Keep, RunnableGraph, Sink, Source}

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContextExecutor, Future}

object FlatMapConcatExample extends App {

  implicit val system: ActorSystem = ActorSystem("HelloWorld")

  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  val logger: LoggingAdapter = Logging(system, "HelloWorld")

  val source: Source[Int, NotUsed] = Source(1 to 9)
  val flow: Flow[Int, Int, NotUsed] = Flow[Int].map { x ⇒
    x * 2
  }
  val sink: Sink[Int, Future[Int]] = Sink.fold[Int, Int](0)(_ + _)

  val maxSubstreams = 4
  val runnableGraph: RunnableGraph[Future[Int]] =
    source.groupBy(maxSubstreams, _ % maxSubstreams)
      .via(flow.async)
      .mergeSubstreams
      .toMat(sink)(Keep.right)
  val result = runnableGraph.run()
  Await.ready(result, 10 seconds)

  logger.info(s"${result.value.get}")
}
