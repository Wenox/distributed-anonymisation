package com.wenox.anonymization.worksheet_service.operation;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OperationRepository extends CrudRepository<Operation, Operation.Key> {
}
