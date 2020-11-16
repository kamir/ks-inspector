package io.confluent.mdgraph;

import io.confluent.cp.clients.FactQueryProducer;
import io.confluent.cp.cs.ClusterStateLoader;
import io.confluent.kafka.schemaregistry.client.rest.exceptions.RestClientException;
import io.confluent.mdgraph.model.IKnowledgeGraph;
import io.confluent.mdgraph.model.KnowledgeGraphFactory;
import io.confluent.mdgraph.model.KnowledgeGraphNeo4J;
import org.apache.log4j.LogManager;

import java.io.IOException;

public class GovernanceToolDEMO {

    private static final org.apache.log4j.Logger logger = LogManager.getLogger(GovernanceToolDEMO.class);

    public static void main(String[] ARGS) throws IOException, RestClientException {

        System.out.println( "Start Knowledge Graph DEMO: => AppID = GovernanceDemo ... ");

       // IKnowledgeGraph g1 = KnowledgeGraphFactory.getKafkaBasedKnowledgegraph("DataGovernanceDemo001");
       // String instancesPath = "/Users/mkampf/Engagements/AO-Cloud-Project/week6/instances";
       // ClusterStateLoader.populateKnowledgeGraphMultiDomains( g1, instancesPath );

        IKnowledgeGraph g2 = KnowledgeGraphFactory.getNeo4JBasedKnowledgeGraph();
        KnowledgeGraphNeo4J queriableGraph = (KnowledgeGraphNeo4J)g2;
        queriableGraph.readGraphFromTopic();
        queriableGraph.show();


    }

}
