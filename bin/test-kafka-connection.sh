#!/bin/bash

# Test script to verify Kafka connection with your credentials

echo "=== Test Kafka Connection ==="

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
echo "=== Kafka Configuration ==="
echo "Bootstrap Servers: $KST_BOOTSTRAP_SERVERS"
echo "Security Protocol: $KST_SECURITY_PROTOCOL"
echo "SASL Mechanism: $KST_SASL_MECHANISM"

echo ""
echo "=== Testing Kafka Connection ==="

# Create a simple Kafka client test
cat > /tmp/KafkaTest.java << 'EOF'
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.ListTopicsResult;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class KafkaTest {
    public static void main(String[] args) {
        Properties props = new Properties();
        
        // Get properties from environment variables
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, System.getenv("KST_BOOTSTRAP_SERVERS"));
        props.put("security.protocol", System.getenv("KST_SECURITY_PROTOCOL"));
        props.put("sasl.mechanism", System.getenv("KST_SASL_MECHANISM"));
        props.put("sasl.jaas.config", System.getenv("KST_SASL_JAAS_CONFIG"));
        
        System.out.println("Connecting to Kafka cluster...");
        System.out.println("Bootstrap Servers: " + props.get(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG));
        System.out.println("Security Protocol: " + props.get("security.protocol"));
        System.out.println("SASL Mechanism: " + props.get("sasl.mechanism"));
        
        try (AdminClient adminClient = AdminClient.create(props)) {
            ListTopicsResult topics = adminClient.listTopics();
            topics.names().get(10, TimeUnit.SECONDS);
            System.out.println("✅ Successfully connected to Kafka cluster!");
            System.out.println("Available topics: " + topics.names().get());
        } catch (Exception e) {
            System.err.println("❌ Failed to connect to Kafka cluster:");
            e.printStackTrace();
        }
    }
}
EOF

cd /Users/kamir/GITHUB.cflt/ks-inspector

# Compile and run the test
javac -cp "target/ks-inspector-${KSI_VERSION}.jar:target/lib/*" /tmp/KafkaTest.java
if [ $? -eq 0 ]; then
    java -cp "/tmp:target/ks-inspector-${KSI_VERSION}.jar:target/lib/*" KafkaTest
    rm /tmp/KafkaTest.java /tmp/KafkaTest.class
else
    echo "❌ Failed to compile Kafka test"
    rm /tmp/KafkaTest.java
fi