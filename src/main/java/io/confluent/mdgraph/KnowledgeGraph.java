package io.confluent.mdgraph;


import io.confluent.cp.mdmodel.Classifications;
import io.confluent.cp.mdmodel.ClusterStateLoader;
import io.confluent.kafka.schemaregistry.client.rest.entities.Schema;
import io.confluent.kafka.schemaregistry.client.rest.entities.SchemaReference;
import io.confluent.kafka.schemaregistry.client.rest.exceptions.RestClientException;
import net.christophschubert.kafka.clusterstate.formats.domain.*;

import org.apache.commons.codec.digest.DigestUtils;

import org.apache.log4j.LogManager;
import org.neo4j.driver.*;
import org.neo4j.driver.Result;

import javax.swing.plaf.synth.SynthScrollBarUI;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import java.util.Properties;

import static org.neo4j.driver.Values.parameters;

public class KnowledgeGraph {

    private static final org.apache.log4j.Logger logger = LogManager.getLogger(KnowledgeGraph.class);
    
    public static void main(String[] ARGS) throws IOException, RestClientException {

        System.out.println( "Start Knowledge Graph DEMO ... ");
        logger.info( "Hey" );
        logger.warn( "Ho!" );

        KnowledgeGraph g = KnowledgeGraph.getGraph();

        g.deleteAllFacts();

        g.show();



        /**
         * Manage TOPIC MD
         */
        g.addTopicNode( "t1" );
        g.addTopicNode( "t2" );
        g.addTopicNode( "t3" );
        g.addTopicNode( "t4" );

        g.addPropertiesToNode( "Topic", "name", "t1", "nrOfPartitions", "1" );
        g.addPropertiesToNode( "Topic", "name", "t1", "replication", "3" );
        g.addPropertiesToNode( "Topic", "name", "t1", "compacted", "false" );

        g.addPropertiesToNode( "Topic", "name", "t2", "nrOfPartitions", "1" );
        g.addPropertiesToNode( "Topic", "name", "t3", "replication", "3" );
        g.addPropertiesToNode( "Topic", "name", "t4", "compacted", "false" );


        Properties props = new Properties();
        props.put( "nrOfPartitions", "1" );
        props.put( "replication", "3" );
        props.put( "compacted", "false" );

        g.addNode("Topic" ,"t5" , props );


        /**
         * Manage Schema MD
         */
        io.confluent.cp.clients.SchemaRegistryClient src = new io.confluent.cp.clients.SchemaRegistryClient();
        src.populateGraph( g );


        /**
         * Manage Domain data
         */
        ClusterStateLoader.populateKnowledgeGraph( g, null );

        /**
         * Manage Domain data
         */
        String instancePath1 = "./src/main/cluster-state-tools/contexts/order-processing/instances/order-processing-c.domy";
        String instancePath2 = "./src/main/cluster-state-tools/contexts/order-processing/instances/order-processing-ks1.domy";
        String instancePath3 = "./src/main/cluster-state-tools/contexts/order-processing/instances/order-processing-ks2.domy";
        String instancePath4 = "./src/main/cluster-state-tools/contexts/order-processing/instances/order-processing-p.domy";

        ClusterStateLoader.populateKnowledgeGraphWithInstanceDescription( g, instancePath1 );
        ClusterStateLoader.populateKnowledgeGraphWithInstanceDescription( g, instancePath2 );
        ClusterStateLoader.populateKnowledgeGraphWithInstanceDescription( g, instancePath3 );
        ClusterStateLoader.populateKnowledgeGraphWithInstanceDescription( g, instancePath4 );

        g.show();

        System.exit(0);

    }



    public void show() {

        System.out.println( ">>> Graph data ...");

        List<String> nodes = getAllNodes();

        int i = 0;
        for( String n : nodes ) {
            i++;
            System.out.println( i + " :: " +  n );

        }

    }

