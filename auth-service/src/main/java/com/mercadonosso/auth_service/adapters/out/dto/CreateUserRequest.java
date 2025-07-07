package com.mercadonosso.auth_service.adapters.out.dto;

import java.util.UUID;

public record CreateUserRequest(
    UUID id,
    String fullName,
    String email,
    String passwordHash,
    String cpf,
    String cnpj,
    boolean isSeller
) {
}
