package io.confluent.cp.mdmodel.kafka;

import io.confluent.mdgraph.model.IKnowledgeGraph;
import org.apache.commons.csv.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;


public class CSVFileFlowGraphProcessor {

    /**
     * We read the CSV file and populate the provided KG instance.
     *
     * We clean up the graph if append is set to False.
     *
     * @param csvFile
     * @param kg
     * @param append
     */
    public static void process(File csvFile, IKnowledgeGraph kg, boolean append) {

        if ( !append ) kg.clearGraph();

        CSVParser parser = null;

        System.out.println( "*** CSVFileFlowGraphProcessor *** ==> start working on CSV file: " + csvFile.getAbsolutePath() );

        try {

            parser = CSVParser.parse(csvFile, Charset.defaultCharset(), CSVFormat.EXCEL);

            System.out.println( "*** TSV Parser has been created. ***" );

            int z = 0;

            int nc = 0;
            int np = 0;
            int nk = 0;

            for (CSVRecord csvRecord : parser) {

                if( z==0 ) {
                    System.out.println( "---[EXAMPLE RECORD]---------------------------" );
                    System.out.println( csvRecord.toString() );
                    System.out.println( "----------------------------------------------" );
                }

                    String[] row = csvRecord.get(0).split("\t");

                    String service = row[0];
                    String team = row[1];

                    System.out.println("\n" + "------------------------------------------------------------------------------");
                    System.out.println(  z + " -> [" + service + "]");
                    System.out.println("------------------------------------------------------------------------------");
                    kg.addServiceNode( service );

                    kg.addTeamNode( team );

                try {
                    String[] TOPICLISTC = getTopicsFromList(row[5]);

                    int i = 0;
                    System.out.print("    C: " + TOPICLISTC.length + " -> [" + (TOPICLISTC[0].length() > 0) + "] \n");
                    for (String t : TOPICLISTC) {
                        if (t.length() > 0) {
                            kg.addServiceAsConsumerOfTopic(service, t);
                            System.out.println("     (" + i + ") C: {" + service + "} <--<consumes>-- {" + t + "}");
                            i++;
                        }
                    }
                    if (i == 0) nc++;


                    String[] TOPICLISTP = getTopicsFromList(row[4]);

                    int j = 0;
                    System.out.print("    P: " + TOPICLISTP.length + " -> [" + (TOPICLISTP[0].length() > 0) + "] \n");
                    for (String t : TOPICLISTP) {
                        if (t.length() > 0) {
                            kg.addServiceAsPublisherToTopic(service, t);
                            System.out.println("     (" + j + ") P: {" + service + "} --<publishes>--> {" + t + "}");
                            j++;
                        }
                    }
                    ;
                    if (j == 0) np++;
                    if ( i==0 && j==0 ) nk++;

                    z++;

                } catch(Exception ex) {

                    // If processing the CSV fails we have no reason to continue.

                    // We simply print the content of the las "ROW"
                    for( String t: row )
                        System.out.println( t );

                    System.out.println( ex.getMessage() );
                    System.out.println( ex.getCause() );

                    System.exit(0);

                }

            }
            System.out.println( "\n==================================================================================" );
            System.out.println( z + " services ");
            System.out.println(   "----------------------------------------------------------------------------------" );
            System.out.println( " services with no Kafka consumer   :" + nc );
            System.out.println( " services with no Kafka producer   :" + np );
            System.out.println( " services with no Kafka connection :" + nk );
            System.out.println( "==================================================================================" );

        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static String[] getTopicsFromList(String s) {
        //System.out.println( s );
        return s.split(" ");
    }

}
