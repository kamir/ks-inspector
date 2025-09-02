# Quick Start Guide

Get up and running with ks-inspector in just a few steps.

## Prerequisites

- **Java 21** - The modernized version requires Java 21
- **Maven 3.6+** - For building and running the project
- **Kafka Cluster** - Access to a Kafka cluster (local or remote)
- **KSQL Server** - Access to a KSQL server REST API
- **Neo4j** (Optional) - For advanced graph querying and visualization

## Installation

1. **Clone the repository:**
   ```bash
   git clone https://github.com/kamir/ks-inspector.git
   cd ks-inspector
   ```

2. **Build the project:**
   ```bash
   mvn clean package
   ```

## Quick Run with Example Data

The project includes example data to get you started quickly:

```bash
mvn exec:java -Pp10-1
```

This runs the `inspectDomain` profile which analyzes domain descriptions from cluster context using example data.

## Running with Your Own Data

### Method 1: Using Maven Profiles (Recommended)

1. **Create a custom profile** in `pom.xml`:
   ```xml
   <profile>
       <id>my-analysis</id>
       <properties>
           <maven.test.skip>true</maven.test.skip>
           <argument0>inspectDomain</argument0>
           <argument1k>-wp</argument1k>
           <argument1v>/path/to/your/data</argument1v>
           <argument2k>-bss</argument2k>
           <argument2v>localhost:9092</argument2v>
           <argument3k>-e</argument3k>
           <argument3v>KST</argument3v>
           <argument4>./src/main/cluster-state-tools-data/example10</argument4>
       </properties>
   </profile>
   ```

2. **Run with your profile:**
   ```bash
   mvn exec:java -Pmy-analysis
   ```

### Method 2: Command Line Arguments

Run directly with arguments:
```bash
mvn exec:java -Dexec.mainClass="io.confluent.mdgraph.cli.CLI" -Dexec.args="inspectDomain -wp /path/to/your/data -bss localhost:9092 -e KST ./src/main/cluster-state-tools-data/example10"
```

## Key Commands

### Analyze Domain Description
```bash
# Profile: p10-1
mvn exec:java -Pp10-1
```

### Inspect Schema Registry
```bash
# Profile: p10-2
mvn exec:java -Pp10-2
```

### Export to Neo4J
```bash
# Profile: p11
mvn exec:java -Pp11
```

### Run Named Query
```bash
# Profile: p12
mvn exec:java -Pp12
```

## Understanding the Output

### Dependency Graphs
The tool generates dependency graphs as `.dot` files in the `insights/` directory.

### Render PDF Graphs
Convert `.dot` files to PDF using Graphviz:
```bash
dot -Tpdf insights/opentsx.ksql.dot -o pdf/opentsx.pdf
```

### Neo4j Integration
For advanced querying, export data to Neo4j:
```bash
# Ensure Neo4j is running, then use profile p11
mvn exec:java -Pp11
```

## Project Structure

- `src/main/java/` - Source code
- `src/main/resources/` - Configuration files
- `src/main/cluster-state-tools-data/` - Example data
- `insights/` - Generated dependency graphs (.dot files)
- `pdf/` - Rendered PDF graphs
- `ksqldb-query-stage/` - KSQL query files

## Troubleshooting

### Java Version Issues
Ensure you're using Java 21:
```bash
java -version
export JAVA_HOME=/path/to/java21
```

### Missing Dependencies
If you encounter dependency issues, try:
```bash
mvn clean install -U
```

### KSQL Server Connection
Ensure your KSQL server is accessible:
```bash
curl -X "POST" "http://localhost:8088/ksql" \
     -H "Content-Type: application/vnd.ksql.v1+json; charset=utf-8" \
     -d '{"ksql": "SHOW STREAMS;", "streamsProperties": {}}'
```

## Next Steps

1. Explore the example profiles in `pom.xml`
2. Review the domain model in `io.confluent.ks.modern.model`
3. Check the CLI implementation in `io.confluent.mdgraph.cli.CLI`
4. Examine example data in `src/main/cluster-state-tools-data/`

For detailed documentation, see the [README.md](README.md) file.