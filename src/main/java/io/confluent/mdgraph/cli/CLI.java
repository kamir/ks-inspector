package io.confluent.mdgraph.cli;

import io.confluent.cp.cs.ClusterStateLoader;
import io.confluent.cp.factflow.FactQueryProducer;
import io.confluent.kafka.schemaregistry.client.rest.exceptions.RestClientException;
import io.confluent.mdgraph.model.IKnowledgeGraph;
import io.confluent.mdgraph.model.KnowledgeGraphFactory;

import io.confluent.mdgraph.model.KnowledgeGraphNeo4J;
import net.christophschubert.kafka.clusterstate.ClientBundle;
import net.christophschubert.kafka.clusterstate.cli.CLITools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;
import java.io.IOException;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

@Command(name= "ksi", subcommands = { CommandLine.HelpCommand.class }, version = "ksi 0.1.0",
        description = "Inspect kafka based streaming processes via flow- and knowledge-graphs.")
class CLI {

    private Logger logger = LoggerFactory.getLogger(CLI.class);

    // *** profile p20 ***
    //
    // We read the facts into a Neo4J database. Kafka topic is not involved.
    // This is part of the analysis procedure we develop.
    // Temporary data is not stored in the MD topic inside the Kafka cluster.
    // The MD topic stores only the real facts related to the real cluster.
    //
    @Command(name = "inspectCSV", description = "Inspect domain description from cluster context.")
    int inspect(
            @Option(names = { "-wp", "--working-path" }, paramLabel = "<working-path>",
                    description = "working path for reading the input file") String workingPath,
            @Option(names = { "-fn", "--file-name" }, paramLabel = "<file-name>",
                    description = "filename for the input") String fileName,
            @Option(names = { "-bss", "--bootstrap-server" }, paramLabel = "<bootstrap-server>",
                    description = "bootstrap server of the cluster to connect too") String bootstrapServer,
            @Option(names = { "-c", "--command-properties"}, paramLabel = "<command properties>",
                    description = "command config file") File configFile,
            @Option(names = { "-e", "--env-var-prefix"}, paramLabel = "<prefix>",
                    description = "prefix for env vars to be added to properties ") String envVarPrefix,
            @CommandLine.Parameters(paramLabel = "context", description = "path to the context", defaultValue = ".") File contextPath
    ) throws IOException, ExecutionException, InterruptedException {
        if (!contextPath.isDirectory()) {
            logger.error("Given context {} is not a folder", contextPath);
            return 1;
        }
        if (configFile == null) {
            configFile = new File(contextPath, "kst.properties");
        }

        System.out.println("> Start inspection of a data flow file. {"+workingPath + "/" + fileName + "}");
        System.out.println("> Use processing context path : " + contextPath.getAbsolutePath() );

        final Properties properties = CLITools.loadProperties(configFile, bootstrapServer, envVarPrefix);
        final ClientBundle bundle = ClientBundle.fromProperties(properties);

        // TODO: expose the details of the bundle
        // bundle.describe();

        System.out.println( ">>> Use config file: [" + configFile.getAbsolutePath() + "] for Knowledge Graph creation. " );

        IKnowledgeGraph g1 = KnowledgeGraphFactory.getNeo4JBasedKnowledgeGraph("DataGovernanceDemo001", properties);

        // Here we work with the default CSV Processor which projects the special CSV file into a graph.
        //    ClusterStateLoader.populateKnowledgeGraphFromCSVFlows( g1, workingPath + "/" + fileName );

        // Here we still work with the default CSV Processor which projects the special CSV file into a graph.
        String defaultMapper = "io.confluent.cp.mdmodel.kafka.CSVFileFlowGraphProcessor";
        ClusterStateLoader.populateKnowledgeGraphFromCSVFlows( g1, workingPath + "/" + fileName, defaultMapper );

        System.out.println( ">>> Finished collecting the facts for our Streaming Processes Knowledge Graph in a topic." );
        g1.describe();

        return 0;

    }

