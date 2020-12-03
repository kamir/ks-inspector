package io.confluent.cp.factcollector;

import io.confluent.cp.cfg.CCloudClusterWrapper;
import io.confluent.kafka.schemaregistry.client.rest.*;
import io.confluent.kafka.schemaregistry.client.rest.entities.Schema;
import io.confluent.kafka.schemaregistry.client.rest.exceptions.RestClientException;
import io.confluent.mdgraph.model.IKnowledgeGraph;

import java.io.IOException;
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
        Properties props = CCloudClusterWrapper.getPropsFrom_ROOT_FOLDER();
        for (String key : props.stringPropertyNames()) {
            map.put(key, props.getProperty(key));
        }

        srr.configure( map );
    }

    public void populateGraph(IKnowledgeGraph kg) throws IOException, RestClientException {

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

    private static void processSubjectMD(String subject, IKnowledgeGraph kg) {

        System.out.println("-----subject-----"  );

        //System.out.println( subject );

        List<Integer> versions = null;

        try {

            versions = srr.getAllVersions( subject );

            for( Integer v : versions ) {

                Schema schema = srr.getVersion(subject, v);

                if ( kg != null )
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
