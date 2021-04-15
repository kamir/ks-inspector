cd ..

docker run \
    --name neo4jlocal \
    -p7474:7474 -p7687:7687 \
    -d \
    -v $(pwd)/graphdb/neo4j/data:/data \
    -v $(pwd)/graphdb/neo4j/logs:/logs \
    -v $(pwd)/graphdb/neo4j/import:/var/lib/neo4j/import \
    -v $(pwd)/graphdb/neo4j/plugins:/plugins \
    -e NEO4J_AUTH=neo4j/test \
    neo4j:latest
