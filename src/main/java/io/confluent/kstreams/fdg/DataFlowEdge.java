package io.confluent.kstreams.fdg;

public class DataFlowEdge {

    String label = "data flow";

    String source = "s";
    String targett = "t";

    double weight = 0.0;

    public DataFlowEdge(String s, String t, double v) {
        this.source = s;
        this.targett = t;
        this.weight = v;
    }
}
