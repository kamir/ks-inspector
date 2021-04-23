#
# REQUIRES a topic ... (use KST ;-)
#
CREATE TABLE regionTable ( rowkey VARCHAR PRIMARY KEY,
     type STRING,
     id INTEGER,
     name STRING,
     lat DOUBLE,
     lon DOUBLE,
     country STRING,
     production DOUBLE,
     consumption DOUBLE,
     imports DOUBLE,
     exports DOUBLE
) WITH (KAFKA_TOPIC='grid-regions', VALUE_FORMAT='JSON');

#
# Creates the topic in case it is not available
#
#   Try it out with REPLICAS='1'
#
CREATE TABLE regionTable ( rowkey VARCHAR PRIMARY KEY,
                           type STRING,
                           id INTEGER,
                           name STRING,
                           lat DOUBLE,
                           lon DOUBLE,
                           country STRING,
                           production DOUBLE,
                           consumption DOUBLE,
                           imports DOUBLE,
                           exports DOUBLE
) WITH (KAFKA_TOPIC='grid-regions', VALUE_FORMAT='JSON', REPLICAS='3', PARTITIONS='1');





CREATE TABLE stationTable (
         rowkey VARCHAR PRIMARY KEY,
         type STRING,
         id STRING,
         name STRING,
         lat DOUBLE,
         lon DOUBLE,
         country STRING
    ) WITH (KAFKA_TOPIC='grid-stations',
         VALUE_FORMAT = 'JSON', REPLICAS='3', PARTITIONS='1'
    );

CREATE TABLE plantsTable (
     rowkey VARCHAR PRIMARY KEY,
     type STRING,
     id STRING,
     name STRING,
     lat DOUBLE,
     lon DOUBLE,
     country STRING
    ) WITH (KAFKA_TOPIC='grid-plants',
        VALUE_FORMAT = 'JSON', REPLICAS='3', PARTITIONS='1'
    );

CREATE TABLE linkTable (
     rowkey VARCHAR PRIMARY KEY,
     id STRING,
     regionContextTag STRING,
     asGeoJSON STRING
    ) WITH (KAFKA_TOPIC='grid-static-links',
        VALUE_FORMAT = 'JSON', REPLICAS='3', PARTITIONS='1'
    );

CREATE STREAM gridLinkFlowDataStream (
     rowkey VARCHAR KEY,
     ts BIGINT,
     flow DOUBLE
    ) WITH (KAFKA_TOPIC='grid-link-flow-data',
        VALUE_FORMAT = 'JSON', REPLICAS='3', PARTITIONS='1'
    );

CREATE STREAM gridflowEnrichedSampleStream2
WITH (KAFKA_TOPIC='grid-flow-enriched-sample-stream2',
     VALUE_FORMAT = 'JSON',
     PARTITIONS = 1
)
AS SELECT
 r.rowkey as rowkey,
 r.ts,
 r.flow,
 l.id,
 l.regionContextTag,
 l.asGeoJSON,
 SPLIT(l.regionContextTag, '->')[1] AS source,
 SPLIT(l.regionContextTag, '->')[2] AS target
FROM gridLinkFlowDataStream r
  JOIN linkTable l ON r.rowkey = l.rowkey
EMIT CHANGES;

Select * from gridflowEnrichedSampleStream2 where source != target emit changes;

CREATE TABLE crossRegionFlows
WITH (KAFKA_TOPIC='grid-cross-region-flows', VALUE_FORMAT='JSON', PARTITIONS=1)
as Select REGIONCONTEXTTAG, sum(flow) from gridflowEnrichedSampleStream2
where source != target
group by REGIONCONTEXTTAG;

#=======================================================================================================================
#
# NOT WORKING !!!
#
CREATE STREAM crossRegionFlowStream
WITH (KAFKA_TOPIC='grid-cross-region-flow-stream', VALUE_FORMAT = 'JSON', PARTITIONS = 1)
as Select REGIONCONTEXTTAG, sum(flow) from gridflowEnrichedSampleStream2
where source != target
group by REGIONCONTEXTTAG
emit changes;
#=======================================================================================================================

CREATE TABLE crossRegionFlowStream3
WITH (KAFKA_TOPIC='grid-cross-region-flow3', VALUE_FORMAT = 'JSON', PARTITIONS = 1)
AS Select
 REGIONCONTEXTTAG, sum(flow) as cross_region_flow,
 TOPK(SOURCE,1)[1] as source_region,
 TOPK(TARGET,1)[1] as target_region,
 TOPK(ASGEOJSON,1)[1] as GEOJSON
from gridflowEnrichedSampleStream2
where source != target
group by REGIONCONTEXTTAG
emit changes;

Select * from crossRegionFlowStream3 emit changes;

CREATE TABLE crossRegionFlowStream4
WITH (KAFKA_TOPIC='grid-cross-region-flow4', VALUE_FORMAT = 'JSON', PARTITIONS = 1)
AS Select
  REGIONCONTEXTTAG, sum(flow) as cross_region_flow,
  TOPK(SOURCE,1)[1] as source_region,
  TOPK(TARGET,1)[1] as target_region,
  TOPK(ASGEOJSON,1)[1] as GEOJSON
from gridflowEnrichedSampleStream2
WINDOW TUMBLING (SIZE 1 MINUTE)
where source != target
group by REGIONCONTEXTTAG
emit changes;

Select * from crossRegionFlowStream4 emit changes;


