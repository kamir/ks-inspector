package io.confluent.ks.modern.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.confluent.ks.modern.model.Domain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Modern replacement for net.christophschubert.kafka.clusterstate.formats.domain.DomainParser
 * Uses Jackson with proper YAML support for latest versions.
 */
public class DomainParser {
    
    private static final Logger logger = LoggerFactory.getLogger(DomainParser.class);
    
    private final ObjectMapper yamlMapper;
    private final ObjectMapper jsonMapper;
    
    public DomainParser() {
        this.yamlMapper = new ObjectMapper(new YAMLFactory());
        this.jsonMapper = new ObjectMapper();
    }
    
    /**
     * Load domain from file - supports both YAML and JSON formats
     */
    public Domain loadFromFile(File file) throws IOException {
        if (!file.exists()) {
            throw new IOException("Domain file does not exist: " + file.getAbsolutePath());
        }
        
        if (!file.canRead()) {
            throw new IOException("Cannot read domain file: " + file.getAbsolutePath());
        }
        
        logger.debug("Loading domain from file: {}", file.getAbsolutePath());
        
        try {
            String fileName = file.getName().toLowerCase();
            if (fileName.endsWith(".yaml") || fileName.endsWith(".yml")) {
                return loadFromYaml(file);
            } else if (fileName.endsWith(".json")) {
                return loadFromJson(file);
            } else {
                // Try YAML first, then JSON as fallback
                try {
                    return loadFromYaml(file);
                } catch (Exception e) {
                    logger.debug("Failed to parse as YAML, trying JSON: {}", e.getMessage());
                    return loadFromJson(file);
                }
            }
        } catch (Exception e) {
            throw new IOException("Failed to parse domain file: " + file.getAbsolutePath(), e);
        }
    }
    
    /**
     * Load domain from YAML file
     */
    public Domain loadFromYaml(File file) throws IOException {
        logger.debug("Parsing YAML domain file: {}", file.getAbsolutePath());
        return yamlMapper.readValue(file, Domain.class);
    }
    
    /**
     * Load domain from JSON file
     */
    public Domain loadFromJson(File file) throws IOException {
        logger.debug("Parsing JSON domain file: {}", file.getAbsolutePath());
        return jsonMapper.readValue(file, Domain.class);
    }
    
    /**
     * Load domain from string content
     */
    public Domain loadFromString(String content, boolean isYaml) throws IOException {
        if (isYaml) {
            return yamlMapper.readValue(content, Domain.class);
        } else {
            return jsonMapper.readValue(content, Domain.class);
        }
    }
    
    /**
     * Save domain to file
     */
    public void saveToFile(Domain domain, File file, boolean asYaml) throws IOException {
        logger.debug("Saving domain to file: {}", file.getAbsolutePath());
        
        // Ensure parent directory exists
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            if (!parentDir.mkdirs()) {
                throw new IOException("Failed to create parent directory: " + parentDir.getAbsolutePath());
            }
        }
        
        if (asYaml) {
            yamlMapper.writeValue(file, domain);
        } else {
            jsonMapper.writeValue(file, domain);
        }
    }
    
    /**
     * Check if a file is a domain file based on extension and content
     */
    public static boolean isDomainFile(Path path) {
        if (!Files.isRegularFile(path)) {
            return false;
        }
        
        String fileName = path.getFileName().toString().toLowerCase();
        return fileName.endsWith(".yaml") || 
               fileName.endsWith(".yml") || 
               fileName.endsWith(".json") ||
               fileName.endsWith(".domy"); // Original domain file extension
    }
    
    /**
     * Validate domain structure
     */
    public boolean validateDomain(Domain domain) {
        if (domain == null) {
            logger.warn("Domain is null");
            return false;
        }
        
        if (domain.name == null || domain.name.trim().isEmpty()) {
            logger.warn("Domain name is null or empty");
            return false;
        }
        
        logger.debug("Domain validation passed for: {}", domain.name);
        return true;
    }
}