#!/bin/sh

echo 'Building main.go...'
GOOS=linux GOARCH=amd64 go build -o main

echo 'Packaging into deployment.zip...'
zip deployment.zip main

echo 'Done'
