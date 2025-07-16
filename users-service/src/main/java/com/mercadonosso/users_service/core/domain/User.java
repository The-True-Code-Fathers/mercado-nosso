package com.mercadonosso.users_service.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

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
    private String telephoneNumber;
    private String socialReason;
    private boolean isSeller;
    private String profilePictureUrl;
    private List<UUID> listingSellingId;
    private List<UUID> listingBoughtId;
    private Instant createdAt = Instant.now();
    private Instant updatedAt = Instant.now();
    private boolean active = true;
    private String cep;
}
