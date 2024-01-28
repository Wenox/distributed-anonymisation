#!/bin/bash

DATABASE_NAME=db_100k_records_v1
POPULATE_SCRIPT_NAME=load_test.sql
OUTPUT_DUMP_NAME=dump_script_100k_records

## Drop database first
dropdb -h localhost -U postgres $DATABASE_NAME --if-exists

## Create database, table, and populate it with data
psql -h localhost -U postgres -f $POPULATE_SCRIPT_NAME

## Dump to script
pg_dump -h localhost -U postgres -f $OUTPUT_DUMP_NAME $DATABASE_NAME
