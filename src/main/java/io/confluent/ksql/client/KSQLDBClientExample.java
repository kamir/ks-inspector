package io.confluent.ksql.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Example usage of the KSQLDBClient.
 */
public class KSQLDBClientExample {
    private static final Logger logger = LoggerFactory.getLogger(KSQLDBClientExample.class);

    public static void main(String[] args) {
        KSQLDBConfig config = new KSQLDBConfig()
                .setHost("localhost")
                .setPort(8088);

        try (KSQLDBClient client = new KSQLDBClient(config)) {
            // Get server info
            ServerInfo serverInfo = client.getServerInfo();
            logger.info("Server Info: {}", serverInfo);

            // List streams
            List<StreamInfo> streams = client.listStreams();
            logger.info("Found {} streams", streams.size());
            streams.forEach(stream -> logger.info("  Stream: {}", stream.getName()));

            // List tables
            List<TableInfo> tables = client.listTables();
            logger.info("Found {} tables", tables.size());
            tables.forEach(table -> logger.info("  Table: {}", table.getName()));

            // List topics
            List<TopicInfo> topics = client.listTopics();
            logger.info("Found {} topics", topics.size());
            topics.forEach(topic -> logger.info("  Topic: {} ({} partitions)", topic.getName(), topic.getPartitions()));

            // List queries
            List<QueryInfo> queries = client.listQueries();
            logger.info("Found {} queries", queries.size());
            queries.forEach(query -> logger.info("  Query: {} ({})", query.getId(), query.getStatus()));

        } catch (Exception e) {
            logger.error("Error occurred while interacting with KSQLDB", e);
        }
    }
}