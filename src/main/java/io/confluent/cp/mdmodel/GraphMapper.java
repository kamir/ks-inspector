package io.confluent.cp.mdmodel;

import io.confluent.mdgraph.model.IKnowledgeGraph;

import java.io.File;

/**
 * GraphMappers read data from any kind of source and interprete the content in a
 * meaningful way, so that the knowledge graph can be populated.
 */
public interface GraphMapper {

    public void process(File csvFile, IKnowledgeGraph kg, boolean append);

}
