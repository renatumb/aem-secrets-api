package com.renatobonfim.aemblogbackend.customExceptions;

import java.time.LocalDateTime;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@ControllerAdvice
public class ControllerAdvisor extends ResponseEntityExceptionHandler {
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({CategoryNotFoundException.class,})
    public ResponseEntity categoryNotFoundException(Exception ex) {
        log.warn(ex.getMessage());
        return new ResponseEntity(Map.of(
                "message", ex.getMessage(),
                "timestamp", LocalDateTime.now().toString()
        ), HttpStatus.NOT_FOUND);
    }
}
