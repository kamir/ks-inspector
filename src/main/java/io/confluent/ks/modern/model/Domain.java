package io.confluent.ks.modern.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

/**
 * Modern replacement for net.christophschubert.kafka.clusterstate.formats.domain.Domain
 * Uses Jackson annotations for YAML/JSON processing with latest versions.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Domain {
    
    @JsonProperty("name")
    public String name;
    
    @JsonProperty("version")
    public String version;
    
    @JsonProperty("description")
    public String description;
    
    @JsonProperty("applications")
    public List<Application> applications;
    
    @JsonProperty("projects")
    public List<Project> projects;
    
    @JsonProperty("topics")
    public Map<String, Topic> topics;
    
    @JsonProperty("schemas")
    public Map<String, Schema> schemas;
    
    @JsonProperty("connectors")
    public Map<String, Connector> connectors;
    
    @JsonProperty("ksqlQueries")
    public List<KsqlQuery> ksqlQueries;
    
    @JsonProperty("acls")
    public List<Acl> acls;
    
    @JsonProperty("tags")
    public List<String> tags;
    
    @JsonProperty("metadata")
    public Map<String, Object> metadata;
    
    // Default constructor for Jackson
    public Domain() {}
    
    public Domain(String name, String version) {
        this.name = name;
        this.version = version;
    }
    
    @Override
    public String toString() {
        return String.format("Domain{name='%s', version='%s', description='%s'}", 
                           name, version, description);
    }
    
    // Inner classes for domain components
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Application {
        @JsonProperty("name")
        public String name;
        
        @JsonProperty("type")
        public String type;
        
        @JsonProperty("config")
        public Map<String, Object> config;
        
        @JsonProperty("inputTopics")
        public List<String> inputTopics;
        
        @JsonProperty("outputTopics")
        public List<String> outputTopics;
    }
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Topic {
        @JsonProperty("name")
        public String name;
        
        @JsonProperty("partitions")
        public Integer partitions;
        
        @JsonProperty("replicationFactor")
        public Integer replicationFactor;
        
        @JsonProperty("config")
        public Map<String, String> config;
        
        @JsonProperty("keySchema")
        public String keySchema;
        
        @JsonProperty("valueSchema")
        public String valueSchema;
    }
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Schema {
        @JsonProperty("name")
        public String name;
        
        @JsonProperty("type")
        public String type;
        
        @JsonProperty("definition")
        public String definition;
        
        @JsonProperty("compatibility")
        public String compatibility;
    }
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Connector {
        @JsonProperty("name")
        public String name;
        
        @JsonProperty("type")
        public String type;
        
        @JsonProperty("config")
        public Map<String, Object> config;
    }
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class KsqlQuery {
        @JsonProperty("name")
        public String name;
        
        @JsonProperty("query")
        public String query;
        
        @JsonProperty("type")
        public String type; // STREAM, TABLE, etc.
    }
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Acl {
        @JsonProperty("principal")
        public String principal;
        
        @JsonProperty("operation")
        public String operation;
        
        @JsonProperty("resource")
        public String resource;
        
        @JsonProperty("resourceType")
        public String resourceType;
    }
}