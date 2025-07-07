package com.mercadonosso.products_service.adapters.out.persistence.mongo;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@Document(collection = "products")
public class ProductsModel {
    @Id
    private UUID id;

    @Field("product_sku")
    private String sku;

    private String name;

    private Map<String, Object> specifications;

    private String brand;
    private String category;
    private String description;

    @Field("created_at")
    private LocalDateTime createdAt;

    @Field("updated_at")
    private LocalDateTime updatedAt;
}

