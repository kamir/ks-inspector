#!/bin/sh

# Source the version configuration
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "${SCRIPT_DIR}/version.conf" 2>/dev/null || export KSI_VERSION="2.6.1"

#
# We build the KST library
#
cd ../kafka-clusterstate-tools
gradlew clean build install -x test -x integrationTest

#
# Now we build the KSI library
#
cd ..
mvn clean compile package install -Dproject.version=${KSI_VERSION}