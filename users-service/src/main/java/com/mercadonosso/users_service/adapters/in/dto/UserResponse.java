package com.mercadonosso.users_service.adapters.in.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String fullName,
        String email,
        boolean isSeller,
        String profilePictureUrl,
        List<UUID> listingSellingId,
        List<UUID> listingBoughtId,
        Instant createdAt,
        Instant updatedAt,
        boolean active
) {}