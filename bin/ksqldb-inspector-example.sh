#!/bin/bash

# KSQLDB Inspector Example Script
# This script demonstrates how to use the KSQLDB inspection functionality

echo "KSQLDB Inspector Example"
echo "========================"

# Check if Maven is available
if ! command -v mvn &> /dev/null
then
    echo "Maven is required but not installed. Please install Maven and try again."
    exit 1
fi

# Compile the project
echo "Compiling the project..."
mvn compile

if [ $? -ne 0 ]; then
    echo "Compilation failed. Please check the errors above."
    exit 1
fi

# Run the KSQLDB client example
echo "Running KSQLDB client example..."
mvn exec:java -Dexec.mainClass="io.confluent.ksql.client.KSQLDBClientExample"

echo "Example completed."