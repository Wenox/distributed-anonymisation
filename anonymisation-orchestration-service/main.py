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
from outcome import Outcome, outcomes_collection, update_outcome_status, OutcomeStatus, update_to_mirror_ready
from trigger_lambda import router as trigger_lambda_router, INPUT_BUCKET, OUTPUT_BUCKET, LAMBDA_NAME
from logger_config import setup_logger

app = FastAPI()
app.include_router(trigger_lambda_router)

logger = setup_logger(__name__)


class TriggerRequest(BaseModel):
    worksheet_id: str


def initialize_outcome(worksheet_id: str):
    logger.info(f"-----> Step 1: Initializing new outcome for worksheet: {worksheet_id}...")
    outcome = Outcome(worksheet_id)
    outcomes_collection.insert_one(outcome.to_dict())
    logger.info(f"<----- Step 1: Initialized new outcome:\n{json.dumps(outcome.to_dict(), indent=4)}")
    return outcome


@task(name="Create mirror")
def start_create_mirror(outcome: Outcome):
    logger.info(f"-----> Step 2: Creating mirror for worksheet: {outcome.worksheet_id}...")
    response, success = create_mirror(outcome.worksheet_id)

    if success:
        update_to_mirror_ready(outcome, response.get('mirrorId'))
        logger.info(f"<----- Step 2: Created mirror. Updated outcome to {OutcomeStatus.MIRROR_READY}.")
    else:
        update_outcome_status(outcome, OutcomeStatus.MIRROR_FAILED)
        logger.info(f"<----- Step 2: Failed to create mirror. Updated outcome to {OutcomeStatus.MIRROR_FAILED}.")

    return response, success



@task(name="Check fragments readiness")
@retry(tries=5, delay=1.0, max_delay=1.0, backoff=1, jitter=(0, 0), logger=logger)
def check_fragments_readiness(outcome: Outcome):
    logger.info(f"-----> Step 3: Checking fragments readiness for worksheet: {outcome.worksheet_id}...")
    response, success = get_tasks_statuses(outcome.worksheet_id)

    if success:
        if response['allSuccessful']:
            logger.info("All anonymisation tasks are completed! Fragments are ready.")
            update_outcome_status(outcome, OutcomeStatus.FRAGMENTS_READY)
            logger.info(f"<----- Step 3: Fragments are ready. Updated outcome to {OutcomeStatus.FRAGMENTS_READY}")
        else:
            logger.info("Some anonymisation tasks not successful...")
            if is_still_processing_tasks(tasks_by_status=response['tasksByStatus']):
                logger.info("Still processing some anonymisation tasks...")
                raise Exception("Still processing some anonymisation tasks...")
            else:
                logger.info("Some anonymisation tasks were failed.")
                raise Exception("Some anonymisation tasks were failed, retrying...")
    else:
        update_outcome_status(outcome, OutcomeStatus.FRAGMENTS_NOT_READY)
        logger.info(f"<----- Step 3: Fragments are not yet ready. Updated outcome to {OutcomeStatus.FRAGMENTS_NOT_READY}.")

    return response, success


def is_still_processing_tasks(tasks_by_status):
    return len(tasks_by_status['STARTED']) > 0 or \
        len(tasks_by_status['EXTRACTED']) > 0 or \
        len(tasks_by_status['TRANSFORMED_ANONYMIZATION']) > 0 or \
        len(tasks_by_status['TRANSFORMED_SQL_SCRIPT']) > 0


