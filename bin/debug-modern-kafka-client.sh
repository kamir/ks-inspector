#!/bin/bash

# Debug script to test ModernKafkaClient with your credentials

echo "=== Debug ModernKafkaClient ==="

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
echo "=== Creating Debug Java Class ==="

# Create a simple debug class to test ModernKafkaClient
cat > /tmp/DebugModernKafkaClient.java << 'EOF'
import io.confluent.ks.modern.kafka.ModernKafkaClient;
import java.io.File;
import java.util.Properties;

public class DebugModernKafkaClient {
    public static void main(String[] args) {
        try {
            System.out.println("=== Debug ModernKafkaClient ===");
            
            // Print environment variables
            System.out.println("Environment Variables:");
            System.out.println("  KST_BOOTSTRAP_SERVERS: " + System.getenv("KST_BOOTSTRAP_SERVERS"));
            System.out.println("  KST_SECURITY_PROTOCOL: " + System.getenv("KST_SECURITY_PROTOCOL"));
            System.out.println("  KST_SASL_MECHANISM: " + System.getenv("KST_SASL_MECHANISM"));
            
            // Test ModernKafkaClient
            File configFile = new File("./src/main/cluster-state-tools-data/example1/kst.properties");
            String bootstrapServer = System.getenv("KST_BOOTSTRAP_SERVERS");
            String envVarPrefix = "KST";
            
            System.out.println("\nCalling ModernKafkaClient.fromProperties with:");
            System.out.println("  configFile: " + configFile.getAbsolutePath());
            System.out.println("  bootstrapServer: " + bootstrapServer);
            System.out.println("  envVarPrefix: " + envVarPrefix);
            System.out.println("  configFile exists: " + configFile.exists());
            System.out.println("  configFile canRead: " + configFile.canRead());
            
            ModernKafkaClient client = ModernKafkaClient.fromProperties(configFile, bootstrapServer, envVarPrefix);
            Properties props = client.getKafkaProperties();
            
            System.out.println("\nResulting Properties (first 10):");
            props.entrySet().stream().limit(10).forEach(entry -> {
                System.out.println("  " + entry.getKey() + ": " + entry.getValue());
            });
            
            System.out.println("\nBootstrap server related properties:");
            props.entrySet().stream()
                .filter(entry -> entry.getKey().toString().contains("bootstrap"))
                .forEach(entry -> {
                    System.out.println("  " + entry.getKey() + ": " + entry.getValue());
                });
            
            System.out.println("\nTesting client.describe():");
            client.describe();
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
EOF

cd /Users/kamir/GITHUB.cflt/ks-inspector

# Compile and run the debug class
echo "Compiling DebugModernKafkaClient..."
javac -cp "target/ks-inspector-${KSI_VERSION}.jar" /tmp/DebugModernKafkaClient.java

if [ $? -eq 0 ]; then
    echo "Running DebugModernKafkaClient..."
    java -cp "/tmp:target/ks-inspector-${KSI_VERSION}.jar" DebugModernKafkaClient
    rm /tmp/DebugModernKafkaClient.java /tmp/DebugModernKafkaClient.class
else
    echo "❌ Failed to compile DebugModernKafkaClient"
    rm /tmp/DebugModernKafkaClient.java
fi