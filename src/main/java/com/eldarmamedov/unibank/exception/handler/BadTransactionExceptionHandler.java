package com.eldarmamedov.unibank.exception.handler;

import com.eldarmamedov.unibank.exception.DeactiveAccountException;
import com.eldarmamedov.unibank.exception.NoEnoughMoneyException;
import com.eldarmamedov.unibank.exception.NonExistingAccountException;
import com.eldarmamedov.unibank.exception.SameAccountException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.time.ZonedDateTime;

@ControllerAdvice
public class BadTransactionExceptionHandler {

    @ExceptionHandler(value = {DeactiveAccountException.class, NoEnoughMoneyException.class,
            NonExistingAccountException.class, SameAccountException.class})
    public ResponseEntity<?> handleDeactiveAccountException(RuntimeException e) {
        ExceptionPayload payload = new ExceptionPayload(e.getMessage(),
                HttpStatus.BAD_REQUEST, ZonedDateTime.now());

        return new ResponseEntity<>(payload, HttpStatus.BAD_REQUEST);
    }
}
