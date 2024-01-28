#!/bin/bash

DUMP_SCRIPT_FULL_PATH=/Users/macbookair/repos/distributed-anonymization/automated_tests/performance/dump_script_100k_records

log() {
    echo "$(date +%Y-%m-%dT%H:%M:%S) -------- $1"
}

log_yellow() {
    echo -e "\033[33m$(date +%Y-%m-%dT%H:%M:%S) -------- $1\033[0m"
}

log_green() {
    echo -e "\033[32m$(date +%Y-%m-%dT%H:%M:%S) -------- $1\033[0m"
}

log_red() {
    echo -e "\033[31m$(date +%Y-%m-%dT%H:%M:%S) -------- $1\033[0m"
}

error_exit() {
    echo -e "\033[31m$(date +%Y-%m-%dT%H:%M:%S) -------- ERROR: $1\033[0m"
    exit 1
}

log_yellow "Starting importing process..."

RETRY_COUNT=0
MAX_RETRIES=20
SUCCESS=false

while [ $RETRY_COUNT -lt $MAX_RETRIES ]; do
    RESPONSE=$(curl --silent --location --write-out "HTTPSTATUS:%{http_code}" 'http://localhost:8080/importing/start/' \
      --form 'dumpFile=@"'$DUMP_SCRIPT_FULL_PATH'"' \
      --form 'databaseType="POSTGRESQL"' \
      --form 'restoreMode="SCRIPT"' \
      --form 'title="Employees"' \
      --form 'description="This is just a sample dump of employees database made in a SCRIPT mode."')

    HTTP_BODY=$(echo $RESPONSE | sed -e 's/HTTPSTATUS\:.*//g')
    HTTP_STATUS=$(echo $RESPONSE | tr -d '\n' | sed -e 's/.*HTTPSTATUS://')

    if [ "$HTTP_STATUS" -eq 202 ]; then
        BLUEPRINT_ID=$(echo $HTTP_BODY | tr -d '\n')
        echo $BLUEPRINT_ID > automated_tests/blueprint_id
        SUCCESS=true
        break
    else
        log_red "Received HTTP status $HTTP_STATUS. Retrying in 10 seconds... (Attempt $((RETRY_COUNT+1))/$MAX_RETRIES)"
        sleep 10
    fi

    ((RETRY_COUNT++))
done

if [ "$SUCCESS" != true ]; then
    error_exit "Failed to retrieve blueprint ID after $MAX_RETRIES attempts"
fi

log_green "OK: Importing process started successfully --- blueprint ID: $BLUEPRINT_ID"


# Sleep for 120 seconds
log_green "Sleeping for 120 seconds..."
sleep 120



log_yellow "Initialising a new worksheet for blueprint_id: $BLUEPRINT_ID"

RETRY_COUNT=0
MAX_RETRIES=20
SUCCESS=false
while [ $RETRY_COUNT -lt $MAX_RETRIES ]; do
    RESPONSE=$(curl --silent --location --write-out "HTTPSTATUS:%{http_code}" 'http://localhost:8080/configuration/worksheets/' \
                                                                               --header 'Content-Type: application/json' \
                                                                               --data "{
                                                                                   \"blueprintId\": \"$BLUEPRINT_ID\",
                                                                                   \"worksheetName\": \"Worksheet for anonymisation\"
                                                                               }")

    HTTP_BODY=$(echo $RESPONSE | sed -e 's/HTTPSTATUS\:.*//g')
    HTTP_STATUS=$(echo $RESPONSE | tr -d '\n' | sed -e 's/.*HTTPSTATUS://')

    if [ "$HTTP_STATUS" -eq 200 ]; then
        WORKSHEET_ID=$(echo $HTTP_BODY | jq -r '.worksheet.worksheetId')
        SUCCESS=true
        log_green "OK: Successfully initialised new worksheet with ID: $WORKSHEET_ID"
        echo $WORKSHEET_ID > automated_tests/worksheet_id
        break
    else
        log_red "Received HTTP status $HTTP_STATUS. Retrying in 10 seconds... (Attempt $((RETRY_COUNT+1))/$MAX_RETRIES)"
        sleep 10
    fi

    ((RETRY_COUNT++))
done

if [ "$SUCCESS" != true ]; then
    error_exit "Failed to initialise a worksheet after $MAX_RETRIES attempts"
fi



# Global variable for the number of columns
NUMBER_OF_COLUMNS=10

log_green "OK: Adding operations"

# Loop through each column
for i in $(seq 1 $NUMBER_OF_COLUMNS)
do
    log_yellow "Adding operation $i..."
    # Execute the curl command for each column
    curl --silent --location --request PUT "http://localhost:8080/configuration/worksheet-operations/$WORKSHEET_ID/suppression" \
                         --header 'Content-Type: application/json' \
                         --data '{
                             "settings": {
                                 "token": "*"
                             },
                             "table": "employees",
                             "column": "column_'$i'"
                         }' \
                         --write-out "HTTPSTATUS:%{http_code}"
    sleep 10
done

# Sleep 5 seconds
log_green "OK: Added operations"
echo "Sleeping 120 seconds before exporting starts..."
sleep 120






# Exporting
RETRY_COUNT=0
MAX_RETRIES=20
SUCCESS=false

while [ $RETRY_COUNT -lt $MAX_RETRIES ]; do
    RESPONSE=$(curl --silent --location --write-out "HTTPSTATUS:%{http_code}" 'http://localhost:8080/exporting/start' \
                                                                              --header 'Content-Type: application/json' \
                                                                              --data "{
                                                                                  \"worksheet_id\": \"$WORKSHEET_ID\",
                                                                                  \"dump_mode\": \"SCRIPT\"
                                                                              }")

    HTTP_BODY=$(echo $RESPONSE | sed -e 's/HTTPSTATUS\:.*//g')
    HTTP_STATUS=$(echo $RESPONSE | tr -d '\n' | sed -e 's/.*HTTPSTATUS://')

    if [ "$HTTP_STATUS" -eq 200 ]; then
        OUTCOME_ID=$(echo $HTTP_BODY | jq -r '.outcomeId')
        SUCCESS=true
        log_green "Successfully retrieved outcome ID: $OUTCOME_ID"
        break
    else
        log_red "Received HTTP status $HTTP_STATUS. Retrying in 10 seconds... (Attempt $((RETRY_COUNT+1))/$MAX_RETRIES)"
        sleep 10
    fi

    ((RETRY_COUNT++))
done

if [ "$SUCCESS" != true ]; then
    error_exit "Failed to retrieve outcome ID after $MAX_RETRIES attempts"
fi

log_green "OK: Exporting process started successfully --- outcome ID: $OUTCOME_ID"