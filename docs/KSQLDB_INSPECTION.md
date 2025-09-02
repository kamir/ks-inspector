# KSQLDB Cluster Inspection

## Overview

The KSQLDB cluster inspection functionality allows you to collect comprehensive metadata from KSQLDB servers, store this metadata in structured YAML format, and export it to Neo4j for advanced analysis and visualization.

## Features

1. **Comprehensive Metadata Collection**: Collect information about streams, tables, topics, and queries
2. **Structured Inventory Storage**: Store metadata in human-readable YAML format
3. **Neo4j Integration**: Export inventory data to Neo4j for advanced querying
4. **CLI Integration**: Easy-to-use command-line interface

## Usage

### Basic Inspection

To perform a basic inspection of a KSQLDB cluster:

```bash
java -cp target/ks-inspector-2.6.1.jar \
    io.confluent.mdgraph.cli.CLI inspectKSQLDB \
    --host localhost \
    --port 8088
```

### Inspection with Output File

To save the inspection results to a YAML file:

```bash
java -cp target/ks-inspector-2.6.1.jar \
    io.confluent.mdgraph.cli.CLI inspectKSQLDB \
    --host localhost \
    --port 8088 \
    --output ksqldb-inventory.yaml
```

### Inspection with Authentication

To inspect a KSQLDB cluster that requires authentication:

```bash
java -cp target/ks-inspector-2.6.1.jar \
    io.confluent.mdgraph.cli.CLI inspectKSQLDB \
    --host localhost \
    --port 8088 \
    --username ksql \
    --password secret \
    --tls
```

### Inspection with Neo4j Export

To export the inspection results directly to Neo4j:

```bash
java -cp target/ks-inspector-2.6.1.jar \
    io.confluent.mdgraph.cli.CLI inspectKSQLDB \
    --host localhost \
    --port 8088 \
    --output ksqldb-inventory.yaml \
    --neo4j-uri bolt://localhost:7687 \
    --neo4j-user neo4j \
    --neo4j-password admin
```

## Command Line Options

| Option | Description | Default |
|--------|-------------|---------|
| `-h`, `--host` | KSQLDB server host | localhost |
| `-p`, `--port` | KSQLDB server port | 8088 |
| `-u`, `--username` | KSQLDB username | None |
| `-pw`, `--password` | KSQLDB password | None |
| `--tls` | Use TLS for connection | false |
| `-o`, `--output` | Output file path for YAML inventory | None |
| `--neo4j-uri` | Neo4j URI for export | None |
| `--neo4j-user` | Neo4j username | None |
| `--neo4j-password` | Neo4j password | None |

## Inventory Structure

The generated YAML inventory contains the following structure:

```yaml
clusterId: "ksql-service-id"
collectedAt: "2025-09-02T10:30:00"
serverInfo:
  serverVersion: "0.29.0"
  kafkaClusterId: "cluster-1"
  ksqlServiceId: "ksql-service-1"
  host: "localhost"
  port: 8088

streams:
  - name: "pageviews_stream"
    topic: "pageviews"
    keyFormat: "KAFKA"
    valueFormat: "JSON"
    isWindowed: false
    timestamp: "ROWTIME"
    partitions: 6
    replicas: 3

tables:
  - name: "users_table"
    topic: "users"
    keyFormat: "KAFKA"
    valueFormat: "AVRO"
    isWindowed: false
    timestamp: null
    partitions: 6
    replicas: 3

topics:
  - name: "pageviews"
    partitions: 6
    replicas: 3
    consumerGroups: 2
    consumerGroupMembers: 4

queries:
  - id: "CSAS_PAGEVIEWS_STREAM_0"
    queryString: "CREATE STREAM pageviews_stream WITH (KAFKA_TOPIC='pageviews', VALUE_FORMAT='JSON');"
    status: "RUNNING"
    sinks: ["pageviews_stream"]
    sinkKafkaTopics: ["pageviews"]
```

## Neo4j Graph Model

When exporting to Neo4j, the following node labels and relationships are created:

### Node Labels
- `:KSQLCluster` - Represents a KSQLDB cluster
- `:KSQLStream` - Represents a KSQLDB stream
- `:KSQLTable` - Represents a KSQLDB table
- `:KafkaTopic` - Represents a Kafka topic
- `:KSQLQuery` - Represents a KSQLDB query

### Relationship Types
- `(:KSQLCluster)-[:CONTAINS]->(:KSQLStream)` - Cluster contains stream
- `(:KSQLCluster)-[:CONTAINS]->(:KSQLTable)` - Cluster contains table
- `(:KSQLStream)-[:BACKED_BY]->(:KafkaTopic)` - Stream is backed by topic
- `(:KSQLTable)-[:BACKED_BY]->(:KafkaTopic)` - Table is backed by topic
- `(:KSQLQuery)-[:WRITES_TO]->(:KSQLStream)` - Query writes to stream
- `(:KSQLQuery)-[:WRITES_TO]->(:KSQLTable)` - Query writes to table

## Example Queries

After exporting to Neo4j, you can run the following queries:

### Find all streams backed by a specific topic
```cypher
MATCH (s:KSQLStream)-[:BACKED_BY]->(t:KafkaTopic {name: 'pageviews'})
RETURN s.name
```

### Find all queries that write to a specific stream
```cypher
MATCH (q:KSQLQuery)-[:WRITES_TO]->(s:KSQLStream {name: 'pageviews_stream'})
RETURN q.id, q.queryString
```

### Find the complete lineage of a stream
```cypher
MATCH path = (t:KafkaTopic {name: 'pageviews'})<-[:BACKED_BY]-(s:KSQLStream)<-[:WRITES_TO]-(q:KSQLQuery)
RETURN path
```

## Troubleshooting

### Connection Issues
- Verify that the KSQLDB server is running and accessible
- Check that the host and port are correct
- Ensure that authentication credentials are correct if required

### Neo4j Export Issues
- Verify that Neo4j is running and accessible
- Check that the Neo4j credentials are correct
- Ensure that the Neo4j database is not in read-only mode

### Performance Issues
- For large clusters, consider increasing the timeout values
- If the inspection takes too long, try inspecting specific components separately