package com.wenox.anonymization.worksheet_service.operation;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OperationByWorksheetRepository extends CrudRepository<OperationByWorksheet, String> {

    List<OperationByWorksheet> findByWorksheetId(String worksheetId);
}