    private List<String> getAllNodes() {

        try ( Session session = driver.session() )
        {
            return session.readTransaction( tx -> {
                List<String> names = new ArrayList<>();
                Result result = tx.run( "MATCH (n) RETURN n.name ORDER BY n.name" );
                while ( result.hasNext() )
                {
                    names.add( result.next().get( 0 ).asString() );
                }
                return names;
            } );
        }
    }

    private static KnowledgeGraph graph = null;

    private Driver driver;

    public static KnowledgeGraph getGraph() {
        if ( graph == null ) {
            graph = new KnowledgeGraph();
        }
        return graph;
    }

    String databaseDirectory = "graph-db";
    String DEFAULT_DATABASE_NAME = "neo4j";
    String uri = "bolt://localhost:7687";

    public KnowledgeGraph() {

        driver = GraphDatabase.driver( uri, AuthTokens.basic( "neo4j", "admin" ) );


        registerShutdownHook( driver );

    }

    public void addNode(String type, String name, Properties props) {

        addNode( type, name );
        for( String k: props.stringPropertyNames() ) {
            addPropertiesToNode(type, "name", name, k, props.getProperty( k ) );
        }

    }

    // START SNIPPET: shutdownHook
    private static void registerShutdownHook( final Driver driver )
    {
        // Registers a shutdown hook for the Neo4j instance so that it
        // shuts down nicely when the VM exits (even if you "Ctrl-C" the
        // running example before it's completed)
        Runtime.getRuntime().addShutdownHook( new Thread()
        {
            @Override
            public void run()
            {
                driver.close();

            }
        } );
    }
    // END SNIPPET: shutdownHook


    /**
      #
      # Define or overwrite a property of a node
      #
      #  Node: N
      #  Type: T
      #  Selector Key: SK
      #  Selector Value: SV
      #
      #  Property: P
      #  Value: V
      #
      # (N:T,SK, SV, K , V)
      #

      MATCH (N:T) { SK: 'SV' })
      SET N.K = 'V';

     */

    public void addPropertiesToNode(String T, String SK, String SV, String K, String V) {

        Session s = driver.session();

        String greeting = s.writeTransaction( new TransactionWork<String>()
        {
            @Override
            public String execute(org.neo4j.driver.Transaction tx) {

                Result result = tx.run( "MATCH (N:" + T +
                        "{ " + SK + ":'" + SV + "' } ) SET N." + K + " = '" + V + "';" );

                return result.consume().query().toString();
            }
        } );

        System.out.println( greeting );

    }

    private void addNode(String type, String name ) {

        Session s = driver.session();

        String greeting = s.writeTransaction( new TransactionWork<String>()
        {
            @Override
            public String execute(org.neo4j.driver.Transaction tx) {

                Result result = tx.run( "CREATE (N:" + type + " { name : '" + name + "' } );" );

                return result.consume().query().toString();
            }
        } );

        System.out.println( greeting );

    }
    public void addTopicNode( String name ) {
         addNode( "Topic", name );
    }


    public void exampleCalls() {

        Session s = driver.session();

        String message = "Hi ho!";

        String greeting = s.writeTransaction( new TransactionWork<String>()
        {
            @Override
            public String execute(org.neo4j.driver.Transaction tx) {

                Result result = tx.run( "CREATE (a:Greeting) " +
                                "SET a.message = $message " +
                                "RETURN a.message + ', from node ' + id(a)",
                        parameters( "message", message ) );
                return result.single().get(0).asString();
            }
        } );
        System.out.println( greeting );

    }


    public void deleteAllFacts() {

        Session s = driver.session();

        String st = s.writeTransaction( new TransactionWork<String>()
        {
            @Override
            public String execute(org.neo4j.driver.Transaction tx) {

                Result result = tx.run( "MATCH (n)\n" +
                                "DETACH DELETE n" );

                return result.toString();

            }

        } );

        System.out.println( st );

    }

