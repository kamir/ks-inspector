#############################################################################################
#
# This script contains all queries needed for the second PoC for ENTSO-E .
#
#############################################################################################

-- table with location data about regions (region centers):
CREATE TABLE regionTable (
     rowkey VARCHAR PRIMARY KEY,
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
) WITH (
     KAFKA_TOPIC = 'grid-regions',
     VALUE_FORMAT = 'JSON'
);

-- table with location data about stations:
    CREATE TABLE stationTable (
         rowkey VARCHAR PRIMARY KEY,
         type STRING,
         id STRING,
         name STRING,
         lat DOUBLE,
         lon DOUBLE,
         country STRING
    ) WITH (
         KAFKA_TOPIC = 'grid-stations',
         VALUE_FORMAT = 'JSON'
    );

-- table with location data about powerPlants:
CREATE TABLE plantsTable (
     rowkey VARCHAR PRIMARY KEY,
     type STRING,
     id STRING,
     name STRING,
     lat DOUBLE,
     lon DOUBLE,
     country STRING
) WITH (
     KAFKA_TOPIC = 'grid-plants',
     VALUE_FORMAT = 'JSON'
);

-- table with link metadata :
CREATE TABLE linkTable (
     rowkey VARCHAR PRIMARY KEY,
     id STRING,
     regionContextTag STRING,
     asGeoJSON STRING
) WITH (
     KAFKA_TOPIC = 'grid-static-links',
     VALUE_FORMAT = 'JSON'
);

-- stream with data points from raw_dp topic
CREATE STREAM gridLinkFlowDataStream (
     rowkey VARCHAR KEY,
     ts BIGINT,
     flow DOUBLE
) WITH (
     KAFKA_TOPIC = 'grid-link-flow-data',
     VALUE_FORMAT = 'JSON'
);

#####################################################################
#
# Insert simple sample data
#
#    rowkey: contains the locaion ID
#
#    The data is generated using the class tool.SimulationScenario
#
#####################################################################












#####################################################################
#
# Join measurements and background data
#

CREATE STREAM gridflowEnrichedSampleStream2
WITH (
     KAFKA_TOPIC = 'grid-flow-enriched-sample-stream2',
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



#
# Filter only traffic which is cross-border
#
Select * from gridflowEnrichedSampleStream2 where source != target emit changes;


Create table crossRegionFlows
WITH (
     KAFKA_TOPIC = 'grid-cross-region-flows',
     VALUE_FORMAT = 'JSON',
     PARTITIONS = 1
)
as Select REGIONCONTEXTTAG, sum(flow) from gridflowEnrichedSampleStream2
where source != target
group by REGIONCONTEXTTAG;



####################################################
#
# This is not possible. We create a table here !!!
#
Create stream crossRegionFlowStream
WITH (
     KAFKA_TOPIC = 'grid-cross-region-flow-stream',
     VALUE_FORMAT = 'JSON',
     PARTITIONS = 1
)
as Select REGIONCONTEXTTAG, sum(flow) from gridflowEnrichedSampleStream2
where source != target
group by REGIONCONTEXTTAG
emit changes;









#
#  Aggregation over time for total flows since beginning ...
#
Create table crossRegionFlowStream3
WITH (
     KAFKA_TOPIC = 'grid-cross-region-flow3',
     VALUE_FORMAT = 'JSON',
     PARTITIONS = 1
)
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



#
#  Aggregation for time windows for actual flows ...
#
Create table crossRegionFlowStream4
WITH (
     KAFKA_TOPIC = 'grid-cross-region-flow4',
     VALUE_FORMAT = 'JSON',
     PARTITIONS = 1
)
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

####################################################
#
# This is the result of the processing
#
####################################################
Select * from crossRegionFlowStream4 emit changes;


######################
#  END OF DEMO Code  #
######################

####################################################
#
# This is an example record from our result
#
####################################################
[
  {
    "REGIONCONTEXTTAG": "FR->BE",
    "WINDOWSTART": 1605189720000,
    "WINDOWEND": 1605189780000,
    "CROSS_REGION_FLOW": 2997.9365865358313,
    "SOURCE_REGION": "FR",
    "TARGET_REGION": "BE",
    "GEOJSON": "{ \"type\": \"Feature\", \"geometry\": { \"type\": \"LineString\", \"coordinates\" : [ [2.2069771,48.8587741], [ 5.565509, 50.623306]  ] }, \"properties\": { \"popupContent\": \"STATIONLINK: (Paris=>2ST12345)\" } }"
  }
]









########################################################################################################################
#
#  We can split the link-id into source and target id of linked stations and re-key the stream...
#
-- stream with data points from raw_dp topic
CREATE STREAM gridLinkFlow_Source_Stream
WITH (
     KAFKA_TOPIC = 'grid-link-by-source-station-flow-data',
     VALUE_FORMAT = 'JSON',
     PARTITIONS = 1
)
AS SELECT SPLIT(ROWKEY, '-')[0] as rowkey, rowkey as stationkey, ts, flow
FROM GRIDLINKFLOWDATASTREAM
EMIT CHANGES;

CREATE STREAM gridLinkFlow_Dest_Stream
WITH (
     KAFKA_TOPIC = 'grid-link-by-dest-station-flow-data',
     VALUE_FORMAT = 'JSON',
     PARTITIONS = 1
)
AS SELECT SPLIT(ROWKEY, '-')[1] as rowkey, rowkey as stationkey, ts, flow
FROM GRIDLINKFLOWDATASTREAM
EMIT CHANGES;





#
# The array index starts with "1" - this is not working:
#
select SPLIT(REGIONCONTEXTTAG, '->')[0] AS SOURCE, SPLIT(REGIONCONTEXTTAG, '->')[1] AS TARGET from LINKTABLE EMIT CHANGES;


#
# The array index starts with "1" - this works:
#
select SPLIT(REGIONCONTEXTTAG, '->')[1] AS SOURCE, SPLIT(REGIONCONTEXTTAG, '->')[2] AS TARGET from LINKTABLE EMIT CHANGES;