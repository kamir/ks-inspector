#!/bin/bash

cd ..

source setenv.sh


export KST_BOOTSTRAP_SERVER=pkc-4r297.europe-west1.gcp.confluent.cloud:9092
export KST_SCHEMA_REGISTRY_URL=https://psrc-4v1qj.eu-central-1.aws.confluent.cloud

export KST_NEO4J_DATABASE_DIRECTORY="graphdb";
export KST_NEO4J_DEFAULT_DATABASE_NAME="neo4j";
export KST_NEO4J_URI="bolt://localhost:7687";
export KST_NEO4J_USERNAME="neo4j";
export KST_NEO4J_PASSWORD="test";

########################################################################################################################
# Show the commands which are available in the tool.
########################################################################################################################
#
java -jar target/ks-inspector-1.0-SNAPSHOT.jar

########################################################################################################################
# Inspect the application-domain files defined in the DOM files in folder:
########################################################################################################################
#
#   src/main/cluster-state-tools-data/contexts/example1/instances
#
java -jar target/ks-inspector-1.0-SNAPSHOT.jar inspect  \
     -b $KST_BOOTSTRAP_SERVER  \
     -e KST src/main/cluster-state-tools-data/example0/domains

########################################################################################################################
# Inspect the SchemaRegistry data.
########################################################################################################################
#
java -jar target/ks-inspector-1.0-SNAPSHOT.jar inspectSchemaRegistry  \
     -b $KST_BOOTSTRAP_SERVER  \
     -e KST src/main/cluster-state-tools-data/example0/domains

########################################################################################################################
# Export the graph data which is persisted in the Kafka topic named _kst_knowledgegraph.
########################################################################################################################
#
java -jar target/ks-inspector-1.0-SNAPSHOT.jar export2Neo4J  \
     -b $KST_BOOTSTRAP_SERVER \
     -e KST

########################################################################################################################
# Clear the graph in NEO4J.
########################################################################################################################
#
# java -jar target/ks-inspector-1.0-SNAPSHOT.jar clearGraph \
#      -e KST
