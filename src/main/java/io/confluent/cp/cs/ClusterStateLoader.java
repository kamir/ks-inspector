package io.confluent.cp.cs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.File;
import java.io.IOException;

/**
 * The ClusterState Loader reads the Kafka Cluster state from a DOMAIN definition.
 * The data is used to populate a KnowledgeGraph.
 *
 *   This class uses the Cluster State Tools (by Cristoph Schubert)
 *   to read the DOMAIN definition file as defined in CST project.
 *
 *   Typically, we create the Knowledge Graph using a Neo4J database, and Kafka topics
 *   to keep the content creation statements.
 */

import io.confluent.cp.mdmodel.GraphMapper;
import io.confluent.cp.mdmodel.kafka.DefaultCSVFileFlowGraphProcessor;
import io.confluent.mdgraph.model.IKnowledgeGraph;
import io.confluent.ks.modern.utils.ModernCLITools;
import io.confluent.ks.modern.model.Domain;
import io.confluent.ks.modern.parser.DomainParser;
import io.confluent.ks.modern.model.CloudCluster;
import io.confluent.ks.modern.model.Environment;
// Environment class moved to CloudCluster.java
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
                .filter(ModernCLITools::isDomainFile)
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

    /**
     * Reads data from a CSV file using the default CSV to graph mapper.
     *
     * In this case we reuse the graph and extend it with multiple views.
     *
     * @param kg
     * @param csvPath
     * @throws IOException
     */
    public static void populateKnowledgeGraphFromCSVFlows( IKnowledgeGraph kg, String csvPath, String graphMapperClassName ) throws IOException {
        // This is now your custom GraphMapper
        try {
            System.out.println( "> load GraphMapper class: {" + graphMapperClassName + "}" );
            GraphMapper gmi = (GraphMapper) Class.forName(graphMapperClassName).getDeclaredConstructor().newInstance();
            populateKnowledgeGraphFromCSVFlows(kg, csvPath, gmi, true);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads data from a CSV file using the default CSV to graph mapper.
     * Im default modus haben legen wir einen neuen Graph an.
     *
     * @param kg
     * @param csvPath
     * @throws IOException
     */
    public static void populateKnowledgeGraphFromCSVFlows( IKnowledgeGraph kg, String csvPath ) throws IOException {
        // This is our default CSVFileProcessor
        DefaultCSVFileFlowGraphProcessor processor = new DefaultCSVFileFlowGraphProcessor();
        populateKnowledgeGraphFromCSVFlows( kg, csvPath, (GraphMapper)processor, false );
    }

    /**
     * Reads data from a CSV file using the provided CSV to graph mapper.
     *
     * @param kg
     * @param csvPath
     * @throws IOException
     */
    public static void populateKnowledgeGraphFromCSVFlows( IKnowledgeGraph kg, String csvPath, GraphMapper processor, boolean append ) throws IOException {

        System.out.println( "### Processing data in CSV file in PATH : " + csvPath );
        //logger.info( "### Processing data in CSV file in PATH : " + csvPath );

        File csvFile = new File( csvPath );

        processor.process(csvFile, kg, true);

        //logger.info( "### DONE ###" );
        System.out.println( "### DONE ###\n" );

        File fOutGraphML = new File( csvFile.getAbsoluteFile() + ".graphML" );
        kg.exportFullGraphAsGraphML( fOutGraphML );

        File fOutCSV = new File( csvFile.getAbsoluteFile() + ".out.csv" );
        kg.exportFullGraphAsCSV( fOutCSV );

    }

    public static void main(String[] args) throws IOException {

        final DomainParser parser = new DomainParser();

        File contextPath = new File( "./src/main/cluster-state-tools/contexts/order-processing");

        final List<Domain> domains = Files.list(contextPath.toPath())
                .filter(ModernCLITools::isDomainFile)
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

        Environment env = yamlMapper.readValue( f, Environment.class);
        logger.info("Environments: " + env);

        for ( CloudCluster c : env.clusters ) {

            logger.info("CloudClusters: " + c);

            g.registerCloudCluster( c, f );

        }

    }

}
