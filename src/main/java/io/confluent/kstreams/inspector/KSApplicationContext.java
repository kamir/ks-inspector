package io.confluent.kstreams.inspector;

import io.confluent.ksql.KSQLServerInstance;

import java.io.File;
import java.util.Vector;

/**
 *
 * The
 *
 */
public class KSApplicationContext {

    public KSQLServerInstance ksqlServer = new KSQLServerInstance();

    public KSApplicationPath appPath = new KSApplicationPath();


    Vector<String> expected_TOPICS = new Vector<>();
    Vector<String> expected_TABLES = new Vector<>();
    Vector<String> expected_STREAMS = new Vector<>();
    Vector<String> expected_QUERIES = new Vector<>();

    String[] TOPICS_on_server;
    String[] TABLES_on_server;
    String[] STREAMS_on_server;
    String[] QUERIES_on_server;

    public KSApplicationContext(String a1, String a2, String a3, String host, String port) {
        setQueryFolder( a1 );
        setKSQLFilename( a2 );
        setKSQLBufferFolder( a3 );

        setKSQLServerHost( host, port );
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

}
