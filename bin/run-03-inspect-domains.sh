#!/bin/bash

# Run domain inspection with Confluent Cloud configuration

echo ">>> Run the domain inspection example ..."

# Source the version configuration
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "${SCRIPT_DIR}/version.conf" 2>/dev/null || export KSI_VERSION="2.6.1"

# Source environment variables for Confluent Cloud configuration
if [ -f "${SCRIPT_DIR}/.env" ]; then
    source "${SCRIPT_DIR}/.env"
    echo "✅ Using Confluent Cloud configuration from .env file"
elif [ -f "${SCRIPT_DIR}/setenv.sh" ]; then
    source "${SCRIPT_DIR}/setenv.sh"
    echo "✅ Using Confluent Cloud configuration from setenv.sh"
else
    echo "⚠️  Warning: No configuration file found, using localhost configuration"
    export KST_BOOTSTRAP_SERVERS="localhost:9092"
fi

# Check if we have a valid bootstrap server
if [ -z "$KST_BOOTSTRAP_SERVERS" ] || [[ "$KST_BOOTSTRAP_SERVERS" == *"your-cluster-endpoint"* ]]; then
    echo "❌ Error: Invalid bootstrap server configuration"
    echo "   Please configure your Confluent Cloud credentials in .env or setenv.sh"
    exit 1
fi

# Run all steps now that Neo4j is running
echo ">>> Using bootstrap server: $KST_BOOTSTRAP_SERVERS"

echo ">>> Step 1: Inspect Domain"
java -jar target/ks-inspector-${KSI_VERSION}.jar inspectDomain  \
        -bss ${KST_BOOTSTRAP_SERVERS} \
        -wp ./src/main/cluster-state-tools-data/example1 \
        ./src/main/cluster-state-tools-data/example1

if [ $? -eq 0 ]; then
    echo ">>> Step 2: Inspect Schema Registry"
    java -jar target/ks-inspector-${KSI_VERSION}.jar inspectSchemaRegistry  \
            -bss ${KST_BOOTSTRAP_SERVERS} \
            -wp ./src/main/cluster-state-tools-data/example1 \
            ./src/main/cluster-state-tools-data/example1

    if [ $? -eq 0 ]; then
        echo ">>> Step 3: Export to Neo4J"
        java -jar target/ks-inspector-${KSI_VERSION}.jar export2Neo4J  \
                -bss ${KST_BOOTSTRAP_SERVERS} \
                -wp ./src/main/cluster-state-tools-data/example1 \
                ./src/main/cluster-state-tools-data/example1
        
        if [ $? -eq 0 ]; then
            echo ">>> All steps completed successfully!"
        else
            echo "❌ Step 3 failed"
        fi
    else
        echo "❌ Step 2 failed"
    fi
else
    echo "❌ Step 1 failed"
fi

echo ">>> Domain inspection completed!"