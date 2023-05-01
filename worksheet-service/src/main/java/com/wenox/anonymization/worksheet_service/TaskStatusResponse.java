package com.wenox.anonymization.worksheet_service;

import com.wenox.anonymization.worksheet_service.operation.Operation;
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

    public static TaskStatusResponse from(Operation operation) {
        return TaskStatusResponse.builder()
                .taskId(operation.getKey().getTaskId())
                .status(operation.getStatus())
                .build();
    }
}
