package com.mercadonosso.users_service.adapters.in.dto;

public record UpdateUserRequest(
    String fullName,
    String profilePictureUrl,
    String email,
    String telephoneNumber,
    String cnpj,
    String socialReason,
    String cep,
    boolean isSeller
) {
}
