package com.mercadonosso.products_service.core.domain.exception;

public class ProductsAlreadyExistsException extends RuntimeException {
    public ProductsAlreadyExistsException(String message) {
        super(message);
    }
}
