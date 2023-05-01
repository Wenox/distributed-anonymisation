package com.wenox.anonymization.worksheet_service.operation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.io.Serializable;

import static org.springframework.data.cassandra.core.cql.Ordering.ASCENDING;
import static org.springframework.data.cassandra.core.cql.PrimaryKeyType.CLUSTERED;
import static org.springframework.data.cassandra.core.cql.PrimaryKeyType.PARTITIONED;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("operations_by_worksheet")
public class OperationByWorksheet implements Serializable {

    @PrimaryKeyColumn(name = "worksheet_id", ordinal = 0, type = PARTITIONED)
    private String worksheetId;

    @PrimaryKeyColumn(name = "task_id", ordinal = 1, type = CLUSTERED, ordering = ASCENDING)
    private String taskId;

    @Column("table_name")
    private String table;

    @Column("column_name")
    private String column;

    @Column("operation_type")
    private OperationType operationType;

    @Column("task_status")
    private TaskStatus status;

    @Column("column_type")
    private String columnType;

    @Column("primary_key")
    private String primaryKey;

    @Column("primary_key_type")
    private String primaryKeyType;

    @Column("settings")
    private String settings;
}
