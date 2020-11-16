package io.confluent.mdgraph.model;

import io.confluent.kafka.schemaregistry.client.rest.entities.Schema;
import net.christophschubert.kafka.clusterstate.formats.domain.Domain;

import java.io.File;
import java.util.Properties;

public interface IKnowledgeGraph {

    void addNode(String type, String name, Properties props);

    void addPropertiesToNode(String T, String SK, String SV, String K, String V);

    void addTopicNode(String name);

    void registerSchema(Schema schema);

    void registerDomain(Domain domain, int i, File contextPath);

    void registerAppInstance4Domain(Domain domain, File contextPath);

}
