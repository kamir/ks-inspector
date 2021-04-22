package io.confluent.cp.governanceprocess;

import net.christophschubert.kafka.clusterstate.cli.EnvVarTools;
import net.christophschubert.kafka.clusterstate.cli.PropertyFileBuilder;
import net.christophschubert.kafka.clusterstate.cli.PropertyMergeTool;
import org.bouncycastle.math.ec.ScaleYNegateXPointMap;

/**
 * This program uses the "PropertyFileBuilder" provided in the KST project and creates the
 * application client properties file in a folder named "cpf" inside the folder in which
 * the "Environment Definition file" is provided.
 *
 * An Environment contains multiple clusters, hence, a particular cluster name has to be provided in
 * order to merge the API-KEY / API-SECRET into the configfile template.
 *
 * We assume that the security sensitive API-KEY / API-SECRET are provided as ENVIRONMENT VARIABLE to the
 * process which wants to instantiate a cluster client.
 *
 * The naming convention is as follows:
 *
 */
public class PropertyFileExporter {

    public static void main(String[] ARGS) {

        String targetClusterName = EnvVarTools.readPropertyFromEnv( "KST" , "TARGET_CLUSTER_NAME" );
        String envFilePath = EnvVarTools.readPropertyFromEnv( "KST" , "ENVIRONMENT_FILENAME" );

        System.out.println( "> environmment file : " + envFilePath );
        System.out.println( "> work on cluster   : " + targetClusterName );

        if ( envFilePath != null ) {

            String[] args1 = {"build", envFilePath, targetClusterName};
            PropertyFileBuilder.main(args1);

        }
        else {
            String[] args1 = {"build", "./src/main/cluster-state-tools-data/example10/environments.yaml", targetClusterName};
            PropertyFileBuilder.main(args1);

        }
        /**
         * Next step:
         * ----------
         * Iterate over all clusters in the environment and export the client props in one step,
         * by iteration over the CloudClusters loaded from the file.
         */

    }

}
