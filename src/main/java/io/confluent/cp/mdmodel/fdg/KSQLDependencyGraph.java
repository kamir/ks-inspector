package io.confluent.cp.mdmodel.fdg;

import io.confluent.cp.mdmodel.ksql.Helper;
import io.confluent.cp.mdmodel.ksql.KSQLDBApplicationContext;
import io.confluent.cp.mdmodel.ksql.KSQLQueryInspector;
import io.confluent.cp.util.graph.analysis.LabelPropagationClustering;
import org.jgrapht.Graph;
import org.jgrapht.alg.cycle.CycleDetector;
import org.jgrapht.alg.interfaces.ClusteringAlgorithm;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.nio.Attribute;
import org.jgrapht.nio.DefaultAttribute;
import org.jgrapht.nio.dot.DOTExporter;
import org.jgrapht.traverse.TopologicalOrderIterator;


import java.io.*;
import java.util.*;
import java.util.logging.Logger;

public class KSQLDependencyGraph {

    KSQLDBApplicationContext context = null;

    private final static Logger log = Logger.getLogger(KSQLQueryInspector.class.getName());

    static KSQLDependencyGraph theGraph = new KSQLDependencyGraph();


    public void setContext( KSQLDBApplicationContext context ) {
        this.context = context;
    }

    public static KSQLDependencyGraph getKSQLDependencyGraph() {
        return theGraph;
    }

    Vector<Set<String>> largeClusters = null;

    Graph<String, DataFlowEdge> flowDependencyGraph = new SimpleDirectedWeightedGraph<>( DataFlowEdge.class );
    Graph<String, DefaultEdge> flowDependencyGraph_undirected = new SimpleWeightedGraph<>( DefaultEdge.class );

    boolean hasCycles = false;
    ClusteringAlgorithm.Clustering<String> clustering = null;

    /**
     * Persist the flowDependencyGraph (SimpleDirectedWeightedGraph).
     *
     * @param name
     * @throws IOException
     */
    public void persistFlowDependencyGraph( String name ) throws IOException {

        DOTExporter<String, DefaultEdge> exporter = new DOTExporter<>(v -> {
            v = v.replace('.', '_');
            return v.replace('-', '_');
        });

        exporter.setVertexAttributeProvider((v) -> {
            Map<String, Attribute> map = new LinkedHashMap<>();
            map.put("label", DefaultAttribute.createAttribute(v.toString()));
            return map;
        });

        Writer writer = new StringWriter();
        exporter.exportGraph(flowDependencyGraph_undirected, writer);

        System.out.println(writer.toString());

        FileWriter fw = new FileWriter( new File( name ) );

        fw.write( writer.toString() );
        fw.flush();
        fw.close();

    }

    private void _addLink(String s, String t) {

        s = s.replaceAll("-", "_" );
        t = t.replaceAll("-", "_" );

        if ( s == null || t == null ) {
            System.out.println("**************************************************");
            System.out.println("***** BAD VERTEX : " + s + " - " + t);
            System.out.println("**************************************************");
        }

        if (s.contains("'") || t.contains("'")){
            System.out.println("**************************************************");
            System.out.println("***** BAD VERTEX : " + s + " - " + t);
            System.out.println("**************************************************");
        }

        flowDependencyGraph.addVertex( s );
        flowDependencyGraph.addVertex( t );

        try {
            flowDependencyGraph.addEdge(s, t, new io.confluent.cp.mdmodel.fdg.DataFlowEdge(s, t, 1.0));
        }
        catch(Exception ex) {
            System.out.println( "*********************" );
            System.out.println( ex.getMessage() );
            System.out.println( "*********************" );

            t = t+"__IN_LOOP__";
            flowDependencyGraph.addVertex( t );
            flowDependencyGraph.addEdge(s, t, new io.confluent.cp.mdmodel.fdg.DataFlowEdge(s, t, 1.0));
        }
        flowDependencyGraph_undirected.addVertex( s );
        flowDependencyGraph_undirected.addVertex( t );

        flowDependencyGraph_undirected.addEdge( s, t );

    }


    public void analyseDependencyGraph() throws Exception {

        inspectCycles(flowDependencyGraph);

        inspectClusters(flowDependencyGraph_undirected);

        // this is a filter step ...
        createLargeSubgraphs( false );

        numberLargeClusters( false );

    }

    Hashtable<Integer, Set<String>> numberedClusters = new Hashtable<Integer, Set<String>>();

    private void numberLargeClusters( boolean useOnlyLargeSubgraphs ) {
        int nr = 0;
        for( Set<String> s : largeClusters ) {
            numberedClusters.put( nr, s );
            nr++;
        }
    }

