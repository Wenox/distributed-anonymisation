package com.wenox.anonymization.worksheet_service.operation;

import lombok.*;
import org.springframework.data.cassandra.core.mapping.*;
import java.io.Serializable;


@Table("operations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Operation implements Serializable {

    @PrimaryKey("task_id")
    private String taskId;

    @Column("worksheet_id")
    private String worksheetId;

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
