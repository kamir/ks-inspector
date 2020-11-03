package io.confluent.cp.mdmodel.ksql;

import java.util.Hashtable;

public class Helper {


    public static int getCountOfStringsInString( String findStr, String statement) {

        int lastIndex = 0;
        int count = 0;
        while(lastIndex != -1){

            lastIndex = statement.indexOf(findStr,lastIndex);

            if(lastIndex != -1){
                count ++;
                lastIndex += findStr.length();
            }
        }

        return count;

    }



    public static void count(String s, Hashtable<String, Integer> stats) {

        Integer z = stats.get( s );
        int v = 0;
        if ( z == null ) {
            v = 1;
        }
        else {
            v = z.intValue() + 1;
        }
        stats.put( s, v);

    }

}
