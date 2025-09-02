# KSQLDB Client Usage Guide

This guide explains how to use the KSQLDB client implementation in the ks-inspector tool.

## Overview

The KSQLDB client provides a simplified interface for interacting with KSQLDB servers using the official KSQLDB API client. It allows you to:

- Connect to KSQLDB servers
- Retrieve server information
- List streams, tables, topics, and queries
- Describe sources
- Create inventories of KSQLDB clusters

## Configuration

The [KSQLDBConfig](../src/main/java/io/confluent/ksql/client/KSQLDBConfig.java) class is used to configure the client connection:

```java
KSQLDBConfig config = new KSQLDBConfig()
    .setHost("localhost")
    .setPort(8088)
    .setUseTls(false);
```

For authenticated connections:

```java
KSQLDBConfig config = new KSQLDBConfig()
    .setHost("localhost")
    .setPort(8088)
    .setUseTls(false)
    .setUsername("user")
    .setPassword("password");
```

## Basic Usage

### Creating a Client

```java
try (KSQLDBClient client = new KSQLDBClient(config)) {
    // Use the client
}
```

### Retrieving Server Information

```java
ServerInfo serverInfo = client.getServerInfo();
System.out.println("Server Version: " + serverInfo.getServerVersion());
System.out.println("Kafka Cluster ID: " + serverInfo.getKafkaClusterId());
System.out.println("KSQL Service ID: " + serverInfo.getKsqlServiceId());
```

### Listing Streams

```java
List<StreamInfo> streams = client.listStreams();
for (StreamInfo stream : streams) {
    System.out.println("Stream: " + stream.getName());
    System.out.println("  Topic: " + stream.getTopic());
    System.out.println("  Key Format: " + stream.getKeyFormat());
    System.out.println("  Value Format: " + stream.getValueFormat());
    System.out.println("  Is Windowed: " + stream.isWindowed());
}
```

### Listing Tables

```java
List<TableInfo> tables = client.listTables();
for (TableInfo table : tables) {
    System.out.println("Table: " + table.getName());
    System.out.println("  Topic: " + table.getTopic());
    System.out.println("  Key Format: " + table.getKeyFormat());
    System.out.println("  Value Format: " + table.getValueFormat());
    System.out.println("  Is Windowed: " + table.isWindowed());
}
```

### Listing Topics

```java
List<TopicInfo> topics = client.listTopics();
for (TopicInfo topic : topics) {
    System.out.println("Topic: " + topic.getName());
    System.out.println("  Partitions: " + topic.getPartitions());
    System.out.println("  Replicas: " + topic.getReplicas());
}
```

### Listing Queries

```java
List<QueryInfo> queries = client.listQueries();
for (QueryInfo query : queries) {
    System.out.println("Query ID: " + query.getId());
    System.out.println("  SQL: " + query.getQueryString());
    System.out.println("  Status: " + query.getStatus());
}
```

## Error Handling

All client methods can throw [KSQLException](../src/main/java/io/confluent/ksql/client/KSQLException.java) which wraps the underlying exceptions:

```java
try {
    List<StreamInfo> streams = client.listStreams();
} catch (KSQLException e) {
    System.err.println("Failed to list streams: " + e.getMessage());
    e.printStackTrace();
}
```

## Integration with Inventory System

The KSQLDB client is designed to work with the inventory system:

```java
InventoryManager inventoryManager = new InventoryManager();
KSQLDBInventory inventory = inventoryManager.createInventory(client);
inventoryManager.saveInventory(inventory, "ksqldb-inventory.yaml");
```

## Running the Example

To run the example application:

```bash
mvn exec:java -Dexec.mainClass="io.confluent.ksql.client.KSQLDBClientExample"
```

## Testing

Unit tests can be run with:

```bash
mvn test
```

Integration tests that require a running KSQLDB server are disabled by default. To run them, you need to:

1. Start a KSQLDB server
2. Remove the `@Disabled` annotation from the integration tests
3. Run the tests

## Dependencies

The KSQLDB client uses the official KSQLDB API client:

```xml
<dependency>
    <groupId>io.confluent.ksql</groupId>
    <artifactId>ksqldb-api-client</artifactId>
    <version>7.6.0</version>
</dependency>
```