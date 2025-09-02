package io.confluent.ks.modern.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

/**
 * Modern replacement for net.christophschubert.kafka.clusterstate.formats.env.CloudCluster
 * and Environment classes for cloud cluster configuration.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CloudCluster {
    
    @JsonProperty("name")
    public String name;
    
    @JsonProperty("type")
    public String type; // DEV, STAGING, PROD
    
    @JsonProperty("clusterId")
    public String clusterId;
    
    @JsonProperty("clusterType")
    public String clusterType; // basic, standard, dedicated
    
    @JsonProperty("provider")
    public String provider; // AWS, GCP, AZURE
    
    @JsonProperty("region")
    public String region;
    
    @JsonProperty("availability")
    public String availability; // single-zone, multi-zone
    
    @JsonProperty("owner")
    public String owner;
    
    @JsonProperty("ownerContact")
    public String ownerContact;
    
    @JsonProperty("org")
    public String org;
    
    @JsonProperty("tags")
    public List<String> tags;
    
    @JsonProperty("principals")
    public Map<String, String> principals;
    
    @JsonProperty("domainFileFolders")
    public List<String> domainFileFolders;
    
    @JsonProperty("clusterLevelAccessPath")
    public String clusterLevelAccessPath;
    
    @JsonProperty("clientProperties")
    public ClientProperties clientProperties;
    
    // Default constructor for Jackson
    public CloudCluster() {}
    
    public CloudCluster(String name, String type, String clusterId) {
        this.name = name;
        this.type = type;
        this.clusterId = clusterId;
    }
    
    @Override
    public String toString() {
        return String.format("CloudCluster{name='%s', type='%s', clusterId='%s', provider='%s'}", 
                           name, type, clusterId, provider);
    }
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ClientProperties {
        @JsonProperty("kafka")
        public Map<String, String> kafka;
        
        @JsonProperty("schemaRegistry")
        public Map<String, String> schemaRegistry;
        
        @JsonProperty("knowledgeGraphService")
        public Map<String, String> knowledgeGraphService;
        
        public ClientProperties() {}
    }
}

