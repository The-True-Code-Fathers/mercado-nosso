package com.mercadonosso.listings_service.adapters.in.web.dto;

import com.mercadonosso.listings_service.core.domain.enums.ProductCondition;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ListingResponse(
    UUID listingId,
    String title,
    String description,
    @Field(targetType = FieldType.DECIMAL128)
    BigDecimal price,
    Integer stock,
    boolean active,
    ProductCondition productCondition,
    LocalDateTime creationTime
) { }
