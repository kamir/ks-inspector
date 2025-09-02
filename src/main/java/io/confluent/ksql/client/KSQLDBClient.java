package io.confluent.ksql.client;

import io.confluent.ksql.api.client.*;
import io.confluent.ksql.security.AuthType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Wrapper class for the official KSQLDB API client.
 * Provides a simplified interface for common KSQLDB operations.
 */
public class KSQLDBClient implements AutoCloseable {
    private static final Logger logger = LoggerFactory.getLogger(KSQLDBClient.class);
    
    private final Client client;
    private final KSQLDBConfig config;
    
    /**
     * Create a new KSQLDB client with the given configuration.
     * 
     * @param config The configuration for the KSQLDB connection
     */
    public KSQLDBClient(KSQLDBConfig config) {
        this.config = config;
        ClientOptions options = ClientOptions.create()
                .setHost(config.getHost())
                .setPort(config.getPort())
                .setUseTls(config.isUseTls());
                
        if (config.getUsername() != null && !config.getUsername().isEmpty()) {
            options = options.setBasicAuthCredentials(config.getUsername(), config.getPassword());
        }
        
        this.client = Client.create(options);
        logger.info("Created KSQLDB client for server: {}", config.getServerAddress());
    }
    
    /**
     * Get information about the KSQLDB server.
     * 
     * @return Server information
     * @throws KSQLException if there is an error retrieving server information
     */
    public ServerInfo getServerInfo() throws KSQLException {
        try {
            logger.debug("Retrieving server information");
            CompletableFuture<io.confluent.ksql.api.client.ServerInfo> serverInfoFuture = client.serverInfo();
            io.confluent.ksql.api.client.ServerInfo apiServerInfo = serverInfoFuture.get(30, TimeUnit.SECONDS);
            
            ServerInfo serverInfo = new ServerInfo();
            serverInfo.setServerVersion(apiServerInfo.getServerVersion());
            serverInfo.setKafkaClusterId(apiServerInfo.getKafkaClusterId());
            serverInfo.setKsqlServiceId(apiServerInfo.getKsqlServiceId());
            serverInfo.setHost(config.getHost());
            serverInfo.setPort(config.getPort());
            
            return serverInfo;
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new KSQLException("Failed to retrieve server information", e);
        }
    }
    
    /**
     * List all streams in the KSQLDB server.
     * 
     * @return List of stream information
     * @throws KSQLException if there is an error retrieving streams
     */
    public List<StreamInfo> listStreams() throws KSQLException {
        try {
            logger.debug("Listing streams");
            CompletableFuture<List<io.confluent.ksql.api.client.StreamInfo>> streamsFuture = client.listStreams();
            List<io.confluent.ksql.api.client.StreamInfo> apiStreamInfos = streamsFuture.get(30, TimeUnit.SECONDS);
            
            return apiStreamInfos.stream()
                    .map(streamInfo -> {
                        StreamInfo info = new StreamInfo();
                        info.setName(streamInfo.getName());
                        info.setTopic(streamInfo.getTopic());
                        info.setKeyFormat(streamInfo.getKeyFormat());
                        info.setValueFormat(streamInfo.getValueFormat());
                        info.setWindowed(streamInfo.isWindowed());
                        // Note: Timestamp, partitions, and replicas are not available in the API response
                        return info;
                    })
                    .toList();
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new KSQLException("Failed to list streams", e);
        }
    }
    
    /**
     * List all tables in the KSQLDB server.
     * 
     * @return List of table information
     * @throws KSQLException if there is an error retrieving tables
     */
    public List<TableInfo> listTables() throws KSQLException {
        try {
            logger.debug("Listing tables");
            CompletableFuture<List<io.confluent.ksql.api.client.TableInfo>> tablesFuture = client.listTables();
            List<io.confluent.ksql.api.client.TableInfo> apiTableInfos = tablesFuture.get(30, TimeUnit.SECONDS);
            
            return apiTableInfos.stream()
                    .map(tableInfo -> {
                        TableInfo info = new TableInfo();
                        info.setName(tableInfo.getName());
                        info.setTopic(tableInfo.getTopic());
                        info.setKeyFormat(tableInfo.getKeyFormat());
                        info.setValueFormat(tableInfo.getValueFormat());
                        info.setWindowed(tableInfo.isWindowed());
                        // Note: Timestamp, partitions, and replicas are not available in the API response
                        return info;
                    })
                    .toList();
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new KSQLException("Failed to list tables", e);
        }
    }
    
