package jp.pigumer.akka

import akka.actor.ActorSystem
import akka.event.Logging
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, Sink, Source, ZipWith}
import akka.stream.{ActorMaterializer, Attributes, FlowShape}
import akka.testkit.TestKitBase
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

import scala.concurrent.Await
import scala.concurrent.duration._

class DeadlockSpec extends Specification with TestKitBase {
  override implicit lazy val system = ActorSystem("Deadlock")

  "Test" should {
    "test1" in new WithFixture {
      val executionContext = system.dispatcher
      val done = Source.single(1).
        flatMapConcat { _ ⇒
          Source(1 to 5).groupBy(5, _ % 5)
            .flatMapConcat { _ ⇒
              Source(1 to 5).groupBy(5, _ % 5)
                .via(broadcastFlow.async)
                .mergeSubstreams
            }
            .mergeSubstreams
        }.runWith(Sink.ignore)

      Await.ready(done, 1 hours)
      done.value.get.get
      success
    }
  }

  trait WithFixture extends Scope {
    implicit val materializer = ActorMaterializer()

    val broadcastFlow =
      Flow.fromGraph(GraphDSL.create() { implicit b ⇒
        import GraphDSL.Implicits._

        val bc = b.add(Broadcast[Int](5))
        val w = (name: String) ⇒ Flow[Int].map { n ⇒
          Thread.sleep(1000)
          s"$n - ${Thread.currentThread.getName}"
        }
          .log(name).withAttributes(Attributes.logLevels(onElement = Logging.InfoLevel))
          .async

        val zip = b.add(ZipWith[String, String, String, String, String,
          (String, String, String, String, String)]((_, _, _, _, _)))

        bc.out(0) ~> w("waiting 0") ~> zip.in0
        bc.out(1) ~> w("waiting 1") ~> zip.in1
        bc.out(2) ~> w("waiting 2") ~> zip.in2
        bc.out(3) ~> w("waiting 3") ~> zip.in3
        bc.out(4) ~> w("waiting 4") ~> zip.in4

        FlowShape(bc.in, zip.out)
      })
  }
}
