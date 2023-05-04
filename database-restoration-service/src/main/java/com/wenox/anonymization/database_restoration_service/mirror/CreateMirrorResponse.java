package com.wenox.anonymization.database_restoration_service.mirror;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateMirrorResponse {
    @JsonProperty("db_name")
    private String dbName;
}
