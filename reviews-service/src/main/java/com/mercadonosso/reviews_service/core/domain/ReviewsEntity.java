package com.mercadonosso.reviews_service.core.domain;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewsEntity {
    private UUID id;

    @NotNull(message = "O ID do anúncio é obrigatório.")
    private UUID listingId;

    @NotNull(message = "O ID do comprador é obrigatório.")
    private UUID buyerId;

    @NotNull(message = "O ID do vendedor é obrigatório.")
    private UUID sellerId;

    @NotNull(message = "A nota (rating) é obrigatória.")
    @Min(value = 1, message = "A nota mínima é 1.")
    @Max(value = 5, message = "A nota máxima é 5.")
    private Integer rating;

    private String message;

    private List<String> imagesUrls;

    private LocalDateTime createdAt;

    @NotNull
    private boolean active;
}