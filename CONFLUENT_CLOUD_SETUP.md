# Confluent Cloud Setup Guide for KS-Inspector

This guide will help you configure KS-Inspector to work with your Confluent Cloud cluster.

## Prerequisites

1. A Confluent Cloud account
2. A Kafka cluster in Confluent Cloud
3. API keys for your cluster
4. Schema Registry enabled (if you plan to use it)

## Configuration Steps

### 1. Update the setenv.sh file

Copy the template and update it with your Confluent Cloud credentials:

```bash
cd /Users/kamir/GITHUB.cflt/ks-inspector/bin
cp setenv.sh.template setenv.sh
```

Or use the example file as a reference:

```bash
cd /Users/kamir/GITHUB.cflt/ks-inspector/bin
cp setenv.sh.example setenv.sh
```

Edit [setenv.sh](file:///Users/kamir/GITHUB.cflt/ks-inspector/bin/setenv.sh) and replace the placeholder values with your actual Confluent Cloud credentials:

```bash
# Replace these with your actual API keys and secrets
export KST_CLUSTER_API_KEY_cluster_0=YOUR_ACTUAL_API_KEY
export KST_CLUSTER_API_SECRET_cluster_0=YOUR_ACTUAL_API_SECRET
export KST_SR_API_KEY_cluster_0=YOUR_SCHEMA_REGISTRY_API_KEY
export KST_SR_API_SECRET_cluster_0=YOUR_SCHEMA_REGISTRY_API_SECRET

# Update with your actual Confluent Cloud cluster endpoint
export KST_BOOTSTRAP_SERVER=your-cluster-endpoint.confluent.cloud:9092
# Update with your actual Schema Registry endpoint
export KST_SCHEMA_REGISTRY_URL=https://your-schema-registry.confluent.cloud
```

### 2. Update the kst.properties file

Edit the [kst.properties](file:///Users/kamir/GITHUB.cflt/ks-inspector/src/main/cluster-state-tools-data/example1/kst.properties) file in your example directory:

```bash
cd /Users/kamir/GITHUB.cflt/ks-inspector/src/main/cluster-state-tools-data/example1
```

Replace the placeholder values with your actual Confluent Cloud cluster details:

```properties
# Kafka - Update these with your Confluent Cloud cluster details
bootstrap.servers=YOUR_CONFLUENT_CLOUD_CLUSTER_ENDPOINT:9092
security.protocol=SASL_SSL
sasl.jaas.config=org.apache.kafka.common.security.plain.PlainLoginModule required username='YOUR_API_KEY' password='YOUR_API_SECRET';
sasl.mechanism=PLAIN

# Confluent Cloud Schema Registry - Update these with your Schema Registry details
schema.registry.url=https://YOUR_SCHEMA_REGISTRY_ENDPOINT.confluent.cloud
basic.auth.credentials.source=USER_INFO
schema.registry.basic.auth.user.info=YOUR_SR_API_KEY:YOUR_SR_API_SECRET

# Neo4J Configuration
KST_NEO4J_DATABASE_DIRECTORY=graph-db
KST_NEO4J_DEFAULT_DATABASE_NAME=neo4j
KST_NEO4J_URI=bolt://localhost:7687
KST_NEO4J_USERNAME=neo4j
KST_NEO4J_PASSWORD=admin
```

### 3. Required Confluent Cloud Setup

Make sure your Confluent Cloud cluster has the following:

1. **API Keys**: Create API keys for your cluster in the Confluent Cloud UI
2. **Schema Registry**: Enable Schema Registry if you plan to use it
3. **Topics**: The application may need to create topics, so ensure your API keys have the necessary permissions

### 4. Test Your Configuration

Use the provided test script to verify your configuration:

```bash
cd /Users/kamir/GITHUB.cflt/ks-inspector
./bin/test-config.sh
```

If the test passes, you're ready to run the application.

### 5. Running the Application

After configuration, you can run the inspection:

```bash
cd /Users/kamir/GITHUB.cflt/ks-inspector
./bin/run-03-inspect-domains.sh
```

## Troubleshooting

### Common Issues

1. **Bootstrap server not resolvable**: Double-check your cluster endpoint
2. **Authentication failed**: Verify your API key and secret
3. **Topic creation permissions**: Ensure your API key has the necessary permissions

### Debugging Tips

1. Check that all placeholder values have been replaced
2. Verify network connectivity to your Confluent Cloud cluster
3. Confirm that your API keys have the correct permissions

## Environment Variables Reference

The application uses the following environment variables:

- `KST_BOOTSTRAP_SERVER`: Confluent Cloud cluster endpoint
- `KST_SECURITY_PROTOCOL`: Security protocol (should be SASL_SSL for Confluent Cloud)
- `KST_SASL_MECHANISM`: SASL mechanism (should be PLAIN for Confluent Cloud)
- `KST_SASL_JAAS_CONFIG`: JAAS configuration with your API key and secret
- `KST_SCHEMA_REGISTRY_URL`: Schema Registry endpoint
- `KST_SCHEMA_REGISTRY_BASIC_AUTH_CREDENTIALS_SOURCE`: Authentication source
- `KST_SCHEMA_REGISTRY_BASIC_AUTH_USER_INFO`: Schema Registry API key and secret

## Security Best Practices

1. Never commit API keys to version control
2. Use separate API keys for different environments (dev, test, prod)
3. Regularly rotate your API keys
4. Restrict API key permissions to only what is necessary