package com.wenox.anonymization.worksheet_service.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateWorksheetResponse {
    Worksheet worksheet;
    Blueprint blueprint;
    Restoration restoration;
    Metadata metadata;
}
