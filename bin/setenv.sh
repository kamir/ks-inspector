#=======================================================================================================================
# Confluent Cloud Configuration for KS-Inspector - EXAMPLE
#=======================================================================================================================

# Version configuration
export KSI_VERSION="2.6.1"

########################################################################################################################
# Confluent Cloud API Keys and Secrets
# Please replace these placeholder values with your actual Confluent Cloud credentials
#-----------------------------------------------------------------------------------------------------------------------
# Environment variables for KSQLDB Docker Compose
# This file contains only simple key=value pairs for Docker compatibility

# Confluent Cloud Configuration
export KST_BOOTSTRAP_SERVER=pkc-619z3.us-east1.gcp.confluent.cloud:9092
export KST_SECURITY_PROTOCOL=SASL_SSL
export KST_SASL_MECHANISM=PLAIN
export KST_SASL_JAAS_CONFIG="org.apache.kafka.common.security.plain.PlainLoginModule required username='5DIC7ADQTDV43IZN' password='cflt/DOxaMUtNPon0Ko/OteSVYKi7LSpVjjhSJcgmW+6MEJyW8bc/PofsiXX+k3Q';"
export KST_SCHEMA_REGISTRY_URL="https://psrc-lo5k9.eu-central-1.aws.confluent.cloud"
export KST_SCHEMA_REGISTRY_BASIC_AUTH_CREDENTIALS_SOURCE=USER_INFO
export KST_SCHEMA_REGISTRY_BASIC_AUTH_USER_INFO="LYJ232YPTURCSJRV:cfltCsZ/YJXuEUM7kJGbiVGXT2J5MFBw1luF1X5WxUD1I191qPArvEBH8EZJ0HvA"
export KST_CLUSTER_API_KEY=5DIC7ADQTDV43IZN
export KST_CLUSTER_API_SECRET=cflt/DOxaMUtNPon0Ko/OteSVYKi7LSpVjjhSJcgmW+6MEJyW8bc/PofsiXX+k3Q

# Kafka Cluster Connection
#export bootstrap.servers=pkc-619z3.us-east1.gcp.confluent.cloud:9092
#export security.protocol=SASL_SSL
#export sasl.mechanisms=PLAIN
#export sasl.jaas.config="org.apache.kafka.common.security.plain.PlainLoginModule required username='5DIC7ADQTDV43IZN' password='cflt/DOxaMUtNPon0Ko/OteSVYKi7LSpVjjhSJcgmW+6MEJyW8bc/PofsiXX+k3Q';"

# Schema Registry Connection
#export schema.registry.url=https://psrc-lo5k9.eu-central-1.aws.confluent.cloud
#export basic.auth.credentials.source=USER_INFO
#export basic.auth.user.info=LYJ232YPTURCSJRV:cfltCsZ/YJXuEUM7kJGbiVGXT2J5MFBw1luF1X5WxUD1I191qPArvEBH8EZJ0HvA