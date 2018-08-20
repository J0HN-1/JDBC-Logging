package com.example.demoApp.exception;

public class ApiError {
    private String type;
    private String description;

    public ApiError(String type, String description) {
        this.type = type;
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }
}
