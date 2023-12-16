package com.wenox.anonymization.database_restoration_service.adapters.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
class CreateMirrorRequest {

    @JsonProperty("worksheet_id")
    private String worksheetId;
}
