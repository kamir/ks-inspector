package io.confluent.cp.factflow;


import io.confluent.cp.mdmodel.JiraTicketFlowGraph;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class JiraChangeEventConsumer extends GenericProducerWrapper {

    static String TOPIC = "jira-cdc";

    Consumer<String, String> consumer = null;

    Properties properties = null;

    public JiraChangeEventConsumer(String cg, Properties props) {
        properties = props;
        consumer = createConsumer( props, cg);
    }

    public static int nrOfStatements = 0;

    static Properties props;

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

    public void processEvents(JiraTicketFlowGraph g) {

        final int giveUp = 10;   int noRecordsCount = 0;

        System.out.println( "> Read jira-cdc queries ...");

        while (true) {

            final ConsumerRecords<String, String> consumerRecords =
                    consumer.poll(Duration.ofMillis(1000));

            if (consumerRecords.count()==0) {
                noRecordsCount++;
                if (noRecordsCount > giveUp) break;
                else {
                    System.out.println( (100.0 * noRecordsCount/giveUp) + " % ... waiting time until end of procedure.");
                    continue;
                }
            }

            consumerRecords.forEach(record -> {

                System.out.println( " " );
                System.out.printf("Consumer Record: [%s, %s]:(%s)\n\t<%s>\n",
                        record.partition(), record.offset(), record.key(), record.value()
                        );

                try {

                    g.handleEvent( record.value()  );

                }
                catch(Exception e) {
                    System.out.println ( "\t****> " + e.getCause() );
                }

            });

            consumer.commitAsync();
        }
        consumer.close();

    }

}


