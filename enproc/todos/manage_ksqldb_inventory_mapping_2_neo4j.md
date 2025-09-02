Great thinking — what you’re describing is essentially an application-context inventory that ties together Kafka topics, schemas, and ksqlDB objects, and then projects them into a graph model for querying and visualization (Neo4j is a perfect choice).

Here’s how I’d structure it:

⸻

Inventory Structure for Application Context

We’ll capture two levels of data:
	1.	Entity Inventory (JSON/YAML file) – canonical record of all objects.
	2.	Graph Mapping (Neo4j model) – nodes + relationships derived from inventory.

⸻

1. Entity Inventory (YAML Example)

use_case: "fraud-detection-risks"
cluster: "confluent-cloud-prod"
collected_at: "2025-09-02T12:00:00Z"

topics:
  - name: "transactions"
    properties_on_creation:
      partitions: 8
      replication: 3
      config:
        cleanup.policy: compact
        retention.ms: 604800000
    current_properties:
      partitions: 12
      replication: 3
      config:
        cleanup.policy: compact
        retention.ms: 1209600000
    partitions:
      - id: 0
        leader: broker-1
        replicas: [broker-1, broker-2, broker-3]
        isr: [broker-1, broker-2, broker-3]
    partition_logic:
      producing_apps: ["payments-service"]
      consuming_apps: ["fraud-detector"]
    schema:
      current: "transactions-value-v3"
      history: ["transactions-value-v1", "transactions-value-v2"]
    flow_metrics:
      records_in_per_sec: 1523.4
      records_out_per_sec: 1477.9
      byte_rate: 2.4MB/s

ksqldb:
  - service: "fraud-ksqldb-cluster"
    version: "0.29.0"
    queries:
      - id: "CTAS_FRAUD_TABLE_0"
        sql: "CREATE TABLE fraud_alerts AS SELECT ...;"
        state: "RUNNING"
        processing_metrics:
          input_msgs_per_sec: 122.0
          output_msgs_per_sec: 15.5
    streams:
      - name: "transactions_stream"
        topic: "transactions"
        format: "AVRO"
        schema: "transactions-value-v3"
      - name: "fraud_alerts_stream"
        topic: "fraud-alerts"
        format: "JSON"
    tables:
      - name: "fraud_alerts"
        topic: "fraud-alerts"
        key_format: "STRING"
        value_format: "JSON"
        queries: ["CTAS_FRAUD_TABLE_0"]


⸻

2. Graph Mapping for Neo4j

Node Labels:
	•	:UseCase {name}
	•	:Topic {name, creation_props, current_props}
	•	:Partition {id, leader, replicas}
	•	:Schema {name, version, format}
	•	:KsqlService {name, version}
	•	:Query {id, sql, state}
	•	:Stream {name, format}
	•	:Table {name, format}
	•	:App {name, type}

Relationships:
	•	(:UseCase)-[:HAS_TOPIC]->(:Topic)
	•	(:Topic)-[:HAS_PARTITION]->(:Partition)
	•	(:Topic)-[:USES_SCHEMA]->(:Schema)
	•	(:Schema)-[:HAS_HISTORY]->(:Schema)
	•	(:App)-[:PRODUCES]->(:Topic)
	•	(:App)-[:CONSUMES]->(:Topic)
	•	(:KsqlService)-[:RUNS_QUERY]->(:Query)
	•	(:Query)-[:CREATES_STREAM]->(:Stream)
	•	(:Query)-[:CREATES_TABLE]->(:Table)
	•	(:Stream)-[:BACKED_BY]->(:Topic)
	•	(:Table)-[:BACKED_BY]->(:Topic)

⸻

Example Graph Queries
	•	Find all topics with schema version drift:

MATCH (t:Topic)-[:USES_SCHEMA]->(s:Schema)
WHERE size((s)<-[:HAS_HISTORY]-()) > 0
RETURN t.name, s.name, collect(s) as schema_history

	•	Trace lineage from producer app → topic → stream → table:

MATCH (a:App)-[:PRODUCES]->(t:Topic)<-[:BACKED_BY]-(s:Stream)<-[:CREATES_STREAM]-(q:Query)
RETURN a.name, t.name, s.name, q.id

	•	Get all streams/tables in a use case:

