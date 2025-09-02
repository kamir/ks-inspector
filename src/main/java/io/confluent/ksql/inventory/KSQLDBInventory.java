package io.confluent.ksql.inventory;

import io.confluent.ksql.client.ServerInfo;
import io.confluent.ksql.client.StreamInfo;
import io.confluent.ksql.client.TableInfo;
import io.confluent.ksql.client.TopicInfo;
import io.confluent.ksql.client.QueryInfo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * Represents a complete inventory of a KSQLDB cluster.
 */
public class KSQLDBInventory {
    private String clusterId;
    private LocalDateTime collectedAt;
    private ServerInfo serverInfo;
    private List<StreamInfo> streams;
    private List<TableInfo> tables;
    private List<TopicInfo> topics;
    private List<QueryInfo> queries;

    public KSQLDBInventory() {
        this.collectedAt = LocalDateTime.now();
    }

    public KSQLDBInventory(String clusterId, ServerInfo serverInfo, 
                          List<StreamInfo> streams, List<TableInfo> tables,
                          List<TopicInfo> topics, List<QueryInfo> queries) {
        this.clusterId = clusterId;
        this.collectedAt = LocalDateTime.now();
        this.serverInfo = serverInfo;
        this.streams = streams;
        this.tables = tables;
        this.topics = topics;
        this.queries = queries;
    }

    public String getClusterId() {
        return clusterId;
    }

    public KSQLDBInventory setClusterId(String clusterId) {
        this.clusterId = clusterId;
        return this;
    }

    public LocalDateTime getCollectedAt() {
        return collectedAt;
    }

    public KSQLDBInventory setCollectedAt(LocalDateTime collectedAt) {
        this.collectedAt = collectedAt;
        return this;
    }

    public ServerInfo getServerInfo() {
        return serverInfo;
    }

    public KSQLDBInventory setServerInfo(ServerInfo serverInfo) {
        this.serverInfo = serverInfo;
        return this;
    }

    public List<StreamInfo> getStreams() {
        return streams;
    }

    public KSQLDBInventory setStreams(List<StreamInfo> streams) {
        this.streams = streams;
        return this;
    }

    public List<TableInfo> getTables() {
        return tables;
    }

    public KSQLDBInventory setTables(List<TableInfo> tables) {
        this.tables = tables;
        return this;
    }

    public List<TopicInfo> getTopics() {
        return topics;
    }

    public KSQLDBInventory setTopics(List<TopicInfo> topics) {
        this.topics = topics;
        return this;
    }

    public List<QueryInfo> getQueries() {
        return queries;
    }

    public KSQLDBInventory setQueries(List<QueryInfo> queries) {
        this.queries = queries;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KSQLDBInventory that = (KSQLDBInventory) o;
        return Objects.equals(clusterId, that.clusterId) &&
                Objects.equals(collectedAt, that.collectedAt) &&
                Objects.equals(serverInfo, that.serverInfo) &&
                Objects.equals(streams, that.streams) &&
                Objects.equals(tables, that.tables) &&
                Objects.equals(topics, that.topics) &&
                Objects.equals(queries, that.queries);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clusterId, collectedAt, serverInfo, streams, tables, topics, queries);
    }

    @Override
    public String toString() {
        return "KSQLDBInventory{" +
                "clusterId='" + clusterId + '\'' +
                ", collectedAt=" + collectedAt +
                ", serverInfo=" + serverInfo +
                ", streams=" + (streams != null ? streams.size() : 0) + " items" +
                ", tables=" + (tables != null ? tables.size() : 0) + " items" +
                ", topics=" + (topics != null ? topics.size() : 0) + " items" +
                ", queries=" + (queries != null ? queries.size() : 0) + " items" +
                '}';
    }
}