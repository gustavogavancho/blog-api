package com.springboot.blog.exception;

import lombok.Getter;

@Getter
public class CustomDuplicateTitleException extends RuntimeException {

    public CustomDuplicateTitleException(String message) {
        super(message);
    }
}
