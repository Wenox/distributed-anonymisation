from enum import Enum
from uuid import uuid4
import pymongo


# Define the status Enum
class SagaStatus(Enum):
    CREATED = "CREATED"
    IN_PROGRESS = "IN_PROGRESS"
    COMPLETED = "COMPLETED"
    FAILED = "FAILED"


# Connect to the MongoDB instance
client = pymongo.MongoClient(
    "mongodb://root:example@localhost:27023/anonymization_saga_db?authSource=admin"
)


# Get the database and collection
db = client.anonymization_saga_db
saga_collection = db.saga


# Define the Saga entity
class Saga:
    def __init__(self, worksheet_id: str, status: SagaStatus = SagaStatus.CREATED):
        self.saga_id = uuid4()
        self.status = status
        self.worksheet_id = worksheet_id

    def to_dict(self):
        return {
            "saga_id": str(self.saga_id),
            "status": self.status.value,
            "worksheet_id": self.worksheet_id,
        }
