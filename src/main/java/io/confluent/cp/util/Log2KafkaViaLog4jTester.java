package io.confluent.cp.util;

import org.apache.log4j.LogManager;

public class Log2KafkaViaLog4jTester {

    private static final org.apache.log4j.Logger logger = LogManager.getLogger(Log2KafkaViaLog4jTester.class);

    public static void main(String[] ARGS) {

        System.out.println("Start Tester DEMO ... ");
        logger.info("Hey");
        logger.warn("Ho!");

        logger.info("Done!");

        System.exit( 0 );

    }

}
