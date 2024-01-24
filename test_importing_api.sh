#!/bin/bash

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
    echo -e "\033[31m$(date +%Y-%m-%dT%H:%M:%S) --------- ERROR: $1\033[0m"
    exit 1
}

log_yellow "Starting Importing API test..."

rm -f automated_test/blueprint

log_yellow "Cleaning up old platform components"
./prune.sh || error_exit "prune.sh failed"

log_green "OK: finished clean up"

log_yellow "Starting docker-compose.commons.yml"
docker compose -f docker-compose.commons.yml up --build -d || error_exit "docker-compose.commons.yml failed to start"

log_yellow "Waiting 45 seconds..."
sleep 45

log_yellow "Starting docker-compose.importing-api.yml"
docker compose -f docker-compose.importing-api.yml up --build -d || error_exit "docker-compose.importing-api.yml failed to start"

  log_yellow "Waiting 30 seconds..."
sleep 30

log_yellow "Starting importing process..."

RETRY_COUNT=0
MAX_RETRIES=20
SUCCESS=false

while [ $RETRY_COUNT -lt $MAX_RETRIES ]; do
    RESPONSE=$(curl --silent --location --write-out "HTTPSTATUS:%{http_code}" 'http://localhost:8080/importing/blueprints/' \
      --form 'dumpFile=@"/Users/macbookair/repos/distributed-anonymization/data/script/employeesdb_script.sql"' \
      --form 'databaseType="POSTGRESQL"' \
      --form 'restoreMode="SCRIPT"' \
      --form 'title="Employees"' \
      --form 'description="This is just a sample dump of employees database made in a SCRIPT mode."')

    HTTP_BODY=$(echo $RESPONSE | sed -e 's/HTTPSTATUS\:.*//g')
    HTTP_STATUS=$(echo $RESPONSE | tr -d '\n' | sed -e 's/.*HTTPSTATUS://')

    if [ "$HTTP_STATUS" -eq 202 ]; then
        BLUEPRINT_ID=$(echo $HTTP_BODY | tr -d '\n')
        echo $BLUEPRINT_ID > automated_tests/blueprint
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

log_yellow "Verifying importing process..."
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

log_yellow "Verifying metadata resource exists..."
RETRY_COUNT=0
MAX_RETRIES=10
SUCCESS=false
while [ $RETRY_COUNT -lt $MAX_RETRIES ]; do
    RESPONSE=$(curl --silent --location --write-out "HTTPSTATUS:%{http_code}" "http://localhost:8080/importing/metadata?blueprint_id=$BLUEPRINT_ID")

    HTTP_BODY=$(echo $RESPONSE | sed -e 's/HTTPSTATUS\:.*//g')
    HTTP_STATUS=$(echo $RESPONSE | tr -d '\n' | sed -e 's/.*HTTPSTATUS://')

    if [ "$HTTP_STATUS" -eq 200 ]; then
        SUCCESS=true
        log_green "OK: Metadata verified"
        break
    else
        log_yellow "Received HTTP status $HTTP_STATUS. Retrying in 10 seconds... (Attempt $((RETRY_COUNT+1))/$MAX_RETRIES)"
        sleep 10
    fi

    ((RETRY_COUNT++))
done

if [ $RETRY_COUNT -eq $MAX_RETRIES ]; then
    error_exit "Metadata verification failed after $MAX_RETRIES attempts"
fi

log_yellow "Verifying restoration snapshot resource exists..."
RETRY_COUNT=0
MAX_RETRIES=10
SUCCESS=false
while [ $RETRY_COUNT -lt $MAX_RETRIES ]; do
    RESPONSE=$(curl --silent --location --write-out "HTTPSTATUS:%{http_code}" "http://localhost:8080/importing/restorations?blueprint_id=$BLUEPRINT_ID")

    HTTP_BODY=$(echo $RESPONSE | sed -e 's/HTTPSTATUS\:.*//g')
    HTTP_STATUS=$(echo $RESPONSE | tr -d '\n' | sed -e 's/.*HTTPSTATUS://')

    if [ "$HTTP_STATUS" -eq 200 ]; then
        SUCCESS=true
        log_green "OK: Restoration snapshot verified"
        break
    else
        log_yellow "Received HTTP status $HTTP_STATUS. Retrying in 10 seconds... (Attempt $((RETRY_COUNT+1))/$MAX_RETRIES)"
        sleep 10
    fi

    ((RETRY_COUNT++))
done

if [ $RETRY_COUNT -eq $MAX_RETRIES ]; then
    error_exit "Metadata verification failed after $MAX_RETRIES attempts"
fi

log_green "OK: importing API test succeeded"
