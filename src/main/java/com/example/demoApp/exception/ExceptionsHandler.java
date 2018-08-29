package com.example.demoApp.exception;

import com.example.demoApp.service.EntityNotFoundException;
import com.example.demoApp.service.InvalidEntityReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestControllerAdvice
public class ExceptionsHandler {

    @ExceptionHandler(ApiErrorException.class)
    @RequestMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Errors> apiErrorExceptionHandler(ApiErrorException exception) {
        Errors errors = new Errors();
        exception.getApiErrors().forEach(apiError -> {
            errors.addError(apiError.getType(), apiError.getDescription());
        });
        return ResponseEntity
                .status(exception.getStatus())
                .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .body(errors);
    }

    @ExceptionHandler
    @RequestMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Errors> entityNotFound(EntityNotFoundException exception) {
        Errors errors = new Errors();
        errors.addError("Entity Not Found", exception.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .body(errors);
    }

    @ExceptionHandler
    @RequestMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Errors> invalidEntityReference(InvalidEntityReferenceException exception) {
        Errors errors = new Errors();
        errors.addError("Invalid Entity Reference", exception.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .body(errors);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @RequestMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Errors> mappingNotFound(NoHandlerFoundException exception) {
        Errors errors = new Errors();
        errors.addError("Unknown Endpoint", exception.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .body(errors);
    }

    @ExceptionHandler(Exception.class)
    @RequestMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Errors> exceptionHandler(Exception exception) {
        Errors errors = new Errors();
        errors.addError(exception.getClass().getSimpleName(), exception.getLocalizedMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .body(errors);
    }
}