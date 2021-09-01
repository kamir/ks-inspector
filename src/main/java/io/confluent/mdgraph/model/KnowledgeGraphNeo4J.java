package io.confluent.mdgraph.model;

import io.confluent.cp.factflow.FactQueryConsumer;

import org.apache.log4j.LogManager;
import org.neo4j.driver.*;
import org.neo4j.driver.Result;

import java.io.*;
import java.util.*;

public class KnowledgeGraphNeo4J extends KnowledgeGraphViaKafkaTopic {

    private static final org.apache.log4j.Logger logger = LogManager.getLogger(KnowledgeGraphNeo4J.class);

    static String databaseDirectory = "graph-db";
    static String DEFAULT_DATABASE_NAME = "neo4j";
    static String uri = "bolt://localhost:7687";
    static String username = "neo4j";
    static String password = "neo4j";

    public static void init(Properties properties) {

        String databaseDirectoryTEMP = properties.getProperty( "KST_NEO4J_DATABASE_DIRECTORY" );
        String DEFAULT_DATABASE_NAME_TEMP = properties.getProperty( "KST_NEO4J_DEFAULT_DATABASE_NAME" );
        String uriTEMP = properties.getProperty( "KST_NEO4J_URI" );
        String usernameTEMP = properties.getProperty( "KST_NEO4J_USERNAME" );
        String passwordTEMP = properties.getProperty( "KST_NEO4J_PASSWORD" );

        if ( usernameTEMP != null ) username = usernameTEMP;
        if ( passwordTEMP != null ) password = passwordTEMP;

        if ( databaseDirectoryTEMP != null ) databaseDirectory = databaseDirectoryTEMP;
        if ( DEFAULT_DATABASE_NAME_TEMP != null ) DEFAULT_DATABASE_NAME = DEFAULT_DATABASE_NAME_TEMP;
        if ( uriTEMP != null ) uri = uriTEMP;


        System.out.println( "Neo4J - Props\n--------------------\n" + databaseDirectory );

        System.out.println( DEFAULT_DATABASE_NAME );
        System.out.println( uri );
        System.out.println( username );
        System.out.println( password );

    }

    public void readGraphFromTopic( String consumerGroup, Properties props ) {

        FactQueryConsumer consumer = new FactQueryConsumer( consumerGroup, props );
        consumer.processCypherQueries( this );

    }

    public void show() {

        System.out.println( ">>> Graph data ...");

        List<String> nodes = getAllNodes();

        int i = 0;
        for( String n : nodes ) {
            i++;
            System.out.println( i + " :: " +  n );

        }

    }

    private List<String> getAllNodes() {

        try ( Session session = driver.session() )
        {
            return session.readTransaction( tx -> {
                List<String> names = new ArrayList<>();
                Result result = tx.run( "MATCH (n) RETURN n.name ORDER BY n.name" );
                while ( result.hasNext() )
                {
                    names.add( result.next().get( 0 ).asString() );
                }
                return names;
            } );
        }
    }

    private static KnowledgeGraphNeo4J graph = null;

    private Driver driver;

    public static KnowledgeGraphNeo4J getGraph() {
        if ( graph == null ) {
            graph = new KnowledgeGraphNeo4J();
        }
        return graph;
    }


    @Override
    public boolean isReadyForDataProcessing() {

        if ( driver != null ) {
            try {
                driver.verifyConnectivity();
                return true;
            }
            catch (Exception ex) {
                return false;
            }
        }
        else return false;

    }

    private KnowledgeGraphNeo4J() {

        driver = GraphDatabase.driver( uri, AuthTokens.basic( username, password ) );

        registerShutdownHook( driver );

    }


    public void deleteAllFacts() {

        System.out.println( ">>> Deletion of graph starts ... " );

        Session s = driver.session();

        String st = s.writeTransaction( new TransactionWork<String>()
        {
            @Override
            public String execute(org.neo4j.driver.Transaction tx) {

                Result result = tx.run( "MATCH (n)\n" +
                                "DETACH DELETE n" );

                return result.toString();

            }

        } );

        System.out.println( st );

        System.out.println( ">>> Deletion of graph DONE." );

    }

    @Override
    public boolean describe() {
        boolean s = super.describe();
        System.out.println( "> nr of nodes: " + this.getAllNodes().size() );
        return s;
    }

