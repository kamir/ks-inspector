package io.confluent.cp.cs;



import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.File;
import java.io.IOException;

/**
 *
 * This class uses the Cluster State Tools to read the DOMAIN definition file
 * as defined in CST project.
 *
 * It can create the Knowledge Graph using a Neo4J database.
 *
 */

import io.confluent.mdgraph.model.IKnowledgeGraph;
import io.confluent.mdgraph.model.KnowledgeGraphNeo4J;
import net.christophschubert.kafka.clusterstate.cli.CLITools;
import net.christophschubert.kafka.clusterstate.formats.domain.Domain;
import net.christophschubert.kafka.clusterstate.formats.domain.DomainParser;
import net.christophschubert.kafka.clusterstate.formats.env.CloudCluster;
import net.christophschubert.kafka.clusterstate.formats.env.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ClusterStateLoader {

    private static Logger logger = LoggerFactory.getLogger(ClusterStateLoader.class);

    /**
     * Reads only a particular file.
     *
     * @param kg
     * @param instanceDescriptionPath
     * @throws IOException
     */
    public static void populateKnowledgeGraphWithInstanceDescription(IKnowledgeGraph kg, String instanceDescriptionPath ) throws IOException {

        final DomainParser parser = new DomainParser();

        File contextPath = new File( instanceDescriptionPath );

        final Domain domain = parser.loadFromFile(contextPath);

        logger.info("Domain: " + domain );

        kg.registerAppInstance4Domain( domain, contextPath );

    }

    /**
     * Iterates over a base folder and reads all domains.
     * Schema folder is skipped.
     */
    public static void populateKnowledgeGraphMultiDomains(IKnowledgeGraph kg, String basePath ) throws IOException {

        File[] dirs = new File( basePath ).listFiles();

        for( File f : dirs ) {

            if ( f.isDirectory() ) {

                if( !f.getName().equals( "schemas" ) ) {

                    System.out.println( "> Read data for instances in Domain folder: " + f.getAbsolutePath() );

                    populateKnowledgeGraph( kg, f.getAbsolutePath() );

                }
            }

        }

    }


    /**
     * Reads all files for this domain.
     *
     * @param kg
     * @param domainPath
     * @throws IOException
     */
    public static void populateKnowledgeGraph( IKnowledgeGraph kg, String domainPath ) throws IOException {

        final DomainParser parser = new DomainParser();

        logger.info( "### DOMAIN-PATH : " + domainPath );

        File contextPath = new File( domainPath );

        final List<Domain> domains = Files.list(contextPath.toPath())
                .filter(CLITools::isDomainFile)
                .flatMap(path -> {
                    try {

                        logger.info( "# DOMAIN-FILE: " + path.toFile().getAbsolutePath() );

                        // System.out.println( path.toFile() );

                        return Stream.of(parser.loadFromFile(path.toFile()));

                    }
                    catch (IOException e) {
                        e.printStackTrace();
                        logger.error("Could not open or parse domain file " + path, e);
                        // we should quit application here since otherwise the topic specified in the unparseable domain
                        // file will be deleted!
                        logger.error("Stopping processing to prevent possible data loss");

                        System.exit(1);
                    }
                    return Stream.empty(); // to keep compiler happy
                }).collect(Collectors.toList());


        logger.info("Domains: " + domains);

        for (int i = 0; i < domains.size(); i++) {

            kg.registerDomain( domains.get(i),i+1 , contextPath );

            kg.registerAppInstance4Domain( domains.get(i), contextPath );

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

    public static void populateKnowledgeGraphWithEnvironmentDescription(IKnowledgeGraph g, String environmentPath) throws Exception {

        ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());

        File f = new File(environmentPath);

        Environment env = (Environment)yamlMapper.readValue( f, Environment.class);
        logger.info("Environments: " + env);

        for ( CloudCluster c : env.clusters ) {

            logger.info("CloudClusters: " + c);

            g.registerCloudCluster( c, f );

        }

    }
}
