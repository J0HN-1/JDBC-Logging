package com.example.demoApp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.stream.Collectors;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class RequestBindingException extends ApiErrorException {

    public RequestBindingException(BindingResult bindingResult) {
        super(bindingResult.getFieldErrors()
                .stream()
                .map(fieldError -> new ApiError("Field Error", "Field error in field '" + fieldError.getField() + "':" +
                        " rejected value [" + ObjectUtils.nullSafeToString(fieldError.getRejectedValue()) + "]; " +
                        fieldError.getDefaultMessage()))
                .collect(Collectors.toList()));
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}
