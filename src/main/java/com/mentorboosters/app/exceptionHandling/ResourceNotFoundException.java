package com.mentorboosters.app.exceptionHandling;

public class ResourceNotFoundException extends Exception {

    public ResourceNotFoundException(String message){
        super(message);
    }
}
