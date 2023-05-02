import json
from pydantic import BaseModel
from fastapi import APIRouter
import boto3
from logger_config import setup_logger


logger = setup_logger(__name__)


router = APIRouter()


class LambdaTriggerRequest(BaseModel):
    worksheet_id: str
    output_script: str


INPUT_BUCKET = "anonymisation-fragments"
OUTPUT_BUCKET = "anonymisation-scripts"
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

        logger.info(f"-----> Lambda Request:\n{json.dumps(payload, indent=4)}")
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
        logger.info(f"<----- Lambda Response:\n{json.dumps(response, indent=4)}")
        return response

    except Exception as e:
        logger.error(f"Error triggering Lambda: {e}")
        return {"status": "error", "message": str(e)}

