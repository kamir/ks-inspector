package io.confluent.cp.apps;

import io.confluent.cp.cs.AppDescriptorLoader;
import io.confluent.cp.mdmodel.kstreams.KafkaStreamsApplicationContextHandler;
import net.christophschubert.kafka.clusterstate.formats.domain.Domain;
import org.apache.log4j.Appender;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.Enumeration;

/**
 * This application uses a KafkaCluster for logging purposes.
 *
 * Logger properties are configured in the log4j.properties file,
 * in the resources folder.
 */

public class SimpleKafkaLoggerApp {

    private static final Logger logger = Logger.getRootLogger();

    public static void main(String[] argv) throws Exception {

        System.out.println("> Started SimpleKafkaLoggerApp.");

        Enumeration appenders = logger.getAllAppenders();

        while( appenders.hasMoreElements() ) {
            Appender a = (Appender)appenders.nextElement();
            System.out.println( "[ ]-> " + a.getName() );
        }

        System.out.println("> ... ");

        logger.debug( "This is a debug message from SimpleKafkaLoggerApp." );
        logger.info(  "This is an info message from SimpleKafkaLoggerApp." );
        logger.warn(  "This is a Warn message from SimpleKafkaLoggerApp.");
        logger.error( "This is an error message from SimpleKafkaLoggerApp." );

        String instancePath1 = "./src/main/cluster-state-tools-data/contexts/order-processing/instances/order-processing-c.domy";
        Domain domain = AppDescriptorLoader.readAppDescriptor( instancePath1 );

        KafkaStreamsApplicationContextHandler.init( "my-host", domain.name );

        KafkaStreamsApplicationContextHandler.persistDomain( domain.toString() );

        KafkaStreamsApplicationContextHandler.persistDomain( "FAkE_TOPOLOGY" );

        KafkaStreamsApplicationContextHandler.persistRuntimeProperties( System.getenv() );

        LogManager.shutdown();

        System.out.println("> Stopped SimpleKafkaLoggerApp.");

        System.exit(0);

    }
}
