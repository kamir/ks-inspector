package io.confluent.cp.apps;

import io.confluent.cp.cfg.CCloudClusterWrapper;
import io.confluent.cp.cs.AppDescriptorLoader;
import io.confluent.cp.mdmodel.kstreams.KafkaStreamsApplicationContextHandler;
import net.christophschubert.kafka.clusterstate.formats.domain.Domain;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;

import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.*;

public class KafkaStreamsExample01 {

  private static final org.apache.log4j.Logger logger = Logger.getRootLogger();

  public static Properties getFlowSpecificProperties() {

    Properties props = CCloudClusterWrapper.getProps();

    props.put(StreamsConfig.APPLICATION_ID_CONFIG, "TSAExample_10_" + System.currentTimeMillis() );

    props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
    props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

    return props;

  }

  public static void main(String[] args) throws Exception {

    Properties props = getFlowSpecificProperties();

    StreamsConfig streamsConfig = new StreamsConfig( props );

    StreamsBuilder builder = new StreamsBuilder();

    Serde<String> stringSerde1 = Serdes.String();
    Serde<String> stringSerde2 = Serdes.String();
    Serde<String> stringSerde3 = Serdes.String();
    Serde<String> stringSerde4 = Serdes.String();

    final Map<String, String> serdeConfig = Collections.singletonMap("schema.registry.url",
            props.getProperty( "schema.registry.url" ));

    KStream<String, String> my_episodes = builder.stream( "OpenTSx_Episodes_B", Consumed.with( stringSerde1, stringSerde2 ) );

    KStream<String, String> my_episodes_fft = my_episodes.mapValues( (k, v) -> v );

    my_episodes_fft.to("EPISODES_DATA_B_SIMPLE_STATS_EXPERIMENT_TAG",  Produced.with( stringSerde3, stringSerde4 ) );

    my_episodes_fft.print(Printed.<String, String>toSysOut().withLabel("[EPISODES_DATA]"));

    Topology topology = builder.build();

    /**
     * Application Context Management ... we dump PROPS and TOPOLOGY to a topic in our kafka cluster.
     */
    String instancePath1 = "./src/main/cluster-state-tools-data/contexts/order-processing/instances/order-processing-ks1.domy";
    Domain d = AppDescriptorLoader.readAppDescriptor( instancePath1 );

    KafkaStreamsApplicationContextHandler.init( "MacBook PRO 2", d.name );

    KafkaStreamsApplicationContextHandler.persistTopology( topology.describe().toString()  );

    KafkaStreamsApplicationContextHandler.persistRuntimeProperties( props );
    KafkaStreamsApplicationContextHandler.persistRuntimeProperties( System.getenv() );


    /**
     * Now we are ready for streaming the data ...
     */
    //KafkaStreams kafkaStreams = new KafkaStreams(topology, streamsConfig);

    //kafkaStreams.cleanUp();

    //kafkaStreams.setUncaughtExceptionHandler((t, e) -> {
    //  logger.error("had exception ", e);
    //});

    //kafkaStreams.close();

    logger.info("> Shutting down the event processing example application now.");

    LogManager.shutdown();

    System.out.println("> Stopped KafkaStreamsExample01.");

    System.exit(0);

  }

}


