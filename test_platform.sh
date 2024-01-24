#!/bin/bash

# Step 1: Test Importing API
sh test_importing_api.sh

# Step 2: Test Configuration API
BLUEPRINT=$(cat automated_tests/blueprint)
sh test_configuration_api.sh $BLUEPRINT

# Step 3: Test Exporting API
WORKSHEET=$(cat automated_tests/worksheet)
sh test_exporting_api.sh $WORKSHEET
