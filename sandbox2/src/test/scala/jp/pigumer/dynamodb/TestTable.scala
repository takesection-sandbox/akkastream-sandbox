package jp.pigumer.dynamodb

import java.util.UUID

import com.amazonaws.services.dynamodbv2.document.{DynamoDB, Item}
import com.amazonaws.services.dynamodbv2.model._

import scala.util.Try

case class Data(hashKey: String,
                rangeKey: String,
                rangeKey2: String) {
  val item = new Item()
    .withPrimaryKey("HashKey", hashKey,
      "RangeKey", rangeKey)
    .withString("RangeKey2", rangeKey2)
}

object TestTable {

  def deleteTable(dynamoDB: DynamoDB) = {
    dynamoDB.getTable("TEST1").delete()
  }

  def createTable(dynamoDB: DynamoDB) = {
    val request = new CreateTableRequest()
      .withTableName("TEST1")
      .withAttributeDefinitions(
        new AttributeDefinition()
          .withAttributeName("HashKey")
          .withAttributeType("S"),
        new AttributeDefinition()
          .withAttributeName("RangeKey")
          .withAttributeType("S"),
        new AttributeDefinition()
          .withAttributeName("RangeKey2")
          .withAttributeType("S")
      )
      .withKeySchema(
        new KeySchemaElement()
          .withAttributeName("HashKey")
          .withKeyType("HASH"),
        new KeySchemaElement()
          .withAttributeName("RangeKey")
          .withKeyType("RANGE")
      )
      .withProvisionedThroughput(
        new ProvisionedThroughput()
          .withReadCapacityUnits(1L)
          .withWriteCapacityUnits(1L)
      )
      .withGlobalSecondaryIndexes(
        new GlobalSecondaryIndex()
          .withIndexName("GlobalIndex")
          .withKeySchema(
            new KeySchemaElement()
              .withAttributeName("HashKey")
              .withKeyType("HASH"),
            new KeySchemaElement()
              .withAttributeName("RangeKey2")
              .withKeyType("RANGE")
          )
          .withProjection(
            new Projection().withProjectionType("ALL")
          )
          .withProvisionedThroughput(
            new ProvisionedThroughput()
              .withReadCapacityUnits(1L)
              .withWriteCapacityUnits(1L)
          )
      )
    val result: TableDescription = dynamoDB.createTable(request).waitForActive()
    println(result)
  }

  def saveTestData(dynamoDB: DynamoDB) = Try {
    val table = dynamoDB.getTable("TEST1")
    for (_ <- 1 to 1000) {
      table.putItem(Data("TEST", UUID.randomUUID().toString, "A").item)
    }
    for (_ <- 1 to 2000) {
      table.putItem(Data("TEST", UUID.randomUUID().toString, "C").item)
    }
    for (_ <- 1 to 3000) {
      table.putItem(Data("TEST", UUID.randomUUID().toString, "B").item)
    }
  }.get
}
