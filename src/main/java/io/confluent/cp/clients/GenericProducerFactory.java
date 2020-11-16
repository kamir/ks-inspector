package io.confluent.cp.clients;

import org.apache.kafka.clients.producer.Producer;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class GenericProducerFactory {

    public static Producer producer = null;

    public static Properties getClientProperties() {

        File f = new File( "ccloud.props"  );

        System.out.println( ">>> Client properties from file: " + f.getAbsolutePath() + " ("+f.canRead()+")");

        Properties properties = new Properties();
        try {
            properties.load(new FileReader( f ));

            System.out.println();
            properties.list( System.out );
            System.out.println();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return properties;

    }

    public static void flush() {

        producer.flush();

    }

    public static void close() {

        producer.flush();
        producer.close();

    }

}

