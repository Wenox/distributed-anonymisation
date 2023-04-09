package com.wenox.anonymization.worksheet_service;

import jakarta.validation.constraints.NotEmpty;

public record CreateWorksheetRequest(@NotEmpty String blueprintId) {

}
