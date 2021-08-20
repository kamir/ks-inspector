package io.confluent.mdgraph;


import io.confluent.cp.cfg.CCloudClusterWrapper;
import io.confluent.cp.mdmodel.infosec.Classifications;
import io.confluent.cp.cs.ClusterStateLoader;
import io.confluent.cp.mdmodel.ksql.KSQLQueryInspector;
import io.confluent.mdgraph.model.IKnowledgeGraph;
import io.confluent.mdgraph.model.KnowledgeGraphFactory;
import io.confluent.mdgraph.model.KnowledgeGraphNeo4J;
import org.apache.log4j.LogManager;

import java.util.Properties;

public class KnowledgeGraphInspection {

    private static final org.apache.log4j.Logger logger = LogManager.getLogger(KnowledgeGraphInspection.class);

    public static void main(String[] ARGS) throws Exception {

        System.out.println( ">>> Start Knowledge Graph DEMO ... ");

        KnowledgeGraphInspection gt = new KnowledgeGraphInspection();

        Properties propertiesKAFKA = CCloudClusterWrapper.getPropsFrom_ROOT_FOLDER();

        IKnowledgeGraph g = KnowledgeGraphFactory.getKafkaBasedKnowledgegraph( "demo1", propertiesKAFKA );

        /**
         * Manage Data-Catalog-Classification-Definitions
         */
        Classifications cs = new Classifications();
        cs.loadDataCatalog();

        /**
         * Manage Schema MD: the fields will be linked to the tags provided by the catalog.
         */
        //io.confluent.cp.factcollector.SchemaRegistryClient src = new io.confluent.cp.factcollector.SchemaRegistryClient();
        //src.populateGraph( g );

        /**
         * Manage CloudClusters which are used as "environments" within the app life-cycle.
         */
        String environmentPath = "./src/main/cluster-state-tools-data/example2/ccloud-environments.yaml";
        ClusterStateLoader.populateKnowledgeGraphWithEnvironmentDescription( g, environmentPath );

        /**
         * Load domain related data which is centrally managed - this describes what applications are expected in a cluster!
         */
        String domainPath = "./src/main/cluster-state-tools-data/example1/domains";
        ClusterStateLoader.populateKnowledgeGraphMultiDomains( g, domainPath );

        /** Command available.
         * Load domain data - which is used in real projects in an enterprise context ...
         */
        String instancePath1 = "./src/main/cluster-state-tools-data/example2/instances/order-processing-c.domy";
        String instancePath2 = "./src/main/cluster-state-tools-data/example2/instances/order-processing-ks1.domy";
        String instancePath3 = "./src/main/cluster-state-tools-data/example2/instances/order-processing-ks2.domy";
        String instancePath4 = "./src/main/cluster-state-tools-data/example2/instances/order-processing-p.domy";

        ClusterStateLoader.populateKnowledgeGraphWithInstanceDescription( g, instancePath1 );
        ClusterStateLoader.populateKnowledgeGraphWithInstanceDescription( g, instancePath2 );
        ClusterStateLoader.populateKnowledgeGraphWithInstanceDescription( g, instancePath3 );
        ClusterStateLoader.populateKnowledgeGraphWithInstanceDescription( g, instancePath4 );


        //------------------------
        // Inspect KSQLDB-QUERIES

        String p = "./ksqldb-query-stage";;
        String qf = "200_PoC-queries-solution.sql";
        // KSQLQueryInspector.inspectQueryFile( p, qf, g );
        //g.show();

        Properties propsGDB = CCloudClusterWrapper.getPropsFrom_ROOT_FOLDER( "./src/main/resources/neo4j.props" );
        IKnowledgeGraph g2 = KnowledgeGraphFactory.getNeo4JBasedKnowledgeGraph( null, propsGDB);

        KnowledgeGraphNeo4J queriableGraph = (KnowledgeGraphNeo4J)g2;
        queriableGraph.readGraphFromTopic( "DataGovernanceDemo001_" + System.currentTimeMillis(), propertiesKAFKA );

        System.out.println( ">>> Finished exporting the facts for our Streaming Processes Knowledge Graph to Neo4J." );

        System.exit(0);

    }

    public static KnowledgeGraphNeo4J graph = null;

    public KnowledgeGraphInspection() {
        graph = KnowledgeGraphNeo4J.getGraph();
    }

    public void deleteAllFacts() {
        graph.deleteAllFacts();
    }

    public void showGraph() {
        graph.show();
    }
}
