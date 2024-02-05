#!/bin/bash

source ../commons.sh

log_yellow "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"
log_yellow "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"
log_yellow "Simulation 4"
log_yellow "Testing: Dead letter queue and alerting"
log_yellow "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"
log_yellow "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"

./../prune.sh || error_exit "prune.sh failed"

log_yellow "Deploying mandatory infrastructure components..."
docker compose -f docker-compose.simulation-4.yml up --build -d || error_exit "docker-compose.simulation-4.yml failed to start"

log_yellow "Waiting 30 seconds for infrastructure to settle..."
sleep 30

log_yellow "Starting importing process..."

RETRY_COUNT=0
MAX_RETRIES=30
SUCCESS=false

while [ $RETRY_COUNT -lt $MAX_RETRIES ]; do
    RESPONSE=$(curl --silent --location --write-out "HTTPSTATUS:%{http_code}" 'http://localhost:8080/importing/start/' \
      --form 'dumpFile=@"/Users/macbookair/repos/distributed-anonymization/data/script/employeesdb_script.sql"' \
      --form 'databaseType="POSTGRESQL"' \
      --form 'restoreMode="SCRIPT"' \
      --form 'title="Employees"' \
      --form 'description="This is just a sample dump of employees database made in a SCRIPT mode."')

    HTTP_BODY=$(echo $RESPONSE | sed -e 's/HTTPSTATUS\:.*//g')
    HTTP_STATUS=$(echo $RESPONSE | tr -d '\n' | sed -e 's/.*HTTPSTATUS://')

    if [ "$HTTP_STATUS" -eq "202" ]; then
        BLUEPRINT_ID=$(echo $HTTP_BODY | tr -d '\n')
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

log_yellow "Waiting 15 seconds: letting the importing process complete"
sleep 15

log_yellow "Importing process should throw runtime exception and publish to a dead letter queue..."
RETRY_COUNT=0
MAX_RETRIES=100
SUCCESS=false
while [ $RETRY_COUNT -lt $MAX_RETRIES ]; do
    RESPONSE=$(curl --silent --location --write-out "HTTPSTATUS:%{http_code}" "http://localhost:8080/importing/blueprints?blueprint_id=$BLUEPRINT_ID")

    HTTP_BODY=$(echo $RESPONSE | sed -e 's/HTTPSTATUS\:.*//g')
    HTTP_STATUS=$(echo $RESPONSE | tr -d '\n' | sed -e 's/.*HTTPSTATUS://')

    if [ "$HTTP_STATUS" -eq 200 ]; then
        log_green "OK: Importing process successfully started"
        SUCCESS=true
        break
    else
        log_red "Received HTTP status $HTTP_STATUS. Retrying in 1 seconds.. (Attempt $((RETRY_COUNT+1))/$MAX_RETRIES)"
    fi

    sleep 1
    ((RETRY_COUNT++))
done

if [ "$SUCCESS" != true ]; then
    error_exit "Importing process is still not started after $MAX_RETRIES attempts"
fi

sleep "Waiting 30 seconds – letting importing process fail gracefully and publish message to a dead letter queue"
sleep 30

log_green "OK: Simulation 4 finished successfully"
log_green "Tested: Dead letter queue and alerting!"
log_yellow "Check services.logs for the existence of published message to a dead letter queue"