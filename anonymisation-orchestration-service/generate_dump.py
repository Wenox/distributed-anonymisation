import httpx

from http_client import async_request_with_circuit_breaker_and_retries
from logger_config import setup_logger
from outcome import Outcome


logger = setup_logger(__name__)


def generate_dump(outcome: Outcome):
    try:
        generate_dump_path = "http://anonymisation-execution-service:8500/api/v1/execute-anonymization/generate-dump"
        response = async_request_with_circuit_breaker_and_retries("POST", generate_dump_path,
                                                                  json={"mirrorId": outcome.mirror_id,
                                                                        "dumpPath": f"{outcome.outcome_id}.sql",
                                                                        "restoreMode": outcome.dump_mode.value})
        return response.json(), True
    except httpx.HTTPStatusError as e:
        logger.error(f"Request failed with HTTP error: {e}")
        return None, False
    except Exception as e:
        logger.error(f"Request failed with an unexpected error: {e}")
        return None, False
