package io.confluent.cp.apps;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.apache.commons.math3.transform.TransformType;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.Produced;
import org.opentsx.data.model.EpisodesRecord;
import org.opentsx.data.model.Event;
import org.opentsx.data.series.TimeSeriesObject;
import org.opentsx.data.series.TimeSeriesObjectFFT;
import org.opentsx.tsa.rng.ReferenceDataset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.*;

public class TSAExample10 {

  private static long   experiment_duration = 30000; // ms (-1 no limit)
  private static String EXPERIMENT_TAG = ReferenceDataset.EXPERIMENT__TAG;
  static MessageDigest md = null;

  private static final Logger LOG = LoggerFactory.getLogger(TSAExample10.class);

  public static Properties getFlowSpecificProperties() {

    Properties props = TSxStreamingAppHelper.getKafkaClientProperties();

    props.put(StreamsConfig.APPLICATION_ID_CONFIG, "TSAExample_10_" + System.currentTimeMillis() );

    props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
    //props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

    //props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, SpecificAvroSerde.class);
    props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, SpecificAvroSerde.class);

    return props;

  }

  public static void main(String[] args) throws Exception {

    Properties props = getFlowSpecificProperties();

    StreamsConfig streamsConfig = new StreamsConfig( props );

    StreamsBuilder builder = new StreamsBuilder();

    Serde<String> stringSerde1 = Serdes.String();
    Serde<String> stringSerde2 = Serdes.String();
    Serde<String> stringSerde3 = Serdes.String();


    final Map<String, String> serdeConfig = Collections.singletonMap("schema.registry.url",
            props.getProperty( "schema.registry.url" ));

    final Serde<EpisodesRecord> valueSpecificAvroSerde = new SpecificAvroSerde<>();
    valueSpecificAvroSerde.configure(serdeConfig, false); // `false` for record values

    KStream<String, EpisodesRecord> my_episodes = builder.stream( "OpenTSx_Episodes_B", Consumed.with( stringSerde1, valueSpecificAvroSerde));

    KStream<String, String>my_episodes_fft = my_episodes.mapValues( (k,v) -> getSimpleStats( k,v ) );

    my_episodes_fft.to("EPISODES_DATA_B_SIMPLE_STATS_" + EXPERIMENT_TAG,  Produced.with( stringSerde2, stringSerde3) );

    my_episodes_fft.print(Printed.<String, String>toSysOut().withLabel("[EPISODES_DATA]"));


    Topology topology = builder.build();

    FileWriter fw = new FileWriter( props.getProperty(StreamsConfig.APPLICATION_ID_CONFIG) + "_topology.dat");
    fw.write( topology.describe().toString() );
    fw.flush();
    fw.close();

    KafkaStreams kafkaStreams = new KafkaStreams(topology, streamsConfig);

    kafkaStreams.cleanUp();

    kafkaStreams.setUncaughtExceptionHandler((t, e) -> {
      LOG.error("had exception ", e);
    });

    LOG.info("Starting event processing example { experiment_duration=" + experiment_duration + " ms}.");
    kafkaStreams.start();

    if ( experiment_duration > 0) {
      Thread.sleep(experiment_duration);
    }

    kafkaStreams.close();

    LOG.info("Shutting down the event processing example application now.");

  }

  static Gson gson = new Gson();

  /**
   * getSimpleStats from TSO
   * -----------------------
   *
   * @param k - key (we extract the uuid which is part of the key and put it also into our result map.
   * @param v - the result map, calculated by the OpenTSx library. Simple descriptive statistics in this case.
   *
   * @return Hashtable with results encoded as JSON.
   */
  private static String getSimpleStats(String k, EpisodesRecord v) {

    TimeSeriesObject tso = TimeSeriesObject.getFromEpisode( v );

    Hashtable<String,String> stats = tso.getStatisticData( new Hashtable<String,String>() );

    Map<String, String> retMap = new Gson().fromJson(
            k, new TypeToken<HashMap<String, String>>() {}.getType()
    );

    stats.put( "mkey" , retMap.get("uuid") );

    return gson.toJson( stats );

  }






}


