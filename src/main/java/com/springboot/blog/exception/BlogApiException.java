package com.springboot.blog.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BlogApiException extends RuntimeException {

    private HttpStatus status;
    private String message;

    public BlogApiException(HttpStatus status, String message) {

        this.status = status;
        this.message = message;
    }

    public BlogApiException(HttpStatus status, String message, String complement) {

        super(message);
        this.status = status;
        this.message = complement;
    }
}
