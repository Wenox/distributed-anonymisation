#!/bin/bash

source ../commons.sh

log_yellow "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"
log_yellow "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"
log_yellow "Simulation 5"
log_yellow "Testing: Listen-to-yourself pattern"
log_yellow "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"
log_yellow "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"

./../prune.sh || error_exit "prune.sh failed"

log_yellow "Deploying mandatory infrastructure components..."
docker compose -f docker-compose.simulation-5.yml up --build -d || error_exit "docker-compose.simulation-5.yml failed to start"

log_yellow "Waiting 60 seconds..."
sleep 60

log_green "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"
log_green "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"
log_green "Infrastructure ready – executing simulation 5"
log_green "Testing: Listen-to-yourself pattern"
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

log_yellow "Waiting 5 seconds: letting the service crash"
sleep 5

log_green "OK: Blueprint service crashed after listen-to-yourself event was produced"
log_yellow "Importing process should resume after the service recovers"

log_yellow "Stopping Docker container spanning the blueprint-service..."
docker compose -f docker-compose.simulation-5.yml stop blueprint-service || error_exit "blueprint-service container failed to stop"
log_green "OK: Docker container stopped"

log_yellow "Starting Docker container spanning the blueprint-service..."
docker-compose -f docker-compose.simulation-5.yml run --detach -e ENABLE_PROCESS_MESSAGES=true --service-ports --name blueprint-service-custom blueprint-service

log_green "OK: Docker container started: blueprint-service restarted"

log_yellow "Verifying if the importing process resumed its execution and successfully completed..."
RETRY_COUNT=0
MAX_RETRIES=240
SUCCESS=false
while [ $RETRY_COUNT -lt $MAX_RETRIES ]; do
    RESPONSE=$(curl --silent --location --write-out "HTTPSTATUS:%{http_code}" "http://localhost:8080/importing/blueprints?blueprint_id=$BLUEPRINT_ID")

    HTTP_BODY=$(echo $RESPONSE | sed -e 's/HTTPSTATUS\:.*//g')
    HTTP_STATUS=$(echo $RESPONSE | tr -d '\n' | sed -e 's/.*HTTPSTATUS://')

    if [ "$HTTP_STATUS" -eq 200 ]; then
        BLUEPRINT_SAGA_STATUS=$(echo $HTTP_BODY | jq -r '.blueprintSagaStatus')
        if [ "$BLUEPRINT_SAGA_STATUS" == "METADATA_EXTRACTION_SUCCESS" ]; then
            log_green "OK: Importing process has completed successfully with status: $BLUEPRINT_SAGA_STATUS"
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
    error_exit "Importing process is still not completed after $MAX_RETRIES attempts"
fi


log_green "OK: Simulation 5 finished successfully"
log_green "Tested: Listen-to-yourself pattern"