    public void storeD3JSModel(String filename) throws Exception {

        File data = new File( "ksqldb-query-stage/d3js/force-directed-graph/files/" + filename + ".json");
        FileWriter fw = new FileWriter( data );

        fw.write("{\n" + "  \"nodes\": [\n");

        Set<String> vertexes = flowDependencyGraph.vertexSet();
        Set<DataFlowEdge> edges = flowDependencyGraph.edgeSet();

        int i = 0;

        for( String node : vertexes ){

            Integer[] clusterIds = getClusterId( node );

            if( clusterIds.length > 1 ) {
                log.info( ">>> " + node + " is in more than one cluster! " + clusterIds.toString() );
            }

            if ( i > 0 ) fw.write( ", \n");
            fw.write( "       {\"id\": \"" + node + "\", \"group\": " + clusterIds[0] + "}");

            i++;
        }

        fw.write( "  ],\n" +
                "  \"links\": [" );

        int j = 0;

        for( DataFlowEdge edge : edges ) {

            System.out.println( edge );

            if ( j > 0 ) fw.write( ", \n");
             fw.write( "            {\"source\": \"" + edge.source + "\", \"target\": \"" + edge.targett + "\", \"value\": " + edge.weight + "}" );

            j++;
        }

        fw.write( "\n" + "  ]\n" + "}" );
        fw.flush();
        fw.close();

    }

    private Integer[] getClusterId(String node) {

        Vector<Integer> cids = new Vector<>();

        for( Integer cn : numberedClusters.keySet() ) {
            Set<String> cluster = numberedClusters.get( cn );
            if ( cluster.contains( node ) )
                cids.add( cn );
        }

        return cids.toArray( new Integer[cids.size()] );
    }

    // we remove all nodes not in the large clusters
    private void createLargeSubgraphs( boolean removeNodes) {

        Set<String> nodes = flowDependencyGraph.vertexSet();

        Set<DataFlowEdge> edges = flowDependencyGraph.edgeSet();

        System.out.println( " flowDependencyGraph            : " + flowDependencyGraph.vertexSet().size() );
        System.out.println( " flowDependencyGraph_undirected : " + flowDependencyGraph_undirected.vertexSet().size() );

        Vector<String> vertexexToRemove = new Vector<String>();

        for( String n : nodes ) {

            boolean removeNode = true;

            for( Set<String> s : largeClusters ) {
                if (s.contains( n ))
                    removeNode = false;
            }

            if ( removeNode && removeNodes )
            {
                System.out.println("> remove: " + n);
                vertexexToRemove.add( n );
            }
        }

        flowDependencyGraph_undirected.removeAllVertices( vertexexToRemove );
        flowDependencyGraph.removeAllVertices( vertexexToRemove );

        System.out.println( " flowDependencyGraph            : " + flowDependencyGraph.vertexSet().size() );
        System.out.println( " flowDependencyGraph_undirected : " + flowDependencyGraph_undirected.vertexSet().size() );

    }


    private void inspectClusters(Graph<String, DefaultEdge> flowDependencyGraph) {

        LabelPropagationClustering<String, DefaultEdge> alg1 = new LabelPropagationClustering<String, DefaultEdge>(flowDependencyGraph);

        clustering = alg1.getClustering();

        System.out.println( "# number of clusters: " + clustering.getNumberClusters() );

        // HISTORGRAM OF CLUSTERS ...
        for(  Set<String> s : alg1.getClustering().getClusters() )
            System.out.println( ">   # s: " + s.size() );

        largeClusters = getClustersLargerThan( 0 );

    }

    public Vector<Set<String>> getClustersLargerThan( int z_min ) {

        Vector<Set<String>> clusters = new Vector<Set<String>>();

        for(  Set<String> s : clustering.getClusters() ) {
//             System.out.println("   # s: " + s.size());
            if ( s.size() > z_min ) {
                clusters.add(s);
            }
        }

        int z = 0;

        for(  Set<String> s : clusters ) {
            System.out.println("#### s: " + s.size() + " => " + s );
            z = z + 1;
        }

        System.out.println( z + " out of " + clustering.getNumberClusters() + " are larger than: s=" + z_min );

        return clusters;

    }

