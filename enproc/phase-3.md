Inspect the Readme file and capture the gist of the project.

My goal is to have a tool, which connects to KSQLDB Servers, loads all relevant metadata about the servers, in particular, list of topics, list of streams, list of queries, and then allows me to process all this ground information in a variety of ways. 

Capturing the "Streaming Topologies" and the Input + Output Topics for each Query is the start.

Overlapping all queries, and forming an overall processing flow graph is the second step.

I need a proper visualization in Neo4J with browsing and analysis capabilities.

Furthermore, I want to pull in all the schema Information from the particular cluster, which serves that KSQLDB service.

Capturing topic streaming metrics from Confluent cloud brings an additional layer into this analysis.

Reading all known properties about a given topic is important, so that we can travel the processing graph, and check the partitioning structure. Repartitioning and heavy filters are indicators for bottlenecks.

Please take this information and write the problem statement and requiremenst in a well structured document and store it in folder:

enproc

Inside this folder we keep Feature Requests FR-0x.md separated from Solution Designs SD-0x.md which maps to a feature.
We intent to keep a record of all architecture decisions in ADR-0x.md in the appropriate folder for each of the artefact.

When implementing a new feature, do not change previoud behaviour, provide tests, document the imrpovements for publishing a Readme file.