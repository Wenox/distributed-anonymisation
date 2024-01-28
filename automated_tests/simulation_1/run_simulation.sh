#!/bin/bash

source ../commons.sh

log_yellow "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"
log_yellow "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"
log_yellow "Simulation 1"
log_yellow "Testing: Saga Pattern – Backward Recovery – Importing Process"
log_yellow "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"
log_yellow "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"

./../prune.sh || error_exit "prune.sh failed"

log_yellow "Deploying mandatory infrastructure components..."
docker compose -f docker-compose.simulation-1.yml up --build -d || error_exit "docker-compose.simulation-1.yml failed to start"

log_yellow "Waiting 60 seconds..."
sleep 60

log_yellow "Waiting 60 seconds..."

log_green "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"
log_green "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"
log_green "Infrastructure ready – executing simulation 1"
log_green "Testing: Saga Pattern – Backward Recovery – Importing process"
log_green "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"
log_green "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"

log_yellow "Starting importing process..."

RETRY_COUNT=0
MAX_RETRIES=20
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

log_yellow "Verifying importing process ----- it should fail during metadata extraction..."
RETRY_COUNT=0
MAX_RETRIES=100
SUCCESS=false
while [ $RETRY_COUNT -lt $MAX_RETRIES ]; do
    RESPONSE=$(curl --silent --location --write-out "HTTPSTATUS:%{http_code}" "http://localhost:8080/importing/blueprints?blueprint_id=$BLUEPRINT_ID")

    HTTP_BODY=$(echo $RESPONSE | sed -e 's/HTTPSTATUS\:.*//g')
    HTTP_STATUS=$(echo $RESPONSE | tr -d '\n' | sed -e 's/.*HTTPSTATUS://')

    if [ "$HTTP_STATUS" -eq 200 ]; then
        BLUEPRINT_SAGA_STATUS=$(echo $HTTP_BODY | jq -r '.blueprintSagaStatus')
        if [ "$BLUEPRINT_SAGA_STATUS" == "METADATA_EXTRACTION_FAILURE" ]; then
            log_green "OK: Importing process failed during metadata extraction. Importing process status: $BLUEPRINT_SAGA_STATUS"
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
    error_exit "Importing process is still not in desired status after $MAX_RETRIES attempts"
fi

sleep 10
log_yellow "Waiting 10 seconds: letting backward recovery complete..."

log_yellow "Verifying restoration database was dropped..."
RETRY_COUNT=0
MAX_RETRIES=10
SUCCESS=false
while [ $RETRY_COUNT -lt $MAX_RETRIES ]; do
    RESPONSE=$(curl --silent --location --write-out "HTTPSTATUS:%{http_code}" "http://localhost:8080/importing/restorations?blueprint_id=$BLUEPRINT_ID")

    HTTP_BODY=$(echo $RESPONSE | sed -e 's/HTTPSTATUS\:.*//g')
    HTTP_STATUS=$(echo $RESPONSE | tr -d '\n' | sed -e 's/.*HTTPSTATUS://')

    if [ "$HTTP_STATUS" -eq 200 ]; then
        DATABASE_STATUS=$(echo $HTTP_BODY | jq -r '.active')
        if [ "$DATABASE_STATUS" = "false" ]; then
            log_green "OK: Database (read-only restoration) was dropped successfully."
            SUCCESS=true
            break
        else
            log_red "Restoration snapshot status: $DATABASE_STATUS, but expected the database to be dropped. Retrying in 5 seconds... (Attempt $((RETRY_COUNT+1))/$MAX_RETRIES)"
        fi
    else
        log_yellow "Received HTTP status $HTTP_STATUS. Retrying in 5 seconds... (Attempt $((RETRY_COUNT+1))/$MAX_RETRIES)"
    fi

    sleep 5
    ((RETRY_COUNT++))
done

if [ $RETRY_COUNT -eq $MAX_RETRIES ]; then
    error_exit "Database was still not dropped after $MAX_RETRIES attempts"
fi

log_green "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"
log_green "OK: First compensating transaction succeeded"
log_green "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"

log_yellow "Verifying that the database dump was removed from Amazon S3 bucket..."

RETRY_COUNT=0
MAX_RETRIES=20
FILE_NOT_FOUND=true
while [ $RETRY_COUNT -lt $MAX_RETRIES ]; do
    if aws s3 ls "s3://blueprints-for-anonymisation/$BLUEPRINT_ID" > /dev/null 2>&1; then
        log_red "Database dump '$BLUEPRINT_ID' still exists in the Amazon S3 bucket. Retrying in 5 seconds... (Attempt $((RETRY_COUNT+1))/$MAX_RETRIES)"
        FILE_NOT_FOUND=false
    else
        log_green "OK: Database dump '$BLUEPRINT_ID' was successfully removed from Amazon S3 bucket"
        FILE_NOT_FOUND=true
        break
    fi

    sleep 5
    ((RETRY_COUNT++))
done

if [ "$FILE_NOT_FOUND" = false ]; then
    error_exit "Database dump '$BLUEPRINT_ID' was still found in the bucket after $MAX_RETRIES attempts"
fi

log_green "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"
log_green "OK: Second compensating transaction succeeded"
log_green "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"


log_green "OK: simulation finished successfully"
log_green "What tested: the Saga pattern, backward recovery"
