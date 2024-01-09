package com.anonymization.anonymization_service;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("file_path")
    private String filePath;
    @NotEmpty
    @JsonProperty("db_name")
    private String db;
}
