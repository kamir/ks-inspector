package io.confluent.mdgraph;

import io.confluent.cp.cs.ClusterStateLoader;
import io.confluent.kafka.schemaregistry.client.rest.exceptions.RestClientException;
import org.apache.log4j.LogManager;

import java.io.IOException;

public class AOGovernanceToolDEMO {

    private static final org.apache.log4j.Logger logger = LogManager.getLogger(AOGovernanceToolDEMO.class);

    public static void main(String[] ARGS) throws IOException, RestClientException {

        System.out.println( "Start Knowledge Graph DEMO ... ");

        KnowledgeGraphTool gt = new KnowledgeGraphTool();

        KnowledgeGraph g = gt.graph;

        g.deleteAllFacts();

        /**
         *
         */
        String instancesPath = "/Users/mkampf/Engagements/AO-Cloud-Project/week6/instances";
        ClusterStateLoader.populateKnowledgeGraphMultiDomains( g, instancesPath );

        g.show();



    }

}
