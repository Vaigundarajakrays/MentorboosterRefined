package com.mentorboosters.app.exceptionHandling;

public class InvalidJwtTokenException extends RuntimeException{

    public InvalidJwtTokenException(String message) {
        super(message);
    }

}
