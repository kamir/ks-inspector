package io.confluent.mdgraph;


import io.confluent.cp.mdmodel.infosec.Classifications;
import io.confluent.cp.cs.ClusterStateLoader;
import io.confluent.kafka.schemaregistry.client.rest.exceptions.RestClientException;
import io.confluent.mdgraph.model.KnowledgeGraphNeo4J;
import org.apache.log4j.LogManager;

import java.io.IOException;

public class KnowledgeGraphTool {

    private static final org.apache.log4j.Logger logger = LogManager.getLogger(KnowledgeGraphTool.class);

    public static void main(String[] ARGS) throws Exception {

        System.out.println( "Start Knowledge Graph DEMO ... ");

        KnowledgeGraphTool gt = new KnowledgeGraphTool();

        KnowledgeGraphNeo4J g = gt.graph;

        g.deleteAllFacts();

        /**
         * Manage Data-Catalog-Definitions
         */
        Classifications cs = new Classifications();
        cs.loadDataCatalog();

        /** Command available.
         * Manage Schema MD: the fields will be linked to the tags provided by the catalog.
         */
        io.confluent.cp.clients.SchemaRegistryClient src = new io.confluent.cp.clients.SchemaRegistryClient();
        src.populateGraph( g );

        String environmentPath = "./src/main/cluster-state-tools-data/contexts/example1/instances/cp-environments.yaml";
        ClusterStateLoader.populateKnowledgeGraphWithEnvironmentDescription( g, environmentPath );

        /** Command available.
         * Manage Domain data with central content - what is expected!
         */
        String instancePath = "./src/main/cluster-state-tools-data/contexts/example1/instances";
        ClusterStateLoader.populateKnowledgeGraph( g, instancePath);

        /** Command available.
         * Manage Domain data - what is used in reality ...
         */
        String instancePath1 = "./src/main/cluster-state-tools-data/contexts/order-processing/instances/order-processing-c.domy";
        String instancePath2 = "./src/main/cluster-state-tools-data/contexts/order-processing/instances/order-processing-ks1.domy";
        String instancePath3 = "./src/main/cluster-state-tools-data/contexts/order-processing/instances/order-processing-ks2.domy";
        String instancePath4 = "./src/main/cluster-state-tools-data/contexts/order-processing/instances/order-processing-p.domy";

        ClusterStateLoader.populateKnowledgeGraphWithInstanceDescription( g, instancePath1 );
        ClusterStateLoader.populateKnowledgeGraphWithInstanceDescription( g, instancePath2 );
        ClusterStateLoader.populateKnowledgeGraphWithInstanceDescription( g, instancePath3 );
        ClusterStateLoader.populateKnowledgeGraphWithInstanceDescription( g, instancePath4 );

        //g.show();

        System.exit(0);

    }

    public static KnowledgeGraphNeo4J graph = null;

    public KnowledgeGraphTool() {
        graph = KnowledgeGraphNeo4J.getGraph();
    }


    public void deleteAllFacts() {
        graph.deleteAllFacts();
    }

    public void showGraph() {
        graph.show();
    }
}
