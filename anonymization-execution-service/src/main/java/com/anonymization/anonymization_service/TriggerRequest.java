package com.anonymization.anonymization_service;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TriggerRequest {
    @NotEmpty
    private String filePath;
    @NotEmpty
    private String databaseName;
}
