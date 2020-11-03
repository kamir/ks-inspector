package io.confluent.cp.util;

import com.google.gson.Gson;

import org.apache.log4j.Logger;

public class MDLogStore {

    private static final Logger logger = Logger.getLogger( MDLogStore.class );

    Gson gson = new Gson();

    public static MDLogStore getSimpleStore() {
        return new MDLogStore();
    }

    public void forward(String topicName, AppContextLogRecord r) {
        logger.debug( gson.toJson( r ) );
    }

}
