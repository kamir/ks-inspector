package io.confluent.mdgraph.model;

import io.confluent.cp.factflow.FactQueryConsumer;

import org.apache.log4j.LogManager;
import org.neo4j.driver.*;
import org.neo4j.driver.Result;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


public class KnowledgeGraphNeo4J extends KnowledgeGraphViaKafkaTopic {

    private static final org.apache.log4j.Logger logger = LogManager.getLogger(KnowledgeGraphNeo4J.class);

    static String databaseDirectory = "graph-db";
    static String DEFAULT_DATABASE_NAME = "neo4j";
    static String uri = "bolt://localhost:7687";
    static String username = "neo4j";
    static String password = "test";

    public static void init(Properties properties) {

        String databaseDirectoryTEMP = properties.getProperty( "KST_NEO4J_DATABASE_DIRECTORY" );
        String DEFAULT_DATABASE_NAME_TEMP = properties.getProperty( "KST_NEO4J_DEFAULT_DATABASE_NAME" );
        String uriTEMP = properties.getProperty( "KST_NEO4J_URI" );
        String usernameTEMP = properties.getProperty( "KST_NEO4J_USERNAME" );
        String passwordTEMP = properties.getProperty( "KST_NEO4J_PASSWORD" );

        if ( usernameTEMP != null ) username = usernameTEMP;
        if ( passwordTEMP != null ) password = passwordTEMP;

        if ( databaseDirectoryTEMP != null ) databaseDirectory = databaseDirectoryTEMP;
        if ( DEFAULT_DATABASE_NAME_TEMP != null ) DEFAULT_DATABASE_NAME = DEFAULT_DATABASE_NAME_TEMP;
        if ( uriTEMP != null ) uri = uriTEMP;

        /**
        System.out.println( databaseDirectory );
        System.out.println( DEFAULT_DATABASE_NAME );
        System.out.println( uri );
        System.out.println( username );
        System.out.println( password );
        **/

    }

    public void readGraphFromTopic( String consumerGroup, Properties props ) {

        FactQueryConsumer consumer = new FactQueryConsumer( consumerGroup, props );
        consumer.processCypherQueries( this );

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

    private static KnowledgeGraphNeo4J graph = null;

    private Driver driver;

    public static KnowledgeGraphNeo4J getGraph() {
        if ( graph == null ) {
            graph = new KnowledgeGraphNeo4J();
        }
        return graph;
    }



    private KnowledgeGraphNeo4J() {

        driver = GraphDatabase.driver( uri, AuthTokens.basic( username, password ) );

        registerShutdownHook( driver );

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

    public void exequteCypherQuery(String q) {

        Session s = driver.session();

        String st = s.writeTransaction( new TransactionWork<String>()
        {
            @Override
            public String execute(org.neo4j.driver.Transaction tx) {

                Result result = tx.run( q );

                while (result.hasNext()) {

                    org.neo4j.driver.Record row = result.next();

                    System.out.println(row.asMap().toString());

                }

                return result.toString();


            }

        } );


    }


    public void queryFromFile(File queryFilePath) {

        try {

            FileReader fr = new FileReader(queryFilePath);
            BufferedReader br = new BufferedReader( fr );
            StringBuffer sb = new StringBuffer();
            while( br.ready() ) {
                String line = br.readLine();
                if ( !line.startsWith("#") )
                    sb.append( line + " " );
            }
            System.out.println( "#####################################################################################" );
            System.out.println( "# Query Path: " +  queryFilePath.getAbsolutePath() );
            System.out.println( "# " );
            System.out.println( "# Query: " );
            System.out.println( "# ------ " );
            System.out.println( "  " + sb.toString() );
            System.out.println( "#####################################################################################" );

            exequteCypherQuery( sb.toString() );

        }
        catch (Exception e) {
            e.printStackTrace();
        };

    }
}
