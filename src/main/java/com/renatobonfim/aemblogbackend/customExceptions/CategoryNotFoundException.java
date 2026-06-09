package com.renatobonfim.aemblogbackend.customExceptions;

public class CategoryNotFoundException extends RuntimeException {
    public CategoryNotFoundException(Long id) {
        super(String.format("Category with ID:%d not found", id));
    }
    public CategoryNotFoundException(String categoryName) {
        super(String.format("Category with Name:%s not found", categoryName));
    }
}
