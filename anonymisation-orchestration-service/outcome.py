from enum import Enum
from uuid import uuid4, UUID
import pymongo

from config import MONGODB_CONNECTION_URI


class OutcomeStatus(Enum):
    INITIALISED = "INITIALISED"
    MIRROR_READY = "MIRROR_READY"
    FRAGMENTS_READY = "FRAGMENTS_READY"
    FRAGMENTS_MERGED = "FRAGMENTS_MERGED"
    SCRIPT_EXECUTED = "SCRIPT_EXECUTED"
    DUMP_GENERATED = "DUMP_GENERATED"

    MIRROR_FAILED = "MIRROR_FAILED"
    FRAGMENTS_NOT_READY = "FRAGMENTS_NOT_READY"
    FRAGMENTS_MERGE_FAILED = "FRAGMENTS_MERGE_FAILED"
    SCRIPT_EXECUTE_FAILED = "SCRIPT_EXECUTE_FAILED"
    DUMP_GENERATION_FAILED = "DUMP_GENERATION_FAILED"
    FINISHED = "FINISHED"
    FAILED = "FAILED"


class DumpMode(Enum):
    SCRIPT = "SCRIPT"
    ARCHIVE = "ARCHIVE"


class Outcome:
    def __init__(self, worksheet_id: str,
                 dump_mode: DumpMode,
                 status: OutcomeStatus = OutcomeStatus.INITIALISED,
                 mirror_id: str = None):
        self.outcome_id = uuid4()
        self.dump_mode = dump_mode
        self.status = status
        self.worksheet_id = worksheet_id
        self.mirror_id = mirror_id

    def to_dict(self):
        data = {
            "outcomeId": str(self.outcome_id),
            "status": self.status.value,
            "dumpMode": self.dump_mode.value,
            "worksheetId": self.worksheet_id,
        }
        if self.mirror_id:
            data["mirrorId"] = self.mirror_id
        return data


def update_outcome_status(outcome: Outcome, status: OutcomeStatus):
    outcome.status = status
    outcomes_collection.update_one(
        {"outcomeId": str(outcome.outcome_id)}, {"$set": {"status": status.value}}
    )


def update_to_mirror_ready(outcome: Outcome, mirror_id: str):
    outcome.status = OutcomeStatus.MIRROR_READY
    outcome.mirror_id = mirror_id
    outcomes_collection.update_one(
        {"outcomeId": str(outcome.outcome_id)},
        {"$set": {"status": OutcomeStatus.MIRROR_READY.value, "mirrorId": mirror_id}}
    )


# Connect to the MongoDB instance
client = pymongo.MongoClient(MONGODB_CONNECTION_URI)


# Get the database and collection
db = client.ANONYMISATION_ORCHESTRATION_DB
outcomes_collection = db.outcomes
