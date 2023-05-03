from config import RESTORATION_SERVICE_URL, CREATE_MIRROR_ENDPOINT
from http_client import async_request_with_circuit_breaker_and_retries

create_mirror_path = RESTORATION_SERVICE_URL + CREATE_MIRROR_ENDPOINT


def create_mirror(worksheet_id):
    try:
        response = async_request_with_circuit_breaker_and_retries("POST", create_mirror_path,
                                                                        json={"worksheet_id": worksheet_id})
        return response.json()
    except Exception as e:
        print(f"Request failed: {e}")
