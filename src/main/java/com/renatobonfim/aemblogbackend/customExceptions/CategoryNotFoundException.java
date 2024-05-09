package com.renatobonfim.aemblogbackend.customExceptions;

public class CategoryNotFoundException extends Exception {
    public CategoryNotFoundException(Long id) {
        super(String.format("Category with ID:%d not found", id));
    }
}
