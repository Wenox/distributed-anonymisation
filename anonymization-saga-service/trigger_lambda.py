import json
import logging
from pydantic import BaseModel
from fastapi import APIRouter
import boto3

router = APIRouter()

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)


class LambdaTriggerRequest(BaseModel):
    worksheet_id: str
    output_script: str


INPUT_BUCKET = "blueprints-for-anonymisation"
OUTPUT_BUCKET = "blueprints-for-anonymisation"
LAMBDA_NAME = "anonymizationMerger"


@router.post("/trigger-lambda")
async def trigger_lambda(request: LambdaTriggerRequest):
    try:
        # Set up the Lambda client
        lambda_client = boto3.client('lambda', region_name='eu-west-2')

        # Prepare the payload
        payload = {
            "inputBucket": INPUT_BUCKET,
            "inputDirectory": request.worksheet_id,
            "outputBucket": OUTPUT_BUCKET,
            "outputScript": request.output_script,
        }

        # Invoke the Lambda function
        response = lambda_client.invoke(
            FunctionName=LAMBDA_NAME,
            InvocationType='RequestResponse',
            Payload=json.dumps(payload),
        )

        # Log the response
        logger.info(f"Lambda response: {response}")

        return {"status": "success", "response": response}

    except Exception as e:
        logger.error(f"Error triggering Lambda: {e}")
        return {"status": "error", "message": str(e)}

