package com.wenox.anonymization.worksheet_service.operation;

import lombok.*;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import java.io.Serializable;

import static org.springframework.data.cassandra.core.cql.PrimaryKeyType.CLUSTERED;
import static org.springframework.data.cassandra.core.cql.PrimaryKeyType.PARTITIONED;

@PrimaryKeyClass
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OperationKey implements Serializable {

    @PrimaryKeyColumn(name = "worksheet_id", type = PARTITIONED)
    private String worksheetId;

    @PrimaryKeyColumn(name = "task_id", type = CLUSTERED)
    private String taskId;

}