package com.mercadonosso.users_service.adapters.out.persistence.jpa;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Entity(name = "users")
public class UserJPAEntity {
    @Id
    private UUID id;

    @Column(name="full_name", length = 255, nullable = false)
    private String fullName;

    @Column(length = 255, nullable = false, unique = true)
    private String email;

    @Column(name="password_hash", length = 255, nullable = false)
    private String passwordHash;

    @Column(length = 11, unique = true, nullable = false)
    private String cpf;

    @Column(length = 14, unique = true, nullable = false)
    private String cnpj;

    @Column(name = "is_seller", nullable = false)
    private boolean isSeller;

    @Column(name = "profile_picture_url")
    private String profilePictureUrl;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "user_selling_listing_ids", joinColumns = @JoinColumn(name = "user_id"))
    private List<UUID> listingSellingId;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "user_bought_id", joinColumns = @JoinColumn(name = "user_id"))
    private List<UUID> listingBoughtId;

    @CreationTimestamp
    @Column(name = "created_at")
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(nullable = false)
    private boolean active = true;
}
