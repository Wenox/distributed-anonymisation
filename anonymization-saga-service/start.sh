#!/bin/zsh
echo "Starting anonymization-saga-service..."
uvicorn main:app --reload --port 9000