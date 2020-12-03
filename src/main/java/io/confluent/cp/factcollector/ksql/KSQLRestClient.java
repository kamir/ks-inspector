package io.confluent.cp.factcollector.ksql;

import org.apache.http.HttpHeaders;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClients;

import java.net.URI;
import java.util.Properties;

public class KSQLRestClient {

    String host = "127.0.0.1";
    String port = "8088";

    public static void main(final String[] args) throws Exception {

        KSQLRestClient c = new KSQLRestClient();

        Properties props1 = new Properties();
        c.callRestAPI( "LIST TABLES;", props1 );

        Properties props2 = new Properties();
        c.callRestAPI( "LIST STREAMS;", props2 );

        Properties props3 = new Properties();
        c.callRestAPI( "LIST QUERIES;", props3 );

        Properties props4 = new Properties();
        c.callRestAPI( "LIST TOPICS;", props4 );

    }

    public String callRestAPI( String CMD, Properties props ) throws Exception {

        URI uri = new URI("http://"+host+":"+port+"/ksql");

        HttpClient client = HttpClients.custom().build();

/*
        HttpUriRequest request = RequestBuilder.get()
                .setUri( uri )
                .setHeader(HttpHeaders.CONTENT_TYPE, "application/vnd.ksql.v1+json; charset=utf-8")
                .setHeader(HttpHeaders.ACCEPT, "application/json")
                .build();

        client.execute(request);
*/

        HttpPost httpPost = new HttpPost(uri);
        httpPost.addHeader( HttpHeaders.CONTENT_TYPE, "application/vnd.ksql.v1+json; charset=utf-8");
        httpPost.addHeader( HttpHeaders.ACCEPT, "application/json" );

        String requestWrapper = "{\n" +
                "                \"ksql\": \" "+  CMD + "\",\n" +
                "                \"streamsProperties\": {}\n" +
                "}\n";

        httpPost.setEntity(new StringEntity( requestWrapper ) );

        CloseableHttpResponse response = (CloseableHttpResponse) client.execute(httpPost);

        String responseString = new BasicResponseHandler().handleResponse(response);

        // System.out.println(responseString);

        // System.out.println( response.getStatusLine().getStatusCode() + " <== " + 200 );

        return responseString;

    }

    public String executeQuery( String CMD, Properties props ) throws Exception {

        URI uri = new URI("http://"+host+":"+port+"/query");

        HttpClient client = HttpClients.custom().build();
/*

        HttpUriRequest request = RequestBuilder.get()
                .setUri( uri )
                .setHeader(HttpHeaders.CONTENT_TYPE, "application/vnd.ksql.v1+json; charset=utf-8")
                .setHeader(HttpHeaders.ACCEPT, "application/json")
                .build();

        client.execute(request);

*/

        HttpPost httpPost = new HttpPost(uri);
        httpPost.addHeader( HttpHeaders.CONTENT_TYPE, "application/vnd.ksql.v1+json; charset=utf-8");
        httpPost.addHeader( HttpHeaders.ACCEPT, "application/json" );

        String requestWrapper = "{\n" +
                "                \"ksql\": \" "+  CMD + "\",\n" +
                "                \"streamsProperties\": {}\n" +
                "}\n";

        httpPost.setEntity(new StringEntity( requestWrapper ) );

        CloseableHttpResponse response = (CloseableHttpResponse) client.execute(httpPost);

        String responseString = new BasicResponseHandler().handleResponse(response);

        // System.out.println(responseString);

        // System.out.println( response.getStatusLine().getStatusCode() + " <== " + 200 );

        return responseString;

    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setHostAndPort(String host, String port) {
        this.host = host;
        this.port = port;
    }

}
