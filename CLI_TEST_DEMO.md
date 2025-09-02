# KS-Inspector CLI Testing Demonstration

## Current Status

The KS-Inspector CLI is a comprehensive tool built with **PicoCLI** framework that provides multiple commands for analyzing Kafka streaming topologies and KSQLDB deployments.

### Available Commands (Based on Code Analysis)

#### 1. `inspectDomain` 
**Purpose**: Inspect domain description from cluster context
- Reads application domain information including KSQLDB scripts
- Populates knowledge graph with domain facts
- Stores data in Kafka-based knowledge graph

**Usage**:
```bash
java -jar ks-inspector.jar inspectDomain \
  --bootstrap-server localhost:9092 \
  --working-path /path/to/working/dir \
  --env-var-prefix KST \
  /path/to/context/folder
```

#### 2. `inspectSchemaRegistry`
**Purpose**: Inspect and load Schema Registry information
- Connects to Schema Registry
- Collects schema metadata and evolution information
- Links schema fields to data catalog tags

**Usage**:
```bash
java -jar ks-inspector.jar inspectSchemaRegistry \
  --bootstrap-server localhost:9092 \
  --working-path /path/to/working/dir \
  --env-var-prefix KST \
  /path/to/context/folder
```

#### 3. `inspectCSV`
**Purpose**: Inspect domain description from CSV files
- Processes CSV files containing streaming topology information
- Projects CSV data into graph structure
- Uses default CSV processor for data flow analysis

**Usage**:
```bash
java -jar ks-inspector.jar inspectCSV \
  --working-path /path/to/csv/files \
  --file-name topology.csv \
  --bootstrap-server localhost:9092 \
  /path/to/context/folder
```

#### 4. `inspectCSV_EXT`
**Purpose**: Extended CSV inspection with external services and topics
- Enhanced CSV processing with multiple mappers
- Handles external services and topic relationships
- Processes complex topology relationships

**Usage**:
```bash
java -jar ks-inspector.jar inspectCSV_EXT \
  --working-path /path/to/csv/files \
  --file-name topology.csv \
  --bootstrap-server localhost:9092 \
  /path/to/context/folder
```

#### 5. `export2Neo4J`
**Purpose**: Export knowledge graph to Neo4j service
- Reads facts from Kafka topic
- Clears existing Neo4j graph data
- Populates Neo4j with streaming topology information

**Usage**:
```bash
java -jar ks-inspector.jar export2Neo4J \
  --bootstrap-server localhost:9092 \
  --working-path /path/to/working/dir \
  --env-var-prefix KST \
  /path/to/context/folder
```

#### 6. `queryGraph`
**Purpose**: Query the knowledge graph in Neo4j with predefined analysis queries
- Executes Cypher queries against Neo4j graph
- Supports custom query file paths
- Generates analysis results

**Usage**:
```bash
java -jar ks-inspector.jar queryGraph \
  --working-path ./src/main/cypher/cmd/ \
  --query-file-path q4.cypher \
  --env-var-prefix KST \
  /path/to/query/file.cypher
```

#### 7. `readEvents`
**Purpose**: Read JIRA ticket flow events into the knowledge graph
- Processes JIRA workflow events
- Integrates change management data with topology analysis
- Links operational events to streaming architecture

**Usage**:
```bash
java -jar ks-inspector.jar readEvents \
  --working-path /path/to/events \
  --query-file-path events.cypher \
  --env-var-prefix KST
```

#### 8. `clearGraph`
**Purpose**: Clear the knowledge graph in Neo4j service
- Performs housekeeping operations
- Removes all facts from Neo4j database
- Provides clean slate for new analysis

**Usage**:
```bash
java -jar ks-inspector.jar clearGraph \
  --bootstrap-server localhost:9092 \
  --working-path /path/to/working/dir \
  --env-var-prefix KST
```

## Current Build Issues

