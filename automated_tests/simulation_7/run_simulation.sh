#!/bin/bash

source ../commons.sh

log_yellow "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"
log_yellow "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"
log_yellow "Simulation 7"
log_yellow "Testing: At least once delivery: message re-delivered – updating operations"
log_yellow "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"
log_yellow "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"

./../prune.sh || error_exit "prune.sh failed"

log_yellow "Deploying mandatory infrastructure components..."
docker compose -f docker-compose.simulation-7.yml up --build -d || error_exit "docker-compose.simulation-7.yml failed to start"

log_yellow "Waiting 120 seconds for infrastructure to settle..."
sleep 120

log_green "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"
log_green "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"
log_green "Infrastructure ready – executing simulation 7"
log_green "Testing: At least once delivery: message re-delivered – updating operations"
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

log_yellow "Waiting 15 seconds: letting the importing process complete"
sleep 15

log_yellow "Verifying importing process is successful..."
RETRY_COUNT=0
MAX_RETRIES=100
SUCCESS=false
while [ $RETRY_COUNT -lt $MAX_RETRIES ]; do
    RESPONSE=$(curl --silent --location --write-out "HTTPSTATUS:%{http_code}" "http://localhost:8080/importing/blueprints?blueprint_id=$BLUEPRINT_ID")

    HTTP_BODY=$(echo $RESPONSE | sed -e 's/HTTPSTATUS\:.*//g')
    HTTP_STATUS=$(echo $RESPONSE | tr -d '\n' | sed -e 's/.*HTTPSTATUS://')

    if [ "$HTTP_STATUS" -eq 200 ]; then
        BLUEPRINT_SAGA_STATUS=$(echo $HTTP_BODY | jq -r '.blueprintSagaStatus')
        if [ "$BLUEPRINT_SAGA_STATUS" == "METADATA_EXTRACTION_SUCCESS" ]; then
            log_green "OK: Importing process completed successfully with status: $BLUEPRINT_SAGA_STATUS"
            SUCCESS=true
            break
        else
            log_red "Importing process status: $BLUEPRINT_SAGA_STATUS. Retrying in 1 second... (Attempt $((RETRY_COUNT+1))/$MAX_RETRIES)"
        fi
    else
        log_red "Received HTTP status $HTTP_STATUS. Retrying in 1 seconds.. (Attempt $((RETRY_COUNT+1))/$MAX_RETRIES)"
    fi

    sleep 1
    ((RETRY_COUNT++))
done

if [ "$SUCCESS" != true ]; then
    error_exit "Importing process is still failed after $MAX_RETRIES attempts"
fi



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



log_yellow "Adding suppression operation to worksheet..."

RETRY_COUNT=0
MAX_RETRIES=10
SUCCESS=false
while [ $RETRY_COUNT -lt $MAX_RETRIES ]; do
    RESPONSE=$(curl --silent --location --request PUT "http://localhost:8080/configuration/worksheet-operations/$WORKSHEET_ID/suppression" \
                     --header 'Content-Type: application/json' \
                     --data '{
                         "settings": {
                             "token": "*"
                         },
                         "table": "employees",
                         "column": "name"
                     }' \
                     --write-out "HTTPSTATUS:%{http_code}")

    HTTP_STATUS=$(echo $RESPONSE | tr -d '\n' | sed -e 's/.*HTTPSTATUS://')

    if [ "$HTTP_STATUS" -eq 200 ]; then
        SUCCESS=true
        log_green "OK: Successfully added suppression operation to a worksheet"
        break
    else
        log_yellow "Received HTTP status $HTTP_STATUS. Retrying in 5 seconds... (Attempt $((RETRY_COUNT+1))/$MAX_RETRIES)"
        sleep 5
    fi

    ((RETRY_COUNT++))
done

if [ "$SUCCESS" != true ]; then
    error_exit "Failed to add suppression operation to worksheet after $MAX_RETRIES attempts"
fi

log_yellow "Waiting 15 seconds – letting the service crash..."
sleep 15

log_yellow "Stopping Docker container spanning the worksheet-service..."
docker compose -f docker-compose.simulation-7.yml stop worksheet-service || error_exit "worksheet-service container failed to stop"
log_green "OK: Docker container stopped"

log_yellow "Starting Docker container spanning the worksheet-service..."
docker-compose -f docker-compose.simulation-7.yml run --detach -e SHOULD_CRASH=false --service-ports --name worksheet-service-custom worksheet-service

log_green "OK: Docker container started: worksheet-service restarted"





log_yellow "Verifying that anonymisation operation converged to the expected state after the message was re-delivered"
RETRY_COUNT=0
MAX_RETRIES=60
SUCCESS=false
while [ $RETRY_COUNT -lt $MAX_RETRIES ]; do
    RESPONSE=$(curl --silent --location --write-out "HTTPSTATUS:%{http_code}" "http://localhost:8080/configuration/operations?worksheet_id=$WORKSHEET_ID")

    HTTP_BODY=$(echo $RESPONSE | sed -e 's/HTTPSTATUS\:.*//g')
    HTTP_STATUS=$(echo $RESPONSE | tr -d '\n' | sed -e 's/.*HTTPSTATUS://')

    if [ "$HTTP_STATUS" -eq 200 ]; then
        ALL_TASKS_PROCESSED_SUCCESSFULLY=$(echo $HTTP_BODY | jq -r '.allSuccessful')
        if [ "$ALL_TASKS_PROCESSED_SUCCESSFULLY" == "true" ]; then
            log_green "OK: Anonymisation operation converged to a valid state after message was re-delivered – task processed successfully"
            SUCCESS=true
            break
        else
            log_red "Anonymisation operation still not successful. Retrying in 5 second... (Attempt $((RETRY_COUNT+1))/$MAX_RETRIES)"
        fi
    else
        log_red "Received HTTP status $HTTP_STATUS. Retrying in 5 seconds.. (Attempt $((RETRY_COUNT+1))/$MAX_RETRIES)"
    fi

    sleep 5
    ((RETRY_COUNT++))
done

if [ "$SUCCESS" != true ]; then
    error_exit "Anonymisation operation is still not successfully completed after $MAX_RETRIES attempts"
fi


log_green "OK: Simulation 7 finished successfully"
log_green "Tested: At least once delivery: message re-delivered – updating operations"
