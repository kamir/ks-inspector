#!/bin/sh

cd ../kafka-clusterstate-tools
gradle install

cd ..
mvn clean compile package install

