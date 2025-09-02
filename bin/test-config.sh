#!/bin/bash

# Test script to verify Confluent Cloud configuration

echo "=== KS-Inspector Configuration Test ==="

# Source the version configuration
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "${SCRIPT_DIR}/version.conf" 2>/dev/null || export KSI_VERSION="2.6.1"

# Source environment variables
if [ -f "${SCRIPT_DIR}/.env" ]; then
    echo "✅ Found .env file, sourcing configuration..."
    source "${SCRIPT_DIR}/.env"
elif [ -f "${SCRIPT_DIR}/setenv.sh" ]; then
    echo "✅ Found setenv.sh, sourcing configuration..."
    source "${SCRIPT_DIR}/setenv.sh"
else
    echo "❌ WARNING: Neither .env nor setenv.sh found"
    echo "   Please create one of these files with your Confluent Cloud credentials"
    echo "   For security, create a .env file (excluded from git) with your credentials"
    exit 1
fi

echo "✅ Successfully sourced configuration file"

# Check if required environment variables are set
REQUIRED_VARS=("KST_BOOTSTRAP_SERVER" "KST_SECURITY_PROTOCOL" "KST_SASL_MECHANISM")

MISSING_VARS=()
for var in "${REQUIRED_VARS[@]}"; do
    if [ -z "${!var}" ]; then
        MISSING_VARS+=("$var")
    fi
done

if [ ${#MISSING_VARS[@]} -ne 0 ]; then
    echo "❌ Missing required environment variables:"
    for var in "${MISSING_VARS[@]}"; do
        echo "   - $var"
    done
    echo "   Please update your configuration file with proper values"
    exit 1
fi

echo "✅ All required environment variables are set"

# Display configuration summary
echo ""
echo "=== Configuration Summary ==="
echo "Bootstrap Server: $KST_BOOTSTRAP_SERVER"
echo "Security Protocol: $KST_SECURITY_PROTOCOL"
echo "SASL Mechanism: $KST_SASL_MECHANISM"
echo "Schema Registry URL: ${KST_SCHEMA_REGISTRY_URL:-Not set}"

# Check for placeholder values
PLACEHOLDER_DETECTED=false
if [[ "$KST_BOOTSTRAP_SERVER" == *"your-cluster-endpoint"* ]]; then
    echo "❌ WARNING: Bootstrap server still contains placeholder value"
    PLACEHOLDER_DETECTED=true
fi

if [[ "$KST_CLUSTER_API_KEY_cluster_0" == "YOUR_API_KEY_HERE" ]] || [[ "$KST_CLUSTER_API_KEY" == "YOUR_API_KEY_HERE" ]]; then
    echo "❌ WARNING: API key still contains placeholder value"
    PLACEHOLDER_DETECTED=true
fi

if [[ "$KST_CLUSTER_API_SECRET_cluster_0" == "YOUR_API_SECRET_HERE" ]] || [[ "$KST_CLUSTER_API_SECRET" == "YOUR_API_SECRET_HERE" ]]; then
    echo "❌ WARNING: API secret still contains placeholder value"
    PLACEHOLDER_DETECTED=true
fi

if [ "$PLACEHOLDER_DETECTED" = true ]; then
    echo "💡 Please update your configuration file with your actual Confluent Cloud credentials"
    exit 1
fi

# Check if JAR file exists
JAR_FILE="target/ks-inspector-${KSI_VERSION}.jar"
if [ ! -f "$JAR_FILE" ]; then
    echo "❌ JAR file not found: $JAR_FILE"
    echo "   Please build the project first with: mvn clean package"
    exit 1
fi

echo "✅ JAR file found: $JAR_FILE"

echo ""
echo "=== Configuration Test Complete ==="
echo "✅ Your configuration appears to be set up correctly!"
echo "💡 To run the domain inspection, use: ./bin/run-03-inspect-domains.sh"