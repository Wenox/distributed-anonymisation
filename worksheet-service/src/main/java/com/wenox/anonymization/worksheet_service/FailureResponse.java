package com.wenox.anonymization.worksheet_service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class FailureResponse {

    List<ErrorInfo> errors;
}
