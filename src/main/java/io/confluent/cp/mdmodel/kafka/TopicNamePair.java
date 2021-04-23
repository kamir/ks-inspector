package io.confluent.cp.mdmodel;

public class TopicNamePair {

    public String contextualName = null;
    public String originalName = null;

    public TopicNamePair( String domain, String project, String topic ) {
        originalName = topic;
        contextualName = domain + project + topic;
    }

}