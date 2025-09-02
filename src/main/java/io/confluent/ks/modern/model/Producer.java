package io.confluent.ks.modern.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

/**
 * Represents a Kafka producer.
 * Simplified replacement for kafka-clusterstate-tools Producer class.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Producer {
    
    @JsonProperty("name")
    public String name;
    
    @JsonProperty("principal")
    public String principal;
    
    @JsonProperty("topics")
    public List<String> topics;
    
    @JsonProperty("config")
    public Map<String, String> config;
    
    @JsonProperty("acks")
    public String acks;
    
    @JsonProperty("enableIdempotence")
    public Boolean enableIdempotence;
    
    @JsonProperty("retries")
    public Integer retries;
    
    @JsonProperty("metadata")
    public Map<String, Object> metadata;
    
    // Default constructor for Jackson
    public Producer() {}
    
    public Producer(String name, String principal) {
        this.name = name;
        this.principal = principal;
    }
    
    @Override
    public String toString() {
        return String.format("Producer{name='%s', principal='%s', topics=%d}", 
                           name, principal,
                           topics != null ? topics.size() : 0);
    }
}