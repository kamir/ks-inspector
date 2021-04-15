#
# Example : How to provide Cluster Credentials via ENV-Variables to the KST tool?
#
source setenv.sh

cd ..
#mvn clean compile package

export KST_TARGET_CLUSTER_NAME=cluster_0
#export KST_TARGET_CLUSTER_NAME=PROD-cluster-101

java -cp target/ks-inspector-1.0-SNAPSHOT.jar io.confluent.cp.util.KSTWrapper ./src/main/cluster-state-tools-data/example0/environments.yaml

echo
