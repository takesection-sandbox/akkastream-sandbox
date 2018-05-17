package jp.pigumer.akka

import akka.actor.{Actor, ActorSystem, Props}
import akka.event.{Logging, LoggingAdapter}
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.util.Timeout

import scala.concurrent.{Await, ExecutionContextExecutor, Future}
import scala.concurrent.duration._

class Mul extends Actor {

  private val logger = Logging(context.system, this)

  lazy val m: (Int, Int) ⇒ Int = (x, y) ⇒ {
    val r = x * y
    logger.info(s"$x * $y = $r")
    r
  }

  lazy val x: Int ⇒ Int = x ⇒ {
    logger.info(s"x = $x")
    x
  }

  lazy val y: Int = {
    logger.info("y = 3")
    3
  }

  override def receive = {
    case i: Int =>
      val r = m(x(i), y)
      sender ! r
  }
}

object Spec extends App {

  implicit val system: ActorSystem = ActorSystem("CompletionTimeoutSpec")

  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher
  implicit val timeout: Timeout = 1 seconds

  val logger: LoggingAdapter = Logging(system, this.getClass)

  val actor = system.actorOf(Props[Mul])

  (1 to 10).flatMap(i ⇒ Stream(i, i, i)).foreach { i ⇒
    val result: Future[Any] = actor ? i
    val z = Await.result(result, 1 seconds)
    logger.info(z.toString)
  }

  system.terminate()
}
