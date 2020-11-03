package io.confluent.cp.cs;

/**
 *
 * This class uses the Cluster State Tools to read the DOMAIN definition file
 * as defined in CST project.
 *
 * It can create the Knowledge Graph using a Neo4J database.
 *
 */


import net.christophschubert.kafka.clusterstate.formats.domain.Domain;
import net.christophschubert.kafka.clusterstate.formats.domain.DomainParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class AppDescriptorLoader {

    private static Logger logger = LoggerFactory.getLogger(AppDescriptorLoader.class);

    public static Domain readAppDescriptor( String instanceDescriptionPath ) throws IOException {

        final DomainParser parser = new DomainParser();

        File contextPath = new File( instanceDescriptionPath );

        final Domain domain = parser.loadFromFile(contextPath);

        logger.info("Domain: " + domain );

        return domain;

    }

}
