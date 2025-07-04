package com.mercadonosso.carts_service.core.domain.exception;

import java.time.Instant;

public record ApiErrorResponse(
    Instant timestamp,
    Integer status,
    String error,
    String message,
    String path
) {}