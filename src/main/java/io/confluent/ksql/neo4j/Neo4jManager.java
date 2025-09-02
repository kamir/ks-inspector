package io.confluent.ksql.neo4j;

import io.confluent.ksql.inventory.KSQLDBInventory;
import io.confluent.ksql.client.StreamInfo;
import io.confluent.ksql.client.TableInfo;
import io.confluent.ksql.client.TopicInfo;
import io.confluent.ksql.client.QueryInfo;
import io.confluent.ksql.client.ServerInfo;
import org.neo4j.driver.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static org.neo4j.driver.Values.parameters;

/**
 * Manages exporting KSQLDB inventory data to Neo4j.
 */
public class Neo4jManager implements AutoCloseable {
    private static final Logger logger = LoggerFactory.getLogger(Neo4jManager.class);
    
    private final Driver driver;
    private final String uri;
    private final String username;
    private final String password;
    
    public Neo4jManager(String uri, String username, String password) {
        this.uri = uri;
        this.username = username;
        this.password = password;
        this.driver = GraphDatabase.driver(uri, AuthTokens.basic(username, password));
        logger.info("Created Neo4j manager for URI: {}", uri);
    }
    
    /**
     * Export a KSQLDB inventory to Neo4j.
     * 
     * @param inventory The inventory to export
     */
    public void exportInventory(KSQLDBInventory inventory) {
        logger.info("Exporting KSQLDB inventory to Neo4j");
        
        try (Session session = driver.session()) {
            // Clear existing data for this cluster
            clearInventory(inventory.getClusterId());
            
            // Export server info
            exportServerInfo(session, inventory.getServerInfo(), inventory.getClusterId());
            
            // Export topics
            for (TopicInfo topic : inventory.getTopics()) {
                exportTopic(session, topic, inventory.getClusterId());
            }
            
            // Export streams
            for (StreamInfo stream : inventory.getStreams()) {
                exportStream(session, stream, inventory.getClusterId());
            }
            
            // Export tables
            for (TableInfo table : inventory.getTables()) {
                exportTable(session, table, inventory.getClusterId());
            }
            
            // Export queries
            for (QueryInfo query : inventory.getQueries()) {
                exportQuery(session, query, inventory.getClusterId());
            }
            
            // Create relationships
            createRelationships(session, inventory);
            
            logger.info("Successfully exported inventory to Neo4j");
        } catch (Exception e) {
            logger.error("Failed to export inventory to Neo4j", e);
            throw new RuntimeException("Failed to export inventory to Neo4j", e);
        }
    }
    
