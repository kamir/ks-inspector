#!/bin/sh

#
# This script works on a demo cluster and creates the topics needed for the KnowledgeGraph tool.
#
# THIS IS A LOCAL CP ALL IN ONE CLUSTER
#
export CLUSTER_ID=pUl8u1yHQNu6p2_29LqlPA

#
# temporary folder for local graph-db (embedded Neo4J9
#
mkdir graph-db

#
# We have to create a topic named: _kst_knowledgegraph
#
# The commands will be executed in the folder with the docker-compose file.
#
docker-compose exec broker kafka-topics --delete --topic _kst_knowledgegraph --bootstrap-server broker:9092

docker-compose exec broker kafka-topics --create --topic _kst_knowledgegraph --bootstrap-server broker:9092 --partitions 1 --if-not-exists --config retention.ms=-1 --config retention.bytes=-1