    private void inspectCycles(Graph<String, DataFlowEdge> flowDependencyGraph) {

        CycleDetector<String, DataFlowEdge> cycleDetector;
        // Checking for cycles in the dependencies
        cycleDetector = new CycleDetector<String, DataFlowEdge>(flowDependencyGraph);

        // Cycle(s) detected.
        if (cycleDetector.detectCycles()) {
            Iterator<String> iterator;
            Set<String> cycleVertices;
            Set<String> subCycle;
            String cycle;

            System.out.println("### Cycles in dependency graph detected. ###" );

            // Get all vertices involved in cycles.
            cycleVertices = cycleDetector.findCycles();

            // Loop through vertices trying to find disjoint cycles.
            while (!cycleVertices.isEmpty()) {
                System.out.println("Cycle:");

                // Get a vertex involved in a cycle.
                iterator = cycleVertices.iterator();
                cycle = iterator.next();

                // Get all vertices involved with this vertex.
                subCycle = cycleDetector.findCyclesContainingVertex(cycle);
                for (String sub : subCycle) {
                    System.out.println("   " + sub);
                    // Remove vertex so that this cycle is not encountered again
                    cycleVertices.remove(sub);
                }
            }

            hasCycles = true;

        }

        // If no cycles are detected, output vertices topologically ordered
        else {
            String v;
            TopologicalOrderIterator<String, DataFlowEdge> orderIterator;

            System.out.println();
            System.out.println("*** No Cycles in dependency graph detected.");

            orderIterator = new TopologicalOrderIterator<String, DataFlowEdge>(flowDependencyGraph);

            // System.out.println("\nTopological Ordering:");
            while (orderIterator.hasNext()) {
                v = orderIterator.next();
                //     System.out.println(v);
            }

            hasCycles = false;
        }
    }


    public void processCS( String line, KSQLDBApplicationContext appContext  ) {

        //System.out.println( " ### " + line );
        String[] words = line.split(" ");
//        String streamName = "STREAM_" + words[2];

        int index = 0;
        int i = 0;
        for( String word : words ) {
            //System.out.println( i + " : " + index + " - " + word );
            i = i + 1;

            if ( word.equals( "WITH" ) ) {
                index = i;
            }
        }

        if ( index > 0 ) {

            // FINDE POSITION "WITH" und nimm nächstes ...
            String streamName = words[2];

            String fragment = words[index].substring(14, words[index].length() - 2);

            // String topicName = "TOPIC_" + fragment;
            String topicName = fragment;

            appContext.trackTopicName( topicName );

            _addLink( putIntoContext( topicName ), putIntoContext( streamName ) );

        }
    }

    private String putIntoContext( String entity ) {

        return this.context.putIntoDomainContext( entity );

    }

    public void processCreateStatement( String line, KSQLDBApplicationContext appContext ) {

        System.out.println( "_________________________________" );
        System.out.println( "LINE: " + line );
        System.out.println( "_________________________________" );

        String[] words = line.split(" ");

        String tableName = words[2];

        int index_WITH = 0;

        int index_FROM = 0;

        int i = 0;

        for( String word : words ) {

            System.out.println( i + " : " + index_WITH + " :: " + index_FROM + " - " + word );
            i = i + 1;

            if ( word.equals( "WITH" ) ) {
                index_WITH = i;
            }

            if ( word.equals( "FROM" ) ) {
                index_FROM = i;
            }
        }

        if ( index_WITH > 0 ) {

            String fragment = words[index_WITH].substring(14, words[index_WITH].length() - 2);
            // String fragment = words[index_WITH];

            // String topicName = "TOPIC_" + fragment;
            String topicName = fragment;

            _addLink( putIntoContext( topicName ), putIntoContext( tableName ) );

            if ( appContext != null )
                appContext.trackTopicName( topicName );

        }

        if ( index_FROM > 0 ) {

            String fragment = words[index_FROM];

            String streamName = fragment;

            _addLink( putIntoContext( streamName ), putIntoContext( tableName ) );

        }

    }


    public int inspectJoin(String statement) {

        String findStr = " FROM ";
        System.out.println( findStr + " => " + Helper.getCountOfStringsInString(findStr, statement) );

        findStr = " LEFT JOIN ";
        System.out.println( findStr + " => " + Helper.getCountOfStringsInString(findStr, statement) );

        findStr = " AS ";
        System.out.println( findStr + " => " + Helper.getCountOfStringsInString(findStr, statement) );

        String[] words = statement.split(" ");

        String lastWord = " ";

        int i = 0;

        String target = null;

        for( String w : words ) {

            String s1 = null;
            String s2 = null;

            String current_word = w;
            System.out.println( "W: [" + lastWord + "] - [" +  current_word  + "]"  );

            i = i + 1;
            if ( lastWord.equals("TABLE") || lastWord.equals("STREAM") ) {
                target = current_word;
            };
            if (lastWord.equals("FROM")) {
                s1 = current_word;

                _addLink( putIntoContext( s1 ), putIntoContext( target ) );
            };
            if (lastWord.equals("JOIN")) {
                s2 = current_word;
                _addLink( putIntoContext( s2 ), putIntoContext( target) );
            };

            lastWord = w;

            System.out.println( "               " + s1 + " -> " + target );
            System.out.println( "               " + s2 + " -> " + target );

        }

        return 0;

    }

}
