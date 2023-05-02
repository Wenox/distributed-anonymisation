import httpx
from tenacity import retry, wait_exponential, stop_after_attempt
from datetime import timedelta
from aiobreaker import CircuitBreaker
from logger_config import setup_logger


logger = setup_logger(__name__)


# Configure retries, exponential backoff, and timeout
@retry(
    wait=wait_exponential(multiplier=1, min=2, max=10),
    stop=stop_after_attempt(5),
    reraise=True,
)
async def async_request_with_retries(*args, **kwargs):
    async with httpx.AsyncClient() as client:
        logger.info(f"-----> Request: {args} {kwargs}")
        response = await client.request(*args, **kwargs)
        response.raise_for_status()
        logger.info(f"<----- Response: {response.status_code} {response.json()}")
        return response


# Configure circuit breaker
circuit_breaker = CircuitBreaker(fail_max=3, timeout_duration=timedelta(seconds=30))


async def async_request_with_circuit_breaker_and_retries(*args, **kwargs):
    return await circuit_breaker.call_async(async_request_with_retries, *args, **kwargs)
