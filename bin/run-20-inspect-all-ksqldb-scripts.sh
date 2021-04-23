#
# Create a lineage graph for our KSQL query template file.
#


java -cp target/ks-inspector-1.0-SNAPSHOT.jar io.confluent.cp.mdmodel.ksql.KSQLQueryInspector 100_KSQLDB_training_intro.template.explained
java -cp target/ks-inspector-1.0-SNAPSHOT.jar io.confluent.cp.mdmodel.ksql.KSQLQueryInspector 100_KSQLDB_training_intro.template.inspect
server:latest