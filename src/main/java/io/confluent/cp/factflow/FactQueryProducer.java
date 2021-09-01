package io.confluent.cp.factflow;

import org.apache.kafka.clients.producer.*;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class FactQueryProducer extends GenericProducerWrapper {

    public static long startTime = 0;

    public static int nrOfStatements = 0;

    static Properties props;

    static String TOPIC = "_kst_knowledgegraph";

    public static void init( String appId, Properties properties ) {
        props = properties;
        producer = createProducer( props, appId );
        startTime = System.currentTimeMillis();
    }

    public static boolean showNote = true;

    public static int sentCounter = 0;

    public static void sendFact( String query ) throws ExecutionException, InterruptedException {

        try {

            RecordMetadata metadata = null;

            nrOfStatements = nrOfStatements + 1;

            final ProducerRecord<String, String> record =
                    new ProducerRecord<String,String>(TOPIC, startTime+"_"+nrOfStatements, query );

            if ( producer != null ) {
                producer.send(record).get();
                sentCounter++;
            }
            else {
                if ( showNote ) {
                    showNote = false;
                    System.out.println(">>> NOTE <<< \n\t  No Kafka Producer configured! Data will be collected in Neo4J Graph.\n\n");
                }
            }
        }
        catch (Exception ex) {

            ex.printStackTrace();

            props.list( System.out );

            System.out.println( "  " );

            throw ex;
        }

    }

    public static Producer<String,String> createProducer(Properties props, String appID) {

        if( props.get( ProducerConfig.CLIENT_ID_CONFIG ) == null )
            props.put(ProducerConfig.CLIENT_ID_CONFIG, appID);

        // props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "io.confluent.kafka.serializers.KafkaJsonSerializer");

        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");

        return new KafkaProducer<>(props);

    }

    public static boolean isReady() {
        return producer != null;
    }
}


