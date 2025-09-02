Inspect the enproc/todos

{{@manage_ksqldb_inventory_mapping_2_neo4j.md 1-748}}

{{@create_the_ksqldb_based_inventory.md 1-35}}

And plan the implementation of my new KSQLDBClusterInspection class.
It must load all relevant items from KSQLDBs REST API Using the Java Client for KSQLDB and create an in memory model of the entity data model.

Then, it must store an inventory as a yaml file as suggested.

After that, create an InventoryMapper to export my data to Neo4J for deeper analysis.

Please plan, document FR-x.md and SD-x.md files and also the ADR-x.md before implementing.