    // *** profile p21 ***
    //
    // We read the facts into a Neo4J database. Kafka topic is not involved.
    // This is part of the analysis procedure we develop.
    // Temporary data is not stored in the MD topic inside the Kafka cluster.
    // The MD topic stores only the real facts related to the real cluster.
    //
    @Command(name = "inspectCSV_EXT", description = "Inspect domain description from cluster context.")
    int inspectExt(
            @Option(names = { "-wp", "--working-path" }, paramLabel = "<working-path>",
                    description = "working path for reading the input file") String workingPath,
            @Option(names = { "-fn", "--file-name" }, paramLabel = "<file-name>",
                    description = "filename for the input") String fileName,
            @Option(names = { "-bss", "--bootstrap-server" }, paramLabel = "<bootstrap-server>",
                    description = "bootstrap server of the cluster to connect too") String bootstrapServer,
            @Option(names = { "-c", "--command-properties"}, paramLabel = "<command properties>",
                    description = "command config file") File configFile,
            @Option(names = { "-e", "--env-var-prefix"}, paramLabel = "<prefix>",
                    description = "prefix for env vars to be added to properties ") String envVarPrefix,
            @CommandLine.Parameters(paramLabel = "context", description = "path to the context", defaultValue = ".") File contextPath
    ) throws IOException, ExecutionException, InterruptedException {
        if (!contextPath.isDirectory()) {
            logger.error("Given context {} is not a folder", contextPath);
            return 1;
        }
        if (configFile == null) {
            configFile = new File(contextPath, "kst.properties");
        }

        System.out.println("> Start inspection of a data flow file. {"+workingPath + "/" + fileName + "}");
        System.out.println("> Use processing context path : " + contextPath.getAbsolutePath() );

        final Properties properties = CLITools.loadProperties(configFile, bootstrapServer, envVarPrefix);
        final ClientBundle bundle = ClientBundle.fromProperties(properties);

        // TODO: expose the details of the bundle
        // bundle.describe();

        System.out.println( ">>> Use config file: [" + configFile.getAbsolutePath() + "] for Knowledge Graph creation. " );

        IKnowledgeGraph g1 = KnowledgeGraphFactory.getNeo4JBasedKnowledgeGraph("DataGovernanceDemo001", properties);

        // Here we work with the default CSV Processor which projects the special CSV file into a graph.
        //    ClusterStateLoader.populateKnowledgeGraphFromCSVFlows( g1, workingPath + "/" + fileName );

        // Here we still work with the default CSV Processor which projects the special CSV file into a graph.
        String customMapper1 = "io.confluent.cp.mdmodel.kafka.CSVFileFlowGraphProcessorExternalServices";
        ClusterStateLoader.populateKnowledgeGraphFromCSVFlows( g1, workingPath + "/" + fileName, customMapper1 );

        // Here we still work with the default CSV Processor which projects the special CSV file into a graph.
        String customMapper2 = "io.confluent.cp.mdmodel.kafka.CSVFileFlowGraphProcessorExternalTopics";
        ClusterStateLoader.populateKnowledgeGraphFromCSVFlows( g1, workingPath + "/" + fileName, customMapper2 );

        System.out.println( ">>> Finished collecting the facts for our Streaming Processes Knowledge Graph in a topic." );
        g1.describe();

        return 0;

    }

    // profile p10-1
    @Command(name = "inspectDomain", description = "Inspect domain description from cluster context.")
    int inspect(
            @Option(names = { "-bss", "--bootstrap-server" }, paramLabel = "<bootstrap-server>",
                    description = "bootstrap server of the cluster to connect too") String bootstrapServer,
            @Option(names = { "-wp", "--working-path" }, paramLabel = "<working-path>",
                    description = "working path for reading the input file") String workingPath,
            @Option(names = { "-c", "--command-properties-configuration-file"}, paramLabel = "<command properties cfg>",
                    description = "command config file") File configFile,
            @Option(names = { "-e", "--env-var-prefix"}, paramLabel = "<prefix>",
                    description = "prefix for env vars to be added to properties ") String envVarPrefix,
            @CommandLine.Parameters(paramLabel = "context", description = "path to the context", defaultValue = ".") File contextPath
    ) throws IOException, ExecutionException, InterruptedException {
        if (!contextPath.isDirectory()) {
            logger.error("Given context {} is not a folder", contextPath);
            return 1;
        }

        if (configFile == null) {
            configFile = new File(contextPath, "kst.properties");
        }

        final Properties properties = CLITools.loadProperties(configFile, bootstrapServer, envVarPrefix);
        final ClientBundle bundle = ClientBundle.fromProperties(properties);

        // TODO: expose the details of the bundle
        // bundle.describe();

        System.out.println( ">>> Use config file: [" + configFile.getAbsolutePath() + "] (canRead::"+configFile.canRead()+") for Knowledge Graph creation. " );
        System.out.println("> Use processing context path : " + contextPath.getAbsolutePath() );

        String instancesPath = contextPath.getPath();

        System.out.println( ">>> Start reading facts from instancesPath [" +contextPath.getAbsolutePath() + "]." );

        IKnowledgeGraph g1 = KnowledgeGraphFactory.getKafkaBasedKnowledgegraph("DataGovernanceDemo001", properties);

        System.out.println( "[KG SETUP]");
        boolean status = g1.describe();
        System.out.println( "Knowledge graph is ready: " + status );

        ClusterStateLoader.populateKnowledgeGraphMultiDomains( g1, instancesPath );

        System.out.println( ">>> Finished collecting the facts for our Streaming Processes Knowledge Graph in a topic." );
        System.out.println( "> " + FactQueryProducer.sentCounter + " fact creation queries sent." );
        return 0;

    }

