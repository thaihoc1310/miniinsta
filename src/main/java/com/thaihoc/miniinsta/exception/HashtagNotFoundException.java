package com.thaihoc.miniinsta.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class HashtagNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public HashtagNotFoundException(String message) {
        super(message);
    }

    public HashtagNotFoundException() {
        super("Hashtag not found");
    }
}