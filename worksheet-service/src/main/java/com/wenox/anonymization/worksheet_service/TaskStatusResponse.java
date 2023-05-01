package com.wenox.anonymization.worksheet_service;

import com.wenox.anonymization.worksheet_service.operation.OperationByWorksheet;
import com.wenox.anonymization.worksheet_service.operation.TaskStatus;
import lombok.*;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskStatusResponse {

    private String taskId;
    private TaskStatus status;

    public static TaskStatusResponse from(OperationByWorksheet operation) {
        return TaskStatusResponse.builder()
                .taskId(operation.getTaskId())
                .status(operation.getStatus())
                .build();
    }
}