### Problem
The project has dependency resolution issues with Confluent's Maven repositories:
- `kafka-clusterstate-tools` library not accessible (403 Forbidden)
- Confluent platform libraries require proper repository configuration
- Build fails due to missing dependencies

### Dependencies Required
1. **kafka-clusterstate-tools**: External library for cluster state management
2. **KSQLDB API Client**: For KSQLDB server communication
3. **Neo4j Driver**: For graph database operations
4. **Confluent Platform Libraries**: For Kafka ecosystem integration

## Configuration Requirements

### Environment Variables
The tool uses environment variables with configurable prefix (typically `KST_`):
- `KST_BOOTSTRAP_SERVER`: Kafka cluster bootstrap servers
- `KST_SCHEMA_REGISTRY_URL`: Schema Registry endpoint
- `KST_NEO4J_URI`: Neo4j database URI
- `KST_NEO4J_USERNAME`: Neo4j username
- `KST_NEO4J_PASSWORD`: Neo4j password
- `KST_NEO4J_DATABASE_DIRECTORY`: Neo4j database directory
- `KST_NEO4J_DEFAULT_DATABASE_NAME`: Default Neo4j database name

### Configuration Files
- **kst.properties**: Main configuration file (located in context path)
- **Domain files**: Located in `src/main/cluster-state-tools-data/example*/domains/`
- **Cypher queries**: Located in `src/main/cypher/cmd/`

## Testing Strategy (When Dependencies Resolved)

### 1. Basic Help Test
```bash
java -jar target/ks-inspector-1.0-SNAPSHOT.jar --help
```

### 2. Command-Specific Help
```bash
java -jar target/ks-inspector-1.0-SNAPSHOT.jar inspectDomain --help
```

### 3. Domain Inspection Test
```bash
java -jar target/ks-inspector-1.0-SNAPSHOT.jar inspectDomain \
  --bootstrap-server localhost:9092 \
  --env-var-prefix KST \
  src/main/cluster-state-tools-data/example0/domains
```

### 4. Neo4j Export Test
```bash
java -jar target/ks-inspector-1.0-SNAPSHOT.jar export2Neo4J \
  --bootstrap-server localhost:9092 \
  --env-var-prefix KST
```

### 5. Graph Query Test
```bash
java -jar target/ks-inspector-1.0-SNAPSHOT.jar queryGraph \
  --query-file-path ./src/main/cypher/cmd/q4.cypher \
  --env-var-prefix KST
```

## Next Steps for Full Testing

### 1. Resolve Dependencies
- Set up proper Confluent Maven repositories
- Build/install `kafka-clusterstate-tools` library
- Configure authentication for Confluent repositories

### 2. Environment Setup
- Install and configure Neo4j database
- Set up Kafka cluster (local or cloud)
- Configure Schema Registry (if testing schema features)

### 3. Create Test Data
- Prepare sample domain configuration files
- Create test CSV files for topology analysis
- Set up sample KSQLDB queries and streams

### 4. Integration Testing
- Test end-to-end workflow: inspect → export → query
- Validate Neo4j graph structure and relationships
- Verify analysis query results

## Architecture Insights from CLI Analysis

### Design Patterns Used
- **Command Pattern**: Each CLI command is a separate method with PicoCLI annotations
- **Factory Pattern**: `KnowledgeGraphFactory` creates different graph implementations
- **Strategy Pattern**: Multiple CSV processors for different data sources
- **Dependency Injection**: Uses properties-based configuration

### Current Capabilities
- Multi-format data ingestion (CSV, domain files, Schema Registry)
- Multiple graph storage backends (Kafka topics, Neo4j)
- Flexible configuration via environment variables
- Batch processing with incremental updates
- Integration with JIRA for change management tracking

### Extension Points
- Custom CSV processors can be plugged in
- New graph storage backends can be added via `IKnowledgeGraph` interface
- Analysis queries can be customized via Cypher files
- Environment variable prefixes allow multi-tenant configurations