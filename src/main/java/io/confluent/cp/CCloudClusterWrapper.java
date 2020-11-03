package io.confluent.cp;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class CCloudClusterWrapper {

    public static Properties getProps() {

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
