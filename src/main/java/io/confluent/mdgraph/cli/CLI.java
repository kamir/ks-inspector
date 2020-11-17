package io.confluent.mdgraph.cli;


import io.confluent.cp.cs.ClusterStateLoader;
import io.confluent.mdgraph.model.IKnowledgeGraph;
import io.confluent.mdgraph.model.KnowledgeGraphFactory;

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
        final ClientBundle bundle = ClientBundle.fromProperties(properties, contextPath);

        // bundle.describe();

        System.out.println( ">>> Use config file: [" + configFile.getAbsolutePath() + "] for Knowledge Graph creation. " );

        String instancesPath = contextPath.getPath();
        System.out.println( ">>> Start reading facts from instancesPath [" +instancesPath + "]." );

        IKnowledgeGraph g1 = KnowledgeGraphFactory.getKafkaBasedKnowledgegraph("DataGovernanceDemo001", properties);

        ClusterStateLoader.populateKnowledgeGraphMultiDomains( g1, instancesPath );

        System.out.println( ">>> Finished reading the facts for our Streaming Processes Knowledge Graph." );

        return 0;

    }

    public static void main(String[] args) {
        final int status = new CommandLine(new CLI()).execute(args);
        System.exit(status);
    }
}
