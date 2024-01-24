#!/bin/bash

WORKSHEET_ID=$1

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

log_yellow "Starting Exporting API test..."

log_yellow "Starting docker-compose.exporting-api.yml"
docker compose -f docker-compose.exporting-api.yml up --build -d || error_exit "docker-compose.exporting-api.yml failed to start"

log_yellow "Waiting 45 seconds..."
sleep 45

log_yellow "Starting exporting process..."

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

log_yellow "Verifying exporting process..."
RETRY_COUNT=0
MAX_RETRIES=20
SUCCESS=false
while [ $RETRY_COUNT -lt $MAX_RETRIES ]; do
    RESPONSE=$(curl --silent --location --write-out "HTTPSTATUS:%{http_code}" "http://localhost:8080/exporting/outcomes?outcome_id=$OUTCOME_ID")

    HTTP_BODY=$(echo $RESPONSE | sed -e 's/HTTPSTATUS\:.*//g')
    HTTP_STATUS=$(echo $RESPONSE | tr -d '\n' | sed -e 's/.*HTTPSTATUS://')

    if [ "$HTTP_STATUS" -eq 200 ]; then
        STATUS=$(echo $HTTP_BODY | jq -r '.status')
        if [ "$STATUS" == "DUMP_GENERATED" ]; then
            log_green "OK: Exporting process succeeded with status: $STATUS"
            SUCCESS=true
            break
        else
            log_red "Exporting process status: $STATUS. Retrying in 3 seconds... (Attempt $((RETRY_COUNT+1))/$MAX_RETRIES)"
        fi
    else
        log_red "Received HTTP status $HTTP_STATUS. Retrying in 3 seconds... (Attempt $((RETRY_COUNT+1))/$MAX_RETRIES)"
    fi

    sleep 3
    ((RETRY_COUNT++))
done

if [ "$SUCCESS" != true ]; then
    error_exit "Exporting process is still failed after $MAX_RETRIES attempts"
fi

log_yellow "Shutting down all platform services..."
docker-compose -f docker-compose.exporting-api.yml down || error_exit "Failed to shut down docker-compose.exporting-api.yml"
docker-compose -f docker-compose.configuration-api.yml down || error_exit "Failed to shut down docker-compose.configuration-api.yml"
docker-compose -f docker-compose.importing-api.yml down || error_exit "Failed to shut down docker-compose.importing-api.yml"
docker-compose -f docker-compose.commons.yml down || error_exit "Failed to shut down docker-compose.commons.yml"

log_green "OK: exporting API test succeeded"
