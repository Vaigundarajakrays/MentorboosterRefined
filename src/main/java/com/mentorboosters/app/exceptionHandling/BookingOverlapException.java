package com.mentorboosters.app.exceptionHandling;

//Unchecked exceptions (those that extend RuntimeException) do not need throws in method signatures. We need to catch this in Global exception handler
public class BookingOverlapException extends RuntimeException{

    public BookingOverlapException(String message){
        super(message);
    }
}
