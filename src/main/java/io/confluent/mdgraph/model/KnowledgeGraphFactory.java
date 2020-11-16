package io.confluent.mdgraph.model;

import io.confluent.cp.clients.FactQueryProducer;

public class KnowledgeGraphFactory {

    public static IKnowledgeGraph getKafkaBasedKnowledgegraph(String appId){

        FactQueryProducer.init( appId );

        return KnowledgeGraphViaKafkaTopic.getGraph();

    };

    public static IKnowledgeGraph getNeo4JBasedKnowledgeGraph(){

        return KnowledgeGraphNeo4J.getGraph();

    };

}
