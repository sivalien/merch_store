package com.merch_store.controller;

import com.merch_store.exception.BadRequestException;
import com.merch_store.exception.UnauthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class ExceptionHandlerController {
    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity handleBadRequestException(BadRequestException ex) {
        return createBody(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity handleBadRequestException(UnauthorizedException ex) {
        return createBody(ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity handleRuntimeException(RuntimeException ex) {
        return createBody("Внутренняя ошибка сервера " + ex.getMessage() , HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity createBody(String error, HttpStatus status) {
        System.out.println(error);
        return new ResponseEntity(Map.of("errors", error), status);
    }
}
