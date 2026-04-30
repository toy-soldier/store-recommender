package com.storerecommender.apiserver.exceptions;

import com.storerecommender.apiserver.dtos.ErrorDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorDto> handleProductNotFound(ProductNotFoundException e) {
        var errorMessage = new ErrorDto(e.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(errorMessage, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorDto> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        var message = "Invalid value '" + e.getValue() + "' for parameter '" + e.getName() + "'";
        var errorMessage = new ErrorDto(message, LocalDateTime.now());
        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }
}
