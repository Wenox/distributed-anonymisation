#!/bin/bash

source ../commons.sh

log_yellow "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"
log_yellow "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"
log_yellow "Simulation 6"
log_yellow "Testing: Stale blueprint reconciliation mechanism"
log_yellow "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"
log_yellow "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"

./../prune.sh || error_exit "prune.sh failed"

log_yellow "Deploying mandatory infrastructure components..."
docker compose -f docker-compose.simulation-6.yml up --build -d || error_exit "docker-compose.simulation-6.yml failed to start"

log_yellow "Waiting 60 seconds..."
sleep 60

log_green "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"
log_green "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"
log_green "Infrastructure ready – executing simulation 6"
log_green "Testing: Stale blueprint reconciliation mechanism"
log_green "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"
log_green "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"

log_yellow "Starting importing process..."

RETRY_COUNT=0
MAX_RETRIES=30
SUCCESS=false

while [ $RETRY_COUNT -lt $MAX_RETRIES ]; do
    RESPONSE=$(curl --silent --location --write-out "HTTPSTATUS:%{http_code}" 'http://localhost:8080/importing/start/' \
      --form 'dumpFile=@"../data/employeesdb_script.sql"' \
      --form 'databaseType="POSTGRESQL"' \
      --form 'restoreMode="SCRIPT"' \
      --form 'title="Employees"' \
      --form 'description="This is just a sample dump of employees database made in a SCRIPT mode."')

    HTTP_BODY=$(echo $RESPONSE | sed -e 's/HTTPSTATUS\:.*//g')
    HTTP_STATUS=$(echo $RESPONSE | tr -d '\n' | sed -e 's/.*HTTPSTATUS://')

    if [ "$HTTP_STATUS" -eq 202 ]; then
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

log_yellow "Waiting 10 seconds: letting the service crash"
sleep 10

log_green "OK: Blueprint service crashed before listen-to-yourself event was produced"
log_yellow "Blueprint is stale: indefinitely stuck in 'Initialised' status"
log_yellow "Stale blueprint reconciliation mechanism shouold delete it after service recovers"

log_yellow "Restarting the blueprint-service instance..."
docker compose -f docker-compose.simulation-6.yml restart blueprint-service || error_exit "blueprint-service failed to start"

log_yellow "Waiting 15 seconds"
sleep 15

log_yellow "Verifying if the blueprint (importing process) successfully marks the process as STALE..."
log_yellow "(Blueprints are marked as STALE after 4 minutes)"
RETRY_COUNT=0
MAX_RETRIES=240
SUCCESS=false
while [ $RETRY_COUNT -lt $MAX_RETRIES ]; do
    RESPONSE=$(curl --silent --location --write-out "HTTPSTATUS:%{http_code}" "http://localhost:8080/importing/blueprints?blueprint_id=$BLUEPRINT_ID")

    HTTP_BODY=$(echo $RESPONSE | sed -e 's/HTTPSTATUS\:.*//g')
    HTTP_STATUS=$(echo $RESPONSE | tr -d '\n' | sed -e 's/.*HTTPSTATUS://')

    if [ "$HTTP_STATUS" -eq 200 ]; then
        BLUEPRINT_SAGA_STATUS=$(echo $HTTP_BODY | jq -r '.blueprintSagaStatus')
        if [ "$BLUEPRINT_SAGA_STATUS" == "STALE" ]; then
            log_green "OK: Importing process is marked as stale with status: $BLUEPRINT_SAGA_STATUS"
            SUCCESS=true
            break
        else
            log_red "Importing process status is still: $BLUEPRINT_SAGA_STATUS. Retrying in 5 seconds... (Attempt $((RETRY_COUNT+1))/$MAX_RETRIES)"
        fi
    else
        log_red "Received HTTP status $HTTP_STATUS. Retrying in 5 seconds.. (Attempt $((RETRY_COUNT+1))/$MAX_RETRIES)"
    fi

    sleep 5
    ((RETRY_COUNT++))
done

if [ "$SUCCESS" != true ]; then
    error_exit "Importing process is still not STALE after $MAX_RETRIES attempts"
fi


log_green "OK: Simulation 6 finished successfully"
log_green "Tested: Stale blueprint reconciliation mechanism"