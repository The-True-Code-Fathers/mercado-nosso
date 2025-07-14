package com.mercadonosso.carts_service.adapters.out.rest.listings;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ListingResponseDto(
        String listingId,
        String title,
        String description,
        BigDecimal price,
        Integer stock,
        boolean active,
        String productCondition,
        LocalDateTime creationTime) {
}
