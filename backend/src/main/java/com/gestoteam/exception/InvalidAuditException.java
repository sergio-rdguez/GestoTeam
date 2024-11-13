package com.gestoteam.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidAuditException extends RuntimeException {
    public InvalidAuditException(String message) {
        super(message);
    }
}
