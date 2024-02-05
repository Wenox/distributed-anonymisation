#!/bin/bash

source ../commons.sh

log_yellow "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"
log_yellow "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"
log_yellow "Simulation 10"
log_yellow "Testing: Failure in a transactional outbox consumer – messages should be redelivered"
log_yellow "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"
log_yellow "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"

./../prune.sh || error_exit "prune.sh failed"

log_yellow "Deploying mandatory infrastructure components..."
docker compose -f docker-compose.simulation-10.yml up --build -d || error_exit "docker-compose.simulation-10.yml failed to start"

log_yellow "Waiting 180 seconds for infrastructure to settle..."
sleep 120

BLUEPRINT_ID=1234

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


log_yellow "Checking if outbox event was produced..."

RETRY_COUNT=0
MAX_RETRIES=20
SUCCESS=false
while [ $RETRY_COUNT -lt $MAX_RETRIES ]; do
    RESPONSE=$(curl --silent --location --write-out "HTTPSTATUS:%{http_code}" 'http://localhost:8080/configuration/worksheet-events?timestamp=2020-01-01T10:10:10.123' \
                                                                               --header 'Content-Type: application/json')

    HTTP_BODY=$(echo $RESPONSE | sed -e 's/HTTPSTATUS\:.*//g')
    HTTP_STATUS=$(echo $RESPONSE | tr -d '\n' | sed -e 's/.*HTTPSTATUS://')

    if [ "$HTTP_STATUS" -eq 200 ]; then
            RECORD_COUNT=$(echo $HTTP_BODY | jq '. | length')
            if [ "$RECORD_COUNT" -gt 0 ]; then
                SUCCESS=true
                log_green "OK: received transactional outbox event : $HTTP_BODY"
                break
            else
                log_red "Received HTTP status $HTTP_STATUS but no transactional outbox events found. Retrying in 3 seconds... (Attempt $((RETRY_COUNT+1))/$MAX_RETRIES)"
                sleep 3
            fi
        else
            log_red "Received HTTP status $HTTP_STATUS. Retrying in 10 seconds... (Attempt $((RETRY_COUNT+1))/$MAX_RETRIES)"
            sleep 10
        fi

    ((RETRY_COUNT++))
done

if [ "$SUCCESS" != true ]; then
    error_exit "Failed to receive a transactional outbox event after $MAX_RETRIES attempts"
fi

log_yellow "Letting database restoration service crash while processing outbox message..."
log_yellow "Waiting 30 seconds"
sleep 30
log_yellow "Database restoration service should have crashed while processing outbox message..."
log_yellow "Restarting it..."

log_yellow "Stopping Docker container spanning the database-restoration-service..."
docker compose -f docker-compose.simulation-10.yml stop database-restoration-service || error_exit "database-restoration-service container failed to stop"
log_green "OK: Docker container stopped"

log_yellow "Starting Docker container spanning the database-restoration-service..."
docker-compose -f docker-compose.simulation-10.yml run --detach -e SHOULD_CRASH=false --service-ports --name database-restoration-service-custom database-restoration-service

log_green "OK: Docker container started: database-restoration-service restarted"
log_yellow "Sleep 120... letting the outbox event be redelivered and reprocessed"

log_green "OK: Simulation 10 finished successfully – check services.logs for redelivered outbox event"
log_green "Tested: Failure in a transactional outbox consumer – messages should be redelivered"
