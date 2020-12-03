#
# Example : How to provide Cluster Credentials via ENV-Variables to the KST tool?
#
source setenv.sh

cd ..

export KST_TARGET_CLUSTER_NAME=cluster_0
#export KST_TARGET_CLUSTER_NAME=cluster_1_QA
#export KST_TARGET_CLUSTER_NAME=cluster_1_PROD

java -cp target/ks-inspector-1.0-SNAPSHOT.jar io.confluent.cp.apps.KafkaStreamsExample01