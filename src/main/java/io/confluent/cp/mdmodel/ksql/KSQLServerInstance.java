package io.confluent.cp.mdmodel.ksql;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import io.confluent.cp.factcollector.ksql.KSQLRestClient;

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

            Map jsonJavaRootObject = new Gson().fromJson( data , Map.class);

            //System.out.println( jsonJavaRootObject );

            ArrayList<LinkedTreeMap> things = (ArrayList) jsonJavaRootObject.get( field );

            //System.out.println( things );

            for(Object o : things) {

                LinkedTreeMap ltm = (LinkedTreeMap)o;
                //System.out.println( ltm );

                String value = (String)ltm.get( attribute );

                // System.out.println( "> " + value );

                if ( field.equals("topics") && HIDE_INTERNAL_TOPICS && !value.startsWith("_") ) {
                    tempT.add(value);
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

            Map jsonJavaRootObject = new Gson().fromJson( data , Map.class);

            // System.out.println( jsonJavaRootObject );

            ArrayList<LinkedTreeMap> things = (ArrayList) jsonJavaRootObject.get( "queries" );

            //System.out.println( things );

            for(Object o : things) {

                LinkedTreeMap ltm = (LinkedTreeMap)o;
                // System.out.println( ltm );

                tempT.add( (String)ltm.get( "queryString" ) );

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
