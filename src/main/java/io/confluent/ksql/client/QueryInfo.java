package io.confluent.ksql.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;

/**
 * Represents information about a KSQLDB query.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class QueryInfo {
    @JsonProperty("id")
    private String id;
    
    @JsonProperty("queryString")
    private String queryString;
    
    @JsonProperty("status")
    private String status;
    
    @JsonProperty("sinks")
    private List<String> sinks;
    
    @JsonProperty("sinkKafkaTopics")
    private List<String> sinkKafkaTopics;
    
    @JsonProperty("executionPlan")
    private String executionPlan;

    public QueryInfo() {
    }

    public QueryInfo(String id, String queryString, String status) {
        this.id = id;
        this.queryString = queryString;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public QueryInfo setId(String id) {
        this.id = id;
        return this;
    }

    public String getQueryString() {
        return queryString;
    }

    public QueryInfo setQueryString(String queryString) {
        this.queryString = queryString;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public QueryInfo setStatus(String status) {
        this.status = status;
        return this;
    }

    public List<String> getSinks() {
        return sinks;
    }

    public QueryInfo setSinks(List<String> sinks) {
        this.sinks = sinks;
        return this;
    }

    public List<String> getSinkKafkaTopics() {
        return sinkKafkaTopics;
    }

    public QueryInfo setSinkKafkaTopics(List<String> sinkKafkaTopics) {
        this.sinkKafkaTopics = sinkKafkaTopics;
        return this;
    }

    public String getExecutionPlan() {
        return executionPlan;
    }

    public QueryInfo setExecutionPlan(String executionPlan) {
        this.executionPlan = executionPlan;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QueryInfo queryInfo = (QueryInfo) o;
        return Objects.equals(id, queryInfo.id) &&
                Objects.equals(queryString, queryInfo.queryString) &&
                Objects.equals(status, queryInfo.status) &&
                Objects.equals(sinks, queryInfo.sinks) &&
                Objects.equals(sinkKafkaTopics, queryInfo.sinkKafkaTopics) &&
                Objects.equals(executionPlan, queryInfo.executionPlan);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, queryString, status, sinks, sinkKafkaTopics, executionPlan);
    }

    @Override
    public String toString() {
        return "QueryInfo{" +
                "id='" + id + '\'' +
                ", queryString='" + queryString + '\'' +
                ", status='" + status + '\'' +
                ", sinks=" + sinks +
                ", sinkKafkaTopics=" + sinkKafkaTopics +
                ", executionPlan='" + executionPlan + '\'' +
                '}';
    }
}