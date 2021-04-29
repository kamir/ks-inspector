#
# Create a lineage graph for our KSQL query template file.
#

cd ..

mvn clean compile package

java -cp target/ks-inspector-1.0-SNAPSHOT.jar io.confluent.cp.mdmodel.ksql.KSQLQueryInspector 100_KSQLDB_training_intro.template.explained
java -cp target/ks-inspector-1.0-SNAPSHOT.jar io.confluent.cp.mdmodel.ksql.KSQLQueryInspector 100_KSQLDB_training_intro.template.inspect
java -cp target/ks-inspector-1.0-SNAPSHOT.jar io.confluent.cp.mdmodel.ksql.KSQLQueryInspector 200_EGrid_PoC_solution.sql
java -cp target/ks-inspector-1.0-SNAPSHOT.jar io.confluent.cp.mdmodel.ksql.KSQLQueryInspector 300_Example_MD_enrichment.sql
java -cp target/ks-inspector-1.0-SNAPSHOT.jar io.confluent.cp.mdmodel.ksql.KSQLQueryInspector 400_opentsx_demo.ksql
