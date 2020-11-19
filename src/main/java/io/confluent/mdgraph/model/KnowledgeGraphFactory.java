package io.confluent.mdgraph.model;

import io.confluent.cp.factflow.FactQueryProducer;

import java.util.Properties;

public class KnowledgeGraphFactory {

    public static IKnowledgeGraph getKafkaBasedKnowledgegraph(String appId, Properties properties){

        FactQueryProducer.init( appId, properties );

        return KnowledgeGraphViaKafkaTopic.getGraph();

    };

    public static IKnowledgeGraph getNeo4JBasedKnowledgeGraph( Properties properties ){

        KnowledgeGraphNeo4J.init( properties );

        return KnowledgeGraphNeo4J.getGraph();

    };

}
