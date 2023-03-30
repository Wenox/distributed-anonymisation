#!/bin/bash
## Creates employees database from Script dump

## 1. Create empty database
createdb -h localhost -U postgres employeesdb

## 2. Import employees script
psql -h localhost -U postgres -d employeesdb -f employeesdb_script.sql