    // profile p10-2
    @Command(name = "inspectSchemaRegistry", description = "Inspect and load SchemaRegistry information.")
    int inspectSchemaRegistry(
            @Option(names = { "-bss", "--bootstrap-server" }, paramLabel = "<bootstrap-server>",
                    description = "bootstrap server of the cluster to connect too") String bootstrapServer,
            @Option(names = { "-wp", "--working-path" }, paramLabel = "<working-path>",
                    description = "working path for reading the input file") String workingPath,
            @Option(names = { "-c", "--command-properties"}, paramLabel = "<command properties>",
                    description = "command config file") File configFile,
            @Option(names = { "-e", "--env-var-prefix"}, paramLabel = "<prefix>",
                    description = "prefix for env vars to be added to properties ") String envVarPrefix,
            @CommandLine.Parameters(paramLabel = "context", description = "path to the context", defaultValue = ".") File contextPath
    ) throws IOException, ExecutionException, InterruptedException {
        if (!contextPath.isDirectory()) {
            logger.error("Given context {} is not a folder", contextPath);
            return 1;
        }
        if (configFile == null) {
            configFile = new File(contextPath, "kst.properties");
        }

        final Properties properties = CLITools.loadProperties(configFile, bootstrapServer, envVarPrefix);
        final ClientBundle bundle = ClientBundle.fromProperties(properties);

        // bundle.describe();

        System.out.println( ">>> Use config file: [" + configFile.getAbsolutePath() + "] for Knowledge Graph creation. " );
        System.out.println("> Use processing context path : " + contextPath.getAbsolutePath() );

        String instancesPath = contextPath.getPath();
        System.out.println( ">>> Start reading facts from SchemaRegistry configured in property file [" +instancesPath + "]." );

        IKnowledgeGraph g1 = KnowledgeGraphFactory.getKafkaBasedKnowledgegraph("DataGovernanceDemo001", properties);

        /**
         * Manage Schema MD: the fields will be linked to the tags provided by the catalog.
         */
        io.confluent.cp.factcollector.SchemaRegistryClient src = new io.confluent.cp.factcollector.SchemaRegistryClient();
        try {
            src.populateGraph( g1 );
        }
        catch (RestClientException e) {
            e.printStackTrace();
        }

        System.out.println( ">>> Finished collecting the facts from SchemaRegistry for our Streaming Processes Knowledge Graph in a topic." );
        System.out.println( "> " + FactQueryProducer.sentCounter + " fact creation queries sent." );

        return 0;

    }

    // profile p11
    @Command(name = "export2Neo4J", description = "Export the knowledge graph to a Neo4J service.")
    int exportGraphToNeo4JService(
            @Option(names = { "-bss", "--bootstrap-server" }, paramLabel = "<bootstrap-server>",
                    description = "bootstrap server of the cluster to connect too") String bootstrapServer,
            @Option(names = { "-c", "--command-properties"}, paramLabel = "<command properties>",
                    description = "command config file") File configFile,
            @Option(names = { "-wp", "--working-path" }, paramLabel = "<working-path>",
                    description = "working path for reading the input file") String workingPath,
            @Option(names = { "-e", "--env-var-prefix"}, paramLabel = "<prefix>",
                    description = "prefix for env vars to be added to properties ") String envVarPrefix,
            @CommandLine.Parameters(paramLabel = "context", description = "path to the context", defaultValue = ".") File contextPath
    ) throws IOException, ExecutionException, InterruptedException {
        if (!contextPath.isDirectory()) {
            logger.error("Given context {} is not a folder", contextPath);
            return 1;
        }
        if (configFile == null) {
            configFile = new File(contextPath, "kst.properties");
        }

        final Properties propertiesKAFKA = CLITools.loadProperties(configFile, bootstrapServer, envVarPrefix);
        final ClientBundle bundle = ClientBundle.fromProperties(propertiesKAFKA);

        // bundle.describe();

        Properties propsNeo4J = new Properties();
        propsNeo4J.putAll( System.getenv() );

        System.out.println( ">>> Use config file: [" + configFile.getAbsolutePath() + "](fileExists: " + configFile.exists() + ") for Knowledge Graph creation. " );
        System.out.println( ">>> Properties are overwritten by ENV_VARIABLES with prefix: " +  envVarPrefix );
        System.out.println( "> Use processing context path : " + contextPath.getAbsolutePath() );

        IKnowledgeGraph g2 = KnowledgeGraphFactory.getNeo4JBasedKnowledgeGraph(null, propsNeo4J);

        KnowledgeGraphNeo4J queriableGraph = (KnowledgeGraphNeo4J)g2;

        System.out.println( "> NOTE (1) : We clear the graph in our temporary graph db.");
        queriableGraph.deleteAllFacts();

        String cg = "DataGovernanceDemo001_" + System.currentTimeMillis();
        System.out.println( "> NOTE (2) : We read the full topic content because we use a new CG each time we run the tool. (CG: " + cg + ")" );
        queriableGraph.readGraphFromTopic( cg, propertiesKAFKA );

        System.out.println( ">>> Finished creation of the knowledge graph based on facts from our MD Topic in Kafka." );
        queriableGraph.describe();

        return 0;

    }

