#!/bin/sh

#
# This script provides a local CP cluster on a DEV machine.
#

export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.8.0_221.jdk/Contents/Home
export CONFLUENT_HOME=/Users/mkampf/bin/confluent-6.0.1


confluent local status

# confluent local destroy

confluent local start