    public void registerSchema(Schema schema) {

        String subject = schema.getSubject();

        String schemaString = schema.getSchema();
        String schemaTpe = schema.getSchemaType();
        String schemaId = "" + schema.getId();
        String version = "" + schema.getVersion();

        Properties props = new Properties();
        props.put( "name" , subject );
        props.put( "subject" , subject );
        props.put( "schemaString", schemaString);
        props.put( "schemaType", schemaTpe);
        props.put( "schemaId", schemaId);
        props.put( "version", version);

        this.mergeNode( "Subject", subject, props );

        // Link zum Topic
        String topic = subject.substring( 0, subject.length()-6 );
        if ( subject.endsWith( "-key" ) ) {
            topic = subject.substring( 0, subject.length()-4 );
        }

        this.mergeTopic( topic );
        this.addTopicSubjectLink( topic, subject, "hasSubject" );

        this.addSchemaVersionToSubject( subject, version, schemaString );

        // Links zu den Referenzen ...
        List<SchemaReference> schemaRef = schema.getReferences();
        for( SchemaReference sr : schemaRef ) {

            this.addSubjectSchemaRefLink( subject, sr );

        }



    }

    /**
     * A classified field will be tagged with classifier tag and it will be linked to the
     * SchemaString within which it is defined.
     *
     * @param schemaNameString
     * @param fieldName
     * @param tags
     */
    private void mergeClassifiedFieldToSchemaString(String schemaNameString, String fieldName, String[] tags) {

        mergeNode( "ClassifiedField", fieldName, null );

        String q1 = "MATCH (s:ClassifiedField),(sst:SchemaString) " +
                "WHERE sst.name = '" + schemaNameString + "' AND s.name = '" + fieldName + "' " +
                "MERGE (sst)-[r:hasClassifiedFild]->(s);";

        exequteCypherQuery( q1 );

        for( String tag: tags ) {

            mergeNode( "ClassificationTag", tag, null );

            String q2 = "MATCH (f:ClassifiedField),(t:ClassificationTag) " +
                    "WHERE f.name = '" + fieldName + "' AND t.name = '" + tag + "' " +
                    "MERGE (f)-[r:hasClassifierTag]->(t);";

            exequteCypherQuery( q2 );

        }

    }

    private void addSubjectSchemaRefLink(String subject, SchemaReference sr) {

        String sr_subject = sr.getSubject();
        String sr_name = sr.getName();
        String sr_version = "" + sr.getVersion();

        String q = "MATCH (s:Subject),(sr:Subject) " +
                "WHERE s.name = '" + subject + "' AND sr.name = '" + sr_subject + "' " +
                "MERGE (t)-[r:hasSchemaReference{ version: '" + sr_version + "'}]->(sr);";

        exequteCypherQuery( q );

    }

    private void mergeTopic(String topic) {
        this.mergeTopic( topic, null );
    }

    private void mergeNode(String type, String name, Properties props) {

        String q = "MERGE (t:" + type + " { name: '" + name + "'});";

        exequteCypherQuery( q );

        if ( props != null ) {
            Enumeration<String> en = (Enumeration<String>)props.propertyNames();
            while( en.hasMoreElements() ) {
                String k = en.nextElement();
                String v = props.getProperty( k );
                addPropertiesToNode("type", "name", name, k, v);
            }
        }

    }

    private void mergeTopic(String topic, Properties props) {

        String q = "MERGE (t:Topic { name: '" + topic + "'});";

        exequteCypherQuery( q );

        if ( props != null ) {
            Enumeration<String> en = (Enumeration<String>)props.propertyNames();
            while( en.hasMoreElements() ) {
                String k = en.nextElement();
                String v = props.getProperty( k );
                addPropertiesToNode("Topic", "name", topic, k, v);
            }
        }

    }

    private void mergeDomain(String domain, Properties props) {

        String q = "MERGE (t:Domain { name: '" + domain + "'});";

        exequteCypherQuery( q );

        if ( props != null ) {
            Enumeration<String> en = (Enumeration<String>)props.propertyNames();
            while( en.hasMoreElements() ) {
                String k = en.nextElement();
                String v = props.getProperty( k );
                addPropertiesToNode("Domain", "name", domain, k, v);
            }
        }

    }

    private void mergeProject(String project) {

        String q = "MERGE (t:Project { name: '" + project + "'});";

        exequteCypherQuery( q );

    }



