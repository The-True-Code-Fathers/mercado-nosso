package com.mercadonosso.reviews_service.adapters.in.web.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record CreateReviewsRequest(
        @NotNull(message = "O ID do anúncio é obrigatório.")
        UUID listingId,

        @NotNull(message = "O ID do comprador é obrigatório.")
        UUID buyerId,

        @NotNull(message = "O ID do vendedor é obrigatório.")
        UUID sellerId,

        @NotNull(message = "A nota (rating) é obrigatória.")
        @Min(value = 1, message = "A nota mínima é 1.")
        @Max(value = 5, message = "A nota máxima é 5.")
        Integer rating,
        String message,
        List<String> imagesUrls,
        LocalDateTime createdAt,
        @Id
        @NotNull(message = "O id da review é obrigatório")
        UUID id
) {
}
