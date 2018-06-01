package jp.pigumer.dynamodb

import java.util.logging.Logger

import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.dynamodbv2.document.DynamoDB
import com.amazonaws.services.dynamodbv2.model.{AttributeValue, QueryRequest}
import com.amazonaws.services.dynamodbv2.{AmazonDynamoDB, AmazonDynamoDBClientBuilder}
import org.specs2.mutable.Specification
import org.specs2.specification.BeforeAfterAll

import scala.collection.JavaConverters._

class DynamoDBSpec extends Specification with BeforeAfterAll {

  val logger = Logger.getLogger(this.getClass.getName)

  lazy val client: AmazonDynamoDB = AmazonDynamoDBClientBuilder
    .standard()
    .withEndpointConfiguration(
      new AwsClientBuilder.EndpointConfiguration("http://localhost:8000", "ap-northeast-1"))
    .build()
  lazy val dynamoDB = new DynamoDB(client)

  "Test" should {
    "test" in {
      logger.info("start")
      val request = new QueryRequest()
        .withTableName("TEST1")
        .withIndexName("GlobalIndex")
        .withSelect("COUNT")
        .withKeyConditionExpression("#H = :H and #R = :R")
        .withExpressionAttributeNames(Map("#H" → "HashKey",
          "#R" → "RangeKey2").asJava)
        .withExpressionAttributeValues(Map(":H" → new AttributeValue().withS("TEST"),
          ":R" → new AttributeValue().withS("B")).asJava)

      val result = client.query(request)
      logger.info(s"completed: ${result.getCount}")
      result.getCount must_== 3000
    }
  }

  override def beforeAll: Unit = {
    TestTable.createTable(dynamoDB)
    TestTable.saveTestData(dynamoDB)
  }

  override def afterAll: Unit = {
    TestTable.deleteTable(dynamoDB)
  }
}
