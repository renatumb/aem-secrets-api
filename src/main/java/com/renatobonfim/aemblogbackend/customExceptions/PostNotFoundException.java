package com.renatobonfim.aemblogbackend.customExceptions;

public class PostNotFoundException extends RuntimeException {
    public PostNotFoundException(String postId) {
        super(postId);
    }
}
