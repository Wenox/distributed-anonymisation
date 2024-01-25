#!/bin/bash

# Function to make a single request
make_request() {
    curl --location 'http://localhost:8080/importing/start/' \
         --form 'dumpFile=@"/Users/macbookair/repos/distributed-anonymization/data/script/employeesdb_script.sql"' \
         --form 'databaseType="POSTGRESQL"' \
         --form 'restoreMode="SCRIPT"' \
         --form 'title="Employees"' \
         --form 'description="This is just a sample dump of employees database made in a SCRIPT mode."'
}

export -f make_request

# Number of parallel processes
num_parallel=200

# Sending 1000 requests
seq 10000 | xargs -n 1 -P $num_parallel -I {} bash -c 'make_request'
