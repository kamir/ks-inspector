package io.confluent.ks.modern.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Properties;

/**
 * Modern replacement for net.christophschubert.kafka.clusterstate.cli.EnvVarTools
 * Provides environment variable processing utilities.
 */
public class ModernEnvVarTools {
    
    private static final Logger logger = LoggerFactory.getLogger(ModernEnvVarTools.class);
    
    /**
     * Read property from environment variables with prefix
     * Modern replacement for EnvVarTools.readPropertyFromEnv()
     */
    public static String readPropertyFromEnv(String prefix, String propertyName) {
        if (prefix == null || propertyName == null) {
            return null;
        }
        
        String actualPrefix = prefix.endsWith("_") ? prefix : prefix + "_";
        String envVarName = actualPrefix + propertyName.toUpperCase().replace(".", "_");
        
        String value = System.getenv(envVarName);
        if (value != null) {
            logger.debug("Found environment variable: {} = {}", envVarName, value);
        } else {
            logger.debug("Environment variable not found: {}", envVarName);
        }
        
        return value;
    }
    
    /**
     * Read property from environment variables with multiple possible names
     */
    public static String readPropertyFromEnv(String prefix, String... propertyNames) {
        for (String propertyName : propertyNames) {
            String value = readPropertyFromEnv(prefix, propertyName);
            if (value != null && !value.trim().isEmpty()) {
                return value;
            }
        }
        return null;
    }
    
    /**
     * Get all environment variables with specified prefix
     */
    public static Properties getAllEnvVarsWithPrefix(String prefix) {
        Properties properties = new Properties();
        String actualPrefix = prefix.endsWith("_") ? prefix : prefix + "_";
        
        System.getenv().forEach((key, value) -> {
            if (key.startsWith(actualPrefix)) {
                String propertyKey = key.substring(actualPrefix.length())
                                      .toLowerCase()
                                      .replace("_", ".");
                properties.setProperty(propertyKey, value);
                logger.debug("Mapped env var {} to property {} = {}", key, propertyKey, value);
            }
        });
        
        return properties;
    }
    
    /**
     * Merge environment variables into existing properties
     */
    public static void mergeEnvVarsIntoProperties(Properties properties, String prefix) {
        Properties envProperties = getAllEnvVarsWithPrefix(prefix);
        envProperties.forEach((key, value) -> {
            properties.setProperty(key.toString(), value.toString());
        });
        
        logger.debug("Merged {} environment variables with prefix '{}'", envProperties.size(), prefix);
    }
    
    /**
     * Check if environment variable exists
     */
    public static boolean hasEnvVar(String prefix, String propertyName) {
        return readPropertyFromEnv(prefix, propertyName) != null;
    }
    
    /**
     * Get environment variable with default value
     */
    public static String readPropertyFromEnvWithDefault(String prefix, String propertyName, String defaultValue) {
        String value = readPropertyFromEnv(prefix, propertyName);
        return value != null ? value : defaultValue;
    }
    
    /**
     * Log all environment variables with prefix (for debugging)
     */
    public static void logEnvVarsWithPrefix(String prefix) {
        String actualPrefix = prefix.endsWith("_") ? prefix : prefix + "_";
        
        logger.info("Environment variables with prefix '{}':", actualPrefix);
        System.getenv().entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(actualPrefix))
                .forEach(entry -> 
                    logger.info("  {} = {}", entry.getKey(), 
                              entry.getValue().length() > 50 ? 
                              entry.getValue().substring(0, 47) + "..." : 
                              entry.getValue()));
    }
    
    /**
     * Validate required environment variables
     */
    public static boolean validateRequiredEnvVars(String prefix, String... requiredVars) {
        boolean allPresent = true;
        
        for (String varName : requiredVars) {
            if (!hasEnvVar(prefix, varName)) {
                logger.error("Required environment variable missing: {}_{}", prefix, varName.toUpperCase());
                allPresent = false;
            }
        }
        
        return allPresent;
    }
}