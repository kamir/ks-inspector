#
# Example : How to provide Cluster Credentials via ENV-Variables to the KST tool?
#
source setenv.sh

cd ..

java -cp target/ks-inspector-1.0-SNAPSHOT.jar io.confluent.cp.governanceprocess.PropertyFileExporter