    // profile p12
    @Command(name = "queryGraph", description = "Query the knowledge graph in a Neo4J service with a predefined analysis query.")
    int queryGraph(
            @Option(names = { "-wp", "--working-path" }, paramLabel = "<working-path>",
                    description = "working path for reading the input file") String workingPath,
            @Option(names = { "-fno", "--file-name-out" }, paramLabel = "<file-name-out>",
                    description = "filename for the output") String fileNameOut,
            @Option(names = { "-bss", "--bootstrap-server" }, paramLabel = "<bootstrap-server>",
                    description = "bootstrap server of the cluster to connect too") String bootstrapServer,
            @Option(names = { "-qfp", "--query-file-path"}, paramLabel = "<query-file-path>",
                    description = "path of a text file with a cypher query ") String qfp,
            @Option(names = { "-e", "--env-var-prefix"}, paramLabel = "<prefix>",
                    description = "prefix for env vars to be added to properties ") String envVarPrefix,
            @CommandLine.Parameters(paramLabel = "queryFilePath", description = "path to a cypher queryfile", defaultValue = "./src/main/cypher/cmd/q4.cypher") File queryFilePath

    ) throws IOException, ExecutionException, InterruptedException {

        Properties p = new Properties();
        p.putAll( System.getenv() );

        if ( qfp != null ) {
            File queryFilePathTEMP = new File( qfp );
            if( queryFilePathTEMP.canRead() ) {
                queryFilePath = queryFilePathTEMP;
                System.out.println(  "> QFP: " + queryFilePathTEMP.getAbsolutePath() );
            }
        }

        File resultFilePath = new File( queryFilePath + ".gqpr.txt" );

        IKnowledgeGraph g2 = KnowledgeGraphFactory.getNeo4JBasedKnowledgeGraph(null,p);

        KnowledgeGraphNeo4J queriableGraph = (KnowledgeGraphNeo4J)g2;
        queriableGraph.queryFromFile( queryFilePath, resultFilePath );

        System.out.println( ">>> Finished analysis work." );
        queriableGraph.describe();


        return 0;

    }


    // profile p100
    @Command(name = "clearGraph", description = "Clear the knowledge graph in Neo4J service.")
    int clearGraph(
            @Option(names = { "-bss", "--bootstrap-server" }, paramLabel = "<bootstrap-server>",
                    description = "bootstrap server of the cluster to connect too") String bootstrapServer,
            @Option(names = { "-c", "--command-properties"}, paramLabel = "<command properties>",
                    description = "command config file") File configFile,
            @Option(names = { "-wp", "--working-path" }, paramLabel = "<working-path>",
                    description = "working path for reading the input file") String workingPath,
            @Option(names = { "-e", "--env-var-prefix"}, paramLabel = "<prefix>",
                    description = "prefix for env vars to be added to properties ") String envVarPrefix,
            @CommandLine.Parameters(paramLabel = "resultFilePath", description = "path to a status result file", defaultValue = "./cleanup-status.log") File cleanUpLogFilePath
    ) throws IOException, ExecutionException, InterruptedException {

        Properties p = new Properties();
        p.putAll( System.getenv() );

        IKnowledgeGraph g2 = KnowledgeGraphFactory.getNeo4JBasedKnowledgeGraph(null,p);

        KnowledgeGraphNeo4J queriableGraph = (KnowledgeGraphNeo4J)g2;
        queriableGraph.deleteAllFacts();

        System.out.println( ">>> Finished housekeeping work." );

        queriableGraph.describe();

        return 0;

    }


    public static void main(String[] args) {
        final int status = new CommandLine(new CLI()).execute(args);
        System.exit(status);
    }
}
