package io.confluent.cp.clients;

import org.apache.kafka.clients.producer.Producer;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class GenericProducerFactory {

    public static Producer producer = null;


    public static void flush() {

        if( producer != null ) {
            producer.flush();
        }
    }

    public static void close() {

        if( producer != null ) {
            producer.flush();
            producer.close();
        }
    }

}

