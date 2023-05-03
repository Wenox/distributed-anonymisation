from config import FRAGMENTS_URL
from http_client import async_request_with_circuit_breaker_and_retries


async def check_fragments_status(worksheet_id: str):
    try:
        check_status_path = FRAGMENTS_URL.format(worksheet_id=worksheet_id)
        response = await async_request_with_circuit_breaker_and_retries("GET", check_status_path)
        return response.json()
    except Exception as e:
        print(f"Request failed: {e}")
