package com.mercadonosso.listings_service.adapters.in.web.dto;

import com.mercadonosso.listings_service.core.domain.enums.ProductCondition;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ListingResponse(
        String listingId,
        String title,
        String description,
        @Field(targetType = FieldType.DECIMAL128)
        BigDecimal price,
        Integer stock,
        boolean active,
        ProductCondition productCondition,
        LocalDateTime creationTime
) { }
