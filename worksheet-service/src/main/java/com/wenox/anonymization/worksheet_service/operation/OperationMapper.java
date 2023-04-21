package com.wenox.anonymization.worksheet_service.operation;

import com.wenox.anonymization.worksheet_service.WorksheetMapper;
import com.wenox.anonymization.worksheet_service.domain.Column;
import com.wenox.anonymization.worksheet_service.domain.Metadata;
import com.wenox.anonymization.worksheet_service.domain.Table;
import com.wenox.anonymization.worksheet_service.domain.Worksheet;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OperationMapper {

    private final WorksheetMapper worksheetMapper;

    public Operation toOperation(Worksheet worksheet, AddSuppressionRequest request) {
        return Operation.builder()
                .key(Operation.Key.builder()
                        .worksheetId(worksheet.getWorksheetId())
                        .tableName(request.getTable())
                        .columnName(request.getColumn())
                        .operationType(OperationType.SUPPRESSION)
                        .build())
                .columnType(Optional.ofNullable(worksheet.getMetadata())
                        .map(Metadata::tables)
                        .map(tables -> tables.get(request.getTable()))
                        .map(Table::getColumns)
                        .map(columns -> columns.get(request.getColumn()))
                        .map(Column::getType)
                        .orElse(null))
                .build();
    }

    public AddOperationResponse toResponse(Operation operation, Worksheet worksheet) {
        return AddOperationResponse.builder()
                .worksheet(worksheetMapper.toResponse(worksheet))
                .worksheetId(operation.getKey().getWorksheetId())
                .tableName(operation.getKey().getTableName())
                .columnName(operation.getKey().getColumnName())
                .operationType(operation.getKey().getOperationType())
                .columnType(operation.getColumnType())
                .settings(operation.getSettings())
                .build();
    }
}