    /**
     * List all topics in the KSQLDB server.
     * 
     * @return List of topic information
     * @throws KSQLException if there is an error retrieving topics
     */
    public List<TopicInfo> listTopics() throws KSQLException {
        try {
            logger.debug("Listing topics");
            CompletableFuture<List<io.confluent.ksql.api.client.TopicInfo>> topicsFuture = client.listTopics();
            List<io.confluent.ksql.api.client.TopicInfo> apiTopicInfos = topicsFuture.get(30, TimeUnit.SECONDS);
            
            return apiTopicInfos.stream()
                    .map(topicInfo -> {
                        TopicInfo info = new TopicInfo();
                        info.setName(topicInfo.getName());
                        info.setPartitions(topicInfo.getPartitions());
                        // Convert replicas list to a single value (using first value or size)
                        if (topicInfo.getReplicasPerPartition() != null && !topicInfo.getReplicasPerPartition().isEmpty()) {
                            // Use the first value as representative, or you could use an average
                            info.setReplicas(topicInfo.getReplicasPerPartition().get(0));
                        }
                        // Note: Consumer groups and members are not available in the API response
                        return info;
                    })
                    .toList();
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new KSQLException("Failed to list topics", e);
        }
    }
    
    /**
     * List all queries in the KSQLDB server.
     * 
     * @return List of query information
     * @throws KSQLException if there is an error retrieving queries
     */
    public List<QueryInfo> listQueries() throws KSQLException {
        try {
            logger.debug("Listing queries");
            CompletableFuture<List<io.confluent.ksql.api.client.QueryInfo>> queriesFuture = client.listQueries();
            List<io.confluent.ksql.api.client.QueryInfo> apiQueryInfos = queriesFuture.get(30, TimeUnit.SECONDS);
            
            return apiQueryInfos.stream()
                    .map(queryInfo -> {
                        QueryInfo info = new QueryInfo();
                        info.setId(queryInfo.getId());
                        info.setQueryString(queryInfo.getSql());
                        // Status is not directly available, but we can infer from query type
                        info.setStatus(queryInfo.getQueryType().name());
                        queryInfo.getSink().ifPresent(sink -> {
                            // Handle sink if present
                        });
                        queryInfo.getSinkTopic().ifPresent(sinkTopic -> {
                            // Handle sink topic if present
                        });
                        // Note: Execution plan is not available in the API response
                        return info;
                    })
                    .toList();
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new KSQLException("Failed to list queries", e);
        }
    }
    
    /**
     * Get detailed information about a specific source (stream or table).
     * 
     * @param sourceName The name of the source
     * @return Detailed source description
     * @throws KSQLException if there is an error retrieving source description
     */
    public io.confluent.ksql.api.client.SourceDescription describeSource(String sourceName) throws KSQLException {
        try {
            logger.debug("Describing source: {}", sourceName);
            CompletableFuture<io.confluent.ksql.api.client.SourceDescription> descriptionFuture = client.describeSource(sourceName);
            return descriptionFuture.get(30, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new KSQLException("Failed to describe source: " + sourceName, e);
        }
    }
    
    /**
     * Close the client connection.
     */
    public void close() {
        if (client != null) {
            logger.info("Closing KSQLDB client connection");
            client.close();
        }
    }
    
    /**
     * Get the underlying KSQLDB API client.
     * 
     * @return The underlying client
     */
    public Client getApiClient() {
        return client;
    }
    
    /**
     * Get the configuration used by this client.
     * 
     * @return The configuration
     */
    public KSQLDBConfig getConfig() {
        return config;
    }
}