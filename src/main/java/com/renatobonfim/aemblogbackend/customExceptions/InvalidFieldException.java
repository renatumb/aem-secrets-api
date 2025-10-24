package com.renatobonfim.aemblogbackend.customExceptions;

public class InvalidFieldException extends RuntimeException{
    public InvalidFieldException(String message){
        super(message);
    }
}
