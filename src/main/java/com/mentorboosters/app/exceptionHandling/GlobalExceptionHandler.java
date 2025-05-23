package com.mentorboosters.app.exceptionHandling;

import io.jsonwebtoken.security.SignatureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

import static com.mentorboosters.app.util.Constant.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFound(ResourceNotFoundException ex) {
        ErrorResponse errorResponse=
                new ErrorResponse(ex.getMessage()
                        ,NO_DATA,STATUS_FALSE, LocalDateTime.now());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UnexpectedServerException.class)
    public ResponseEntity<?> handleUnexpectedServerError(UnexpectedServerException ex){

        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), SERVER_ERROR, STATUS_FALSE, LocalDateTime.now());

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(InvalidJwtTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidJwtToken(InvalidJwtTokenException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                ex.getMessage(),
                INVALID_TOKEN,
                false,
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

}
