import asyncio
import json

import httpx
from tenacity import retry, wait_exponential, stop_after_attempt
from datetime import timedelta
from aiobreaker import CircuitBreaker
from logger_config import setup_logger


logger = setup_logger(__name__)


@retry(
    wait=wait_exponential(multiplier=1.5, min=1, max=1),
    # wait=wait_exponential(multiplier=1.5, min=2, max=10),
    stop=stop_after_attempt(3),
    reraise=True,
)
async def async_request_with_retries(*args, timeout=10, **kwargs):
    async with httpx.AsyncClient(timeout=timeout) as client:
        logger.info(f"==========> Request: {args} {kwargs}")
        response = await client.request(*args, **kwargs)
        logger.info(f"<========== Response: Status {response.status_code}\n{json.dumps(response.json(), indent=4)[:100]}")
        response.raise_for_status()
        return response


circuit_breaker = CircuitBreaker(fail_max=5, timeout_duration=timedelta(seconds=30))


def async_request_with_circuit_breaker_and_retries(*args, timeout=10, **kwargs):
    return asyncio.run(circuit_breaker.call_async(async_request_with_retries, *args, timeout=timeout, **kwargs))
