package io.confluent.cp.util;

import java.util.Hashtable;
import java.util.Properties;

public class AppContextLogRecord {

    public AppContextLogRecord(String _host, String _dom) {
        hostname = _host;
        CP_DOMAIN_NAME = _dom;
        ts = System.currentTimeMillis();
    }

    public void setProperties(Properties props) {
        for( String k: props.stringPropertyNames() ) {
            APP_CONFIG_PROPS.put( k, (String)props.get( k ) );
        }
    };

    public long ts;
    public String hostname = null;
    public String CP_DOMAIN_NAME = null;

    public String DOMAIN_YAML = null;
    public String TOPOLOGY_YAML = null;

    public Hashtable<String,String> APP_CONFIG_PROPS = new Hashtable<String,String>();

}
