package io.confluent.cp.mdmodel.ksql;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import io.confluent.cp.factcollector.ksql.KSQLRestClient;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

public class KSQLServerInstance {

    public static boolean HIDE_INTERNAL_TOPICS = true;

    KSQLRestClient restClient = new KSQLRestClient();

    public String[] getListOfStreams() {

        return getListOfObjects( "STREAMS", "streams", "name" );

    }

    public String[] getListOfTables() {

        return getListOfObjects( "TABLES", "tables", "name" );

    }


    public String[] getListOfTopics() {

        return getListOfObjects( "TOPICS", "topics", "name" );

    }

    public String[] getListOfObjects(String object, String field, String attribute) {

        Vector<String> tempT = new Vector<>();

        try {

            Properties props1 = new Properties();

            String data = restClient.callRestAPI( "LIST " + object.toUpperCase() + ";", props1 );

            data = data.substring(1, data.length()-1);

            Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
            Map<String, Object> jsonJavaRootObject = new Gson().fromJson( data , mapType);

            //System.out.println( jsonJavaRootObject );

            Type listType = new TypeToken<ArrayList<LinkedTreeMap<String, Object>>>(){}.getType();
            ArrayList<LinkedTreeMap<String, Object>> things = new Gson().fromJson(new Gson().toJson(jsonJavaRootObject.get(field)), listType);

            //System.out.println( things );

            for(Object o : things) {
                if (o instanceof LinkedTreeMap) {
                    @SuppressWarnings("unchecked")
                    LinkedTreeMap<String, Object> ltm = (LinkedTreeMap<String, Object>)o;
                    //System.out.println( ltm );

                    Object attrValue = ltm.get(attribute);
                    if (attrValue instanceof String) {
                        String value = (String)attrValue;

                        // System.out.println( "> " + value );

                        if (field.equals("topics") && HIDE_INTERNAL_TOPICS && !value.startsWith("_")) {
                            tempT.add(value);
                        }
                    }
                }
            }

        }
        catch(Exception ex) {

            ex.printStackTrace();

        }

        return tempT.toArray( new String[ tempT.size() ] );

    };


    public String[] getListOfQueries() {

        Vector<String> tempT = new Vector<>();

        try {

            Properties props1 = new Properties();

            String data = restClient.callRestAPI( "LIST QUERIES;", props1 );

            data = data.substring(1, data.length()-1);

            Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
            Map<String, Object> jsonJavaRootObject = new Gson().fromJson( data , mapType);

            // System.out.println( jsonJavaRootObject );

            Type listType = new TypeToken<ArrayList<LinkedTreeMap<String, Object>>>(){}.getType();
            ArrayList<LinkedTreeMap<String, Object>> things = new Gson().fromJson(new Gson().toJson(jsonJavaRootObject.get("queries")), listType);

            //System.out.println( things );

            for(Object o : things) {
                if (o instanceof LinkedTreeMap) {
                    @SuppressWarnings("unchecked")
                    LinkedTreeMap<String, Object> ltm = (LinkedTreeMap<String, Object>)o;
                    // System.out.println( ltm );

                    Object queryValue = ltm.get("queryString");
                    if (queryValue instanceof String) {
                        tempT.add((String)queryValue);
                    }
                }
            }

        }
        catch(Exception ex) {

            ex.printStackTrace();

        }

        return tempT.toArray( new String[ tempT.size() ] );

    }

    public void setHost(String host) {
        this.restClient.setHost( host );
    }

    public void setHostAndPort(String host, String port) {
        this.restClient.setHostAndPort( host, port );
    }

}