package io.confluent.cp.mdmodel;

//import com.atlassian.common.api.*;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import io.confluent.cp.factflow.JiraChangeEventConsumer;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public class JiraTicketFlowGraph {

    Gson gson = new Gson();
    JsonParser jsonParser = new JsonParser();

    static JiraTicketFlowGraph g = null;

    public static void main(String[] ARGS) {

        Properties props = new Properties();

        JiraChangeEventConsumer processor = new JiraChangeEventConsumer( "jira-event-processor", props );
        processor.processEvents(g);

    }

    public static void run(Properties props) {

        if( g == null )
            g = new JiraTicketFlowGraph();

        try {
            FileInputStream fins = new FileInputStream(new File("./cfg/ccloud.props"));
            Properties p = new Properties();
            p.load(fins);

            props.putAll( p );

        }
        catch(Exception ex) {
            ex.printStackTrace();
        }

        props.list(System.out );

        JiraChangeEventConsumer processor = new JiraChangeEventConsumer( "jira-event-processor", props );
        processor.processEvents( g );

    }

    public void handleEvent(String value) {

        //System.out.println( "-----" );
        //System.out.println( value );
        //System.out.println( "-----\n" );

        JsonElement jsonTree = null;

        try {
            jsonTree = jsonParser.parse( value );
        }
        catch(Exception ex){
            System.err.println( ex.getMessage() );
            jsonTree = null;
        }

        if( jsonTree != null ) {
/*            System.out.println("\n======== START ========");
            System.out.println(jsonTree);
            System.out.println("======== ENDE ========\n");
*/
            System.out.println(" \t[OK]");
        }
    }

/*
    public void onIssueEvent(IssueEvent issueEvent) {
        Long eventTypeId = issueEvent.getEventTypeId();
        Issue issue = issueEvent.getIssue();

        if (eventTypeId.equals(EventType.ISSUE_CREATED_ID)) {
            log.info("Issue {} has been created at {}.", issue.getKey(), issue.getCreated());
        } else if (eventTypeId.equals(EventType.ISSUE_RESOLVED_ID)) {
            log.info("Issue {} has been resolved at {}.", issue.getKey(), issue.getResolutionDate());
        } else if (eventTypeId.equals(EventType.ISSUE_CLOSED_ID)) {
            log.info("Issue {} has been closed at {}.", issue.getKey(), issue.getUpdated());
        }
    }

*/
}
