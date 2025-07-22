package com.mercadonosso.users_service.adapters.out.persistence.jpa;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import java.time.LocalDate;

@Data
@Entity(name = "users")
public class UserJPAEntity {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "social_reason", length = 255)
    private String socialReason;

    @Column(name = "full_name", length = 255, nullable = false)
    private String fullName;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(length = 255, nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", length = 255, nullable = false)
    private String passwordHash;

    @Column(unique = true)
    private String cpf;

    @Column(length = 14)
    private String cnpj;

    @Column(name = "telephone_number", length = 25)
    private String telephoneNumber;

    @Column(name = "is_seller", nullable = false)
    private boolean isSeller;

    @Column(name = "profile_picture_url")
    private String profilePictureUrl;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "user_selling_listing_ids", joinColumns = @JoinColumn(name = "user_id"))
    private List<UUID> orderSellingId;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "user_bought_id", joinColumns = @JoinColumn(name = "user_id"))
    private List<UUID> orderBoughtId;

    @CreationTimestamp
    @Column(name = "created_at")
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "cep", length = 9)
    private String cep;
}
