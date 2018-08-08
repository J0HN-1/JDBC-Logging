package com.example.demoApp.exception;

import com.example.demoApp.exception.ApiError;
import com.example.demoApp.exception.ApiErrorsHolder;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collections;
import java.util.List;

@RestControllerAdvice
public class ExceptionsHandler {

    @ExceptionHandler
    public List<ApiError> exceptionHandler(Exception exception) {
        if (exception instanceof ApiErrorsHolder) {
            return ((ApiErrorsHolder)exception).getErrors();
        }
        Throwable rootCause = ExceptionUtils.getRootCause(exception);
        return Collections.singletonList(new ApiError(rootCause.getClass().getSimpleName(), rootCause.getMessage()));
    }
}