package com.wenox.anonymization.worksheet_service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class FailureResponse {

    List<ErrorInfo> errors;

    public static FailureResponse toWorksheetClosed(String worksheetId) {
        return new FailureResponse(List.of(ErrorInfo.builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .phrase(HttpStatus.BAD_REQUEST.getReasonPhrase())
                        .description(String.format("Worksheet %s is already closed!", worksheetId))
                .build()));
    }

    public static FailureResponse toTableNotFound(String worksheetId, String table) {
        return new FailureResponse(List.of(ErrorInfo.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .phrase(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .description(String.format("Table '%s' is not found in worksheetId %s!", table, worksheetId))
                .build()));
    }

    public static FailureResponse toColumnNotFound(String worksheetId, String table, String column) {
        return new FailureResponse(List.of(ErrorInfo.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .phrase(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .description(String.format("Column '%s' is not found in table '%s' of worksheetId %s!", column, table, worksheetId))
                .build()));
    }
}
