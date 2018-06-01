package jp.pigumer.akka

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Source}
import akka.stream.testkit.scaladsl.TestSink
import akka.testkit.TestKitBase
import org.specs2.mutable.Specification

class SourceSpec extends Specification with TestKitBase {

  override implicit lazy val system = ActorSystem("Test")
  implicit val materializer = ActorMaterializer()

  "Source" should {
    "sample1" in {
      val source: Source[String, NotUsed] =
        Source(1 to 10).via(Flow[Int].fold(0)((acc, n) ⇒ acc + n).map(_.toString))
      val result = source.runWith(TestSink.probe[String])
      result.request(1).expectNext must_== "55"
    }
  }
}
