package jp.pigumer.akka

import akka.actor.{ActorSystem, Props}
import akka.event.Logging
import akka.util.Timeout
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

import scala.concurrent.duration._
import akka.pattern._

import scala.concurrent.Await

class FutureSpec extends Specification {

  trait WithFixture extends Scope {
    implicit val system = ActorSystem("FutureSpec")
    implicit val logger = Logging(system, classOf[WithFixture])

    val actor = system.actorOf(Props[SlowActor])
  }

  "Future Test" should {
    "Success" in new WithFixture {
      implicit val timeout: Timeout = 1 seconds

      logger.info("start")
      val e = actor ? 1
      Await.ready(e, 60 minutes)
      logger.info("finish")

      e.value.get.fold(
        cause ⇒ {
            logger.error(cause, "failed")
            failure
          },
        _ ⇒ success
      )
    }

    "Timeout" in new WithFixture {
      implicit val timeout: Timeout = 1 seconds

      logger.info("start")
      val e = actor ? 4
      Await.ready(e, 5 seconds)
      logger.info("finish")

      e.value.get.fold(
        cause ⇒ {
          logger.error(cause, "failed")
          success
        },
        _ ⇒ failure
      )
    }
  }
}
