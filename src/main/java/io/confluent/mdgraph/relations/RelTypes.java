package io.confluent.mdgraph.relations;

import org.neo4j.graphdb.RelationshipType;

public enum RelTypes implements RelationshipType {

    CONSUMES_DATA,
    PRODUCES_DATA,
    FILTERS_DATA,
    JOINS_DATA,
    ENRICHES_DATA;

}