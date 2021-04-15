#
# Example : How to provide Cluster Credentials via ENV-Variables to the KST tool?
#
source setenv.sh

cd ..

export KST_TARGET_CLUSTER_NAME=cluster_0

java -cp target/ks-inspector-1.0-SNAPSHOT.jar io.confluent.cp.governanceprocess.PropertyFileExporter

export KST_TARGET_CLUSTER_NAME=PROD-cluster-101

java -cp target/ks-inspector-1.0-SNAPSHOT.jar io.confluent.cp.governanceprocess.PropertyFileExporter