    /**
     * Clear existing inventory data for a specific cluster.
     * 
     * @param clusterId The cluster ID to clear
     */
    public void clearInventory(String clusterId) {
        logger.info("Clearing inventory data for cluster: {}", clusterId);
        
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                // Delete relationships first
                tx.run("MATCH (c:KSQLCluster {id: $clusterId})-[r]-() DELETE r", 
                       parameters("clusterId", clusterId));
                
                // Delete nodes
                tx.run("MATCH (c:KSQLCluster {id: $clusterId})-[]->(n) DETACH DELETE n", 
                       parameters("clusterId", clusterId));
                
                // Delete cluster node if it exists
                tx.run("MATCH (c:KSQLCluster {id: $clusterId}) DETACH DELETE c", 
                       parameters("clusterId", clusterId));
                
                return null;
            });
        }
        
        logger.info("Cleared inventory data for cluster: {}", clusterId);
    }
    
    private void exportServerInfo(Session session, ServerInfo serverInfo, String clusterId) {
        session.writeTransaction(tx -> {
            tx.run(Neo4jQueryTemplates.CREATE_CLUSTER_NODE,
                   parameters("id", clusterId,
                             "serverVersion", serverInfo.getServerVersion(),
                             "kafkaClusterId", serverInfo.getKafkaClusterId(),
                             "ksqlServiceId", serverInfo.getKsqlServiceId()));
            return null;
        });
    }
    
    private void exportTopic(Session session, TopicInfo topic, String clusterId) {
        session.writeTransaction(tx -> {
            Map<String, Object> params = new HashMap<>();
            params.put("name", topic.getName());
            params.put("clusterId", clusterId);
            
            if (topic.getPartitions() != null) {
                params.put("partitions", topic.getPartitions());
            }
            
            if (topic.getReplicas() != null) {
                params.put("replicas", topic.getReplicas());
            }
            
            if (topic.getConsumerGroups() != null) {
                params.put("consumerGroups", topic.getConsumerGroups());
            }
            
            if (topic.getConsumerGroupMembers() != null) {
                params.put("consumerGroupMembers", topic.getConsumerGroupMembers());
            }
            
            tx.run(Neo4jQueryTemplates.CREATE_TOPIC_NODE, params);
            return null;
        });
    }
    
    private void exportStream(Session session, StreamInfo stream, String clusterId) {
        session.writeTransaction(tx -> {
            Map<String, Object> params = new HashMap<>();
            params.put("name", stream.getName());
            params.put("clusterId", clusterId);
            
            if (stream.getTopic() != null) {
                params.put("topic", stream.getTopic());
            }
            
            if (stream.getKeyFormat() != null) {
                params.put("keyFormat", stream.getKeyFormat());
            }
            
            if (stream.getValueFormat() != null) {
                params.put("valueFormat", stream.getValueFormat());
            }
            
            params.put("isWindowed", stream.isWindowed());
            
            if (stream.getTimestamp() != null) {
                params.put("timestamp", stream.getTimestamp());
            }
            
            if (stream.getPartitions() != null) {
                params.put("partitions", stream.getPartitions());
            }
            
            if (stream.getReplicas() != null) {
                params.put("replicas", stream.getReplicas());
            }
            
            tx.run(Neo4jQueryTemplates.CREATE_STREAM_NODE, params);
            return null;
        });
    }
    
    private void exportTable(Session session, TableInfo table, String clusterId) {
        session.writeTransaction(tx -> {
            Map<String, Object> params = new HashMap<>();
            params.put("name", table.getName());
            params.put("clusterId", clusterId);
            
            if (table.getTopic() != null) {
                params.put("topic", table.getTopic());
            }
            
            if (table.getKeyFormat() != null) {
                params.put("keyFormat", table.getKeyFormat());
            }
            
            if (table.getValueFormat() != null) {
                params.put("valueFormat", table.getValueFormat());
            }
            
            params.put("isWindowed", table.isWindowed());
            
            if (table.getTimestamp() != null) {
                params.put("timestamp", table.getTimestamp());
            }
            
            if (table.getPartitions() != null) {
                params.put("partitions", table.getPartitions());
            }
            
            if (table.getReplicas() != null) {
                params.put("replicas", table.getReplicas());
            }
            
            tx.run(Neo4jQueryTemplates.CREATE_TABLE_NODE, params);
            return null;
        });
    }
    
    private void exportQuery(Session session, QueryInfo query, String clusterId) {
        session.writeTransaction(tx -> {
            Map<String, Object> params = new HashMap<>();
            params.put("id", query.getId());
            params.put("clusterId", clusterId);
            
            if (query.getQueryString() != null) {
                params.put("queryString", query.getQueryString());
            }
            
            if (query.getStatus() != null) {
                params.put("status", query.getStatus());
            }
            
            tx.run(Neo4jQueryTemplates.CREATE_QUERY_NODE, params);
            return null;
        });
    }
    
    private void createRelationships(Session session, KSQLDBInventory inventory) {
        // Link cluster to streams
        for (StreamInfo stream : inventory.getStreams()) {
            session.writeTransaction(tx -> {
                tx.run(Neo4jQueryTemplates.REL_CLUSTER_CONTAINS_STREAM,
                       parameters("clusterId", inventory.getClusterId(),
                                 "streamName", stream.getName()));
                return null;
            });
        }
        
        // Link cluster to tables
        for (TableInfo table : inventory.getTables()) {
            session.writeTransaction(tx -> {
                tx.run(Neo4jQueryTemplates.REL_CLUSTER_CONTAINS_TABLE,
                       parameters("clusterId", inventory.getClusterId(),
                                 "tableName", table.getName()));
                return null;
            });
        }
        
        // Link streams to topics
        for (StreamInfo stream : inventory.getStreams()) {
            if (stream.getTopic() != null && !stream.getTopic().isEmpty()) {
                session.writeTransaction(tx -> {
                    tx.run(Neo4jQueryTemplates.REL_STREAM_BACKED_BY_TOPIC,
                           parameters("streamName", stream.getName(),
                                     "topicName", stream.getTopic(),
                                     "clusterId", inventory.getClusterId()));
                    return null;
                });
            }
        }
        
        // Link tables to topics
        for (TableInfo table : inventory.getTables()) {
            if (table.getTopic() != null && !table.getTopic().isEmpty()) {
                session.writeTransaction(tx -> {
                    tx.run(Neo4jQueryTemplates.REL_TABLE_BACKED_BY_TOPIC,
                           parameters("tableName", table.getName(),
                                     "topicName", table.getTopic(),
                                     "clusterId", inventory.getClusterId()));
                    return null;
                });
            }
        }
        
        // Link queries to sinks (streams/tables)
        for (QueryInfo query : inventory.getQueries()) {
            if (query.getSinks() != null) {
                for (String sink : query.getSinks()) {
                    // Check if sink is a stream
                    boolean isStream = inventory.getStreams().stream()
                            .anyMatch(s -> s.getName().equals(sink));
                    
                    if (isStream) {
                        session.writeTransaction(tx -> {
                            tx.run(Neo4jQueryTemplates.REL_QUERY_WRITES_TO_STREAM,
                                   parameters("queryId", query.getId(),
                                             "streamName", sink,
                                             "clusterId", inventory.getClusterId()));
                            return null;
                        });
                    } else {
                        // Assume it's a table
                        session.writeTransaction(tx -> {
                            tx.run(Neo4jQueryTemplates.REL_QUERY_WRITES_TO_TABLE,
                                   parameters("queryId", query.getId(),
                                             "tableName", sink,
                                             "clusterId", inventory.getClusterId()));
                            return null;
                        });
                    }
                }
            }
        }
    }
    
    /**
     * Verify that the Neo4j connection is working.
     * 
     * @return true if the connection is working, false otherwise
     */
    public boolean isConnectionValid() {
        try {
            driver.verifyConnectivity();
            return true;
        } catch (Exception e) {
            logger.error("Neo4j connection verification failed", e);
            return false;
        }
    }
    
    @Override
    public void close() {
        if (driver != null) {
            driver.close();
            logger.info("Closed Neo4j driver connection");
        }
    }
    
    public String getUri() {
        return uri;
    }
    
    public String getUsername() {
        return username;
    }
}