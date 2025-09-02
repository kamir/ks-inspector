package io.confluent.mdgraph;

import io.confluent.cp.cs.ClusterStateLoader;
import io.confluent.kafka.schemaregistry.client.rest.exceptions.RestClientException;
import io.confluent.mdgraph.model.IKnowledgeGraph;
import io.confluent.mdgraph.model.KnowledgeGraphFactory;
import io.confluent.mdgraph.model.KnowledgeGraphNeo4J;
import io.confluent.ks.modern.kafka.ModernKafkaClient;
import org.apache.log4j.LogManager;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

public class GovernanceWorkflowDEMO {

    private static final org.apache.log4j.Logger logger = LogManager.getLogger(GovernanceWorkflowDEMO.class);

    public static void main(String[] ARGS) throws IOException, RestClientException {

        System.out.println( "Start Knowledge Graph DEMO: => AppID : DataGovernanceDemo001_ ... ");

        final Properties properties = ModernKafkaClient.loadProperties(new File("src/main/cluster-state-tools-data/example2/kst.properties"), null, "KST");

        /**
         * Command: inspect
         */
       IKnowledgeGraph g1 = KnowledgeGraphFactory.getKafkaBasedKnowledgegraph("DataGovernanceDemo001", properties);
       String instancesPath = "/Users/mkampf/Engagements/AO-Cloud-Project/week6/instances";
       ClusterStateLoader.populateKnowledgeGraphMultiDomains( g1, instancesPath );

       System.out.println( "> Step 1 ... [done]");

        System.exit(0);

        /**
         * Command: export
         */
        IKnowledgeGraph g2 = KnowledgeGraphFactory.getNeo4JBasedKnowledgeGraph(null, properties);
        KnowledgeGraphNeo4J queriableGraph = (KnowledgeGraphNeo4J)g2;
        queriableGraph.readGraphFromTopic( "DataGovernanceDemo001_" + System.currentTimeMillis(), properties);
        queriableGraph.show();

        System.out.println( "> Step 2 ... [done]");

        System.exit(0);

    }

}