@task(name="Merge anonymisation fragments")
def merge_anonymisation_fragments(outcome: Outcome):
    logger.info(f"-----> Step 4: Merging anonymisation fragments for worksheet: {outcome.worksheet_id}...")
    try:
        lambda_client = boto3.client('lambda', region_name='eu-west-2')
        payload = {
            "inputBucket": INPUT_BUCKET,
            "inputDirectory": outcome.worksheet_id,
            "outputBucket": OUTPUT_BUCKET,
            "outputScript": 'result-output.sql',
        }

        logger.info(f"==========> AWS Lambda Request:\n{json.dumps(payload, indent=4)}")
        lambda_response = lambda_client.invoke(
            FunctionName=LAMBDA_NAME,
            InvocationType='RequestResponse',
            Payload=json.dumps(payload),
        )
        response_payload = json.loads(lambda_response['Payload'].read().decode('utf-8'))
        logger.info(f"<========== AWS Lambda Response:\n{json.dumps(response_payload, indent=4)}")

        update_outcome_status(outcome, OutcomeStatus.FRAGMENTS_MERGED)
        logger.info(f"<----- Step 4: Merged anonymisation fragments. Updated outcome to {OutcomeStatus.FRAGMENTS_MERGED}")
        return response_payload, True

    except Exception as e:
        logger.error(f"Error triggering Lambda: {e}")
        update_outcome_status(outcome, OutcomeStatus.FRAGMENTS_MERGE_FAILED)
        logger.info(f"<----- Step 4: Merging anonymisation fragments failed. Updated outcome to {OutcomeStatus.FRAGMENTS_MERGE_FAILED}")
        return None, False

@task(name="Execute anonymisation script")
def execute_anonymisation_script(outcome):
    logger.info(f"-----> Step 5: Executing anonymisation script for worksheet: {outcome.worksheet_id}...")
    try:
        anonymization_execution_path = "http://localhost:8500/api/v1/execute-anonymization"
        file_name = f"{outcome.worksheet_id}/result-output.sql"
        response = async_request_with_circuit_breaker_and_retries("POST", anonymization_execution_path,
                                                                  json={"mirrorId": outcome.mirror_id, "file_path": file_name},
                                                                  timeout=60)
        return response.json()
    except Exception as e:
        print(f"Request failed: {e}")

    update_outcome_status(outcome, OutcomeStatus.SCRIPT_EXECUTED)
    logger.info(f"<----- Step 5: Executed anonymisation script: {OutcomeStatus.SCRIPT_EXECUTED}")


@task(name="Generate anonymisation dump")
def generate_anonymisation_dump(outcome):
    logger.info(f"-----> Step 6: Generating anonymisation dump: {outcome.worksheet_id}...")
    try:
        generate_dump_path = "http://localhost:8500/api/v1/execute-anonymization/generate-dump"
        response = async_request_with_circuit_breaker_and_retries("POST", generate_dump_path,
                                                                  json={"mirrorId": outcome.mirror_id},
                                                                  timeout=60)
        return response.json()
    except Exception as e:
        print(f"Request failed: {e}")

    update_outcome_status(outcome, OutcomeStatus.DUMP_GENERATED)
    logger.info(f"<----- Step 6: Generated anonymisation dump: {OutcomeStatus.DUMP_GENERATED}")


@flow(name="Exporting Process Saga")
def exporting_process_saga(outcome):
    response, success = start_create_mirror(outcome)
    if not success:
        logger.info("Finishing exporting saga: mirror creation failed")
        return

    response, success = check_fragments_readiness(outcome)
    if not success:
        logger.info("Finishing exporting saga: some fragments are not ready")
        return

    response, success = merge_anonymisation_fragments(outcome)
    if not success:
        logger.info("Finishing exporting saga: merging anonymisation fragments failed")

    execute_anonymisation_script(outcome)
    generate_anonymisation_dump(outcome)


@app.post("/api/v1/exporting/start")
async def start_exporting_process(body: TriggerRequest, background_tasks: BackgroundTasks):
    logger.info(f"-----> /api/v1/exporting/start: Started exporting process for worksheet : {json.dumps(body.dict(), indent=4)}")
    outcome = initialize_outcome(body.worksheet_id)
    background_tasks.add_task(exporting_process_saga, outcome)
    return outcome.to_dict()
