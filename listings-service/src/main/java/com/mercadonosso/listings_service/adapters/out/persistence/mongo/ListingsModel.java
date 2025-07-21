package com.mercadonosso.listings_service.adapters.out.persistence.mongo;

import java.math.BigDecimal;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import com.mercadonosso.listings_service.core.domain.enums.ProductCondition;

import lombok.Data;

@Data
@Document(collection = "listings")
public class ListingsModel {
    @Id
    private ObjectId id;

    private String sellerId;
    private String sku;
    private List<String> productRecommendation;
    private String title;
    private String description;

    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal price;
    private Integer rating;
    private List<ObjectId> reviewsId;
    private List<String> imagesUrl;
    private String category;
    private Integer stock;
    private Integer salesCount;
    private boolean active;
    private ProductCondition productCondition;
}
