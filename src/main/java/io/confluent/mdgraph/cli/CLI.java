package io.confluent.mdgraph.cli;


import io.confluent.cp.cs.ClusterStateLoader;
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

    @Command(name = "inspect", description = "Inspect domain description from cluster context.")
    int inspect(
            @Option(names = { "-b", "--bootstrap-server" }, paramLabel = "<bootstrap-server>",
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

        final Properties properties = CLITools.loadProperties(configFile, bootstrapServer, envVarPrefix);
        final ClientBundle bundle = ClientBundle.fromProperties(properties);

        // bundle.describe();

        System.out.println( ">>> Use config file: [" + configFile.getAbsolutePath() + "] for Knowledge Graph creation. " );

        String instancesPath = contextPath.getPath();
        System.out.println( ">>> Start reading facts from instancesPath [" +instancesPath + "]." );

        IKnowledgeGraph g1 = KnowledgeGraphFactory.getKafkaBasedKnowledgegraph("DataGovernanceDemo001", properties);

        ClusterStateLoader.populateKnowledgeGraphMultiDomains( g1, instancesPath );

        System.out.println( ">>> Finished collecting the facts for our Streaming Processes Knowledge Graph in a topic." );

        return 0;

    }

    @Command(name = "inspectSchemaRegistry", description = "Inspect and load SchemaRegistry information.")
    int inspectSchemaRegistry(
            @Option(names = { "-b", "--bootstrap-server" }, paramLabel = "<bootstrap-server>",
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

        final Properties properties = CLITools.loadProperties(configFile, bootstrapServer, envVarPrefix);
        final ClientBundle bundle = ClientBundle.fromProperties(properties);

        // bundle.describe();

        System.out.println( ">>> Use config file: [" + configFile.getAbsolutePath() + "] for Knowledge Graph creation. " );

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

        return 0;

    }

    @Command(name = "export2Neo4J", description = "Export the knowledge graph to a Neo4J service.")
    int exportGraphToNeo4JService(
            @Option(names = { "-b", "--bootstrap-server" }, paramLabel = "<bootstrap-server>",
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

        final Properties propertiesKAFKA = CLITools.loadProperties(configFile, bootstrapServer, envVarPrefix);
        final ClientBundle bundle = ClientBundle.fromProperties(propertiesKAFKA);

        // bundle.describe();

        Properties propsNeo4J = new Properties();
        propsNeo4J.putAll( System.getenv() );

        System.out.println( ">>> Use config file: [" + configFile.getAbsolutePath() + "](fileExists: " + configFile.exists() + ") for Knowledge Graph creation. " );
        System.out.println( ">>> Properties are overwritten by ENV_VARIABLES with prefix: " +  envVarPrefix );

        IKnowledgeGraph g2 = KnowledgeGraphFactory.getNeo4JBasedKnowledgeGraph(propsNeo4J);

        KnowledgeGraphNeo4J queriableGraph = (KnowledgeGraphNeo4J)g2;
        queriableGraph.readGraphFromTopic( "DataGovernanceDemo001_" + System.currentTimeMillis(), propertiesKAFKA );

        System.out.println( ">>> Finished exporting the facts for our Streaming Processes Knowledge Graph to Neo4J." );

        return 0;

    }

    @Command(name = "clearGraph", description = "Clear the knowledge graph in Neo4J service.")
    int clearGraph(
            @Option(names = { "-e", "--env-var-prefix"}, paramLabel = "<prefix>",
                    description = "prefix for env vars to be added to properties ") String envVarPrefix
    ) throws IOException, ExecutionException, InterruptedException {


        Properties p = new Properties();
        p.putAll( System.getenv() );

        IKnowledgeGraph g2 = KnowledgeGraphFactory.getNeo4JBasedKnowledgeGraph(p);

        KnowledgeGraphNeo4J queriableGraph = (KnowledgeGraphNeo4J)g2;
        queriableGraph.deleteAllFacts();

        System.out.println( ">>> Finished housekeeping work." );

        return 0;

    }

    @Command(name = "queryGraph", description = "Query the knowledge graph in a Neo4J service with a predefined analysis query.")
    int queryGraph(
            @Option(names = { "-e", "--env-var-prefix"}, paramLabel = "<prefix>",
                    description = "prefix for env vars to be added to properties ") String envVarPrefix,
            @CommandLine.Parameters(paramLabel = "queryFilePath", description = "path to a cypher queryfile", defaultValue = "./src/main/cypher/q1.cypher") File queryFilePath

    ) throws IOException, ExecutionException, InterruptedException {

        Properties p = new Properties();
        p.putAll( System.getenv() );

        IKnowledgeGraph g2 = KnowledgeGraphFactory.getNeo4JBasedKnowledgeGraph(p);

        KnowledgeGraphNeo4J queriableGraph = (KnowledgeGraphNeo4J)g2;
        queriableGraph.queryFromFile( queryFilePath );

        System.out.println( ">>> Finished analysis work." );

        return 0;

    }

    public static void main(String[] args) {
        final int status = new CommandLine(new CLI()).execute(args);
        System.exit(status);
    }
}