    public void exequteCypherQuery(String q, boolean verbose, StringBuffer logger, String qid) {

        Session s = driver.session();

        String st = s.writeTransaction( new TransactionWork<String>()
        {
            @Override
            public String execute(org.neo4j.driver.Transaction tx) {

                try {

                    Result result = tx.run(q);

                    int i = 0;
                    while (result.hasNext()) {

                        i++;

                        org.neo4j.driver.Record row = result.next();

                        String content = row.asMap().toString();

                        if ( verbose ) {
                            System.out.println(content);
                        }
                        logger.append(content + "\n");

                    }

                    // System.out.println( "> Result (" + qid + ") : " + i + " rows." );

                    logger.append( "> Result (" + qid + ") : " + i + " rows.\n" );

                    return result.toString();

                }
                catch(Exception ex) {

                    tx.close();
                    System.err.println( "*****************" );
                    System.err.println( "*** EXCEPTION ***" );
                    System.err.println( "*****************" );
                    System.err.println( " " + ex.getMessage() + "\n\n");
                    ex.printStackTrace();

                    if ( ex.getMessage().startsWith("Expected exactly one statement per query but got:") ) {

                        System.out.println( "*** TRY MULTI-QUERY PROCESSING MODE ***");

                        String logs = processMultiLineQuery( q, true, logger );

                        if ( verbose )
                            System.out.println( logs );

                        return logs;

                    }
                    else
                        return ex.getMessage();

                }

            }

        } );


    }

    private String processMultiLineQuery(String q, boolean verbose, StringBuffer logger) {

        String[] cypherCommands = splitIntoCommands( q );

        System.out.println( "\n" + cypherCommands.length + " queries loaded from query string with " + q.length() + " bytes .\n");

        StringBuffer responses = new StringBuffer();

        int i = 0;
        boolean executeAll = false;

        for ( String c : cypherCommands ) {

            boolean executeTHIS = false;

            i++;

            Scanner scanner = new Scanner(System.in);

            System.out.println( "\n----------------" );
            System.out.println( "QUERY ("+i+"):       ["+c+"]" );
            System.out.println( "----------------" );

            logger.append( "\n----------------\n" );
            logger.append( "QUERY ("+i+"):       ["+c+"]\n" );
            logger.append( "----------------\n" );

            if ( !executeAll ) {

                System.out.print("(s)kip or (e)xecute the query? (a) for execution of all remaining queries. ");

                System.out.println(" ");

                String decision = scanner.next();

                if (decision.equals("a")) executeAll = true;

                if (decision.equals("e")) executeTHIS = true;

            }
            if ( executeAll || executeTHIS ) {

                boolean success = executeSingleQueryCypherCOMMAND(c, true, logger, "MCQ:"+i);

                System.out.println("> Execution done : [" + success + "]");

                responses.append("EXECUTION (" + i + ")-->[" + success + "] \n");

            }
            else {
                System.out.println("> SKIPPED the query! ");
                responses.append("SKIPPED (" + i + ")\n");
            }
        }

        logger.append( "\n\nSummary:\n");
        logger.append( "\n" + responses.toString() + "\n");

        return responses.toString();
    }

    private String[] splitIntoCommands(String q) {

        Vector<String> commands = new Vector<String>();

        String[] LINES = q.split("\n" );

        System.out.println( "*** LINES: " +LINES.length );

        StringBuffer sb = new StringBuffer();
        for( String ll : LINES ) {
            String l = ll.trim();
            if (l.length() > 0) {
                sb.append( l + " " );
                System.out.println( sb.toString().length() + " :: " + l );
            }
            else {
                if ( sb.toString().length() > 0 ) {
                    commands.add(sb.toString());
                    sb = new StringBuffer();
                    System.out.println( " NEW CMD IDENTIFIED. ");
                }
            }
        }

        return commands.toArray( new String[commands.size()] );
    }

    public boolean executeSingleQueryCypherCOMMAND(String c, boolean verbose, StringBuffer logger, String qid) {

        try {

            System.out.println( "#####################################################################################" );
            System.out.println( "# Query: " );
            System.out.println( "  " + c );

            exequteCypherQuery( c, verbose, logger, qid );

            System.out.println( "#####################################################################################" );

        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        };

        return true;

    }

    public void queryFromFile(File queryFilePath, File resultFile) {

        StringBuffer sbLogger = new StringBuffer();

        try {

            FileReader fr = new FileReader(queryFilePath);
            BufferedReader br = new BufferedReader( fr );
            StringBuffer sb = new StringBuffer();
            while( br.ready() ) {
                String line = br.readLine();
                if ( !line.startsWith("#") )
                    sb.append( line + " \n" );
            }
            System.out.println( "#####################################################################################" );
            System.out.println( "# Query Path: " +  queryFilePath.getAbsolutePath() );
            System.out.println( "# " );
            System.out.println( "#####################################################################################" );

            executeSingleQueryCypherCOMMAND( sb.toString() , true, sbLogger, "SCQ:1" );

            if ( resultFile != null ) {
                FileWriter fw = new FileWriter( resultFile );
                BufferedWriter bw = new BufferedWriter( fw );
                bw.write( sbLogger.toString() );
                bw.flush();
                bw.close();
                System.out.println( "> Graph Query Processor Result file has been written to: " + resultFile.getAbsolutePath() );
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        };

    }
}
