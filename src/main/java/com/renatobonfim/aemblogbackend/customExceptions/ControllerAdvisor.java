package com.renatobonfim.aemblogbackend.customExceptions;

import java.time.LocalDateTime;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ControllerAdvisor extends ResponseEntityExceptionHandler {

    @ExceptionHandler({CategoryNotFoundException.class,})
    public ResponseEntity categoryNotFoundException() {
        return new ResponseEntity(Map.of(
                "message", "Entity not found for the given ID",
                "timestamp", String.valueOf(LocalDateTime.now())
        ), HttpStatus.NOT_FOUND);
    }
}
