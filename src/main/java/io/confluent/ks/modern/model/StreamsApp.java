package io.confluent.ks.modern.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

/**
 * Represents a Kafka Streams application.
 * Simplified replacement for kafka-clusterstate-tools StreamsApp class.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class StreamsApp {
    
    @JsonProperty("name")
    public String name;
    
    @JsonProperty("applicationId")
    public String applicationId;
    
    @JsonProperty("description")
    public String description;
    
    @JsonProperty("inputTopics")
    public List<String> inputTopics;
    
    @JsonProperty("outputTopics")
    public List<String> outputTopics;
    
    @JsonProperty("config")
    public Map<String, String> config;
    
    @JsonProperty("topology")
    public String topology;
    
    @JsonProperty("stateStores")
    public List<String> stateStores;
    
    @JsonProperty("metadata")
    public Map<String, Object> metadata;
    
    // Default constructor for Jackson
    public StreamsApp() {}
    
    public StreamsApp(String name, String applicationId) {
        this.name = name;
        this.applicationId = applicationId;
    }
    
    @Override
    public String toString() {
        return String.format("StreamsApp{name='%s', applicationId='%s', inputTopics=%d, outputTopics=%d}", 
                           name, applicationId, 
                           inputTopics != null ? inputTopics.size() : 0,
                           outputTopics != null ? outputTopics.size() : 0);
    }
}