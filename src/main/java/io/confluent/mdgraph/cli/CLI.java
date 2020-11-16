package io.confluent.mdgraph.cli;


import io.confluent.cp.cs.ClusterStateLoader;
import io.confluent.mdgraph.model.IKnowledgeGraph;
import io.confluent.mdgraph.model.KnowledgeGraphFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;
import java.io.IOException;

import java.util.concurrent.ExecutionException;



@Command(name= "ksi", subcommands = { CommandLine.HelpCommand.class }, version = "ksi 0.1.0",
        description = "Inspect kafka based streaming processes via flow- and knowledge-graphs.")
class CLI {
    private Logger logger = LoggerFactory.getLogger(CLI.class);

    @Command(name = "inspect", description = "Inspect domain description from cluster context.")
    int inspect(
            @Option(names = { "-c", "--command-properties"}, paramLabel = "<command properties>",
                    description = "command config file") File configFile,
            @CommandLine.Parameters(paramLabel = "context", description = "path to the context", defaultValue = ".") File contextPath
    ) throws IOException, ExecutionException, InterruptedException {
        if (!contextPath.isDirectory()) {
            logger.error("Given context {} is not a folder", contextPath);
            return 1;
        }
        if (configFile == null) {
            configFile = new File(contextPath, "ksi.properties");
        }

        System.out.println( "Start Knowledge Graph creation ... " );

        IKnowledgeGraph g1 = KnowledgeGraphFactory.getKafkaBasedKnowledgegraph("DataGovernanceDemo001");

        // String instancesPath = "/Users/mkampf/Engagements/AO-Cloud-Project/week6/instances";
        String instancesPath = contextPath.getPath();

        ClusterStateLoader.populateKnowledgeGraphMultiDomains( g1, instancesPath );

        return 0;
    }

    public static void main(String[] args) {
        final int status = new CommandLine(new CLI()).execute(args);
        System.exit(status);
    }
}
