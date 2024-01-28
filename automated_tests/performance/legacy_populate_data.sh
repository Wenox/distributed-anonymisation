#!/bin/bash

DUMP_SCRIPT_FULL_PATH=/Users/macbookair/repos/distributed-anonymization/automated_tests/performance/dump_script_100k_records

# Function to extract a value from JSON (requires jq to be installed)
function jsonValue() {
    echo "${1}" | jq -r "${2}"
}

# Login and get the response headers and body
loginResponse=$(curl --silent --include --location 'http://localhost:8080/api/v1/auth/login' \
--header 'Content-Type: application/json' \
--data-raw '{
    "email": "user@user.com",
    "password": "user"
}' --write-out "\nHTTPSTATUS:%{http_code}")

# Extract HTTP status code
loginHttpStatus=$(echo "$loginResponse" | awk -F'HTTPSTATUS:' '{print $2}' | tr -d '[:space:]')

# Check for HTTP Status 200 and extract the token
if [ "$loginHttpStatus" -eq "200" ]; then
    # Extract the access token using awk
    AUTHORIZATION_HEADER="Bearer $(echo "$loginResponse" | awk -F' ' '/^access_token:/{print $2}' | tr -d '\r\n')"
    if [ -n "$AUTHORIZATION_HEADER" ]; then
        echo "Login successful. Authorization token acquired."
        echo "Token: $AUTHORIZATION_HEADER"
    else
        echo "Login successful but access token not found in headers."
        exit 1
    fi
else
    echo "Login failed with status $loginHttpStatus"
    exit 1
fi


# Upload template
templateResponse=$(curl --silent --location 'http://localhost:8080/api/v1/templates' \
--header "Authorization: $AUTHORIZATION_HEADER" \
--form 'file=@"'$DUMP_SCRIPT_FULL_PATH'"' \
--form 'type="PSQL"' \
--form 'restoreMode="SCRIPT"' \
--form 'title="Employees"' \
--form 'description="This is just a sample dump of employees database made in a SCRIPT mode."' --write-out "HTTPSTATUS:%{http_code}")

# Extract HTTP status code and response body
templateHttpStatus=$(echo $templateResponse | tr -d '\n' | sed -e 's/.*HTTPSTATUS://')
templateBody=$(echo $templateResponse | sed -e 's/HTTPSTATUS:.*//g')

# Check for HTTP Status 202
if [ $templateHttpStatus -eq 202 ]; then
    TEMPLATE_ID=$templateBody
    echo "Template uploaded successfully. Template ID: $TEMPLATE_ID"
else
    echo "Template upload failed with status $templateHttpStatus"
    exit 1
fi

# Sleep for 15 seconds
sleep 15

# Create worksheet
worksheetResponse=$(curl --silent --location 'http://localhost:8080/api/v1/worksheets' \
--header "Authorization: $AUTHORIZATION_HEADER" \
--header 'Content-Type: application/json' \
--data '{
    "templateId": "'$TEMPLATE_ID'"
}' --write-out "HTTPSTATUS:%{http_code}")

# Extract HTTP status code and response body
worksheetHttpStatus=$(echo $worksheetResponse | tr -d '\n' | sed -e 's/.*HTTPSTATUS://')
worksheetBody=$(echo $worksheetResponse | sed -e 's/HTTPSTATUS:.*//g')

# Check for success and extract WORKSHEET_ID
if [ $worksheetHttpStatus -eq 200 ]; then
    WORKSHEET_ID=$(jsonValue "$worksheetBody" '.id')
    echo "Worksheet created successfully. Worksheet ID: $WORKSHEET_ID"
else
    echo "Worksheet creation failed with status $worksheetHttpStatus"
    exit 1
fi

# Global variable for the number of columns
NUMBER_OF_COLUMNS=10

# Loop through each column
for i in $(seq 1 $NUMBER_OF_COLUMNS)
do
    # Execute the curl command for each column
    curl --silent --location --request PUT 'http://localhost:8080/api/v1/worksheet/'$WORKSHEET_ID'/column-operations/add-suppression' \
    --header "Authorization: $AUTHORIZATION_HEADER" \
    --header 'Content-Type: application/json' \
    --data '{
        "suppressionToken": "*",
        "tableName": "employees",
        "columnName": "column_'$i'",
        "columnType": "12",
        "primaryKeyColumnName": "id",
        "primaryKeyColumnType": "4"
    }'
done

# Sleep 5 seconds
echo "Sleeping 5 seconds..."
sleep 5

# Execute this endpoint:
echo "Executing exporting..."
curl --silent --location 'http://localhost:8080/api/v1/outcomes/generate' \
    --header "Authorization: $AUTHORIZATION_HEADER" \
    --header 'Content-Type: application/json' \
    --data '{
        "worksheetId": "'$WORKSHEET_ID'",
        "anonymisationScriptName": "anonymisation_script_1.sql",
        "dumpName": "dump_1.sql",
        "dumpMode": "SCRIPT_FILE"
}'

echo "OK: done"