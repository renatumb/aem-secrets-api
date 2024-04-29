package com.renatobonfim.aemblogbackend.customExceptions;

public class CategoryNotFoundException extends RuntimeException {
    public CategoryNotFoundException(Long id) {
        super(String.format("Recipe with Id %d not found", id));
    }
}
