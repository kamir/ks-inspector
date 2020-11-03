
###################################
# Topics needed in the Use-Case
#
CREATE (orders:Topic { name: 'orders', compacted: 'no', nrPartitions: '5', replication: '3' })
CREATE (articles:Topic { name: 'articles', compacted: 'no', nrPartitions: '1', replication: '8' })
CREATE (customers:Topic { name: 'customers', compacted: 'yes', nrPartitions: '10', replication: '3' })

CREATE (UseCase2:UC { name: 'order-processing', critical: 'yes', owner: "Team A" })

CREATE (teama:Team { name: 'Team A' })

###################################
# Link the Use-Case to the topics
#
MATCH (a:Topic),(b:UC)
  WHERE a.name = 'orders' AND b.name = 'order-processing'
CREATE (a)-[r:consumedInContext]->(b);

MATCH (a:Topic),(b:UC)
  WHERE a.name = 'articles' AND b.name = 'order-processing'
CREATE (a)-[r:consumedInContext]->(b);

MATCH (a:Topic),(b:UC)
  WHERE a.name = 'customers' AND b.name = 'order-processing'
CREATE (a)-[r:consumedInContext]->(b);

################################################
# What applications do exist in this Use-Case ?
#
CREATE (app1:KafkaStreamsApp { name: 'order-processor', critical: 'yes' })
CREATE (app2:KSQLDB { name: 'stock-processor', critical: 'yes' })



#
# Link both apps to the topics they work with
#
MATCH (a:Topic),(b:KafkaStreamsApp)
  WHERE a.name = 'orders' and b.name = 'order-processor'
CREATE (a)-[r:consumedByStreamingApp]->(b)

MATCH (a:Topic),(b:KafkaStreamsApp)
  WHERE a.name = 'orders' and b.name = 'stock-processor'
CREATE (a)-[r:consumedByStreamingApp]->(b)

MATCH (a:Topic),(b:KSQLDB)
WHERE a.name = 'articles' and b.name = 'order-processor'
CREATE (a)-[r:prodcuedByStreamingApp]->(b)

MATCH (a:Topic),(b:KSQLDB)
WHERE a.name = 'customers' and b.name = 'order-processor'
CREATE (a)-[r:consumedByStreamingApp]->(b)



#
# Define or overwrite a property of a node
#
#  Node: N
#  Type: T
#  Selector Key: SK
#  Selector Value: SV
#
#  Property: P
#  Value: V
#
# (N:T,SK, SV, K , V)
#
MATCH (N:T) { SK: 'SV' })
SET N.K = 'V';



#
# Find all topics with two registered subjects
#
MATCH p=()<-[r1:hasRegisteredSchema]-(n)-[r2:hasRegisteredSchema]->()
RETURN n
