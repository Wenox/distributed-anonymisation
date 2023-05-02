#!/bin/bash
# This script deletes all objects in specified S3 buckets

# List of S3 buckets to delete objects from
BUCKETS=("anonymisation-fragments" "anonymisation-scripts" "blueprints-for-anonymisation")

# Function to delete all objects in a given S3 bucket
delete_objects() {
  BUCKET_NAME=$1
  echo "Deleting objects from bucket: ${BUCKET_NAME}"

  # Retrieve list of objects in the bucket
  OBJECTS=$(aws s3api list-objects --bucket "${BUCKET_NAME}" --query 'Contents[].{Key: Key}' --output json)

  # Delete each object in the list
  for OBJECT in $(echo "${OBJECTS}" | jq -r '.[].Key'); do
    aws s3api delete-object --bucket "${BUCKET_NAME}" --key "${OBJECT}"
    echo "Deleted object: ${OBJECT}"
  done

  echo "All objects in bucket ${BUCKET_NAME} have been deleted."
}

# Iterate over the list of S3 buckets and call the delete_objects function
for BUCKET in "${BUCKETS[@]}"; do
  delete_objects "${BUCKET}"
done

echo "Finished deleting objects from all specified buckets."
