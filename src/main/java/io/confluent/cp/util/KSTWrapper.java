package io.confluent.cp.util;


import io.confluent.ks.modern.utils.ModernEnvVarTools;
// Note: CloudGovernance functionality moved to modern implementations

import java.io.File;


/**
 * This is the programm, which will be executed by the pipeline.
 */
public class KSTWrapper {

    public static void main(String[] ARGS) {

        String targetClusterName = ModernEnvVarTools.readPropertyFromEnv("KST", "TARGET_CLUSTER_NAME");

        String targetDomainName = ModernEnvVarTools.readPropertyFromEnv("KST", "TARGET_DOMAIN_NAME");

        File f = new File( ARGS[0] );
        File folder = f.getParentFile();
        String propsFileName4KST = folder + "/cpf/cp-client-props-" + targetClusterName + ".properties";

        for ( String s  : ARGS )
            System.out.println( s );

        System.out.println( "[*] > Apply all changes needed to bring the cluster : ["+ targetClusterName +"] into the desired state.");
        System.out.println( "    > Process the environment-file                  : "  +   ARGS[0] );
        System.out.println( "    > Select the domain                             : "  +   targetDomainName );

        String[] args3 = {"applyToSelectedClusterAndDomain", ARGS[0], targetClusterName, targetDomainName};

        // TODO: Implement CloudGovernance replacement
        // CloudGovernance.main(args3);
        System.out.println("TODO: Implement cloud governance functionality");

    }

}
