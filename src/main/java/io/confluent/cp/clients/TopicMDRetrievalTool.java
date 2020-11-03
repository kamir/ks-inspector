package io.confluent.cp.clients;

import io.confluent.cp.CCloudClusterWrapper;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.acl.AccessControlEntryFilter;
import org.apache.kafka.common.acl.AclBinding;
import org.apache.kafka.common.acl.AclBindingFilter;
import org.apache.kafka.common.resource.ResourcePatternFilter;
import org.bouncycastle.math.ec.ScaleYNegateXPointMap;

import java.io.IOException;
import java.io.InputStream;
import java.security.acl.Acl;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

public class TopicMDRetrievalTool {

    /**
     * This tool reads all ACLs from a Confluent cloud cluster.
     *
     * We convert this data into a knowledge graph data model.
     */

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        AdminClient kac = AdminClient.create( CCloudClusterWrapper.getProps() );

        ListTopicsResult topicList = kac.listTopics();

        System.out.println( " * ");

        for( TopicListing topic : topicList.listings().get() ) {
            System.out.println( topic.toString() );
            Vector<String> tn = new Vector<String>();
            tn.add( topic.name() );

            DescribeTopicsResult a = kac.describeTopics(tn);

            Map<String, TopicDescription> b = a.all().get();
            for( String key : b.keySet() ) {
                TopicDescription td = b.get( key );

                System.out.println( "     > details : " + td );
            }

        }

    }

}
