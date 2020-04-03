# ks-inspector
A tool which helps operatora and developera of Kafka Streaming application to understand dependencies between KSQL queries and KStreams applications.

<a href="https://codeclimate.com/github/kamir/ks-inspector"><img src="https://api.codeclimate.com/v1/badges/ef4bcda7d1b5fd0a4f1e/maintainability" /></a>  [![Build Status](https://travis-ci.org/kamir/ks-inspector.svg?branch=master)](https://travis-ci.org/kamir/ks-inspector)


## Concept

We define an *application context* in order to provide all required information for an
analysis of a particular *streaming use-case*. 
This also works for for multiple uses cases in a multi-tenant environment.

The application context consists of:

- the list of expected topics
- the KSQL queries which implement the application
- the hostname and the port to connect to the KSQL server REST-API

With this information we are able to:

- deploy the KSQL queries step by step automatically.
- analyse existing KSQL queries.
- compare the expected queries / streams / tables / topics with available queries / streams / tables / topics.
- any mismatch is a hint for operational, implementation, or even design problems.

![The KStreams application context](docs/intro.png)

### How to use the tool?

You can run the tool via Maven with the `mvn exec:java` command. 

To point to your own application context, please provide the following arguments:

- *-p*
- *-ks*
- *-qf*

as properties in the `pom.xml` file as shown in this example:

```
    <!-- use custom project and overwrite default settings for KSQL application context.  -->
    <profiles>
        <profile>
            <id>p1</id>
            <properties>
                <maven.test.skip>true</maven.test.skip>
                <argument1k>-p</argument1k>
                <argument1v>/Users/mkampf/Engagements/KSQL/P1</argument1v>
                <argument2k>-ks</argument2k>
                <argument2v>127.0.0.1</argument2v>
                <argument3k>-bss</argument3k>
                <argument3v>127.0.0.1:9092</argument3v>
                <argument4k>-qf</argument4k>
                <argument4v>script1.ksql</argument4v>
            </properties>
        </profile>
    </profiles>
```

The following arguments are used by *ks-inspector*:

```
usage: KSQLQueryInspector :

 -bss,--bootstrap.servers <arg>   the Kafka bootstrap.servers ... [OPTIONAL]
 
 -ks,--ksql-server <arg>          the hostname/IP of the KSQL server we want to work with [REQUIRED]
 
 -p,--projectPath <arg>           BASE PATH for streaming app topology-dumps and KSQL scripts 
                                  ... this is the place from which the custom data is loaded [REQUIRED]
 
 -qf,--queryFileName <arg>        Filename for the KSQL query file which needs to be analysed 
                                  ... this is the central part of the analysis [REQUIRED]
                                                                 
```                                 


### How to draw the dependency graph?

![A simple dependency graph for streams an tables](docs/simple-dep-graph.png)

The tool produces a dependency graph in the folder `insights` within your working directory.

Using the Graphviz tool we are able to render a dependency network as a PDF file.
```
dot -Tpdf insights/opentsx.ksql.dot -o pdf/opentsx.pdf
```
Source: https://github.com/rakhimov/cppdep/wiki/How-to-view-or-work-with-Graphviz-Dot-files

### How to get the currently deployed queries from KSQL server via REST API?

```
curl -X "POST" "http://localhost:8088/ksql" \
     -H "Content-Type: application/vnd.ksql.v1+json; charset=utf-8" \
     -d $'{
  "ksql": "LIST STREAMS;",
  "streamsProperties": {}
}' > streams.data
```

```
curl -X "POST" "http://localhost:8088/ksql" \
     -H "Content-Type: application/vnd.ksql.v1+json; charset=utf-8" \
     -d $'{
  "ksql": "LIST TABLES;",
  "streamsProperties": {}
}' > tables.data
```

```
curl -X "POST" "http://localhost:8088/ksql" \
     -H "Content-Type: application/vnd.ksql.v1+json; charset=utf-8" \
     -d $'{
  "ksql": "LIST QUERIES;",
  "streamsProperties": {}
}' > queries.data
```

Source: https://rmoff.net/2019/01/17/ksql-rest-api-cheatsheet/
