package io.confluent.cp.factflow;

import io.confluent.mdgraph.model.KnowledgeGraphNeo4J;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.Collections;
import java.util.Properties;

public class FactQueryConsumer extends GenericProducerWrapper {

    Consumer<String, String> consumer = null;

    Properties properties = null;

    public FactQueryConsumer(String cg, Properties props) {
        properties = props;
        consumer = createConsumer( props, cg);
    }

    public static int nrOfStatements = 0;

    static Properties props;

    static String TOPIC = FactQueryProducer.TOPIC;

    private Consumer<String, String> createConsumer(Properties props, String consumerGroup ) {

        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");

        props.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroup + "_" + System.currentTimeMillis() );
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        // Create the consumer using props.
        final Consumer<String, String> consumer = new KafkaConsumer<>(props);

        // Subscribe to the topic.
        consumer.subscribe(Collections.singletonList(TOPIC));
        return consumer;
    }

    public void processCypherQueries(KnowledgeGraphNeo4J knowledgeGraphNeo4J) {

        final int giveUp = 10;   int noRecordsCount = 0;

        System.out.println( "Read fact queries ...");

        while (true) {

            final ConsumerRecords<String, String> consumerRecords =
                    consumer.poll(1000);

            if (consumerRecords.count()==0) {
                noRecordsCount++;
                if (noRecordsCount > giveUp) break;
                else {
                    System.out.println( (100.0 * noRecordsCount/giveUp) + " % ... waiting time until end of procedure.");
                    continue;
                }
            }

            consumerRecords.forEach(record -> {
                System.out.printf("Consumer Record:(%s, %s, %s, %s)\n",
                        record.key(), record.value(),
                        record.partition(), record.offset());

                knowledgeGraphNeo4J.exequteCypherQuery( record.value() );

            });

            consumer.commitAsync();
        }
        consumer.close();

    }

    public void processReadClusterStateTimestamps(KnowledgeGraphNeo4J knowledgeGraphNeo4J) {

        final int giveUp = 10;   int noRecordsCount = 0;

        System.out.println( "Read fact queries ...");

        while (true) {

            final ConsumerRecords<String, String> consumerRecords =
                    consumer.poll(1000);

            if (consumerRecords.count()==0) {
                noRecordsCount++;
                if (noRecordsCount > giveUp) break;
                else {
                    System.out.println( (100.0 * noRecordsCount/giveUp) + " % ... waiting time until end of procedure.");
                    continue;
                }
            }

            consumerRecords.forEach(record -> {
                System.out.printf("Consumer Record:(%s, %s, %s, %s)\n",
                        record.key(), record.value(),
                        record.partition(), record.offset());

                knowledgeGraphNeo4J.exequteCypherQuery( record.value() );

            });

            consumer.commitAsync();
        }
        consumer.close();

    }

}