MATCH (u:UseCase {name:"fraud-detection-risks"})-[:HAS_TOPIC]->(t:Topic)<-[:BACKED_BY]-(s)
RETURN u.name, t.name, s.name, labels(s)


⸻

✅ This design lets you keep a versioned inventory file for offline tracking and audits, while mapping everything into Neo4j for interactive lineage exploration, impact analysis, and monitoring.


---------

We export the inventory in "MEMORY-Model" into YAML format.

---------

Next we export into Neo4J.

---------

Absolutely—here’s a Java (Maven) version of the inventory→Neo4j mapper. It reads your inventory YAML and upserts a clean, idempotent graph in Neo4j using the official Java driver. It mirrors the structure we discussed (Topics, Schemas & history, ksqlDB Streams/Tables/Queries, Apps, and relationships).

⸻

🧩 Java Neo4j Inventory Mapper

1) pom.xml dependencies

Add these to your project:

<dependencies>
  <!-- Neo4j Java Driver -->
  <dependency>
    <groupId>org.neo4j.driver</groupId>
    <artifactId>neo4j-java-driver</artifactId>
    <version>5.23.0</version>
  </dependency>

  <!-- YAML parsing -->
  <dependency>
    <groupId>org.yaml</groupId>
    <artifactId>snakeyaml</artifactId>
    <version>2.2</version>
  </dependency>

  <!-- JSON serialization (for nested blobs to store as strings) -->
  <dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.17.1</version>
  </dependency>
</dependencies>


⸻

2) Inventory format (example)

Save as inventory.yaml (adapt as needed):

use_case:
  name: "RFP-Risk-Factors-Analysis"
  cluster: "ccloud-euc1"
  collected_at: "2025-09-01T12:00:00Z"

topics:
  - name: "risk-factors.raw"
    creation_properties:
      partitions: 6
      replication_factor: 3
      cleanup.policy: "delete"
    current_properties:
      cleanup.policy: "compact"
      min.insync.replicas: 2
    partitions:
      - id: 0
      - id: 1
    schema:
      subject: "risk-factors-value"
      version: 7
      id: 1024
      type: "AVRO"
      compatibility: "BACKWARD"
      history:
        - { version: 5, id: 900, type: "AVRO" }
        - { version: 6, id: 950, type: "AVRO" }
    flow_metrics:
      bytes_in_rate: 12345.6
      bytes_out_rate: 4567.8

  - name: "risk-factors.cleaned"
    creation_properties:
      partitions: 3
      replication_factor: 3
      cleanup.policy: "compact"
    current_properties: {}
    schema:
      subject: "risk-factors-cleaned-value"
      version: 2
      id: 1101
      type: "JSON"

ksqldb:
  cluster:
    id: "ksqldb-euc1-01"
    endpoint: "https://xyz.eu-central-1.aws.confluent.cloud"
  queries:
    - id: "CTAS_RISK_FACTORS_001"
      sql: "CREATE STREAM RISK_FACTORS_CLEAN AS SELECT * FROM RISK_FACTORS_RAW EMIT CHANGES;"
      status: "RUNNING"
  streams:
    - name: "RISK_FACTORS_RAW"
      topic: "risk-factors.raw"
      key_format: "KAFKA"
      value_format: "AVRO"
    - name: "RISK_FACTORS_CLEAN"
      topic: "risk-factors.cleaned"
      key_format: "KAFKA"
      value_format: "JSON"
  tables:
    - name: "RISK_FACTORS_SUMMARY"
      topic: "risk-factors.summary"
      key_format: "KAFKA"
      value_format: "JSON"

apps:
  - name: "risk-ingestor"
    produces:
      - topic: "risk-factors.raw"
        partitioner: "murmur3-key"
    consumes: []
  - name: "risk-api"
    consumes:
      - topic: "risk-factors.cleaned"
        group_id: "risk-api-v1"
    produces: []


⸻

3) Mapper code — src/main/java/com/scalytics/inventory/InventoryMapper.java

package com.scalytics.inventory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.neo4j.driver.*;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static org.neo4j.driver.Values.parameters;

