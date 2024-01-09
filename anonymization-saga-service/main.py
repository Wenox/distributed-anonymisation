import datetime
import json

import boto3
from fastapi import FastAPI
from prefect import flow, task
from pydantic import BaseModel
from starlette.background import BackgroundTasks
from retry import retry
from fragments_population import get_tasks_statuses
from http_client import async_request_with_circuit_breaker_and_retries
from mirror import create_mirror
from saga import Saga, saga_collection, update_saga_status, SagaStatus
from trigger_lambda import router as trigger_lambda_router, INPUT_BUCKET, OUTPUT_BUCKET, LAMBDA_NAME
from logger_config import setup_logger

app = FastAPI()
app.include_router(trigger_lambda_router)

logger = setup_logger(__name__)


class TriggerRequest(BaseModel):
    worksheet_id: str


def create_saga(worksheet_id: str):
    logger.info(f"-----> Step 1: Creating new Saga for worksheet_id: {worksheet_id}...")
    saga = Saga(worksheet_id)
    saga_collection.insert_one(saga.to_dict())
    logger.info(f"<----- Step 1: Created new Saga:\n{json.dumps(saga.to_dict(), indent=4)}")
    return saga


@task(name="Create mirror")
def start_create_mirror(saga: Saga):
    logger.info(f"-----> Step 1: Creating mirror for worksheet_id: {saga.worksheet_id}...")
    response = create_mirror(saga.worksheet_id)
    update_saga_status(saga.saga_id, SagaStatus.MIRROR_READY)
    logger.info(f"<----- Step 1: Created mirror. Updated saga to {SagaStatus.MIRROR_READY}.")
    return response


@task(name="Check tasks status")
@retry(tries=5, delay=1.0, max_delay=1.0, backoff=1, jitter=(0, 0), logger=logger)
def tasks_status(saga: Saga):
    logger.info(f"-----> Step 2: Checking tasks status for worksheet_id: {saga.worksheet_id}...")
    response = get_tasks_statuses(saga.worksheet_id)

    if response['allSuccessful']:
        logger.info("All tasks completed!")
        update_saga_status(saga.saga_id, SagaStatus.TASKS_COMPLETED)
    else:
        logger.info("Some tasks not successful...")
        if is_still_processing_tasks(tasks_by_status=response['tasksByStatus']):
            logger.info("Still processing some tasks...")
            # Re-run the task up to 7 times, timeout 3000 ms
            raise Exception("Still processing some tasks...")
        else:
            logger.info("Some tasks were failed.")
            # Re-run the task up to 4 times, timeout 1000 ms
            raise Exception("Some tasks were failed, retrying...")

    logger.info(f"<----- Step 2: Retrieved fragments. Updated saga to {SagaStatus.TASKS_COMPLETED}")


def is_still_processing_tasks(tasks_by_status):
    return len(tasks_by_status['STARTED']) > 0 or \
        len(tasks_by_status['EXTRACTED']) > 0 or \
        len(tasks_by_status['TRANSFORMED_ANONYMIZATION']) > 0 or \
        len(tasks_by_status['TRANSFORMED_SQL_SCRIPT']) > 0


@task(name="Merge anonymization fragments")
def merge_anonymization_fragments(saga: Saga):
    logger.info(f"-----> Step 3: Merging anonymization fragments worksheet_id: {saga.worksheet_id}...")
    try:
        # Set up the Lambda client
        lambda_client = boto3.client('lambda', region_name='eu-west-2')

        # Prepare the payload
        payload = {
            "inputBucket": INPUT_BUCKET,
            "inputDirectory": saga.worksheet_id,
            "outputBucket": OUTPUT_BUCKET,
            "outputScript": 'result-output.sql',
        }

        logger.info(f"==========> AWS Lambda Request:\n{json.dumps(payload, indent=4)}")
        # Invoke the Lambda function
        lambda_response = lambda_client.invoke(
            FunctionName=LAMBDA_NAME,
            InvocationType='RequestResponse',
            Payload=json.dumps(payload),
        )

        # Read and parse the response payload
        response_payload = json.loads(lambda_response['Payload'].read().decode('utf-8'))

        # Log the response
        response = {"status": "success", "response": response_payload}
        logger.info(f"<========== AWS Lambda Response:\n{json.dumps(response, indent=4)}")

    except Exception as e:
        logger.error(f"Error triggering Lambda: {e}")

    update_saga_status(saga.saga_id, SagaStatus.MERGE_SUCCESS)
    logger.info(f"<----- Step 3: Merged anonymization fragments. Updated saga to {SagaStatus.MERGE_SUCCESS}")


@task(name="Execute anonymization script")
def execute_anonymization_script(saga, db_name):
    logger.info(f"-----> Step 4: Executing anonymization script: {saga.worksheet_id}...")
    try:
        anonymization_execution_path = "http://localhost:8500/api/v1/execute-anonymization"
        file_name = f"{saga.worksheet_id}/result-output.sql"
        response = async_request_with_circuit_breaker_and_retries("POST", anonymization_execution_path,
                                                                  json={"db_name": db_name, "file_path": file_name},
                                                                  timeout=60)
        return response.json()
    except Exception as e:
        print(f"Request failed: {e}")

    update_saga_status(saga.saga_id, SagaStatus.ANONYMIZATION_EXECUTED)
    logger.info(f"<----- Step 4: Executed anonymization script: {SagaStatus.ANONYMIZATION_EXECUTED}")


@task(name="Generate anonymization dump")
def generate_anonymization_dump(saga, db_name):
    logger.info(f"-----> Step 5: Generating anonymization dump: {saga.worksheet_id}...")
    try:
        generate_dump_path = "http://localhost:8500/api/v1/execute-anonymization/generate-dump"
        response = async_request_with_circuit_breaker_and_retries("POST", generate_dump_path,
                                                                  json={"db_name": db_name},
                                                                  timeout=60)
        return response.json()
    except Exception as e:
        print(f"Request failed: {e}")

    update_saga_status(saga.saga_id, SagaStatus.DUMP_GENERATED)
    logger.info(f"<----- Step 5: Generated anonymization dump: {SagaStatus.DUMP_GENERATED}")


@flow(name="Anonymization Saga Workflow")
def anonymization_saga_workflow(saga):
    response = start_create_mirror(saga)
    logger.info(f"Returned response {response}")
    tasks_status(saga)
    merge_anonymization_fragments(saga)
    execute_anonymization_script(saga, response['db_name'])
    generate_anonymization_dump(saga, response['db_name'])


@app.post("/api/anonymization-sagas")
async def trigger_anonymization_saga_workflow(body: TriggerRequest, background_tasks: BackgroundTasks):
    logger.info(
        f"-----> /api/anonymization-sagas: Started anonymization saga workflow. Body:\n{json.dumps(body.dict(), indent=4)}")
    saga = create_saga(body.worksheet_id)
    background_tasks.add_task(anonymization_saga_workflow, saga)
    return saga.to_dict()
