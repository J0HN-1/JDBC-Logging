package com.example.demoApp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;
import java.util.List;

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class ValidationException extends RuntimeException implements ApiErrorsHolder {
    private List<ApiError> apiErrors = new ArrayList<>();

    public ValidationException(List<ApiError> validationErrors) {
        this(null, validationErrors);
    }

    public ValidationException(String s, List<ApiError> validationErrors) {
        super(s);
        apiErrors = validationErrors;
    }

    @Override
    public List<ApiError> getErrors() {
        return apiErrors;
    }
}
