package com.wenox.anonymization.worksheet_service.operation;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OperationRepository extends CassandraRepository<Operation, OperationKey> {

    @Query("SELECT * FROM operations WHERE worksheet_id = ?0")
    List<Operation> findByWorksheetId(String worksheetId);

    @Query("SELECT * FROM operations WHERE task_id = ?0")
    Optional<Operation> findByTaskId(String taskId);
}