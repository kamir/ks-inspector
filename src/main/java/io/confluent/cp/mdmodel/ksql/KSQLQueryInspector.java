package io.confluent.cp.mdmodel.ksql;

import io.confluent.cp.mdmodel.fdg.KSQLDependencyGraph;
import io.confluent.mdgraph.model.IKnowledgeGraph;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;

public class KSQLQueryInspector {

    private final static Logger log = Logger.getLogger(KSQLQueryInspector.class.getName());

    /**
     * workdir is the location in which a particular KSQL-Application is provided.
     */
    public static String workdir = "./ksqldb-query-stage";

    /**
     * In a workdir folder we expect the following folders:
     *
     * default_queryDeployStageFolder       : holds the developed KSQL queries.
     *
     * default_queryBuffer                  : the system stores the responses from the KSQL-REST-API in this place.
     */
    String default_queryBuffer = "/query-loaded-from-ksql-server";
    String default_queryDeployStageFolder = "/query-to-deploy-to-ksql-server";

    /**
     * Example 1: OpenTSx DEMO uses KSQL for TSA on streaming data.
     */
    String default_queryFileName = "opentsx.ksql";

    KSQLDependencyGraph dg = null;
    KSQLDBApplicationContext appContext;

    /**
     * The KSQL statements loaded from the KSQL script.
     */
    Vector<String> statements = new Vector<>();

    public KSQLQueryInspector() {

        dg = KSQLDependencyGraph.getKSQLDependencyGraph();

    }

    public KSQLQueryInspector(String path, String qfn) {

        this();

        this.workdir = path;

        this.default_queryFileName = qfn;

    }

    public static void inspectQueryFile(String p, String qf, IKnowledgeGraph graph) throws Exception {

        String ks = "127.0.0.1";
        String port = "8088";

        KSQLQueryInspector inspector = new KSQLQueryInspector( p, qf );

        inspector.appContext = new KSQLDBApplicationContext( inspector.getQueryStageFolder(), inspector.default_queryFileName, inspector.getQueryStageFolder(), ks, port );

///        graph.registerKSQL

        inspector.processStatementsFromFile( inspector.appContext );

    }

    public static void main( String[] args ) throws Exception {

        String p = workdir;
        String qf = "200_PoC-queries-solution.sql"; // ""opentsx.ksql";
        String ks = "127.0.0.1";
        String port = "8088";

        KSQLQueryInspector inspector = new KSQLQueryInspector( p, qf );

        inspector.appContext = new KSQLDBApplicationContext( inspector.getQueryStageFolder(), inspector.default_queryFileName, inspector.getQueryStageFolder(), ks, port );

        //inspector.getQueriesFromKSQLServer(inspector.appContext);

        inspector.processStatementsFromFile( inspector.appContext );

        //inspector.compareExpacted_vs_available( inspector.appContext );

    }

    private String getQueryStageFolder() {
        return workdir + "/" + default_queryDeployStageFolder;
    }

    private String getQueryBufferFolder() {
        return workdir + "/" + default_queryBuffer;
    }

    private void compareExpacted_vs_available(KSQLDBApplicationContext appContext) {

        appContext.compare();

    }

    /**
     *
     * We load the objects, filter relevant objects, and list them ...
     *
     * The application-context specific status of a resource is provided in a report.
     *
     */
    private void getQueriesFromKSQLServer( KSQLDBApplicationContext applicationContext ) {

        String[] TOPICS;
        String[] TABLES;
        String[] STREAMS;
        String[] QUERIES;

        KSQLServerInstance server = applicationContext.ksqlServer;

        System.out.println("**** TOPICS ****");
        TOPICS = server.getListOfTopics();
        int i = 0;
        for( String t : TOPICS ) {
            i++;
            System.out.println( i + ". TOPIC: " + t );
        }
        System.out.println();

        System.out.println("**** TABLES ****");
        TABLES = server.getListOfTables();
        i = 0;
        for( String t : TABLES ) {
            i++;
            System.out.println( i + ". TABLE: " + t );
        }
        System.out.println();

        System.out.println("**** STREAMS ****");
        STREAMS = server.getListOfStreams();
        i = 0;
        for( String t : STREAMS ) {
            i++;
            System.out.println( i + ". STREAM: " + t );
            System.out.println();
        }
        System.out.println();


        System.out.println("**** QUERIES ****");
        QUERIES = server.getListOfQueries();
        i = 0;
        for( String t : QUERIES ) {
            i++;
            System.out.println("--------------------------\n" + i + ". QUERY: \n--------------------------\n" + t + "\n" );
            System.out.println();
        }
        System.out.println();

        applicationContext.setTopicsOnServer( TOPICS );
        applicationContext.setTablesOnServer( TABLES );
        applicationContext.setQueriesOnServer( QUERIES );
        applicationContext.setStreamsOnServer( STREAMS );

    }


    public void processStatementsFromFile( KSQLDBApplicationContext appContext ) throws Exception {

        processStatementsFromFile( appContext.getKSQLFile() );

    }


    public void processStatementsFromFile(File f) throws Exception {

        readQueryFile( f );

        log.info( ">>> # of parsed statements: " + statements.size() );

        analyseStatements( statements );

        dg.analyseDependencyGraph();

        storeAndRenderGraph( f.getName() );

    }

