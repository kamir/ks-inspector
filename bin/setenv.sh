#=======================================================================================================================
# Confluent Cloud Configuration for KS-Inspector
#=======================================================================================================================

# Source the .env file which contains the actual credentials (excluded from git)
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
if [ -f "${SCRIPT_DIR}/.env" ]; then
    source "${SCRIPT_DIR}/.env"
    echo "✅ Sourced credentials from .env file"
else
    echo "⚠️  Warning: .env file not found, using placeholder values"
    
    # Version configuration
    export KSI_VERSION="2.6.1"

    ########################################################################################################################
    # Confluent Cloud API Keys and Secrets
    # Please replace these placeholder values with your actual Confluent Cloud credentials
    #-----------------------------------------------------------------------------------------------------------------------

    # Development/Default Environment
    export KST_CLUSTER_API_KEY_cluster_0="YOUR_API_KEY_HERE"
    export KST_CLUSTER_API_SECRET_cluster_0="YOUR_API_SECRET_HERE"
    export KST_SR_API_KEY_cluster_0="YOUR_SR_API_KEY_HERE"
    export KST_SR_API_SECRET_cluster_0="YOUR_SR_API_SECRET_HERE"

    # QA Environment
    export KST_CLUSTER_API_KEY_cluster_1_QA="YOUR_QA_API_KEY_HERE"
    export KST_CLUSTER_API_SECRET_cluster_1_QA="YOUR_QA_API_SECRET_HERE"
    export KST_SR_API_KEY_cluster_1_QA="YOUR_QA_SR_API_KEY_HERE"
    export KST_SR_API_SECRET_cluster_1_QA="YOUR_QA_SR_API_SECRET_HERE"

    # Production Environment
    export KST_CLUSTER_API_KEY_cluster_1_PROD="YOUR_PROD_API_KEY_HERE"
    export KST_CLUSTER_API_SECRET_cluster_1_PROD="YOUR_PROD_API_SECRET_HERE"
    export KST_SR_API_KEY_cluster_1_PROD="YOUR_PROD_SR_API_KEY_HERE"
    export KST_SR_API_SECRET_cluster_1_PROD="YOUR_PROD_SR_API_SECRET_HERE"

    ########################################################################################################################
    # Environment Selection
    # This allows us to switch between Confluent Cloud environments
    #-----------------------------------------------------------------------------------------------------------------------
    export KST_TARGET_CLUSTER_NAME="cluster_0"
    # export KST_TARGET_CLUSTER_NAME="cluster_1_QA"
    # export KST_TARGET_CLUSTER_NAME="cluster_1_PROD"

    ########################################################################################################################
    # Confluent Cloud Cluster Configuration
    # These values will be set based on the selected environment above
    #-----------------------------------------------------------------------------------------------------------------------

    # Function to set environment-specific variables
    set_cluster_config() {
        local cluster_name=${KST_TARGET_CLUSTER_NAME}
        
        # Set the actual values based on the selected cluster
        case $cluster_name in
            "cluster_0")
                export KST_CLUSTER_API_KEY="${KST_CLUSTER_API_KEY_cluster_0}"
                export KST_CLUSTER_API_SECRET="${KST_CLUSTER_API_SECRET_cluster_0}"
                export KST_SR_API_KEY="${KST_SR_API_KEY_cluster_0}"
                export KST_SR_API_SECRET="${KST_SR_API_SECRET_cluster_0}"
                ;;
            "cluster_1_QA")
                export KST_CLUSTER_API_KEY="${KST_CLUSTER_API_KEY_cluster_1_QA}"
                export KST_CLUSTER_API_SECRET="${KST_CLUSTER_API_SECRET_cluster_1_QA}"
                export KST_SR_API_KEY="${KST_SR_API_KEY_cluster_1_QA}"
                export KST_SR_API_SECRET="${KST_SR_API_SECRET_cluster_1_QA}"
                ;;
            "cluster_1_PROD")
                export KST_CLUSTER_API_KEY="${KST_CLUSTER_API_KEY_cluster_1_PROD}"
                export KST_CLUSTER_API_SECRET="${KST_CLUSTER_API_SECRET_cluster_1_PROD}"
                export KST_SR_API_KEY="${KST_SR_API_KEY_cluster_1_PROD}"
                export KST_SR_API_SECRET="${KST_SR_API_SECRET_cluster_1_PROD}"
                ;;
            *)
                echo "Unknown cluster name: $cluster_name"
                return 1
                ;;
        esac
    }

    # Set the cluster configuration
    set_cluster_config

    # Confluent Cloud Cluster Details
    # Replace these with your actual Confluent Cloud cluster details
    export KST_BOOTSTRAP_SERVER="your-cluster-endpoint.confluent.cloud:9092"
    export KST_SECURITY_PROTOCOL="SASL_SSL"
    export KST_SASL_MECHANISM="PLAIN"
    export KST_SASL_JAAS_CONFIG="org.apache.kafka.common.security.plain.PlainLoginModule required username=\"${KST_CLUSTER_API_KEY}\" password=\"${KST_CLUSTER_API_SECRET}\";"
    export KST_SCHEMA_REGISTRY_URL="https://your-schema-registry.confluent.cloud"
    export KST_SCHEMA_REGISTRY_BASIC_AUTH_CREDENTIALS_SOURCE="USER_INFO"
    export KST_SCHEMA_REGISTRY_BASIC_AUTH_USER_INFO="${KST_SR_API_KEY}:${KST_SR_API_SECRET}"
fi