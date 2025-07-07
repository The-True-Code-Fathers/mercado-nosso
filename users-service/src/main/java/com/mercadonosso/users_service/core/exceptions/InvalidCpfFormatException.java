package com.mercadonosso.users_service.core.exceptions;

public class InvalidCpfFormatException extends RuntimeException {
    
    public InvalidCpfFormatException(String message) {
        super(message);
    }
    
    public InvalidCpfFormatException(String message, Throwable cause) {
        super(message, cause);
    }
}
