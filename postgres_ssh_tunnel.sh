#!/bin/sh

echo "Creating ssh tunnel on port 15432 for postgres..."
echo "ssh -L 5433:localhost:5432 -f -N ec2-user@3.8.162.197"

ssh -L 5433:localhost:5432 -f -N ec2-user@3.8.162.197

echo "Tunnel created"

ps aux | grep "ssh -fN -L"