docker run \
-e KST_BOOTSTRAP_SERVER=pkc-4yyd6.us-east1.gcp.confluent.cloud:9092 \
-e KST_SSL_ENDPOINT_IDENTIFICATION_ALGORITHM=https \
-e KST_SECURITY_PROTOCOL=SASL_SSL \
-e KST_SASL_MECHANISM=PLAIN \
-e KST_SASL_JAAS_CONFIG=´org.apache.kafka.common.security.plain.PlainLoginModule required username="7PRLJ5F5JX7PKHRN" password="eSzR/gN5qSI+wepTvnLAPg0XT77/J/JDBxoICrwNDdy3JVS9PzPP9dkMYHa/3FF4";’ \
-e KST_SCHEMA_REGISTRY_URL=https://psrc-4v1qj.eu-central-1.aws.confluent.cloud \
-e KST_SCHEMA_REGISTRY_BASIC_AUTH_CREDENTIALS_SOURCE=USER_INFO \
-e KST_SCHEMA_REGISTRY_BASIC_AUTH_USER_INFO=2NOYXIN4EXJDZYTR:ToV9mOn81vDnx/hLxIZK3hl3mstQtjPR1ZdoelVCwGJPFWORQW552+WeJh4Il7b4 \
kst_test:latest