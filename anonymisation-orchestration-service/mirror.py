import httpx

from config import RESTORATION_SERVICE_URL, CREATE_MIRROR_ENDPOINT
from http_client import async_request_with_circuit_breaker_and_retries
from logger_config import setup_logger

logger = setup_logger(__name__)

create_mirror_path = RESTORATION_SERVICE_URL + CREATE_MIRROR_ENDPOINT


def create_mirror(worksheet_id):
    try:
        response = async_request_with_circuit_breaker_and_retries("POST", create_mirror_path, json={"worksheet_id": worksheet_id})
        return response.json(), True
    except httpx.HTTPStatusError as e:
        logger.error(f"Request failed with HTTP error: {e}")
        return None, False
    except Exception as e:
        logger.error(f"Request failed with an unexpected error: {e}")
        return None, False
