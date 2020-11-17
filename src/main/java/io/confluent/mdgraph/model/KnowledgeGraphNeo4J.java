package io.confluent.mdgraph.model;

import io.confluent.cp.clients.FactQueryConsumer;

import org.apache.log4j.LogManager;
import org.neo4j.driver.*;
import org.neo4j.driver.Result;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


public class KnowledgeGraphNeo4J extends KnowledgeGraphViaKafkaTopic {

    private static final org.apache.log4j.Logger logger = LogManager.getLogger(KnowledgeGraphNeo4J.class);

    static String databaseDirectory = "graph-db";
    static String DEFAULT_DATABASE_NAME = "neo4j";
    static String uri = "bolt://localhost:7687";
    static String username = "neo4j";
    static String password = "admin";

    public static void init(Properties properties) {

        databaseDirectory = properties.getProperty( "KST_NEO4J_DATABASE_DIRECTORY" );
        DEFAULT_DATABASE_NAME = properties.getProperty( "KST_NEO4J_DEFAULT_DATABASE_NAME" );
        uri = properties.getProperty( "KST_NEO4J_URI" );
        username = properties.getProperty( "KST_NEO4J_USERNAME" );
        password = properties.getProperty( "KST_NEO4J_PASSWORD" );

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

                return result.toString();

            }

        } );


    }


}
