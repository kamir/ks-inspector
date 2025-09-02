package io.confluent.ks.modern.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

/**
 * Environment configuration containing multiple cloud clusters
 * Simplified replacement for kafka-clusterstate-tools Environment class.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Environment {
    
    @JsonProperty("name")
    public String name;
    
    @JsonProperty("description")
    public String description;
    
    @JsonProperty("clusters")
    public List<CloudCluster> clusters;
    
    @JsonProperty("globalConfig")
    public Map<String, Object> globalConfig;
    
    // Default constructor for Jackson
    public Environment() {}
    
    public Environment(String name, List<CloudCluster> clusters) {
        this.name = name;
        this.clusters = clusters;
    }
    
    @Override
    public String toString() {
        return String.format("Environment{name='%s', clusters=%d}", 
                           name, clusters != null ? clusters.size() : 0);
    }
}