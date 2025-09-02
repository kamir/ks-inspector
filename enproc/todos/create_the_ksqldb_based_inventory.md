Got it — you want a functional overview in table form, highlighting what the Java client functions can do, especially in the context of building and maintaining an inventory of ksqlDB streams. Here’s a structured summary:

⸻

ksqlDB Java Client — Function Overview for Stream Inventory

Function	What It Does	Use for Stream Inventory
listStreams()	Lists all ksqlDB streams with metadata like name, topic, key/value formats, windowing.	Core method to build the inventory of available streams. Collects stream definitions for auditing or cataloging.
describeSource(name)	Describes a specific stream or table: schema, topic, formats, queries reading/writing.	Enrich inventory with per-stream schema and lineage (what queries produce/consume it).
listTables()	Lists ksqlDB tables (similar to streams, but stateful).	Expand inventory to include tables/materialized views.
listTopics()	Lists Kafka topics visible to ksqlDB (name, partitions, replicas).	Cross-check streams with their backing Kafka topics. Detect orphaned topics vs. registered streams.
listQueries()	Lists all running queries (push/persistent, their sinks).	Capture which queries feed streams or depend on them. Links queries ↔ streams.
executeStatement("CREATE STREAM ...")	Creates new streams/tables.	Automate onboarding of new streams into the inventory. Store definition SQL.
executeStatement("DROP STREAM ...")	Drops existing streams/tables.	Update inventory to remove retired streams.
executeStatement("TERMINATE <queryId>")	Terminates persistent queries.	Update lineage inventory when a stream stops being populated.
serverInfo()	Gets cluster metadata (ksqlDB version, Kafka cluster ID, service ID).	Annotate stream inventory with cluster context (where the streams live).
assertTopic(name, props, exists)	Asserts topic existence (or non-existence).	Verify that inventory streams still have backing topics.
assertSchema(name, version, exists)	Asserts schema existence in Schema Registry.	Validate schema availability for streams in inventory.
define()/undefine()/getVariables()	Manage session variables for templated SQL.	Use variables to standardize inventory queries across clusters.
createConnector()/listConnectors()/describeConnector()	Manage connectors (sources/sinks).	Link streams in inventory to external data sources/sinks. Shows how data enters/leaves.
streamQuery()	Executes continuous (push) queries row-by-row.	Sample data from a stream to verify inventory entries are active.
executeQuery()	Executes queries returning results in batch (pull/terminated push).	Inspect current state of tables/materialized views for inventory consistency.


⸻

👉 For a stream inventory pipeline, the most relevant functions are:
	•	listStreams() → collect all stream names & metadata.
	•	describeSource() → enrich with schema, topic, lineage.
	•	listQueries() → track dependencies and population.
	•	listTopics() → cross-check Kafka topics behind streams.
	•	serverInfo() → cluster context (multi-cluster inventories).

⸻
