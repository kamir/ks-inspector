package io.confluent.mdgraph.model;

import net.christophschubert.kafka.clusterstate.formats.domain.Domain;
import net.christophschubert.kafka.clusterstate.formats.env.CloudCluster;

import io.confluent.kafka.schemaregistry.client.rest.entities.Schema;

import java.io.File;
import java.util.Properties;

public interface IKnowledgeGraph {

    void exportFullGraphAsGraphML( File outputFile );

    void exportFullGraphAsCSV(File outputFile);

    void exportFullGraphsNodeListAsCSV(File outputFile);

    void exportFullGraphsEdgeListAsCSV(File outputFile);

    void addCGNode(String cgId);

    void addServiceAsConsumerOfTopic(String service, String topic);

    void addServiceAsPublisherToTopic(String service, String topic);

    void mergeNode(String type, String name);

    void mergeNode(String type, String name, Properties props);

    void addTopicNode(String name);

    void addServiceNode(String name);

    void addTeamNode(String name);

    void addNode(String type, String name);

    void addNode(String type, String name, Properties props);

    void addPropertiesToNode(String T, String SK, String SV, String K, String V);

    void registerCloudCluster(CloudCluster c, File contextPath);

    void registerDomain(Domain domain, int i, File contextPath);

    void registerAppInstance4Domain(Domain domain, File contextPath);

    void registerSchema(Schema schema);

    void clearGraph();
}
