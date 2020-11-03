package io.confluent.cp.mdmodel;

import java.util.Hashtable;

public class Classifications {

    /**
     * THIS MAPPING WILL BE LOADED FROM A DATA-CATALOG DEFINITION FILE.
     */
    static Hashtable<String, String[]> tags = new Hashtable();

    public static void initTags() {

        String name1 = "SSN";
        String[] classifications1 = { "PII/Financial" };
        tags.put( name1, classifications1 );

        String name2 = "Name";
        String[] classifications2 = { "PII/Personal" };
        tags.put( name2, classifications2 );

        String name3 = "Address";
        String[] classifications3 = { "PII/Personal" };
        tags.put( name3, classifications3 );

        String name4 = "Account";
        String[] classifications4 = { "PII/Financial" };
        tags.put( name4, classifications4 );

    }


    public static String[] getTagsForField( String fieldname ) {
        return tags.get( fieldname );
    }


    public void loadDataCatalog() {
        Classifications.initTags();
    }

}
