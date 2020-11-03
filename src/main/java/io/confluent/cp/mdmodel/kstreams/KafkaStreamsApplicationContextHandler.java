package io.confluent.cp.mdmodel.kstreams;

import io.confluent.cp.util.AppContextLogRecord;
import io.confluent.cp.util.MDLogStore;

import java.util.Map;
import java.util.Properties;

/**
 * This class collects the Topology information and the application metadata and forwards this into a topic.
 */
public class KafkaStreamsApplicationContextHandler {

    public static final String APP_CONTEXT_MD_TOPIC = "_APP_CONTEXT_MD_TOPIC_001";

    public static MDLogStore mdLogStream = null;
    public static String hostname = null;
    private static String domain = null;

    /**
     *   READ THE CLUSTER CONFIGURATION FROM ENVIRONMENT : TODO
     */
    public static void init( String hostname, String cpDomainName ) throws Exception {

        if ( mdLogStream == null )
            mdLogStream = MDLogStore.getSimpleStore();

        KafkaStreamsApplicationContextHandler.hostname = hostname;
        KafkaStreamsApplicationContextHandler.domain = cpDomainName;

    }

    public static void persistRuntimeProperties( Properties props ) {
        AppContextLogRecord r = new AppContextLogRecord(hostname, domain);
        r.setProperties( props );
        storeRcord( r );
    }

    public static void persistRuntimeProperties(Map<String, String> getenv) {
        Properties properties = new Properties();
        properties.putAll(getenv);
        persistRuntimeProperties( properties );
    }

    public static void persistTopology( String topology ) {
        AppContextLogRecord r = new AppContextLogRecord(hostname, domain);
        r.TOPOLOGY_YAML = topology;
        storeRcord( r );
    }

    public static void persistDomain( String domain ) {
        AppContextLogRecord r = new AppContextLogRecord(hostname, domain);
        r.DOMAIN_YAML = domain;
        storeRcord( r );
    }

    private static void storeRcord(AppContextLogRecord r) {

        mdLogStream.forward( APP_CONTEXT_MD_TOPIC, r );

    }


}

