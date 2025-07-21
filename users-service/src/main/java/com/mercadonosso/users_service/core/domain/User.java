package com.mercadonosso.users_service.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private UUID id;
    private String fullName;
    private String email;
    private String passwordHash;
    private LocalDate birthDate;
    private String cpf;
    private String cnpj;
    private String telephoneNumber;
    private String socialReason;
    private boolean isSeller;
    private String profilePictureUrl;
    private List<UUID> orderSellingId;
    private List<UUID> orderBoughtId;
    private Instant createdAt = Instant.now();
    private Instant updatedAt = Instant.now();
    private boolean active = true;
    private String cep;
}
