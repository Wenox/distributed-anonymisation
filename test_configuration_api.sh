#!/bin/bash

BLUEPRINT_ID=$1

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
    echo -e "\033[31m$(date +%Y-%m-%dT%H:%M:%S)  ---------: ERROR: $1\033[0m"
    exit 1
}

log_yellow "Starting Configuration API test..."

rm -f automated_test/worksheet

log_yellow "Starting docker-compose.configuration-api.yml"
docker compose -f docker-compose.configuration-api.yml up --build -d || error_exit "docker-compose.configuration-api.yml failed to start"

log_yellow "Waiting 120 seconds..."
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
        echo $WORKSHEET_ID > automated_tests/worksheet
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





log_yellow "Adding suppression operation #1 to worksheet..."

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
        log_green "OK: Successfully added suppression operation #1 to a worksheet"
        break
    else
        log_yellow "Received HTTP status $HTTP_STATUS. Retrying in 5 seconds... (Attempt $((RETRY_COUNT+1))/$MAX_RETRIES)"
        sleep 5
    fi

    ((RETRY_COUNT++))
done

if [ "$SUCCESS" != true ]; then
    error_exit "Failed to add suppression operation #1 to worksheet after $MAX_RETRIES attempts"
fi

log_yellow "Adding suppression operation #2 to worksheet..."

RETRY_COUNT=0
MAX_RETRIES=10
SUCCESS=false
while [ $RETRY_COUNT -lt $MAX_RETRIES ]; do
    RESPONSE=$(curl --silent --location --request PUT "http://localhost:8080/configuration/worksheet-operations/$WORKSHEET_ID/suppression" \
                     --header 'Content-Type: application/json' \
                     --data '{
                         "settings": {
                             "token": "XXXXX"
                         },
                         "table": "employees",
                         "column": "surname"
                     }' \
                     --write-out "HTTPSTATUS:%{http_code}")

    HTTP_STATUS=$(echo $RESPONSE | tr -d '\n' | sed -e 's/.*HTTPSTATUS://')

    if [ "$HTTP_STATUS" -eq 200 ]; then
        SUCCESS=true
        log_green "OK: Successfully added suppression operation #2 to a worksheet"
        break
    else
        log_yellow "Received HTTP status $HTTP_STATUS. Retrying in 5 seconds... (Attempt $((RETRY_COUNT+1))/$MAX_RETRIES)"
        sleep 5
    fi

    ((RETRY_COUNT++))
done

if [ "$SUCCESS" != true ]; then
    error_exit "Failed to add suppression operation #2 to worksheet after $MAX_RETRIES attempts"
fi

log_green "OK: configuration API test succeeded"

echo $WORKSHEET_ID