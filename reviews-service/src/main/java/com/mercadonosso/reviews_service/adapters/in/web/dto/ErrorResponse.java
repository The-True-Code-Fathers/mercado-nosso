package com.mercadonosso.reviews_service.adapters.in.web.dto;

import java.time.LocalDateTime;

public record ErrorResponse(
        LocalDateTime timeStamp,
        int status,
        String error,
        String message
) {
}
