package io.confluent.mdgraph.model;

import io.confluent.cp.factflow.FactQueryProducer;
import io.confluent.cp.mdmodel.infosec.Classifications;
import io.confluent.kafka.schemaregistry.client.rest.entities.Schema;
import io.confluent.kafka.schemaregistry.client.rest.entities.SchemaReference;
import io.confluent.ks.modern.model.Domain;
import io.confluent.ks.modern.model.CloudCluster;
import io.confluent.ks.modern.model.Project;
import io.confluent.ks.modern.model.StreamsApp;
import io.confluent.ks.modern.model.Consumer;
import io.confluent.ks.modern.model.Producer;
import io.confluent.ks.modern.parser.DomainParser;
import java.util.HashSet;
import org.apache.commons.codec.digest.DigestUtils;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.TransactionWork;

import java.io.File;
import java.util.*;

public class KnowledgeGraphViaKafkaTopic implements IKnowledgeGraph {

    static KnowledgeGraphViaKafkaTopic graph = null;

    public static IKnowledgeGraph getGraph() {
        if ( graph == null ) {
            graph = new KnowledgeGraphViaKafkaTopic();
        }
        return graph;
    }

    // START SNIPPET: shutdownHook
    public static void registerShutdownHook(final Driver driver)
    {
        // Registers a shutdown hook for the Neo4j instance so that it
        // shuts down nicely when the VM exits (even if you "Ctrl-C" the
        // running example before it's completed)
        Runtime.getRuntime().addShutdownHook( new Thread()
        {
            @Override
            public void run()
            {
                FactQueryProducer.close();

            }
        } );

    }

    /**
     * Every CYPHER statement will be intercepted in order to keep the Graph data independent from a Neo4J service.
     *
     * @param q
     */
    public void exequteCypherQuery(String q) {
        StringBuffer sb = new StringBuffer();
        exequteCypherQuery( q, false, sb, "0" );
    }

    public void exequteCypherQuery(String q, boolean verbose, StringBuffer logger, String qid) {

        try {
            FactQueryProducer.sendFact(q);
        }
        catch( Exception ex ) {
            ex.printStackTrace();
            System.exit(-1);
        }
    }

    @Override
    public void addNode(String type, String name, Properties props) {

        addNode( type, name );
        for( String k: props.stringPropertyNames() ) {
            addPropertiesToNode(type, "name", name, k, props.getProperty( k ) );
        }

    }

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

    @Override
    public void addPropertiesToNode(String T, String SK, String SV, String K, String V) {
        exequteCypherQuery( "MATCH (N:" + T + "{ " + SK + ":'" + SV + "' } ) SET N." + K + " = '" + V + "';" );
    }

    public void addNode(String type, String name ) {
        exequteCypherQuery( "CREATE (N:" + type + " { name : '" + name + "' label : '" + type + "_" + name + "' } );" );
    }

    @Override
    public void addTopicNode(String name) { mergeNode( "Topic", name ); }

    @Override
    public void addServiceNode(String name) { mergeNode( "Service", name ); }

    @Override
    public void addTeamNode(String name) {
        mergeNode( "Team", name );
    }

    @Override
    public void exportFullGraphAsCSV(File outputFile) {
        showExportInfo("exportFullGraphAsCSV");
        exequteCypherQuery( "CALL apoc.export.csv.all(\"" + outputFile.getAbsolutePath() + "\", {})" );
    }

    @Override
    public void exportFullGraphsEdgeListAsCSV(File outputFile) {
        showExportInfo( "exportFullGraphsEdgeListAsCSV" );
        // exequteCypherQuery( "CALL apoc.export.csv.all(\"" + outputFile.getAbsolutePath() + "\", {})" );
    }

    @Override
    public void exportFullGraphAsGraphML(File outputFile) {
        showExportInfo( "exportFullGraphAsGraphML" );
        exequteCypherQuery( "CALL apoc.export.graphml.all(\"" + outputFile.getAbsolutePath() + "\", {})" );
    }

    @Override
    public void exportFullGraphsNodeListAsCSV(File outputFile) {
        showExportInfo( "exportFullGraphsNodeListAsCSV" );
        exequteCypherQuery( "CALL apoc.export.csv.all(\"" + outputFile.getAbsolutePath() + "\", {})" );
    }

    private void showExportInfo(String CALLER) {
        System.out.println( "\n*** NOTE *** {"+CALLER+"}" );
        System.out.println( "  - Documentation with details regarding Neo4J setup for data export is available here:" );
        System.out.println( "    https://neo4j.com/labs/apoc/4.1/export\n" );
        System.out.println( "--- Did you set? -------------------------------------- ");
        System.out.println( " apoc.export.file.enabled=true" );
        System.out.println( " apoc.import.file.use_neo4j_config=false" );
        System.out.println( "------------------------------------------------------- ");
    }


    public void addCGNode(String cgId) {
        mergeNode( "ConsumerGroup", cgId );
    }

