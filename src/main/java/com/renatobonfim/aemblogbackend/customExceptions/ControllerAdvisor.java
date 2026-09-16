package com.renatobonfim.aemblogbackend.customExceptions;

import com.renatobonfim.aemblogbackend.dto.ErrorResponseDTO;
import java.nio.file.NoSuchFileException;
import java.time.LocalDateTime;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestValueException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

@Slf4j
@ControllerAdvice
public class ControllerAdvisor {
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponseDTO> unsupportedMediaType(HttpMediaTypeNotSupportedException ex) {
        log.warn(ex.getMessage());
        return buildError(HttpStatus.UNSUPPORTED_MEDIA_TYPE, ex.getMessage());
    }

    @ExceptionHandler({
            MissingServletRequestPartException.class,
            MissingServletRequestParameterException.class,
            MultipartException.class,
            MethodArgumentTypeMismatchException.class
    })
    public ResponseEntity<ErrorResponseDTO> multipartOrBindingError(Exception ex) {
        log.warn("{}: {}", ex.getClass().getSimpleName(), ex.getMessage());
        return buildError(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler({CategoryNotFoundException.class,
            SubscriberNotFoundException.class,
            UserNotFoundException.class,
            PostNotFoundException.class,
            NoCommentFoundException.class})
    public ResponseEntity<ErrorResponseDTO> entityNotFoundException(Exception ex) {
        log.warn(ex.getMessage());

        return buildError(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler({InvalidFieldException.class,
            IllegalArgumentException.class,
            ErrorReadingPhotoException.class,
            PropertyReferenceException.class,
            HttpMessageConversionException.class,
            MissingRequestValueException.class})
    public ResponseEntity InvalidFieldException(Exception ex) {
        log.warn(ex.getMessage());

        return buildError(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity invalidPayload(MethodArgumentNotValidException ex) {
        StringBuilder errors = new StringBuilder();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            errors.append(error.getDefaultMessage());
            errors.append(" | ");
        });

        log.warn(errors.toString());
        return buildError(HttpStatus.BAD_REQUEST, errors.toString());
    }

    @ExceptionHandler({
            DataIntegrityViolationException.class
    })
    private ResponseEntity<ErrorResponseDTO> conflict(Exception ex) {
        log.warn(ex.getMessage());
        return buildError(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler({BadCredentialsException.class, AuthenticationException.class})
    public ResponseEntity<ErrorResponseDTO> authenticationFailed(AuthenticationException ex) {
        log.warn("Authentication failed: {}", ex.getMessage() );

        return buildError(HttpStatus.UNAUTHORIZED, ex.getMessage() );
    }

    @ExceptionHandler( NoSuchFileException.class)
    public ResponseEntity<ErrorResponseDTO> noSuchFileException(NoSuchFileException ex){
        log.warn("No Such FileException: {}", ex.getMessage() );
        return buildError(HttpStatus.NOT_FOUND, ex.getMessage() );
    }

    /**
     * Generic method to send formated API response which will always be like:<BR/>
     * {
     *     "message":"string"
     *     "timestamp": "..."
     * }
     *  <BR/>
     *  Along with http status code
     * */
    private ResponseEntity<ErrorResponseDTO> buildError(HttpStatus status, String message){
        return ResponseEntity.
                status(status)
                .body(ErrorResponseDTO.builder()
                        .message(message)
                        .timestamp(LocalDateTime.now().toString())
                        .build());
    }
}
