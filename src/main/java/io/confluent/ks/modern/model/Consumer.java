package io.confluent.ks.modern.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

/**
 * Represents a Kafka consumer.
 * Simplified replacement for kafka-clusterstate-tools Consumer class.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Consumer {
    
    @JsonProperty("name")
    public String name;
    
    @JsonProperty("groupId")
    public String groupId;
    
    @JsonProperty("principal")
    public String principal;
    
    @JsonProperty("topics")
    public List<String> topics;
    
    @JsonProperty("config")
    public Map<String, String> config;
    
    @JsonProperty("autoOffsetReset")
    public String autoOffsetReset;
    
    @JsonProperty("enableAutoCommit")
    public Boolean enableAutoCommit;
    
    @JsonProperty("metadata")
    public Map<String, Object> metadata;
    
    // Default constructor for Jackson
    public Consumer() {}
    
    public Consumer(String name, String groupId, String principal) {
        this.name = name;
        this.groupId = groupId;
        this.principal = principal;
    }
    
    @Override
    public String toString() {
        return String.format("Consumer{name='%s', groupId='%s', principal='%s', topics=%d}", 
                           name, groupId, principal,
                           topics != null ? topics.size() : 0);
    }
}