    public void addServiceAsConsumerOfTopic(String service, String topic) {
        addServiceNode( service );
        addTopicNode( topic );
        addLink( service, "Service", topic, "Topic", "consumes");
    }

    public void addServiceAsPublisherToTopic(String service, String topic) {
        addServiceNode( service );
        addTopicNode( topic );
        addLink(service, "Service", topic, "Topic", "publishes");
    }


    @Override
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

    @Override
    public void clearGraph() {
        deleteAllFacts();
    }

    @Override
    public boolean describe() {
        System.out.println( "{" + this.getClass().getName() + "} implements the KG module.");
        return this.isReadyForDataProcessing();
    }

    @Override
    public boolean isReadyForDataProcessing() {
        if ( this.graph != null && FactQueryProducer.isReady() ) return true;
        else
            return false;
    }

    public void deleteAllFacts() {

        System.out.println( ">>> Deletion of graph is not supported in [class :: " + this.getClass() + "]" );

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

    public void mergeNode(String type, String name) {

        String q = "MERGE (t:" + type + " { name: '" + name + "'});";

        exequteCypherQuery( q );

    }

    public void mergeNode(String type, String name, Properties props) {

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

    private void mergeCloudCluster(String env, Properties props) {

        String q = "MERGE (t:CloudCluster { name: '" + env + "'});";

        exequteCypherQuery( q );

        if ( props != null ) {
            Enumeration<String> en = (Enumeration<String>)props.propertyNames();
            while( en.hasMoreElements() ) {
                String k = en.nextElement();
                String v = props.getProperty( k );
                addPropertiesToNode("CloudCluster", "name", env, k, v);
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

    @Override
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

    @Override
    public void registerCloudCluster(CloudCluster c, File contextPath) {

        String md5Hex = DigestUtils.md5Hex(contextPath.getAbsolutePath()).toUpperCase();

        Properties props = new Properties();
        props.put( "instancePath_" + md5Hex, contextPath.getAbsolutePath() );

        props.put( "clusterId", c.clusterId );
        props.put( "clusterType", c.clusterType );
        props.put( "org", c.org );
        props.put( "owner", c.owner );
        props.put( "ownerContact", c.ownerContact );
        props.put( "provider",c.provider );
        props.put( "region",c.region );

        props.put( "org", c.org );
        props.put( "availability", c.availability );

        this.mergeCloudCluster( c.name , props );

        for(String tag : c.tags ) {
            this.addTagLink( c.name, "CloudCluster", tag );
        }

        Map<String,String> principals = c.principals;
        for( String alias : principals.keySet() ) {
            addLink( c.name, "CloudCluster", alias, "PrincipleAlias", "hasPrincipleAlias" );
            addLink( alias,"PrincipleAlias", principals.get( alias ), "ServiceAccount", "hasServiceAccount" );
            addLink( c.name, "CloudCluster", principals.get( alias ), "ServiceAccount", "hasServiceAccount" );
        }

        /**
         * Read the Domain for a particular DOMAIN File in the DOMAIN
         */
        List<String> dffs = c.domainFileFolders;

        String CCNAME = c.name;

        if (dffs != null) {

            for( String dff : dffs ) {

                File f = new File(dff);

                System.out.println("# Link Domain to CloudCluster: " + CCNAME + " -> " + f.canRead() + " => " + dff + " :: " + f.getAbsolutePath());

                /**
                 * Iterate over a list of folders within a domain-folder
                 */
                for (File folders : f.listFiles()) {

                    System.out.println(folders.getAbsolutePath() + " - " + folders.canRead());

                    if (folders.isDirectory()) {

                        for (File file : folders.listFiles()) {

                            final DomainParser parser = new DomainParser();

                            try {

                                final Domain domain = parser.loadFromFile(file);

                                String DN = domain.name;

                                addLink(CCNAME, "CloudCluster", DN, "Domain", "hostsDomain");

                            }
                            catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                }
            }

        }
        else {
            System.out.println("# No Domain-Folder is available in " + CCNAME);
        }

    }

    private void addLink(String sname, String stype, String tname, String ttype, String linkName) {

        mergeNode( stype, sname, null );
        mergeNode( ttype, tname, null );

        String q = "MATCH (s:" + stype + "),(t:"+ ttype +") " +
                "WHERE s.name = '" + sname + "' AND t.name = '" + tname + "' " +
                "MERGE (s)-[r:" + linkName + "]->(t);";

        exequteCypherQuery( q );

    }

    private void addTagLink(String name, String type, String tag) {

        mergeNode( "tag", tag, null );

        String q = "MATCH (n1:" + type + "),(t:tag) " +
                "WHERE n1.name = '" + name + "' AND t.name = '" + tag + "' " +
                "MERGE (n1)-[r:hasTag]->(t);";

        exequteCypherQuery( q );

    }

    @Override
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

    private String getConsumerNodeName(Consumer c ) {
        String name = "CONS (" + c.groupId + ") <- " + c.principal;
        return name;
    }

    private String getProducerNodeName(Producer pr) {
        String name = "PROD <- " + pr.principal;
        return name;
    }

}
