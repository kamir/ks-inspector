package io.confluent.ksql.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * Represents information about a KSQLDB table.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TableInfo {
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

    public TableInfo() {
    }

    public TableInfo(String name, String topic, String keyFormat, String valueFormat) {
        this.name = name;
        this.topic = topic;
        this.keyFormat = keyFormat;
        this.valueFormat = valueFormat;
    }

    public String getName() {
        return name;
    }

    public TableInfo setName(String name) {
        this.name = name;
        return this;
    }

    public String getTopic() {
        return topic;
    }

    public TableInfo setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public String getKeyFormat() {
        return keyFormat;
    }

    public TableInfo setKeyFormat(String keyFormat) {
        this.keyFormat = keyFormat;
        return this;
    }

    public String getValueFormat() {
        return valueFormat;
    }

    public TableInfo setValueFormat(String valueFormat) {
        this.valueFormat = valueFormat;
        return this;
    }

    public boolean isWindowed() {
        return isWindowed;
    }

    public TableInfo setWindowed(boolean windowed) {
        isWindowed = windowed;
        return this;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public TableInfo setTimestamp(String timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public Integer getPartitions() {
        return partitions;
    }

    public TableInfo setPartitions(Integer partitions) {
        this.partitions = partitions;
        return this;
    }

    public Integer getReplicas() {
        return replicas;
    }

    public TableInfo setReplicas(Integer replicas) {
        this.replicas = replicas;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TableInfo tableInfo = (TableInfo) o;
        return isWindowed == tableInfo.isWindowed &&
                Objects.equals(name, tableInfo.name) &&
                Objects.equals(topic, tableInfo.topic) &&
                Objects.equals(keyFormat, tableInfo.keyFormat) &&
                Objects.equals(valueFormat, tableInfo.valueFormat) &&
                Objects.equals(timestamp, tableInfo.timestamp) &&
                Objects.equals(partitions, tableInfo.partitions) &&
                Objects.equals(replicas, tableInfo.replicas);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, topic, keyFormat, valueFormat, isWindowed, timestamp, partitions, replicas);
    }

    @Override
    public String toString() {
        return "TableInfo{" +
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