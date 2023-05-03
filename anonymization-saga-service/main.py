import asyncio
import json
import time

from fastapi import FastAPI
from prefect import flow, task
from prefect.task_runners import SequentialTaskRunner
from pydantic import BaseModel

from fragments_population import check_fragments_status
from mirror import create_mirror
from saga import Saga, saga_collection, update_saga_status, SagaStatus
from trigger_lambda import router as trigger_lambda_router
from logger_config import setup_logger

app = FastAPI()
app.include_router(trigger_lambda_router)

logger = setup_logger(__name__)


class TriggerRequest(BaseModel):
    worksheet_id: str


@task(name="Initialize Saga")
def create_saga(worksheet_id: str):
    logger.info(f"-----> Step 1: Creating new Saga for worksheet_id: {worksheet_id}...")
    saga = Saga(worksheet_id)
    saga_collection.insert_one(saga.to_dict())
    logger.info(f"<----- Step 1: Created new Saga:\n{json.dumps(saga.to_dict(), indent=4)}")
    return saga


@task(name="Create mirror")
def start_create_mirror(saga: Saga):
    logger.info(f"-----> Step 2: Creating mirror for worksheet_id: {saga.worksheet_id}...")
    create_mirror(saga.worksheet_id)
    update_saga_status(saga.saga_id, SagaStatus.MIRROR_READY)
    logger.info(f"<----- Step 2: Created mirror. Updated saga to {SagaStatus.MIRROR_READY}.")


@task(name="Check fragments status")
def fragments_population(saga: Saga):
    logger.info(f"-----> Step 3: Checking fragments status for worksheet_id: {saga.worksheet_id}...")
    check_fragments_status(saga.worksheet_id)
    update_saga_status(saga.saga_id, SagaStatus.FRAGMENTS_READY)
    logger.info(f"<----- Step 3: Retrieved fragments. Updated saga to {SagaStatus.FRAGMENTS_READY}")


@task
def step4():
    print("Step 4")


@task
def step5():
    print("Step 5")


@flow(name="Anonymization Saga Workflow")
def anonymization_saga_workflow(body: TriggerRequest):
    saga = create_saga(body.worksheet_id)
    start_create_mirror(saga)
    fragments_population(saga)
    step4()
    step5()


@app.post("/api/anonymization-sagas")
async def trigger_anonymization_saga_workflow(body: TriggerRequest):
    logger.info(f"-----> /api/anonymization-sagas: Started anonymization saga workflow. Body:\n{json.dumps(body.dict(), indent=4)}")
    anonymization_saga_workflow(body)
    return {"service": "anonymization-saga-service"}
