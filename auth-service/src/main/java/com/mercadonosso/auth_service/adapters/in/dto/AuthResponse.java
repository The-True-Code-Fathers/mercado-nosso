package com.mercadonosso.auth_service.adapters.in.dto;

import java.util.UUID;

public record AuthResponse(
    String accessToken,
    String refreshToken,
    UUID userId,
    String tokenType
) {
    public AuthResponse(String accessToken, String refreshToken, UUID userId) {
        this(accessToken, refreshToken, userId, "Bearer");
    }
}
