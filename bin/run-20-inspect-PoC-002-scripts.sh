#
# Create a lineage graph for our KSQL query template file.
#

cd ..

mvn clean compile package

for f in ksqldb-query-stage/query-to-deploy-to-ksql-server/PoC-002/*
do

  IN=$f
  arrIN=(${IN//// })

  po=${arrIN[2]}
  fn=${arrIN[3]}

  echo "$f -> $po/$fn"

  java -cp target/ks-inspector-1.0-SNAPSHOT.jar io.confluent.cp.mdmodel.ksql.KSQLQueryInspector $po/$fn

done