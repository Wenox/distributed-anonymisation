import asyncio
import json

from fastapi import FastAPI
from prefect import flow, task
from pydantic import BaseModel

from saga import Saga, saga_collection
from trigger_lambda import router as trigger_lambda_router
from logger_config import setup_logger


app = FastAPI()
app.include_router(trigger_lambda_router)


logger = setup_logger(__name__)


class TriggerRequest(BaseModel):
    worksheet_id: str


@task
def create_saga(worksheet_id: str):
    logger.info(f"Step 1: Creating new Saga for worksheet_id: {worksheet_id}...")
    saga = Saga(worksheet_id)
    saga_collection.insert_one(saga.to_dict())


@task
async def step2():
    await asyncio.sleep(3)
    print("Step 2")


@task
def step3():
    print("Step 3")


@task
def step4():
    print("Step 4")


@task
def step5():
    print("Step 5")


@flow
async def anonymization_saga_workflow(body: TriggerRequest):
    create_saga(body.worksheet_id)
    step2()
    step3()
    step4()
    step5()


@app.post("/api/anonymization-sagas")
async def trigger_anonymization_saga_workflow(body: TriggerRequest):
    logger.info(f"-----> /api/anonymization-sagas: Started anonymization saga workflow. Body:\n{json.dumps(body.dict(), indent=4)}")
    await asyncio.create_task(anonymization_saga_workflow(body))
    return {"service": "anonymization-saga-service"}
