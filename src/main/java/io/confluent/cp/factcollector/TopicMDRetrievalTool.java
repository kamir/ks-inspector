package io.confluent.cp.factcollector;

import io.confluent.cp.cfg.CCloudClusterWrapper;
import org.apache.kafka.clients.admin.*;

import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

public class TopicMDRetrievalTool {

    /**
     * This tool reads topic-metadata from a Confluent cloud cluster.
     *
     * We can convert this data into a knowledge graph data model. TODO
     */

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        AdminClient kac = AdminClient.create( CCloudClusterWrapper.getPropsFrom_ROOT_FOLDER() );

        ListTopicsResult topicList = kac.listTopics();

        System.out.println( " * ");

        for( TopicListing topic : topicList.listings().get() ) {
            System.out.println( topic.toString() );
            Vector<String> tn = new Vector<String>();
            tn.add( topic.name() );

            DescribeTopicsResult a = kac.describeTopics(tn);

            Map<String, TopicDescription> b = a.allTopicNames().get();
            for( String key : b.keySet() ) {
                TopicDescription td = b.get( key );

                System.out.println( "     > details : " + td );
            }

        }

    }

}
