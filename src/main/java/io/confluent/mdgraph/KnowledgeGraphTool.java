package io.confluent.mdgraph;


import io.confluent.cp.mdmodel.infosec.Classifications;
import io.confluent.cp.cs.ClusterStateLoader;
import io.confluent.kafka.schemaregistry.client.rest.exceptions.RestClientException;
import io.confluent.mdgraph.model.KnowledgeGraphNeo4J;
import org.apache.log4j.LogManager;

import java.io.IOException;

public class KnowledgeGraphTool {

    private static final org.apache.log4j.Logger logger = LogManager.getLogger(KnowledgeGraphTool.class);

    public static void main(String[] ARGS) throws IOException, RestClientException {

        System.out.println( "Start Knowledge Graph DEMO ... ");
        logger.info( "Hey" );
        logger.warn( "Ho!" );

        KnowledgeGraphTool gt = new KnowledgeGraphTool();

        KnowledgeGraphNeo4J g = gt.graph;

        g.deleteAllFacts();

        g.show();

        /**
         * Manage TOPIC MD
         *

        g.addTopicNode( "t1" );
        g.addTopicNode( "t2" );
        g.addTopicNode( "t3" );
        g.addTopicNode( "t4" );

        g.addPropertiesToNode( "Topic", "name", "t1", "nrOfPartitions", "1" );
        g.addPropertiesToNode( "Topic", "name", "t1", "replication", "3" );
        g.addPropertiesToNode( "Topic", "name", "t1", "compacted", "false" );

        g.addPropertiesToNode( "Topic", "name", "t2", "nrOfPartitions", "1" );
        g.addPropertiesToNode( "Topic", "name", "t3", "replication", "3" );
        g.addPropertiesToNode( "Topic", "name", "t4", "compacted", "false" );

        Properties props = new Properties();
        props.put( "nrOfPartitions", "1" );
        props.put( "replication", "3" );
        props.put( "compacted", "false" );

        g.addNode("Topic" ,"t5" , props );

         */

        /**
         * Manage Data-Catalog-Definitions
         */
        Classifications cs = new Classifications();
        cs.loadDataCatalog();

        /**
         * Manage Schema MD: the fields will be linked to the tags provided by the catalog.
         */
        io.confluent.cp.clients.SchemaRegistryClient src = new io.confluent.cp.clients.SchemaRegistryClient();
        src.populateGraph( g );

        /**
         * Manage Domain data
         */
        ClusterStateLoader.populateKnowledgeGraph( g, null);

        /**
         * Manage Domain data
         */
        String instancePath1 = "./src/main/cluster-state-tools-data/contexts/order-processing/instances/order-processing-c.domy";
        String instancePath2 = "./src/main/cluster-state-tools-data/contexts/order-processing/instances/order-processing-ks1.domy";
        String instancePath3 = "./src/main/cluster-state-tools-data/contexts/order-processing/instances/order-processing-ks2.domy";
        String instancePath4 = "./src/main/cluster-state-tools-data/contexts/order-processing/instances/order-processing-p.domy";

        ClusterStateLoader.populateKnowledgeGraphWithInstanceDescription( g, instancePath1 );
        ClusterStateLoader.populateKnowledgeGraphWithInstanceDescription( g, instancePath2 );
        ClusterStateLoader.populateKnowledgeGraphWithInstanceDescription( g, instancePath3 );
        ClusterStateLoader.populateKnowledgeGraphWithInstanceDescription( g, instancePath4 );

        g.show();

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
