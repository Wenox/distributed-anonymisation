#!/bin/bash

# Step 1: Test Importing API
sh test_importing_api.sh

# Step 2: Test Configuration API
BLUEPRINT_ID=$(cat automated_tests/blueprint_id)
sh test_configuration_api.sh $BLUEPRINT_ID

# Step 3: Test Exporting API
WORKSHEET_ID=$(cat automated_tests/worksheet_id)
sh test_exporting_api.sh $WORKSHEET_ID
