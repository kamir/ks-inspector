package io.confluent.cp.util;


import net.christophschubert.kafka.clusterstate.cli.CloudGovernance;
import net.christophschubert.kafka.clusterstate.cli.EnvVarTools;

import java.io.File;


/**
 * This is the programm, which will be executed by the pipeline.
 */
public class KSTWrapper {

    public static void main(String[] ARGS) {

        String targetClusterName = EnvVarTools.readPropertyFromEnv("KST", "TARGET_CLUSTER_NAME");

        String targetDomainName = EnvVarTools.readPropertyFromEnv("KST", "TARGET_DOMAIN_NAME");

        File f = new File( ARGS[0] );
        File folder = f.getParentFile();
        String propsFileName4KST = folder + "/cpf/cp-client-props-" + targetClusterName + ".properties";

        for ( String s  : ARGS )
            System.out.println( s );

        System.out.println( "[*] > Apply all changes needed to bring the cluster : ["+ targetClusterName +"] into the desired state.");
        System.out.println( "    > Process the environment-file                  : "  +   ARGS[0] );
        System.out.println( "    > Select the domain                             : "  +   targetDomainName );

        String[] args3 = {"applyToSelectedClusterAndDomain", ARGS[0], targetClusterName, targetDomainName};

        CloudGovernance.main(args3);

    }

}
