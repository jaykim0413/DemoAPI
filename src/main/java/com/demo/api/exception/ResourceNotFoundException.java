package com.demo.api.exception;

public class ResourceNotFoundException extends RuntimeException {
    private static final long serialVersionUUID = 1L;

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
