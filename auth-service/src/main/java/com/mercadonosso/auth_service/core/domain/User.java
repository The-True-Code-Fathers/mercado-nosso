package com.mercadonosso.auth_service.core.domain;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private UUID id;
    private String fullName;
    private String email;
    private String passwordHash;
    private String cpf;
    private String cnpj;
    private boolean isSeller;
    private String profilePictureUrl;
    private List<UUID> listingSellingId;
    private List<UUID> listingBoughtId;
    private Instant createdAt;
    private Instant updatedAt;
    private boolean active;
}
