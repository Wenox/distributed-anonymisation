from config import RESTORATION_SERVICE_URL, CREATE_MIRROR_ENDPOINT
from http_client import async_request_with_circuit_breaker_and_retries


create_mirror_path = RESTORATION_SERVICE_URL + CREATE_MIRROR_ENDPOINT


async def create_mirror(request):
    try:
        response = await async_request_with_circuit_breaker_and_retries("POST", create_mirror_path, json=request)
        return response.json()
    except Exception as e:
        print(f"Request failed: {e}")
