package com.example.demoApp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class RequestBindingException extends RuntimeException implements ApiErrorsHolder {
    private List<ApiError> apiErrors = new ArrayList<>();

    public RequestBindingException(Errors errors) {
        this(null, errors);
    }

    public RequestBindingException(String s, Errors errors) {
        super(s);
        builErrorsList(errors);
    }

    private void builErrorsList(Errors errors) {
        apiErrors = errors.getFieldErrors().stream()
                .map(fieldError ->
                        new ApiError("Field Error")
//                        .addData("Object", fieldError.getObjectName())
                        .addData("Field", fieldError.getField())
                        .addData("Rejected value", ObjectUtils.nullSafeToString(fieldError.getRejectedValue()))
                        .addData("Reason", fieldError.getDefaultMessage()))
                .collect(Collectors.toList());
    }

    @Override
    public List<ApiError> getErrors() {
        return apiErrors;
    }
}
