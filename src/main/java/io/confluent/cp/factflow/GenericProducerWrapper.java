package io.confluent.cp.factflow;

import org.apache.kafka.clients.producer.Producer;

public class GenericProducerWrapper {

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

