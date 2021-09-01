#---------------------------------------------------------------
# Find Services by data usage types: Ingest, Consume, Process
#---------------------------------------------------------------

#match p=(a:Service)-[l:publishes]->(b:Topic) return apoc.node.degree(a, "publishes>") AS outDegree, p, a, b, l;
#match p=(a:Service)-[l:publishes]->(b:Topic) return apoc.node.degree(a, "publishes>") AS outDegree, p, a, b, l;
#match p=(a:Service)-[l:consumes]->(b:Topic) return apoc.node.degree(a, "consumes>") AS inDegree, p, a, b, l;

CALL gds.graph.drop('graph1');

CALL gds.graph.create.cypher(
 'graph1',
 'match (n) WHERE n:Service OR n:Topic return id(n) as id ',
 'match (a:Service)-[c]->(b:Topic) return id(c) as id, id(a) as source, id(b) as target',
 {validateRelationships: False}
 )
 YIELD graphName, nodeCount, relationshipCount, createMillis;

#CALL gds.graph.drop('graph1C');
#CALL gds.graph.create.cypher(
# 'graph1C',
# 'match (n) WHERE n:Service OR n:Topic return id(n) as id ',
# 'match (a:Service)-[c:consumes]->(b:Topic) return id(c) as id, id(a) as source, id(b) as target',
# {validateRelationships: False}
# )
# YIELD graphName, nodeCount, relationshipCount, createMillis;

#CALL gds.graph.drop('graph1P');
#CALL gds.graph.create.cypher(
# 'graph1P',
#'match (n) WHERE n:Service OR n:Topic return id(n) as id ',
# 'match (a:Service)-[c:publishes]->(b:Topic) return id(c) as id, id(a) as source, id(b) as target',
# {validateRelationships: False}
# )
# YIELD graphName, nodeCount, relationshipCount, createMillis;

CALL gds.graph.list();

#CALL gds.beta.k1coloring.stream('graph1')
#YIELD nodeId, color
#RETURN color, gds.util.asNode(nodeId).name AS name
#ORDER BY color, name

CALL gds.localClusteringCoefficient.write.estimate('graph1', {
writeProperty: 'localClusteringCoefficient'
})
YIELD nodeCount, relationshipCount, bytesMin, bytesMax, requiredMemory

#CALL algo.triangleCount.stream('graph1')
#YIELD nodeId, triangles, coefficient
#MATCH (p:Service) WHERE id(p) = nodeId
#RETURN p.id AS name, triangles, coefficient
#ORDER BY coefficient DESC

CALL apoc.export.graphml.all( "/Users/mkaempf/GITHUB.public/ks-inspector/last-export.graphml", {readLabels: true, storeNodeIds: true})

CALL gds.graph.list();
