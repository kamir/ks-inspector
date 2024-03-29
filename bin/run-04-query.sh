#!/bin/bash

cd ..

export KST_BOOTSTRAP_SERVER=pkc-4yyd6.us-east1.gcp.confluent.cloud:9092
export KST_SSL_ENDPOINT_IDENTIFICATION_ALGORITHM=https
export KST_SECURITY_PROTOCOL=SASL_SSL
export KST_SASL_MECHANISM=PLAIN
export KST_SASL_JAAS_CONFIG='org.apache.kafka.common.security.plain.PlainLoginModule required username="7PRLJ5F5JX7PKHRN" password="eSzR/gN5qSI+wepTvnLAPg0XT77/J/JDBxoICrwNDdy3JVS9PzPP9dkMYHa/3FF4";'
export KST_SCHEMA_REGISTRY_URL=https://psrc-4v1qj.eu-central-1.aws.confluent.cloud
export KST_SCHEMA_REGISTRY_BASIC_AUTH_CREDENTIALS_SOURCE=USER_INFO
export KST_SCHEMA_REGISTRY_BASIC_AUTH_USER_INFO=2NOYXIN4EXJDZYTR:ToV9mOn81vDnx/hLxIZK3hl3mstQtjPR1ZdoelVCwGJPFWORQW552+WeJh4Il7b4

export KST_NEO4J_DATABASE_DIRECTORY="graphdb";
export KST_NEO4J_DEFAULT_DATABASE_NAME="neo4j";
export KST_NEO4J_URI="bolt://localhost:7687";
export KST_NEO4J_USERNAME="neo4j";
export KST_NEO4J_PASSWORD="test";

########################################################################################################################
# Query the knowledge graph with a standard query, useful for operators.
########################################################################################################################
java -jar target/ks-inspector-1.0-SNAPSHOT.jar queryGraph  \
     -e KST src/main/cypher/cmd/$1.cypher

#------------------------------------------------------------------------
# a very compact tutoorial for cypher queries is available here:
#    https://nick.readthedocs.io/en/latest/Big_Data/neo4j_examples/
#------------------------------------------------------------------------
