package com.springboot.blog.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resourceName, String fieldName, Long fieldValue) {

        super(String.format("%s not found %s: '%s'", resourceName, fieldName, fieldValue));
    }
}
