package io.confluent.cp.util;

import com.google.gson.Gson;
import io.confluent.cp.mdmodel.AppContextLogRecord;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class MDLogStore {

    private static final Logger logger = Logger.getRootLogger();

    Gson gson = new Gson();

    public static MDLogStore getSimpleStore() {
        return new MDLogStore();
    }

    public void forward(String topicName, AppContextLogRecord r) {
        logger.log( Level.INFO, gson.toJson( r ) );
    }

}
