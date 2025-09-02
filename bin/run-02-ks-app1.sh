#!/bin/bash

# Source the version configuration
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "${SCRIPT_DIR}/version.conf" 2>/dev/null || export KSI_VERSION="2.6.1"

echo ">>> Run the Kafka Streams App example ..."

java -cp target/ks-inspector-${KSI_VERSION}.jar io.confluent.cp.apps.KafkaStreamsExample01