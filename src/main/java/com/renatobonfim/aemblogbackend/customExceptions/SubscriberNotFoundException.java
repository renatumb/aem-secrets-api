package com.renatobonfim.aemblogbackend.customExceptions;

public class SubscriberNotFoundException extends Exception {
    public SubscriberNotFoundException(String email){
        super(String.format("Subscriber with ID:'%s' not found",email));
    }
}
