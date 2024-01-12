import json

import boto3
from fastapi import FastAPI
from prefect import flow, task
from pydantic import BaseModel
from starlette.background import BackgroundTasks
from retry import retry

from config import ANONYMISATION_FRAGMENTS_BUCKET, ANONYMISATION_SCRIPTS_BUCKET, FRAGMENTS_MERGER_LAMBDA
from fragments_population import get_tasks_statuses
from http_client import async_request_with_circuit_breaker_and_retries
from mirror import create_mirror
from outcome import Outcome, outcomes_collection, update_outcome_status, OutcomeStatus, update_to_mirror_ready
from logger_config import setup_logger

app = FastAPI()

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
def check_fragments_readiness(outcome: Outcome):
    try:
        check_fragments_readiness_delegate(outcome)
        return True
    except Exception as e:
        logger.error("Check fragments readiness --- failed")
        return False


@retry(tries=5, delay=1.0, max_delay=1.0, backoff=1, jitter=(0, 0), logger=logger)
def check_fragments_readiness_delegate(outcome: Outcome):
    logger.info(f"-----> Step 3: Checking fragments readiness for worksheet: {outcome.worksheet_id}...")
    response, success = get_tasks_statuses(outcome.worksheet_id)

    if success:
        if response['allSuccessful']:
            logger.info("All anonymisation tasks are completed! Fragments are ready.")
            update_outcome_status(outcome, OutcomeStatus.FRAGMENTS_READY)
            logger.info(f"<----- Step 3: Fragments are ready. Updated outcome to {OutcomeStatus.FRAGMENTS_READY}")
        else:
            logger.info("Not all anonymisation tasks have been completed yet.")
            update_outcome_status(outcome, OutcomeStatus.FRAGMENTS_NOT_READY)
            if is_still_processing_tasks(tasks_by_status=response['tasksByStatus']):
                logger.info(f"<----- Step 3: Some tasks are still processing... Updated outcome to {OutcomeStatus.FRAGMENTS_NOT_READY}.")
                raise Exception("Some tasks are still processing")
            else:
                logger.info(f"<----- Step 3: Some tasks have failed... Updated outcome to {OutcomeStatus.FRAGMENTS_NOT_READY}.")
                raise Exception("Some tasks have failed")
    else:
        update_outcome_status(outcome, OutcomeStatus.FRAGMENTS_NOT_READY)
        logger.info(f"<----- Step 3: Could not verify if all anonymisation tasks have completed. Updated outcome to {OutcomeStatus.FRAGMENTS_NOT_READY}.")
        raise Exception("Could not verify task completion")


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
            "inputBucket": ANONYMISATION_FRAGMENTS_BUCKET,
            "inputDirectory": outcome.worksheet_id,
            "outputBucket": ANONYMISATION_SCRIPTS_BUCKET,
            "outputScript": f"{outcome.outcome_id}.sql"
        }

        logger.info(f"==========> AWS Lambda Request:\n{json.dumps(payload, indent=4)}")
        lambda_response = lambda_client.invoke(
            FunctionName=FRAGMENTS_MERGER_LAMBDA,
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
        response = async_request_with_circuit_breaker_and_retries("POST", anonymization_execution_path,
                                                                  json={"mirrorId": outcome.mirror_id, "filePath": f"{outcome.worksheet_id}/{outcome.outcome_id}.sql"},
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
                                                                  json={"mirrorId": outcome.mirror_id,
                                                                        "dumpPath": f"{outcome.worksheet_id}/{outcome.outcome_id}.sql"},
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

    success = check_fragments_readiness(outcome)
    if not success:
        logger.info("Finishing exporting saga: fragments readiness failed")
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
