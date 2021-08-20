package io.confluent.cp.mdmodel;

import io.confluent.mdgraph.model.IKnowledgeGraph;
import org.apache.commons.csv.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Vector;

public class CSVFileFlowGraphProcessor {

    public static void process(File csvFile, IKnowledgeGraph kg) {

        CSVParser parser = null;
        System.out.println( csvFile.getAbsolutePath() );

        try {
            parser = CSVParser.parse(csvFile, Charset.defaultCharset(), CSVFormat.EXCEL);
            int z = 0;
            for (CSVRecord csvRecord : parser) {
                if( z==0 ) {
                    System.out.println(csvRecord.toString());
                }
                else {

                    String[] row = csvRecord.get(0).split("\t");

                    System.out.println(z + "->[" + row[0] + "]");

                    kg.addServiceNode( row[0]);
                    kg.addTeamNode( row[1]);

                    // kg.addCGNode( row[7] );


                    if ( row.length > 4 )
                        for( String t : getTopicsFromList( row[4] ) )
                            kg.addTopicToConsume( row[0], t );

                    if ( row.length > 5 )
                        for( String t : getTopicsFromList( row[5] ) )
                            kg.addTopicToPublish( row[0], t );


                }
                z++;

            }
            System.out.println( z + " <<< rows ");
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static String[] getTopicsFromList(String s) {
        return cleanNames( s.split("\n") );
    }

    private static String[] cleanNames(String[] split) {
        int i = 0;
        for( String s : split) {

            String temp = s.trim();

            if ( temp.startsWith("- name: ") )
                temp = temp.substring(8);

            if ( temp.startsWith("- ") )
                temp = temp.substring(3);

            if ( temp.startsWith("\"") )
                temp = temp.substring(1);

            if ( temp.endsWith("\"") )
                temp = temp.substring(0,temp.length()-1);

            split[i] = temp;
        }
        return split;
    }

}
