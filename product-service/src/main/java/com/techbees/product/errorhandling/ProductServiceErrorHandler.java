package com.techbees.product.errorhandling;

import org.axonframework.commandhandling.CommandExecutionException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

@ControllerAdvice
public class ProductServiceErrorHandler {

    @ExceptionHandler(value = {IllegalStateException.class})
    public ResponseEntity<Object> handleIllegalStateException(
            IllegalStateException exception, WebRequest request) {

        ErrorMessage errorMessage = new ErrorMessage(new Date(), exception.getMessage());

        return new ResponseEntity<>(errorMessage, new HttpHeaders(),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<Object> handleOtherExceptions(Exception exception, WebRequest request) {
        ErrorMessage errorMessage = new ErrorMessage(new Date(), exception.getMessage());

        return new ResponseEntity<>(errorMessage, new HttpHeaders(),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = {CommandExecutionException.class})
    public ResponseEntity<Object> handleCommandExecutionException(CommandExecutionException exception, WebRequest request) {
        ErrorMessage errorMessage = new ErrorMessage(new Date(), exception.getMessage());

        return new ResponseEntity<>(errorMessage, new HttpHeaders(),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
