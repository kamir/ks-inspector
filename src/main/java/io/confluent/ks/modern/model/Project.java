package io.confluent.ks.modern.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

/**
 * Represents a project in the domain model.
 * Simplified replacement for kafka-clusterstate-tools Project class.
 * 
 * A Project contains applications, consumers, producers, and streams apps
 * that work together to implement business functionality.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Project {
    
    @JsonProperty("name")
    public String name;
    
    @JsonProperty("version")
    public String version;
    
    @JsonProperty("description")
    public String description;
    
    @JsonProperty("owner")
    public String owner;
    
    @JsonProperty("domains")
    public List<String> domains;
    
    @JsonProperty("applications")
    public List<String> applications;
    
    @JsonProperty("environment")
    public String environment;
    
    // Core components based on actual usage in knowledge graph
    @JsonProperty("streamsApps")
    public List<StreamsApp> streamsApps;
    
    @JsonProperty("consumers")
    public List<Consumer> consumers;
    
    @JsonProperty("producers")
    public List<Producer> producers;
    
    @JsonProperty("metadata")
    public Map<String, Object> metadata;
    
    // Default constructor for Jackson
    public Project() {
        this.streamsApps = new ArrayList<>();
        this.consumers = new ArrayList<>();
        this.producers = new ArrayList<>();
        this.domains = new ArrayList<>();
        this.applications = new ArrayList<>();
    }
    
    public Project(String name, String version) {
        this();
        this.name = name;
        this.version = version;
    }
    
    /**
     * Add a Kafka Streams application to this project
     */
    public void addStreamsApp(StreamsApp app) {
        if (streamsApps == null) {
            streamsApps = new ArrayList<>();
        }
        streamsApps.add(app);
    }
    
    /**
     * Add a consumer to this project
     */
    public void addConsumer(Consumer consumer) {
        if (consumers == null) {
            consumers = new ArrayList<>();
        }
        consumers.add(consumer);
    }
    
    /**
     * Add a producer to this project
     */
    public void addProducer(Producer producer) {
        if (producers == null) {
            producers = new ArrayList<>();
        }
        producers.add(producer);
    }
    
    /**
     * Get total number of components in this project
     */
    public int getTotalComponents() {
        int total = 0;
        total += streamsApps != null ? streamsApps.size() : 0;
        total += consumers != null ? consumers.size() : 0;
        total += producers != null ? producers.size() : 0;
        return total;
    }
    
    @Override
    public String toString() {
        return String.format("Project{name='%s', version='%s', owner='%s', components=%d}", 
                           name, version, owner, getTotalComponents());
    }
}