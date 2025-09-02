package io.confluent.ksql.neo4j;

/**
 * Cypher query templates for Neo4j operations.
 */
public class Neo4jQueryTemplates {
    
    // Node creation templates
    public static final String CREATE_CLUSTER_NODE = 
            "MERGE (c:KSQLCluster {id: $id}) " +
            "SET c.serverVersion = $serverVersion, " +
            "    c.kafkaClusterId = $kafkaClusterId, " +
            "    c.ksqlServiceId = $ksqlServiceId, " +
            "    c.lastUpdated = timestamp()";
    
    public static final String CREATE_STREAM_NODE = 
            "MERGE (s:KSQLStream {name: $name, clusterId: $clusterId}) " +
            "SET s.topic = $topic, " +
            "    s.keyFormat = $keyFormat, " +
            "    s.valueFormat = $valueFormat, " +
            "    s.isWindowed = $isWindowed, " +
            "    s.timestamp = $timestamp, " +
            "    s.partitions = $partitions, " +
            "    s.replicas = $replicas, " +
            "    s.lastUpdated = timestamp()";
    
    public static final String CREATE_TABLE_NODE = 
            "MERGE (t:KSQLTable {name: $name, clusterId: $clusterId}) " +
            "SET t.topic = $topic, " +
            "    t.keyFormat = $keyFormat, " +
            "    t.valueFormat = $valueFormat, " +
            "    t.isWindowed = $isWindowed, " +
            "    t.timestamp = $timestamp, " +
            "    t.partitions = $partitions, " +
            "    t.replicas = $replicas, " +
            "    t.lastUpdated = timestamp()";
    
    public static final String CREATE_TOPIC_NODE = 
            "MERGE (k:KafkaTopic {name: $name, clusterId: $clusterId}) " +
            "SET k.partitions = $partitions, " +
            "    k.replicas = $replicas, " +
            "    k.consumerGroups = $consumerGroups, " +
            "    k.consumerGroupMembers = $consumerGroupMembers, " +
            "    k.lastUpdated = timestamp()";
    
    public static final String CREATE_QUERY_NODE = 
            "MERGE (q:KSQLQuery {id: $id, clusterId: $clusterId}) " +
            "SET q.queryString = $queryString, " +
            "    q.status = $status, " +
            "    q.lastUpdated = timestamp()";
    
    // Relationship creation templates
    public static final String REL_CLUSTER_CONTAINS_STREAM = 
            "MATCH (c:KSQLCluster {id: $clusterId}), " +
            "      (s:KSQLStream {name: $streamName, clusterId: $clusterId}) " +
            "MERGE (c)-[:CONTAINS]->(s)";
    
    public static final String REL_CLUSTER_CONTAINS_TABLE = 
            "MATCH (c:KSQLCluster {id: $clusterId}), " +
            "      (t:KSQLTable {name: $tableName, clusterId: $clusterId}) " +
            "MERGE (c)-[:CONTAINS]->(t)";
    
    public static final String REL_STREAM_BACKED_BY_TOPIC = 
            "MATCH (s:KSQLStream {name: $streamName, clusterId: $clusterId}), " +
            "      (k:KafkaTopic {name: $topicName, clusterId: $clusterId}) " +
            "MERGE (s)-[:BACKED_BY]->(k)";
    
    public static final String REL_TABLE_BACKED_BY_TOPIC = 
            "MATCH (t:KSQLTable {name: $tableName, clusterId: $clusterId}), " +
            "      (k:KafkaTopic {name: $topicName, clusterId: $clusterId}) " +
            "MERGE (t)-[:BACKED_BY]->(k)";
    
    public static final String REL_QUERY_WRITES_TO_STREAM = 
            "MATCH (q:KSQLQuery {id: $queryId, clusterId: $clusterId}), " +
            "      (s:KSQLStream {name: $streamName, clusterId: $clusterId}) " +
            "MERGE (q)-[:WRITES_TO]->(s)";
    
    public static final String REL_QUERY_WRITES_TO_TABLE = 
            "MATCH (q:KSQLQuery {id: $queryId, clusterId: $clusterId}), " +
            "      (t:KSQLTable {name: $tableName, clusterId: $clusterId}) " +
            "MERGE (q)-[:WRITES_TO]->(t)";
    
    // Utility queries
    public static final String CLEAR_CLUSTER_DATA = 
            "MATCH (c:KSQLCluster {id: $clusterId})-[]->(n) DETACH DELETE n";
    
    public static final String DELETE_CLUSTER = 
            "MATCH (c:KSQLCluster {id: $clusterId}) DETACH DELETE c";
    
    private Neo4jQueryTemplates() {
        // Private constructor to prevent instantiation
    }
}