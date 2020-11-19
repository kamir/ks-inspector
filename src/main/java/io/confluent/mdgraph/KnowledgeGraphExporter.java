package io.confluent.mdgraph;


import io.confluent.cp.cfg.CCloudClusterWrapper;
import io.confluent.cp.cs.ClusterStateLoader;
import io.confluent.cp.mdmodel.infosec.Classifications;
import io.confluent.mdgraph.model.IKnowledgeGraph;
import io.confluent.mdgraph.model.KnowledgeGraphFactory;
import io.confluent.mdgraph.model.KnowledgeGraphNeo4J;
import org.apache.log4j.LogManager;

import java.util.Properties;

public class KnowledgeGraphExporter {

    private static final org.apache.log4j.Logger logger = LogManager.getLogger(KnowledgeGraphExporter.class);

    public static void main(String[] ARGS) throws Exception {

        System.out.println( ">>> Start Knowledge Graph DEMO ... ");

        Properties propertiesKAFKA = CCloudClusterWrapper.getPropsFrom_ROOT_FOLDER();

        Properties propsGDB = CCloudClusterWrapper.getPropsFrom_ROOT_FOLDER( "./src/main/resources/neo4j.props" );
        IKnowledgeGraph g2 = KnowledgeGraphFactory.getNeo4JBasedKnowledgeGraph(propsGDB);

        KnowledgeGraphNeo4J queriableGraph = (KnowledgeGraphNeo4J)g2;

        queriableGraph.deleteAllFacts();

        queriableGraph.readGraphFromTopic( "DataGovernanceDemo001_" + System.currentTimeMillis(), propertiesKAFKA );

        System.out.println( ">>> Finished exporting the facts for our Streaming Processes Knowledge Graph to Neo4J." );

        System.exit(0);

    }

    public static KnowledgeGraphNeo4J graph = null;

    public KnowledgeGraphExporter() {
        graph = KnowledgeGraphNeo4J.getGraph();
    }

    public void deleteAllFacts() {
        graph.deleteAllFacts();
    }

    public void showGraph() {
        graph.show();
    }
}
