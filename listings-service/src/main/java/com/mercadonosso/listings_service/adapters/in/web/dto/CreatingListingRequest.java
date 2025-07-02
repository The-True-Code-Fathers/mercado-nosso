package com.mercadonosso.listings_service.adapters.in.web.dto;

import com.mercadonosso.listings_service.core.domain.enums.ProductCondition;

import java.math.BigDecimal;
import java.util.UUID;

public record CreatingListingRequest(UUID productId,
                                     UUID sellerId,
                                     String title,
                                     String description,
                                     BigDecimal price,
                                     Integer stock,
                                     ProductCondition productCondition) {
}
