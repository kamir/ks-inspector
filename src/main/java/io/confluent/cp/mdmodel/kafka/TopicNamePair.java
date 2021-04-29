package io.confluent.cp.mdmodel.kafka;

public class TopicNamePair {

    public String contextualName = null;
    public String originalName = null;

    public TopicNamePair( String domain, String project, String topic ) {
        originalName = topic;
        contextualName = domain + project + topic;
    }

    @Override
    public String toString() {
        return "Topic name [" + originalName  + "] in context doain context : {" + "contextualName='" + contextualName + "\'";
    }
}