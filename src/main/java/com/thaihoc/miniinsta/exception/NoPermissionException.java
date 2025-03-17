package com.thaihoc.miniinsta.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class NoPermissionException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public NoPermissionException(String message) {
        super(message);
    }

    public NoPermissionException() {
        super("You don't have permission to perform this action");
    }
}
