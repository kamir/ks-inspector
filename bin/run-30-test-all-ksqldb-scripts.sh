#
# Create a lineage graph for our KSQL query template file.
#

export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.8.0_221.jdk/Contents/Home
export CONFLUENT_HOME=/Users/mkampf/bin/confluent-6.0.1

cd ..
cd ksqldb-query-stage/query-to-deploy-to-ksql-server



#$CONFLUENT_HOME/bin/ksql-test-runner -s 0_test.sql -i testdata/0_input.json -o testdata/0_output.json | grep ">>>"
$CONFLUENT_HOME/bin/ksql-test-runner -s 100_KSQLDB_training_intro.template.inspect -i testdata/100_input.json -o testdata/100_output.json


#100_KSQLDB_training_intro.template.inspect
#200_EGrid_PoC_solution.sql
#300_Example_MD_enrichment.sql
#400_opentsx_demo.ksql