/**
 * Inventory → Neo4j mapper (idempotent)
 *
 * Usage:
 *   java -jar mapper.jar \
 *     --neo4j-uri bolt://localhost:7687 \
 *     --neo4j-user neo4j \
 *     --neo4j-pass admin \
 *     --inventory ./inventory.yaml
 */
public class InventoryMapper implements AutoCloseable {

    private final Driver driver;
    private final ObjectMapper om = new ObjectMapper().enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS);

    public InventoryMapper(String uri, String user, String pass) {
        this.driver = GraphDatabase.driver(uri, AuthTokens.basic(user, pass),
                Config.builder().withEncryption().disabled().build());
    }

    @Override
    public void close() {
        driver.close();
    }

    private String jdump(Object obj) {
        try {
            if (obj == null) return "{}";
            return om.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("JSON serialization failed", e);
        }
    }

    // ---------- Cypher helpers ----------

    private static final String MERGE_USECASE = """
        MERGE (u:UseCase {name: $name})
        ON CREATE SET u.createdAt = datetime()
        SET u.cluster = $cluster,
            u.collected_at = $collected_at
        """;

    private static final String MERGE_TOPIC = """
        MERGE (t:Topic {name: $name})
        ON CREATE SET t.createdAt = datetime()
        SET t.creation_properties_json = $creation_props,
            t.current_properties_json  = $current_props
        """;

    private static final String REL_USECASE_HAS_TOPIC = """
        MATCH (u:UseCase {name:$usecase}), (t:Topic {name:$topic})
        MERGE (u)-[r:USES_TOPIC]->(t)
        """;

    private static final String MERGE_PARTITION = """
        MATCH (t:Topic {name:$topic})
        MERGE (p:Partition {topic:$topic, id:$pid})
        ON CREATE SET p.createdAt = datetime()
        MERGE (t)-[:HAS_PARTITION]->(p)
        """;

    private static final String MERGE_SCHEMA_SUBJECT = """
        MERGE (s:SchemaSubject {subject:$subject})
        ON CREATE SET s.createdAt = datetime()
        SET s.compatibility = $compatibility
        """;

    private static final String MERGE_SCHEMA_VERSION = """
        MATCH (s:SchemaSubject {subject:$subject})
        MERGE (sv:SchemaVersion {subject:$subject, version:$version})
        ON CREATE SET sv.createdAt = datetime()
        SET sv.id = $id,
            sv.type = $type
        MERGE (s)-[:HAS_VERSION]->(sv)
        """;

    private static final String REL_TOPIC_CURRENT_SCHEMA = """
        MATCH (t:Topic {name:$topic})
        MATCH (sv:SchemaVersion {subject:$subject, version:$version})
        MERGE (t)-[r:USES_SCHEMA]->(sv)
        SET r.current=true
        """;

    private static final String REL_TOPIC_SCHEMA_HISTORY = """
        MATCH (t:Topic {name:$topic})
        MATCH (sv:SchemaVersion {subject:$subject, version:$version})
        MERGE (t)-[:USED_SCHEMA_HISTORY]->(sv)
        """;

    private static final String MERGE_KSQL_CLUSTER = """
        MERGE (k:KSQLDBCluster {id:$id})
        ON CREATE SET k.createdAt = datetime()
        SET k.endpoint = $endpoint
        """;

    private static final String MERGE_KSQL_STREAM = """
        MERGE (s:KSQLStream {name:$name})
        ON CREATE SET s.createdAt = datetime()
        SET s.key_format = $key_format,
            s.value_format = $value_format
        """;

    private static final String MERGE_KSQL_TABLE = """
        MERGE (t:KSQLTable {name:$name})
        ON CREATE SET t.createdAt = datetime()
        SET t.key_format = $key_format,
            t.value_format = $value_format
        """;

    private static final String REL_STREAM_BACKED_BY_TOPIC = """
        MATCH (s:KSQLStream {name:$stream}), (t:Topic {name:$topic})
        MERGE (s)-[:BACKED_BY_TOPIC]->(t)
        """;

    private static final String REL_TABLE_BACKED_BY_TOPIC = """
        MATCH (t:KSQLTable {name:$table}), (topic:Topic {name:$topic})
        MERGE (t)-[:BACKED_BY_TOPIC]->(topic)
        """;

    private static final String MERGE_KSQL_QUERY = """
        MERGE (q:KSQLQuery {id:$id})
        ON CREATE SET q.createdAt = datetime()
        SET q.sql = $sql, q.status = $status
        """;

    private static final String REL_CLUSTER_HAS_QUERY = """
        MATCH (k:KSQLDBCluster {id:$clusterId}), (q:KSQLQuery {id:$queryId})
        MERGE (k)-[:HAS_QUERY]->(q)
        """;

    private static final String MERGE_APP = """
        MERGE (a:App {name:$name})
        ON CREATE SET a.createdAt = datetime()
        """;

    private static final String REL_PRODUCES_TO = """
        MATCH (a:App {name:$app}), (t:Topic {name:$topic})
        MERGE (a)-[r:PRODUCES_TO]->(t)
        SET r.partitioner = $partitioner
        """;

    private static final String REL_CONSUMES_FROM = """
        MATCH (a:App {name:$app}), (t:Topic {name:$topic})
        MERGE (a)-[r:CONSUMES_FROM]->(t)
        SET r.group_id = $group_id
        """;

    private static final String SET_TOPIC_FLOW_METRICS = """
        MATCH (t:Topic {name:$topic})
        SET t.flow_metrics_json = $metrics
        """;

    // ---------- Mapping ----------

    public void map(Path yamlPath) throws IOException {
        String content = Files.readString(yamlPath);
        Map<String, Object> root = new Yaml().load(content);
        if (root == null) {
            throw new IllegalArgumentException("Empty YAML");
        }

        try (Session session = driver.session(SessionConfig.forDatabase("neo4j"))) {
            session.writeTransaction(tx -> {
                // UseCase
                Map<String, Object> uc = asMap(root.get("use_case"));
                if (uc != null) {
                    tx.run(MERGE_USECASE, parameters(
                            "name", str(uc, "name"),
                            "cluster", str(uc, "cluster"),
                            "collected_at", str(uc, "collected_at")
                    ));
                }
                final String usecaseName = uc != null ? str(uc, "name") : null;

                // Topics
                List<Map<String, Object>> topics = asListOfMap(root.get("topics"));
                if (topics != null) {
                    for (Map<String, Object> t : topics) {
                        String tName = str(t, "name");
                        Map<String, Object> creationProps = asMap(t.get("creation_properties"));
                        Map<String, Object> currentProps  = asMap(t.get("current_properties"));

                        tx.run(MERGE_TOPIC, parameters(
                                "name", tName,
                                "creation_props", jdump(creationProps),
                                "current_props",  jdump(currentProps)
                        ));
                        if (usecaseName != null) {
                            tx.run(REL_USECASE_HAS_TOPIC, parameters(
                                    "usecase", usecaseName, "topic", tName
                            ));
                        }

                        // Partitions
                        List<Map<String, Object>> parts = asListOfMap(t.get("partitions"));
                        if (parts != null) {
                            for (Map<String, Object> p : parts) {
                                Integer pid = asInt(p.get("id"));
                                if (pid != null) {
                                    tx.run(MERGE_PARTITION, parameters(
                                            "topic", tName, "pid", pid
                                    ));
                                }
                            }
                        }

                        // Schema (current + history)
                        Map<String, Object> schema = asMap(t.get("schema"));
                        if (schema != null) {
                            String subject = str(schema, "subject");
                            tx.run(MERGE_SCHEMA_SUBJECT, parameters(
                                    "subject", subject,
                                    "compatibility", str(schema, "compatibility")
                            ));
                            Integer ver = asInt(schema.get("version"));
                            Integer id = asInt(schema.get("id"));
                            tx.run(MERGE_SCHEMA_VERSION, parameters(
                                    "subject", subject, "version", ver, "id", id, "type", str(schema, "type")
                            ));
                            tx.run(REL_TOPIC_CURRENT_SCHEMA, parameters(
                                    "topic", tName, "subject", subject, "version", ver
                            ));

                            List<Map<String, Object>> hist = asListOfMap(schema.get("history"));
                            if (hist != null) {
                                for (Map<String, Object> hv : hist) {
                                    tx.run(MERGE_SCHEMA_VERSION, parameters(
                                            "subject", subject,
                                            "version", asInt(hv.get("version")),
                                            "id", asInt(hv.get("id")),
                                            "type", str(hv, "type")
                                    ));
                                    tx.run(REL_TOPIC_SCHEMA_HISTORY, parameters(
                                            "topic", tName,
                                            "subject", subject,
                                            "version", asInt(hv.get("version"))
                                    ));
                                }
                            }
                        }

                        // Flow metrics (store blob)
                        Map<String, Object> metrics = asMap(t.get("flow_metrics"));
                        if (metrics != null) {
                            tx.run(SET_TOPIC_FLOW_METRICS, parameters(
                                    "topic", tName,
                                    "metrics", jdump(metrics)
                            ));
                        }
                    }
                }

                // ksqlDB
                Map<String, Object> ksql = asMap(root.get("ksqldb"));
                if (ksql != null) {
                    Map<String, Object> kc = asMap(ksql.get("cluster"));
                    if (kc != null) {
                        tx.run(MERGE_KSQL_CLUSTER, parameters(
                                "id", str(kc, "id"),
                                "endpoint", str(kc, "endpoint")
                        ));
                    }
                    String clusterId = kc != null ? str(kc, "id") : null;

                    List<Map<String, Object>> streams = asListOfMap(ksql.get("streams"));
                    if (streams != null) {
                        for (Map<String, Object> s : streams) {
                            tx.run(MERGE_KSQL_STREAM, parameters(
                                    "name", str(s, "name"),
                                    "key_format", str(s, "key_format"),
                                    "value_format", str(s, "value_format")
                            ));
                            String topic = str(s, "topic");
                            if (topic != null) {
                                tx.run(REL_STREAM_BACKED_BY_TOPIC, parameters(
                                        "stream", str(s, "name"), "topic", topic
                                ));
                            }
                        }
                    }

                    List<Map<String, Object>> tables = asListOfMap(ksql.get("tables"));
                    if (tables != null) {
                        for (Map<String, Object> t : tables) {
                            tx.run(MERGE_KSQL_TABLE, parameters(
                                    "name", str(t, "name"),
                                    "key_format", str(t, "key_format"),
                                    "value_format", str(t, "value_format")
                            ));
                            String topic = str(t, "topic");
                            if (topic != null) {
                                tx.run(REL_TABLE_BACKED_BY_TOPIC, parameters(
                                        "table", str(t, "name"), "topic", topic
                                ));
                            }
                        }
                    }

                    List<Map<String, Object>> queries = asListOfMap(ksql.get("queries"));
                    if (queries != null) {
                        for (Map<String, Object> q : queries) {
                            String qid = str(q, "id");
                            tx.run(MERGE_KSQL_QUERY, parameters(
                                    "id", qid,
                                    "sql", str(q, "sql"),
                                    "status", str(q, "status")
                            ));
                            if (clusterId != null) {
                                tx.run(REL_CLUSTER_HAS_QUERY, parameters(
                                        "clusterId", clusterId, "queryId", qid
                                ));
                            }
                        }
                    }
                }

                // Apps
                List<Map<String, Object>> apps = asListOfMap(root.get("apps"));
                if (apps != null) {
                    for (Map<String, Object> app : apps) {
                        String appName = str(app, "name");
                        tx.run(MERGE_APP, parameters("name", appName));

                        List<Map<String, Object>> produces = asListOfMap(app.get("produces"));
                        if (produces != null) {
                            for (Map<String, Object> pr : produces) {
                                tx.run(REL_PRODUCES_TO, parameters(
                                        "app", appName,
                                        "topic", str(pr, "topic"),
                                        "partitioner", str(pr, "partitioner")
                                ));
                            }
                        }
                        List<Map<String, Object>> consumes = asListOfMap(app.get("consumes"));
                        if (consumes != null) {
                            for (Map<String, Object> co : consumes) {
                                tx.run(REL_CONSUMES_FROM, parameters(
                                        "app", appName,
                                        "topic", str(co, "topic"),
                                        "group_id", str(co, "group_id")
                                ));
                            }
                        }
                    }
                }

                return null;
            });
        }
    }

    // ---------- Small parsing helpers ----------

    @SuppressWarnings("unchecked")
    private static Map<String, Object> asMap(Object o) {
        if (o == null) return null;
        if (o instanceof Map<?, ?> m) {
            Map<String, Object> out = new LinkedHashMap<>();
            for (Map.Entry<?, ?> e : m.entrySet()) {
                out.put(String.valueOf(e.getKey()), e.getValue());
            }
            return out;
        }
        throw new IllegalArgumentException("Expected map, got: " + o.getClass());
    }

    @SuppressWarnings("unchecked")
    private static List<Map<String, Object>> asListOfMap(Object o) {
        if (o == null) return null;
        if (o instanceof List<?> l) {
            List<Map<String, Object>> out = new ArrayList<>();
            for (Object item : l) {
                if (item == null) continue;
                if (item instanceof Map<?, ?>) out.add(asMap(item));
                else throw new IllegalArgumentException("Expected list of maps");
            }
            return out;
        }
        throw new IllegalArgumentException("Expected list, got: " + o.getClass());
    }

    private static String str(Map<String, Object> m, String key) {
        Object v = m.get(key);
        return v == null ? null : String.valueOf(v);
    }

    private static Integer asInt(Object v) {
        if (v == null) return null;
        if (v instanceof Number n) return n.intValue();
        return Integer.parseInt(String.valueOf(v));
    }

    // ---------- Main ----------

    public static void main(String[] args) throws Exception {
        Map<String, String> arg = parseArgs(args);
        String uri = arg.getOrDefault("neo4j-uri", "bolt://localhost:7687");
        String user = arg.getOrDefault("neo4j-user", "neo4j");
        String pass = arg.getOrDefault("neo4j-pass", "admin");
        Path inv  = Path.of(arg.getOrDefault("inventory", "inventory.yaml"));

        try (InventoryMapper m = new InventoryMapper(uri, user, pass)) {
            System.out.println("→ Mapping inventory: " + inv.toAbsolutePath());
            m.map(inv);
            System.out.println("✓ Done.");
        }
    }

    private static Map<String, String> parseArgs(String[] args) {
        Map<String, String> out = new HashMap<>();
        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("--") && i + 1 < args.length) {
                out.put(args[i].substring(2), args[i + 1]);
                i++;
            }
        }
        return out;
    }
}


