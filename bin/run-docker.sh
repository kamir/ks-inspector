docker run \
-e KST_BOOTSTRAP_SERVER=pkc-4yyd6.us-east1.gcp.confluent.cloud:9092 \
-e KST_SSL_ENDPOINT_IDENTIFICATION_ALGORITHM=https \
-e KST_SECURITY_PROTOCOL=SASL_SSL \
-e KST_SASL_MECHANISM=PLAIN \
-e KST_SASL_JAAS_CONFIG='org.apache.kafka.common.security.plain.PlainLoginModule required username="7MIELF4TQDRB6LO7" password="RWg4W/R2xHw9g7XyDk63dB7clGj/fnlbzuhByTlEFAAc3a4KWnfPPEGJ9zlXdYz9"'; \
-e KST_SCHEMA_REGISTRY_URL=https://psrc-4v1qj.eu-central-1.aws.confluent.cloud \
-e KST_SCHEMA_REGISTRY_BASIC_AUTH_CREDENTIALS_SOURCE=USER_INFO \
-e KST_SCHEMA_REGISTRY_BASIC_AUTH_USER_INFO=AIR4NTVWXBVPS7DG:RYuFhIcu/z2uhcHsVdNuAQ+w1GwfWbpaf74X9YIAyHig37OX2d4SVG/GDbYIq2p3 \
kstb:latest