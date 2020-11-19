package io.confluent.cp.cfg;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 * This class provides the cluster configuration of a Confluent cloud cluster as
 * a properties file.
 *
 */

public class CCloudClusterWrapper {


    public static Properties getPropsFrom_ROOT_FOLDER(String configFile) {

        Properties props = new Properties();

        try {

            InputStream resourceStream = new FileInputStream( new File(configFile) );

            props.load(resourceStream);
            return props;

        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit( -1 );
        }

        return null;

    }

    public static Properties getPropsFrom_ROOT_FOLDER() {

        String configFile = "./src/main/resources/ccloud.props"; // could also be a constant

        return getPropsFrom_ROOT_FOLDER( configFile );

    }

    public static Properties getPropsFromJAR() {

        String resourceName = "ccloud.props"; // could also be a constant

        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Properties props = new Properties();

        try( InputStream resourceStream = loader.getResourceAsStream(resourceName) ) {
            props.load(resourceStream);
            return props;
        }
        catch (IOException e) {
            e.printStackTrace();
            System.exit( -1 );
        }

        return null;

    }


}
