package io.confluent.kstreams.fdg;

import io.confluent.Helper;
import io.confluent.ksql.KSQLQueryInspector;
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

    private final static Logger log = Logger.getLogger(KSQLQueryInspector.class.getName());


    static KSQLDependencyGraph theGraph = new KSQLDependencyGraph();

    public static KSQLDependencyGraph getKSQLDependencyGraph() {
        return theGraph;
    }

    Vector<Set> largeClusters = null;

    Graph<String, DataFlowEdge> flowDependencyGraph = new SimpleDirectedWeightedGraph<>(DataFlowEdge.class);
    Graph<String, DefaultEdge> flowDependencyGraph_undirected = new SimpleWeightedGraph<>(DefaultEdge.class);


    boolean hasCycles = false;
    ClusteringAlgorithm.Clustering clustering = null;

    /**
     * Persist the flowDependencyGraph (SimpleDirectedWeightedGraph).
     *
     * @param name
     * @throws IOException
     */
    public void persistFlowDependencyGraph( String name ) throws IOException {

        DOTExporter<String, DefaultEdge> exporter = new DOTExporter<>(v -> v.replace('.', '_'));

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

    public void addLink(String s, String t) {

        if (s.contains("'") || t.contains("'")){
            System.out.println("**************************************************");
            System.out.println("***** BAD VERTEX : " + s + " - " + t);
            System.out.println("**************************************************");
        }

        flowDependencyGraph.addVertex( s );
        flowDependencyGraph.addVertex( t );

        flowDependencyGraph.addEdge( s, t, new DataFlowEdge(s,t,1.0));

        flowDependencyGraph_undirected.addVertex( s );
        flowDependencyGraph_undirected.addVertex( t );

        flowDependencyGraph_undirected.addEdge( s, t );

    }


    public void analyseDependencyGraph() throws Exception {

        inspectCycles(flowDependencyGraph);

        inspectClusters(flowDependencyGraph_undirected);

        // this is a filter step ...
        createLargeSubgraphs( false );

        _numberLargeClusters( false );

        storeD3JSModel();

    }

    Hashtable<Integer, Set<String>> numberedClusters = new Hashtable<>();

    private void _numberLargeClusters( boolean useOnlyLargeSubgraphs ) {
        int nr = 0;
        for( Set<String> s : largeClusters ) {
            numberedClusters.put( nr, s );
            nr++;
        }
    }

    private void storeD3JSModel() throws Exception {

        File data = new File( "d3js/force-directed-graph/files/graph2");
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

        LabelPropagationClustering alg1 = new LabelPropagationClustering(flowDependencyGraph);

        clustering = alg1.getClustering();

        System.out.println( "# number of clusters: " + clustering.getNumberClusters() );

        // HISTORGRAM OF CLUSTERS ...
        for(  Set s : (List<Set>)alg1.getClustering().getClusters() )
            System.out.println( ">   # s: " + s.size() );

        largeClusters = getClustersLargerThan( 0 );

    }

    public Vector<Set> getClustersLargerThan( int z_min ) {

        Vector<Set> clusters = new Vector<Set>();

        for(  Set s : (List<Set>) clustering.getClusters() ) {
//             System.out.println("   # s: " + s.size());
            if ( s.size() > z_min ) {
                clusters.add(s);
            }
        }

        int z = 0;

        for(  Set s : (List<Set>)clusters ) {
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


    public void __processCS( String line ) {

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

            addLink(topicName, streamName);

        }
    }

    public void processCreateStatement( String line ) {

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

            //       String topicName = "TOPIC_" + fragment;
            String topicName = fragment;

            addLink(topicName, tableName);

        }

        if ( index_FROM > 0 ) {

            String fragment = words[index_FROM];

            String streamName = fragment;

            addLink(streamName, tableName);

        }

    }



    public int inspectJoin(String statement) {

        String findStr = " FROM ";
        System.out.println( findStr + " => " + Helper.getCountOfStringsInString(findStr, statement) );

        findStr = " JOIN ";
        System.out.println( findStr + " => " + Helper.getCountOfStringsInString(findStr, statement) );

        findStr = " AS ";
        System.out.println( findStr + " => " + Helper.getCountOfStringsInString(findStr, statement) );

        String[] words = statement.split(" ");

        String target = null;
        String s1 = null;
        String s2 = null;

        String lastWord = " ";


        int i = 0;
        for( String w : words ) {

            String current_word = w;

            i = i + 1;
            if ( lastWord.equals("TABLE") || lastWord.equals("STREAM") ) {
                target = w;
            };
            if (lastWord.equals("FROM")) {
                s1 = w;
            };
            if (lastWord.equals("JOIN")) {
                s2 = w;
            };

            lastWord = w;

            System.out.println( s1 + " -> " + target );
            System.out.println( s2 + " -> " + target );

            addLink( s1, target );
            addLink( s2, target );

        }

    }

}
