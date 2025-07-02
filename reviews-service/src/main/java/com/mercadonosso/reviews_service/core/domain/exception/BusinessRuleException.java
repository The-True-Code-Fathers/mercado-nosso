package com.mercadonosso.reviews_service.core.domain.exception;

public class BusinessRuleException extends RuntimeException{
    public BusinessRuleException(String message) {
        super(message);
    }
}
