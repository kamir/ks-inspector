package io.confluent.ksql.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * Represents information about a Kafka topic as seen by KSQLDB.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TopicInfo {
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("partitions")
    private Integer partitions;
    
    @JsonProperty("replicas")
    private Integer replicas;
    
    @JsonProperty("consumerGroups")
    private Integer consumerGroups;
    
    @JsonProperty("consumerGroupMembers")
    private Integer consumerGroupMembers;

    public TopicInfo() {
    }

    public TopicInfo(String name, Integer partitions, Integer replicas) {
        this.name = name;
        this.partitions = partitions;
        this.replicas = replicas;
    }

    public String getName() {
        return name;
    }

    public TopicInfo setName(String name) {
        this.name = name;
        return this;
    }

    public Integer getPartitions() {
        return partitions;
    }

    public TopicInfo setPartitions(Integer partitions) {
        this.partitions = partitions;
        return this;
    }

    public Integer getReplicas() {
        return replicas;
    }

    public TopicInfo setReplicas(Integer replicas) {
        this.replicas = replicas;
        return this;
    }

    public Integer getConsumerGroups() {
        return consumerGroups;
    }

    public TopicInfo setConsumerGroups(Integer consumerGroups) {
        this.consumerGroups = consumerGroups;
        return this;
    }

    public Integer getConsumerGroupMembers() {
        return consumerGroupMembers;
    }

    public TopicInfo setConsumerGroupMembers(Integer consumerGroupMembers) {
        this.consumerGroupMembers = consumerGroupMembers;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TopicInfo topicInfo = (TopicInfo) o;
        return Objects.equals(name, topicInfo.name) &&
                Objects.equals(partitions, topicInfo.partitions) &&
                Objects.equals(replicas, topicInfo.replicas) &&
                Objects.equals(consumerGroups, topicInfo.consumerGroups) &&
                Objects.equals(consumerGroupMembers, topicInfo.consumerGroupMembers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, partitions, replicas, consumerGroups, consumerGroupMembers);
    }

    @Override
    public String toString() {
        return "TopicInfo{" +
                "name='" + name + '\'' +
                ", partitions=" + partitions +
                ", replicas=" + replicas +
                ", consumerGroups=" + consumerGroups +
                ", consumerGroupMembers=" + consumerGroupMembers +
                '}';
    }
}