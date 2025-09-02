package io.confluent.ksql.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * Represents information about a KSQLDB server.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServerInfo {
    @JsonProperty("serverVersion")
    private String serverVersion;
    
    @JsonProperty("kafkaClusterId")
    private String kafkaClusterId;
    
    @JsonProperty("ksqlServiceId")
    private String ksqlServiceId;
    
    @JsonProperty("host")
    private String host;
    
    @JsonProperty("port")
    private Integer port;

    public ServerInfo() {
    }

    public ServerInfo(String serverVersion, String kafkaClusterId, String ksqlServiceId) {
        this.serverVersion = serverVersion;
        this.kafkaClusterId = kafkaClusterId;
        this.ksqlServiceId = ksqlServiceId;
    }

    public String getServerVersion() {
        return serverVersion;
    }

    public ServerInfo setServerVersion(String serverVersion) {
        this.serverVersion = serverVersion;
        return this;
    }

    public String getKafkaClusterId() {
        return kafkaClusterId;
    }

    public ServerInfo setKafkaClusterId(String kafkaClusterId) {
        this.kafkaClusterId = kafkaClusterId;
        return this;
    }

    public String getKsqlServiceId() {
        return ksqlServiceId;
    }

    public ServerInfo setKsqlServiceId(String ksqlServiceId) {
        this.ksqlServiceId = ksqlServiceId;
        return this;
    }

    public String getHost() {
        return host;
    }

    public ServerInfo setHost(String host) {
        this.host = host;
        return this;
    }

    public Integer getPort() {
        return port;
    }

    public ServerInfo setPort(Integer port) {
        this.port = port;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServerInfo that = (ServerInfo) o;
        return Objects.equals(serverVersion, that.serverVersion) &&
                Objects.equals(kafkaClusterId, that.kafkaClusterId) &&
                Objects.equals(ksqlServiceId, that.ksqlServiceId) &&
                Objects.equals(host, that.host) &&
                Objects.equals(port, that.port);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serverVersion, kafkaClusterId, ksqlServiceId, host, port);
    }

    @Override
    public String toString() {
        return "ServerInfo{" +
                "serverVersion='" + serverVersion + '\'' +
                ", kafkaClusterId='" + kafkaClusterId + '\'' +
                ", ksqlServiceId='" + ksqlServiceId + '\'' +
                ", host='" + host + '\'' +
                ", port=" + port +
                '}';
    }
}