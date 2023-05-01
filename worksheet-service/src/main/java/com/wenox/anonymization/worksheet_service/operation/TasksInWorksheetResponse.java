package com.wenox.anonymization.worksheet_service.operation;

import com.wenox.anonymization.worksheet_service.TaskStatusResponse;

import java.util.List;
import java.util.Map;

public class TasksInWorksheetResponse {

    private Map<TaskStatus, List<TaskStatusResponse>> tasks;
}
