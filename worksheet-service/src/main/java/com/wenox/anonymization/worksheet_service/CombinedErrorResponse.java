package com.wenox.anonymization.worksheet_service;

import org.springframework.http.HttpStatusCode;

public class CombinedErrorResponse  {
    private final HttpStatusCode status;
    private final String errorMessage;

    public CombinedErrorResponse(HttpStatusCode status, String errorMessage) {
        this.status = status;
        this.errorMessage = errorMessage;
    }

    public HttpStatusCode getStatus() {
        return status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public String toString() {
        return "CombinedErrorResponse{" +
                "status=" + status +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}