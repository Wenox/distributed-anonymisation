import httpx

from config import FRAGMENTS_URL
from http_client import async_request_with_circuit_breaker_and_retries
from logger_config import setup_logger

logger = setup_logger(__name__)


def get_tasks_statuses(worksheet_id: str):
    try:
        check_status_path = FRAGMENTS_URL.format(worksheet_id=worksheet_id)
        response = async_request_with_circuit_breaker_and_retries("GET", check_status_path)
        return response.json(), True
    except httpx.HTTPStatusError as e:
        logger.error(f"Request failed with HTTP error: {e}")
        return None, False
    except Exception as e:
        logger.error(f"Request failed with an unexpected error: {e}")
        return None, False


