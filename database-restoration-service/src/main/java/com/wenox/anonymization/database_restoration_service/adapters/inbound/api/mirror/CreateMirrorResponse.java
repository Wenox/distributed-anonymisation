package com.wenox.anonymization.database_restoration_service.adapters.inbound.api.mirror;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
class CreateMirrorResponse {

    @JsonProperty("db_name")
    private String db;
}
