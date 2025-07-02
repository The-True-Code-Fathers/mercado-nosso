package com.mercadonosso.orders_service.orderservice.core.domain.exceptions;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(String message) {
        super(message);
    }
}
