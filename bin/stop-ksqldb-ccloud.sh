#!/bin/bash

# Script to stop KSQLDB with Confluent Cloud backend

echo "🛑 Stopping KSQLDB services..."

# Check if Docker Compose is available
if ! command -v docker-compose &> /dev/null
then
    echo "❌ Docker Compose is required but not installed."
    exit 1
fi

# Stop the Docker Compose services
docker-compose -f ksqldb-ccloud-docker-compose.yml down

if [ $? -eq 0 ]; then
    echo "✅ KSQLDB services stopped successfully!"
else
    echo "❌ Failed to stop KSQLDB services."
    exit 1
fi