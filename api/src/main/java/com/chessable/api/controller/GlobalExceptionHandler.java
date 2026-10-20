package com.chessable.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.chessable.library.error.ErrorResponse;
import com.chessable.library.error.UserNotFoundException;
import com.chessable.library.error.UserErrorResponse;

/*
 * TODO move this to a controller file
 */

@ControllerAdvice
public class GlobalExceptionHandler {

    private <T extends ErrorResponse> ResponseEntity<T> buildResponseEntity(Exception ex, HttpStatus status, T error) {
        error.setStatus(status.value());
        error.setMessage(ex.getMessage());
        error.setTimeStamp(System.currentTimeMillis());
        return new ResponseEntity<>(error, status);
    }

    /*
     * handles user not found exception
     */

    @ExceptionHandler
    public ResponseEntity<UserErrorResponse> handleUserNotFoundException(UserNotFoundException ex) {
        return buildResponseEntity(ex, HttpStatus.NOT_FOUND, new UserErrorResponse());
    }

    @ExceptionHandler
    public ResponseEntity<UserErrorResponse> handleException(Exception ex) {
        return buildResponseEntity(ex, HttpStatus.BAD_REQUEST, new UserErrorResponse());
    }
}
