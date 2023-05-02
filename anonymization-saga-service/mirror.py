from http_client import async_request_with_circuit_breaker_and_retries


async def create_mirror(request):
    try:
        response = await async_request_with_circuit_breaker_and_retries("POST", "http://localhost:8200/api/v1/restorations/mirror", json=request)
        return response.json()
    except Exception as e:
        print(f"Request failed: {e}")
