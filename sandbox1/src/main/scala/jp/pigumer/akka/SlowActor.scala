package jp.pigumer.akka

import akka.actor.Actor

import scala.concurrent.Future

class SlowActor extends Actor {
  override def receive = {
    case i: Int ⇒
      implicit val executionContext = context.system.dispatcher
      val originalSender = sender
      Future {
        Thread.sleep(i * 500)
        originalSender ! i
      }
  }
}

