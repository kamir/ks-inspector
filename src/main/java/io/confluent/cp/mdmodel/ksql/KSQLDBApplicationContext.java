package io.confluent.cp.mdmodel.ksql;

import io.confluent.cp.mdmodel.kafka.TopicNamePair;

import java.io.File;
import java.util.Vector;

/**
 * The KSApplicationContext class will be the place where METADATA MANAGEMENT is integrated with the
 * Confluent cluster state tools and the knowledge graph about multiple streaming applications.
 */
public class KSQLDBApplicationContext {

    public static boolean ignoreContext = true;

    public KSQLServerInstance ksqlServer = new KSQLServerInstance();

    public KSQLDBApplicationPath appPath = new KSQLDBApplicationPath();

    Vector<String> expected_TOPICS = new Vector<>();
    Vector<String> expected_TABLES = new Vector<>();
    Vector<String> expected_STREAMS = new Vector<>();
    Vector<String> expected_QUERIES = new Vector<>();

    String[] TOPICS_on_server;
    String[] TABLES_on_server;
    String[] STREAMS_on_server;
    String[] QUERIES_on_server;

    String[] TOPICS_in_domain_context;


    String domain = null;
    String project = null;

    private KSQLDBApplicationContext() {
        // Private constructor to prevent direct instantiation
    }

    public static KSQLDBApplicationContext create(String a1, String a2, String a3, String host, String port, String domain, String project) {
        KSQLDBApplicationContext context = new KSQLDBApplicationContext();
        
        context.setQueryFolder(a1);
        context.setKSQLFilename(a2);
        context.setKSQLBufferFolder(a3);
        
        context.setKSQLServerHost(host, port);
        
        context.domain = domain;
        context.project = project;
        
        return context;
    }

    public void setKSQLServerHost( String host ) {
        this.ksqlServer.setHost( host );
    }

    public void setKSQLServerHost( String host, String port ) {
        this.ksqlServer.setHostAndPort( host, port );
    }

    public void setQueryFolder( String folder ) {
        this.appPath.setFolder( folder );
    }

    public void setKSQLFilename( String filename ) {
        this.appPath.setKSQLFilename( filename );
    }


    public void setTopicsInDomainContext(String[] topicsInContext) {
        this.TOPICS_in_domain_context = topicsInContext;
    }

    public void setTopicsOnServer(String[] topics) {
        this.TOPICS_on_server = topics;
    }

    public void setTablesOnServer(String[] tables) { this.TABLES_on_server = tables; }

    public void setQueriesOnServer(String[] queries) {
        this.QUERIES_on_server = queries;
    }

    public void setStreamsOnServer(String[] streams) {
        this.STREAMS_on_server = streams;
    }

    // public void setStreamsExpected(String[] streams) { this.expected_STREAMS = streams; }

    // public void setTopicsExpected(String[] topics) { this.expected_TOPICS = topics; }

    // public void setTablesExpected(String[] tables) {this.TABLES_on_server = tables; }

    // public void setQueriesExpected(String[] queries) {this.QUERIES_on_server = queries; }

    public File getKSQLFile() {
        return this.appPath.getKSQLFile();
    }

    public void setKSQLBufferFolder(String default_queryBufferFolder) {
        this.appPath.setKSQLBufferFolder(default_queryBufferFolder);
    }

    public void overview() {

        overview( "IN SPEC", x(expected_TOPICS), x(expected_STREAMS), x(expected_TABLES), x(expected_QUERIES) );

    }

    public void compare() {

        overview( "ON SERVER" , TOPICS_on_server, STREAMS_on_server, TABLES_on_server, QUERIES_on_server );
        overview( "IN SPEC", x(expected_TOPICS), x(expected_STREAMS), x(expected_TABLES), x(expected_QUERIES) );

        compareSollIst( "TOPIC", TOPICS_on_server, x(expected_TOPICS) );
        compareSollIst( "STREAM", STREAMS_on_server, x(expected_STREAMS) );
        compareSollIst( "TABLE", TABLES_on_server, x(expected_TABLES) );
        compareSollIst( "QUERY", QUERIES_on_server, x(expected_QUERIES) );

    }

    private void compareSollIst(String context, String[] on_server, String[] expected) {

        System.out.println("-------------------------");
        System.out.println("  " + context);
        System.out.println("-------------------------");
        System.out.println("  ");

        for( String exp : expected ) {
            System.out.println( exp );
            System.out.println(  );
        }

    }

    private void overview(String context, String[] topics, String[] streams, String[] tables, String[] queries) {

        System.out.println("=========================");
        System.out.println("  " + context);
        System.out.println("=========================");
        System.out.println("  ");

        System.out.println( ">  TOPICS  : " + topics.length);
        System.out.println( ">  STREAMS : " + streams.length);
        System.out.println( ">  TABLES  : " + tables.length);
        System.out.println( ">  QUERIES : " + queries.length);
        System.out.println("  ");

    }

    public void addExpectedStream(String ksql) {
        expected_STREAMS.add( ksql );
    }

    public String[] x( Vector<String> d) {
        return d.toArray( new String[d.size()] );
    }

    public void addExpectedTable(String ksql) {
        expected_TABLES.add( ksql );
    }



    public Vector<TopicNamePair> getTopicNameContextualization() {

        System.out.println( "> nr of topics: " + getTopicNames().size() );
        String[] topicINCONTEXT = new String[getTopicNames().size() ];

        Vector<TopicNamePair> alLTOPICS = new Vector<TopicNamePair>();

        int i = 0;
        for ( String topic : getTopicNames() ) {

            TopicNamePair p = new TopicNamePair( domain, project, topic );
            alLTOPICS.add( p );
            topicINCONTEXT[i] = p.contextualName;
            i++;
        }

        setTopicsInDomainContext( topicINCONTEXT );

        return alLTOPICS;

    }

    public Vector<String> getTopicNames() {
        return expected_TOPICS;
    }

    public void trackTopicName(String topicName) {
        this.expected_TOPICS.add( topicName );
    }

    public String putIntoDomainContext(String entity) {
        if ( ignoreContext ) return entity;
        else {
            TopicNamePair p = new TopicNamePair(domain, project, entity);
            return p.contextualName;
        }
    }
}


