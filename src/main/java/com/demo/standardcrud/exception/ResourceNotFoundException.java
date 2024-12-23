package com.demo.standardcrud.exception;

public class ResourceNotFoundException extends RuntimeException {

    // Constructor to accept custom error message
    public ResourceNotFoundException(String message) {
        super(message);
    }

    // Constructor to accept both custom message and cause
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
