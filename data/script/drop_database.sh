#!/bin/sh
echo "Dropping PostgreSQL databases"
psql -U postgres -t -c "SELECT datname FROM pg_database WHERE datistemplate = false AND datname NOT IN ('postgres', 'template1', 'template0', 'template2', 'template3', 'macbookair')" | while read dbname; do psql -U postgres -c "DROP DATABASE IF EXISTS \"$dbname\""; done
echo "Successfully dropped."