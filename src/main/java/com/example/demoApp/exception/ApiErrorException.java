package com.example.demoApp.exception;

import org.springframework.http.HttpStatus;

import java.util.List;

public abstract class ApiErrorException extends RuntimeException {
    private final List<ApiError> apiErrors;

    public ApiErrorException(List<ApiError> apiErrors) {
        this.apiErrors =  apiErrors;
    }

    public List<ApiError> getApiErrors() {
        return apiErrors;
    }

    public abstract HttpStatus getStatus();
}
