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

        @PrimaryKeyColumn(name = "table_name", type = PARTITIONED)
        private String tableName;

        @PrimaryKeyColumn(name = "column_name", type = PARTITIONED)
        private String columnName;

        @PrimaryKeyColumn(name = "operation_type", type = PARTITIONED)
        private OperationType operationType;
    }

    @PrimaryKey
    private Key key;

    @Column("column_type")
    private String columnType;

    @Column("is_primary_key")
    private boolean isPrimaryKey;

    @Column("is_foreign_key")
    private boolean isForeignKey;

    @Column("is_nullable")
    private boolean isNullable;

    @Column("settings")
    private String settings;
}
