#!/bin/sh
#
# This script works with a Confluent cloud cluster and creates the topics needed for the KnowledgeGraph tool.
#

export CLUSTER_ID=lkc-jwgvw

#
# temporary folder for local graph-db (embedded Neo4J9
#
mkdir graph-db

#
# We have to create a topic named: _kst_knowledgegraph
#
ccloud kafka topic delete _kst_knowledgegraph --cluster $CLUSTER_ID
ccloud kafka topic create _kst_knowledgegraph --cluster $CLUSTER_ID --partitions 1 --if-not-exists --config retention.ms=-1,retention.bytes=-1
