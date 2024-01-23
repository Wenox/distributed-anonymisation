#!/bin/bash

echo "Deleting networks"
yes | docker network prune

# Container IDs to keep
keep_containers=()

# Volumes to keep
keep_volumes=("distributed-anonymization_elasticsearch-data" "distributed-anonymization_grafana-data")

echo "Container IDs to keep:"
for container in "${keep_containers[@]}"; do
    echo "$container"
done

if [ ${#keep_containers[@]} -eq 0 ]; then
    echo "No containers to keep. All containers will be deleted."
fi

echo "Volumes to keep:"
for volume in "${keep_volumes[@]}"; do
    echo "$volume"
done

if [ ${#keep_volumes[@]} -eq 0 ]; then
    echo "No volumes to keep. All volumes will be deleted."
fi

# List all container IDs
all_containers=$(docker ps -aq)

# Loop through all container IDs
for container in ${all_containers[@]}; do

    # Check if the container ID is in the keep_containers array
    should_delete=true
    for keep_container in ${keep_containers[@]}; do
        if [[ "$container" == "$keep_container" ]]; then
            should_delete=false
            break
        fi
    done

    # Delete the container if it's not in the keep_containers array
    if $should_delete; then
        echo "Deleting container: $container"
        docker rm -f $container
    else
        echo "Keeping container: $container"
    fi
done

# List all volume names
all_volumes=$(docker volume ls -q)

# Loop through all volume names
for volume in ${all_volumes[@]}; do
    # Check if the volume is in the keep_volumes array
    should_delete=true
    for keep_volume in ${keep_volumes[@]}; do
        if [[ "$volume" == "$keep_volume" ]]; then
            should_delete=false
            break
        fi
    done

    # Delete the volume if it's not in the keep_volumes array
    if $should_delete; then
        echo "Deleting volume: $volume"
        docker volume rm $volume
    else
        echo "Keeping volume: $volume"
    fi
done

echo "Deleting spark_checkpoint"
rm -rf .spark_checkpoint

echo "Done pruning"