package io.confluent.cp.factcollector.ksql;

import io.confluent.ksql.api.client.*;
import io.confluent.mdgraph.model.KnowledgeGraphNeo4J;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class KSQLDBClient2 {

    public static String KSQLDB_SERVER_HOST = "192.168.0.154";
    public static int KSQLDB_SERVER_HOST_PORT = 8088;

    public static void main(String[] args) throws Exception {

        ClientOptions options = ClientOptions.create()
                .setHost(KSQLDB_SERVER_HOST)
                .setPort(KSQLDB_SERVER_HOST_PORT);
        Client client = Client.create(options);


        // Send requests with the client by following the other examples
        KnowledgeGraphNeo4J runtimeGraph = KnowledgeGraphNeo4J.getGraph();
        runtimeGraph.deleteAllFacts();
        //runtimeGraph.exampleCalls();

        System.out.println( "Load metadata ... " );

        String tableName = "KSQL_PROCESSING_LOG";

        SourceDescription streamDescription;

        try {

            CompletableFuture<SourceDescription> cf1 = client.describeSource(tableName);
            streamDescription = cf1.get( 3 , TimeUnit.SECONDS);

            System.out.println( streamDescription.topic() );

        }
        catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Failed to describe stream/table", e);
        }

        runtimeGraph.show();

        // Terminate any open connections and close the client
        client.close();

        System.out.println( "Done." );

        System.exit(0);

    }



}
