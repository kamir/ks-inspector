package io.confluent.ksql.inspector;

import io.confluent.ksql.client.KSQLDBClient;
import io.confluent.ksql.client.KSQLDBConfig;
import io.confluent.ksql.client.KSQLException;
import io.confluent.ksql.inventory.InventoryManager;
import io.confluent.ksql.inventory.KSQLDBInventory;
import io.confluent.ksql.neo4j.Neo4jManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Main class for inspecting KSQLDB clusters and managing inventory.
 */
public class KSQLDBClusterInspector {
    private static final Logger logger = LoggerFactory.getLogger(KSQLDBClusterInspector.class);
    
    private final InventoryManager inventoryManager;
    
    public KSQLDBClusterInspector() {
        this.inventoryManager = new InventoryManager();
    }
    
    /**
     * Inspect a KSQLDB cluster and create an inventory.
     * 
     * @param config The configuration for connecting to the KSQLDB server
     * @return A KSQLDB inventory containing all collected metadata
     * @throws KSQLException if there is an error collecting metadata
     */
    public KSQLDBInventory inspectCluster(KSQLDBConfig config) throws KSQLException {
        logger.info("Inspecting KSQLDB cluster at {}", config.getServerAddress());
        
        try (KSQLDBClient client = new KSQLDBClient(config)) {
            return inventoryManager.createInventory(client);
        } catch (Exception e) {
            logger.error("Failed to inspect KSQLDB cluster", e);
            throw new KSQLException("Failed to inspect KSQLDB cluster", e);
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
        inventoryManager.saveInventory(inventory, filePath);
    }
    
    /**
     * Export a KSQLDB inventory to Neo4j.
     * 
     * @param inventory The inventory to export
     * @param neo4jUri The URI of the Neo4j database
     * @param neo4jUsername The username for the Neo4j database
     * @param neo4jPassword The password for the Neo4j database
     */
    public void exportToNeo4j(KSQLDBInventory inventory, String neo4jUri, 
                             String neo4jUsername, String neo4jPassword) {
        logger.info("Exporting KSQLDB inventory to Neo4j at {}", neo4jUri);
        
        try (Neo4jManager neo4jManager = new Neo4jManager(neo4jUri, neo4jUsername, neo4jPassword)) {
            if (!neo4jManager.isConnectionValid()) {
                throw new RuntimeException("Unable to connect to Neo4j database");
            }
            
            neo4jManager.exportInventory(inventory);
        } catch (Exception e) {
            logger.error("Failed to export inventory to Neo4j", e);
            throw new RuntimeException("Failed to export inventory to Neo4j", e);
        }
    }
    
    /**
     * Load a KSQLDB inventory from a YAML file.
     * 
     * @param filePath The path to the file from which to load the inventory
     * @return The loaded inventory
     * @throws IOException if there is an error reading from the file
     */
    public KSQLDBInventory loadInventory(String filePath) throws IOException {
        return inventoryManager.loadInventory(filePath);
    }
    
    /**
     * Get the inventory manager used by this inspector.
     * 
     * @return The inventory manager
     */
    public InventoryManager getInventoryManager() {
        return inventoryManager;
    }
}