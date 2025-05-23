package com.mentorboosters.app.exceptionHandling;

public class UnexpectedServerException extends Exception{

    public UnexpectedServerException(String message){
        super(message);
    }
}
