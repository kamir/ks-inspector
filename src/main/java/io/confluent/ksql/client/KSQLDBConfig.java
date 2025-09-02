package io.confluent.ksql.client;

import java.util.Objects;

/**
 * Configuration class for KSQLDB client connections.
 */
public class KSQLDBConfig {
    private String host;
    private int port;
    private String username;
    private String password;
    private boolean useTls;
    private String basePath;

    public KSQLDBConfig() {
        this.host = "localhost";
        this.port = 8088;
        this.useTls = false;
        this.basePath = "";
    }

    public KSQLDBConfig(String host, int port) {
        this.host = host;
        this.port = port;
        this.useTls = false;
        this.basePath = "";
    }

    public String getHost() {
        return host;
    }

    public KSQLDBConfig setHost(String host) {
        this.host = host;
        return this;
    }

    public int getPort() {
        return port;
    }

    public KSQLDBConfig setPort(int port) {
        this.port = port;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public KSQLDBConfig setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public KSQLDBConfig setPassword(String password) {
        this.password = password;
        return this;
    }

    public boolean isUseTls() {
        return useTls;
    }

    public KSQLDBConfig setUseTls(boolean useTls) {
        this.useTls = useTls;
        return this;
    }

    public String getBasePath() {
        return basePath;
    }

    public KSQLDBConfig setBasePath(String basePath) {
        this.basePath = basePath;
        return this;
    }

    /**
     * Get the full URL for the KSQLDB server.
     * @return The full URL including protocol, host, port, and base path.
     */
    public String getServerAddress() {
        String protocol = useTls ? "https" : "http";
        String address = String.format("%s://%s:%d", protocol, host, port);
        if (basePath != null && !basePath.isEmpty()) {
            if (!basePath.startsWith("/")) {
                address += "/";
            }
            address += basePath;
        }
        return address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KSQLDBConfig that = (KSQLDBConfig) o;
        return port == that.port &&
                useTls == that.useTls &&
                Objects.equals(host, that.host) &&
                Objects.equals(username, that.username) &&
                Objects.equals(password, that.password) &&
                Objects.equals(basePath, that.basePath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(host, port, username, password, useTls, basePath);
    }

    @Override
    public String toString() {
        return "KSQLDBConfig{" +
                "host='" + host + '\'' +
                ", port=" + port +
                ", username='" + username + '\'' +
                ", useTls=" + useTls +
                ", basePath='" + basePath + '\'' +
                '}';
    }
}