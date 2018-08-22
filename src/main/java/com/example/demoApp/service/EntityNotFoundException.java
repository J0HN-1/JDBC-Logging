package com.example.demoApp.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class EntityNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public EntityNotFoundException(Class<?> entityClass, Object id) {
        super("Cannot find entity " + entityClass.getSimpleName() + " with ID " + id);
    }
}