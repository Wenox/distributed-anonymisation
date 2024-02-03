#!/bin/bash

DATABASE_NAME=EMPLOYEES_DATABASE
POPULATE_SCRIPT_NAME=script.sql
OUTPUT_DUMP_NAME=database.dump

## Drop database first
dropdb -h localhost -U postgres $DATABASE_NAME --if-exists

## Create database, table, and populate it with data
psql -h localhost -U postgres -f $POPULATE_SCRIPT_NAME

## Dump to script
pg_dump -h localhost -U postgres -f $OUTPUT_DUMP_NAME $DATABASE_NAME
