package io.confluent.cp.clients;

import io.confluent.cp.CCloudClusterWrapper;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.DescribeAclsResult;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.acl.AccessControlEntryFilter;
import org.apache.kafka.common.acl.AclBinding;
import org.apache.kafka.common.acl.AclBindingFilter;
import org.apache.kafka.common.resource.ResourceFilter;
import org.apache.kafka.common.resource.ResourcePatternFilter;

import java.util.Collection;
import java.util.concurrent.ExecutionException;

public class ACLRetrievalTool {

    /**
     * This tool reads all ACLs from a Confluent cloud cluster.
     *
     * We convert this data into a knowledge graph data model.
     */

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        AdminClient kac = AdminClient.create( CCloudClusterWrapper.getProps() );

        AclBindingFilter aclBindingFilter = new AclBindingFilter(ResourceFilter.ANY, AccessControlEntryFilter.ANY);

        System.out.println( " * ");

        DescribeAclsResult acl = kac.describeAcls(aclBindingFilter);

        System.out.println( " * ");

        KafkaFuture<Collection<AclBinding>> f = acl.values();

        System.out.println( " * ");

        Collection<AclBinding> aclCollection = f.get();

        System.out.println( " * ");

        for( AclBinding a : aclCollection ) {
            System.out.println( a.entry().toString() );
        }

    }

}
