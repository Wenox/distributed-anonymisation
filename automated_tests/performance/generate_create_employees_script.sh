#!/bin/bash

# Global variables
NUMBER_OF_COLUMNS=10
NUMBER_OF_RECORDS=100000
COLUMN_STRING_LENGTH=60
FILENAME="load_test.sql"
DATABASE="100k_records_v1"

# Function to generate a random string of specified length
generate_random_string() {
    head -c 30 /dev/urandom | base64 | tr -dc 'a-zA-Z0-9' | fold -w ${1:-$COLUMN_STRING_LENGTH} | head -n 1
}

# Write the initial SQL commands to the file
echo "CREATE DATABASE $DATABASE;" > $FILENAME
printf '\\c' >> $FILENAME
echo " $DATABASE;" >> $FILENAME
echo "CREATE TABLE employees (" >> $FILENAME
echo "  id SERIAL PRIMARY KEY," >> $FILENAME
for i in $(seq 1 $NUMBER_OF_COLUMNS); do
    if [ $i -lt $NUMBER_OF_COLUMNS ]; then
        echo "    column_$i VARCHAR(255)," >> $FILENAME
    else
        echo "    column_$i VARCHAR(255)" >> $FILENAME
    fi
done
echo ");" >> $FILENAME

# Append the INSERT commands for the specified number of records
echo "INSERT INTO employees (" >> $FILENAME
for i in $(seq 1 $NUMBER_OF_COLUMNS); do
    if [ $i -lt $NUMBER_OF_COLUMNS ]; then
        echo "    column_$i,"
    else
        echo "    column_$i"
        echo ")"
    fi
done >> $FILENAME
echo "VALUES" >> $FILENAME
for i in $(seq 1 $NUMBER_OF_RECORDS); do
    echo "[$i / $NUMBER_OF_RECORDS]"
    # Generate random data for each column
    record="("
    for j in $(seq 1 $NUMBER_OF_COLUMNS); do
        random_data="'$(generate_random_string)'"
        record+="$random_data"
        if [ $j -lt $NUMBER_OF_COLUMNS ]; then
            record+=", "
        fi
    done
    record+=")"
    # Add a comma except for the last record
    if [ $i -lt $NUMBER_OF_RECORDS ]; then
        record+=","
    fi
    echo $record >> $FILENAME
done
echo ";" >> $FILENAME
