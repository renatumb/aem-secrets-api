package com.renatobonfim.aemblogbackend.customExceptions;

public class NoCommentFoundException extends RuntimeException {
    public NoCommentFoundException(String commentId) {
        super(String.valueOf(commentId));
    }
}
