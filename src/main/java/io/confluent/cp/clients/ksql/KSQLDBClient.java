package io.confluent.cp.clients.ksql;

import io.confluent.ksql.api.client.*;
import io.confluent.mdgraph.KnowledgeGraph;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class KSQLDBClient {

    public static String KSQLDB_SERVER_HOST = "localhost";
    public static int KSQLDB_SERVER_HOST_PORT = 8088;

    public static void main(String[] args) throws Exception {

        ClientOptions options = ClientOptions.create()
                .setHost(KSQLDB_SERVER_HOST)
                .setPort(KSQLDB_SERVER_HOST_PORT);
        Client client = Client.create(options);

        // Send requests with the client by following the other examples
        KnowledgeGraph runtimeGraph = new KnowledgeGraph();
        //runtimeGraph.deleteAllFacts();
        runtimeGraph.exampleCalls();

        System.out.println( "Load metadata ... " );;
        CompletableFuture<List<QueryInfo>> listQueries = client.listQueries();
        CompletableFuture<List<StreamInfo>> listStreams = client.listStreams();
        CompletableFuture<List<TableInfo>> listTables = client.listTables();
        CompletableFuture<List<TopicInfo>> listTopics = client.listTopics();

        //CompletableFuture<SourceDescription> descriptionF = client.describeSource("my_source");

        CompletableFuture<Void> fAll = CompletableFuture.allOf( listTopics, listTables, listStreams, listQueries );

        CompletableFuture<String> lt = listTopics.thenApply(  v -> {

            List<TopicInfo> result11 = null;

            try {

                result11 = listTopics.get( 1 , TimeUnit.SECONDS );

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }

            System.out.println(result11.size());

            return "DATA";
        }

        );

        // When all the Futures are completed, call `future.join()` to get their results and collect the results in a list -
        CompletableFuture<String> allPageContentsFuture = fAll.thenApply( v -> {

            try {

                List<QueryInfo> result1 = listQueries.get();
                System.out.println(result1.size());
                ;

                List<StreamInfo> result2 = listStreams.get();
                System.out.println(result2.size());
                ;

                List<TableInfo> result3 = listTables.get();
                System.out.println(result3.size());
                ;

                List<TopicInfo> result4 = listTopics.get();
                System.out.println(result4.size());
/*
                SourceDescription description = descriptionF.get();

                System.out.println("This source is a " + description.type());
                System.out.println("This stream/table has " + description.fields().size() + " columns.");
                System.out.println(description.writeQueries().size() + " queries write to this stream/table.");
                System.out.println(description.readQueries().size() + " queries read from this stream/table.");
*/

                ;
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }

            System.out.println( "Done. Loaded some metadata ... " );;

            return "DATA";

        });

        System.out.println( "*" + allPageContentsFuture.get() );

        runtimeGraph.show();

        // Terminate any open connections and close the client
        client.close();

        System.out.println( "Done." );
        System.exit(0);

    }



}
