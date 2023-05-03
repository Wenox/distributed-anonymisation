package com.wenox.anonymization.database_restoration_service.mirror;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CreateMirrorRequest {

    @JsonProperty("worksheet_id")
    private String worksheetId;
}
