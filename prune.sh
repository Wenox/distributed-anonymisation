#!/bin/bash

# Container IDs to keep
keep_containers=("abafc6b68816" "8ec68ed4b489" "ed2cc38b2163")

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

docker volume prune -f

echo "Done pruning"