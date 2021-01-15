CREATE DATABASE scms_test;
\c scms_test
CREATE EXTENSION IF NOT EXISTS timescaledb CASCADE;

CREATE TABLE "sensor"(
    ilabel TEXT,
    ts TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    val NUMERIC
);

CREATE TABLE "sensor2"(
    ilabel TEXT,
    ts timestamptz NOT NULL,
    val NUMERIC
);

SELECT create_hypertable('sensor', 'ts');

INSERT INTO sensor(ilabel, ts, val) VALUES('TEST_VVLD', '1970-01-01T00:00:00Z', 0.2);
INSERT INTO sensor2(ilabel, ts, val) VALUES('TEST_VVLD', '1970-01-01T00:00:00Z', 0.2);

SELECT * FROM sensor LIMIT 10;


