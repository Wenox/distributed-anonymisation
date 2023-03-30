#!/bin/bash
# Ensure postgres user exists

## Create database, table, and populate it with data
psql -h localhost -U postgres -f create_employees.sql

## Dump to archive
pg_dump -h localhost -U postgres -Fc -f employeesdb_archive.backup employeesdb

## Dump to script
pg_dump -h localhost -U postgres -f employeesdb_script.sql employeesdb
