#!/bin/bash

# Script to test KSQLDB connection to Confluent Cloud

echo "🧪 Testing KSQLDB connection to Confluent Cloud..."

# Check if setenv.sh exists
if [ ! -f "./setenv.sh" ]; then
    echo "❌ setenv.sh not found. Please create it first."
    exit 1
fi

# Source the environment variables
source ./setenv.sh

# Validate required environment variables
if [ -z "$KST_BOOTSTRAP_SERVER" ] || [ -z "$KST_CLUSTER_API_KEY" ] || [ -z "$KST_CLUSTER_API_SECRET" ]; then
    echo "❌ Required Confluent Cloud configuration missing."
    exit 1
fi

# Test Kafka connectivity first
echo "🔗 Testing Kafka connectivity..."
echo "   Bootstrap Server: $KST_BOOTSTRAP_SERVER"

# Create a temporary ksql file for testing
cat > /tmp/test.ksql << EOF
SHOW TOPICS;
SHOW STREAMS;
SHOW TABLES;
SHOW QUERIES;
EOF

# Try to connect to KSQLDB server (assuming it's running on localhost:8088)
echo "🔍 Testing KSQLDB server connectivity..."
curl -s -X "POST" "http://localhost:8088/info" \
     -H "Content-Type: application/vnd.ksql.v1+json; charset=utf-8" \
     -d '{}' > /tmp/ksqldb-info.json

if [ $? -eq 0 ] && [ -s /tmp/ksqldb-info.json ]; then
    echo "✅ KSQLDB server is accessible!"
    echo "   Server Info: $(cat /tmp/ksqldb-info.json | jq -r '.KsqlServerInfo.version')"
else
    echo "⚠️  KSQLDB server is not accessible at http://localhost:8088"
    echo "   Make sure KSQLDB is running with bin/run-ksqldb-ccloud.sh"
fi

# Clean up
rm -f /tmp/test.ksql /tmp/ksqldb-info.json

echo "🏁 Test completed."