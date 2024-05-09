package com.renatobonfim.aemblogbackend.customExceptions;

public class PostNotFoundException extends Exception {
    public PostNotFoundException(String postId) {
        super(postId);
    }
}
