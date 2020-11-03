package io.confluent.cp.clients;

import io.confluent.cp.CCloudClusterWrapper;
import io.confluent.kafka.schemaregistry.client.rest.*;
import io.confluent.kafka.schemaregistry.client.rest.entities.Schema;
import io.confluent.kafka.schemaregistry.client.rest.exceptions.RestClientException;
import io.confluent.kafka.schemaregistry.client.security.basicauth.BasicAuthCredentialProvider;
import io.confluent.mdgraph.KnowledgeGraph;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * This tool reads all schemas from a Confluent cloud environment.
 *
 * We convert this data into a knowledge graph data model.
 */

public class SchemaRegistryClient {

    static RestService srr = null;

    /**
     * The SchemaRegistry Client needs a security configuration to work with CCloud.
     */
    public SchemaRegistryClient() {

        String baseUrlConfig = "https://psrc-4v1qj.eu-central-1.aws.confluent.cloud";

        srr = new RestService( baseUrlConfig );

        Map<String, String> map = new HashMap<String, String>();
        Properties props = CCloudClusterWrapper.getProps();
        for (String key : props.stringPropertyNames()) {
            map.put(key, props.getProperty(key));
        }

        srr.configure( map );
    }

    public void populateGraph(KnowledgeGraph kg) throws IOException, RestClientException {

        List<String> subjects = srr.getAllSubjects();

        for( String subject : subjects ) {
            processSubjectMD( subject, kg );
        }

    }

    public static void main(String[] args) throws IOException, RestClientException {

        SchemaRegistryClient client = new SchemaRegistryClient();

        List<String> subjects = srr.getAllSubjects();

        for( String subject : subjects ) {
            processSubjectMD( subject, null );
        }

    }

    private static void processSubjectMD(String subject, KnowledgeGraph kg) {

        System.out.println("-----subject-----"  );

        //System.out.println( subject );

        List<Integer> versions = null;

        try {

            versions = srr.getAllVersions( subject );

            for( Integer v : versions ) {

                Schema schema = srr.getVersion(subject, v);

                kg.registerSchema( schema );

                System.out.println( "    " + schema );
            }

        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (RestClientException e) {
            e.printStackTrace();
        }

        System.out.println("-----subject-----"  );
        System.out.println();
    }

}
