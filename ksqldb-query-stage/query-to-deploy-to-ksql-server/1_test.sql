CREATE STREAM sourceStream ( a VARCHAR PRIMARY KEY, b STRING
) WITH (KAFKA_TOPIC='sourceStreamTopic', VALUE_FORMAT='JSON', REPLICAS='3', PARTITIONS='1');

INSERT INTO sourceTable (a, b) VALUES ('k1', 'v1');
INSERT INTO sourceTable (a, b) VALUES ('k1', 'v2');
INSERT INTO sourceTable (a, b) VALUES ('k1', 'v3');
INSERT INTO sourceTable (a, b) VALUES ('k2', 'v1');
INSERT INTO sourceTable (a, b) VALUES ('k3', 'v1');
INSERT INTO sourceTable (a, b) VALUES ('k3', 'foo');

CREATE STREAM filteredStream WITH (KAFKA_TOPIC='filteredStreamTopic', PARTITIONS=1, REPLICAS=3) AS SELECT
                                                                          t1.A A,
                                                                          t1.B B
                                                                      FROM sourceStream t1;

