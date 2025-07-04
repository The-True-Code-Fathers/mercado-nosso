package com.mercadonosso.orders_service.orderservice.adapters.in.dto;

import java.time.LocalDateTime;

public record ErrorResponse(
        LocalDateTime timeStamp,
        int status,
        String error,
        String message
) {
}

