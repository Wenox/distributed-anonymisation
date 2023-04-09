package com.wenox.anonymization.metadata_extraction_service.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.AbstractRequestLoggingFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;


@Slf4j
@Component
public class LoggingFilter extends AbstractRequestLoggingFilter {

    @Value("${logging.filter.request.enabled:true}")
    private boolean shouldLogRequest;

    @Value("${logging.filter.response.enabled:true}")
    private boolean shouldLogResponse;

    private final ObjectMapper objectMapper;

    public LoggingFilter() {
        setIncludeQueryString(true);
        setIncludePayload(true);
        setIncludeHeaders(true);
        setMaxPayloadLength(10000);

        objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Override
    protected void beforeRequest(HttpServletRequest request, String message) {
        log.info(message);
    }

    @Override
    protected void afterRequest(HttpServletRequest request, String message) {
        // Do nothing, since we will handle response logging separately
    }

    @Override
    protected boolean shouldLog(HttpServletRequest request) {
        return shouldLogRequest || shouldLogResponse;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);
        try {
            if (shouldLogRequest) {
                beforeRequest(request, createMessage(request, "-----> Request: ", ""));
            }
            filterChain.doFilter(request, wrappedResponse);
        } finally {
            if (shouldLogResponse) {
                String responseBody = new String(wrappedResponse.getContentAsByteArray());
                int status = wrappedResponse.getStatus();
                HttpStatus httpStatus = HttpStatus.valueOf(status);
                log.info("<----- Response (HTTP {} {}):\n{}", status, httpStatus.getReasonPhrase(), prettyPrintJson(responseBody));
                wrappedResponse.copyBodyToResponse();
            }
        }
    }

    private String prettyPrintJson(String json) {
        try {
            Object jsonObject = objectMapper.readValue(json, Object.class);
            return objectMapper.writeValueAsString(jsonObject);
        } catch (IOException e) {
            log.warn("Failed to pretty-print JSON: {}", json, e);
            return json;
        }
    }
}
