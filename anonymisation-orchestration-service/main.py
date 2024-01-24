import json
import tempfile

import boto3
from bson import ObjectId
from fastapi.params import Query
from py_eureka_client import eureka_client
from fastapi import FastAPI, HTTPException
from prefect import flow, task
from pydantic import BaseModel
from pymongo.errors import PyMongoError
from starlette.background import BackgroundTasks
from retry import retry
from starlette.responses import FileResponse

from config import ANONYMISATION_FRAGMENTS_BUCKET, ANONYMISATION_SCRIPTS_BUCKET, FRAGMENTS_MERGER_LAMBDA
from execute_anonymisation import execute_anonymisation
from fragments_population import get_tasks_statuses
from generate_dump import generate_dump
from http_client import async_request_with_circuit_breaker_and_retries
from mirror import create_mirror
from outcome import Outcome, outcomes_collection, update_outcome_status, OutcomeStatus, update_to_mirror_ready, DumpMode
from logger_config import setup_logger

app = FastAPI()

logger = setup_logger(__name__)

eureka_server = "http://eureka-server:8761/eureka/"
app_name = "anonymisation-orchestration-service"
instance_port = 9000
instance_host = "anonymisation-orchestration-service"

@app.on_event("startup")
async def startup_event():
    logger.info(f"Registering with Eureka... App Name: {app_name}")
    await eureka_client.init_async(
        eureka_server=eureka_server,
        app_name=app_name,
        instance_port=instance_port,
        instance_host=instance_host
    )


class TriggerRequest(BaseModel):
    worksheet_id: str
    dump_mode: DumpMode

    def dict(self, *args, **kwargs):
        d = super().dict(*args, **kwargs)
        d['dump_mode'] = self.dump_mode.value
        return d


def initialise_outcome(worksheet_id: str, dump_mode: DumpMode):
    logger.info(f"-----> Step 1: Initialising new outcome for worksheet: {worksheet_id}...")
    outcome = Outcome(worksheet_id, dump_mode)
    outcomes_collection.insert_one(outcome.to_dict())
    logger.info(f"<----- Step 1: Initialised new outcome:\n{json.dumps(outcome.to_dict(), indent=4)}")
    return outcome


@task(name="Create mirror")
def start_create_mirror(outcome: Outcome):
    logger.info(f"-----> Step 2: Creating mirror for worksheet: {outcome.outcome_id}...")
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
    logger.info(f"-----> Step 3: Checking fragments readiness for worksheet: {outcome.outcome_id}...")
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
    logger.info(f"-----> Step 4: Merging anonymisation fragments for worksheet: {outcome.outcome_id}...")
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
def start_execute_anonymisation_script(outcome):
    logger.info(f"-----> Step 5: Executing anonymisation script for worksheet: {outcome.worksheet_id}...")
    response, success = execute_anonymisation(outcome)

    if success:
        update_outcome_status(outcome, OutcomeStatus.SCRIPT_EXECUTED)
        logger.info(f"<----- Step 5: Executed anonymisation script. Updated outcome to {OutcomeStatus.SCRIPT_EXECUTED}.")
    else:
        update_outcome_status(outcome, OutcomeStatus.SCRIPT_EXECUTE_FAILED)
        logger.info(f"<----- Step 5: Failed to execute anonymisation script. Updated outcome to {OutcomeStatus.SCRIPT_EXECUTE_FAILED}.")

    return response, success


@task(name="Generate dump")
def start_generate_dump(outcome):
    logger.info(f"-----> Step 6: Generating anonymisation dump: {outcome.outcome_id}...")
    response, success = generate_dump(outcome)

    if success:
        update_outcome_status(outcome, OutcomeStatus.DUMP_GENERATED)
        logger.info(f"<----- Step 6: Generated dump. Updated outcome to {OutcomeStatus.DUMP_GENERATED}.")
    else:
        update_outcome_status(outcome, OutcomeStatus.DUMP_GENERATION_FAILED)
        logger.info(f"<----- Step 6: Failed to generate dump. Updated outcome to {OutcomeStatus.DUMP_GENERATION_FAILED}.")

    return response, success

