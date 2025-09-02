package io.confluent.ksql.inventory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.confluent.ksql.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Manages KSQLDB inventory operations including creation, serialization, and deserialization.
 */
public class InventoryManager {
    private static final Logger logger = LoggerFactory.getLogger(InventoryManager.class);
    
    private final ObjectMapper yamlMapper;
    
    public InventoryManager() {
        this.yamlMapper = new ObjectMapper(new YAMLFactory());
        this.yamlMapper.registerModule(new JavaTimeModule());
    }
    
    /**
     * Create a KSQLDB inventory from the metadata collected by a KSQLDB client.
     * 
     * @param client The KSQLDB client to use for collecting metadata
     * @return A KSQLDB inventory containing all collected metadata
     * @throws KSQLException if there is an error collecting metadata
     */
    public KSQLDBInventory createInventory(KSQLDBClient client) throws KSQLException {
        logger.info("Creating KSQLDB inventory from client metadata");
        
        try {
            // Collect all metadata
            ServerInfo serverInfo = client.getServerInfo();
            List<StreamInfo> streams = client.listStreams();
            List<TableInfo> tables = client.listTables();
            List<TopicInfo> topics = client.listTopics();
            List<QueryInfo> queries = client.listQueries();
            
            // Create inventory
            KSQLDBInventory inventory = new KSQLDBInventory();
            inventory.setClusterId(serverInfo.getKsqlServiceId())
                    .setServerInfo(serverInfo)
                    .setStreams(streams)
                    .setTables(tables)
                    .setTopics(topics)
                    .setQueries(queries);
            
            logger.info("Created inventory with {} streams, {} tables, {} topics, and {} queries",
                    streams.size(), tables.size(), topics.size(), queries.size());
            
            return inventory;
        } catch (Exception e) {
            throw new KSQLException("Failed to create inventory", e);
        }
    }
    
    /**
     * Save a KSQLDB inventory to a YAML file.
     * 
     * @param inventory The inventory to save
     * @param filePath The path to the file where the inventory should be saved
     * @throws IOException if there is an error writing to the file
     */
    public void saveInventory(KSQLDBInventory inventory, String filePath) throws IOException {
        logger.info("Saving KSQLDB inventory to file: {}", filePath);
        yamlMapper.writeValue(new File(filePath), inventory);
        logger.info("Successfully saved inventory to file: {}", filePath);
    }
    
    /**
     * Load a KSQLDB inventory from a YAML file.
     * 
     * @param filePath The path to the file from which to load the inventory
     * @return The loaded inventory
     * @throws IOException if there is an error reading from the file
     */
    public KSQLDBInventory loadInventory(String filePath) throws IOException {
        logger.info("Loading KSQLDB inventory from file: {}", filePath);
        KSQLDBInventory inventory = yamlMapper.readValue(new File(filePath), KSQLDBInventory.class);
        logger.info("Successfully loaded inventory from file: {}", filePath);
        return inventory;
    }
    
    /**
     * Get the YAML mapper used by this manager.
     * 
     * @return The YAML mapper
     */
    public ObjectMapper getYamlMapper() {
        return yamlMapper;
    }
}