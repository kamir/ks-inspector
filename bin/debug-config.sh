#!/bin/bash

# Debug script to check configuration

echo "=== Debug Configuration ==="

# Source the version configuration
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "${SCRIPT_DIR}/version.conf" 2>/dev/null || export KSI_VERSION="2.6.1"

# Source environment variables for Confluent Cloud configuration
if [ -f "${SCRIPT_DIR}/.env" ]; then
    source "${SCRIPT_DIR}/.env"
    echo "✅ Using Confluent Cloud configuration from .env file"
else
    echo "❌ .env file not found"
    exit 1
fi

echo ""
echo "=== Environment Variables ==="
echo "KST_BOOTSTRAP_SERVER: $KST_BOOTSTRAP_SERVER"
echo "KST_SECURITY_PROTOCOL: $KST_SECURITY_PROTOCOL"
echo "KST_SASL_MECHANISM: $KST_SASL_MECHANISM"
echo "KST_CLUSTER_API_KEY: $KST_CLUSTER_API_KEY"
echo "KST_CLUSTER_API_SECRET: $KST_CLUSTER_API_SECRET"
echo "KST_SCHEMA_REGISTRY_URL: $KST_SCHEMA_REGISTRY_URL"
echo "KST_SCHEMA_REGISTRY_BASIC_AUTH_USER_INFO: $KST_SCHEMA_REGISTRY_BASIC_AUTH_USER_INFO"

echo ""
echo "=== All KST Environment Variables ==="
env | grep KST_

echo ""
echo "=== Testing Java with Environment Variables ==="
cd /Users/kamir/GITHUB.cflt/ks-inspector
echo "Running simple test to check if environment variables are accessible to Java..."

# Create a simple test Java class
cat > /tmp/EnvTest.java << 'EOF'
public class EnvTest {
    public static void main(String[] args) {
        System.out.println("KST_BOOTSTRAP_SERVER: " + System.getenv("KST_BOOTSTRAP_SERVER"));
        System.out.println("KST_CLUSTER_API_KEY: " + System.getenv("KST_CLUSTER_API_KEY"));
        System.out.println("All KST variables:");
        System.getenv().entrySet().stream()
            .filter(entry -> entry.getKey().startsWith("KST_"))
            .forEach(entry -> System.out.println("  " + entry.getKey() + "=" + entry.getValue()));
    }
}
EOF

javac /tmp/EnvTest.java
java -cp /tmp EnvTest
rm /tmp/EnvTest.java /tmp/EnvTest.class