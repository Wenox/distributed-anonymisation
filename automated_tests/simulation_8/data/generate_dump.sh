#!/bin/bash

DATABASE_NAME=simulation_8
POPULATE_SCRIPT_NAME=simulation_8_script.sql
OUTPUT_DUMP_NAME=simulation_8_dump.sql

## Drop database first
dropdb -h localhost -U postgres $DATABASE_NAME --if-exists

## Create database, table, and populate it with data
psql -h localhost -U postgres -f $POPULATE_SCRIPT_NAME

## Dump to script
pg_dump -h localhost -U postgres -f $OUTPUT_DUMP_NAME $DATABASE_NAME
