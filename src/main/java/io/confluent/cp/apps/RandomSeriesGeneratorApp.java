package io.confluent.cp.apps;

import io.confluent.cp.cs.AppDescriptorLoader;
import io.confluent.cp.mdmodel.kafka.KafkaApplicationContextHandler;
import io.confluent.ks.modern.utils.ModernEnvVarTools;
import io.confluent.ks.modern.model.Domain;
import org.apache.log4j.Appender;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.Enumeration;

/**
 * This application generates some random numbers into a Kafka topic.
 *
 * Logger properties are configured in the log4j.properties file,
 * in the resources folder.
 */

public class RandomSeriesGeneratorApp {

    private static final Logger logger = Logger.getRootLogger();

    public static void main(String[] argv) throws Exception {

        String targetDomainName = ModernEnvVarTools.readPropertyFromEnv("KST", "TARGET_DOMAIN_NAME");
        String targetClusterName = ModernEnvVarTools.readPropertyFromEnv("KST", "TARGET_CLUSTER_NAME");

        System.out.println("> Started RandomSeriesGeneratorApp in Domain ["+targetDomainName+"].");
        System.out.println("> I will work with cluster: [" + targetClusterName + "].");

        Enumeration appenders = logger.getAllAppenders();

        while( appenders.hasMoreElements() ) {
            Appender a = (Appender)appenders.nextElement();
            System.out.println( "[ ]-> " + a.getName() );
        }

        System.out.println("****************** ");
        System.out.println("* SOME TEST LOGS * ");
        System.out.println("****************** ");

        logger.debug( " This is a debug message from RandomSeriesGeneratorApp." );
        logger.info(  " This is an info message from RandomSeriesGeneratorApp." );
        logger.warn(  " This is a Warn message from RandomSeriesGeneratorApp.");
        logger.error( " This is an error message from RandomSeriesGeneratorApp." );



        String instancePath1 = "./src/main/cluster-state-tools-data/example10/domains/" + targetDomainName + ".domy";
        File f = new File( instancePath1 );
        System.out.println( f.getAbsolutePath() + " -> " + f.canRead() );

        Domain domain = AppDescriptorLoader.readAppDescriptor( instancePath1 );

        KafkaApplicationContextHandler.init( "my-host-1", domain.name );

        KafkaApplicationContextHandler.persistDomain( domain.toString() );

        KafkaApplicationContextHandler.persistRuntimeProperties( System.getenv() );

        System.out.println("****************** ");
        System.out.println("* ACTION         * ");
        System.out.println("****************** ");

        //--------------------------------------------------------------------------------------------------------------
        //  Generate some data ...
        //--------------------------------------------------------------------------------------------------------------

        LogManager.shutdown();

        System.out.println("> Stopped RandomSeriesGeneratorApp.");

        System.exit(0);

    }
}