@task(name="Generate anonymisation dump")
def generate_anonymisation_dump(outcome):
    logger.info(f"-----> Step 6: Generating anonymisation dump: {outcome.outcome_id}...")
    try:
        generate_dump_path = "http://anonymisation-execution-service:8500/api/v1/execute-anonymization/generate-dump"
        response = async_request_with_circuit_breaker_and_retries("POST", generate_dump_path,
                                                                  json={"mirrorId": outcome.mirror_id,
                                                                        "dumpPath": f"{outcome.outcome_id}.sql",
                                                                        "restoreMode": outcome.restoreMode.value},
                                                                  timeout=60)
        return response.json()
    except Exception as e:
        print(f"Request failed: {e}")

    update_outcome_status(outcome, OutcomeStatus.DUMP_GENERATED)
    logger.info(f"<----- Step 6: Generated anonymisation dump: {OutcomeStatus.DUMP_GENERATED}")


@flow(name="Exporting Process Saga")
def exporting_process_saga(outcome):
    logger.info(f"Exporting saga ----- started ----- outcome : {outcome.outcome_id}")
    response, success = start_create_mirror(outcome)
    if not success:
        logger.info(f"Exporting saga ----- PAUSED: mirror creation failed ----- outcome : {outcome.outcome_id}")
        return

    success = check_fragments_readiness(outcome)
    if not success:
        logger.info(f"Exporting saga ----- PAUSED: fragments not ready ----- outcome : {outcome.outcome_id}")
        return

    response, success = merge_anonymisation_fragments(outcome)
    if not success:
        logger.info(f"Exporting saga ----- PAUSED: merging anonymisation fragments failed ----- outcome : {outcome.outcome_id}")

    success = start_execute_anonymisation_script(outcome)
    if not success:
        logger.info(f"Exporting saga ----- PAUSED: anonymisation script execution failed ----- outcome : {outcome.outcome_id}")
        return

    success = start_generate_dump(outcome)
    if not success:
        logger.info(f"Exporting saga ----- PAUSED: dump generation failed ----- outcome : {outcome.outcome_id}")
        return

    logger.info(f"Exporting saga ----- FINISHED WITH SUCCESS ----- outcome : {outcome.outcome_id}")

@app.post("/api/v1/exporting/start")
async def start_exporting_process(body: TriggerRequest, background_tasks: BackgroundTasks):
    logger.info(f"-----> /api/v1/exporting/start: Started exporting process for worksheet : {json.dumps(body.dict(), indent=4)}")
    outcome = initialise_outcome(body.worksheet_id, body.dump_mode)
    background_tasks.add_task(exporting_process_saga, outcome)
    return outcome.to_dict()


s3_client = boto3.client('s3')


def find_outcome(outcome_id: str):
    try:
        outcome = outcomes_collection.find_one({"outcomeId": outcome_id})
        if not outcome:
            raise ValueError(f"Outcome with ID {outcome_id} not found.")

        return {k: str(v) if isinstance(v, ObjectId) else v for k, v in outcome.items()}

    except ValueError as ve:
        logger.error(ve)
        raise HTTPException(status_code=404, detail=str(ve))
    except PyMongoError as pe:
        logger.error(f"Database error occurred: {pe}")
        raise HTTPException(status_code=500, detail="Database error")

@app.get("/api/v1/outcomes")
async def get_outcome(outcome_id: str = Query(..., description="The ID of the outcome to retrieve")):
    logger.info(f"-----> /api/v1/outcomes: Retrieving outcome with ID: {outcome_id}")
    return find_outcome(outcome_id)


@app.get("/api/v1/outcomes/download")
async def download_outcome_file(outcome_id: str):
    logger.info(f"-----> /api/v1/outcomes/download: Started downloading dump for outcome : {outcome_id}")
    bucket_name = "anonymisation-dumps"
    file_extensions = ['.sql', '.dump']

    for ext in file_extensions:
        file_path = f"{outcome_id}{ext}"
        try:
            logger.info(f"Trying file_path : {file_path}")
            s3_client.head_object(Bucket=bucket_name, Key=file_path)
        except s3_client.exceptions.ClientError:
            logger.info(f"Not found file_path : {file_path}")
            continue
        else:
            logger.info(f"Matched file_path : {file_path}")
            with tempfile.NamedTemporaryFile(delete=False) as temp_file:
                s3_client.download_file(bucket_name, file_path, temp_file.name)
                return FileResponse(path=temp_file.name, filename=file_path, media_type="application/octet-stream")

    logger.error(f"Final result: Not found dump for outcome : {outcome_id}")
    raise HTTPException(status_code=404, detail=f"File for outcome_id {outcome_id} not found in S3 bucket.")
