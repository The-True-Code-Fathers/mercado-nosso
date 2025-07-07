package com.mercadonosso.users_service.adapters.in.dto;

public record UpdateUserRequest(
    String fullName,
    String profilePictureUrl
) {
}
