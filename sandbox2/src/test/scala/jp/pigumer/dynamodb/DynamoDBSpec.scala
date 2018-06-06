package jp.pigumer.dynamodb

import java.util.logging.Logger

import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.dynamodbv2.document.DynamoDB
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap
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
    "Count" in {
      logger.info("start")
      val request = new QueryRequest()
        .withTableName("TEST1")
        .withIndexName("GlobalIndex")
        .withSelect("COUNT")
        .withKeyConditionExpression("#H = :H and #R = :R")
        .withExpressionAttributeNames(Map("#H" → "HashKey",
          "#R" → "RangeKey2").asJava)
        .withExpressionAttributeValues(Map(":H" → new AttributeValue().withS(TestTable.hashKeyValue),
          ":R" → new AttributeValue().withS("BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB")).asJava)

      val result = client.query(request)
      logger.info(s"completed: ${result.getCount}")
      result.getCount must_== 8457

      val request2 = request.withExclusiveStartKey(result.getLastEvaluatedKey)
      val result2 = client.query(request2)
      logger.info(s"completed2: ${result2.getCount}")
      result2.getCount must_== 1543
    }

    "Page" in {
      logger.info("start")
      val queryRequest = new QueryRequest()
        .withTableName("TEST1")
        .withIndexName("GlobalIndex")
        .withKeyConditionExpression("#H = :H and #R = :R")
        .withExpressionAttributeNames(Map("#H" → "HashKey",
        "#R" → "RangeKey2").asJava)
        .withExpressionAttributeValues( Map(":H" → new AttributeValue().withS(TestTable.hashKeyValue),
          ":R" → new AttributeValue().withS("CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC")).asJava)
      val result = client.query(queryRequest)
      val s = result.getItems().size
      logger.info(s"size: $s")
      s must_== 8457
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
