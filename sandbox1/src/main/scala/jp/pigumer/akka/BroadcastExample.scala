package jp.pigumer.akka

import akka.NotUsed
import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import akka.stream.{ActorMaterializer, ClosedShape, UniformFanOutShape}
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, RunnableGraph, Sink, Source, ZipWith, ZipWithN}

import scala.concurrent.{ExecutionContextExecutor, Future}

object BroadcastExample extends App {

  implicit val system: ActorSystem = ActorSystem("HelloWorld")

  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  val logger: LoggingAdapter = Logging(system, "HelloWorld")

  val source: Source[Int, NotUsed] = Source(1 to 10)
  val flowA: Flow[Int, Int, NotUsed] = Flow[Int].map(a ⇒ a)
  val flowB: Flow[Int, Int, NotUsed] = Flow[Int].map(b ⇒ b)
  val flowC: Flow[Int, Int, NotUsed] = Flow[Int].map(c ⇒ c)
  val sink: Sink[Int, Future[Int]] = Sink.fold[Int, Int](0)(_ + _)

  val runnable: RunnableGraph[Future[Int]] =
    RunnableGraph.fromGraph(GraphDSL.create(sink) { implicit b ⇒sink ⇒
      import GraphDSL.Implicits._

      val bcast = b.add(Broadcast[Int](3))
      val zip = b.add(ZipWith[Int, Int, Int, Int] { (a, b, c) ⇒
        a
      })

      source ~> bcast.in

      bcast.out(0) ~> flowA.async ~> zip.in0
      bcast.out(1) ~> flowB.async ~> zip.in1
      bcast.out(2) ~> flowC.async ~> zip.in2

      zip.out ~> sink

      ClosedShape
  })
}
