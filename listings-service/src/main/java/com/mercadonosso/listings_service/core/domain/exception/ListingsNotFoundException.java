package com.mercadonosso.listings_service.core.domain.exception;

public class ListingsNotFoundException extends RuntimeException {
    public ListingsNotFoundException(String message) {
        super(message);
    }
}
