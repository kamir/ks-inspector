#!/bin/bash

# Script to run KSQLDB with Confluent Cloud as the backend using Docker Compose

echo "🚀 Starting KSQLDB with Confluent Cloud backend..."

# Check if Docker is available
if ! command -v docker &> /dev/null
then
    echo "❌ Docker is required but not installed. Please install Docker and try again."
    exit 1
fi

# Check if Docker Compose is available
if ! command -v docker-compose &> /dev/null
then
    echo "❌ Docker Compose is required but not installed. Please install Docker Compose and try again."
    exit 1
fi

# Check if setenv.sh exists
if [ ! -f "./setenv.sh" ]; then
    echo "❌ setenv.sh not found. Please create it by copying setenv.sh.example and updating with your Confluent Cloud credentials."
    echo "   cp bin/setenv.sh.example bin/setenv.sh"
    echo "   Then edit bin/setenv.sh with your actual Confluent Cloud credentials."
    exit 1
fi

# Source the environment variables
echo "📄 Loading Confluent Cloud configuration..."
source ./setenv.sh


# Validate required environment variables
if [ -z "$KST_BOOTSTRAP_SERVER" ] || [ -z "$KST_CLUSTER_API_KEY" ] || [ -z "$KST_CLUSTER_API_SECRET" ]; then
    echo "❌ Required Confluent Cloud configuration missing. Please check your setenv.sh file."
    exit 1
fi

echo "📋 Configuration summary:"
echo "   Bootstrap Server: $KST_BOOTSTRAP_SERVER"
echo "   Security Protocol: $KST_SECURITY_PROTOCOL"
echo "   SASL Mechanism: $KST_SASL_MECHANISM"
echo "   Schema Registry: $KST_SCHEMA_REGISTRY_URL"

# Create ksqldb-scripts directory if it doesn't exist
mkdir -p ./ksqldb-scripts

# Start the Docker Compose services
echo "🐳 Starting KSQLDB services..."
docker-compose -f ksqldb-ccloud-docker-compose.yml up -d

if [ $? -eq 0 ]; then
    echo "✅ KSQLDB services started successfully!"
    echo ""
    echo "📊 Access KSQLDB Server at: http://localhost:8088"
    echo ""
    echo "🔧 To access the KSQLDB CLI, run:"
    echo "   docker exec -it ksqldb-cli ksql http://ksqldb-server:8088"
    echo ""
    echo "🛑 To stop the services, run:"
    echo "   bin/stop-ksqldb-ccloud.sh"
else
    echo "❌ Failed to start KSQLDB services."
    exit 1
fi