    private void addSchemaVersionToSubject(String subject, String version, String schemaString) {

        String nodeName = subject + "_" + version;

        Properties props = new Properties();
        props.put( "schemaString", schemaString);

        mergeNode( "SchemaString", nodeName , props );

        String q = "MATCH (s:Subject),(sst:SchemaString) " +
                "WHERE sst.name = '" + nodeName + "' AND s.name = '" + subject + "' " +
                "MERGE (s)-[r:hasSchemaString { version: " + version + " } ]->(sst);";

        exequteCypherQuery( q );

        /**
         * link all tagged fields to the SchemaString
         */
        try {

            org.apache.avro.Schema as = new org.apache.avro.Schema.Parser().setValidate(true).parse( schemaString );
            List<org.apache.avro.Schema.Field> fields = as.getFields();

            for (org.apache.avro.Schema.Field f : fields) {
                String[] fieldClassificationTags = Classifications.getTagsForField(f.name());
                if ( fieldClassificationTags != null) {

                    System.out.println("###*** " + f.name() + " => " + fieldClassificationTags[0] + " ..." );
                    mergeNode( "Field" , f.name(), null );
                    mergeClassifiedFieldToSchemaString( nodeName, f.name(), fieldClassificationTags );

                }
            }
        }
        catch (Exception ex) {
            // primitive types are not classified.

        }

    }
    private void addTopicSubjectLink(String topic, String subject, String hasRegisteredSchema) {

        String q = "MATCH (t:Topic),(s:Subject) " +
                   "WHERE t.name = '" + topic + "' AND s.name = '" + subject + "' " +
                   "MERGE (t)-[r:" +  hasRegisteredSchema + "]->(s);";

        exequteCypherQuery( q );

    }

    private void addSchemaNode(String subject, Properties props) {
        this.addNode( "Subject" , subject, props );
    }

    private void exequteCypherQuery(String q) {

        Session s = driver.session();

        String st = s.writeTransaction( new TransactionWork<String>()
        {
            @Override
            public String execute(org.neo4j.driver.Transaction tx) {

                Result result = tx.run( q );

                return result.toString();

            }

        } );

        System.out.println( st );

    }

    public void registerDomain(Domain domain, int i, File contextPath) {

        Properties props = new Properties();
        props.put( "nrInPath", i );
        props.put( "contextPath", contextPath.getAbsolutePath() );
        props.put( "nrOfProjects" , domain.projects.size() );

        this.mergeDomain( domain.name , props );

        /**
         *  Manage the Projects per Domain
         */
        for (Project p : domain.projects ) {
            this.mergeProject( p.name );
            this.addDomainProjectLink( domain, p );


        }

    }



    private void addDomainProjectLink(Domain domain, Project p) {

        String q = "MATCH (t:Domain),(s:Project) " +
                "WHERE t.name = '" + domain.name + "' AND s.name = '" + p.name + "' " +
                "MERGE (t)-[r:hasProject]->(s);";

        exequteCypherQuery( q );

    }

    public void registerAppInstance4Domain(Domain domain, File contextPath) {

        String md5Hex = DigestUtils.md5Hex(contextPath.getAbsolutePath()).toUpperCase();

        Properties props = new Properties();
        props.put( "instancePath_" + md5Hex, contextPath.getAbsolutePath() );

        this.mergeDomain( domain.name , props );

        /**
         *  Manage the Projects per Domain
         */
        for (Project p : domain.projects ) {

            this.mergeProject( p.name );
            this.addProjectInDomainLink( domain, p );

            for (Consumer c : p.consumers ) {
                this.addConsumerProjectLink( c, p );

                for(String t: c.topics ) {
                    this.addTopicConsumerLink( t, c );
                }
            }

            for (Producer pr : p.producers ) {
                this.addProducerProjectLink( pr, p );

                for(String t: pr.topics ) {
                    this.addTopicProducerLink( t, pr );
                }
            }

            for( StreamsApp app : p.streamsApps) {

                this.addStreamsAppProjectLink( app, p );

                for( String t :  app.inputTopics ) {
                    this.addTopicConsumerLink( t, app );
                }

                for( String t :  app.outputTopics ) {
                    this.addTopicProducerLink( t, app );
                }

            }

        }



    }

