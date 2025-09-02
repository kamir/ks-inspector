# Running KSQLDB with Confluent Cloud

This guide explains how to run KSQLDB locally using Docker with Confluent Cloud as the backend Kafka cluster.

## Prerequisites

1. Docker and Docker Compose installed
2. Confluent Cloud account with a Kafka cluster
3. API keys for your Confluent Cloud cluster
4. Schema Registry enabled (optional)

## Setup Instructions

### 1. Configure Environment Variables

First, set up your Confluent Cloud credentials:

```bash
cd /Users/kamir/GITHUB.cflt/ks-inspector
cp bin/setenv.sh.example bin/setenv.sh
```

Edit `bin/setenv.sh` and replace the placeholder values with your actual Confluent Cloud credentials:

```bash
# Replace these with your actual API keys and secrets
export KST_CLUSTER_API_KEY_cluster_0="YOUR_ACTUAL_API_KEY"
export KST_CLUSTER_API_SECRET_cluster_0="YOUR_ACTUAL_API_SECRET"
export KST_SR_API_KEY_cluster_0="YOUR_SCHEMA_REGISTRY_API_KEY"
export KST_SR_API_SECRET_cluster_0="YOUR_SCHEMA_REGISTRY_API_SECRET"

# Update with your actual Confluent Cloud cluster endpoint
export KST_BOOTSTRAP_SERVER="your-cluster-endpoint.confluent.cloud:9092"
# Update with your actual Schema Registry endpoint
export KST_SCHEMA_REGISTRY_URL="https://your-schema-registry.confluent.cloud"
```

### 2. Start KSQLDB

Run the KSQLDB services using Docker Compose:

```bash
bin/run-ksqldb-ccloud.sh
```

This will start:
- A KSQLDB server connected to your Confluent Cloud cluster
- A KSQLDB CLI container for interactive queries

### 3. Access KSQLDB CLI

Once the services are running, access the KSQLDB CLI:

```bash
docker exec -it ksqldb-cli ksql http://ksqldb-server:8088
```

### 4. Test the Connection

You can test the connection with:

```bash
bin/test-ksqldb-ccloud.sh
```

### 5. Stop KSQLDB

To stop the services:

```bash
bin/stop-ksqldb-ccloud.sh
```

## Using with KS-Inspector

Once KSQLDB is running, you can use the KS-Inspector tool to inspect your Confluent Cloud cluster:

```bash
# Inspect KSQLDB cluster and save to YAML
java -cp target/ks-inspector-2.6.1.jar \
    io.confluent.mdgraph.cli.CLI inspectKSQLDB \
    --host localhost \
    --port 8088 \
    --output ksqldb-inventory.yaml

# Export directly to Neo4j
java -cp target/ks-inspector-2.6.1.jar \
    io.confluent.mdgraph.cli.CLI inspectKSQLDB \
    --host localhost \
    --port 8088 \
    --output ksqldb-inventory.yaml \
    --neo4j-uri bolt://localhost:7687 \
    --neo4j-user neo4j \
    --neo4j-password admin
```

## Troubleshooting

### Common Issues

1. **Docker not running**: Make sure Docker daemon is running
2. **Authentication failed**: Verify your API keys and secrets in setenv.sh
3. **Connection timeout**: Check your network connectivity to Confluent Cloud
4. **Port already in use**: Stop other services using port 8088

### Debugging Tips

1. Check Docker container logs:
   ```bash
   docker logs ksqldb-server
   ```

2. Verify environment variables are loaded:
   ```bash
   source bin/setenv.sh
   echo $KST_BOOTSTRAP_SERVER
   ```

3. Test Kafka connectivity directly:
   ```bash
   bin/test-config.sh
   ```

## Security Best Practices

1. Never commit your `setenv.sh` file to version control
2. Use separate API keys for different environments
3. Regularly rotate your API keys
4. Restrict API key permissions to only what is necessary for KSQLDB

## Docker Compose Configuration

The Docker Compose file (`bin/ksqldb-ccloud-docker-compose.yml`) configures:

- **ksqldb-server**: KSQLDB server connected to Confluent Cloud
- **ksqldb-cli**: Interactive CLI for running KSQL queries

Key configuration parameters:
- Bootstrap servers from Confluent Cloud
- SASL/SSL security configuration
- Schema Registry integration (if enabled)
- Automatic topic and stream creation