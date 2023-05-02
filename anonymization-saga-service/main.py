import asyncio
from fastapi import FastAPI
from prefect import flow, task
from pydantic import BaseModel
from trigger_lambda import router as trigger_lambda_router
from logger_config import setup_logger


app = FastAPI()
app.include_router(trigger_lambda_router)


logger = setup_logger(__name__)


@task
def step1():
    print("Step 1")


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
async def workflow():
    step1()
    step2()
    step3()
    step4()
    step5()


class TriggerRequest(BaseModel):
    message: str


@app.post("/trigger-workflow")
async def trigger_workflow(body: TriggerRequest):
    print('Request body:', body)
    await asyncio.create_task(workflow())
    return {"service": "anonymization-saga-service"}
