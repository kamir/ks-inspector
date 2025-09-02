package io.confluent.ksql.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * Represents information about a KSQLDB stream.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class StreamInfo {
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("topic")
    private String topic;
    
    @JsonProperty("keyFormat")
    private String keyFormat;
    
    @JsonProperty("valueFormat")
    private String valueFormat;
    
    @JsonProperty("isWindowed")
    private boolean isWindowed;
    
    @JsonProperty("timestamp")
    private String timestamp;
    
    @JsonProperty("partitions")
    private Integer partitions;
    
    @JsonProperty("replicas")
    private Integer replicas;

    public StreamInfo() {
    }

    public StreamInfo(String name, String topic, String keyFormat, String valueFormat, boolean isWindowed) {
        this.name = name;
        this.topic = topic;
        this.keyFormat = keyFormat;
        this.valueFormat = valueFormat;
        this.isWindowed = isWindowed;
    }

    public String getName() {
        return name;
    }

    public StreamInfo setName(String name) {
        this.name = name;
        return this;
    }

    public String getTopic() {
        return topic;
    }

    public StreamInfo setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public String getKeyFormat() {
        return keyFormat;
    }

    public StreamInfo setKeyFormat(String keyFormat) {
        this.keyFormat = keyFormat;
        return this;
    }

    public String getValueFormat() {
        return valueFormat;
    }

    public StreamInfo setValueFormat(String valueFormat) {
        this.valueFormat = valueFormat;
        return this;
    }

    public boolean isWindowed() {
        return isWindowed;
    }

    public StreamInfo setWindowed(boolean windowed) {
        isWindowed = windowed;
        return this;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public StreamInfo setTimestamp(String timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public Integer getPartitions() {
        return partitions;
    }

    public StreamInfo setPartitions(Integer partitions) {
        this.partitions = partitions;
        return this;
    }

    public Integer getReplicas() {
        return replicas;
    }

    public StreamInfo setReplicas(Integer replicas) {
        this.replicas = replicas;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StreamInfo that = (StreamInfo) o;
        return isWindowed == that.isWindowed &&
                Objects.equals(name, that.name) &&
                Objects.equals(topic, that.topic) &&
                Objects.equals(keyFormat, that.keyFormat) &&
                Objects.equals(valueFormat, that.valueFormat) &&
                Objects.equals(timestamp, that.timestamp) &&
                Objects.equals(partitions, that.partitions) &&
                Objects.equals(replicas, that.replicas);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, topic, keyFormat, valueFormat, isWindowed, timestamp, partitions, replicas);
    }

    @Override
    public String toString() {
        return "StreamInfo{" +
                "name='" + name + '\'' +
                ", topic='" + topic + '\'' +
                ", keyFormat='" + keyFormat + '\'' +
                ", valueFormat='" + valueFormat + '\'' +
                ", isWindowed=" + isWindowed +
                ", timestamp='" + timestamp + '\'' +
                ", partitions=" + partitions +
                ", replicas=" + replicas +
                '}';
    }
}