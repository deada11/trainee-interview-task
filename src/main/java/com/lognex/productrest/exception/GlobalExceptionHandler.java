package com.lognex.productrest.exception;

import org.hibernate.id.IdentifierGenerationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IdentifierGenerationException.class)
    protected ResponseEntity<Object> handleIdentifierGenerationException(IdentifierGenerationException ex) {
        ApiError apiError = new ApiError("Incorrect or missed identifier");
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }
}
