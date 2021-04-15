package io.confluent.cp.util;

import io.confluent.kafka.serializers.subject.strategy.SubjectNameStrategy;
import net.christophschubert.kafka.clusterstate.cli.CloudGovernance;
import net.christophschubert.kafka.clusterstate.cli.EnvVarTools;
import net.christophschubert.kafka.clusterstate.cli.PropertyFileBuilder;
import org.checkerframework.checker.units.qual.A;

/**
 * This is the programm, which will be executed by the pipeline.
 */
public class KSTWrapper {

    public static void main(String[] ARGS) {

        String targetClusterName = EnvVarTools.readPropertyFromEnv("KST", "TARGET_CLUSTER_NAME");

        String targetDomainName = EnvVarTools.readPropertyFromEnv("KST", "TARGET_DOMAIN_NAME");

        for ( String s  : ARGS )
            System.out.println( s );


        if ( ARGS == null || ARGS.length == 0 ) {

            System.out.println( "[1] > Apply all changes needed to bring the cluster: ["+targetClusterName+"] into the desired state.");
            String[] args1 = { "applyToSelectedCluster", "./src/main/cluster-state-tools-data/example0/environments.yaml", targetClusterName };
            CloudGovernance.main( args1 );

        }
        else if ( ARGS.length == 1 ) {
            System.out.println( "[2] > Apply all changes needed to bring the cluster: ["+targetClusterName+"] into the desired state.");
            System.out.println( "    > Process the environment-file: "  +   ARGS[0] );


            // How to call KST to make my changes?
            String[] args2 = { "applyToSelectedCluster",  ARGS[0] , targetClusterName };
            CloudGovernance.main( args2 );

        }
        else if ( ARGS.length == 2 ) {

            System.out.println( "[3] > Apply all changes needed to bring the cluster : ["+targetClusterName+"] into the desired state.");
            System.out.println( "    > Process the environment-file : "  +   ARGS[0] );
            System.out.println( "    > Select the domain : "  +   ARGS[1] );


            // TODO: Add function for selective execution ...
            String[] args3 = {"applyToSelectedClusterAndDomain", ARGS[0], targetClusterName, ARGS[1]};
            CloudGovernance.main(args3);

        }

    }

}
