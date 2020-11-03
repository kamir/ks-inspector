package io.confluent.cp.cs;

/**
 *
 * This class uses the Cluster State Tools to read the DOMAIN definition file
 * as defined in CST project.
 *
 * It can create the Knowledge Graph using a Neo4J database.
 *
 */

import io.confluent.mdgraph.KnowledgeGraph;
import net.christophschubert.kafka.clusterstate.cli.CLITools;
import net.christophschubert.kafka.clusterstate.formats.domain.Domain;
import net.christophschubert.kafka.clusterstate.formats.domain.DomainParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ClusterStateLoader {

    private static Logger logger = LoggerFactory.getLogger(ClusterStateLoader.class);

    public static void populateKnowledgeGraphWithInstanceDescription( KnowledgeGraph kg, String instanceDescriptionPath ) throws IOException {

        final DomainParser parser = new DomainParser();

        File contextPath = new File( instanceDescriptionPath );

        final Domain domain = parser.loadFromFile(contextPath);

        logger.info("Domain: " + domain );

        kg.registerAppInstance4Domain( domain, contextPath );

    }


    public static void populateKnowledgeGraph( KnowledgeGraph kg, String domainPath ) throws IOException {

        final DomainParser parser = new DomainParser();

        if ( domainPath == null) {
            domainPath = "./src/main/cluster-state-tools/contexts/order-processing";
        }

        File contextPath = new File( domainPath );

        final List<Domain> domains = Files.list(contextPath.toPath())
                .filter(CLITools::isDomainFile)
                .flatMap(path -> {
                    try {
                        return Stream.of(parser.loadFromFile(path.toFile()));
                    } catch (IOException e) {
                        logger.error("Could not open or parse domain file " + path, e);
                        // we should quit application here since otherwise the topic specified in the unparseable domain
                        // file will be deleted!
                        logger.error("Stopping processing to prevent possible data loss");
                        System.exit(1);
                    }
                    return Stream.empty(); // to keep compiler happy
                }).collect(Collectors.toList());

        logger.info("Domains: " + domains);

        System.out.println("Domains: " + domains);

        for (int i = 0; i < domains.size(); i++) {
            kg.registerDomain( domains.get(i),i+1 , contextPath );
        }
    }

    public static void main(String[] args) throws IOException {

        final DomainParser parser = new DomainParser();

        File contextPath = new File( "./src/main/cluster-state-tools/contexts/order-processing");

        final List<Domain> domains = Files.list(contextPath.toPath())
                .filter(CLITools::isDomainFile)
                .flatMap(path -> {
                    try {
                        return Stream.of(parser.loadFromFile(path.toFile()));
                    } catch (IOException e) {
                        logger.error("Could not open or parse domain file " + path , e);
                        // we should quit application here since otherwise the topic specified in the unparseable domain
                        // file will be deleted!
                        logger.error("Stopping processing to prevent possible data loss");

                    }
                    return Stream.empty(); // to keep compiler happy
                }).collect(Collectors.toList());

        logger.info("Domains: " + domains);

        System.out.println( "  Domains: " + domains );
        System.out.println( "# Domains: " + domains.size() );

        System.exit( 0 );
    }

}
