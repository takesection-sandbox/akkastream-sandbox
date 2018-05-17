package jp.pigumer.akka

import java.util.logging.Logger

trait Mul {

  val logger = Logger.getLogger("Spec")

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

  def mul(i: Int) = m(x(i), y)
}

object Spec extends App with Mul {

  (1 to 10).flatMap(i ⇒ Stream.fill(3)(i)).foreach { i ⇒
    mul(i)
  }

}
