package com.wenox.anonymization.worksheet_service.domain;

public record CreateWorksheetResponse(Blueprint blueprint,
                                      Restoration restoration,
                                      Metadata metadata) {
}
