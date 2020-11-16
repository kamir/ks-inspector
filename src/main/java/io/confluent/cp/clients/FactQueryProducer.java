package io.confluent.cp.clients;

import org.apache.kafka.clients.producer.*;

import java.util.Properties;

public class FactQueryProducer extends GenericProducerFactory {

    public static int nrOfStatements = 0;

    static Properties props;

    static String TOPIC = "_kst_knowledgegraph";

    public static void init( String appId ) {
        Properties props = GenericProducerFactory.getClientProperties();
        producer = createProducer( props, appId );
    }

    public static void sendFact( String query ) {

        try {

            RecordMetadata metadata = null;

            nrOfStatements = nrOfStatements + 1;

            final ProducerRecord<String, String> record =
                    new ProducerRecord<String,String>(TOPIC, ""+nrOfStatements, query );

            producer.send(record).get();

        }
        catch (Exception ex) {
            ex.printStackTrace();
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

}


