from enum import Enum
from uuid import uuid4, UUID
import pymongo

from config import MONGODB_CONNECTION_URI


class SagaStatus(Enum):
    CREATED = "CREATED"
    MIRROR_READY = "MIRROR_READY"
    FRAGMENTS_READY = "FRAGMENTS_READY"
    MERGE_SUCCESS = "MERGE_SUCCESS"
    ANONYMIZATION_EXECUTED = "ANONYMIZATION_EXECUTED"
    DUMP_GENERATED = "DUMP_GENERATED"
    FINISHED = "FINISHED"
    FAILED = "FAILED"


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


def update_saga_status(saga_id: UUID, status: SagaStatus):
    saga_collection.update_one(
        {"saga_id": str(saga_id)}, {"$set": {"status": status.value}}
    )


# Connect to the MongoDB instance
client = pymongo.MongoClient(MONGODB_CONNECTION_URI)


# Get the database and collection
db = client.anonymization_saga_db
saga_collection = db.saga
