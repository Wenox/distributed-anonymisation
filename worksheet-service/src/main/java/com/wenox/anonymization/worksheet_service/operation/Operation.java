package com.wenox.anonymization.worksheet_service.operation;

import lombok.*;
import org.springframework.data.cassandra.core.mapping.*;
import java.io.Serializable;

import static org.springframework.data.cassandra.core.cql.PrimaryKeyType.PARTITIONED;

@Table("operations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Operation implements Serializable {

    @PrimaryKeyClass
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    @Builder
    public static class Key implements Serializable {
        @PrimaryKeyColumn(name = "worksheet_id", type = PARTITIONED)
        private String worksheetId;

        @PrimaryKeyColumn(name = "table", type = PARTITIONED)
        private String table;

        @PrimaryKeyColumn(name = "column", type = PARTITIONED)
        private String column;

        @PrimaryKeyColumn(name = "operation_type", type = PARTITIONED)
        private OperationType operationType;
    }

    @PrimaryKey
    private Key key;

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