    public void storeAndRenderGraph( String filename ) throws IOException {

        File folderInsights = new File( workdir + "/insights" );
        if ( !folderInsights.exists() ) folderInsights.mkdirs();

        File folderPDF = new File( workdir + "/pdf" );
        if ( !folderPDF.exists() ) folderPDF.mkdirs();

        String nameGraphFile = folderInsights.getAbsolutePath() + "/" + filename + ".dot";
        String namePDFFile = folderPDF.getAbsolutePath() + "/" + filename + ".pdf";

        dg.persistFlowDependencyGraph(nameGraphFile);

        String cmd = "dot -Tpdf " + nameGraphFile + " -o " + namePDFFile;
        ProcessBuilder builder = new ProcessBuilder();
        builder.command("sh", "-c", cmd);

        builder.directory( new File(".") );

        Process process = builder.start();

    }


    public void analyseStatements(Vector<String> statements) {

        Hashtable<String,Integer> stats = new Hashtable<String,Integer>();

        int i = 0;

        for( String statement : statements ) {

            i++;

            if( statement.startsWith( "CREATE TABLE ")) {

                dg.processCreateStatement(statement);

                if ( statement.contains( " AS SELECT ") ) {
                    Helper.count("CTAS", stats);
                }

                if ( statement.contains( " WITH ") ) {
                    Helper.count( "CTWT", stats );
                }

                if ( statement.contains( " PARTITION ") ) {
                    Helper.count("TP", stats);
                }

                if ( statement.contains( " GROUP BY ") ) {
                    Helper.count("TG", stats);
                }

                if ( statement.contains( " FROM ") ) {
                    Helper.count("TF", stats);
                }

                if ( statement.contains( " JOIN ") ) {

                    Helper.count("TJ", stats);
                    System.out.println("[" + i + "] " + statement );

                    dg.inspectJoin( statement );

                }

            }

            if( statement.startsWith( "CREATE STREAM ")) {

                log.info( dg.toString() );

                dg.processCreateStatement(statement);

                if ( statement.contains( " AS SELECT ") ) {
                    Helper.count("CSAS", stats);
                }
                if ( statement.contains( " WITH ") ) {
                    Helper.count( "CSWT", stats );
                }
                if ( statement.contains( " PARTITION ") ) {
                    Helper.count("SP", stats);
                }

                if ( statement.contains( " GROUP BY ") ) {
                    Helper.count("SG", stats);
                }

                if ( statement.contains( " FROM ") ) {
                    Helper.count("SF", stats);
                }

                if ( statement.contains( " JOIN ") ) {
                    Helper.count("SJ", stats);
                    System.out.println("[" + i + "] " + statement );

                    dg.inspectJoin( statement );

                }

            }

        }

        System.out.println(stats);

    }






    private String readQueryFile(File f) throws IOException {

        System.out.println("# KSQL-Query-file      : " + f.getAbsolutePath() + "   ("+f.canRead()+")" );

        StringBuffer sb = new StringBuffer();

        BufferedReader br = new BufferedReader( new FileReader( f ) );

        return readQuerryData( br );

    }




    String readQuerryData( BufferedReader br ) throws IOException {

        StringBuffer sb = new StringBuffer();

        int lineCount = 0;
        int commentCount = 0;
        int streamCount = 0;
        int tableCount = 0;
        int otherCount = 0;
        int emptyLineCount = 0;
        int insertCount = 0;
        int statementsCount = 0;
        int statementsLength = 0;

        int csasCount = 0;

        StringBuffer statement = new StringBuffer();

        statement.append("");

        try {

            while( br.ready() ) {

                String line = br.readLine().trim();
                //  System.out.println(line);

                lineCount++;

                if (line.length() < 2) {
                    emptyLineCount++;
                    // IGNORE THAT LINE.

                } else {


                    if (line.startsWith("--")) {
                        commentCount++;
                        // IGNORE THAT LINE.

                    } else {

                        statement.append( line + " " );

                        /****************
                           SIMPLE STATS
                         ****************/

                        if (line.startsWith("CREATE STREAM")) {
                            streamCount++;
//                            dg.processCS(line);
                        }
                        else if (line.startsWith("CREATE TABLE")) {
                            tableCount++;
//                            dg.processCT(line);
                        }
                        else if (line.startsWith("INSERT")) {
                            insertCount++;

                        }
                        else {
                            otherCount++;
                        }

                        if ( line.endsWith(";") ) {

                            statementsCount++;

                            trackStatement( statement.toString(), statementsCount, statementsLength );

                            statementsLength=0;

                            statement = new StringBuffer();
                            statement.append("");

                        }

                    }

                }

            }

        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println("# lines     : " + lineCount );

        System.out.println("# comments  : " + commentCount );

        System.out.println("# tables    : " + tableCount );
        System.out.println("# streams   : " + streamCount );
        System.out.println("# insert    : " + insertCount );

        System.out.println("# empty         : " + emptyLineCount );
        System.out.println("# otherCount    : " + otherCount );

        return sb.toString();

    }

    private void trackStatement(String KSQL, int statementsCount, int statementsLength) {

        String line = statementsCount + " | " + statementsLength + " | " + KSQL;
        log.info( line );

        statements.add( KSQL );

        if ( KSQL.startsWith("CREATE STREAM") ) appContext.addExpectedStream( KSQL );

        if ( KSQL.startsWith("CREATE TABLE") ) appContext.addExpectedTable( KSQL );

    }





}
