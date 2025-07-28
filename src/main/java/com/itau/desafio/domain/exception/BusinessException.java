package com.itau.desafio.domain.exception;

import org.springframework.http.HttpStatus;

import java.util.Map;

public class BusinessException extends RuntimeException {
    private final HttpStatus status;
    private final Map<String, String> details;

    public BusinessException(String message, HttpStatus status) {
        this(message, status, null);
    }

    public BusinessException(String message, HttpStatus status, Map<String, String> details) {
        super(message);
        this.status = status;
        this.details = details;
    }

    // Getters
    public HttpStatus getStatus() { return status; }
    public Map<String, String> getDetails() { return details; }
}
