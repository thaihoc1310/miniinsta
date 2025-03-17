package com.thaihoc.miniinsta.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ProfileNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ProfileNotFoundException(String message) {
        super(message);
    }

    public ProfileNotFoundException() {
        super("Profile not found");
    }
}