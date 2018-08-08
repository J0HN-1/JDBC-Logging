package com.example.demoApp.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class InvalidEntityReference extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public InvalidEntityReference(Class<?> entityClass, Object id) {
        super("Invalid reference to " + entityClass.getSimpleName() + " with ID " + id);
    }
}
