package com.mentorboosters.app.exceptionHandling;

public class PhoneNumberRequiredException extends RuntimeException {
    public PhoneNumberRequiredException(String message) {
        super(message);
    }
}

