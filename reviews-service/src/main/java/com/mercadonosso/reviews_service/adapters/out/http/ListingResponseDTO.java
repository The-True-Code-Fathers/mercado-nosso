package com.mercadonosso.reviews_service.adapters.out.http;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ListingResponseDTO(
                String listingId,
                String sellerId,
                String sku,
                List<String> productRecommendation,
                String title,
                String description,
                BigDecimal price,
                Integer stock,
                Integer salesCount,
                Integer rating,
                List<String> reviewsId,
                List<String> imagesUrl,
                String category,
                boolean active) {
}
