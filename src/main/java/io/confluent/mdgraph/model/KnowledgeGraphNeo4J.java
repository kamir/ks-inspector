package io.confluent.mdgraph.model;

import io.confluent.cp.clients.FactQueryConsumer;
import io.confluent.cp.cs.ClusterStateLoader;
import io.confluent.kafka.schemaregistry.client.rest.exceptions.RestClientException;

import org.apache.log4j.LogManager;
import org.neo4j.driver.*;
import org.neo4j.driver.Result;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import java.util.Properties;

import static org.neo4j.driver.Values.parameters;

public class KnowledgeGraphNeo4J extends KnowledgeGraphViaKafkaTopic {

    private static final org.apache.log4j.Logger logger = LogManager.getLogger(KnowledgeGraphNeo4J.class);

    public void readGraphFromTopic() {

        FactQueryConsumer consumer = new FactQueryConsumer( "DemoApp" );
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

    String databaseDirectory = "graph-db";
    String DEFAULT_DATABASE_NAME = "neo4j";
    String uri = "bolt://localhost:7687";

    private KnowledgeGraphNeo4J() {

        driver = GraphDatabase.driver( uri, AuthTokens.basic( "neo4j", "admin" ) );

        registerShutdownHook( driver );

    }

    // END SNIPPET: shutdownHook


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

        // System.out.println( st.toString() );

    }


}