    private void addStreamsAppProjectLink(StreamsApp app, Project p) {

        String appName = getStreamsAppNodeName( app );

        mergeNode( "KSApp", appName, null );

        String q = "MATCH (app:KSApp),(p:Project) " +
                "WHERE app.name = '" + appName + "' AND p.name = '" + p.name + "' " +
                "MERGE (p)-[r:hasStreamsApp]->(app);";

        exequteCypherQuery( q );

    }

    private String getStreamsAppNodeName(StreamsApp app) {
        return "KSApp [" + app.applicationId + "]";
    }

    private void addTopicConsumerLink(String t, StreamsApp app) {

        mergeTopic( t );

        String name = this.getStreamsAppNodeName( app );

        String q = "MATCH (app:KSApp),(t:Topic) " +
                "WHERE t.name = '" + t + "' AND app.name = '" + name + "' " +
                "MERGE (app)-[r:readsFromTopic]->(t);";

        exequteCypherQuery( q );

    }

    private void addTopicProducerLink(String t, StreamsApp app) {

        mergeTopic( t );

        String name = this.getStreamsAppNodeName( app );

        String q = "MATCH (app:KSApp),(t:Topic) " +
                "WHERE t.name = '" + t + "' AND app.name = '" + name + "' " +
                "MERGE (app)-[r:writesToTopic]->(t);";

        exequteCypherQuery( q );

    }

    private void addTopicConsumerLink(String t, Consumer c) {

        mergeTopic( t );

        String name = "CONS (" + c.groupId + ") <- " + c.principal;

        String q = "MATCH (c:Consumer),(t:Topic) " +
                "WHERE t.name = '" + t + "' AND c.name = '" + name + "' " +
                "MERGE (c)-[r:readsFromTopic]->(t);";

        exequteCypherQuery( q );

    }

    private void addTopicProducerLink(String t, Producer pr) {

        mergeTopic( t );

        String name = "PROD <- " + pr.principal;
        mergeProducer(  name );


        String q = "MATCH (pr:Producer),(t:Topic) " +
                "WHERE t.name = '" + t + "' AND pr.name = '" + name + "' " +
                "MERGE (pr)-[r:writesIntoTopic]->(t);";

        exequteCypherQuery( q );

    }

    private void mergeProducer(String name) {
        mergeNode( "Producer", name, null );
    }


    private void addProducerProjectLink(Producer pr, Project p) {

        String name = getProducerNodeName( pr );

        mergeProducer( name );

        String q = "MATCH (pr:Producer),(p:Project) " +
                "WHERE pr.name = '" + name + "' AND p.name = '" + p.name + "' " +
                "MERGE (p)-[r:hasProducer]->(pr);";

        exequteCypherQuery( q );

    }



    private void addConsumerProjectLink(Consumer c, Project p) {

        String name = getConsumerNodeName( c );

        mergeNode( "Consumer", name, null );

        String q = "MATCH (t:Consumer),(s:Project) " +
                "WHERE t.name = '" + name + "' AND s.name = '" + p.name + "' " +
                "MERGE (s)-[r:hasConsumer]->(t);";

        exequteCypherQuery( q );

    }

    private void addProjectInDomainLink(Domain domain, Project p) {

            String q = "MATCH (t:Domain),(s:Project) " +
                    "WHERE t.name = '" + domain.name + "' AND s.name = '" + p.name + "' " +
                    "MERGE (s)-[r:providesAppInstance]->(t);";

            exequteCypherQuery( q );

    }

    private String getConsumerNodeName( Consumer c ) {
        String name = "CONS (" + c.groupId + ") <- " + c.principal;
        return name;
    }

    private String getProducerNodeName(Producer pr) {
        String name = "PROD <- " + pr.principal;
        return name;
    }
}
