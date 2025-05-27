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

    @ExceptionHandler(BookingOverlapException.class)
    public ResponseEntity<ErrorResponse> handleBookingOverlap(BookingOverlapException ex) {

        ErrorResponse errorResponse = new ErrorResponse(
                ex.getMessage(),
                "BOOKING_OVERLAP",
                false,
                LocalDateTime.now()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(EmailOrPhoneAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleEmailOrPhoneAlreadyExists(EmailOrPhoneAlreadyExistsException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                ex.getMessage(),
                "EMAIL_OR_PHONE_EXISTS",
                false,
                LocalDateTime.now()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(PhoneNumberRequiredException.class)
    public ResponseEntity<ErrorResponse> handlePhoneNumberRequired(PhoneNumberRequiredException ex) {
        ErrorResponse error = new ErrorResponse(
                ex.getMessage(),
                "PHONE_NUMBER_REQUIRED",
                false,
                LocalDateTime.now()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }



}
