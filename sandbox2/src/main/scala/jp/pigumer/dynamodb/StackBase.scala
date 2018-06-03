package jp.pigumer.dynamodb

import akka.util.ByteString
import com.amazonaws.services.cloudformation.{AmazonCloudFormationAsync, AmazonCloudFormationAsyncClientBuilder}

trait StackBase {

  protected lazy val amazonCloudFormation: AmazonCloudFormationAsync =
    AmazonCloudFormationAsyncClientBuilder.standard.build

  protected val stackName: String = "test-stack"

  protected val template: ByteString = {
    val is = Thread.currentThread.getContextClassLoader.getResourceAsStream("dynamodb.yaml")
    try {
      ByteString(
        Stream.continually(is.read).takeWhile(_ != -1).map(_.toByte).toArray
      )
    } finally {
      is.close()
    }
  }
}
