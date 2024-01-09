#!/bin/zsh
echo "Starting anonymization-orchestration-service..."
uvicorn main:app --reload --port 9000 --log-level info