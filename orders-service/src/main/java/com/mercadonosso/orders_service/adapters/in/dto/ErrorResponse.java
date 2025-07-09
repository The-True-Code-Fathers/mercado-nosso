package com.mercadonosso.orders_service.adapters.in.dto;

import java.time.LocalDateTime;

public record ErrorResponse(
        LocalDateTime timeStamp,
        int status,
        String error,
        String message
) {
}

