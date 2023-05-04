package com.wenox.anonymization.worksheet_service;

import com.wenox.anonymization.worksheet_service.operation.TaskStatus;
import lombok.*;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class TasksInWorksheetResponse {
    private boolean allSuccessful;
    private int numberOfTasks;
    private Map<TaskStatus, List<String>> tasksByStatus;
}
