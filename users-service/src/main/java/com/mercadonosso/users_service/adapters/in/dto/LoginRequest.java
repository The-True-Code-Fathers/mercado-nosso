package com.mercadonosso.users_service.adapters.in.dto;

public record LoginRequest(
        String email,
        String password
) {
}
