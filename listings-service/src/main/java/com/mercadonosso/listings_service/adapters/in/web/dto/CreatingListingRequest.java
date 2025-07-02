package com.mercadonosso.listings_service.adapters.in.web.dto;

import com.mercadonosso.listings_service.core.domain.enums.ProductCondition;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.math.BigDecimal;
import java.util.UUID;

public record CreatingListingRequest(UUID productId,
                                     UUID sellerId,
                                     String title,
                                     String description,
                                     @Field(targetType = FieldType.DECIMAL128)
                                     BigDecimal price,
                                     Integer stock,
                                     ProductCondition productCondition) {
}
