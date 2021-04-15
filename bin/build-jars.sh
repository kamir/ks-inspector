#!/bin/sh

#
# We build the KST library
#
cd ../kafka-clusterstate-tools
gradle clean build install -x test -x integrationTest

#
# Now we build the KSI library
#
cd ..
mvn clean compile package install
