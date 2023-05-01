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

        /**
         * PK serialization. Example key: SUPPRESSION:employees:salary:worksheet-id
         * */
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(operationType)
                    .append(':')
                    .append(table)
                    .append(':')
                    .append(column)
                    .append(':')
                    .append(worksheetId);
            return sb.toString();
        }

        /**
         * Efficient implementation for deserialization.
         * */
        public static Key from(String serialized) {
            System.out.println("Serialized string: " + serialized);
            if (serialized == null || serialized.isEmpty()) {
                throw new IllegalArgumentException("Serialized string cannot be null or empty: " + serialized);
            }

            int[] colonPositions = new int[3];
            int colonCount = 0;
            for (int i = 0; i < serialized.length() && colonCount < 3; i++) {
                if (serialized.charAt(i) == ':') {
                    colonPositions[colonCount++] = i;
                }
            }
            if (colonCount != 3) {
                throw new IllegalArgumentException("Serialized string should have 4 parts separated by colons: " + serialized);
            }

            String operationTypeString = serialized.substring(0, colonPositions[0]);
            String table = serialized.substring(colonPositions[0] + 1, colonPositions[1]);
            String column = serialized.substring(colonPositions[1] + 1, colonPositions[2]);
            String worksheetId = serialized.substring(colonPositions[2] + 1);

            OperationType operationType;
            try {
                operationType = OperationType.valueOf(operationTypeString);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid operation type in serialized string: " + operationTypeString);
            }

            return new Key(worksheetId, table, column, operationType);
        }
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
