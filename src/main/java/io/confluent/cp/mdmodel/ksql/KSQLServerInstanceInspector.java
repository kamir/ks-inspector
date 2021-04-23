package io.confluent.cp.mdmodel.ksql;

import io.confluent.cp.mdmodel.fdg.KSQLDependencyGraph;
import org.apache.commons.cli.*;

import java.io.*;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Logger;

public class KSQLServerInstanceInspector {

    private final static Logger log = Logger.getLogger(KSQLServerInstanceInspector.class.getName());

    /**
     * this is the location in which a particular KSQL-Application is provided.
     *
     */
    public String workdir = ".";
    /**
     * In this folder we expect the following folders:
     *
     * default_queryFolder         : holds the developed KSQL queries.
     *
     * default_queryBufferFolder   : the system stores the responses from the KSQL-REST-API in this place.
     *
     */
    String default_queryFolder = "query-stage/query-to-deploy-to-ksql-server";
    String default_queryBufferFolder = "query-stage/query-to-deploy-to-ksql-server";


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

    public KSQLServerInstanceInspector() {

        dg = KSQLDependencyGraph.getKSQLDependencyGraph();

    }

    public KSQLServerInstanceInspector(String p, String qfn) {

        this();

        this.workdir = p;

        this.default_queryFileName = qfn;

    }

    public static void main( String[] args ) throws Exception {

        Options options = new Options();
        options.addOption(Option.builder("p")
                .longOpt("projectPath")
                .hasArg(true)
                .desc("BASE PATH for streaming app scripts and topology-dumps ... this is the place from which data is loaded [REQUIRED]")
                .required(true)
                .build());

        options.addOption(Option.builder("qf")
                .longOpt("queryFileName")
                .hasArg(true)
                .desc("Filename for the KSQL query file which needs to be analysed ... this is the central part of the analysis [REQUIRED]")
                .required(true)
                .build());

        options.addOption(Option.builder("ks")
                .longOpt("ksql-server")
                .hasArg(true)
                .desc("the hostname/IP of the KSQL server we want to work with [REQUIRED]")
                .required(true)
                .build());

        options.addOption(Option.builder("bss")
                .longOpt("bootstrap.servers")
                .hasArg(true)
                .desc("the Kafka bootstrap.servers ... [OPTIONAL]")
                .required(false)
                .build());

        CommandLineParser parser = new DefaultParser();

        CommandLine cmd = null;

        String p = ".";
        String qf = "opentsx.ksql";
        String ks = "127.0.0.1";
        String port = "8088";
        String bss = "localhost:9092";
        String domain = "DOMAIN";
        String project = "PROJECT";


        try {

            cmd = parser.parse(options, args);

            if (cmd.hasOption("p")) {
                p = cmd.getOptionValue("p");
                File f = new File( p );
                System.out.println("--projectPath option = " + f.getAbsolutePath() + " (readable: "+f.canRead()+")");
                if ( !f.canRead() ) {
                    System.exit( -1 );
                }

            }

            if (cmd.hasOption("ks")) {
                ks = cmd.getOptionValue("ks");
                System.out.println("--ksql-server option = " + ks);

                if ( ks.contains( ":" ) ) {
                    String[] PARTS = ks.split(":");
                    ks = PARTS[0];
                    port = PARTS[1];
                }


            }

            if (cmd.hasOption("qf")) {
                qf = cmd.getOptionValue("qf");
                System.out.println("--queryFileName option = " + qf);
            }

            if (cmd.hasOption("bss")) {
                bss = cmd.getOptionValue("bss");
                System.out.println("--bootstrap.servers option = " + bss);
            }

        }
        catch (ParseException pe) {
            System.out.println("Error parsing command-line arguments!");
            System.out.println("Please, follow the instructions below:");
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp( "KSQLQueryInspector : ", options );
            System.exit(1);
        }

        KSQLServerInstanceInspector inspector = new KSQLServerInstanceInspector( p, qf );

        inspector.appContext = new KSQLDBApplicationContext( inspector.getQueryFolder(), inspector.default_queryFileName, inspector.getQueryBufferFolder(), ks, port, domain, project );

        inspector.getQueriesFromKSQLServer(inspector.appContext);

        inspector.processStatementsFromFile( inspector.appContext );

        inspector.compareExpacted_vs_available( inspector.appContext );

    }

    private String getQueryBufferFolder() {
        return workdir + "/" + default_queryBufferFolder;
    }

    private String getQueryFolder() {
        return workdir + "/" + default_queryFolder;
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

        log.info( "# of parsed statements: " + statements.size() );

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
