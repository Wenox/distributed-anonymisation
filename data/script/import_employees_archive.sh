#!/bin/bash
## Creates employees database from Archive dump

## 1. Create empty database
createdb -h localhost -U postgres employeesdb

## 2. Import employees script
pg_restore -h localhost -U postgres -d employeesdb -f employeesdb_archive.backup

