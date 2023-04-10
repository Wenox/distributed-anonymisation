package com.wenox.anonymization.worksheet_service;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
public class ErrorInfo {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer status;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String phrase;
    private String description;
    private String reason;
    private String exception;
}
