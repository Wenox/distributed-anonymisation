#!/bin/bash

rm /var/lib/cassandra/init_done_flag

# Wait for the init flag file
while [ ! -f /var/lib/cassandra/init_done_flag ]; do
  echo "Waiting for Cassandra initialisation to complete..."
  sleep 5
done

# Start the Spring Boot application
exec java -jar /app.jar
