package com.example.demoApp.service;

public class NoSuchEntityException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public NoSuchEntityException(Class<?> entityClass, Object id) {
        super("Cannot find entity " + entityClass.getSimpleName() + " with ID " + id);
    }
}