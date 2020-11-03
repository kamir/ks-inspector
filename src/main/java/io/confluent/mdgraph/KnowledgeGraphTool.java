package io.confluent.mdgraph;


import io.confluent.cp.mdmodel.Classifications;
import io.confluent.cp.mdmodel.ClusterStateLoader;
import io.confluent.kafka.schemaregistry.client.rest.entities.Schema;
import io.confluent.kafka.schemaregistry.client.rest.entities.SchemaReference;
import io.confluent.kafka.schemaregistry.client.rest.exceptions.RestClientException;
import net.christophschubert.kafka.clusterstate.formats.domain.Domain;
import net.christophschubert.kafka.clusterstate.formats.domain.Project;
import org.apache.log4j.LogManager;
import org.neo4j.driver.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.neo4j.driver.Values.parameters;

public class KnowledgeGraphTool {

    private static final org.apache.log4j.Logger logger = LogManager.getLogger(KnowledgeGraphTool.class);

    public static void main(String[] ARGS) throws IOException, RestClientException {

        System.out.println( "Start Knowledge Graph DEMO ... ");
        logger.info( "Hey" );
        logger.warn( "Ho!" );

        KnowledgeGraphTool gt = new KnowledgeGraphTool();

        KnowledgeGraph g = gt.graph;

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
        String instancePath1 = "./src/main/cluster-state-tools/contexts/order-processing/instances/order-processing-c.domy";
        String instancePath2 = "./src/main/cluster-state-tools/contexts/order-processing/instances/order-processing-ks1.domy";
        String instancePath3 = "./src/main/cluster-state-tools/contexts/order-processing/instances/order-processing-ks2.domy";
        String instancePath4 = "./src/main/cluster-state-tools/contexts/order-processing/instances/order-processing-p.domy";

        ClusterStateLoader.populateKnowledgeGraphWithInstanceDescription( g, instancePath1 );
        ClusterStateLoader.populateKnowledgeGraphWithInstanceDescription( g, instancePath2 );
        ClusterStateLoader.populateKnowledgeGraphWithInstanceDescription( g, instancePath3 );
        ClusterStateLoader.populateKnowledgeGraphWithInstanceDescription( g, instancePath4 );




        g.show();

        System.exit(0);

    }

    public static KnowledgeGraph graph = null;

    public KnowledgeGraphTool() {
        graph = KnowledgeGraph.getGraph();
    }


}
