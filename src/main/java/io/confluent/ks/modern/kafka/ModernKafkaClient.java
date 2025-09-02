package io.confluent.ks.modern.kafka;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Modern replacement for net.christophschubert.kafka.clusterstate.ClientBundle
 * Uses modern Kafka Admin Client and provides utility functions for configuration.
 */
public class ModernKafkaClient {
    
    private static final Logger logger = LoggerFactory.getLogger(ModernKafkaClient.class);
    
    private final Properties kafkaProperties;
    private final Properties schemaRegistryProperties;
    private AdminClient adminClient;
    
    public ModernKafkaClient(Properties kafkaProperties) {
        this.kafkaProperties = new Properties();
        this.kafkaProperties.putAll(kafkaProperties);
        this.schemaRegistryProperties = new Properties();
        
        // Extract schema registry properties
        extractSchemaRegistryProperties(kafkaProperties);
        
        // Initialize admin client
        initializeAdminClient();
    }
    
    /**
     * Create ModernKafkaClient from properties file and environment variables
     * Modern replacement for CLITools.loadProperties() and ClientBundle.fromProperties()
     */
    public static ModernKafkaClient fromProperties(File configFile, String bootstrapServer, String envVarPrefix) {
        Properties properties = loadProperties(configFile, bootstrapServer, envVarPrefix);
        return new ModernKafkaClient(properties);
    }
    
    /**
     * Load properties from file and merge with environment variables
     * Modern replacement for CLITools.loadProperties()
     */
    public static Properties loadProperties(File configFile, String bootstrapServer, String envVarPrefix) {
        Properties properties = new Properties();
        
        // Load from file if exists
        if (configFile != null && configFile.exists() && configFile.canRead()) {
            try (FileInputStream fis = new FileInputStream(configFile)) {
                properties.load(fis);
                logger.debug("Loaded properties from file: {}", configFile.getAbsolutePath());
            } catch (IOException e) {
                logger.warn("Failed to load properties file: {}", configFile.getAbsolutePath(), e);
            }
        }
        
        // Override with bootstrap server if provided
        if (bootstrapServer != null && !bootstrapServer.trim().isEmpty()) {
            properties.setProperty(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
            properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
            properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
            logger.debug("Set bootstrap server: {}", bootstrapServer);
        }
        
        // Merge environment variables
        if (envVarPrefix != null && !envVarPrefix.trim().isEmpty()) {
            mergeEnvironmentVariables(properties, envVarPrefix);
        }
        
        // Set default properties for modern Kafka versions
        setDefaultProperties(properties);
        
        return properties;
    }
    
    /**
     * Merge environment variables with specified prefix
     * Modern replacement for EnvVarTools functionality
     */
    private static void mergeEnvironmentVariables(Properties properties, String prefix) {
        String actualPrefix = prefix.endsWith("_") ? prefix : prefix + "_";
        
        System.getenv().forEach((key, value) -> {
            if (key.startsWith(actualPrefix)) {
                String propertyKey = key.substring(actualPrefix.length())
                                      .toLowerCase()
                                      .replace("_", ".");
                properties.setProperty(propertyKey, value);
                logger.debug("Mapped env var {} to property {}", key, propertyKey);
            }
        });
    }
    
    /**
     * Set default properties for modern Kafka client compatibility
     */
    private static void setDefaultProperties(Properties properties) {
        // Default admin client properties
        properties.putIfAbsent(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, "30000");
        properties.putIfAbsent(AdminClientConfig.RETRIES_CONFIG, "5");
        
        // Default producer properties
        properties.putIfAbsent(ProducerConfig.ACKS_CONFIG, "all");
        properties.putIfAbsent(ProducerConfig.RETRIES_CONFIG, "2147483647");
        properties.putIfAbsent(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
        
        // Default consumer properties
        properties.putIfAbsent(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        properties.putIfAbsent(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        
        // Modern serialization defaults
        properties.putIfAbsent(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, 
                              "org.apache.kafka.common.serialization.StringSerializer");
        properties.putIfAbsent(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, 
                              "org.apache.kafka.common.serialization.StringSerializer");
        properties.putIfAbsent(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, 
                              "org.apache.kafka.common.serialization.StringDeserializer");
        properties.putIfAbsent(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, 
                              "org.apache.kafka.common.serialization.StringDeserializer");
    }
    
    /**
     * Extract schema registry specific properties
     */
    private void extractSchemaRegistryProperties(Properties kafkaProps) {
        kafkaProps.forEach((key, value) -> {
            String keyStr = key.toString();
            if (keyStr.startsWith("schema.registry")) {
                schemaRegistryProperties.setProperty(keyStr, value.toString());
            }
        });
    }
    
    /**
     * Initialize Kafka Admin Client
     */
    private void initializeAdminClient() {
        Map<String, Object> adminConfig = new HashMap<>();
        kafkaProperties.forEach((key, value) -> {
            String keyStr = key.toString();
            // Include admin client relevant properties
            if (keyStr.startsWith("bootstrap.servers") ||
                keyStr.startsWith("security.") ||
                keyStr.startsWith("sasl.") ||
                keyStr.startsWith("ssl.") ||
                keyStr.startsWith("request.timeout") ||
                keyStr.startsWith("retries")) {
                adminConfig.put(keyStr, value);
            }
        });
        
        try {
            this.adminClient = AdminClient.create(adminConfig);
            logger.debug("Admin client initialized successfully");
        } catch (Exception e) {
            logger.error("Failed to initialize admin client", e);
            throw new RuntimeException("Failed to initialize admin client", e);
        }
    }
    
    /**
     * Get Kafka properties for producer/consumer
     */
    public Properties getKafkaProperties() {
        return new Properties(kafkaProperties);
    }
    
    /**
     * Get Schema Registry properties
     */
    public Properties getSchemaRegistryProperties() {
        return new Properties(schemaRegistryProperties);
    }
    
    /**
     * Get Admin Client
     */
    public AdminClient getAdminClient() {
        return adminClient;
    }
    
    /**
     * Describe the client configuration
     */
    public void describe() {
        logger.info("ModernKafkaClient Configuration:");
        logger.info("Bootstrap Servers: {}", kafkaProperties.getProperty("bootstrap.servers"));
        logger.info("Security Protocol: {}", kafkaProperties.getProperty("security.protocol"));
        logger.info("Schema Registry URL: {}", schemaRegistryProperties.getProperty("schema.registry.url"));
        logger.info("Admin Client Status: {}", adminClient != null ? "Initialized" : "Not Initialized");
    }
    
    /**
     * Close resources
     */
    public void close() {
        if (adminClient != null) {
            try {
                adminClient.close();
                logger.debug("Admin client closed");
            } catch (Exception e) {
                logger.warn("Error closing admin client", e);
            }
        }
    }
}