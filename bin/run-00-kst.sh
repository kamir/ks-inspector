#
# Example : How to provide Cluster Credentials via ENV-Variables to the KST tool?
#
source setenv.sh

cd ..
mvn clean compile package

#java -cp target/ks-inspector-1.0-SNAPSHOT.jar io.confluent.cp.util.KSTWrapper ./src/main/cluster-state-tools-data/example2/ccloud-environments.yaml

java -cp target/ks-inspector-1.0-SNAPSHOT.jar io.confluent.cp.util.KSTWrapper ./src/main/cluster-state-tools-data/example2/ccloud-environments.yaml Incentives

echo
