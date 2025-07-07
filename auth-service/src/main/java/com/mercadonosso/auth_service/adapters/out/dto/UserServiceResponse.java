package com.mercadonosso.auth_service.adapters.out.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record UserServiceResponse(
    UUID id,
    String fullName,
    String email,
    String passwordHash,
    String cpf,
    String cnpj,
    boolean isSeller,
    String profilePictureUrl,
    List<UUID> listingSellingId,
    List<UUID> listingBoughtId,
    Instant createdAt,
    Instant updatedAt,
    boolean active
) {
}