⸻

4) Run it

mvn -q -DskipTests package
java -jar target/<your-jar>.jar \
  --neo4j-uri bolt://localhost:7687 \
  --neo4j-user neo4j \
  --neo4j-pass admin \
  --inventory ./inventory.yaml


⸻

5) What you’ll see in Neo4j

Nodes
	•	(:UseCase {name, cluster, collected_at})
	•	(:Topic {name, creation_properties_json, current_properties_json, flow_metrics_json})
	•	(:Partition {topic, id})
	•	(:SchemaSubject {subject, compatibility})
	•	(:SchemaVersion {subject, version, id, type})
	•	(:KSQLDBCluster {id, endpoint})
	•	(:KSQLStream {name, key_format, value_format})
	•	(:KSQLTable {name, key_format, value_format})
	•	(:KSQLQuery {id, sql, status})
	•	(:App {name})

Relationships
	•	(UseCase)-[:USES_TOPIC]->(Topic)
	•	(Topic)-[:HAS_PARTITION]->(Partition)
	•	(SchemaSubject)-[:HAS_VERSION]->(SchemaVersion)
	•	(Topic)-[:USES_SCHEMA {current:true}]->(SchemaVersion)
	•	(Topic)-[:USED_SCHEMA_HISTORY]->(SchemaVersion)
	•	(KSQLDBCluster)-[:HAS_QUERY]->(KSQLQuery)
	•	(KSQLStream)-[:BACKED_BY_TOPIC]->(Topic)
	•	(KSQLTable)-[:BACKED_BY_TOPIC]->(Topic)
	•	(App)-[:PRODUCES_TO {partitioner}]->(Topic)
	•	(App)-[:CONSUMES_FROM {group_id}]->(Topic)

Everything is idempotent (safe reruns), nested blobs are kept as JSON strings on nodes for fidelity, and you can evolve the schema over time without breaking merges.

If you want me to add:
	•	Query→Stream/Table derivation edges,
	•	Metrics as time-versioned nodes (e.g., (:MetricSnapshot)),
	•	Or constraints/indexes (e.g., CREATE CONSTRAINT ... IF NOT EXISTS),
say the word and I’ll extend the mapper.



