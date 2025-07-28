package com.itau.desafio.domain.exception;

import org.springframework.http.HttpStatus;

public class FraudApiException extends RuntimeException {
    private final HttpStatus statusCode;
    private final String errorDetails;

    public FraudApiException(String message, HttpStatus statusCode, String errorDetails) {
        super(message);
        this.statusCode = statusCode;
        this.errorDetails = errorDetails;
    }

    public FraudApiException(String message, HttpStatus statusCode, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
        this.errorDetails = cause != null ? cause.getMessage() : null;
    }

    public HttpStatus getStatusCode() {
        return statusCode;
    }

    public String getErrorDetails() {
        return errorDetails;
    }

    @Override
    public String toString() {
        return "FraudApiException{" +
                "statusCode=" + statusCode +
                ", errorDetails='" + errorDetails + '\'' +
                ", message='" + getMessage() + '\'' +
                '}';
    }
}