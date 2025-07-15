package com.mercadonosso.listings_service.adapters.in.web.dto;

import java.math.BigDecimal;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import com.mercadonosso.listings_service.core.domain.enums.ProductCondition;

public record ListingResponse(
        String listingId,
        String sellerId,
        String sku,
        List<String> productRecommendation,
        String title,
        String description,
        @Field(targetType = FieldType.DECIMAL128) BigDecimal price,
        Integer stock,
        Integer rating,
        List<ObjectId> reviewsId,
        List<String> imagesUrl,
        String category,
        boolean active,
        ProductCondition productCondition) {
}
