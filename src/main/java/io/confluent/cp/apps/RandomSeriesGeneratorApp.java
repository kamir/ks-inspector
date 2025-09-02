package io.confluent.cp.apps;

import io.confluent.cp.cs.AppDescriptorLoader;
import io.confluent.cp.mdmodel.kafka.KafkaApplicationContextHandler;
import io.confluent.ks.modern.model.Domain;
import org.apache.log4j.Appender;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.Enumeration;

public class RandomSeriesGeneratorApp {

    private static final Logger logger = Logger.getRootLogger();

    public static void main(String[] argv) throws Exception {

        System.out.println("> Started RandomSeriesGeneratorApp.");

        String targetDomainName = "order-processing";
        String targetClusterName = "cp-cluster-1";

        System.out.println("> I will work with cluster: [" + targetClusterName + "].");

        @SuppressWarnings("unchecked")
        Enumeration<Appender> appenders = logger.getAllAppenders();

        while( appenders.hasMoreElements() ) {
            Appender a = appenders.nextElement();
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