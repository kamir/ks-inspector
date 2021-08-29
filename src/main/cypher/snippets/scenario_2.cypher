#---------------------------------------------------------------
# Analyse the flow-graph of a complex application ...
#---------------------------------------------------------------

#########
# 1 :
#
match p=(a:Service)-[l:publishes]->(b:Topic) return apoc.node.degree(b, "<PUBLISHES") AS outDegree, p, a, b, l;

#########
# 2 :
#
match p=(a:Service)-[l:consumes]->(b:Topic) return p, a, b, l;

#########
# 3 :
#
match p=(a:Service)-[l:publishes]->(b:Topic) return apoc.node.degree(a, "publishes>") AS outDegree, p, a, b, l;

#########
# 4 :
#
match p=(a:Service)-[l:consumes]->(b:Topic) return apoc.node.degree(a, "consumes>") AS inDegree, p, a, b, l;

#########
# 5 :
#
CALL gds.graph.drop('graph1');
CALL gds.graph.drop('graph1C');
CALL gds.graph.drop('graph1P');

#########
# 6 :
#
CALL gds.graph.create.cypher(
 'graph1',
 'match (n) WHERE n:Service OR n:Topic return id(n) as id ',
 'match (a:Service)-[c]->(b:Topic) return id(c) as id, id(a) as source, id(b) as target',
 {validateRelationships: False}
 )
 YIELD graphName, nodeCount, relationshipCount, createMillis;

#########
# 7 :
#
CALL gds.graph.create.cypher(
 'graph1C',
 'match (n) WHERE n:Service OR n:Topic return id(n) as id ',
 'match (a:Service)-[c:consumes]->(b:Topic) return id(c) as id, id(a) as source, id(b) as target',
 {validateRelationships: False}
 )
 YIELD graphName, nodeCount, relationshipCount, createMillis;

#########
# 8 :
#
CALL gds.graph.create.cypher(
 'graph1P',
 'match (n) WHERE n:Service OR n:Topic return id(n) as id ',
 'match (a:Service)-[c:publishes]->(b:Topic) return id(c) as id, id(a) as source, id(b) as target',
 {validateRelationships: False}
 )
 YIELD graphName, nodeCount, relationshipCount, createMillis;

#########
# 9 :
#
CALL gds.graph.list();
