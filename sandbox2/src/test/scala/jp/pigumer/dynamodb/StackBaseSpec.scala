package jp.pigumer.dynamodb

import akka.util.ByteString
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

class StackBaseSpec extends Specification {

  "StackBase" should {
    "template" in new WithFixture {
      template match {
        case bytes: ByteString ⇒
          println(new String(bytes.toArray))
          success
        case _ ⇒
          failure
      }
    }
  }

  trait WithFixture extends Scope with StackBase {
  }

}
