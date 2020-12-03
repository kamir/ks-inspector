package io.confluent.cp.util;

import net.christophschubert.kafka.clusterstate.cli.CloudGovernance;
import net.christophschubert.kafka.clusterstate.cli.EnvVarTools;
import net.christophschubert.kafka.clusterstate.cli.PropertyFileBuilder;

/**
 * This is the programm, which will be executed by the pipeline.
 */
public class KSTWrapper {

    public static void main(String[] ARGS) {

        String targetClusterName = EnvVarTools.readPropertyFromEnv("KST", "TARGET_CLUSTER_NAME");
        System.out.println( "> Apply all changes needed to bring the cluster: ["+targetClusterName+"] into the desired state.");

        // How to call KST to make my changes?

        String[] args1 = { "applyToSelectedCluster", "./src/main/cluster-state-tools-data/example2/ccloud-environments.yaml", targetClusterName };


        CloudGovernance.main( args1 );


    }

}
