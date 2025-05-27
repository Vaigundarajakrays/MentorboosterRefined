package com.mentorboosters.app.exceptionHandling;

public class EmailOrPhoneAlreadyExistsException extends RuntimeException {
    public EmailOrPhoneAlreadyExistsException(String message) {
        super(message);
    }
}
