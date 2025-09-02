#!/bin/bash

# Example script for using the KSQLDB inspection functionality

echo "=== KSQLDB Cluster Inspection Example ==="

# Build the project first
echo "Building the project..."
mvn clean package -DskipTests

if [ $? -ne 0 ]; then
    echo "Build failed!"
    exit 1
fi

echo "Build successful!"

# Example 1: Basic inspection with YAML output
echo
echo "=== Example 1: Basic inspection with YAML output ==="
java -cp target/ks-inspector-2.6.1.jar \
    io.confluent.mdgraph.cli.CLI inspectKSQLDB \
    --host localhost \
    --port 8088 \
    --output ksqldb-inventory.yaml

echo "Inventory saved to ksqldb-inventory.yaml"

# Example 2: Inspection with authentication
echo
echo "=== Example 2: Inspection with authentication ==="
java -cp target/ks-inspector-2.6.1.jar \
    io.confluent.mdgraph.cli.CLI inspectKSQLDB \
    --host localhost \
    --port 8088 \
    --username ksql \
    --password secret \
    --tls \
    --output ksqldb-inventory-secure.yaml

echo "Secure inventory saved to ksqldb-inventory-secure.yaml"

# Example 3: Inspection with Neo4j export
echo
echo "=== Example 3: Inspection with Neo4j export ==="
java -cp target/ks-inspector-2.6.1.jar \
    io.confluent.mdgraph.cli.CLI inspectKSQLDB \
    --host localhost \
    --port 8088 \
    --output ksqldb-inventory-neo4j.yaml \
    --neo4j-uri bolt://localhost:7687 \
    --neo4j-user neo4j \
    --neo4j-password admin

echo "Inventory saved to ksqldb-inventory-neo4j.yaml and exported to Neo4j"

echo
echo "=== All examples completed ==="